package com.shareshow.aide.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.google.gson.Gson;
import com.shareshow.aide.event.FileDownLoadEvent;
import com.shareshow.aide.event.GetAdEvent;
import com.shareshow.aide.event.MessageEvent;
import com.shareshow.aide.event.NettyEvent;
import com.shareshow.aide.event.NetworkEvent;
import com.shareshow.aide.mvp.model.BoxDataModel;
import com.shareshow.aide.nettyfile.Device;
import com.shareshow.aide.nettyfile.DownLoadListener;
import com.shareshow.aide.nettyfile.FileUploadClient;
import com.shareshow.aide.nettyfile.TcpMessageListener;
import com.shareshow.aide.nettyfile.db.GreenDaoUtil;
import com.shareshow.aide.nettyfile.socket.MessageThread;
import com.shareshow.aide.nettyfile.socket.SearchEvent;
import com.shareshow.aide.nettyfile.socket.SearchMessage;
import com.shareshow.aide.util.CacheConfig;
import com.shareshow.aide.util.CacheUserInfo;
import com.shareshow.aide.util.Fixed;
import com.shareshow.aide.util.NetContent;
import com.shareshow.airpc.Collocation;
import com.shareshow.airpc.util.DebugLog;
import com.shareshow.airpc.util.NetworkUtils;
import com.shareshow.airpc.util.PackZip;
import com.shareshow.db.AppUseInfo;
import com.shareshow.db.DevAdPlayRecord;
import com.shareshow.db.DevAreaUseInfo;
import com.shareshow.db.DeviceUser;
import com.shareshow.db.FileChannelBean;
import com.shareshow.db.FileCordBean;
import com.shareshow.db.SearchBean;
import com.shareshow.db.StudyFilePlayRecord;
import com.shareshow.db.VisitFilePlayRecord;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2016/10/27.
 * 1，服务启动发起搜索，获取盒子列表;
 * 2，获取盒子上传数据，存入数据库;
 * 3，获取盒子广告版本和销售资料，并且上传广告(3个)和销售资料；
 *
 */

public class NettyService extends Service{

