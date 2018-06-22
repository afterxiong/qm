package com.shareshow.airpc.record;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.widget.Toast;

import com.shareshow.App;
import com.shareshow.airpc.Collocation;
import com.shareshow.airpc.ports.ConnectFTPListener;
import com.shareshow.airpc.util.CommonTimer;
import com.shareshow.airpc.util.DebugLog;
import com.shareshow.airpc.util.HairUtils;
import com.shareshow.airpc.util.ITimerCallback;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import audio.shareshow.com.Utils.INotify;

/**
 * Created by TEST on 2017/12/15.
 */

public class RecordManager implements INotify,EncoderListener,ITimerCallback {

    static final String RECORD_LOG ="RecordBit.file_icon_txt";
    private static final String FILE_PATH ="mnt/sdcard/renyiping/setting.file_icon_txt";
    private static final String LOG ="mnt/sdcard/renyiping/";
    // ==============原始提供的===========start================
    private boolean mMuxerStarted = false;
    private MediaProjection mMediaProjection;
    private Surface mInputSurface;
    private MediaMuxer mMuxer;
    private MediaCodec mVideoEncoder;
    private MediaCodec.BufferInfo mVideoBufferInfo;
    private int mTrackIndex = -1;
    private String mp4file = "mnt/sdcard/video11.mp4";
    private static final String VIDEO_MIME_TYPE = "video/avc";
    private String sps = null;
    private String pps = null;
    private byte[] nativePPS=null;
    private byte[] nativeSPS=null;
    private MediaProjectionManager mMediaProjectionManager;
    private static final int REQUEST_CODE_CAPTURE_PERM = 1234;
    private static final String TAG = "RecordScreenActivity";
    private static Boolean bint = false;
    private static Boolean bstarttask = false;
    private int framecount = 0;
    public int bitRate = 2500;// 码率
    public int frameRate = 30;// 帧率
    public static int VIDEO_WIDTH = 1080;
    public static int VIDEO_HEIGHT = 1920;
    private  int repeat_frame=1000000;
    private int i_frame_interval=2;

    //   private Timer timer;

    // ================原始提供的===========end====================

    // ================扩展的属性==========================

    public static boolean canT = true;// 系统有没有在截屏,在一开始时候就进行弹框，系统自带的弹框

    //public DataTrans datatrans = DataTrans.getInstance();// 手机允许截屏就用该类去调用 投屏底层提供的方法

    public DataTrans datatrans = new DataTrans();

    public boolean supportScreen = true;// 手机是否支持截屏

    private ConnectFTPListener mLoinListener;// 给MainAcitivity提供有没有截屏的回调接口

    public int typeT = 0;// 是给任盒投屏还是给手机投屏 两者要求的截屏分辨率不一样 默认给任盒投屏



    private final Handler mDrainHandler = new Handler(Looper.getMainLooper());
    private Runnable mDrainEncoderRunnable = new Runnable(){

        @Override
        public void run(){
            drainEncoder();
        }
    };

    private long bb;
    private SimpleDateFormat formater;
    private Timer timer;
    private boolean readSettting=false;
    private int screenDensity=-1;

    private boolean isRateConstant=true;
    private  static boolean isLogOpen=false;
    private  boolean isRequestRate=false;
    public   boolean isRateThrow=false;
    private long requestCount;
    private boolean isFirstData =true;
    private static boolean isReleaseEncoder =false;
    private int iForceCnt;
    private CommonTimer iFrameforceTimer;
    public boolean isSDpermission=true;
    private  long startTime;
    private static RecordManager recordManager =null;


    private RecordManager(){

    }


    public static  RecordManager getRecordManager(){

         if(recordManager==null){
             synchronized (RecordManager.class){
                 if(recordManager==null){
                     recordManager = new RecordManager();
                 }
             }
         }

         return recordManager;
    }

    /**
     * 请求截屏
     *
     * @param mLoinListener
     *
     */

    public void startrecord(ConnectFTPListener mLoinListener,RecodListener listener){

//        if(isSDpermission){
            this.mLoinListener = mLoinListener;

            if(listener!=null){
                listener.startRecod();
                isFirstData =true;
            }
            // 屏幕宽高
//        DisplayMetrics dm = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(dm);
//        VIDEO_WIDTH = dm.widthPixels;
//        VIDEO_HEIGHT = dm.heightPixels;
//            if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
//                mMediaProjectionManager = (android.media.projection.MediaProjectionManager)mContext.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
//                Intent permissionIntent = mMediaProjectionManager.createScreenCaptureIntent();
//                startActivityForResult(permissionIntent, REQUEST_CODE_CAPTURE_PERM);

//            }
//        }else{
//
//            DebugLog.showLog(this,"权限被拒绝了");
//
//        }
    }

