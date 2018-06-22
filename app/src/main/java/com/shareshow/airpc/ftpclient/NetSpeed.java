package com.shareshow.airpc.ftpclient;

import android.net.TrafficStats;

import java.util.Timer;
import java.util.TimerTask;

public class NetSpeed{

    private long firstRxBytes =  getTotalBytes(0);
    private long firstTxBytes =  getTotalBytes(1);
    private int[] speedInt = new int[2];//以String方式显示网速


    public int[] getSpeedInt() {
        return speedInt;
    }

    public void setSpeedInt(int[] speedInt) {
        this.speedInt = speedInt;
    }

    public NetSpeed(){
        Timer timer = new Timer();
        timer.schedule(timerTask,2000,2000);//每隔2秒获取当前的总字节数
    }

    /**
     * 计时任务 Task
     */
    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            setSpeedInt(geIntSpeed());
        }
    };

    /**
     * @return 以Int类型记录网速
     */
    private  int[] geIntSpeed(){
        long traffic_Rx_data  =  getTotalBytes(0)-firstRxBytes;
        long traffic_Tx_data  =  getTotalBytes(1)-firstTxBytes;
        firstRxBytes = getTotalBytes(0);
        firstTxBytes = getTotalBytes(1);
        int speedRx = (int) (traffic_Rx_data / 2);
        int speedTx = (int) (traffic_Tx_data / 2);
        speedInt[0] = speedRx<65536?65536:speedRx;
        speedInt[1] = speedTx<65536?65536:speedTx;
        return speedInt;
    }
    /**
     * 获取当前的总字节数
     * @param type 0 接受总字节数 ， 1 发送总字节数
     * @return 当前的总字节数
     */
    private Long getTotalBytes(int type){
        long totalBytes = -1;
        if (type == 0) {
            totalBytes = TrafficStats.getTotalRxBytes();//总的接受数据字节数
        }else {
            totalBytes = TrafficStats.getTotalTxBytes();//总的发送数据字节数
        }
        return totalBytes;
    }

    public void closeTimer(){
        if(timerTask!=null){
            timerTask.cancel();
        }
    }
}

