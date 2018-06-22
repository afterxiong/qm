package com.shareshow.aide.mvp.model;

import com.shareshow.aide.nettyfile.DownLoadListener;
import com.shareshow.aide.nettyfile.db.GreenDaoUtil;
import com.shareshow.aide.retrofit.Api;
import com.shareshow.aide.retrofit.RetrofitProvider;
import com.shareshow.aide.util.CacheConfig;
import com.shareshow.aide.util.CacheUserInfo;
import com.shareshow.aide.util.Fixed;
import com.shareshow.aide.util.NetContent;
import com.shareshow.airpc.Collocation;
import com.shareshow.airpc.util.AdSettingUtils;
import com.shareshow.airpc.util.DebugLog;
import com.shareshow.airpc.util.FileNameRuleUtils;
import com.shareshow.airpc.util.FileReNameUtils;
import com.shareshow.db.Adertisement;
import com.shareshow.db.DeviceIds;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

/**
 * Created by TEST on 2018/1/10.
 * 主要是给netty服务请求服务器数据和上传服务器数据
 */

public class BoxDataModel implements BaseModel{

    private  static BoxDataModel boxDataModel=null;
    private Long boot_updateTime;
    private Long screen_updateTime;
    private int screen_timeInterval=3;
    private int screen_timeWait=300;
    private Long hot_updateTime;
    private int hot_timeInterval=3;
    private int boot_videoFlag;
    private String boot_adcasfid;
    private int boot_shemeIndex;
    private int boot_adcIndex;
    private String boot_hotUrl;
    private int screen_videoFlag;
    private String screen_adcasfid;
    private int screen_shemeIndex;
    private int screen_adcIndex;
    private String screen_hotUrl;
    private int hot_videoFlag;
    private String hot_adcasfid;
    private int hot_shemeIndex;
    private int hot_adcIndex;
    private String hot_hotUrl;
    private int hot_type;
    private int screen_type;
    private int boot_type;
    private Long net_updateTime;
    private String net_adcasfid;
    private String net_name;
    private int net_adcIndex;
    private String net_hotUrl;
    private int net_type;
    private Adertisement boot_ad;
    private Adertisement screen_ad;
    private Adertisement hot_ad;
    private Adertisement net_ad;
    private Api api;
    private boolean isNetChange;
    private boolean isScreenChange;
    private boolean isBootChange;
    private boolean isHotChange;


    private BoxDataModel(){
        api= RetrofitProvider.getApi();
    }

    public static BoxDataModel getBoxDataModel(){
            if(boxDataModel==null){
                synchronized (BoxDataModel.class){
                    if (boxDataModel == null){
                        boxDataModel = new BoxDataModel();
                    }
                }
            }
        return boxDataModel;
    }