    /**
     * 系统回调用户是否要截屏
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void onActivityResult(MediaProjectionManager mMediaProjectionManager,int requestCode, int resultCode, Intent intent){
            if(resultCode == Activity.RESULT_OK){// 允许截屏
                DebugLog.showLog(this,"onActivityResult");
                isReleaseEncoder=false;
                this.mMediaProjectionManager =mMediaProjectionManager;
                canT = true;
                if (android.os.Build.VERSION.SDK_INT > 19){
                    mMediaProjection = mMediaProjectionManager.getMediaProjection(
                            resultCode, intent);
                    try {
                        mMuxer = new MediaMuxer(mp4file,
                                MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
                    } catch (IOException ioe) {
                        throw new RuntimeException("MediaMuxer creation failed",
                                ioe);
                    }
                }
//                if(datatrans!=null){
//                    datatrans.setListener(this);
//                }
                switch (typeT){
                    case 0:// 任盒投屏的分辨率
//                        if (!Collocation.getCollocation().getExpand()) {// 固定分辨率截屏
//                            VIDEO_WIDTH = 1920;
//                            VIDEO_HEIGHT = 1080;
//                        } else {// 全屏截屏------手机在此处无法投屏
//                            DisplayMetrics dm = new DisplayMetrics();
//                            getWindowManager().getDefaultDisplay().getMetrics(dm);
//                            VIDEO_WIDTH = dm.widthPixels;
//                            VIDEO_HEIGHT = dm.heightPixels;
//                        }
                        break;
                    case 1:// 手机投屏的分辨率
//                        VIDEO_WIDTH = 720;
//                        VIDEO_HEIGHT = 1280;
                        break;
                }
                if(!bint){
                    startRecording();// 开始截屏
                }else{
                    DebugLog.showLog(this,"已经正在截屏了又初始化编码器出现erro！");
                }

                bstarttask = true;
                if (mLoinListener != null)
                    mLoinListener.success();
            } else {
                canT = false;
                if (mLoinListener != null)
                    mLoinListener.fail(-1);
            }
    }

    /**
     * 停止截屏
     */

    public void endrecord(){
        //  audioTranAPI.StopAudioTran();

        // Log.d(TAG, "......" + "停止截屏录制了");
        if(!isReleaseEncoder){
            DebugLog.showLog(this,"......" + "停止截屏录制了");
            if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
                releaseEncoders();
            }
            if(datatrans!=null){
                datatrans.DestroyMeidaRouter();
            }

            bstarttask = false;
            bint = false;
            canT =false;
            isReleaseEncoder=true;
        }

