package com.shareshow.aide.nettyfile;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shareshow.aide.event.MessageEvent;
import com.shareshow.aide.nettyfile.db.GreenDaoUtil;
import com.shareshow.aide.retrofit.entity.StudyMaterialsVisitingMaterials;
import com.shareshow.aide.util.CacheConfig;
import com.shareshow.aide.util.CacheUserInfo;
import com.shareshow.aide.util.Fixed;
import com.shareshow.aide.util.NetContent;
import com.shareshow.airpc.util.DebugLog;
import com.shareshow.db.AppUseInfo;
import com.shareshow.db.DataChannelBean;
import com.shareshow.db.DevAdPlayRecord;
import com.shareshow.db.DevAreaUseInfo;
import com.shareshow.db.FileChannelBean;
import com.shareshow.db.FileCordBean;
import com.shareshow.db.GreenDaoManager;
import com.shareshow.db.SearchBean;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * Created by TEST on 2017/12/12.
 */

public class UploadMessageClientHandler extends ChannelInboundHandlerAdapter {

    private Gson gson;
    private  TcpMessageListener listener;
    public static final int NET_TIME_OUT =0;
    public static final int READ_TIME_OUT =1;
    public static final int WRITE_TIME_OUT =2;
    public static final int ALL_TIME_OUT =3;
    private boolean isTimeOut;

    public UploadMessageClientHandler(TcpMessageListener listener){
        gson = new Gson();
        this.listener = listener;
    }

    public  String createJsonData(String cmd,int del,int type,String content){
        DataChannelBean bean = new DataChannelBean(cmd,content,del,type);
        String json =gson.toJson(bean);
        return json;
    }

    public void channelInactive(ChannelHandlerContext ctx) throws Exception{
         DebugLog.showLog(this,"channelInactive"+isTimeOut);
//         if(listener!=null){
//             listener.complete(isTimeOut);
//         }
         EventBus.getDefault().post(new MessageEvent(MessageEvent.SEND_FILE_TO_BOX,isTimeOut));
    }