    /*
     * 获取广告
     */
    public void  getAdCurrentForDevice(String phone, String type, DownLoadListener listener){
          api.getAdCurrentForDevice(phone,type)
                  .subscribeOn(Schedulers.io())
                  .map(new Function<Adertisement, Adertisement>(){
                      @Override
                      public Adertisement apply(Adertisement ad) throws Exception{
                         // DebugLog.showLog(this,"类型:"+type+"---广告数据:"+ad.getList().toString());
                           switch (type){
                               case NetContent.BOOT_AD:
                                   boot_updateTime = ad.getUpdateTime();
                                   boot_ad = ad;
                                   return checkUpdate(boot_ad, type);
                               case NetContent.SCREEN_AD:
                                   screen_updateTime = ad.getUpdateTime();
                                   screen_timeInterval = ad.getTimeInterval();
                                   screen_timeWait = ad.getTimeWait();
                                   screen_ad = ad;
                                   return checkUpdate(screen_ad, type);
                               case NetContent.HOT_AD:
                                   hot_updateTime = ad.getUpdateTime();
                                   hot_timeInterval = ad.getTimeInterval();
                                   hot_ad = ad;
                                   return checkUpdate(hot_ad, type);
                               case NetContent.NET_AD:
                                   net_ad=ad;
                                   net_updateTime=ad.getUpdateTime();
                                   return checkUpdate(net_ad,type);
                               default:
                                      return ad;
                           }
                      }
                  })
                 .flatMap(new Function<Adertisement, ObservableSource<Adertisement.ListBean>>(){
                     @Override
                     public ObservableSource<Adertisement.ListBean> apply(Adertisement ad) throws Exception{
                         if(ad.getList().size() != 0){
                             if(type.equals(NetContent.BOOT_AD)){
                                 return Observable.fromIterable(getAddAD(boot_ad, type));
                             }else if(type.equals(NetContent.SCREEN_AD)){
                                 return Observable.fromIterable(getAddAD(screen_ad, type));
                             }else if(type.equals(NetContent.HOT_AD)){
                                 return Observable.fromIterable(getAddAD(hot_ad, type));
                             }else if(type.equals(NetContent.NET_AD)){
                                 return Observable.fromIterable(getAddAD(net_ad, type));
                             }else{
                                 return Observable.fromIterable(ad.getList());
                             }
                         } else {
                                return Observable.fromIterable(ad.getList());
                         }
                     }
                 })
                 .flatMap(new Function<Adertisement.ListBean, ObservableSource<ResponseBody>>(){
                     @Override
                        public ObservableSource<ResponseBody> apply(Adertisement.ListBean listBean) throws Exception {
                         if(type.equals(NetContent.BOOT_AD)){
                             boot_videoFlag = listBean.getAdcVideoflag();
                             boot_adcasfid = listBean.getAdcAdsfId();
                             boot_shemeIndex = listBean.getAdcAdschemeIndex();
                             boot_adcIndex = listBean.getAdcIndex();
                             boot_hotUrl = FileNameRuleUtils.toEscape(listBean.getAdcHoturl());
                             boot_type =listBean.getAdcType();
                         }else if(type.equals(NetContent.SCREEN_AD)){
                             screen_videoFlag = listBean.getAdcVideoflag();
                             screen_adcasfid = listBean.getAdcAdsfId();
                             screen_shemeIndex = listBean.getAdcAdschemeIndex();
                             screen_adcIndex = listBean.getAdcIndex();
                             screen_hotUrl = FileNameRuleUtils.toEscape(listBean.getAdcHoturl());
                             screen_type =listBean.getAdcType();
                         }else if(type.equals(NetContent.HOT_AD)){
                             hot_videoFlag = listBean.getAdcVideoflag();
                             hot_adcasfid = listBean.getAdcAdsfId();
                             hot_shemeIndex = listBean.getAdcAdschemeIndex();
                             hot_adcIndex = listBean.getAdcIndex();
                             hot_hotUrl = FileNameRuleUtils.toEscape(listBean.getAdcHoturl());
                             hot_type =listBean.getAdcType();
                         }else if(type.equals(NetContent.NET_AD)){
                             net_adcasfid = listBean.getAdcAdsfId();
                             net_name = listBean.getAdcVideourl();
                             net_adcIndex = listBean.getAdcIndex();
                             net_hotUrl = FileNameRuleUtils.toEscape(listBean.getAdcHoturl());
                             net_type =listBean.getAdcType();
                         }
                         if(listBean.getAdcVideoflag()==1){
                             return api.donwnload(listBean.getAdcVideourl().replaceAll("\\\\", "/"));
                         }else{
                             return api.donwnload(listBean.getAdcPicurl().replaceAll("\\\\", "/"));
                         }
                     }
                 })
                 .map(new Function<ResponseBody, File>(){
                     @Override
                     public File apply(ResponseBody responseBody) throws Exception{
                         long length = responseBody.contentLength();
                         String fileName="";
                         String fileDir="";
                         if(type.equals(NetContent.BOOT_AD)){
                             fileDir="boot";
                             if(boot_videoFlag ==1){
                                 fileName = boot_type+"_"+boot_shemeIndex +"_"+ boot_adcIndex +"_"+ boot_adcasfid +"_"+ boot_hotUrl +".mp4";
                             }else{
                                 fileName = boot_type+"_"+boot_shemeIndex +"_"+ boot_adcIndex +"_"+ boot_adcasfid +"_"+ boot_hotUrl +".png";
                             }
                         }else if(type.equals(NetContent.SCREEN_AD)){
                             fileDir="screensaver";
                             if(screen_videoFlag ==1){
                                 fileName = screen_type+"_"+screen_shemeIndex +"_"+ screen_adcIndex +"_"+ screen_adcasfid +"_"+ screen_hotUrl +".mp4";
                             }else{
                                 fileName = screen_type+"_"+screen_shemeIndex +"_"+ screen_adcIndex +"_"+ screen_adcasfid +"_"+ screen_hotUrl +".png";
                             }
                         }else if(type.equals(NetContent.HOT_AD)){
                             fileDir ="hot";
                             if(hot_videoFlag ==1){
                                 fileName = hot_type+"_"+hot_shemeIndex +"_"+ hot_adcIndex +"_"+ hot_adcasfid +"_"+ hot_hotUrl +".mp4";
                             }else{
                                 fileName = hot_type+"_"+ hot_shemeIndex +"_"+ hot_adcIndex +"_"+ hot_adcasfid +"_"+ hot_hotUrl +".png";
                             }
                         }else if(type.equals(NetContent.NET_AD)){
                             fileDir ="official";
                             fileName = net_type+"_"+net_name +"_"+ net_adcIndex +"_"+ net_adcasfid +"_"+ net_hotUrl +".png";
                         }

                         File file =new File(Fixed.getAdPath()+File.separator+fileDir+File.separator+fileName);
                         if(file.exists()&&(file.length()==length)){
                             return file;
                         }else{
                             file.getParentFile().mkdirs();
                             file.createNewFile();
                         }
                         //这样处理可以减少网络中数据过大OOM
                         BufferedSource source = responseBody.source();
                         BufferedSink bufferedSink = Okio.buffer(Okio.sink(file));
                         Buffer buffer = new Buffer();
                         long total = 0;
                         while (!source.exhausted()){
                             long count = source.read(buffer, 1024);
                             total += count;
                             bufferedSink.write(buffer, count);
                         }
                         bufferedSink.flush();
                         bufferedSink.close();
                         source.close();
                         return file;
                     }
                 })
                 .subscribe(new Observer<File>(){

                     @Override
                     public void onSubscribe(Disposable d){

                     }

                     @Override
                     public void onNext(File file){

                     }

                     @Override
                     public void onError(Throwable e){
                         e.printStackTrace();
                         DebugLog.showLog(this,"下载失败!"+e.toString());
                         if(isAdChange(type)){
                             clearFile(type);
                         }

                         if(listener!=null){
                             listener.OnFail(Integer.parseInt(type));
                         }

                     }

                     @Override
                     public void onComplete(){
                         DebugLog.showLog(this,"下载完成!");
                         if(type.equals(NetContent.BOOT_AD)){
                             DebugLog.showLog(this,"更新时间:"+boot_updateTime);
                             if(boot_updateTime ==null|| CacheConfig.get().getAdBootUpdateTime(CacheUserInfo.get().getUserPhone()).equals(String.valueOf(boot_updateTime))){
                                 DebugLog.showLog(this,"不更新BOOT zip");
                                 if(listener!=null){
                                     listener.OnSuccess(Integer.parseInt(type),0);
                                 }
                             }else{
                                 CacheConfig.get().setAdBootUpdateTime(CacheUserInfo.get().getUserPhone(),String.valueOf(boot_updateTime));
                                 //执行打包
                                 if(listener!=null){
                                     listener.OnSuccess(Integer.parseInt(type),1);
                                 }
                             }

                         }else if(type.equals(NetContent.SCREEN_AD)){
                             DebugLog.showLog(this,"更新时间:"+screen_updateTime);
                             if(screen_updateTime ==null||CacheConfig.get().getAdScreenUpdateTime(CacheUserInfo.get().getUserPhone()).equals(String.valueOf(screen_updateTime))){
                                 DebugLog.showLog(this,"不更新SCREEN zip "+screen_updateTime);
                                 if(listener!=null){
                                     listener.OnSuccess(Integer.parseInt(type),0);
                                 }
                             }else {
                                 CacheConfig.get().setAdScreenUpdateTime(CacheUserInfo.get().getUserPhone(),String.valueOf(screen_updateTime));
                                 //执行打包
                                 if (listener != null){
                                     listener.OnSuccess(Integer.parseInt(type), 1);
                                 }
                             }
                         }else if(type.equals(NetContent.HOT_AD)){
                             DebugLog.showLog(this,"更新时间:"+hot_updateTime);
                             if(hot_updateTime ==null||CacheConfig.get().getAdHotUpdateTime(CacheUserInfo.get().getUserPhone()).equals(String.valueOf(hot_updateTime))){
                                 DebugLog.showLog(this,"不更新hot zip ");
                                 if(listener!=null){
                                     listener.OnSuccess(Integer.parseInt(type),0);
                                 }
                             }else{
                                 CacheConfig.get().setAdHotUpdateTime(CacheUserInfo.get().getUserPhone(),String.valueOf(hot_updateTime));
                                 //执行zip打包
                                 if(listener!=null){
                                     listener.OnSuccess(Integer.parseInt(type),1);
                                 }
                             }
                         }else  if(type.equals(NetContent.NET_AD)){
                             if(net_updateTime ==null||CacheConfig.get().getAdNetUpdateTime(CacheUserInfo.get().getUserPhone()).equals(String.valueOf(net_updateTime))){
                                 DebugLog.showLog(this,"不更新Net zip");
                                 if(listener!=null){
                                     listener.OnSuccess(Integer.parseInt(type),0);
                                 }
                             }else{
                                 CacheConfig.get().setAdNetUpdateTime(CacheUserInfo.get().getUserPhone(),String.valueOf(net_updateTime));
                                 //执行打包
                                 if(listener!=null){
                                     listener.OnSuccess(Integer.parseInt(type),1);
                                 }
                             }
                         }
                     }
                 });
          }