    private final static int SEARCH_CODE = 0x0001;
    private final static int AD_DOWNLOAD =0x0002;
    private final static int AD_TOTAL =4;
    ArrayList<VisitFilePlayRecord> visitFiles =new ArrayList<VisitFilePlayRecord>();
    ArrayList<StudyFilePlayRecord> studyFiles= new ArrayList<StudyFilePlayRecord>();
    private SimpleDateFormat formater;
    private SimpleDateFormat data;
    private Gson gson;
    private boolean isFinish =true;
    private ArrayList<Integer> updates = new ArrayList<Integer>();
    private List<FileChannelBean.ContentBean> beans;
    private boolean isTimeOut;
    private Handler mHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void dispatchMessage(Message msg){
            super.dispatchMessage(msg);
            switch (msg.what){
                //处理和盒子之间的数据和文件传输
                case SEARCH_CODE:
                    final SearchBean bean = (SearchBean) msg.getData().getSerializable("device");
                    MessageThread.post(new Runnable(){
                        @Override
                        public void run(){
                            isFinish =false;
                            isTimeOut=false;
                            DebugLog.showLog(this, "开始发送：" + bean.getIp());
                            try{
                                new FileUploadClient().connectMessage(bean.getPort(), bean.getIp(), new TcpMessageListener() {
                                    @Override
                                    public void success(FileChannelBean fileBean){
                                        //将获取数据上传到服务器
                                        upDataToService();
                                        beans = fileBean.getContent();
                                        for (FileChannelBean.ContentBean contentBean : beans){
                                            try{
                                                new FileUploadClient().connectFile(bean.getPort(), bean.getIp(), contentBean);
                                                Device.clear();
                                                if(contentBean.getFileName().contains("zip")){
                                                    DeviceUser deviceUser =new DeviceUser();
                                                    deviceUser.setIds(bean.getIds());
                                                    GreenDaoUtil.getGreenDao().savaDeviceUpdate(deviceUser);
                                                }
                                            }catch (Exception e){
                                                e.printStackTrace();
                                                Device.clear();
                                                isTimeOut=true;
                                                DebugLog.showLog(this, "发送文件失败！");
                                            }
                                        }
                                    }

                                    @Override
                                    public void fail(int state){
                                        DebugLog.showLog(this,"传输失败了！");
                                        isTimeOut=true;
                                        Device.clear();
                                    }
                                });
                             }catch (Exception e){
                                DebugLog.showLog(this, "发送失败!");
                                e.printStackTrace();
                                isTimeOut=true;
                             }
                             while(!isFinish){

                             }
                             DebugLog.showLog(this,"handleThread执行完毕!");
                        }
                    });

                    break;
                case AD_DOWNLOAD:
                    //广告下载完毕打包
                    int index = msg.arg1;
                    Boolean isSuccess = (Boolean) msg.obj;
                    int update = msg.arg2;
                    updates.add(update);
                    DebugLog.showLog(this,"updates:"+updates.toString());
                    DebugLog.showLog(this,"下载广告："+index+" 是否成功:"+isSuccess+" updates:"+update);
                    switch (index){
                        case 1:
                            if(isSuccess&&update==1){
                                completeZIPfile(index,"boot");
                            }
                            break;

                        case 2:
                            if(isSuccess&&update==1){
                                completeZIPfile(index, "screensaver");
                            }

                            break;

                        case 3:
                            if(isSuccess&&update==1){
                                completeZIPfile(index, "hot");
                            }

                            break;

                        case 5:
                            if(isSuccess&&update==1){
                                completeZIPfile(index, "official");
                            }
                            break;
                    }

                    if(updates.size()==AD_TOTAL){
                        CacheConfig.get().setAdDodnloadComplete();
                        reNameCompleteFile();
                        if(updates.contains(1)){
                            initSearch();
                        }
                        updates.clear();
                    }

                    break;
            }
        }
    };


    private void reNameCompleteFile(){
       File adZipFile = new File(Fixed.getAdZipPath());
       if(adZipFile.exists()&&adZipFile.listFiles()!=null){
           for (File file : adZipFile.listFiles()){
               if(file.getName().contains("zip")&&file.getName().contains(NetContent.UN_FINISHED)){
                  String[] names = file.getName().split("_");
                  try{
                    File destFile = new File(Fixed.getAdZipPath(                                                               ) + File.separator+names[0]+"_"+names[1]+".zip");
                    file.renameTo(destFile);
                  }catch (Exception e){
                      e.printStackTrace();
                  }
               }
            }
        }

        DebugLog.showLog(this,"广告重命名成功!");
    }


    @Override
    public void onCreate(){
        super.onCreate();
        gson = new Gson();
        DebugLog.showLog(this, "onCreate");
        formater = new SimpleDateFormat("yyyy-MM-dd");
        MessageThread.prepareThread();
        EventBus.getDefault().register(this);
        upDataToService();
    }

    private void upDataToService(){
        boxDataToServer();
        //上传服务器APP使用数据
        upSaleFile();
        //上传服务器销售资料和学习资料使用数据
        updateAdState();
        //向广告更新设备数据上传
        upAdRecordData();
        //上传广告点击使用数据;
    }

    private void upAdRecordData(){
     List<DevAdPlayRecord> records =GreenDaoUtil.getGreenDao().getDevAdPlayRecords();
     if(records==null||records.size()==0){
         return;
     }
     String json = new Gson().toJson(records);
     DebugLog.showLog(this,"广告使用数据:"+json);
     BoxDataModel.getBoxDataModel().upDevAdPlayRecord(json);
    }

    private void updateAdState(){
        List<DeviceUser> users =GreenDaoUtil.getGreenDao().getDeviceUserAll();
        if (users == null){
            return;
        }

        for(DeviceUser user : users){
            DebugLog.showLog(this,"更新广告的设备:"+user.getIds());
            BoxDataModel.getBoxDataModel().upAdReceiverByDevice(user.getIds());
        }
    }

    private void upSaleFile(){
        List<FileCordBean> beans = GreenDaoUtil.getGreenDao().getFileCordBeans();
        DebugLog.showLog(this, "盒子的文件使用数据:" + beans.toString());
        visitFiles.clear();
        studyFiles.clear();
        if (beans != null&&beans.size()!=0){
            for (FileCordBean bean : beans){
                if (bean.getVfr_File_Type() == 3){
                    VisitFilePlayRecord visitFilePlayRecord =  new VisitFilePlayRecord();
                    visitFilePlayRecord.setVfrDevid(bean.getVfrDevid());
                    visitFilePlayRecord.setVfrFileid(bean.getVfrFileid());
                    visitFilePlayRecord.setVfrTimeend(String.valueOf(bean.getVfrTimeend()));
                    visitFilePlayRecord.setVfrTimestart(String.valueOf(bean.getVfrTimestart()));
                    visitFiles.add(visitFilePlayRecord);
                    upToServer(0);
                }else if (bean.getVfr_File_Type() == 2){
                    StudyFilePlayRecord studyFilePlayRecord = new StudyFilePlayRecord();
                    studyFilePlayRecord.setSfrSpid(bean.getFileVid());
                    studyFilePlayRecord.setSfrUrId(CacheUserInfo.get().getUserPhone());
                    studyFilePlayRecord.setSfrDate(String.format(formater.format(bean.getVfrTimestart())));
                    studyFilePlayRecord.setSfrFileid(bean.getVfrFileid());
                    studyFilePlayRecord.setSfrTimestart(String.valueOf(bean.getVfrTimestart()));
                    studyFilePlayRecord.setSfrTimeend(String.valueOf(bean.getVfrTimeend()));
                    studyFiles.add(studyFilePlayRecord);
                    upToServer(1);
                }
            }
        }
    }

    private void upToServer(int type){
        if (type == 0){
            if(visitFiles.size()==0){
                return;
            }
            String visitejson = gson.toJson(visitFiles);
            BoxDataModel.getBoxDataModel().upVisitFilePlayRecord(visitejson);

        }else if (type == 1){
            if(studyFiles.size()==0){
                return;
            }
            String studyjosn =gson.toJson(studyFiles);
            BoxDataModel.getBoxDataModel().upStudyFilePlayRecord(studyjosn);
        }
    }

    private void getADData(){
        BoxDataModel.getBoxDataModel().getBinds(null);
        String phone = CacheUserInfo.get().getUserPhone();
        if(TextUtils.isEmpty(phone)){
            return;
        }

        if(Collocation.getCollocation().getIDS()==null){
            CacheConfig.get().setAdDodnloadComplete();
            return;
        }

        createDir();
        DebugLog.showLog(this, "开始获取广告....");
        DebugLog.showLog(this,"热门："+CacheConfig.get().getAdHotUpdateTime(CacheUserInfo.get().getUserPhone()));
        DebugLog.showLog(this,"屏保："+CacheConfig.get().getAdBootUpdateTime(CacheUserInfo.get().getUserPhone()));
        DebugLog.showLog(this,"开机："+CacheConfig.get().getAdHotUpdateTime(CacheUserInfo.get().getUserPhone()));
        DebugLog.showLog(this,"官网："+CacheConfig.get().getAdNetUpdateTime(CacheUserInfo.get().getUserPhone()));

        for(int i = 1; i <= 3; i++){
             getAdData(phone,i);
        }
        getAdData(phone,5);
    }

    private void createDir() {
        DebugLog.showLog(this,"创建文件夹");
        try{
            File boxFile = new File(Fixed.getBoxPath());
            if(!boxFile.exists()){
               boxFile.getParentFile().mkdirs();
               boxFile.mkdir();
            }
            File adFile =  new File(Fixed.getAdPath());
            if(!adFile.exists()){
                adFile.getParentFile().mkdirs();
                adFile.mkdir();
            }

            File bootFile =new File(Fixed.getAdPath()+File.separator+"boot");
            if(!bootFile.exists()){
                bootFile.getParentFile().mkdirs();
                bootFile.mkdir();
            }

            File screenFile =new File(Fixed.getAdPath()+File.separator+"screensaver");
            if(!screenFile.exists()){
                screenFile.getParentFile().mkdirs();
                screenFile.mkdir();
            }

            File hotFile =new File(Fixed.getAdPath()+File.separator+"hot");
            if(!hotFile.exists()){
                hotFile.getParentFile().mkdirs();
                hotFile.mkdir();
            }

            File netFile =new File(Fixed.getAdPath()+File.separator+"official");
            if(!netFile.exists()){
                netFile.getParentFile().mkdirs();
                netFile.mkdir();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //打zip包
    private void completeZIPfile(int index, String fileName){
        GreenDaoUtil.getGreenDao().deleteDeviceUserAll();
        File file = new File(Fixed.getAdZipPath());
        if (file.exists() && file.isDirectory()){
            File[] files = file.listFiles();
            if (files != null){
                for (int i = 0; i < files.length; i++){
                    if(files[i].getName().contains(fileName)){
                        files[i].delete();
                    }
                }
            }
        }

        File fileZip = new File(Fixed.getAdZipPath() + File.separator + System.currentTimeMillis() +"_"+fileName+"_"+ NetContent.UN_FINISHED+ ".zip");
        DebugLog.showLog(this, "打包的文件名：" + fileZip.getName());
        try {
            fileZip.getParentFile().mkdirs();
            fileZip.createNewFile();
            int state = PackZip.zip(Fixed.getAdPath()+File.separator+fileName, fileZip.getPath());
                //打包成功后重新搜索设备
            if(state==0){
                File srcfile = new File(Fixed.getAdPath()+File.separator+fileName);
                if(srcfile.exists()&&srcfile.listFiles().length==0){
                     File fixed= new File(Fixed.getAdPath()+File.separator+fileName+File.separator+"wl.txt");
                     fixed.createNewFile();
                     FileWriter fileWriter = new FileWriter(fixed);
                     fileWriter.write(1);
                     fileWriter.flush();
                     fileWriter.close();
                     PackZip.zip(fixed.getPath(),fileZip.getPath());
                }else{
                     changeUpdateTime(index);
                }
             }
            DebugLog.showLog(this, "打包zip的状态:" + state + "文件目录：" + fileZip.getPath());
         }catch(IOException e){
            e.printStackTrace();
            changeUpdateTime(index);
            DebugLog.showLog(this, "创建失败!");
         }
    }

    private void changeUpdateTime(int index){
        switch (index){
            case 1:
                CacheConfig.get().setAdBootUpdateTime(CacheUserInfo.get().getUserPhone(),"");
                break;
            case 2:
                CacheConfig.get().setAdScreenUpdateTime(CacheUserInfo.get().getUserPhone(),"");
                break;
            case 3:
                CacheConfig.get().setAdHotUpdateTime(CacheUserInfo.get().getUserPhone(),"");
                break;
            case 5:
                CacheConfig.get().setAdNetUpdateTime(CacheUserInfo.get().getUserPhone(),"");
                break;
        }
    }

    private void boxDataToServer(){
                List<AppUseInfo> apps = GreenDaoUtil.getGreenDao().getAppUseInfos();
                if (apps != null&&apps.size()!=0){
                    DebugLog.showLog(this,"APP使用数据上传:"+apps.toString());
                    String appJson = gson.toJson(apps);
                    BoxDataModel.getBoxDataModel().upDevAppUse(appJson);
                }
                List<DevAreaUseInfo> devs = GreenDaoUtil.getGreenDao().getDevAreaUseInfos();
                if (devs != null&&devs.size()!=0) {
                    String devJson = gson.toJson(devs);
                    BoxDataModel.getBoxDataModel().upDevUseInfo(devJson);
                }
    }

    private void initSearch(){
        BoxDataModel.getBoxDataModel().getBinds(new DownLoadListener(){
            @Override
            public void OnSuccess(int index, int isUpdate){
                 search();
            }

            @Override
            public void OnFail(int index){
                 search();
            }

        });
    }

    private void search(){
            Device.clear();
            SearchMessage.getIntance(this).sendMessage();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent){
        DebugLog.showLog(this, "onBind");
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        DebugLog.showLog(this, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy(){
        super.onDestroy();
        DebugLog.showLog(this, "onDestroy");
        EventBus.getDefault().unregister(this);
        MessageThread.destoryThread();
    }

    //搜索获取到的结果可以在此发送到handler处理
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(SearchEvent event){
        DebugLog.showLog(this, "result:" + event.getEvent());
        Gson gson = new Gson();
        SearchBean bean = gson.fromJson(event.getEvent(), SearchBean.class);
        if(bean.getIp().equals(NetworkUtils.getNetworkIP())){
            return;
        }

        if(Device.addDevice(bean)){
            Message msg = new Message();
            Bundle bundle = new Bundle();
            bundle.putSerializable("device", bean);
            msg.setData(bundle);
            msg.what = SEARCH_CODE;
            mHandler.sendMessage(msg);
        }
    }

    /*
     * 网络改变时候自动搜索
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNetChangeSearch(NetworkEvent event){
        boolean isConnected = NetworkUtils.isNetworkConnected();
        if(isConnected&isFinish){
            DebugLog.showLog(this, "网络改变了....");
            initSearch();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFileDownloadEvent(FileDownLoadEvent event){
        DebugLog.showLog(this,"文件下载完毕！");
        boolean isConnected = com.shareshow.airpc.util.NetworkUtils.isNetworkConnected();
        if(isConnected&&isFinish){
            initSearch();
        }
    }

    //isFinish:上一次传输盒子有没有结束
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetAdEvent(GetAdEvent event){
        if(updates.size()==0&&isFinish){
            getADData();
         }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event){
        if(event.getCmd()==MessageEvent.SEND_FILE_TO_BOX){
            DebugLog.showLog(this,"传输结束了"+event.isTimeOut());
            isFinish=true;
            if(event.isTimeOut()||isTimeOut){
                DebugLog.showLog(this,"更新广告超时了！");
                return;
            }
            if(beans!=null){
                for (FileChannelBean.ContentBean bean : beans){
                     if(bean.getFileType()==1&&bean.getFileName().contains("zip")){
                EventBus.getDefault().post(new MessageEvent(MessageEvent.SEND_FILE_COMPILETE,"上传广告到任屏成功了！"));
                         return;
                     }

                     if(bean.getFileType()==2||bean.getFileType()==3){
                EventBus.getDefault().post(new MessageEvent(MessageEvent.SEND_FILE_COMPILETE,"上传文件到任屏成功了！"));
                         return;
                      }
                 }
            }

                EventBus.getDefault().post(new MessageEvent(MessageEvent.SEND_FILE_COMPILETE,""));
        }
    }


    public void getAdData(String phone,int type){
                BoxDataModel.getBoxDataModel().getAdCurrentForDevice(phone, String.valueOf(type), new DownLoadListener() {
                    @Override
                    public void OnSuccess(int index, int isUpdate){
                        Message message = new Message();
                        message.what=AD_DOWNLOAD;
                        message.arg1=index;
                        message.arg2=isUpdate;
                        message.obj=true;
                        mHandler.sendMessage(message);
                    }

                    @Override
                    public void OnFail(int index){
                        Message message = new Message();
                        message.what=AD_DOWNLOAD;
                        message.arg1=index;
                        message.arg2=0;
                        message.obj=false;
                        mHandler.sendMessage(message);
                    }
                });
          }
  }