    public void channelActive(ChannelHandlerContext ctx){
        DebugLog.showLog(this,"channelActive");
        isTimeOut=false;
        ctx.writeAndFlush(createJsonData(SearchBean.CMD_DATA_ADD,0,1,""));
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception{
            DebugLog.showLog(this,"message:"+msg);
            if (msg instanceof String){
                String message = (String) msg;
                if(message.contains(String.valueOf(SearchBean.CMD_AD))){
                        String content =message.split("&")[1];
                        List<DevAreaUseInfo> list = gson.fromJson(content,new TypeToken<List<DevAreaUseInfo>>(){}.getType());
                        if(list!=null){
                            for (DevAreaUseInfo devAreaUseInfo : list){
                                if(!GreenDaoUtil.getGreenDao().updateDevAreaUseInfo(devAreaUseInfo)){
                                    GreenDaoUtil.getGreenDao().saveDevAreaUseInfo(devAreaUseInfo);
                                }
                            }
                        }
                        ctx.writeAndFlush(createJsonData(SearchBean.CMD_DATA_ADD,1,1,""));
                }else if(message.contains(String.valueOf(SearchBean.CMD_DATA_ADD))){
                        ctx.writeAndFlush(createJsonData(SearchBean.CMD_DATA_USER,0,2,""));
                }else if (message.contains(String.valueOf(SearchBean.CMD_USER))){
                        String content =message.split("&")[1];
                        List<AppUseInfo> list = gson.fromJson(content,new TypeToken<List<AppUseInfo>>(){}.getType());
                        if(list!=null){
                            for (AppUseInfo devAreaUseInfo : list){
                                GreenDaoUtil.getGreenDao().saveAppUserInfo(devAreaUseInfo);
                            }
                        }
                        ctx.writeAndFlush(createJsonData(SearchBean.CMD_DATA_USER,1,2,""));
                }else if(message.contains(String.valueOf(SearchBean.CMD_DATA_USER))){
                        ctx.writeAndFlush(createJsonData(SearchBean.CMD_DATA_RECORD,0,3,""));
                } else if (message.contains(String.valueOf(SearchBean.CMD_RECORD))){
                    String content = message.split("&")[1];
                    List<FileCordBean> list = gson.fromJson(content, new TypeToken<List<FileCordBean>>() {
                    }.getType());
                    DebugLog.showLog(this, "list:" + list.toString());
                    if (list != null) {
                        for (FileCordBean devAreaUseInfo : list) {
                            GreenDaoUtil.getGreenDao().saveFileCordBean(devAreaUseInfo);
                        }
                    }
                    ctx.writeAndFlush(createJsonData(SearchBean.CMD_DATA_RECORD, 1, 3, ""));
                }else if(message.contains(String.valueOf(SearchBean.CMD_DATA_RECORD))){
                        ctx.writeAndFlush(createJsonData(SearchBean.CMD_DATA_UP_ADRECORD,0,3,""));
                }else if(message.contains(String.valueOf(SearchBean.CMD_AD_RECORD))){
                    String content = message.split("&")[1];
                    List<DevAdPlayRecord> list = gson.fromJson(content, new TypeToken<List<DevAdPlayRecord>>(){
                    }.getType());
                    DebugLog.showLog(this, "广告使用数据:" + list.toString());
                    if (list != null){
                        //存数据库
                        for(DevAdPlayRecord devAdPlayRecord : list){
                            GreenDaoUtil.getGreenDao().updateDevAdPlayRecord(devAdPlayRecord);
                        }
                    }
                    ctx.writeAndFlush(createJsonData(SearchBean.CMD_DATA_UP_ADRECORD, 1, 3, ""));
                }else if(message.contains(String.valueOf(SearchBean.CMD_DATA_UP_ADRECORD))){
                    ctx.writeAndFlush(createJsonData(SearchBean.CMD_TIME,0,0,System.currentTimeMillis()+""));
                }else if(message.contains(String.valueOf(SearchBean.CMD_TIME))){
                    File file =new File(Fixed.getAdZipPath());
                    List<FileChannelBean.ContentBean> beans =new ArrayList<FileChannelBean.ContentBean>();
                    if(file.exists()&&file.isDirectory()){
                        File[] files=file.listFiles();
                        for(int i = 0; i < files.length; i++){
                            if(files[i].getName().contains("zip")){
                                if(files[i].getName().contains(NetContent.UN_FINISHED)){
                                    return;
                                }
                                FileChannelBean.ContentBean bean =  new FileChannelBean.ContentBean(files[i].getName(),1,files[i].getName(),files[i].getPath(),"",files[i].length());
                                beans.add(bean);
                             }else{
                                FileChannelBean.ContentBean bean =  new FileChannelBean.ContentBean(files[i].getName(),1,System.currentTimeMillis()+"",files[i].getPath(),"",files[i].length());
                                beans.add(bean);
                            }
                        }
                    }

                    if(!CacheUserInfo.get().getUserPhone().isEmpty()){
                        List<StudyMaterialsVisitingMaterials> list = GreenDaoManager.getDaoSession().getStudyMaterialsVisitingMaterialsDao().loadAll();
                        for (StudyMaterialsVisitingMaterials material : list){
                            File mFile = new File(material.getLocalPath());
                            if(!mFile.exists()){
                                continue;
                            }
                            FileChannelBean.ContentBean bean =  new FileChannelBean.ContentBean(material.getSfFilename(),material.getSfType(),material.getSfId(),material.getLocalPath(),material.getSfSpid(),mFile.length());
                            beans.add(bean);
                        }
                    }
                    FileChannelBean  filebean= new FileChannelBean(SearchBean.CMD_FILE,beans);
                    String json = new Gson().toJson(filebean);
                    ctx.writeAndFlush(json);
                }else if(message.contains(String.valueOf(SearchBean.CMD_FILE))){
                    FileChannelBean bean = new Gson().fromJson(message,FileChannelBean.class);
                    ctx.close();
                    if(listener!=null){
                        listener.success(bean);
                    }
                }
            }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception{
        super.userEventTriggered(ctx, evt);
        isTimeOut=true;
        if (evt instanceof IdleStateEvent){
            IdleStateEvent event = (IdleStateEvent) evt;
            int type = -1;
            if(event.state() == IdleState.READER_IDLE){
                type = READ_TIME_OUT;
            }else if (event.state() == IdleState.WRITER_IDLE){
                type = WRITE_TIME_OUT;
            } else if (event.state() == IdleState.ALL_IDLE){
                type = ALL_TIME_OUT;
            }
             if(listener!=null){
                listener.fail(type);
             }
            DebugLog.showLog(this,"超时了 userEventTriggered type:"+type);
        } else {
            super.userEventTriggered(ctx, evt);
        }
        ctx.close();
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        DebugLog.showLog(this,"exceptionCaught "+cause.getMessage());
        isTimeOut=true;
        if(listener!=null){
            listener.fail(NET_TIME_OUT);
        }
    }
}