    private boolean isAdChange(String type) {
        switch (type){
            case NetContent.BOOT_AD:
                return isBootChange;
            case NetContent.SCREEN_AD:
                return isScreenChange;
            case NetContent.HOT_AD:
                return isHotChange;
            case NetContent.NET_AD:
                return isNetChange;

        }

        return false;
    }


    private void clearFile(String type){
        if(type.equals("1")){
                isBootChange=false;
                File bootFile =  new File(Fixed.getAdPath()+File.separator+"boot");
                if(bootFile.exists()){
                    FileReNameUtils.delAllFile(bootFile.getPath());
                }

        }else if(type.equals("2")){
                isScreenChange=false;
                File screenFile =  new File(Fixed.getAdPath()+File.separator+"screensaver");
                if(screenFile.exists()){
                    FileReNameUtils.delAllFile(screenFile.getPath());
                }
        }else if(type.equals("3")){
                isHotChange=false;
                File hotFile =  new File(Fixed.getAdPath()+File.separator+"hot");
                if(hotFile.exists()){
                    FileReNameUtils.delAllFile(hotFile.getPath());
                }
        }else{
            isNetChange=false;
            File netFile =  new File(Fixed.getAdPath()+File.separator+"official");
            if(netFile.exists()){
                FileReNameUtils.delAllFile(netFile.getPath());
            }
        }
    }