        if(fw!=null){
            try{
                fw.close();
                fw=null;
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }


    /**
     * 重新进行截屏-----主要是手机和任盒间的投屏切换 或者是分辨率从高清到标准间的切换
     *
     * @param mLoinListener
     * @param i
     */

//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    public void alterF(ConnectFTPListener mLoinListener, int i){
//        switch (i){
//            case 0:
//                endrecord();
//                break;
//            case 1:
//                releaseEncoders();
//                break;
//        }
//
//        startrecord(mLoinListener);
//    }



    /**
     * 开始截屏录制了...
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void startRecording(){
        DebugLog.showLog(this,"startRecording");
        //  if (Collocation.getCollocation().getAudio()){
        datatrans.audioChannelState = true;
        //   audioTranAPI.StartAudioTran();
        //   }
        Log.d(TAG, "......" + "开始截屏录制了");
        setDensity(HairUtils.getScreenMetrics());
        prepareVideoEncoder();
        DebugLog.showLog(this,"编码分辨率："+"VIDEO_WIDTH:"+VIDEO_WIDTH+"VIDEO_HEIGHT:"+VIDEO_HEIGHT+"screenDensity:"+ screenDensity);
        mMediaProjection.createVirtualDisplay("Recording Display", VIDEO_WIDTH, VIDEO_HEIGHT, screenDensity, 0, mInputSurface, null, null);
        formater = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS");
        drainEncoder();
    }

    private void setDensity(DisplayMetrics metrics){
        screenDensity = metrics.densityDpi;

//            VIDEO_WIDTH =(typeT ==0 ? 1280:720);
//            VIDEO_HEIGHT =(typeT ==0 ? 720:1280);

        if(metrics.widthPixels>1080){
            VIDEO_WIDTH =(typeT ==0 ? 1920:1080);
            VIDEO_HEIGHT =(typeT ==0 ? 1080:1920);
        }else{
            VIDEO_WIDTH = (typeT == 0 ? metrics.heightPixels : metrics.widthPixels);
            VIDEO_HEIGHT = (typeT == 0 ? metrics.widthPixels : metrics.heightPixels);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void prepareVideoEncoder(){
        initSurface();
        mVideoEncoder.start();
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void initSurface(){
        bitRate= Collocation.getCollocation().getNetbitRate();
        frameRate=Collocation.getCollocation().getprogress();
        mVideoBufferInfo = new MediaCodec.BufferInfo();
        // getSetting();
        MediaFormat format = MediaFormat.createVideoFormat(VIDEO_MIME_TYPE,
                VIDEO_WIDTH, VIDEO_HEIGHT);
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT,
                MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        //设置编码格式
        format.setInteger(MediaFormat.KEY_BIT_RATE, bitRate * 1000);
        //设置码率
        format.setInteger(MediaFormat.KEY_FRAME_RATE, frameRate);
        //设置帧率
        format.setInteger(MediaFormat.KEY_CAPTURE_RATE, frameRate);

        format.setInteger(MediaFormat.KEY_REPEAT_PREVIOUS_FRAME_AFTER, repeat_frame);
        //    format.setInteger(MediaFormat.KEY_REPEAT_PREVIOUS_FRAME_AFTER, 25000 / frameRate);
        if(isRateConstant){
            // save2("===码率设置为恒定码率===");
            DebugLog.showLog(this,"===码率设置为恒定码率===");
            format.setInteger(MediaFormat.KEY_BITRATE_MODE, MediaCodecInfo.EncoderCapabilities.BITRATE_MODE_CBR);
        }

        format.setInteger(MediaFormat.KEY_CHANNEL_COUNT, 1);

        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, i_frame_interval); // 1 seconds
        //设置I帧间隔

        DebugLog.showLog(this,"帧率："+frameRate+" 码率："+bitRate+"---"+" i帧间隔："+i_frame_interval+" 其他："+repeat_frame+"  分辨率："+VIDEO_WIDTH+"*"+VIDEO_HEIGHT+"  "
                +"  是否丢帧："+isRateThrow+" 是否请求： "+isRequestRate+"定码率："+isRateConstant);
        try {
            mVideoEncoder = MediaCodec.createEncoderByType(VIDEO_MIME_TYPE);
            mVideoEncoder.configure(format, null, null,
                    MediaCodec.CONFIGURE_FLAG_ENCODE);
            mInputSurface = mVideoEncoder.createInputSurface();
        } catch (Exception e){
            e.printStackTrace();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                releaseEncoders();
            }
        }
    }

    int i = 0;
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private boolean drainEncoder(){
        mDrainHandler.removeCallbacks(mDrainEncoderRunnable);
        while (true){
            int bufferIndex = mVideoEncoder.dequeueOutputBuffer(
                    mVideoBufferInfo, 0);
            // 第二个参数为0，表示不等待；
//          if(bufferIndex==MediaCodec.BUFFER_FLAG_KEY_FRAME){

//              save2("我是I帧....");
//            }

            if (bufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER){
                // nothing available yet
                break;
            } else if (bufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED){
                // should happen before receiving buffers, and should only
                // happen once
                DebugLog.showLog(this,"mTrackIndex :"+mTrackIndex);

                if (mTrackIndex >= 0){
                    throw new RuntimeException("format changed twice");
                }

                mTrackIndex = mMuxer.addTrack(mVideoEncoder.getOutputFormat());
                if (!mMuxerStarted && mTrackIndex >= 0){
                    mMuxer.start();
                    mMuxerStarted = true;
                }
            }else if (bufferIndex < 0){
                // not sure what's going on, ignore it
            }else{
                ByteBuffer encodedData = mVideoEncoder
                        .getOutputBuffer(bufferIndex);
                if (encodedData == null){
                    throw new RuntimeException(
                            "couldn't fetch buffer at index " + bufferIndex);
                }

                if ((mVideoBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0){
                    mVideoBufferInfo.size = 0;
                }

                if (mVideoBufferInfo.size != 0){
                    if (mMuxerStarted){
                        encodedData.position(mVideoBufferInfo.offset);
                        encodedData.limit(mVideoBufferInfo.offset
                                + mVideoBufferInfo.size);

                        if (!bint){
                            mMuxer.writeSampleData(mTrackIndex, encodedData,
                                    mVideoBufferInfo);
                            // framecount++;

                            Log.d(TAG, "framecount=" + framecount);
                            // if (framecount > recordcount)
                            if (bstarttask){
                                if (mMuxer != null){
                                    if (mMuxerStarted){
                                        mMuxer.stop();
                                    }
                                    mMuxer.release();
                                    mMuxer = null;
                                    File temp_file = new File(mp4file);
                                    if (temp_file.exists()){

                                        try{
                                            MP4Config mp4Config = new MP4Config(
                                                    mp4file);
                                            sps = mp4Config.getB64SPS();
                                            pps = mp4Config.getB64PPS();
                                            nativePPS=mp4Config.getPps();
                                            nativeSPS=mp4Config.getSps();
                                            DebugLog.showLog(this,"sps:"+sps+"  pps:"+pps);
                                        }catch(FileNotFoundException e){
                                            Log.e(TAG,"parse sps faild for file not found");
                                        }catch (IOException e){
                                            Log.e(TAG, "parse sps IOException");
                                        }

                                        datatrans.CreateMeidaRouter(sps, pps);
                                        Log.d(TAG, "CreateMeidaRouter");
                                    }

                                    bstarttask = false;
                                    bint = true;
                                }
                                framecount = 0;
                            }
                        }

                        if (bint){
                            byte[] outData;
                            outData = new byte[mVideoBufferInfo.size];
                            encodedData.get(outData);
                            byte[] outFirstIFrameData = null;

                            // size=size+outData.length;
                            // Log.d(TAG,"次数"+(++length)+"===大小"+outData.length+"====总大小"+size);
//                             DataTrans.sendVideoData(sps.getBytes(),
//                             pps.getBytes(), outData, mVideoBufferInfo.size);


                            if (timer == null){
                                timer = new Timer();
                                timer.scheduleAtFixedRate(new RefreshTask(), 0, 500);
                            }


                            if(isFirstData){
                                DebugLog.showLog(this,"第一帧数据："+mVideoBufferInfo.size);
                                printTimeLog("帧的大小："+mVideoBufferInfo.size+"帧的序号："+1+"帧的总数"+ mVideoBufferInfo.size);
                                //  isFirstData=false;
                                //printTimeLog("帧的大小："+mVideoBufferInfo.size+"帧的序号："+1+"帧的总数"+ mVideoBufferInfo.size);
                                outFirstIFrameData =  new byte[outData.length];
                                System.arraycopy(outData, 0,outFirstIFrameData,0,outFirstIFrameData.length);
                            }

                            datatrans.sendVideoData(nativeSPS,
                                    nativePPS, outData,
                                    mVideoBufferInfo.size);


                            if(isFirstData){
                                /**
                                 *   重传第一个I帧
                                 */
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                datatrans.sendVideoData(nativeSPS, nativePPS, outFirstIFrameData, outFirstIFrameData.length);
                                isFirstData = false;

                                /**
                                 *  TODO
                                 *
                                 *  周期（500ms）I帧请求,加上MediaCodec延时，估计I帧间隔接近1秒
                                 *
                                 */
                                iFrameforceTimer = new CommonTimer();
                                iFrameforceTimer.Start(500, this);
                                iForceCnt = 0;
                            }


//                            int a = mVideoBufferInfo.size;
//                            bb = bb + mVideoBufferInfo.size;

