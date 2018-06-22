package com.shareshow.aide.mvp.model;

import android.os.Debug;

import com.shareshow.aide.event.MessageEvent;
import com.shareshow.aide.event.NettyEvent;
import com.shareshow.aide.nettyfile.Device;
import com.shareshow.aide.nettyfile.FileUploadClient;
import com.shareshow.aide.nettyfile.TcpMessageListener;
import com.shareshow.aide.nettyfile.db.GreenDaoUtil;
import com.shareshow.airpc.Collocation;
import com.shareshow.airpc.util.DebugLog;
import com.shareshow.db.DeviceUser;
import com.shareshow.db.FileChannelBean;
import com.shareshow.db.SearchBean;
import org.greenrobot.eventbus.EventBus;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by FENGYANG on 2018/4/16.
 */

public class NettyModel {

    private boolean isRunner = true;

    private BlockingQueue<SearchBean> queues = new LinkedBlockingQueue<SearchBean>(1);

    private boolean isTimeOut=false;

    private List<FileChannelBean.ContentBean> beans;

    private boolean isFinish=true;

    public NettyModel(){
        if(nettyThread!=null&&!nettyThread.isAlive()){
            nettyThread.start();
        }
    }

    public void equeue(SearchBean bean){
        String ids = Collocation.getCollocation().getIDS();
        if(ids==null||!ids.equals(bean.getIds())){
            return;
        }
        boolean isSuccess =queues.offer(bean);
        DebugLog.showLog(this,"添加数据是否成功:"+isSuccess);
    }

    public boolean isFinish(){
        return isFinish;
    }


    private  Thread nettyThread = new Thread(new Runnable() {

        @Override
        public void run(){
             while (isRunner){
                    if(!queues.isEmpty()&&isFinish){
                       SearchBean bean = queues.peek();
                       sendDataToBox(bean);
                   }
             }
        }
    });

    private synchronized void sendDataToBox(SearchBean bean){
        isFinish=false;
        isTimeOut=false;
        beans =null;
        try{
            new FileUploadClient().connectMessage(bean.getPort(), bean.getIp(), new TcpMessageListener(){
                @Override
                public void success(FileChannelBean fileBean){
                    //将获取数据上传到服务器
                   // upDataToService(); 条用service的接口
                    EventBus.getDefault().post(new NettyEvent(NettyEvent.SEND_DATA_TO_SERVER));
                    beans = fileBean.getContent();
                    for(FileChannelBean.ContentBean contentBean : beans){
                        try{
                            new FileUploadClient().connectFile(bean.getPort(), bean.getIp(), contentBean);
                            if(contentBean.getFileName().contains("zip")){
                                DeviceUser deviceUser =new DeviceUser();
                                deviceUser.setIds(bean.getIds());
                                GreenDaoUtil.getGreenDao().savaDeviceUpdate(deviceUser);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                            isTimeOut =true;
                            DebugLog.showLog(this, "发送文件失败！");
                        }
                    }
                }

                @Override
                public void fail(int state){
                    DebugLog.showLog(this,"传输失败了！");
                    isTimeOut =true;
                }

//                @Override
//                public void complete(boolean timeOut){
//                    DebugLog.showLog(this, "发送数据完成!");
//                    dataOperation(timeOut);
//                    queues.poll();
//                    isFinish=true;
//                }
            });
        }catch (Exception e){
            DebugLog.showLog(this, "发送失败!");
            e.printStackTrace();
            isTimeOut =true;
        }
    }

    private void dataOperation(boolean flag){
        if(flag||isTimeOut){
            DebugLog.showLog(this,"更新广告超时了！");
            return;
        }

        if(beans!=null){
            for (FileChannelBean.ContentBean bean : beans){
                if(bean.getFileType()==1&&bean.getFileName().contains("zip")){
                    EventBus.getDefault().post(new NettyEvent(NettyEvent.SEND_TO_BOX_AD_COMPLETE));
                    return;
                }

                if(bean.getFileType()==2||bean.getFileType()==3){
                    EventBus.getDefault().post(new NettyEvent(NettyEvent.SEND_TO_BOX_FILE_COMPLETE));
                    return;
                }
            }
        }

        EventBus.getDefault().post(new NettyEvent(NettyEvent.SEND_TO_BOX_COMPLETE));
    }


    public void realse(){
        isRunner=false;
        if(nettyThread!=null){
            nettyThread.interrupt();
            nettyThread.stop();
            nettyThread=null;
        }
        queues.clear();
        queues=null;
    }

}