    private Adertisement checkUpdate(Adertisement ad, String type){
         updateAdXml(type);
        if(ad.getList()==null&&ad.getUpdateTime()==null){
            ad.setList(new ArrayList<Adertisement.ListBean>());
            changeUpdate(type);
            clearFile(type);
            return ad;
        }

        if(ad.getList().size()==0){
            clearFile(type);
            return ad;
        }
        String phone = CacheUserInfo.get().getUserPhone();
        switch (type){
            case NetContent.BOOT_AD:
                if (CacheConfig.get().getAdBootUpdateTime(phone).equals(String.valueOf(boot_updateTime))) {
                    ad.setList(new ArrayList<Adertisement.ListBean>());
                    isBootChange=false;
                }else{
                    isBootChange=true;
                }
                break;
            case NetContent.SCREEN_AD:
                if (CacheConfig.get().getAdScreenUpdateTime(phone).equals(String.valueOf(screen_updateTime))) {
                    ad.setList(new ArrayList<Adertisement.ListBean>());
                    isScreenChange=false;
                }else{
                    isScreenChange=true;
                }
                break;

            case NetContent.HOT_AD:
                if (CacheConfig.get().getAdHotUpdateTime(phone).equals(String.valueOf(hot_updateTime))) {
                    ad.setList(new ArrayList<Adertisement.ListBean>());
                    isHotChange=false;
                }else{
                    isHotChange=true;
                }
                break;

            case NetContent.NET_AD:
                if (CacheConfig.get().getAdHotUpdateTime(phone).equals(String.valueOf(net_updateTime))) {
                    ad.setList(new ArrayList<Adertisement.ListBean>());
                    isNetChange=false;
                }else{
                    isNetChange=true;
                }
                break;
         }

            return ad;
    }

