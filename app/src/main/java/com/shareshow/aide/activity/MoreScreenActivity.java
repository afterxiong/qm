package com.shareshow.aide.activity;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.shareshow.App;
import com.shareshow.aide.R;
import com.shareshow.aide.dialog.InputNameDialog;
import com.shareshow.aide.event.NetworkEvent;
import com.shareshow.aide.event.ScanEvent;
import com.shareshow.aide.mvp.presenter.ScreenPresenter;
import com.shareshow.aide.mvp.view.ScreenView;
import com.shareshow.aide.util.OpenFileUtil;
import com.shareshow.aide.util.uuid.CacheHelper;
import com.shareshow.aide.util.uuid.Devices;
import com.shareshow.airpc.Collocation;
import com.shareshow.airpc.QMCommander;
import com.shareshow.airpc.activity.ControlActivity;
import com.shareshow.airpc.activity.HeartBeatServer;
import com.shareshow.airpc.activity.QuiteFileActivity;
import com.shareshow.airpc.activity.RemouteFileShareActivity;
import com.shareshow.airpc.activity.ScreenShareActivity;
import com.shareshow.airpc.adapter.DeviceItemAdapter;
import com.shareshow.airpc.adapter.MainAdapter;
import com.shareshow.airpc.float_window.HomeWatcher;
import com.shareshow.airpc.ftpserver.FTPServerService;
import com.shareshow.airpc.model.QMLocalFile;
import com.shareshow.airpc.model.RootPoint;
import com.shareshow.airpc.ports.BoxOnClickListener;
import com.shareshow.airpc.ports.ConnectFTPListener;
import com.shareshow.airpc.ports.PassWDCallBack;
import com.shareshow.airpc.ports.PositionListener;
import com.shareshow.airpc.record.RecodListener;
import com.shareshow.airpc.record.RecordManager;
import com.shareshow.airpc.share.FileDialog;
import com.shareshow.airpc.share.FileRecieveAdapter;
import com.shareshow.airpc.socket.command.CommandExectuorMobile;
import com.shareshow.airpc.socket.command.CommandExecutorBox;
import com.shareshow.airpc.socket.command.CommandExecutorLancher;
import com.shareshow.airpc.socket.command.CommandExecutorLancherFile;
import com.shareshow.airpc.socket.command.CommandListenerBox;
import com.shareshow.airpc.socket.command.CommandListenerLancher;
import com.shareshow.airpc.socket.command.CommandListenerMobile;
import com.shareshow.airpc.socket.common.PassWDValidate;
import com.shareshow.airpc.socket.common.QMDevice;
import com.shareshow.airpc.util.DebugLog;
import com.shareshow.airpc.util.HairUtils;
import com.shareshow.airpc.util.NetworkUtils;
import com.shareshow.airpc.util.QMDialog;
import com.shareshow.airpc.util.QMFileType;
import com.shareshow.airpc.util.QMUtil;
import com.shareshow.airpc.widget.MoreScreenPopupWindow;
import com.shareshow.airpc.widget.MoreSettingPopupWindow;
import com.socks.library.KLog;
import com.umeng.analytics.MobclickAgent;
import com.xcg.ScanActivity;
import com.xcg.helper.EventNetworkReceiver;
import com.xcg.helper.EventString;
import com.xcg.helper.RequestNetworkInfo;
import com.xcg.helper.SampleWifiManager;
import com.xcg.helper.WifiParams;
import com.xcg.qrcode.EncodQrCode;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by FENGYANG on 2018/3/16.
 */

public class MoreScreenActivity extends BaseActivity<ScreenView,ScreenPresenter> implements ScreenView, View.OnClickListener, CommandListenerLancher, CommandListenerBox, CommandListenerMobile, SwipeRefreshLayout.OnRefreshListener,FileRecieveAdapter.OnClickListener,MainAdapter.MainOnClickListener, InputNameDialog.OnDialogDismissListener ,AbsListView.OnScrollListener,DeviceItemAdapter.MainOnClickListener, RecodListener {

