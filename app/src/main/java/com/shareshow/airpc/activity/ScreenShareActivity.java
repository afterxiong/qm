package com.shareshow.airpc.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shareshow.aide.R;
import com.shareshow.airpc.animutils.AnimationsContainer;
import com.xtmedia.xtview.GlRenderNative;
import com.shareshow.airpc.Collocation;
import com.shareshow.airpc.QMCommander;
import com.shareshow.airpc.adapter.ScreenShareAdapter;
import com.shareshow.airpc.model.RootPoint;
import com.shareshow.airpc.ports.BoxOnClickListener;
import com.shareshow.airpc.ports.MoveListener;
import com.shareshow.airpc.ports.PositionListener;
import com.shareshow.airpc.socket.command.CommandExectuorMobile;
import com.shareshow.airpc.socket.command.CommandExecutorBox;
import com.shareshow.airpc.socket.command.CommandExecutorLancher;
import com.shareshow.airpc.socket.command.CommandListenerBox;
import com.shareshow.airpc.socket.command.CommandListenerMobile;
import com.shareshow.airpc.socket.common.HeatbeatEvent;
import com.shareshow.airpc.socket.common.QMDevice;
import com.shareshow.airpc.socket.common.QMSurfaceLayout;
import com.shareshow.airpc.socket.common.ScreenStarteListener;
import com.shareshow.airpc.util.DebugLog;
import com.shareshow.airpc.util.NetworkUtils;
import com.shareshow.airpc.util.QMDialog;
import com.shareshow.airpc.util.QMPopupWindow;
import com.shareshow.airpc.util.QMUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ScreenShareActivity extends ConnectLancherActivity implements MoveListener, CommandListenerBox, CommandListenerMobile,ScreenStarteListener {

    public static final int code2 = 886;//返回MainActivity的
    //播放SurfaceView
//    @BindView(R.id.surface_layout)
    public QMSurfaceLayout surface_layout;
    //头部
    @BindView(R.id.show_head)
    RelativeLayout show_head;
    @BindView(R.id.back)
    LinearLayout back;
    @BindView(R.id.text)
    TextView text;
    @BindView(R.id.menu)
    Button menu;
    //倒计时返回----分享结束的布局
    @BindView(R.id.show_close)
    RelativeLayout show_close;
    @BindView(R.id.end_play)
    TextView end_play;
    @BindView(R.id.close)
    TextView close;
    //显示加载的美团动画的布局
    @BindView(R.id.show_loading)
    public RelativeLayout show_loading;
    @BindView(R.id.loadingImage)
    ImageView loadingImage;
    @BindView(R.id.play_fame)
    TextView play_fame;
    //显示屏幕亮度的布局
    @BindView(R.id.light_bg)
    LinearLayout light_bg;
    @BindView(R.id.light_count)
    TextView light_count;
    //调节系统声音的布局
    @BindView(R.id.show_audio)
    LinearLayout show_audio;
    @BindView(R.id.vpb_right)
    TextView vpb_right;

    private QMPopupWindow right_popupWindow;// 弹出右上角的菜单
  //  @BindView(R.id.listserver)
    private ListView list;// 右上角弹出视图布局中控件
  //  @BindView(R.id.swipe_refresh)
    private SwipeRefreshLayout swipeLayout;
    private ScreenShareAdapter adapter;

    //=================亮度及声音的控制=====================
    /**
     * 当前亮度
     */
    private float mBrightness = -1f;
    private float mOldY = 0;
    private float mOldY2 = 0;
    private boolean dir1 = true, dir3 = true;
    private boolean show2 = false;
    private AudioManager mAudioManager;

    /**
     * 最大声音
     */
    private int mMaxVolume;
    /**
     * 当前声音
     */
    private int mVolume = -1;
    private int rightProgress = 0;
    private boolean isPlay;//当前是不是成功分享了画面
    private RootPoint rootPoint = null;
    private int count = 5;//5秒倒计时关闭分享
    private int maxHeight = 0;
    private int maxWidth = 0;
    private Handler pHandler = null;
    private RelativeLayout root;
    private boolean isShareScreen;

    private ArrayList<RootPoint> shareRoots=new ArrayList<RootPoint>();
    private AnimationsContainer.FramesSequenceAnimation animation;
    private boolean isClose;
    private CountDownTimer timer;

    public RootPoint getRootPoint(){
        return rootPoint;
    }

    public boolean isPlay(){
        return isPlay;
    }


    public void setPlay(boolean isPlay){
        this.isPlay = isPlay;
        if(this.rootPoint!=null){
            RootPoint rp= QMDevice.getInstance().getSameRootPoint(rootPoint);
            if(rp!=null){
                rp.setShareScreen(isPlay);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //无标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_screenshare);
        root = (RelativeLayout) findViewById(R.id.root);
        surface_layout = (QMSurfaceLayout)findViewById(R.id.surface_layout);
       // x.view().inject(this);
        ButterKnife.bind(this);
        initPopupWindow();// 初始化右侧菜单弹出框
        initAdapter();
        //初始化声音
        initVolume();
      //  stopAutoBrightness(this);//此功能是系统如果禁止调节声音，程序是改变不了系统的声音的，加入此demo才能避免此现象

        //初始化播放器 数据来源 开启底层分享接口
        loadPlayer(getIntent());
        EventBus.getDefault().register(this);
        super.onCreate(savedInstanceState);
    }


    //弹出右侧popupWindow
    @OnClick(R.id.menu)
    public void menu(View v){
        adapter.reData(rootPoint);
        right_popupWindow.showAsDropDown(menu);
    }


    // 初始化右侧菜单弹出框
    private void initPopupWindow(){
        View vv = View.inflate(this, R.layout.show_device_popup, null);
        //x.view().inject(this, vv);
        list= (ListView) vv.findViewById(R.id.listserver);
        swipeLayout= (SwipeRefreshLayout) vv.findViewById(R.id.swipe_refresh);
        right_popupWindow = new QMPopupWindow(vv);
    }


    //退出程序
    @OnClick(R.id.back)
    public void back(View v){
        if (isPlay){
            new QMDialog(this, R.string.close_share_tip, R.string.yes, new PositionListener(){
                @Override
                public void method(int position){
                    if (rootPoint.getdType() != -1) {//手机退出分享
                        backtoMainActivity(3);
                     }else{
                        //任盒退出分享
                        finish();
                    }
                }

                @Override
                public void cancel() {

                }
            });
        }else{
            finish();
        }
    }


    @OnClick(R.id.close)
     public void close(View v){//任盒退出分享---倒计时中直接关闭
        finish();
     }


    /**
     * 加载播放器
     *
     * @param intent
     */
    public void loadPlayer(Intent intent){
        getUrl(intent);//接受传值的一些对象
        DebugLog.showLog(this,"isShareScreen:"+isShareScreen);
        surface_layout.loadSurfaceView(this, maxWidth, maxHeight,isShareScreen,this);//初始化自定义LinearLayout的布局，将与底层交互的处理写在自定义中
        GlRenderNative.setPlayStatusEnable(new playstatus());//解码接口返回回调
    }


    /**
     * 接收mainActivity的传值
     *
     * @param intent
     */
    public void getUrl(Intent intent){
        this.rootPoint = (RootPoint) intent.getSerializableExtra("rootPoint");
        this.isShareScreen=intent.getBooleanExtra("shareScreen",false);
        play_fame.setText(Html.fromHtml(String.format(
                getString(R.string.play_fame), rootPoint.getName() + "")));
        text.setText(Html.fromHtml(String.format(
                getString(R.string.play_fame), rootPoint.getName() + "")));
        adapter.reData(rootPoint);
        pHandler = new Handler();
        DebugLog.showLog(this,"type:"+rootPoint.getdType());
        if(rootPoint.getdType()==1||rootPoint.getdType() == -1||rootPoint.getdType()==2||rootPoint.isPC()){//任盒横屏
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            maxWidth = dm.widthPixels;
            maxHeight = dm.heightPixels;
        }else{//手机竖屏
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            maxWidth = dm.widthPixels;
            maxHeight = dm.heightPixels;
          //  menu.setVisibility(View.GONE);
        }
    }


    public class playstatus{

        public void PlayStatus(int nTaskIndex, int status){
            surface_layout.onPlayState(status);
        }
    }



    /**
     * 停止自动亮度调节
     */

    public void stopAutoBrightness(Activity activity){
        Settings.System.putInt(activity.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
    }



    /**
     * 连续三次发送搜索的请求
     */
    private void type_SearchBox(){
        for (int i = 0; i < 3; i++){
            CommandExecutorLancher.getOnlyExecutor().searchMessageLancher(
                    QMCommander.TYPE_SEARCH);
            try {
                Thread.sleep(300);
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        loadPlayer(intent);//重新加载播放器 否则无法捕获流
    }

    /**
     * 初始化列表数据
     */
    private void initAdapter(){

        swipeLayout.setColorSchemeResources(android.R.color.holo_red_light,
                android.R.color.holo_green_light,
                android.R.color.holo_blue_bright,
                android.R.color.holo_orange_light);
        adapter = new ScreenShareAdapter(this, new BoxOnClickListener(){

            @Override
            public void onClick(RootPoint rootPoint){
                transmitOtherBox(rootPoint);
            }
        });//不能通过下标来确定点击事件，通过对象回调

        list.setAdapter(adapter);
        swipeLayout.setOnRefreshListener(new OnRefreshListener(){

            @Override
            public void onRefresh(){
                QMDevice.getInstance().setOffline();
                new Thread() {
                    public void run(){
                        type_SearchBox();
                    };
                }.start();

                swipeLayout.postDelayed(new Runnable(){

                    @Override
                    public void run(){
                        swipeLayout.setRefreshing(false);
                        QMDevice.getInstance().removeOffline();
                        adapter.reData(rootPoint);
                    }
                }, 5 * 1000);
            }
        });
    }

    /**
     * 点击左侧菜单的每一项
     *
     * @param
     */
    private void transmitOtherBox(final RootPoint rootPoint){

        if (rootPoint.getAddress().equals(this.rootPoint.getAddress())){
            QMUtil.getInstance().showToast2(this, getResources().getString(R.string.share) + "\t" + rootPoint.getName());

           }else{
            //判断盒子有没有设置连接密码，有密码的话
            if("true".equals(rootPoint.getEnablepwd())){
                passwdVertify(rootPoint);
            } //否则直接切换
            else {
                swicthBox(rootPoint);
            }
        }
    }

    /**
     * 切换分享其他的盒子
     *
     * @param rootPoint
     */
    public void swicthBox(RootPoint rootPoint){
        if (right_popupWindow != null)
            right_popupWindow.dismiss();
        this.rootPoint = rootPoint;
        adapter.reData(this.rootPoint);//刷新适配器并且更改指定选中盒子的状态

//        if(isPlay == true){
//            isPlay = false;
//            surface_layout.stopPlay();//之前的盒子正在播放要先停止点播
//        }

        surface_layout.removeAllCallbacks();

        if (rootPoint.getdType() == -1) {
            // 分享盒子l
            CommandExecutorBox.getOnlyExecutor().connectMessage(
                    QMCommander.TYPE_SET_CLIENT_MSG, rootPoint.getAddress(), null);
            CommandExecutorLancher.getOnlyExecutor().controlMessage(QMCommander.SCREEN_OPEN, rootPoint.getAddress());
        }
        // 向android设备发送分享指令
        else if (rootPoint.getdType() == 0||rootPoint.getdType()==1) {

            // 发出消息提示对方需要投屏
            CommandExectuorMobile.getOnlyExecutor().connectMessage(QMCommander.TYPE_NEED_SHARED, rootPoint.getAddress());
        } else {
            // 发出消息给pc提示对方需要投屏
            CommandExecutorLancher.getOnlyExecutor().connectPcMessage(QMCommander.TYPE_PC_SHARED, rootPoint.getAddress());
        }

        play_fame.setText(Html.fromHtml(String.format(
                getString(R.string.play_fame), rootPoint.getName() + "")));
        text.setText(Html.fromHtml(String.format(
                getString(R.string.play_fame), rootPoint.getName() + "")));
        show_Loading();
    }

    /**
     * 初始化系统声音
     */
    private void initVolume(){
        //获取系统的最大声音及当前声音
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        mMaxVolume = mAudioManager
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        //将当前声音默认设置为最大声音的50%
        if (mVolume < 8) {
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 8, 0);
            mVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            rightProgress = (int) (mVolume * 100 / mMaxVolume);
            vpb_right.setText("50%");
            vpb_right.invalidate();
        } else {
            rightProgress = (int) (mVolume * 100 / mMaxVolume);
            vpb_right.setText(rightProgress + "%");
            vpb_right.invalidate();
        }

       // Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 255);
    }



    /**
     * 盒子退出时app定时5秒内关闭
     */
    public void showClose(){

//        final Handler mHandler = new Handler(){
//            @Override
//            public void handleMessage(Message msg){
//
//                super.handleMessage(msg);
//                switch (msg.what){
//                    case 0:
//                        end_play.setText(Html.fromHtml(String.format(
//                                getString(R.string.end_play), count + "")));
//                        end_play.invalidate();
//                        break;
//                    case 1:
//                        finish();
//                        break;
//                }
//            }
//        };
//

    runOnUiThread(new Runnable(){
            @Override
            public void run(){
                root.bringChildToFront(show_close);
                show_close.setVisibility(View.VISIBLE);
                timer = new CountDownTimer(5 * 1000, 1 * 1000){

                    @Override
                    public void onTick(long millisUntilFinished){
                        end_play.setText(Html.fromHtml(String.format(
                                getString(R.string.end_play), millisUntilFinished/1000 + "")));
                        end_play.invalidate();

                    }

                    @Override
                    public void onFinish(){

                           finish();
                    }

                };

                timer.start();
            }
        });

//
//        new Thread(){
//            public void run(){
//                while (count >= 0){
//                        mHandler.sendEmptyMessage(0);
//                    if (count == 0) {
//                        mHandler.sendEmptyMessage(1);
//                    }
//                    try {
//                        Thread.sleep(1000);
//                    }catch (InterruptedException e){
//                        e.printStackTrace();
//                    }
//                    count--;
//                }
//            };
//        }.start();
    }

    /**
     * @param palyError 0表示视频不能播放 1表示取消盒子播放 2表示不能连接盒子 3表示手机间的退出
     *
     */

    public void backtoMainActivity(int palyError){
        Intent intent = new Intent();
        intent.putExtra("palyError", palyError);
        Bundle bundle = new Bundle();
        bundle.putSerializable("rootPoint", getRootPoint());
        intent.putExtras(bundle);
        setResult(code2, intent);
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    /**
     * 加载美团的动画
     */

    public void show_Loading(){
        show_loading.setVisibility(View.VISIBLE);
        animation = AnimationsContainer.getInstance(R.array.loading_anim, 20,this).createProgressDialogAnim(loadingImage);
//        loadingImage.setBackgroundResource(R.drawable.loading);
//        animation = (AnimationDrawable) loadingImage.getBackground();
        animation.start();

//		loadingIv.setBackgroundResource(R.anim.frame);
      //  Glide.with(this).load(R.drawable.loading).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).centerCrop().into(loadingImage);
        // 通过ImageView对象拿到背景显示的AnimationDrawable
//		final AnimationDrawable mAnimation = (AnimationDrawable) loadingIv

//				.getBackground();
//		// 为了防止在onCreate方法中只显示第一帧的解决方案之一
//		loadingIv.postDelayed(new Runnable() {
//			@Override
//			public void run() {
//				mAnimation.start();
//			}
//		}, 500);
    }

    /**
     * 点播成功后隐藏美团动画 设置播放成功
     */
    public void playImg(){
        setPlay(true);
        stopAnimation();
        show_loading.setVisibility(View.GONE);
        sendCoverScreen();
    }

    /**
     * 盒子退出了，也没有收到3000消息，app这边自动判断断流检测有没有再次连接，没有直接弹出分享结束的提示框
     */
    public void showClosePD(){
        if(isPlay){
            showClose();
        }
    }



    /**
     * 退回键，分享成功，弹出退出提示框，正在分享请求中退出直接退出
     */
    public boolean onKeyDown(int keyCode, KeyEvent OnClick){
        if (OnClick.getKeyCode() == KeyEvent.KEYCODE_BACK){
            if (isPlay){
                exitAPP();//正在分享的过程中弹出退出分享的提示框
            }else{
                finish();
            }
        }
        return super.onKeyDown(keyCode, OnClick);
    }

    /**
     * 退出的提示框布局
     */
    private void exitAPP(){
        new QMDialog(this, R.string.close_share_tip,
                R.string.close_share, rootPoint.getdType(), new PositionListener(){
            @Override
            public void method(int position){
                if (rootPoint.getdType() != -1) {
                    backtoMainActivity(3);
                } else {
                    finish();
                }
            }

            @Override
            public void cancel() {

            }
        });
    }

    /**
     * 分享成功后退出要停止点播
     */
    @Override
    protected void onStop(){
        super.onStop();
        DebugLog.showLog(this,"onStop");
        if (surface_layout.isLocalPlay()){
            DebugLog.showLog(this,"----停止了");
            surface_layout.stopPlay(false);
        }
        stopAnimation();
        if(timer!=null){
            timer.cancel();
            timer=null;
        }
    }

    private void stopAnimation(){

        if(animation!=null&&animation.ismShouldRun()){
            DebugLog.showLog(this,"animation停止了");
            animation.stop();
           // show_loading.setBackgroundResource(0);
        }

    }


    @Override
    protected void onResume(){
        super.onResume();
        // MobclickAgent.onResume(this);
        CommandExecutorBox.getOnlyExecutor().setListener(this);//监听任盒的断流
//        if(rootPoint!=null&&rootPoint.isPC()){
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//        }

     //  if (rootPoint.getdType() != -1)
            CommandExectuorMobile.getOnlyExecutor().setListener(this);//监听手机停止投屏
     //  else
            CommandExecutorLancher.getOnlyExecutor().setListener(this);//监听任盒停止投屏
            surface_layout.startPlay(rootPoint);//请求底层分享的方法
    }


    @Override
    protected void onPause(){
        super.onPause();
        CommandExecutorBox.getOnlyExecutor().setListener(null);
        CommandExectuorMobile.getOnlyExecutor().setListener(null);
        CommandExecutorLancher.getOnlyExecutor().setListener(null);
    }

    protected void onDestroy(){
        super.onDestroy();
        stopSharescreenDevice();
        DebugLog.showLog(this,"onDestroy");
        surface_layout.removeAllCallbacks();
        shareRoots.clear();
        isShareScreen=false;
        rootPoint.setPC(false);
        EventBus.getDefault().unregister(this);
    }


    @Subscribe
    public void onEventMainThread(HeatbeatEvent event){
        DebugLog.showLog(this,"收到心跳超时的回调...");
        String address =event.getResult();
        if(address!=null){
            RootPoint rp = QMDevice.getInstance().getSameRootPoint(address);
           if(rp==null){
               return;
           }else if(rp.getdType()==2){
               showClose();
           }

        }

    }


    /**
     * 判断手机还是平板
     *
     * @return
     */
    public boolean isPad(){
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display dis = wm.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        dis.getMetrics(dm);
        double x = Math.pow(dm.widthPixels / dm.xdpi, 2);
        double y = Math.pow(dm.heightPixels / dm.ydpi, 2);
        double z = Math.sqrt(x + y);
        if (z >= 6.0)
            return true;
        else
            return false;
    }

    //===============================调节系统声音亮度====================================

    /**
     * 结束手势,触摸屏幕抬起时触发的事件
     */
    private void endGesture(){
        mVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        // 隐藏
        mHandler.removeMessages(0);
        mHandler.sendEmptyMessageDelayed(0, 500);
        isFisrt = true;
    }

    /**
     * 定时隐藏
     */
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            light_bg.setVisibility(View.GONE);//隐藏显示亮度的布局
            show_audio.setVisibility(View.GONE);//隐藏显示声音的布局
        }
    };

    /**
     * 滑动改变系统亮度
     *
     * @param percent
     */
    private void onBrightnessSlide2(float percent){
        try {
            mBrightness = Settings.System.getInt(getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mBrightness < 0) {
            mBrightness = 0;
        }

        int brightness = (int) (mBrightness + percent * 255);
        if (brightness > 255) {
            brightness = 255;
        } else if (brightness < 0) {
            brightness = 0;
        }
        Settings.System.putInt(getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS, brightness);

        light_count.setText((int) (brightness * 100 / 255) + "%");
        light_bg.setVisibility(View.VISIBLE);
    }

    /**
     * 声音增减
     *
     * @param OnClick
     */
    private void verticalMove(MotionEvent OnClick){
        boolean dir2 = true;
        if (OnClick.getY() >= mOldY2) {
            mOldY2 = OnClick.getY();

            onVolumeSlide((mOldY - OnClick.getY()) / (maxHeight * 10));
            dir2 = true;
        } else {
            mOldY2 = OnClick.getY();
            onVolumeSlide((mOldY - OnClick.getY()) / (maxHeight * 10));
            dir2 = false;
        }
        if (dir3 != dir2) {
            mOldY = mOldY2;
            dir3 = !dir3;
        }

    }

    /**
     * 屏幕亮暗
     *
     * @param OnClick
     */
    private void horizontalMove(MotionEvent OnClick){
        boolean dir2 = true;
        if (OnClick.getY() >= mOldY2) {
            mOldY2 = OnClick.getY();
            onBrightnessSlide2((mOldY - OnClick.getY()) / (maxHeight * 10));
            dir2 = true;
        } else {
            mOldY2 = OnClick.getY();
            onBrightnessSlide2((mOldY - OnClick.getY()) / (maxHeight * 10));
            dir2 = false;
        }
        if (dir1 != dir2) {
            mOldY = mOldY2;
            dir1 = !dir1;
        }
    }

    /**
     * 滑动改变声音大小
     *
     * @param percent
     */
    boolean isFisrt = true;

    private void onVolumeSlide(float percent) {
        if (isFisrt) {
            mVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            if (mVolume < 0)
                mVolume = 0;
            rightProgress = (int) (mVolume * 100 / mMaxVolume);
            isFisrt = false;
        }

        int progress = rightProgress + (int) (percent * 100);
        if (progress > 100) {
            progress = 100;
        } else if (progress < 0) {
            progress = 0;
        }

        int index = 0;

        index = (int) (progress * 0.15);
        // 变更声音
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);

        vpb_right.setText(progress + "%");
        rightProgress = progress;
        show_audio.setVisibility(View.VISIBLE);
    }

    //触摸屏幕按下的触发事件
    @Override
    public void down(MotionEvent OnClick) {
        mOldY = OnClick.getY();
        mOldY2 = OnClick.getY();
    }

    //触摸屏幕抬起时的触发事件
    @Override
    public void up(MotionEvent OnClick) {
        endGesture();
        show2 = false;
    }

    //触摸屏幕移动时的触发事件
    @Override
    public void move(MotionEvent OnClick){
        if (show2){
            return;
        }
        if (OnClick.getX() < 1 * maxWidth / 2){
            if (mOldY - OnClick.getY() < maxHeight / 10
                    && OnClick.getY() - mOldY < maxHeight / 10){
                return;
            }
            horizontalMove(OnClick);
        } else if (OnClick.getX() >= 1 * maxWidth / 2){
            if (mOldY - OnClick.getY() < maxHeight / 10
                    && OnClick.getY() - mOldY < maxHeight / 10) {
                return;
            }
            verticalMove(OnClick);
        }
    }

    /**
     * 显示头部导航条
     */
    @Override
    public void showHead(int i){
        switch (i){
            case 0:
                if (show_head.getVisibility() == View.VISIBLE)
                    show_head.setVisibility(View.GONE);
                else
                    show_head.setVisibility(View.VISIBLE);
                break;
            case 1:
                show_head.setVisibility(View.GONE);
                break;
        }
    }

    //========================================监听指令===============================================
    //20001端口监听，与盒子交互的，断流时会发送连接的请求，在这接收到后就进行点播
    @Override
    public void connectBox(RootPoint rootPoint){
        play_fame.setText(Html.fromHtml(String.format(
                getString(R.string.play_fame), rootPoint.getName() + "")));
        text.setText(Html.fromHtml(String.format(
                getString(R.string.play_fame), rootPoint.getName() + "")));
        playBox(rootPoint);
    }

    /**
     * 断流再次点播成功后解码端发送过来的的2002消息，
     */
    @Override
    public void playBox(RootPoint rootPoint){
        //在此界面收到消息才跳转
        if (QMUtil.getInstance().isForeground(this, "ScreenShareActivity")){
            if(rootPoint!=null){
                Intent intent = new Intent(this, ScreenShareActivity.class);
                intent.putExtra("rootPoint", rootPoint);
                startActivity(intent);
            }

        }

    }

    //盒子退出的时候收到3000的响应，注意能收到3000的响应是次盒子投屏过了的，在盒子左侧列表有显示的
    @Override
    public void exitBox(RootPoint rootPoint){
        //收到3000盒子退出的消息直接弹出分享结束的提示框
        if (this.rootPoint.getAddress().equals(rootPoint.getAddress()))
            showClose();
    }

    @Override
    public void heartBeatBox(RootPoint rootPoint){


    }


    @Override
    public void screenSuccessBox(RootPoint rootPoint){


    }

    @Override
    public void screenCoverBox(RootPoint rootPoint){
        if (this.rootPoint.getAddress().equals(rootPoint.getAddress())){
            Toast.makeText(this,getResources().getString(R.string.tp_cut), Toast.LENGTH_SHORT).show();
            showClose();
           }

    }


    //====================手机=======================
    @Override
    public void searchMobile(RootPoint rootPoint){


    }

    @Override
    public void screenMobile(RootPoint rootPoint){

        DebugLog.showLog(this,"信令:"+rootPoint.getCategory());
        if(rootPoint.getCategory()==QMCommander.TYPE_NEED_SHARED){
            if(isPlay()){
                //接受分享指令
                if(getResources().getConfiguration().orientation== Configuration.ORIENTATION_LANDSCAPE){
                    //pc端的分享要横屏
                    CommandExectuorMobile.getOnlyExecutor().connectMessage(QMCommander.TYPE_SCREEN_M, rootPoint.getAddress(),true);
                }else{
                    CommandExectuorMobile.getOnlyExecutor().connectMessage(QMCommander.TYPE_SCREEN_M, rootPoint.getAddress());
                }

            }else{
                CommandExectuorMobile.getOnlyExecutor().sendconnectMessage(QMCommander.TYPE_NO_SCREEN_M, rootPoint.getAddress());
            }
            return;
        }

        if(rootPoint.getCategory()==QMCommander.TYPE_SCREEN_SCREEN_SUCCESS&&rootPoint.isResponse()){
            DebugLog.showLog(this,"分享成功...");
            RootPoint rp = QMDevice.getInstance().getSameRootPoint(rootPoint);
            DebugLog.showLog(this,"name:"+rp.getName());
            addSharepoint(rp);
            if(rootPoint.getdType()==1||rootPoint.getdType()==0){
                CommandExectuorMobile.getOnlyExecutor().connectMessage(QMCommander.TYPE_SCREEN_REQUEST_FRAME, this.rootPoint.getAddress());
            }
            return;
        }


        if(rootPoint.getCategory()==QMCommander.TYPE_SCREEN_M&&(this.rootPoint.getdType() == -1||this.isShareScreen)){//正在分享任盒收到手机的投屏指令
            //---发送消息给投屏的手机
            CommandExectuorMobile.getOnlyExecutor().sendconnectMessage(QMCommander.TYPE_FORBIDDEN_M, rootPoint.getAddress());
            return;
        }

        //当处于分享界面的时候收到这个消息表明是被其他手机投屏覆盖--需要通知之前的投屏手机
        if(this.rootPoint.getdType()==1||this.rootPoint.getdType()==0){
            CommandExectuorMobile.getOnlyExecutor().connectMessage(QMCommander.TYPE_COVER_M, this.rootPoint.getAddress());
        }else if(this.rootPoint.getdType()==2){
            CommandExecutorLancher.getOnlyExecutor().connectPcMessage(QMCommander.TYPE_PC_STOP,this.rootPoint.getAddress());
        }

       // surface_layout.stopPlay();

        if(pHandler!=null){
            pHandler.removeCallbacks(run);
        }

        Intent intent = new Intent(this, ScreenShareActivity.class);
        intent.putExtra("rootPoint", rootPoint);
        startActivity(intent);
    }

    /**
     * @param rp
     * 添加分享列表
     */
    private void addSharepoint(RootPoint rp){
        for (int i = 0; i < shareRoots.size(); i++) {
            if(shareRoots.get(i).getAddress().equals(rp.getAddress())){
                return;
            }
        }
        shareRoots.add(rp);
    }

    private void sendCoverScreen(){
        for (RootPoint shareRoot : shareRoots){

            if(shareRoot.getdType()==1||shareRoot.getdType()==0){

                CommandExectuorMobile.getOnlyExecutor().connectMessage(QMCommander.TYPE_COVER_SCREEN_SHARE, shareRoot.getAddress());

            }else if(shareRoot.getdType()==2){

                CommandExectuorMobile.getOnlyExecutor().connectMessage(QMCommander.TYPE_COVER_SHARED_PC, shareRoot.getAddress());

            }
        }
    }


    @Override
    public void screenInterruptMobile(RootPoint rootPoint){

        DebugLog.showLog(this,"断开的信令："+rootPoint.getCategory());
        RootPoint rp= QMDevice.getInstance().getSameRootPoint(rootPoint);
        //在这里是接收到了停止投屏的指令，将当前的界面给finish掉
        if(shareRoots.contains(rp)){
            removeSharePoint(rootPoint);
            return;
        }

        if(rootPoint.getCategory() == QMCommander.TYPE_STOP_M && rootPoint.getAddress().equals(this.rootPoint.getAddress())){
                    finish();
        }

        if(rootPoint.getCategory()==QMCommander.TYPE_STOP_SHARE_SCREEN){

                     if(!isClose){
                         isClose=true;
                         showClose();
                         //finish();
                     }
        }

//        if(rootPoint.getCategory()==QMCommander.TYPE_SHARE_STOP_M){
//            RootPoint rp = QMDevice.getInstance().getSameRootPoint(rootPoint);
//            removeSharePoint(rp);
//        }
    }

    private void removeSharePoint(RootPoint rp){
        for (int i = 0; i < shareRoots.size(); i++) {
            if(shareRoots.get(i).getAddress().equals(rp.getAddress())){
                shareRoots.remove(i);
                break;
            }
        }
    }

    @Override
    public void heartBeatMobile(RootPoint rootPoint){
        //忽略掉收到自己发送的广播
        if (rootPoint.getAddress().equals(NetworkUtils.getNetworkIP()))
            return;
        //在这里进行了连接的判断
        if (rootPoint.isClient()){
            CommandExectuorMobile.getOnlyExecutor().sendconnectMessage(QMCommander.TYPE_HEARTBEAT_M, rootPoint.getAddress());
            pHandler.removeCallbacks(run);
            pHandler.postDelayed(run, 8000);
        }
    }

    /**
     * @param mRootPoint
     * 切换视频源请求重新点播
     */
    @Override
    public void swichMobileScreen(RootPoint mRootPoint){

        Toast.makeText(this,"切换视频源播放!", Toast.LENGTH_LONG).show();
        RootPoint rootPoint=  QMDevice.getInstance().getSameRootPoint(mRootPoint);
        // surface_layout.stopPlay();
        if(pHandler!=null){
            pHandler.removeCallbacks(run);
        }
       // rootPoint.setShareScreen(true);
        Intent intent = new Intent(this, ScreenShareActivity.class);
        intent.putExtra("rootPoint", rootPoint);
        intent.putExtra("shareScreen",true);
        startActivity(intent);
    }

    @Override
    public void requestScreenFrame(){

    }

    private Runnable run = new Runnable(){
        @Override
         public void run(){
            DebugLog.showLog(this,"对方没发心跳，停止播放..");
            showClose();
        }
    };

    //=======================================任盒=============================================
    @Override
    public void searchLancher(RootPoint rootPoint){
    if(!Collocation.getCollocation().getIsScan()){
        //任盒设备
        if (rootPoint.getAddress().equals(NetworkUtils.getNetworkIP()))
            return;//过滤自己收到的自己发送的搜索指令
            addCollotion(rootPoint);// 添加到本类集合中
        }
    }

    /**
     * 添加到本类集合中
     *
     * @param rootPoint
     */
    public void addCollotion(RootPoint rootPoint){
        QMDevice.getInstance().add(rootPoint);

    }

    @Override
    public void controlLancher(RootPoint rootPoint){


    }

    @Override
    public void screenOpenLancher(RootPoint rootPoint){


    }

    @Override
    public void passwdAlterLancher(final RootPoint rootPoint){
        QMDevice.getInstance().oprationSameDevice(rootPoint, new BoxOnClickListener(){
            @Override
            public void onClick(RootPoint rp){
                rp.setEnablepwd(rootPoint.getEnablepwd());
                Collocation.getCollocation().savePassWd(rp.getUuid(), "");
            }
        });
    }

    @Override
    public void controlHeartBeatLancher(RootPoint rootPoint) {

    }

    @Override
    public void passwdVertifyCallBack(RootPoint rp){
             swicthBox(rp);
    }

    @Override
    public void touPingPc(RootPoint rp){
        // TODO Auto-generated method stub
        if(rp.getCategory()==QMCommander.TYPE_SUCCESS_PC){

            DebugLog.showLog(this,"收到PC点播成功消息");
            RootPoint rootPoint = QMDevice.getInstance().getSameRootPoint(rp);
            addSharepoint(rootPoint);

        }
    }

    @Override
    public void stopPc(RootPoint rp){
        // TODO Auto-generated method stub
        DebugLog.showLog(this,"stopPc信令:"+rp.getCategory());
        RootPoint rootPoint=QMDevice.getInstance().getSameRootPoint(rp);
        if(shareRoots.contains(rootPoint)){
            removeSharePoint(rp);
            return;
        }
        finish();
    }


    @Override
    public void pcTouPing(RootPoint rp){

        if(QMCommander.TYPE_SCREEN_REFUSED == rp.getCategory()){

            return;
        }

        DebugLog.showLog(this,"rootPoint type:"+rootPoint.getdType()+"isShareScreen :"+isShareScreen);

        if((isShareScreen||this.rootPoint.getdType()==-1)&&QMCommander.TYPE_PCTP==rp.getCategory()){
            CommandExecutorLancher.getOnlyExecutor().connectPcMessage(QMCommander.TYPE_SCREEN_REFUSED,rp.getAddress());
            //如果分享别人就拒绝投屏
            return;
        }

        DebugLog.showLog(this,"pcTouPing信令:"+rp.getCategory());
        if(rp.getCategory()==QMCommander.TYPE_PCTP){
            // 收到投屏601，覆盖点播
         //   surface_layout.stopPlay();

            rp.setPC(true);

            if(pHandler!=null){
                pHandler.removeCallbacks(run);
            }
            coverStopTp(this.rootPoint);
            Intent intent = new Intent(this, ScreenShareActivity.class);
            rp.setdType(2);
            intent.putExtra("rootPoint", rp);
            startActivity(intent);

        }else if(rp.getCategory()==QMCommander.TYPE_PC_STOP){

            RootPoint rootPoint=QMDevice.getInstance().getSameRootPoint(rp);
            if(shareRoots.contains(rootPoint)){
                removeSharePoint(rp);
                return;
            }

            finish();

        }else if(rp.getCategory()==QMCommander.TYPE_PC_SHARED){

            DebugLog.showLog(this,"接受到PC分享指令");

            if(isPlay()){
                if(this.rootPoint.getdType()==1||this.rootPoint.getdType()==0){
                    CommandExecutorLancher.getOnlyExecutor().connectPcMessage(QMCommander.TYPE_PCTP,rp.getAddress(),1,false);
                }else{
                    CommandExecutorLancher.getOnlyExecutor().connectPcMessage(QMCommander.TYPE_PCTP,rp.getAddress(),1,true);

                }
            }
        }
    }


    @Override
    public void pcCoverShare(RootPoint rp){

        Toast.makeText(this,"切换视频源播放!", Toast.LENGTH_LONG).show();
        // RootPoint rootPoint=  QMDevice.getInstance().getSameRootPoint(rp);
        // rootPoint.setPC(true);
        rootPoint.setPC(true);
        rootPoint.setPlayurl(rp.getPlayurl());
        rootPoint.setChannel(rp.getChannel());

      //  surface_layout.stopPlay();

        if(pHandler!=null){
            pHandler.removeCallbacks(run);
        }
        // rootPoint.setShareScreen(true);
        Intent intent = new Intent(this, ScreenShareActivity.class);
        intent.putExtra("rootPoint", rootPoint);
        intent.putExtra("shareScreen",true);
        startActivity(intent);
    }


    private void stopSharescreenDevice(){
      //  ArrayList<RootPoint> devices= QMDevice.getInstance().getAl();
        for (RootPoint device : shareRoots){
           // if(device.isShareScreen()){
                //device.setShareScreen(false);
            for (int i = 0; i < 3; i++) {
                DebugLog.showLog(this,"device:"+device.getName()+"type:"+device.getdType());
                if(device.getdType() == 0||device.getdType()==1){
                    // 手机间取消投屏指令 只有其在投屏的状态的时候才能够发送停止的指令
                    DebugLog.showLog(this,"发给手机停止...");
                    CommandExectuorMobile.getOnlyExecutor().connectMessage(QMCommander.TYPE_STOP_SHARE_SCREEN, device.getAddress());
                }else if (device.getdType() == 2){
                    CommandExecutorLancher.getOnlyExecutor().connectPcMessage(QMCommander.TYPE_PC_STOP, device.getAddress());
                }
                else if (device.getdType() == -1){
                    device.setStopByHandle(false);
                    // 向任盒 发送停止投屏的指令，没有返回接口
                    CommandExecutorBox.getOnlyExecutor().connectMessage(QMCommander.TYPE_STOP_PLAY, device.getAddress(), null);
                }

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
           // }
        }
    }



    public void onWindowFocusChanged(boolean hasFocus){
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && Build.VERSION.SDK_INT >= 19){
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }


    @Override
    public void LoadingAnimate(){
        runOnUiThread(new Runnable(){
            @Override
            public void run(){
                show_Loading();//加载动画
            }
        });
    }

    @Override
    public void playSuccess(){

        playImg();

        RootPoint rp=  QMDevice.getInstance().getSameRootPoint(this.rootPoint);

        if(rp!=null){
            rp.setShareing(true);
        }

        if(getRootPoint().isClient()&&getRootPoint().getdType()!=-1){
            //让相关联的设备进行分享操作
            if(isShareScreen){
                CommandExectuorMobile.getOnlyExecutor().sendconnectMessage(QMCommander.TYPE_SCREEN_SCREEN_SUCCESS, getRootPoint().getAddress());
            }else{
                CommandExectuorMobile.getOnlyExecutor().sendconnectMessage(QMCommander.TYPE_SCREEN_M, getRootPoint().getAddress());
            }
         }

        DebugLog.showLog(this,"getRootPoint type :"+getRootPoint().getdType());
        if(getRootPoint().getdType()==2){
            DebugLog.showLog(this,"address:"+rootPoint.getAddress());
            CommandExecutorLancher.getOnlyExecutor().connectPcMessage(QMCommander.TYPE_SUCCESS_PC, rootPoint.getAddress());
        }

//        if(getRootPoint().getdType()==-1){
//            CommandExecutorBox.getOnlyExecutor().connectMessage(
//                    QMCommander.TYPE_HEARTBEAT, getRootPoint().getAddress(), null);
//            //收到分享盒子成功，发送心跳100
//        }

    }



    @Override
    public void playError(boolean isPlay, String host){
        setPlay(isPlay);
        CommandExecutorBox.getOnlyExecutor().connectMessage(
                QMCommander.TYPE_SET_CLIENT_MSG, host, null);
    }

    @Override
    public void InterruptTimeOut(){
        showClosePD();
    }

    @Override
    public void backToMainActivity(int state){
          backtoMainActivity(state);
    }

    @Override
    public void showCloseActivity(){

        showClose();
    }

    private void coverStopTp(RootPoint rootPoint){

         if(rootPoint.getdType()==1||rootPoint.getdType()==0){
             CommandExectuorMobile.getOnlyExecutor().sendconnectMessage(QMCommander.TYPE_COVER_M, rootPoint.getAddress());
         }else if(rootPoint.getdType()==2){
             CommandExecutorLancher.getOnlyExecutor().connectPcMessage(QMCommander.TYPE_PC_STOP,rootPoint.getAddress());
         }
    }



}