    private void updateAdXml(String type){
        switch (type){
            case NetContent.SCREEN_AD:
                AdSettingUtils.getSingle().updataElement("interval_screen", screen_timeInterval);
                AdSettingUtils.getSingle().updataElement("waitTime_pic", screen_timeWait);
                return;
            case NetContent.HOT_AD:
                AdSettingUtils.getSingle().updataElement("interval_hot", hot_timeInterval);
               return;
        }
    }

    private void changeUpdate(String type){
        switch (type){
            case NetContent.BOOT_AD:
                boot_updateTime=System.currentTimeMillis();
                break;
            case NetContent.SCREEN_AD:
                screen_updateTime=System.currentTimeMillis();
                break;
            case NetContent.HOT_AD:
                hot_updateTime=System.currentTimeMillis();
                break;
            case NetContent.NET_AD:
                net_updateTime =System.currentTimeMillis();
                break;
        }
    }


    private List<Adertisement.ListBean> getAddAD(Adertisement ad,String type){
        List<Adertisement.ListBean> updateAds = new ArrayList<Adertisement.ListBean>();
        List<String> filepaths = getFilePaths(ad);//新来的广告
        List<String> fileNames = getAdFileNames(type);//遍历当前广告目录的所有的文件
        String dirPath = getAdFileDirPath(type);//文件夹
        if(fileNames.size()==0){
                updateAds.addAll(ad.getList());
        }else{
            for (Adertisement.ListBean bean : ad.getList()){
                if(!fileNames.contains(getFilePath(bean))){
                    updateAds.add(bean);
                }
            }
        }

        for (String fileName : fileNames){
            if(!filepaths.contains(fileName)){
                File file = new File(dirPath+File.separator+fileName);
                if(file.exists()){
                    file.delete();
                    DebugLog.showLog(this, "类型：" + type+"删除成功:" + file.getPath());
                }
            }
        }
        DebugLog.showLog(this, "类型：" + type+"需要下载：" + updateAds.toString() );
        return updateAds;
    }

    private String getAdFileDirPath(String type){
        switch (type) {
            case NetContent.BOOT_AD:
                return Fixed.getAdPath() + File.separator + "boot";
            case NetContent.SCREEN_AD:
                return Fixed.getAdPath() + File.separator + "screensaver";
            case NetContent.HOT_AD:
                return Fixed.getAdPath() + File.separator + "hot";
            case NetContent.NET_AD:
                return Fixed.getAdPath() + File.separator + "official";
        }

        return "";
    }