    private ScreenPresenter screenPresenter;
    private Context mContext;
    private MediaProjectionManager mMediaProjectionManager;
    private LinearLayout screenCast;
    private Bitmap bitmap;
    public static LinearLayout show_search_error;// 没有搜索到盒子的时候显示的布局提示
    public static TextView nearby_nobox;// 没有搜索到盒子的时候显示的布局提示
    private SwipeRefreshLayout swipe_refresh;
    private ListView list;
    public  static DeviceItemAdapter<RootPoint> boxAdapter;// 适配器
    public static final String ACTION_BUTTON = "com.shareshow.aide.activity.MoreScreenActivity";// 通知栏点击停止发送的广播
    private RemoteViews mRemoteViews = null;// 通知栏
    private Notification notify = null;
    private NotificationManager nm = null;
    private Handler mHandler;
    public static boolean endStop;// 是否执行了停止投屏的功能,若为true的话，接收到20001端口发送来的200数据不做提示处理
    public static int TorS = 0;// 检测屏幕投屏后是执行投屏还是分享的 1 投屏 2 分享
    private String pass_input = "";// 输入密码
    private double back_pressed;// 点击两次退出
    private int refreshOnline = 0;// 进行下拉刷新时，0表示可以设置所有盒子不在线状态，1表示可以执行10秒内判断盒子在不在线的方法
    // 2表示前两者均不能执行,
    private boolean shareResponse = false;// 请求分享或控制是否得到响应了
    private ActivityManager am = null;
    private String uuid = "";// 手机唯一标示
    private QMDialog myDialog;// 对话框
    public static final int code1 = 223;// Activity回调
    public static final int REQUEST_CODE_QR = 0x001;
    public static final int SETTING_CODE = 0x0030;
    private static final int NO_NET_STATE = 0X001;
    private static final int PUBLIC_STATE = 0X002;
    private static final int PRIVATE_STATE = 0X003;
    private static final int REQUEST_CODE_CAPTURE_PERM = 0X010;
    private static final int WIFI_RESULT_CODE = 0x00002;
    private MoreSettingPopupWindow moreSetting;// 弹出右上角的菜单
    private HomeWatcher homeWatcher;
    private Intent intentService;
    private boolean isFirstSearch;
    private WifiParams scanParms;
    private RequestNetworkInfo request;
    private ArrayList<QMLocalFile> downLoadFiles;
    private ImageView isFileOpen;
    private FileDialog filedialog;
    private boolean isOpeanfile;
    private FileRecieveAdapter adapter;
    private boolean isSameDevice;
    private boolean isScanFinish;
    public final static String LOCK_LINK = "";
    // private int scanCount;
    private boolean isResume;
    private ImageView mode_img;
    private boolean isScreenShare;
    private MoreScreenPopupWindow pop;
    private MoreScreenActivity.FileBroadCast fileBroadCast;
    String name;
    String password;

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            switch (msg.what){
                case 0x001:
                    RootPoint point = (RootPoint) msg.obj;
                    ArrayList<QMLocalFile> downLoadFiles = (ArrayList<QMLocalFile>) msg.getData().getSerializable("down");
                    if (point != null) {
                        if (point.getFiles() == null) {
                            point.setFiles(new ArrayList<QMLocalFile>());
                        }
                        for (QMLocalFile downLoadFile : downLoadFiles) {
                            point.getFiles().add(0, downLoadFile);
                        }

                        if (Collocation.getCollocation().getFileOpen() && point != null && point.getFiles() != null && point.getFiles().size() != 0) {
                            openFile(point.getFiles(), 0);
                        } else {
                            if (point.getFiles() != null && point.getFiles().size() != 0) {
                                showSendFileDalog(point.getName(), point.getFiles());
                            }
                            boxAdapter.notifyDataSetChanged();
                        }
                    }

                    //}
                    break;
            }
        }
    };
    private Toolbar toolbar;
    private Intent heartServer;
    private TextView screenState;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_morescreen);
        mContext=MoreScreenActivity.this;
        init();
        initToolbar();
    }

    public void initToolbar() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    @Override
    public Toolbar getToolbar(){

        return toolbar;
    }

    @Override
    public ScreenPresenter createPresenter(){

        return new ScreenPresenter(this);
    }
    

    @Override
    public void startRecod(){
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            mMediaProjectionManager = (MediaProjectionManager)mContext.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
            Intent permissionIntent = mMediaProjectionManager.createScreenCaptureIntent();
            startActivityForResult(permissionIntent, REQUEST_CODE_CAPTURE_PERM);
        }
    }

    public void init(){
        initView();
        initEvent();// 设置控件的点击事件
        initToolbar();
        initData();// 初始化数据
        startTimerServer();// 启动后台服务
        startFTPService();
        initQrcode();
        initOtherCast();// 点击通知栏停止投屏的监听事件
        initFileBroadCast();
        DebugLog.showLog(this, "onCreate");
    }

    private void initFileBroadCast(){
        fileBroadCast = new MoreScreenActivity.FileBroadCast();
        IntentFilter filter = new IntentFilter(FTPServerService.FILE_SEND_FINISH);
        mContext.registerReceiver(fileBroadCast, filter);
    }

    private void initQrcode(){
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
        request = new RequestNetworkInfo(mContext);
        // NetWorkManager.getManager().registerReceiver(mContext);
    }

    public void initView(){
        toolbar = findViewById(R.id.toolbar);
        ScaleAnimation scaleAnimation = new ScaleAnimation(1, 0.5f, 1, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(2000);
        show_search_error = (LinearLayout) findViewById(R.id.show_search_error);
        nearby_nobox = (TextView) findViewById(R.id.nearby_nobox);
        mode_img = (ImageView)findViewById(R.id.mode_img);
        if (!Collocation.getCollocation().getIsScan()) {
            nearby_nobox.setText(getResources().getString(R.string.nearby_nobox));
            setMode(PUBLIC_STATE);
        } else {
            nearby_nobox.setText(getResources().getString(R.string.seach_safe_mode));
            setMode(PRIVATE_STATE);
        }
        swipe_refresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        list = (ListView) findViewById(R.id.list);
        //LinearLayout screen_control =(LinearLayout)findViewById(R.id.screen_control);
        screenCast = findViewById(R.id.screen_cast);
        screenState = findViewById(R.id.screen_cast_state);
        LinearLayout screen_file=findViewById(R.id.screen_file);

        //screen_control.setOnClickListener(this);
        screenCast.setOnClickListener(this);
        screen_file.setOnClickListener(this);
//        LinearLayout screenScan =findViewById(R.id.screen_scan);
//        screenScan.setOnClickListener(this);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                          onItem(position);
            }
        });
    }

    private void setMode(int state){
        switch (state){
            case NO_NET_STATE:
                mode_img.setImageDrawable(getResources().getDrawable(R.mipmap.no_net));
                break;

            case PUBLIC_STATE:
                mode_img.setImageDrawable(getResources().getDrawable(R.mipmap.no_net_1));
                break;

            case PRIVATE_STATE:
                mode_img.setImageDrawable(getResources().getDrawable(R.mipmap.scan_to_scan));
                break;
        }
    }


    /**
     * dialog消失的回调
     */
    @Override
    public void OnDialogDismiss(){
        DebugLog.showLog(mContext, "isScan:" + Collocation.getCollocation().getIsScan());
        if (!Collocation.getCollocation().getIsScan()){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    type_SearchBox();
                }
            }).start();
        } else {
            DebugLog.showLog(mContext, "第一次选择的是扫码模式...");
            QMDevice.getInstance().clear();
            show_search_error.setVisibility(View.VISIBLE);
            setNetSuccess();
            if (boxAdapter != null){
                boxAdapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * listview的滑动
     */
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState){

        if (list.getChildCount() > 0 && list.getFirstVisiblePosition() == 0 && list.getChildAt(0).getTop() > list.getTop()) {
            // DebugLog.showLog(mContext,"swipeRefresh----true");
            swipe_refresh.setEnabled(true);
        } else {
            // DebugLog.showLog(mContext,"swipeRefresh----false");
            swipe_refresh.setEnabled(false);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }



    //扫描结果回调
    // http://www.shareshow.com.cn/download/index.html?{"ip":"192.168.43.1","dp":"","dt":"2","ds":"4U8RUQ4MLW"}
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(ScanEvent event){
        String result = event.getResult();
        handleResult(result);
    }

    private void handleResult(String result){
        DebugLog.showLog(this,"二维码："+result);
        String json = result.subSequence( result.indexOf("?") + 1, result.length()).toString();
        try {
            JSONObject jsonObject = new JSONObject(json);
            netIp = jsonObject.getString("ip");
            if(netIp !=null){
                String net = netIp.substring(0, netIp.lastIndexOf("."));
                String localIp = NetworkUtils
                        .getNetworkIP();
                if(TextUtils.isEmpty(localIp)){
                    //如果是空，就是没有联网
                    Toast.makeText(App.getApp(),"请调整到与被扫码设备同网络!",Toast.LENGTH_SHORT).show();
                }else if((localIp.substring(0,localIp.lastIndexOf("."))).equals(net)){
                    //同一网段
                    RootPoint rootPoint = QMDevice.getInstance().getSameRootPoint(netIp);
                    if(rootPoint==null){
                        //如果为空，就是发起搜索添加
                        isScan = true;
                        scanForsearch(netIp);
                    }else{
                        //打钩修改UI
                        rootPoint.setPlay(true);
                        boxAdapter.notifyDataSetChanged();
                    }
                }else{
                    //不相同就要盒子调整到同一网段
                    Toast.makeText(App.getApp(),"请调整到与被扫码设备同网络!",Toast.LENGTH_SHORT).show();
                }
            }
        } catch (JSONException e){
            e.printStackTrace();
        }
    }


    private boolean isSameDevice(String ip){
        if (ip != null){
            ArrayList<RootPoint> al = QMDevice.getInstance().getAl();
            for (RootPoint point : al) {
                if (point.getAddress().equals(ip)) {
                    return true;
                }
            }
        }
        return false;
    }


    //网络状态监听回调
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(EventNetworkReceiver event){
        // scanCount = 0;
        if (EventNetworkReceiver.Status.LOADING == event.getStatus()){
            DebugLog.showLog(this, "wifi加载中");
        }else if (EventNetworkReceiver.Status.SUCCESS == event.getStatus()){
            DebugLog.showLog(this, "wifi成功了");
            setNetSuccess();
        }else if(EventNetworkReceiver.Status.CLOSE == event.getStatus()){
            DebugLog.showLog(this, "wifi关闭了");
            setNetSuccess();
        } else if (EventNetworkReceiver.Status.TIMEOUT == event.getStatus()){
            KLog.d("TimeOut");
            DebugLog.showLog(this, "TimeOut！");
        }
    }

    private void setNetSuccess(){
        if (isNet()){
            if (!Collocation.getCollocation().getIsScan()) {
                nearby_nobox.setText(getResources().getString(R.string.nearby_nobox));
                setMode(PUBLIC_STATE);
            } else {
                nearby_nobox.setText(getResources().getString(R.string.seach_safe_mode));
                setMode(PRIVATE_STATE);
            }
        } else {
            if (!Collocation.getCollocation().getIsScan()) {
                nearby_nobox.setText(getResources().getString(R.string.nearby_nobox2));
                setMode(NO_NET_STATE);
            } else {
                nearby_nobox.setText(getResources().getString(R.string.seach_safe_mode));
                setMode(PRIVATE_STATE);
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(EventString event){
        if (event.getEvent().equals(EventString.ALREADY_NETWORK_CONNECT)){
            DebugLog.showLog(mContext, "网络名称相同");
            if (isSameDevice){
                Toast.makeText(mContext, "设备已连接！", Toast.LENGTH_SHORT).show();
                isSameDevice = false;
                if (netIp != null) {
                    RootPoint rootPoint = QMDevice.getInstance().getSameRootPoint(netIp);
                    rootPoint.setPlay(true);
                    boxAdapter.notifyDataSetChanged();
                }
            }
        } else if (event.getEvent().equals(EventString.INPUT_WIFI_PASSWORD)){
            if (scanParms != null) {
                if (scanParms.getSsid() != null && scanParms.getSsid().equals(ssid)) {
                    DebugLog.showLog(mContext, "是热点网络...");
                } else {
                    scanParms.setPassword("无密码");
                    SampleWifiManager.getManager(mContext).checkWifiEnable(scanParms);
                    DebugLog.showLog(mContext, "输入密码操作...scanParms:" + scanParms.getPassword() + scanParms.getSsid());
                }
            }
        }
    }


    //显示二维码信息
    public void loadingBitmap(){
        if(request == null){
            request = new RequestNetworkInfo(mContext);
        }
        request.setOnWifiParamsListener(new RequestNetworkInfo.OnWifiParamsListener(){
            @Override
            public void callback(WifiParams params){
                Gson gson = new GsonBuilder().serializeNulls().create();
                String content = "http://www.shareshow.com.cn/download/index.file_icon_html?" + gson.toJson(params);
                KLog.d(content);
                Bitmap logo = BitmapFactory.decodeResource(getResources(), R.mipmap.icon);
                ssid = params.getSsid();
                bitmap = EncodQrCode.createQRCode(content, 600, 600, logo);
                //image.setImageBitmap(bitmap);
            }
        });
    }


    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.screen_cast:
                tp();
                break;
            case R.id.screen_file:
                file();
                break;
//            case R.id.screen_control:
//                control();
//                break;
//            case R.id.screen_scan:
//                scan();
//                break;
            case R.id.is_open:
                setOpenFile();
                break;

            case R.id.close_file_dialog:
                if (filedialog != null) {
                    filedialog.dismiss();
                }
                break;
        }
    }

    private void scan(){
        boolean isNetConnect =false;
        if(NetworkUtils.isWifi()){
            loadingBitmap();
            isNetConnect =true;
        }else{
            isNetConnect =false;
        }
        AlertDialog.Builder alert = new AlertDialog.Builder(mContext,R.style.window_dialog_wifi_style);
        final Dialog d = alert.create();
        d.show();
        d.getWindow().setContentView(R.layout.dialog_wifi_scan);
        ImageView wifiImg = d.findViewById(R.id.image);
        TextView netName = d.findViewById(R.id.netName);
        Button wifi_scan = (Button)d.findViewById(R.id.wifi_scan);
        LinearLayout screen_wifi= d.findViewById(R.id.screen_wifi_scan);
        LinearLayout screen_no_wifi =d.findViewById(R.id.screen_no_wifi);
        if(isNetConnect){
            wifiImg.setImageBitmap(bitmap);
            screen_wifi.setVisibility(View.VISIBLE);
            screen_no_wifi.setVisibility(View.GONE);
        }else{
            screen_wifi.setVisibility(View.GONE);
            screen_no_wifi.setVisibility(View.VISIBLE);
        }

        wifi_scan.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivityForResult(new Intent(mContext,ScanActivity.class),REQUEST_CODE_QR);
                d.dismiss();
            }
        });

    }

    private void setOpenFile() {
        if (Collocation.getCollocation().getFileOpen()) {
            isFileOpen.setImageDrawable(getResources().getDrawable(R.mipmap.bt_2));
            Collocation.getCollocation().saveFileOpen(false);
        } else {
            isFileOpen.setImageDrawable(getResources().getDrawable(R.mipmap.bt_1));
            Collocation.getCollocation().saveFileOpen(true);
        }
    }

    public class IntentSpan extends ClickableSpan {

        private final View.OnClickListener listener;

        public IntentSpan(View.OnClickListener listener) {
            this.listener = listener;
        }

        @Override
        public void onClick(View widget) {
            // TODO Auto-generated method stub
            listener.onClick(widget);
        }
    }

    public static String ssid;


    /**
     * @param text 生成的文件的名称,和内容
     */
    public void save(String text, String name){
        try {
            FileOutputStream outputStream = mContext.openFileOutput(name, Context.MODE_WORLD_READABLE);
            outputStream.write(text.toString().getBytes());
            outputStream.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    private void initEvent(){
        swipe_refresh.setOnRefreshListener(this);// 下拉刷新事件
    }

    private void initData(){
        swipe_refresh.setColorSchemeResources(android.R.color.holo_red_light, android.R.color.holo_green_light, android.R.color.holo_blue_bright, android.R.color.holo_orange_light);
        // uuid = QMUtil.getInstance().getUUID(mContext);
        uuid = Devices.getInstace(new CacheHelper()).getKey();
        // 适配器
        boxAdapter = new DeviceItemAdapter<RootPoint>(mContext, QMDevice.getInstance().getAl(), this);
        list.setAdapter(boxAdapter);
        list.setOnScrollListener(this);
        mHandler = new Handler();
        // 创建文件共享需要的文件夹
        File f = new File(QMFileType.LOCALPATH);
        if (!f.exists())
            f.mkdirs();
        File f2 = new File(QMFileType.CACHE);
        if (!f2.exists())
            f2.mkdirs();
        // OpenTP();// 开启截屏功能
        RecordManager.canT = false;
        searchDevice();
        registerCommandExecutor();
    }

    private void registerCommandExecutor(){
        CommandExecutorLancher.getOnlyExecutor().setListener(this);// 20002端口监听
        CommandExecutorBox.getOnlyExecutor().setListener(this);// 20001端口监听
        CommandExectuorMobile.getOnlyExecutor().setListener(this).setWH(this);// 20004端口监听
        CommandExecutorLancherFile.getOnlyExecutor();// 启动20003端口
    }

    private void unregisterCommandExecutor(){
        CommandExecutorLancher.getOnlyExecutor().setListener(null);// 20002端口监听
        CommandExecutorBox.getOnlyExecutor().setListener(null);// 20001端口监听
        CommandExectuorMobile.getOnlyExecutor().setListener(null);
    }

    // 5.0系统投屏
    private void OpenTP(){
        if (Build.VERSION.SDK_INT >= 21) {// 5.0系统及以上才可投屏
            // 默认帧率是25 码率2000为高清 1000为标准
            switch (Collocation.getCollocation().getQXprogress()) {
                case 0:
                    RecordManager.getRecordManager().frameRate = 25;
                    RecordManager.getRecordManager().bitRate = 1000;
                case 1:
                    RecordManager.getRecordManager().frameRate = 25;
                    RecordManager.getRecordManager().bitRate = 1000;
                    break;
            }

            RecordManager.getRecordManager().startrecord(null,this);// 启动截屏的接口，继承的类所在的包下的所有文件均是引用的，来源于recordscreen.jar的编译文件
            // 是写投屏底层提供的，他的作用是程序一打开弹出'是否允许投屏...'的提示框,点击"是"才可以投屏，"否"
            // 的话不能进行投屏
            // 整个程序是先进行截屏，能够投屏是指定设备发送开始投屏的指令1001
        }
    }

    private String netID;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(NetworkEvent event){
        boolean isConnected = NetworkUtils.isNetworkConnected();
        // 有网络发送搜索指令，无网络弹出“没有网络..”的提示框
        if (isConnected){
            //   changeNetBitRate();
            //更改码率
            ip = NetworkUtils.getNetworkIP();
            loadSmile();// 发送搜索指令
            startFTPService();// 开启文件共享服务
        } else {
            // 无网络就要把二维码的连接信息隐藏
            stopFTPService();// 停止文件共享服务
            no_Net();// 无网络更新UI
            // sheZhi();// 弹出无网络对话框
        }

        if (QMDevice.getInstance().getSize() == 0) {
            show_search_error.setVisibility(View.VISIBLE);
            if (!Collocation.getCollocation().getIsScan()) {
                nearby_nobox.setText(getResources().getString(R.string.nearby_nobox2));
                setMode(NO_NET_STATE);
            } else {
                nearby_nobox.setText(getResources().getString(R.string.seach_safe_mode));
                setMode(PRIVATE_STATE);
            }
        }
    }


    private void changeNetBitRate() {
        if (NetworkUtils.is5GLocalNet() && Collocation.getCollocation().getNetbitRate() < 4000) {
            if (HairUtils.is2KScreen()) {
                Collocation.getCollocation().saveNetbitRate(5000);
            } else {
                Collocation.getCollocation().saveNetbitRate(4000);
            }
        } else if (!NetworkUtils.is5GLocalNet() && Collocation.getCollocation().getNetbitRate() >= 4000) {
            if (HairUtils.is2KScreen()) {
                Collocation.getCollocation().saveNetbitRate(3000);
            } else {
                Collocation.getCollocation().saveNetbitRate(2500);
            }
        }
    }

    /**
     * 加载进度显示
     */
    private void loadSmile() {
        // Toast.makeText(mContext, "连接成功", Toast.LENGTH_SHORT).show();
        if (myDialog != null)
            myDialog.dismiss();
        // show_search_error.setVisibility(View.GONE);
        // showLoading.setVisibility(View.VISIBLE);
        //loadingText.setText(getResources().getString(R.string.nearby));
        new Thread() {
            public void run() {
                type_SearchBox();// 发送搜索的方法
            }
        }.start();
        mHandler.postDelayed(searchBox, 8 * 1000);// 8秒内没数据默认没有搜索到盒子
    }

    /**
     * 在8秒后设备是否搜索到设备
     */
    private Runnable searchBox = new Runnable() {

        @Override
        public void run() {
            QMUtil.getInstance().closeDialog();
            // showLoading.setVisibility(View.GONE);
            boolean isConnected = NetworkUtils.isNetworkConnected();
            if (!isConnected) {// 没有网就显示"网络没有连接"
                // show_search_error.setVisibility(0);
                // nearby_nobox.setText(getResources().getString(
                // R.string.net_noconnect));
            } else {// 有网，但还是没有盒子，显示"附近没有盒子"
                if (QMDevice.getInstance().getSize() == 0) {
                    // show_search_error.setVisibility(0);
                    //loadingText.setText(getResources().getString(R.string.nearby));
                    if (!Collocation.getCollocation().getIsScan()) {
                        nearby_nobox.setText(getResources().getString(R.string.nearby_nobox));
                        setMode(PUBLIC_STATE);
                    } else {
                        nearby_nobox.setText(getResources().getString(R.string.seach_safe_mode));
                        setMode(PRIVATE_STATE);
                    }

                }
            }
        }
    };

    /**
     * 每300毫秒请求一次搜索，连续三次
     */
    public static void type_SearchBox(){
        if (!Collocation.getCollocation().getIsScan()){
            for (int i = 0; i < 3; i++) {
                // 发送与任盒的搜索够令--102
                CommandExecutorLancher.getOnlyExecutor().searchMessageLancher(QMCommander.TYPE_SEARCH);
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // 发送与Android、IOS的搜索够令--102
                CommandExecutorLancher.getOnlyExecutor().searchRequestMessageMobile(QMCommander.TYPE_SEARCH);
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
                // 发送与Android、IOS的搜索够令--101000 兼容旧版本设计的
                CommandExectuorMobile.getOnlyExecutor().searchMessage(QMCommander.TYPE_SEARCH_M);
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // 发送与PC的搜索够令--102
                CommandExecutorLancher.getOnlyExecutor().searchRequestMessagePC(QMCommander.TYPE_SEARCH);
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 开启心跳检测的服务
     */
    private void startTimerServer(){
        heartServer = new Intent(mContext, HeartBeatServer.class);
        startService(heartServer);
    }

    /**
     * 停止心跳检测的服务
     */
    private void stopTimerService(){
        if(heartServer!=null){
            stopService(heartServer);
        }
    }

    /**
     * 没有网络更新UI
     */
    private void no_Net() {
        QMDevice.getInstance().clear();
        boxAdapter.notifyDataSetChanged();
        closeState();// 关闭通知栏及恢复投屏原始状态
    }

    /**
     * 返回主页面
     */
    private void backToMoreScreenActivity(){
        if (am == null)
            am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> infos = am.getRunningTasks(100);
        for (ActivityManager.RunningTaskInfo rapi : infos){
            if (rapi.topActivity.getClassName().equals(ACTION_BUTTON)){
                am.moveTaskToFront(rapi.id, ActivityManager.MOVE_TASK_WITH_HOME);
                return;
            }
        }
    }

    /**
     * 弹出设置网络的提示框
     */
    private void sheZhi() {
        if (myDialog != null) {
            myDialog.dismiss();
        } else {
            myDialog = new QMDialog(mContext, R.string.wifi_tip, R.string.connect_wifi, new PositionListener() {

                @Override
                public void method(int position) {
                    reRry();
                }

                @Override
                public void cancel() {

                }
            });
        }
    }

    /**
     * 跳转系统网络设置中
     */
    private void reRry() {
        Intent intent = null;
        if (Build.VERSION.SDK_INT > 10)
            intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
        else {
            intent = new Intent();
            intent.setClassName("com.shareshow.aide", "com.android.settings.WirelessSettings");
        }
        startActivity(intent);
    }



    /**
     * 点击主页ListView盒子列表的每一项
     */

    private void onItem(int position){
        final RootPoint rootPoint = QMDevice.getInstance().getRootPoint(position);
        // 先判断有没有选中，如果是选中状态
        if (rootPoint.isPlay()) {
            // 选中就将其取消
            rootPoint.setPlay(false);
            boxAdapter.notifyDataSetChanged();
            if (rootPoint.getdType() == -1)// 是任盒的话发送取消链接消息
                rootPoint.setLcount(-1);
            if (rootPoint.isTouPing()){
                // 设备在投屏状态下取消选中
                rootPoint.setTouPing(false);
                for (int i = 0; i < 3; i++) {
                    if (rootPoint.getdType() == 0 || rootPoint.getdType() == 1) {
                        // 手机间取消投屏指令 只有其在投屏的状态的时候才能够发送停止的指令
                        CommandExectuorMobile.getOnlyExecutor().connectMessage(QMCommander.TYPE_STOP_M, rootPoint.getAddress());
                    } else if (rootPoint.getdType() == 2) {
                        CommandExecutorLancher.getOnlyExecutor().connectPcMessage(QMCommander.TYPE_PC_STOP, rootPoint.getAddress());
                    } else if (rootPoint.getdType() == -1) {
                        endStop = true;
                        rootPoint.setStopByHandle(false);
                        // 向任盒 发送停止投屏的指令，没有返回接口
                        CommandExecutorBox.getOnlyExecutor().connectMessage(QMCommander.TYPE_STOP_PLAY, rootPoint.getAddress(), null);
                    }

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                // 判断还有没有在投屏的设备，
                withoutScreenDevice();
            }
        } else {
            // 选中是手机的时候
            if (rootPoint.getdType() == 0 || rootPoint.getdType() == 1) {
                if (QMDevice.getInstance().hasScreenDevice()) {
                    if (QMDevice.getInstance().hasBoxScreen()) {// 有没有正在投屏的盒子
                        QMUtil.getInstance().showToast(mContext, R.string.phone_box_error);
                        return;
                    }
                    // 发送手机投屏的指令
                    CommandExectuorMobile.getOnlyExecutor().connectMessage(QMCommander.TYPE_SCREEN_M, rootPoint.getAddress());
                    QMUtil.getInstance().showProgressDialog(this, R.string.touping);
                    TorS = 1;// TorS=1表示要投屏的操作，TorS=2表示要分享的操作.
                    rootPoint.setResponse(false);
                    rootPoint.startHandler(new PositionListener() {
                        @Override
                        public void method(int position) {
                            if (QMDevice.getInstance().hasSelectDevice()) {
                                rootPoint.setPlay(false);
                                boxAdapter.notifyDataSetChanged();
                            }
                            QMUtil.getInstance().showToast2(mContext, rootPoint.getName() + "\t" + getString(R.string.no_response));
                            QMUtil.getInstance().closeDialog();
                        }

                        @Override
                        public void cancel() {

                        }
                    });
                }
                rootPoint.setPlay(true);
                boxAdapter.notifyDataSetChanged();
                return;
            }

            // 选中是PC的时候
            if (rootPoint.getdType() == 2) {
                if (QMDevice.getInstance().hasScreenDevice()) {
                    // 发送投屏指令
                    if (QMDevice.getInstance().hasMoblieScreen()) {
                        QMUtil.getInstance().showToast(mContext, R.string.phone_box_error);
                        return;
                    }
                    CommandExecutorLancher.getOnlyExecutor().connectPcMessage(QMCommander.TYPE_PCTP, rootPoint.getAddress());
                }
                rootPoint.setPlay(true);
                boxAdapter.notifyDataSetChanged();
                return;
            }

            // 选中是任盒的时候
            if (rootPoint.getdType() == -1) {
                if (rootPoint.isProgress())// 密码验证过程点击无效
                    return;
                if (QMDevice.getInstance().hasScreenDevice()) {
                    if (QMDevice.getInstance().hasMoblieScreen()) {
                        QMUtil.getInstance().showToast(mContext, R.string.phone_box_error);
                        return;
                    }
                }
                // 盒子没有选中状态，再判断盒子有没有设置连接密码，有密码的话
                DebugLog.showLog(this,"onItem是否有密码："+rootPoint.getEnablepwd());
                if (rootPoint.getEnablepwd().equals("true")) {
                    // 先取本地保存的密码
                    String info = Collocation.getCollocation().getPassWd(rootPoint.getUuid());
                    DebugLog.showLog(this,"保存的密码:"+info);
                    rootPoint.setPassword(info);
                    // 本地如果没有保存密码的话弹出输入密码对话框
                    if (info.equals("")) {
                        showPasswdDialog(rootPoint);
                    } else {
                        passwdVlidate(rootPoint, info);
                    }
                } else {
                    if (QMDevice.getInstance().hasScreenDevice()) {
                        CommandExecutorLancher.getOnlyExecutor().controlMessage(QMCommander.SCREEN_OPEN, rootPoint.getAddress());
                        QMUtil.getInstance().showProgressDialog(this, R.string.touping);
                        TorS = 1;// TorS=1表示要投屏的操作，TorS=2表示要分享的操作.
                        rootPoint.setResponse(false);// 处理投屏20秒内没有响应net
                        rootPoint.startHandler(new PositionListener() {// 可能是多个设备同时投屏，需要分别做个处理，此处可以直接使用handler来处理
                            @Override
                            public void method(int position) {
                                if (QMDevice.getInstance().hasSelectDevice()) {
                                    rootPoint.setPlay(false);
                                    boxAdapter.notifyDataSetChanged();
                                    rootPoint.setLcount(-1);
                                }
                                QMUtil.getInstance().showToast2(mContext, rootPoint.getName() + "\t" + getString(R.string.no_response));
                                QMUtil.getInstance().closeDialog();
                            }

                            @Override
                            public void cancel() {

                            }
                        });
                    }
                    DebugLog.showLog(mContext, "onItem 发送201");
                    CommandExecutorLancher.getOnlyExecutor().connectMessage(QMCommander.TYPE_CONNECT, rootPoint.getAddress(), "");
                    // 如果没有密码的话直接勾选上，
                    rootPoint.setPlay(true);
                    boxAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    /**
     * 向任盒发送密码验证
     *
     * @param rp
     * @param info
     */
    private void passwdVlidate(RootPoint rp, String info) {
        DebugLog.showLog(mContext, "passwdVlidate 发送201");
        CommandExecutorLancher.getOnlyExecutor().connectMessage(QMCommander.TYPE_CONNECT, rp.getAddress(), info + "");
        rp.setProgress(true);
        boxAdapter.notifyDataSetChanged();
        mHandler.postDelayed(passwd, 5000);

    }

    /**
     * 下拉刷新处理
     */
    @Override
    public void onRefresh(){
        // tv_re.setVisibility(View.VISIBLE);
        boolean isConnected = NetworkUtils.isNetworkConnected();
        // 设置刷新可用和无用|| QMDevice.getInstance().hasScreenDevice()
        if (refreshOnline == 1 || !isConnected ){
            swipe_refresh.setRefreshing(false);
            return;
        }
        // show_search_error.setVisibility(8);
        refreshOnline = 1;
        // 每次刷新默认所有盒子不在线
        if(!Collocation.getCollocation().getIsScan()){
            QMDevice.getInstance().setOffline();
        }
        new Thread(){
            public void run(){
                if (Collocation.getCollocation().getIsScan()){
                    QMDevice.getInstance().removeOffline();
                    runOnUiThread(new Runnable(){
                        @Override
                        public void run(){
                            Toast toast= Toast.makeText(mContext, "请扫码连接或者在设置中调整为公开连接模式", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
                            toast.show();
                            swipe_refresh.setRefreshing(false);
                            // tv_re.setVisibility(View.GONE);
                            refreshOnline = 0;
                            return;
                        }
                    });
                    return;
                }else{
                    type_SearchBox();
                }
            }
        }.start();
        complete();
    }


    /**
     * 下拉刷新完成后的操作，在主线程中运行
     */
    private void complete(){
        mHandler.postDelayed(refresh, 8 * 1000);
    }

    private Runnable refresh = new Runnable() {
        @Override
        public void run() {
            swipe_refresh.setRefreshing(false);
            // tv_re.setVisibility(View.GONE);
            refreshOnline = 0;// 判断盒子在不在线方法后状态恢复为0
            // 8秒内判断盒子不能再被搜索到就移除
            QMDevice.getInstance().removeOffline();
            boxAdapter.notifyDataSetChanged();
            if (QMDevice.getInstance().getSize() == 0) {
                if (!Collocation.getCollocation().getIsScan()) {
                    nearby_nobox.setText(getResources().getString(R.string.nearby_nobox));
                    setMode(PUBLIC_STATE);
                }else{
                    nearby_nobox.setText(R.string.seach_safe_mode);
                    setMode(PRIVATE_STATE);
                }
                show_search_error.setVisibility(View.VISIBLE);
                //showLoading.setVisibility(View.GONE);// 集合没有数据，显示/附近没有设备的布局
            }
        }
    };

    // 判断当前还有没有盒子在投屏
    private void withoutScreenDevice(){
        // 没有的话，取消通知栏和改变底部【投屏】的状态
        if (!QMDevice.getInstance().hasScreenDevice()){
            closeState();
        }
    }

    /**
     * 5秒内密码验证是否有响应
     */

    private Runnable passwd = new Runnable(){

        @Override
        public void run(){
            // 没有响应取消进度条显示
            QMDevice.getInstance().setProgress();
            boxAdapter.notifyDataSetChanged();
            QMUtil.getInstance().showToast(mContext, R.string.no_response);
            pass_input = "";
        }

    };


    // 点击投屏按钮

    public void tp(){
        // 系统低于5.0不准投屏
        if (Build.VERSION.SDK_INT < 21) {
            QMUtil.getInstance().showToast(mContext, R.string.version_higher);
            return;
        }
        if (!RecordManager.getRecordManager().supportScreen) {
            QMUtil.getInstance().showToast(mContext, R.string.version_self);
            return;
        }
        judgeTP();
    }

    // 点击分享按钮

    private void fx(){
        // 要分享之前如果已经投屏了先关闭投屏
        isScreenShare = true;
        if (QMDevice.getInstance().hasScreenDevice()) {
            close_TP(R.string.close_tp_tip);
        } else {
            shareOrYk(0);
        }

        // 暂时关闭分享功能
        // QMUtil.getInstance().showToast(mContext,R.string.screen_share_forbidden);
    }

    // 点击远程控制按钮
    public void control(){
        // 要控制之前如果已经投屏了先关闭投屏
        if (QMDevice.getInstance().hasScreenDevice()){
            close_TP(R.string.close_tp_tip2);
        }else{
            shareOrYk(1);
        }
    }

    // 点击文件共享按钮
    public void file(){
        if(QMDevice.getInstance().hasScreenDevice()){
            close_TP(R.string.close_tp_tip3);
        }else{
            startFileShare();
        }
    }

    /**
     * 开始分享或远程控制
     */
    private void shareOrYk(int aa){
        if (QMDevice.getInstance().getSize() == 0){
            switch (aa){
//                case 0:
//                    QMUtil.getInstance().showToast(mContext, R.string.no_fx_divice);
//                    break;
                case 1:
                    QMUtil.getInstance().showToast(mContext, R.string.no_yk_divice);
                    break;
            }
            return;
        }

        int chooseCount = 0;
        RootPoint rp = null;// 需要分享或控制的设备
        for (int i = 0; i < QMDevice.getInstance().getSize(); i++) {
            RootPoint point = QMDevice.getInstance().getRootPoint(i);
            if (point.isPlay()){
                // switch (point.getdType()) {
                // case -1:
                chooseCount++;
                if (chooseCount > 1){
                    switch (aa) {
//                        case 0:
//                            QMUtil.getInstance().showToast(mContext, R.string.just_one_can_share);
//                            return;
                        case 1:
                            QMUtil.getInstance().showToast(mContext, R.string.just_one_can_control);
                            return;
                    }
                }

                rp = point;
            }
        }

        if (chooseCount == 0){
            QMUtil.getInstance().showToast(mContext, R.string.without_selected_device);
            return;
        }

        if(rp!=null&&rp.getdType()!=-1){
            QMUtil.getInstance().showToast(mContext, R.string.without_device_control);
            return;
        }

        if(aa==1){
            controlLancher(rp);
            //control(QMCommander.TYPE_OPEN_CONTROL, rp);
        }

//        switch (aa){
//            // 0是分享
//            case 0:
//
//                break;
//            case 1:
//
//                break;
//        }


    }

    /**
     * 判断能否投屏
     */
    private void judgeTP(){
        // 如果已经在投屏了，直接关闭投屏
        if (QMDevice.getInstance().hasScreenDevice()){
            closeTP();
        }else{
            if (QMDevice.getInstance().getSize() == 0) {
                QMUtil.getInstance().showToast(mContext, R.string.no_tp_divice);
                return;
            }
            // 先判断有没有选中设备
            int chooseCount = 0;
            int type = -1;
            for (int i = 0; i < QMDevice.getInstance().getSize(); i++){
                RootPoint point = QMDevice.getInstance().getRootPoint(i);
                if (point.isPlay()) {
                    switch (point.getdType()) {
                        case -1:
                        case 2:
                            if (type == 1) {
                                QMUtil.getInstance().showToast(mContext, R.string.phone_box_error);
                                return;
                            }
                            type = 0;
                            break;
                        case 0:
                        case 1:
                            if (type == 0) {
                                QMUtil.getInstance().showToast(mContext, R.string.phone_box_error);
                                return;
                            }
                            type = 1;
                            break;
                        // case 2:
                        // QMUtil.getInstance().showToast(mContext,
                        // R.string.pc_forbidden);
                        // return;
                    }
                    chooseCount++;
                }
            }

            if (chooseCount == 0){
                QMUtil.getInstance().showToast(mContext, R.string.without_selected_device);
                return;
            }

            if (RecordManager.canT){
                // 手机在截屏
                if (RecordManager.getRecordManager().typeT != type)// 但投屏的类型与截屏分辨率不一致
                {
                    RecordManager.getRecordManager().endrecord();// 先停止之前的截屏
                    RecordManager.getRecordManager().typeT = type;
                    reRecord();
                }else{
                    readyTP();// 一致情况直接投屏去
                }
            }else{// 程序第一次进来的时候弹出的提示框若点击了“取消”,则要投屏的话这个提示框还要弹出
                RecordManager.getRecordManager().typeT = type;
                reRecord();
            }
        }
    }

    /**
     * 切换任盒或手机 重新截屏
     */
    public void reRecord() {
        RecordManager.getRecordManager().startrecord(new ConnectFTPListener(){

            @Override
            public void success() {
                readyTP();
            }

            @Override
            public void fail(int state) {
                QMUtil.getInstance().showToast(mContext, R.string.must_choose);
            }
        },this);
    }


    /**
     * 开始投屏
     */
    private void readyTP(){
        TorS = 1;// TorS=1表示要投屏的操作，TorS=2表示要分享的操作.
        endStop = false;
        QMUtil.getInstance().showProgressDialog(MoreScreenActivity.this, R.string.touping);
        QMDevice.getInstance().oprationSelectDevice(new BoxOnClickListener() {

            @Override
            public void onClick(final RootPoint rp) {
                if (rp.getdType() == 0 || rp.getdType() == 1){
                    // 发送手机投屏的指令
                    CommandExectuorMobile.getOnlyExecutor().connectMessage(QMCommander.TYPE_SCREEN_M, rp.getAddress());
                }else if (rp.getdType() == 2){
                    // 发送pc投屏指令
                    CommandExecutorLancher.getOnlyExecutor().connectPcMessage(QMCommander.TYPE_PCTP, rp.getAddress());
                } else {
                    // 首先请求屏幕投屏有没有打开 监听接口是run2（）方法中
                    CommandExecutorLancher.getOnlyExecutor().controlMessage(QMCommander.SCREEN_OPEN, rp.getAddress());
                }

                rp.setResponse(false);// 多个设备投屏没有响应的处理，可以直接使用handler来处理
                rp.startHandler(new PositionListener() {

                    @Override
                    public void method(int position){
                        QMUtil.getInstance().closeDialog();
                        if (QMDevice.getInstance().hasSelectDevice()){
                            rp.setPlay(false);
                            boxAdapter.notifyDataSetChanged();
                            if (rp.getdType() == -1)
                                rp.setLcount(-1);
                        }
                        stopTp(rp);
                        QMUtil.getInstance().showToast2(mContext, rp.getName() + "\t" + getString(R.string.no_response));
                    }

                    @Override
                    public void cancel() {

                    }
                });
            }
        });
    }

    /**
     * 文件共享
     */
    private void startFileShare(){
        int chooseCount = QMDevice.getInstance().selectDeviceCount();
        Intent intent = new Intent(mContext, QuiteFileActivity.class);
        startActivity(intent);
    }

    /**
     * 开启分享关闭投屏的提示框
     */
    private void close_TP(int id) {
        new QMDialog(mContext, id, R.string.yes, new PositionListener() {
            @Override
            public void method(int position) {
                closeTP();
            }

            @Override
            public void cancel() {

            }

        });
    }

    /**
     * 开启远程控制的请求，control表示发送的请求有没有得到响应，10秒内没有响应提示请求‘没有响应’
     *
     * @param typeOpenControl
     * @param rootPoint
     */
    private void control(int typeOpenControl, RootPoint rootPoint) {
        TorS = 3;
        // QMUtil.getInstance().showProgressDialog(mContext, R.string.controlimg);
        CommandExecutorLancher.getOnlyExecutor().controlMessage(typeOpenControl, rootPoint.getAddress());
        shareResponse = false;
        mHandler.postDelayed(share, 10 * 1000);
    }

    /**
     * 在5秒后设备的分享或控制请求是否有响应
     */
    private Runnable share = new Runnable(){
        @Override
        public void run(){
            if (!shareResponse){
                QMUtil.getInstance().closeDialog();
                switch (TorS){
                    case 2:
                        QMUtil.getInstance().showToast(MoreScreenActivity.this, R.string.no_response2);
                        break;
                    case 3:
                        QMUtil.getInstance().showToast(MoreScreenActivity.this, R.string.no_response3);

                        break;

                }
            }
            shareResponse = false;
        }
    };

    /**
     * 关闭投屏
     */
    private void closeTP(){
        endStop = true;// 是否是点击了底部【投屏】按钮来进行停止投屏的功能
        sendStop();
        RecordManager.getRecordManager().endrecord();
    }

    /**
     * Home键，回到桌面的方法
     */
    private void getHome(){
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        i.addCategory(Intent.CATEGORY_HOME);
        startActivity(i);
    }

    @Override
    public void onStart(){
        super.onStart();
        DebugLog.showLog(mContext,"onStart");
    }

    @Override
    public void onStop(){
        // 开启悬浮框
        super.onStop();
        DebugLog.showLog(this,"onStop");
    }

    /**
     * 注册20001和20002端口的监听
     */
    @Override
    public void onResume(){
        super.onResume();
        isOpeanfile=false;
        isResume=true;
        registerCommandExecutor();
        MobclickAgent.onResume(mContext);
        if(Collocation.getCollocation().getFileFriendAddress()!=null){
            ArrayList<RootPoint> roots=QMDevice.getInstance().getAl();
            String address = Collocation.getCollocation().getFileFriendAddress();
            RootPoint point= getRootRecieveFile(address);
            if(point!=null&&point.getFiles()!=null&&point.getFiles().size()!=0&&Collocation.getCollocation().getFileOpen()){
                startActivity(OpenFileUtil.openFile(this, point.getFiles().get(0).getPath()));
                point.getFiles().remove(0);
                int pos=0;
                for (int i = 0; i < roots.size(); i++){
                    if(roots.get(i).getAddress().equals(point.getAddress())){
                        pos=i;
                        DebugLog.showLog(mContext,"root1:"+roots.get(i));
                        roots.remove(i);
                        break;
                    }
                }
                QMDevice.getInstance().getAl().add(pos,point);
            }
            Collocation.getCollocation().savaFileFriendAddress(null);
        }
        if (boxAdapter != null)// 从文件共享页面返回时如果切换到其他设备需要重新勾选
            boxAdapter.notifyDataSetChanged();
        setNetSuccess();
        if(QMDevice.getInstance().getSize()!=0){
            show_search_error.setVisibility(View.GONE);
        }else{
            show_search_error.setVisibility(View.VISIBLE);
        }
    }

    private void searchDevice(){
        QMDevice.getInstance().clear();
        if (!Collocation.getCollocation().getIsScan()){
            new Thread(new Runnable(){
                @Override
                public void run(){
                    type_SearchBox();
                }
            }).start();

            mHandler.postDelayed(searchBox, 8 * 1000);// 8秒内没数据默认没有搜索到盒子
        }
    }

    private boolean isNet(){
        boolean isConnect=NetworkUtils.isNetworkConnected();
        return isConnect;
    }

    private MoreScreenActivity.MainConnection conn = new MoreScreenActivity.MainConnection();

    private class MainConnection implements ServiceConnection {

        public void onServiceDisconnected(ComponentName name){
            DebugLog.showLog(mContext, "解除绑定！");
            // App.isServiceConnect = false;
            //	isConnectService = false;
        }

        public void onServiceConnected(ComponentName name, IBinder service){
            // App.isServiceConnect = true;
            //	isConnectService = true;
            DebugLog.showLog(mContext, "绑定成功！");

        }
    }

    /**
     * 停止所有的已经点起来的设备 发送停止投屏的指令
     */
    private void sendStop(){
        QMDevice.getInstance().oprationScreenDevice(new BoxOnClickListener(){
            @Override
            public void onClick(RootPoint rootPoint){
                rootPoint.setTouPing(false);
                for (int i = 0; i < 3; i++){
                    if (rootPoint.getdType() == -1){
                        rootPoint.setStopByHandle(false);
                        // 已经进行投屏的盒子全部发送停止投屏的指令
                        CommandExecutorBox.getOnlyExecutor().connectMessage(QMCommander.TYPE_STOP_PLAY, rootPoint.getAddress(), null);
                    } else if (rootPoint.getdType() == 2){
                        // 发送停止投屏到PC端
                        CommandExecutorLancher.getOnlyExecutor().connectPcMessage(QMCommander.TYPE_PC_STOP, rootPoint.getAddress());
                    }else{
                        CommandExectuorMobile.getOnlyExecutor().connectMessage(QMCommander.TYPE_STOP_M, rootPoint.getAddress());
                    }

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        closeState();
    }

    // 关闭通知栏
    private void closeState(){
        endStop = true;// 为true的话在接收20001端口发送的200数据不处理
        // 改变状态及取消通知栏
        RecordManager.getRecordManager().endrecord();
        screenState.setText(getResources().getString(R.string.screen_cast));
        // screenCast.setBackground(getResources().getDrawable(R.color.tab_background));
        if (nm != null)
            nm.cancel(1000);
    }


    @Override
    public void onPause(){
        super.onPause();
        MobclickAgent.onPause(mContext);
        isResume=false;
        if(homeWatcher!=null){
            homeWatcher.stopWatch();
        }

        if(filedialog!=null){
            filedialog.dismiss();
        }
    }




    // 结束所有的链接
    private void end(){
        sendStop();
        // 取消与任盒文件共享的链接
        QMDevice.getInstance().cancelFileShare();
        if (Build.VERSION.SDK_INT >= 21){
            RecordManager.getRecordManager().endrecord();// 停止底层截屏的接口，只有5.0系统才执行了开始截屏的方法，这里需要停止截屏
        }
    }

    /**
     * 投屏成功后通知栏的显示
     */
    private void openNotification(String info){
        nm = (NotificationManager) mContext.getSystemService(mContext.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext);
        mRemoteViews = new RemoteViews(mContext.getPackageName(), R.layout.main_notify);
        mRemoteViews.setImageViewResource(R.id.notify_image_view, R.mipmap.title_icon);
        mRemoteViews.setImageViewResource(R.id.notify_button_random1, R.mipmap.touping_stop);
        mRemoteViews.setTextColor(R.id.notify_button_random2, getResources().getColor(R.color.xt_red));
        // API3.0 以上的时候显示按钮，否则消失
        mRemoteViews.setTextViewText(R.id.notify_button_play_pause, info);
        // mRemoteViews.setTextViewText(R.id.notify_button_random, "停止");
        // 点击通知栏【停止】按钮的广播进行注册
        Intent buttonIntent = new Intent(MoreScreenActivity.this.ACTION_BUTTON);
        // 上一首按钮
        PendingIntent intent_paly = PendingIntent.getBroadcast(mContext, 1, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.notify_button_random, intent_paly);
        mRemoteViews.setOnClickPendingIntent(R.id.notify_button_random1, intent_paly);
        mRemoteViews.setOnClickPendingIntent(R.id.notify_button_random2, intent_paly);
        Intent intt = new Intent(mContext, MoreScreenActivity.class);
        intt.putExtra("page",3);
        intt.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pi = PendingIntent.getActivity(MoreScreenActivity.this, 1, intt, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContent(mRemoteViews).setContentIntent(pi).setTicker("投屏在后台运行").setWhen(System.currentTimeMillis())
                // 通知产生的时间，会在通知信息里显示
                .setContentTitle(getResources().getString(R.string.tp_now)).setPriority(Notification.PRIORITY_DEFAULT)// 设置该通知优先级
                .setSmallIcon(R.mipmap.icon2);

        notify = mBuilder.build();
        notify.flags = Notification.FLAG_NO_CLEAR;
        notify.defaults |= Notification.DEFAULT_SOUND;
        nm.notify(1000, notify);
    }

    /**
     * 通知栏点击停止按钮的广播和投屏心跳公用的广播
     */
    private MoreScreenActivity.NotifiCationReceiver boxRemoveReceiver;
    //private TextView netName2;
    //private TextView passwordConn;
    //private RelativeLayout rLayout;
    public static String ip;
    public static String netIp=null;

    private void initOtherCast(){
        // 注册接收消息广播
        boxRemoveReceiver = new MoreScreenActivity.NotifiCationReceiver();
        IntentFilter intentFilter = new IntentFilter(ACTION_BUTTON);
        mContext.registerReceiver(boxRemoveReceiver, intentFilter);
    }

    private class NotifiCationReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent){
            // aa为0表示通知栏点击停止按钮的广播
            // aa为1表示的是投屏发送心跳8秒内没有接受到数据发送的广播通知
            DebugLog.showLog(this,"收到广播的code :"+intent.getIntExtra("aa",0));
            switch (intent.getIntExtra("aa", 0)){
                case 0:
                    sendStop();// 停止所有设备的投屏
//                    collapseStatusBar();// 收起通知栏以便显示程序的UI
                    backToMoreScreenActivity();
                    break;
                case 1:
                    sendStop();
                    int position = intent.getIntExtra("position", 0);
                    int type = intent.getIntExtra("type", 0);
                    exitTP(QMDevice.getInstance().getRootPoint(position), type, R.string.tp_interrupt);// 任盒退出
                    DebugLog.showLog(mContext,"收到心跳断开的接口");
                    break;
                case 2:
                    if (nm != null){
                        nm.cancel(1000);
                    }
                    break;
            }
        }
    }

    // 投屏发送心跳8秒内没有收到消息做的处理
    public void exitTP(RootPoint rp, int type, int id){
        rp.setTouPing(false);
        QMUtil.getInstance().showToast2(mContext, rp.getName() + getResources().getString(id));
        if (QMDevice.getInstance().hasScreenDevice()) {// 如果还有其他投屏设备，此设备要取消选中状态
            rp.setPlay(false);
            if (type == -1)// 任盒要取消与文件共享的链接
                rp.setLcount(-1);
            boxAdapter.notifyDataSetChanged();
        } else {
            backToMoreScreenActivity();
            closeState();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CODE_CAPTURE_PERM){
            RecordManager.getRecordManager().onActivityResult(mMediaProjectionManager,requestCode,resultCode,data);
        }

//        if(requestCode==SETTING_CODE){
//            if(!Collocation.getCollocation().getIsScan()){
//                new Thread(new Runnable() {
//                    @Override
//                    public void run(){
//                        type_SearchBox();
//                    }
//                }).start();
//                if (boxAdapter != null)
//                    boxAdapter.notifyDataSetChanged();
//            }
//        }

        if (requestCode == code1 && resultCode == ScreenShareActivity.code2) {// 分享界面的回调
            isScreenShare=false;
            int palyError = data.getIntExtra("palyError", 0);
            RootPoint rootPoint = (RootPoint) data.getExtras().getSerializable("rootPoint");
            switch (palyError){
                case 0:// 任盒分享失败的提示
                    QMUtil.getInstance().showToast2(mContext, Html.fromHtml(String.format(getString(R.string.play_error), rootPoint.getName() + "")) + "");
                    startActivity(new Intent(mContext, MainActivity.class));
                    if(rootPoint.getdType()==0||rootPoint.getdType()==1){
                        CommandExectuorMobile.getOnlyExecutor().connectMessage(QMCommander.TYPE_SHARE_STOP_M, rootPoint.getAddress());
                    }else if(rootPoint.getdType()==2){
                        CommandExecutorLancher.getOnlyExecutor().connectPcMessage(QMCommander.TYPE_PC_STOP, rootPoint.getAddress());
                    }

                    break;
                case 3:
                    // 手机间退出分享需要向投屏方发送停止投屏的消息
                    if (rootPoint.getdType() == 2){
                        CommandExecutorLancher.getOnlyExecutor().connectPcMessage(QMCommander.TYPE_PC_STOP, rootPoint.getAddress());
                    }else if(rootPoint.getdType()==1||rootPoint.getdType()==0){
                        CommandExectuorMobile.getOnlyExecutor().connectMessage(QMCommander.TYPE_SHARE_STOP_M, rootPoint.getAddress());
                    }
                    break;
            }
        }

        if (requestCode == code1 && resultCode == ControlActivity.code2){// 远程控制界面的回调
            if (data.getExtras() != null) {
                RootPoint rootPoint = (RootPoint) data.getExtras().getSerializable("rootPoint");
                RootPoint rp = QMDevice.getInstance().getSameRootPoint(rootPoint);
                rp.setEnablepwd("true");
                Collocation.getCollocation().savePassWd(rp.getUuid(), "");
                rp.setLcount(-1);
                rp.setPlay(false);
                boxAdapter.notifyDataSetChanged();
                QMUtil.getInstance().showToast2(mContext, rp.getName() + getResources().getString(R.string.password_changed));
            } else
                QMUtil.getInstance().showToast(mContext, R.string.no_response3);
        }

        if (requestCode == SETTING_CODE){
            if (Collocation.getCollocation().getFloatWindow()){
                initWindowPermission();
                recordWindow();
            }
        }

        if (requestCode==REQUEST_CODE_QR){
            WifiManager wifimanager = (WifiManager) mContext.getSystemService(mContext.WIFI_SERVICE);
            if (name!=null&&name.equals(wifimanager.getConnectionInfo().getSSID())){
                scanForsearch(scanParms.getIp());
            }
        }

    }

    /**
     * 发送扫描的搜索指令
     */
    public static  boolean isScan=false;
    private void scanForsearch(String ip){
        // 发送搜索指令
        CommandExectuorMobile.getOnlyExecutor().sendsearchMessage(QMCommander.TYPE_SEARCH_SCAN,ip);
        CommandExecutorLancher.getOnlyExecutor().replyResponseMessagePC(QMCommander.TYPE_SEARCH_SCAN_PC);
        CommandExecutorLancher.getOnlyExecutor().replyConnectMessagePC(QMCommander.TYPE_SEARCH_SCAN_PC,ip);
        //兼容旧版本的搜索指令
        CommandExecutorLancher.getOnlyExecutor().searchMessageLancher(QMCommander.TYPE_SEARCH);
        CommandExecutorLancher.getOnlyExecutor().searchRequestMessagePC(QMCommander.TYPE_SEARCH);
        CommandExectuorMobile.getOnlyExecutor().connectMessage(QMCommander.TYPE_SEARCH_M,ip);
    }


    private void chooseNetDialog(WifiParams scanParms){
        WifiManager wifimanager = (WifiManager)mContext.getSystemService(mContext.WIFI_SERVICE);
        netID = wifimanager.getConnectionInfo().getSSID();
        //得到的目标的名称
        name = scanParms.getSsid();
        password = scanParms.getPassword();
        if (password != null)
            if (password.length() < 3){
                password = "无密码";
            }

    }



    private void recordWindow(){
        int type = getType();
        DebugLog.showLog(mContext, "recordWindow:" + type);
        if (RecordManager.canT) {// 手机在截屏
            if (RecordManager.getRecordManager().typeT != type)// 但投屏的类型与截屏分辨率不一致
            {
                RecordManager.getRecordManager().endrecord();// 先停止之前的截屏
                RecordManager.getRecordManager().typeT = type;
                reRecordWindow();
            }
        } else {// 程序第一次进来的时候弹出的提示框若点击了“取消”,则要投屏的话这个提示框还要弹出
            RecordManager.getRecordManager().typeT = type;
            if (type != -1) {
                reRecordWindow();
            }
        }
    }

    private void reRecordWindow(){

        RecordManager.getRecordManager().startrecord(new ConnectFTPListener() {

            @Override
            public void success(){
                // readyTP();
            }

            @Override
            public void fail(int state) {
                QMUtil.getInstance().showToast(mContext,
                        R.string.must_choose);
            }
        },this);


    }


    private int getType(){
        boolean isPhone = false;
        boolean isPC = false;
        for (int i = 0; i < QMDevice.getInstance().getSize(); i++) {
            RootPoint point = QMDevice.getInstance().getRootPoint(i);
            if (point.isPlay()){
                if (point.getdType() == -1 || point.getdType() == 2) {
                    isPC = true;
                } else if (point.getdType() == 0 || point.getdType() == 1){
                    isPhone = true;
                }
            }
        }


        if ((isPC && isPhone) || (isPC && !isPhone)) {


            //  都打勾或者只有pc/盒子打勾
            return 0;
        } else if ((!isPC && isPhone)) {


            //只有手机打勾的
            return 1;
        } else {

            //都没有打勾的
            return -1;
        }

    }


//    @Override
//    protected void onNewIntent(Intent intent){
//        super.onNewIntent(intent);
//        if (intent.getIntExtra("aa", 0) == 1) {
//            int position = intent.getIntExtra("position", 0);
//            RootPoint point = QMDevice.getInstance().getRootPoint(position);
//            if (point.isPlay()) {
//                QMUtil.getInstance().showToast2(mContext, point.getName() + getResources().getString(R.string.password_changed));
//                if (point.getdType() == -1)
//                    point.setLcount(-1);
//                point.setPlay(false);
//                point.setEnablepwd("true");
//                boxAdapter.notifyDataSetChanged();
//            }
//        }
    //   }


    // ===========================================指令监听接口===============================================
    // ============10002端口======Lancher==========start==============



    /**
     * 10002端口102指令搜索设备的监听
     */
    @Override
    public void searchLancher(RootPoint rootPoint){
        //收到的是改名
        if (rootPoint.getCategory() == QMCommander.TYPE_NAME){
            RootPoint rp = QMDevice.getInstance().getSameRootPoint(rootPoint);
            rp.setName(rootPoint.getName());
            return;
        }

        if (Collocation.getCollocation().getIsScan() && rootPoint.getCategory() != QMCommander.TYPE_SEARCH_SCAN){
            //当是选择的是扫描互联的时候，和回复的是不是扫描互联的指令的时候,针对的是老版本及盒子的回复
            if (rootPoint!=null&&rootPoint.getAddress().equals(netIp)|| rootPoint!=null&&((NetworkUtils.getNetworkIP().equals(rootPoint.getHost())))) {
                if (rootPoint.getUuid() == null || rootPoint.getUuid().equals("") || rootPoint.getUuid().equals("null") || rootPoint.getUuid().equals(uuid))
                    return;// 没有uuid唯一标示的过滤，是自己手机设备过滤
                Log.d("CCC","老指令"+rootPoint.getName());
                addCollotion(rootPoint);
            } else {
                return;
            }
        }else if (Collocation.getCollocation().getIsScan() && rootPoint.getCategory() == QMCommander.TYPE_SEARCH_SCAN){
            //当是选择的是扫描互联的时候，收到的也是扫描互联的指令的时候
            if (rootPoint.getUuid() == null || rootPoint.getUuid().equals("") || rootPoint.getUuid().equals("null") || rootPoint.getUuid().equals(uuid))
                return;// 没有uuid唯一标示的过滤，是自己手机设备过滤
            if (rootPoint.getAddress().equals(netIp)||rootPoint!=null&&((NetworkUtils.getNetworkIP().equals(rootPoint.getHost())))){
                Log.d("CCC","新指令"+rootPoint.getName());
                addCollotion(rootPoint);
            }
        }else{
            //当自己选择的不是扫描互联的时候
            if (rootPoint.getName() == null || rootPoint.getName().equals(""))// 过滤没有名称的设备，不知为啥会收到这样的消息
                return;
            if (rootPoint.getdType() != -1) {// 非任盒设备
                if (rootPoint.getUuid() == null || rootPoint.getUuid().equals("") || rootPoint.getUuid().equals("null") || rootPoint.getUuid().equals(uuid))
                    return;// 没有uuid唯一标示的过滤，是自己手机设备过滤
                //    DebugLog.showLog(mContext,"搜索------isScan111:"+isScan+"指令："+rootPoint.getCategory());
                if (rootPoint.getCategory() != QMCommander.TYPE_SEARCH_SCAN || isScan||(NetworkUtils.getNetworkIP().equals(rootPoint.getHost()))||rootPoint.getAddress().equals(netIp)){
                    addCollotion(rootPoint);
                    //    DebugLog.showLog(mContext,"添加rootPoint："+rootPoint.getName());
                }
            } else {// 任盒设备
                if (rootPoint.getAddress().equals(NetworkUtils.getNetworkIP()))
                    return;// 过滤自己收到的自己发送的搜索指令
                addCollotion(rootPoint);// 添加到本类集合中
            }
        }

        mHandler.removeCallbacks(searchBox);
        if (QMDevice.getInstance().getSize() == 1){// 默认设备搜索的时候第一个盒子没有密码就勾选上
            RootPoint point = QMDevice.getInstance().getRootPoint(0);
            switch (point.getdType()){
                case -1:
                    if (point.getEnablepwd() != null && point.getEnablepwd().equals("false")){
                        point.setPlay(true);// 与任盒链接需要发送文件共享链接消息
                        DebugLog.showLog(mContext,"searchLancher 发送201");
                        CommandExecutorLancher.getOnlyExecutor().connectMessage(QMCommander.TYPE_CONNECT, point.getAddress(), "");
                    }
                    break;
                case 0:
                case 1:
                case 2:
                    point.setPlay(true);
                    break;
            }
        }

        // 当是扫码得到的ip的时候将传过来的设备进行连接
        if (netIp != null && rootPoint.getAddress().equals(netIp)){
            rootPoint.setPlay(true);
            netIp = null;
            isScan=false;
        }

        // showLoading.setVisibility(View.GONE);
        swipe_refresh.setRefreshing(false);
        boxAdapter.notifyDataSetChanged();
        if(QMDevice.getInstance().getSize()!=0){
            show_search_error.setVisibility(View.GONE);
        }
        //firstOpenTp(rootPoint); //TODO
    }


    private void firstOpenTp(RootPoint rootPoint) {
        if (Collocation.getCollocation().getFloatWindow()) {
            if (isFirstSearch) {
                return;
            }
            isFirstSearch = true;
            int type = rootPoint.getdType();
            switch (type){
                case -1:
                case 2:
                    RecordManager.getRecordManager().typeT = 0;
                    break;
                case 0:
                case 1:
                    RecordManager.getRecordManager().typeT = 1;
                    break;
                default:
                    break;
            }
            //  OpenTP( );// 开启截屏功能
        }
    }

    /**
     * 添加到本类集合中
     *
     * @param rootPoint
     */
    public void addCollotion(RootPoint rootPoint){
        if (rootPoint.getAddress().equals(NetworkUtils.getNetworkIP())) {
            return;
        }
        if (NetworkUtils.isNetworkConnected()){
            QMDevice.getInstance().add(rootPoint);
        }

    }

    /**
     * 10002端口201指令连接设备的监听 ，用于处理密码验证，与lancher交互的
     */
    @Override
    public void connectLancher(RootPoint rootPoint){
        final RootPoint rp = QMDevice.getInstance().getSameRootPoint(rootPoint);
        if (rp == null || rp.getdType() != -1)
            return;
        // QMUtil.getInstance().closeDialog();
        DebugLog.showLog(this,"是否有密码："+rootPoint.getEnablepwd());
        mHandler.removeCallbacks(passwd);
        if("true".equals(rootPoint.getLinkstate())){
            // 密码验证如果对了的话
            rp.setPlay(true);// 一要勾选
            rp.setProgress(false);// 二要取消进度条的显示
            boxAdapter.notifyDataSetChanged();
            if(!pass_input.equals("")){// 要将密码保存到本地，之所以判断pass_input是否为空
                // 是因为pass_input为空的话是绝对没有在弹出输入密码对话框那里进行赋值，而是用户直接取了本地的
                // 密码进行验证的
                Collocation.getCollocation().savePassWd(rp.getUuid(), pass_input);
                DebugLog.showLog(this,"盒子密码："+pass_input);
                rp.setPassword(pass_input);
            }
            if (QMDevice.getInstance().hasScreenDevice()){
                // 如果系统已在投屏状态就进行请求盒子端屏幕投屏有没有打开
                QMUtil.getInstance().showProgressDialog(MoreScreenActivity.this, R.string.touping);
                // 请求屏幕投屏指令，监听接口为run
                //
                // 2（）
                CommandExecutorLancher.getOnlyExecutor().controlMessage(QMCommander.SCREEN_OPEN, rp.getAddress());
                TorS = 1;
                rp.setResponse(false);
                rp.startHandler(new PositionListener(){
                    @Override
                    public void method(int position){
                        QMUtil.getInstance().closeDialog();
                        if (QMDevice.getInstance().hasSelectDevice()){
                            rp.setPlay(false);
                            boxAdapter.notifyDataSetChanged();
                            rp.setLcount(-1);
                        }
                        QMUtil.getInstance().showToast2(mContext, rp.getName() + "" + getString(R.string.no_response));
                    }

                    @Override
                    public void cancel() {

                    }
                });
            }
        }else{
            // 密码验证如果错了的话，提示密码错误，进度条消失，并且在此弹出密码输入框
            rp.setPlay(false);
            QMUtil.getInstance().showToast(mContext, R.string.error_password);
            rp.setProgress(false);
            boxAdapter.notifyDataSetChanged();
            showPasswdDialog(rp);
        }
        pass_input = "";// 将手动输入的密码数据清空
    }

    /**
     * 显示输入密码的对话框
     */

    private void showPasswdDialog(final RootPoint rp){
        new PassWDValidate(MoreScreenActivity.this).showPassWDInput(new PassWDCallBack(){
            @Override
            public void callBack(String inputContent){
                rp.setPassword(inputContent + "");
                pass_input = inputContent + "";
                passwdVlidate(rp, inputContent);
            }
        });
    }


    /**
     * 10002端口300指令请求远程控制的监听，与lancher交互的
     */
    @Override
    public void controlLancher(RootPoint rootPoint){
//        RootPoint rp = QMDevice.getInstance().getSameRootPoint(rootPoint);
//        if (rp == null)
//            return;
//        QMUtil.getInstance().closeDialog();
//        shareResponse = false;
//        mHandler.removeCallbacks(share);
        //       if ("true".equals(rootPoint.getEnablecontrol())) {// 如果可以进行远程控制就跳到控制的UI中
        Intent intent = new Intent(mContext, ControlActivity.class);
        intent.putExtra("rootPoint", rootPoint);
        startActivityForResult(intent, code1);
//        } else {
//            QMUtil.getInstance().showToast(MoreScreenActivity.this.mContext, R.string.unable_yk);
//        }
    }

    /**
     * 10002端口800指令判断盒子那边的屏幕投屏是否开启的监听与lancher交互的
     */
    @Override
    public void screenOpenLancher(RootPoint rootPoint){
        DebugLog.showLog(mContext,"收到800："+rootPoint.getAddress());
        RootPoint rp = QMDevice.getInstance().getSameRootPoint(rootPoint);
        if (rp == null)
            return;
        // 屏幕投屏如果打开了
        if("true".equals(rootPoint.getIsrunning())){
            if (TorS == 1) {// 投屏操作发送的请求
                endStop = false;
                // 有密码的设备先进行密码连接请求。监听接口connect（）,与盒子交互的
                if (rp.getEnablepwd().equals("true")){
                    CommandExecutorBox.getOnlyExecutor().connectMessage(QMCommander.TYPE_SET_CLIENT_MSG, rootPoint.getAddress(), rp.getPassword());
                } else {
                    rp.setStopByHandle(true);
                    // 没有密码的话就发送投屏播放的指令，监听接口为toPlay（） 与盒子交互的；
                    CommandExecutorBox.getOnlyExecutor().connectMessage(QMCommander.TYPE_START_PLAY, rootPoint.getAddress(), null);
                }

            } else if (TorS == 2){// 分享操作发送的请求直接进入分享的UI
                // 有密码的设备先进行密码连接请求。监听接口connect（）,与盒子交互的
                if (rp.getEnablepwd().equals("true")) {
                    CommandExecutorBox.getOnlyExecutor().connectMessage(QMCommander.TYPE_SET_CLIENT_MSG, rootPoint.getAddress(), rp.getPassword());
                } else {
                    CommandExecutorBox.getOnlyExecutor().connectMessage(QMCommander.TYPE_SET_CLIENT_MSG, rootPoint.getAddress(), null);
                }
            }
        }else{// 屏幕投屏如果没有打开
            switch (TorS){
                case 1:
                    rp.setResponse(true);
                    // 开始选择投屏的个数是1的情况
                    if (QMDevice.getInstance().selectDeviceCount() == 1)
                        QMUtil.getInstance().closeDialog();

                    // 开始选择投屏的个数大于1的情况，没有打开屏幕投屏的设备勾选状态取消
                    if (QMDevice.getInstance().selectDeviceCount() > 1) {
                        rp.setPlay(false);
                        boxAdapter.notifyDataSetChanged();
                        rp.setLcount(-1);
                    }
                    // 已经投屏的时候，再次启用其他盒子投屏
                    if (QMDevice.getInstance().hasScreenDevice()) {
                        rp.setPlay(false);
                        boxAdapter.notifyDataSetChanged();
                        rp.setLcount(-1);
                        QMUtil.getInstance().closeDialog();
                    }
                    break;
                case 2:
                    shareResponse = true;
                    QMUtil.getInstance().closeDialog();
                    mHandler.removeCallbacks(share);
                    break;
            }
            QMUtil.getInstance().showToast2(mContext, rootPoint.getName() + getResources().getString(R.string.not_open));
        }
    }

    /**
     * 10002端口801指令密码修改的监听 与lancher的交互
     */
    @Override
    public void passwdAlterLancher(RootPoint rootPoint){
        RootPoint rp = QMDevice.getInstance().getSameRootPoint(rootPoint);
        if (rp == null || rp.getdType() != -1)
            return;
        rp.setEnablepwd(rootPoint.getEnablepwd());
        Collocation.getCollocation().savePassWd(rp.getUuid(), "");
        if (rp.getEnablepwd().equals("true") && rp.isPlay()) {// 勾选状态
            // 收到密码为true的情况
            // 取消勾选
            QMUtil.getInstance().showToast2(mContext, rp.getName() + getResources().getString(R.string.password_changed));
            rp.setLcount(-1);
            rp.setPlay(false);
            boxAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void controlHeartBeatLancher(RootPoint rootPoint){

    }

    // ============10002端口======Lancher==========end==============

    // ============10001端口======Box==========start==============

    /**
     * 10001端口连接Box的监听 ，box
     */
    @Override
    public void connectBox(RootPoint rootPoint){
        RootPoint rp = QMDevice.getInstance().getSameRootPoint(rootPoint);
        DebugLog.showLog(mContext,"信令："+rootPoint.getCategory()+"rp:"+rp);
        if (rp == null)
            return;
        if (TorS == 2){
            // 分享连接成功的处理
            shareResponse = true;
            mHandler.removeCallbacks(share);
            QMUtil.getInstance().closeDialog();
            if (!rootPoint.isCanShare()){
                QMUtil.getInstance().showToast(mContext, R.string.seek);
                return;
            }
            Intent intent = new Intent(mContext, ScreenShareActivity.class);
            intent.putExtra("rootPoint", rootPoint);
            // intent.putExtra("shareScreen",isScreenShare);
            isScreenShare=false;
            //startActivity(intent);
            startActivityForResult(intent, code1);
            return;
        }

        // 投屏连接成功的处理
        endStop = false;
        rp.setStopByHandle(true);
        // 设备连接成功就发送投屏播放的指令，监听接口为toPlay（） 与盒子交互的；
        CommandExecutorBox.getOnlyExecutor().connectMessage(QMCommander.TYPE_START_PLAY, rootPoint.getAddress(), null);
    }

    /**
     * Box端主动投屏过来手机需要有个tip显示表示屏幕滑动过了，否则投屏不上
     */
    @Override
    public void playBox(RootPoint rootPoint){
        if (QMCommander.TYPE_MOUSE_MODEL == rootPoint.getCategory())
            QMUtil.getInstance().showToast(mContext, R.string.tp_c2);
    }

    /**
     * 10001端口盒子退出屏幕投屏的监听 注意能收到3000的响应是次盒子投屏过了的，在盒子左侧列表有显示的
     */
    @Override
    public void exitBox(RootPoint rootPoint){
        RootPoint rp = QMDevice.getInstance().getSameRootPoint(rootPoint);
        if (rp == null || !rp.isTouPing() || !QMDevice.getInstance().hasScreenDevice())// 系统不在投屏状态收到盒子退出屏幕投屏的消息不做处理
            return; // 盒子没有投屏，收到盒子退出屏幕投屏的消息也不做处理(这种情况是其他手机盒子投屏了，解码端盒子
        // 盒子退出时发的消息
        exitTP(rp, -1, R.string.exception);
    }

    // 100心跳接口
    @Override
    public void heartBeatBox(RootPoint rootPoint){
        if (!QMDevice.getInstance().hasScreenDevice()){
            QMUtil.getInstance().closeDialog();
            openNotification(getResources().getString(R.string.tp_now));// 启动通知栏消息
            getHome();// 让程序在后台运行
        }

        // 改变底部【投屏】按钮的状态
//        if (tp.getBackground() != getResources().getDrawable(R.drawable.tp_)){
//            ll_tp.setBackground(null);
//            tp.setBackgroundResource(R.drawable.tp_);
//            tv.setPressed(true);
//            tv.setTextColor(getResources().getColor(R.color.textselector_));
//
//        }
    }

    /**
     * 10001端口接收300的监听，判断盒子有没有投屏上的
     */
    @Override
    public void screenSuccessBox(RootPoint rootPoint){
        DebugLog.showLog(this,"投屏成功："+rootPoint.getAddress());
        RootPoint rp = QMDevice.getInstance().getSameRootPoint(rootPoint);
        String player = rootPoint.getPlayurl();// player为空没有投上。不为空此设备投屏成功
        String identity = rootPoint.getIdentity();// identity与自己的相等表示不是此手机投屏收到的消息
        if (rp == null || identity.equals(""))// 返回数据中设备唯一标示的字段为空直接忽略
            return;
        // 收到投屏失败信号
        if (player.equals("")){
            // 此手机发送的投屏请求得到的响应 -----当前设备投屏失败
            if (identity.equals(NetworkUtils.getNetworkMac()))
                tpFail(rp);
                // 不是是此手机发送的投屏请求得到的响应------其他设备投屏失败
            else
                tpFailOther(rp);
        }
        // 收到投屏成功信号
        else{
            // a.是此手机发送的投屏请求得到的响应 ----当前设备投屏成功
            if (player.equals(NetworkUtils.getNetworkMac()))
                tpSuccess(rp);
                // b1.不是此手机发送的投屏请求得到的响应----其他设备投屏覆盖
            else
                tpSuccessOther(rp);
        }
    }

    @Override
    public void screenCoverBox(RootPoint rootPoint){
        RootPoint rp = QMDevice.getInstance().getSameRootPoint(rootPoint);
        if (rp == null || !rp.isTouPing() || !QMDevice.getInstance().hasScreenDevice())// 系统不在投屏状态收到盒子退出屏幕投屏的消息不做处理
            return;
        exitTP(rp, -1, R.string.tp_cut);
    }

    /**
     * 不是是此手机发送的投屏请求得到的响应------其他设备投屏失败 会影响当前已投屏设备
     *
     * @param rp
     */
    private void tpFailOther(RootPoint rp) {
        if (rp.isTouPing()) {// 如果此设备在投屏的话，能够收到此消息，则另外手机投屏是失败了
            // 会影响当前已投屏设备 盒子端会显示"点播超时"的提示
            QMUtil.getInstance().showToast2(mContext, rp.getName() + getResources().getString(R.string.tp_interrupt));
            rp.setTouPing(false);
            otherBoxTP(rp);
        }
    }

    /**
     * 此手机发送的投屏请求得到的响应 -----当前设备投屏失败
     *
     * @param rp
     */
    private void tpFail(RootPoint rp){
        if (endStop) {// 如果是自己手动停止投屏 发送1003指令也会收到此消息。忽略！
            return;
        }
        QMUtil.getInstance().closeDialog();
        rp.setTouPing(false);
        rp.setResponse(true);
        QMUtil.getInstance().showToast(mContext, R.string.exception3);
        otherBoxTP(rp);
    }

    /**
     * 不是此手机发送的投屏请求得到的响应---投屏覆盖
     *
     * @param rp
     */
    private void tpSuccessOther(RootPoint rp){
        if (rp.isTouPing()) {// 此设备正在投屏却收到其他设备投屏的消息，是盒子屏幕被其他设备投屏覆盖的原因
            QMUtil.getInstance().showToast(mContext, R.string.tp_cut);
            rp.setTouPing(false);
            otherBoxTP(rp);
        }

    }

    /**
     * 投屏成功,并且是此手机发送的投屏请求得到的响应
     *
     * @param rp
     */

    private void tpSuccess(RootPoint rp){
        // 在此之前没有其他任盒在投屏
        if (!QMDevice.getInstance().hasScreenDevice()){
            // 改变底部【投屏】按钮的状态
              screenState.setText(getResources().getString(R.string.close_));
//            ll_tp.setBackground(null);
//            tp.setBackgroundResource(R.drawable.tp_);
//            tv.setPressed(true);
//            tv.setTextColor(getResources().getColor(R.color.textselector_));
            //   screenCast.setBackground(getResources().getDrawable(R.color.colorPrimary));
            openNotification(getResources().getString(R.string.tp_now));// 启动通知栏消息
        }

        TimerCloseDialog();
        // 将成功投上的盒子的状态更改为true
        if(!rp.isTouPing()){
            rp.setResponse(true);
            rp.setTouPing(true);
            rp.setHeartbeat(0);
            QMUtil.getInstance().closeDialog();
            QMUtil.getInstance().showToast(mContext, R.string.tp_success);
            if(QMUtil.getInstance().isForeground(MoreScreenActivity.this, ACTION_BUTTON)||isResume){
                //   if(rp.getdType()!=2){
                getHome();// 让程序在后台运行
                //  }
            }else{
                DebugLog.showLog(mContext,"不在前台");
            }


        }
    }

    private void TimerCloseDialog(){
        new Timer().schedule(new TimerTask(){
            @Override
            public void run(){
                QMUtil.getInstance().closeDialog();
            }
        }, 3000);
    }

    /**
     * 还有没有其他设备在投屏 在的话取消当前设备勾选 没有的话取消通知栏及恢复投屏按钮
     *
     * @param rp
     */
    public void otherBoxTP(RootPoint rp){
        if (QMDevice.getInstance().hasScreenDevice()){
            rp.setPlay(false);
            rp.setLcount(-1);
            boxAdapter.notifyDataSetChanged();
        } else {
            closeState();
            backToMoreScreenActivity();
        }
    }

    // ============10001端口======Box==========end==============

    // ============10004端口======手机间的搜索监听==========start==============

    /**
     * 搜索设备
     */
    @Override
    public void searchMobile(RootPoint rootPoint){
        searchLancher(rootPoint);
    }

    /**
     * 投屏消息的处理
     */

    @Override
    public void screenMobile(RootPoint rootPoint){

        DebugLog.showLog(mContext,"信令："+rootPoint.getCategory());

        if(QMCommander.TYPE_SCREEN_SCREEN_SUCCESS==rootPoint.getCategory()){
            return;
        }

        if(QMCommander.TYPE_NO_SCREEN_M==rootPoint.getCategory()){

            Toast.makeText(mContext,rootPoint.getName()+"没有可以分享的播放源！", Toast.LENGTH_SHORT).show();
            return;
        }


        if (QMCommander.TYPE_REFUSED_M == rootPoint.getCategory()){
            Toast.makeText(mContext, rootPoint.getName() + "拒绝了您的分享请求", Toast.LENGTH_LONG).show();
            return;
        }

        // 收到其他手机的提示信息,弹出提示框，需要进行投屏(收到对方分享的指令108000)
        RootPoint p=QMDevice.getInstance().getSameRootPoint(rootPoint);

        if (QMCommander.TYPE_NEED_SHARED == rootPoint.getCategory()){
            //showFXDialog(rootPoint);
            //  if(shareScreenSuccess(rootPoint)){
            //  Toast.makeText(mContext,"没有播放源，无法分享！",Toast.LENGTH_SHORT).show();

            CommandExectuorMobile.getOnlyExecutor().sendconnectMessage(QMCommander.TYPE_NO_SCREEN_M, rootPoint.getAddress());

//             }else{
//                QMUtil.getInstance().showProgressDialog(mContext,R.string.share);
//
//             }
            return;
        }

        // 忽略掉收到自己发送的广播
        if (rootPoint.getAddress().equals(NetworkUtils.getNetworkIP()))
            return;

        // 手机收到来自其他手机的投屏请求 ---进入分享界面
        if (rootPoint.isClient()){
            // 此手机不在主页在其他页面做操作时 或者 手机在投屏状态 则不允许其他手机投屏上来
            if (QMDevice.getInstance().hasScreenDevice() || !QMUtil.getInstance().isForeground(MoreScreenActivity.this, ACTION_BUTTON)) {
                // ---发送消息给投屏的手机
                CommandExectuorMobile.getOnlyExecutor().sendconnectMessage(QMCommander.TYPE_FORBIDDEN_M, rootPoint.getAddress());
                return;
            }
            // rootPoint.setdType(0);// 区分分享的是手机还是任盒
            Intent intent = new Intent(mContext, ScreenShareActivity.class);
            intent.putExtra("rootPoint", rootPoint);
            intent.putExtra("shareScreen",isScreenShare);
            isScreenShare=false;
            startActivityForResult(intent, code1);
        }
        // 手机投屏成功后得到的响应要做的处理 ----手机投屏手机成功
        else{
            // 改变底部【投屏】按钮的状态
            RootPoint rp = QMDevice.getInstance().getSameRootPoint(rootPoint);
            if (rp == null)
                return;
            if (!rp.isTouPing()){
                tpSuccess(rp);// 手机间的投屏成功的处理
            }
        }
    }

    /**
     *
     *
     * */
    int time;
    int type = -1;

    private void showFXDialog(final RootPoint rp) {
        // 弹出dialog，进行输出
        //解决重复弹框的问题
        if (time != 0) {
            return;
        }
        time = 10;
        View vv = View.inflate(mContext, R.layout.app_tip, null);
        ((TextView) vv.findViewById(R.id.title)).setText("是否允许" + rp.getName() + "分享您的屏幕？");
        Button comfirm = (Button) vv.findViewById(R.id.comfirm);
        comfirm.setText("接受");
        final Button cancel = (Button) vv.findViewById(R.id.cancel);
        cancel.setText("拒绝");

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext, R.style.MyDialognotitle);
        final AlertDialog alDialog = alertDialog.create();
        alDialog.setCancelable(false);
        alDialog.show();
        alDialog.getWindow().setContentView(vv);
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
               runOnUiThread(new Runnable(){

                    @Override
                    public void run(){
                        // TODO Auto-generated method stub
                        time = time - 1;
                        cancel.setText("拒绝(" + time + "S)");
                        if (time == 1) {
                            // 发送一个指令提示对方被拒绝了
                            if (rp.getdType() == 2) {
                                CommandExecutorLancher.getOnlyExecutor().connectPcMessage(QMCommander.TYPE_SHARED_REFUSED, rp.getAddress());
                            } else {
                                CommandExectuorMobile.getOnlyExecutor().connectMessage(QMCommander.TYPE_REFUSED_M, rp.getAddress());
                            }
                            timer.cancel();
                            time = 0;
                            if (alDialog != null)
                                alDialog.dismiss();
                        }
                    }
                });

            }
        }, 1000, 1000);


        alDialog.getWindow().setLayout(getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().widthPixels);
        comfirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (shareScreenSuccess(rp)) return;
                timer.cancel();
                time = 0;
                if (alDialog != null)
                    alDialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (rp.getdType() == 2) {
                    CommandExecutorLancher.getOnlyExecutor().connectPcMessage(QMCommander.TYPE_SHARED_REFUSED, rp.getAddress());
                } else {
                    CommandExectuorMobile.getOnlyExecutor().connectMessage(QMCommander.TYPE_REFUSED_M, rp.getAddress());
                }
                timer.cancel();
                time = 0;
                if (alDialog != null)
                    alDialog.dismiss();
            }

        });
    }

    private boolean shareScreenSuccess(RootPoint rp) {
        if (Build.VERSION.SDK_INT < 21) {
            QMUtil.getInstance().showToast(mContext, R.string.version_higher);
            return true;
        }
        if (!RecordManager.getRecordManager().supportScreen) {
            QMUtil.getInstance().showToast(mContext, R.string.version_self);
            return true;
        }
        // 当此设备正在投屏盒子的时候需要关闭
        if (QMDevice.getInstance().hasScreenDevice() && QMDevice.getInstance().hasBoxScreen()) {
            closeTP();
        }
        int type = -1;
        switch (rp.getdType()) {
            case 2:
            case -1:
                type = 0;
                break;
            case 0:
            case 1:
                type = 1;
                break;
        }
        // 进行投屏
        if (RecordManager.canT) {// 手机在截屏
            if (RecordManager.getRecordManager().typeT != type)// 但投屏的类型与截屏分辨率不一致
            {
                RecordManager.getRecordManager().endrecord();// 先停止之前的截屏
                RecordManager.getRecordManager().typeT = type;
                RecordManager.getRecordManager().startrecord(null,this);
            }
        } else {// 程序第一次进来的时候弹出的提示框若点击了“取消”,则要投屏的话这个提示框还要弹出
            RecordManager.getRecordManager().typeT = type;
            RecordManager.getRecordManager().startrecord(null,this);
        }
        if (rp.getdType() == 2){
            CommandExecutorLancher.getOnlyExecutor().connectPcMessage(QMCommander.TYPE_PCTP, rp.getAddress());
        } else {
            CommandExectuorMobile.getOnlyExecutor().connectMessage(QMCommander.TYPE_SCREEN_M, rp.getAddress());
        }
        return false;
    }

    /**
     * 断开投屏的处理
     */

    @Override
    public void screenInterruptMobile(RootPoint rootPoint){
        if(rootPoint.getCategory()==QMCommander.TYPE_STOP_SHARE_SCREEN){
            return;
        }
        // 忽略掉收到自己发送的广播
        if (rootPoint.getAddress().equals(NetworkUtils.getNetworkIP()))
            return;
        RootPoint rp = QMDevice.getInstance().getSameRootPoint(rootPoint);
        if (rp == null)
            return;
        QMUtil.getInstance().closeDialog();
        if (QMCommander.TYPE_FORBIDDEN_M == rootPoint.getCategory()){
            rp.setResponse(true);
            QMUtil.getInstance().showToast2(mContext, rp.getName() + getResources().getString(R.string.tp_forbidde));
            rp.setPlay(false);
            return;
        }

        if (rp.isTouPing()){// 是投屏状态才处理下面的断开情况
            rp.setTouPing(false);
            if (QMDevice.getInstance().hasScreenDevice()){
                rp.setPlay(false);
                boxAdapter.notifyDataSetChanged();
            }else{
                closeState();
                backToMoreScreenActivity();
            }

            if (QMCommander.TYPE_COVER_M == rootPoint.getCategory()) {
                QMUtil.getInstance().showToast2(mContext, getResources().getString(R.string.tp_cut));
            } else {
                QMUtil.getInstance().showToast2(mContext, rp.getName() + getResources().getString(R.string.exit_shared));
            }
        }
    }

    /**
     * 心跳检测
     */
    @Override
    public void heartBeatMobile(RootPoint rootPoint){
        //忽略掉收到自己发送的广播
        if (rootPoint.getAddress().equals(NetworkUtils.getNetworkIP()))
            return;
        //在这里进行了连接的判断,解决了不断跳出投屏成功的问题
        if (rootPoint.isTouPing()){
            heartBeatBox(rootPoint);
        }

    }


    /**
     * @param mRootPoint
     * 切换视频源
     */
    public void swichMobileScreen(RootPoint mRootPoint) {

    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void requestScreenFrame(){

        RecordManager.getRecordManager().requestRate();

    }

    /**
     * 发送投屏pc返回的信息
     */
    @Override
    public void touPingPc(RootPoint rootPoint){
        DebugLog.showLog(mContext,"touPingPc信令:"+rootPoint.getCategory());
        // TODO Auto-generated method stub
        RootPoint rp = QMDevice.getInstance().getSameRootPoint(rootPoint);
        if (rp == null)
            return;
        if(!rp.isTouPing()){
            tpSuccess(rp);// 手机间的投屏成功的处理
        }
    }

    // ============10004端口======手机间的搜索监听==========end==============
    @Override
    public void stopPc(RootPoint rootPoint){

        DebugLog.showLog(mContext,"stopPc信令:"+rootPoint.getCategory());
        // 忽略掉收到自己发送的广播
        if (rootPoint.getAddress().equals(NetworkUtils.getNetworkIP()))
            return;
        RootPoint rp = QMDevice.getInstance().getSameRootPoint(rootPoint);
        if (rp == null)
            return;
        QMUtil.getInstance().closeDialog();

        if (rp.isTouPing()){// 是投屏状态才处理下面的断开情况
            rp.setTouPing(false);
            if (QMDevice.getInstance().hasScreenDevice()){
                rp.setPlay(false);
                boxAdapter.notifyDataSetChanged();
            }else{
                closeState();
                backToMoreScreenActivity();
            }

        }
    }

    @Override
    public void pcTouPing(RootPoint rootPoint){

        if(QMCommander.TYPE_PC_SHARED==rootPoint.getCategory()){
            //QMUtil.getInstance().showToast2(mContext,"没有播放源，无法分享");
            CommandExecutorLancher.getOnlyExecutor().connectPcMessage(QMCommander.TYPE_PCSHARED_REFUSED,rootPoint.getAddress());
            return;
        }

        DebugLog.showLog(mContext,"pcTouPing信令:"+rootPoint.getCategory());
        if (QMCommander.TYPE_PCSHARED_REFUSED == rootPoint.getCategory()){
            QMUtil.getInstance().showToast2(mContext, rootPoint.getName()+"没有可以分享的播放源!");
            return;
        }

        if(QMCommander.TYPE_SCREEN_REFUSED == rootPoint.getCategory()){
            // QMUtil.getInstance().showToast2(mContext, rootPoint.getName() + "拒绝了您的投屏请求");
            QMUtil.getInstance().showToast2(mContext, rootPoint.getName() + getResources().getString(R.string.tp_forbidde));
            QMUtil.getInstance().closeDialog();
            RootPoint rp=QMDevice.getInstance().getSameRootPoint(rootPoint);
            rp.setResponse(true);
            return;
        }

        if (rootPoint.getCategory() == QMCommander.TYPE_PC_STOP){
            QMUtil.getInstance().closeDialog();
            RootPoint rp=QMDevice.getInstance().getSameRootPoint(rootPoint);
            rp.setResponse(true);
            if(rp.isTouPing()){
                exitTP(rp,rp.getdType(), R.string.exception);
            }
            return;
        }
//        else if (rootPoint.getCategory() == QMCommander.TYPE_PC_SHARED) {
//            //收到pc端的分享指令
//            rootPoint.setdType(2);
//            showFXDialog(rootPoint);
//        }
        else{
            if (QMDevice.getInstance().hasScreenDevice() || !QMUtil.getInstance().isForeground(MoreScreenActivity.this, ACTION_BUTTON)) {
                // ---发送消息给投屏的手机
                CommandExecutorLancher.getOnlyExecutor().connectPcMessage(QMCommander.TYPE_SCREEN_REFUSED , rootPoint.getAddress());
                return;
            }
            DebugLog.showLog(mContext,"收到的信令:"+rootPoint.getCategory());
            rootPoint.setdType(2);// 区分分享的是手机还是任盒
            Intent intent = new Intent(mContext, ScreenShareActivity.class);
            intent.putExtra("rootPoint", rootPoint);
            if(TorS==2){
                intent.putExtra("shareScreen", true);
            }
            startActivityForResult(intent, code1);
        }

    }

    @Override
    public void pcCoverShare(RootPoint rp) {

    }

    private void initWindowPermission(){

        if ("Xiaomi".equals(Build.MANUFACTURER)) {
            //小米手机
            DebugLog.showLog(mContext, "小米手机");
            requestPermission("小米");
        } else if ("Meizu".equals(Build.MANUFACTURER)) {
            //魅族手机
            DebugLog.showLog(mContext, "魅族手机");
            requestPermission("魅族");
        } else {//其他手机
            DebugLog.showLog(mContext, "其他手机");
        }
    }

    /**
     * 判断悬浮窗权限
     *
     * @param context
     * @return
     */

    public static boolean isFloatWindowOpAllowed(Context context){
        final int version = Build.VERSION.SDK_INT;
        if (version >= 19) {
            return checkOp(context, 24);  // AppOpsManager.OP_SYSTEM_ALERT_WINDOW
        } else {
            if ((context.getApplicationInfo().flags & 1 << 27) == 1 << 27) {
                return true;
            } else {
                return false;
            }
        }
    }


    public static boolean checkOp(Context context, int op){

        final int version = Build.VERSION.SDK_INT;

        if (version >= 19) {
            AppOpsManager manager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            try {
                Class<?> spClazz = Class.forName(manager.getClass().getName());
                Method method = manager.getClass().getDeclaredMethod("checkOp", int.class, int.class, String.class);
                int property = (Integer) method.invoke(manager, op,
                        Binder.getCallingUid(), context.getPackageName());
                Log.e("399", " property: " + property);

                if (AppOpsManager.MODE_ALLOWED == property) {
                    return true;
                } else {
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.e("399", "Below API 19 cannot invoke!");
        }
        return false;
    }


    /**
     * 请求用户给予悬浮窗的权限
     */
    public void requestPermission(String name){
        if (isFloatWindowOpAllowed(mContext)) {//已经开启
            //    switchActivity();
            DebugLog.showLog(mContext, "已经获取到权限");
        } else {
            AlertDialog.Builder alert = new AlertDialog.Builder(mContext, R.style.window_dialog_style);
            final Dialog d = alert.create();
            d.show();
            d.getWindow().setContentView(R.layout.window_permiss_dialog);
            TextView diviceName = (TextView) d.findViewById(R.id.window_permiss_divice);
            TextView diviceWay = (TextView) d.findViewById(R.id.window_permiss_way);
            TextView cancel = (TextView) d.findViewById(R.id.window_permiss_cancel);
            TextView setting = (TextView) d.findViewById(R.id.window_permiss_setting);
            diviceName.setText("您所使用的是" + name + "设备须在：");
            if (name.equals("魅族")) {
                diviceWay.setText("应用信息>>权限管理>>应用>>点击桌面悬浮窗 ");
            }
            cancel.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {

                    Toast.makeText(mContext, "取消", Toast.LENGTH_SHORT).show();
                    d.dismiss();
                }
            });

            setting.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {

                    Toast.makeText(mContext, "设置", Toast.LENGTH_SHORT).show();
                    d.dismiss();
                    openSetting();
                }
            });
        }
    }


    /**
     * 打开权限设置界面
     */
    public void openSetting(){
        try {
            Intent localIntent = new Intent(
                    "miui.intent.action.APP_PERM_EDITOR");
            localIntent.setClassName("com.miui.securitycenter",
                    "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
            localIntent.putExtra("extra_pkgname", mContext.getPackageName());
            startActivityForResult(localIntent, 11);
            Toast.makeText(mContext, "请您在权限管理中将浮窗的权限打开！", Toast.LENGTH_SHORT).show();
            DebugLog.showLog(mContext, "启动小米悬浮窗设置界面");
        } catch (ActivityNotFoundException localActivityNotFoundException) {
            Intent intent1 = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", mContext.getPackageName(), null);
            intent1.setData(uri);
            startActivityForResult(intent1, 11);
            Toast.makeText(mContext, "请您在权限管理中将浮窗的权限打开！", Toast.LENGTH_SHORT).show();
            DebugLog.showLog(mContext, "启动悬浮窗界面");
        }
    }

    private class  FileBroadCast  extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent){

            if(intent.getAction().equals(FTPServerService.FILE_SEND_FINISH)){
                DebugLog.showLog(this,"收到广播!");
                ArrayList<String> files = intent.getStringArrayListExtra("FILES");
                // String friendName= intent.getStringExtra("friendName");
                DebugLog.showLog(this,"收到文件files："+files.toString());
                String friendAddress= intent.getStringExtra("friendAddress");
                receiveFile(files, friendAddress);
            }
        }
    }

    private void receiveFile(ArrayList<String> files, String friendAddress){
        if(files!=null){
            RootPoint root= getRootRecieveFile(friendAddress);
            DebugLog.showLog(this,"root:"+root);
            getDownLoadFile(files,root);
        }
    }

    private RootPoint getRootRecieveFile(String friendAddress){
        ArrayList<RootPoint> roots= QMDevice.getInstance().getAl();
        DebugLog.showLog(this,"收到address："+friendAddress);
        for (RootPoint root : roots){
            if(root.getAddress().equals(friendAddress)){
                root.setIsfileRecieve(true);
                return root;
            }
        }
        return  null;
    }


    /**
     * 加载从任盒或手机中下载的文件
     */
    private void getDownLoadFile(ArrayList<String> fileNames, RootPoint point){
        downLoadFiles=new ArrayList<QMLocalFile>();
        File root=new File(QMFileType.LOCALPATH);
        File[] files=root.listFiles();
        for (File file:files){
            if(file.isDirectory()){
                continue;
            }else{
                String name = file.getName();
                for (String fileName : fileNames){
                    if(name.equals(fileName)){
                        long size=file.length();
                        if(size==0)
                            continue;
                        int type=QMFileType.getType(file.getName());
                        String path = file.getPath();
                        long update=file.lastModified();
                        QMLocalFile localFile=new QMLocalFile(name,path, size, update, type);
                        downLoadFiles.add(localFile);
                    }
                }
            }
        }
        DebugLog.showLog(this,"downLoadFile:"+downLoadFiles.toString());
        Message msg= new Message();
        msg.obj=point;
        msg.what=0x001;
        Bundle bundle=new Bundle();
        bundle.putSerializable("down",downLoadFiles);
        msg.setData(bundle);
        handler.sendMessage(msg);
    }


    private  void showSendFileDalog(String friendName, ArrayList<QMLocalFile> files){
        if(files==null){
            files=new ArrayList<QMLocalFile>();
        }
        if(filedialog!=null&&filedialog.isShowing()){
            filedialog.dismiss();
        }
        filedialog= new FileDialog(mContext);
        ViewGroup d=filedialog.getView();
        filedialog.setCancelable(false);
        if(NetworkUtils.isNetworkConnected()){
            filedialog.show();
        }
        RecyclerView fileList= (RecyclerView)d.findViewById(R.id.recievefile);
        isFileOpen=(ImageView)  d.findViewById(R.id.is_open);
        if(Collocation.getCollocation().getFileOpen()){
            isFileOpen.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.bt_1));
        }else{
            isFileOpen.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.bt_2));
        }
        ImageView close_dialog=(ImageView) d.findViewById(R.id.close_file_dialog);
        adapter = new FileRecieveAdapter(friendName,mContext,files,this);
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        fileList.setLayoutManager(linearLayoutManager);
        fileList.setAdapter(adapter);
        isFileOpen.setOnClickListener(this);
        close_dialog.setOnClickListener(this);
    }


    @Override
    public void onItemClick(ArrayList<QMLocalFile> files, int position){
//            if(filedialog!=null){
//                filedialog.dismiss();
//            }
        openFile(files,position);
        if(files.size()==0&&filedialog!=null){
            filedialog.dismiss();
        }
    }

    @Override
    public void mainOnclick(String name, ArrayList<QMLocalFile> files){
        showSendFileDalog(name, files);
    }

    @Override
    public void OnScrenShare(RootPoint rootPoint){
        if(rootPoint==null){
            return;
        }
        // Toast.makeText(mContext,rootPoint.getName()+"开始分享了!",Toast.LENGTH_SHORT).show();
        TorS = 2;
        DebugLog.showLog(mContext,"开始点击分享...");
        if(rootPoint.getdType() == -1){
            CommandExecutorLancher.getOnlyExecutor().controlMessage(QMCommander.SCREEN_OPEN, rootPoint.getAddress());
        }
        else if (rootPoint.getdType() == 0||rootPoint.getdType()==1){
            CommandExectuorMobile.getOnlyExecutor().connectMessage(QMCommander.TYPE_NEED_SHARED, rootPoint.getAddress());
        }else{
            CommandExecutorLancher.getOnlyExecutor().connectPcMessage(QMCommander.TYPE_PC_SHARED, rootPoint.getAddress());
        }
        shareResponse = false;
    }

    @Override
    public void OnLookShareFile(RootPoint rootPoint){
        Toast.makeText(mContext,"开始查看"+rootPoint.getName()+"的共享文件了!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();
        intent.putExtra("rootpoint",rootPoint);
        intent.setClass(mContext,RemouteFileShareActivity.class);
        startActivity(intent);
    }

    @Override
    public void OnClickSelete(RootPoint rootPoint, int position){
               onItem(position);
    }

    private void  openFile(ArrayList<QMLocalFile> files, int postion){
        if(files!=null){
            if(!isOpeanfile){
                isOpeanfile = true;
                startActivity(OpenFileUtil.openFile(this, files.get(postion).getPath()));
                files.remove(postion);
                if(adapter!=null){
                    adapter.notifyDataSetChanged();
                }
                boxAdapter.notifyDataSetChanged();
            }
        }

    }

    //超时就直接发送退出的消息
    private void stopTp(RootPoint rootPoint){

        for (int i = 0; i < 3; i++) {
            if (rootPoint.getdType() == -1) {
                rootPoint.setStopByHandle(false);
                // 已经进行投屏的盒子全部发送停止投屏的指令
                CommandExecutorBox.getOnlyExecutor().connectMessage(QMCommander.TYPE_STOP_PLAY, rootPoint.getAddress(), null);
            } else if (rootPoint.getdType() == 2) {
                // 发送停止投屏到PC端
                CommandExecutorLancher.getOnlyExecutor().connectPcMessage(QMCommander.TYPE_PC_STOP, rootPoint.getAddress());
            }


            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * 开启文件分享的服务
     */
    private void startFTPService(){
        Intent intent2 = new Intent(MoreScreenActivity.this, FTPServerService.class);
        if (!FTPServerService.isRunning()){
            this.startService(intent2);
        }
    }

    /**
     * 停止文件分享的服务
     */
    private void stopFTPService(){
        Intent intent2 = new Intent(MoreScreenActivity.this, FTPServerService.class);
        if(FTPServerService.isRunning()){
            stopService(intent2);
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        DebugLog.showLog(this,"MoreScreenActivity.this退出了!");
        mHandler.removeCallbacks(searchBox);
        mHandler.removeCallbacks(share);
        mHandler.removeCallbacks(refresh);
        unregisterReceiver(boxRemoveReceiver);// 注销之前注册的广播
        unregisterReceiver(fileBroadCast);
        EventBus.getDefault().unregister(this);
        end();
        closeTP();
        stopTimerService();
        stopFTPService();
        unregisterCommandExecutor();
    }

}