                            // String format = formater.format(new Date());
                            // Log.i("test","_包的大小："+a+"_数据总量:"+bb);

                            // if(readSettting){
                            //  saveH264(outData);
                            //   save2(""+format+"  包的大小"+a+"_数据总量"+bb,RECORD_LOG);
                            // }
                            startTime = System.currentTimeMillis();
                        }

                    } else {
                        // muxer not started
                    }
                }

                mVideoEncoder.releaseOutputBuffer(bufferIndex, false);

                if ((mVideoBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0){
                    break;
                }
            }
        }

        mDrainHandler.postDelayed(mDrainEncoderRunnable, 10);

        return false;
    }



    /**
     * @param text
     * 生成的文件的名称,和内容
     */
    public static File file;

    private FileWriter fw;

    public void save2(String text){
        if(isLogOpen){
            if(fw==null){
                SimpleDateFormat data = new SimpleDateFormat("yyyyMMddHHmm");
                String name=data.format(new Date());
                StringBuffer buffer= new StringBuffer();
                buffer.append(LOG);
                buffer.append(name);
                buffer.append("_"+VIDEO_HEIGHT);
                buffer.append("_"+bitRate);
                buffer.append("_"+frameRate);
                buffer.append("_log.file_icon_txt");
                try{
                    if (file == null){
                        file = new File(buffer.toString());
                    }

                    if(!file.exists()){
                        file.getParentFile().mkdir();
                        file.createNewFile();
                        //file.mkdirs();
                    }
                    fw = new FileWriter(file, true);
                    // fw.close();
                }catch (Exception e){
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }else{
                try{
                    fw.write(text + "\n");
                    fw.flush();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

        }
    }

    public void transModeStop() {
        // flag=false;
        // mGetEncoder=null;

        if (mVideoEncoder != null) {
            mVideoEncoder.stop();
            mVideoEncoder.release();
            mVideoEncoder = null;
        }
        if (mInputSurface != null) {
            mInputSurface.release();
            mInputSurface = null;
        }

        mVideoBufferInfo = null;

        // mTrackIndex = -1;
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void releaseEncoders(){
//        if (Collocation.getCollocation().getAudio()) {
//            audioTranAPI.StopAudioTran();
//        }
        // flag=false;
        // mGetEncoder=null;
        mDrainHandler.removeCallbacks(mDrainEncoderRunnable);
        mMuxerStarted = false;
        bint = false;
        bstarttask = false;

        if (mVideoEncoder != null){
            mVideoEncoder.stop();
            mVideoEncoder.release();
            mVideoEncoder = null;
        }
        if (mInputSurface != null) {
            mInputSurface.release();
            mInputSurface = null;
        }
        if (mMediaProjection != null) {
            mMediaProjection.stop();
            mMediaProjection = null;
        }
        mVideoBufferInfo = null;
        // mDrainEncoderRunnable = null;
        mTrackIndex = -1;

        if(timer!=null){
            timer.cancel();
            timer=null;
        }
    }

    @Override
    public void onDataComeing(Object context, byte[] data, int size) {
//        if (audioTranAPI == null) {
//            return;
//        }
//        if (Recorder.class.isInstance(context)) {
//            Log.d(TAG, " Recorder data !!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//            if (audioTranAPI.getmDataPusher() != null) {
//                BufferChunk bufferChunk = new BufferChunk(data, size,
//                        System.nanoTime());
//                audioTranAPI.getmDataPusher().push(bufferChunk);
//            }
//        } else if (Encoder.class.isInstance(context)) {
//          audioTranAPI.saveInAACfile(data, size);
//            datatrans.sendAudioData(data, size);
//        }

    }

    @Override
    public void onStateChanged(Object o, int i) {

    }

    @Override
    public void onRecordSuccee(boolean b) {

    }


    private void getSetting(){
        File file= new File(FILE_PATH);
        if(file.exists()&&readSettting){
            try {
                FileReader fr= new FileReader(file);
                String text =null;
                BufferedReader rr= new BufferedReader(fr);
                StringBuffer sr= new StringBuffer();
                while ((text=rr.readLine())!=null){
                    sr.append(text);
                }
                // Toast.makeText(this,""+sr.toString(),Toast.LENGTH_SHORT).show();
                String[] str=  sr.toString().split("&&");
                frameRate= Integer.parseInt(str[1].split("=")[1]);
                bitRate= Integer.parseInt(str[2].split("=")[1]);
                i_frame_interval= Integer.parseInt(str[3].split("=")[1]);
                repeat_frame= Integer.parseInt(str[4].split("=")[1]);
                String isRate = str[8].split("=")[1];
                String rate="";
                if(isRate.equals("true")){
                    isRateConstant =true;
                    rate="定码率";
                }else{
                    isRateConstant =false;
                    rate="变码率";
                }

                String isThrow = str[9].split("=")[1];
                if (isThrow.equals("true")) {
                    isRateThrow =true;
                    isThrow="丢帧处理开启";
                } else {
                    isRateThrow =false;
                    isThrow="丢帧处理关闭";
                }

                String isRequest = str[10].split("=")[1];
                if(isRequest.equals("true")){
                    isRequestRate =true;
                    isRequest="I帧请求已经开启";
                }else{
                    isRequestRate =false;
                    isRequest="I帧请求已经关闭";
                }

//                if(datatrans!=null){
//                    datatrans.setRateThrow(isRateThrow);
//                    datatrans.setFrame(frameRate);
//                    datatrans.setRequestRate(isRequestRate);
//                }

                String isLogSwitch = str[11].split("=")[1];
                if (isLogSwitch.equals("true")) {
                    isLogOpen =true;
                    isLogSwitch="日志已开启";
                } else {
                    isLogOpen =false;
                    isLogSwitch="日志已关闭";
                }

                int width= Integer.parseInt(str[5].split("=")[1]);
                if(width!=1){
                    int hight= Integer.parseInt(str[6].split("=")[1]);
//                    VIDEO_WIDTH =(typeT ==0 ? hight:width);
//                    VIDEO_HEIGHT =(typeT ==0 ? width:hight);
                    if(width>1080){
                        VIDEO_WIDTH =(typeT ==0 ? 1920:1080);
                        VIDEO_HEIGHT =(typeT ==0 ? 1080:1920);
                    }else{
                        VIDEO_WIDTH =(typeT ==0 ? hight:width);
                        VIDEO_HEIGHT =(typeT ==0 ? width:hight);
                    }
                }
                Toast.makeText(App.getApp(),"帧率："+frameRate+" 码率："+bitRate+"---"+rate+" i帧间隔："+i_frame_interval+" 其他："+repeat_frame+"  分辨率："+VIDEO_WIDTH+"*"+VIDEO_HEIGHT+"  "
                        +"   "+isThrow+"  "+isRequest+"  "+isLogSwitch, Toast.LENGTH_SHORT).show();
                // Log.i("test","文件读取成功:"+"帧率："+frameRate+"码率："+bitRate+"i帧间隔："+i_frame_interval+"其他："+repeat_frame);
            }catch (Exception e){
                e.printStackTrace();
                Log.i("test","文件读取失败！");
                readSettting=false;
            }
        }else{
            Log.i("test","参数文件不存在！");
            readSettting=false;
        }

    }



    @Override
    public void printTimeLog(String log){
        String format =formater.format(new Date());
        StringBuffer buffer= new StringBuffer();
        buffer.append("  ");
        buffer.append(format);
        buffer.append("  ");
        buffer.append(log);
        save2(buffer.toString());
    }

    @Override
    public void StartTimer(){


    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void requestRate(){
        // if (!datatrans.is_i_frame()){
        DebugLog.showLog(this,"请求了I帧");
        requestCount++;
        Bundle bit = new Bundle();
        bit.putInt(MediaCodec.PARAMETER_KEY_REQUEST_SYNC_FRAME, 0);
        if(mVideoEncoder!=null){
            mVideoEncoder.setParameters(bit);
        }
        // printTimeLog("====请求发送第" + requestCount + "个I帧====");
        //  datatrans.setIs_i_frame(true);
        //  datatrans.setRequestFrameTime(System.currentTimeMillis());
        // }
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onTimer() {
        iForceCnt++;
        if (iForceCnt >5){
            /**
             *  TODO
             *
             *  在接受投屏成功消息后或投屏请求超时 停止周期I帧请求
             *   本处供简单测试
             */
            new Thread(new Runnable(){
                @Override
                public void run() {
                    if(iFrameforceTimer != null){
                        iFrameforceTimer.Stop();
                    }
                }
            } ).start();

        }else{
            requestRate();
        };

    }


     private  class  RefreshTask extends TimerTask {

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        public void run(){
            if((System.currentTimeMillis()-startTime)>2*1000){
                // datatrans.sendVideoData(sps.getBytes(),pps.getBytes()," ".getBytes(),0);
                requestRate();
                startTime = System.currentTimeMillis();
            }

        }

    }


}