    private List<String> getAdFileNames(String type){
        List<String> fileNames =  new ArrayList<String>();
        switch (type) {
            case NetContent.BOOT_AD:
                File bootDirs = new File(Fixed.getAdPath()+File.separator+"boot");
                if(bootDirs.exists()&&bootDirs.isDirectory()&&bootDirs.listFiles()!=null){
                    for (File file : bootDirs.listFiles()) {
                         fileNames.add(file.getName());
                    }
                }

                break;

            case NetContent.SCREEN_AD:
                File screenDirs = new File(Fixed.getAdPath()+File.separator+"screensaver");
                if(screenDirs.exists()&&screenDirs.isDirectory()&&screenDirs.listFiles()!=null){
                    for (File file : screenDirs.listFiles()) {
                        fileNames.add(file.getName());
                    }
                }
                break;


            case NetContent.HOT_AD:
                File hotDirs = new File(Fixed.getAdPath()+File.separator+"hot");
                if(hotDirs.exists()&&hotDirs.isDirectory()&&hotDirs.listFiles()!=null){
                    for (File file : hotDirs.listFiles()) {
                        fileNames.add(file.getName());
                    }
                }

                break;
            case NetContent.NET_AD:
                File netDirs = new File(Fixed.getAdPath()+File.separator+"official");
                if(netDirs.exists()&&netDirs.isDirectory()&&netDirs.listFiles()!=null){
                    for (File file : netDirs.listFiles()){
                        fileNames.add(file.getName());
                    }
                }

                break;
        }

        return fileNames;
    }

    private String getFilePath(Adertisement.ListBean bean){
        if(bean.getAdcVideoflag()==1){
            return bean.getAdcType()+"_"+bean.getAdcAdschemeIndex()+"_"+bean.getAdcIndex()+"_"+bean.getAdcAdsfId()+"_"+bean.getAdcHoturl()+".mp4";
        }else{
           return bean.getAdcType()+"_"+bean.getAdcAdschemeIndex()+"_"+bean.getAdcIndex()+"_"+bean.getAdcAdsfId()+"_"+bean.getAdcHoturl()+".png";
        }
    }

    private List<String> getFilePaths(Adertisement ad){
        List<String> filepaths = new ArrayList<String>();
        for (Adertisement.ListBean bean : ad.getList()){
             if(bean.getAdcVideoflag()==1){
                 filepaths.add(bean.getAdcType()+"_"+bean.getAdcAdschemeIndex()+"_"+bean.getAdcIndex()+"_"+bean.getAdcAdsfId()+"_"+bean.getAdcHoturl()+".mp4");
             }else{
                 filepaths.add(bean.getAdcType()+"_"+bean.getAdcAdschemeIndex()+"_"+bean.getAdcIndex()+"_"+bean.getAdcAdsfId()+"_"+bean.getAdcHoturl()+".png");
             }
        }
        return filepaths;
    }

   /*
    * 获取服务器上面的绑定id
    */
    public void getBinds(DownLoadListener listener){
             api.getBindIds(CacheUserInfo.get().getUserPhone())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<DeviceIds>(){
                    @Override
                    public void accept(DeviceIds info) throws Exception{
                        DebugLog.showLog(this, "绑定设备:" + info + "phone:" + CacheUserInfo.get().getUserPhone());
                        List<String> ids = info.getDatas();
                        if(ids == null){
                            return;
                        }

                        if(ids.size()==0){
                              Collocation.getCollocation().saveBindIds(null);
                        }else{
                            for(String id : ids){
                                Collocation.getCollocation().saveBindIds(id);
                            }
                        }

                        if(listener!=null){
                            listener.OnSuccess(-1,-1);
                        }

                    }
                }, new Consumer<Throwable>(){
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        DebugLog.showLog(this, "throwable:" + throwable);
                        throwable.printStackTrace();
                        if(listener!=null){
                            listener.OnFail(-1);
                        }
                    }
                });
        }


     /*
      * 上传更新广告的设备
      */
    public void upAdReceiverByDevice(String listString){
        api.upAdReceiverByDevice(listString)
           .subscribeOn(Schedulers.newThread())
           .subscribe(new Consumer<ResponseBody>(){
               @Override
               public void accept(ResponseBody responseBody) throws Exception {
                   String result = responseBody.string();
                   if(result.equals("1")){
                       DebugLog.showLog(this, "上传广告的设备成功!" );
                       GreenDaoUtil.getGreenDao().deleteDeviceUserAll();
                   }else{
                       DebugLog.showLog(this, "服务器返回上传广告的设备失败!" );
                   }
               }
           }, new Consumer<Throwable>(){
               @Override
               public void accept(Throwable throwable) throws Exception {
                     throwable.printStackTrace();
                     DebugLog.showLog(this, "上传广告的设备失败!" );
               }
           });
    }

   /*
    * 上传广告的使用数据
    */
    public void upDevAdPlayRecord(String listString){
            api.upDevAdPlayRecord(listString)
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Consumer<ResponseBody>() {
                    @Override
                    public void accept(ResponseBody responseBody) throws Exception {
                        String result = responseBody.string();
                        if(result.equals("1")){
                            GreenDaoUtil.getGreenDao().deleteAllDevAdPlayRecords();
                            DebugLog.showLog(this,"上传广告使用数据成功");
                        }else{
                            DebugLog.showLog(this,"服务器返回上传广告使用数据失败");
                        }
                    }
                }, new Consumer<Throwable>(){
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        DebugLog.showLog(this,"上传广告使用数据失败");
                    }
                });
      }

       /*
        *上传设备使用数据
        *
       */
     public void upDevUseInfo(String listString){
             api.upDevUseInfo(listString)
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Consumer<ResponseBody>(){
                    @Override
                    public void accept(ResponseBody responseBody) throws Exception {
                        String result = responseBody.string();
                        if(result.contains("1")){
                            DebugLog.showLog(this, "设备使用数据上报成功!" );
                            GreenDaoUtil.getGreenDao().deleteAllDevAreaUseInfo();
                        }else{
                            DebugLog.showLog(this, "服务器返回设备使用数据上报失败!" );
                        }
                    }
                }, new Consumer<Throwable>(){
                    @Override
                    public void accept(Throwable throwable) throws Exception{
                        throwable.printStackTrace();
                        DebugLog.showLog(this, "设备使用数据上报失败!" );
                    }
                });
       }

     /*
      * 上传APP的使用数据
      */
    public void upDevAppUse(String listString){
        api.upDevAppUse(listString)
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Consumer<ResponseBody>() {
                    @Override
                    public void accept(ResponseBody responseBody) throws Exception {
                        String result = responseBody.string();
                        if (result.contains("1")){
                            DebugLog.showLog(this, "app使用数据上报成功!");
                            GreenDaoUtil.getGreenDao().deleteAllAppUserInfo();
                        }else{
                            DebugLog.showLog(this, "服务器返回app使用数据上报失败!");
                        }
                    }
                }, new Consumer<Throwable>(){
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        DebugLog.showLog(this, "app使用数据上报失败!");
                    }
                });
    }

    /*
     *上传拜访资料的使用数据
     */
    public void upVisitFilePlayRecord(String listString){
             api.upVisitFilePlayRecord(listString)
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Consumer<ResponseBody>() {
                    @Override
                    public void accept(ResponseBody responseBody) throws Exception {
                        String result =responseBody.string();
                        if(result.equals("1")){
                            DebugLog.showLog(this, "上传拜访资料数据成功!" );
                            GreenDaoUtil.getGreenDao().deletAllFileCordBean();
                        }else{
                            DebugLog.showLog(this, "服务器返回上传拜访资料数据失败!" );
                        }
                    }
                }, new Consumer<Throwable>(){
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        DebugLog.showLog(this, "上传拜访资料数据失败!" );
                    }
                });
    }

    /*
     *上传培训资料的使用数据
     */
    public void upStudyFilePlayRecord(String listString){
        api.upStudyFilePlayRecord(listString)
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Consumer<ResponseBody>(){
                    @Override
                    public void accept(ResponseBody responseBody) throws Exception {
                        String result =responseBody.string();
                        if(result.equals("1")){
                            DebugLog.showLog(this, "上传学习资料数据成功!");
                            GreenDaoUtil.getGreenDao().deletAllFileCordBean();
                        }else{
                            DebugLog.showLog(this, "服务器返回上传学习资料数据失败!");
                        }

                    }
                }, new Consumer<Throwable>(){
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        DebugLog.showLog(this, "上传学习资料数据失败!");
                    }
                });
        }

}
