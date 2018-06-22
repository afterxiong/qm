package com.shareshow.airpc.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shareshow.aide.R;
import com.shareshow.aide.activity.MainActivity;
import com.shareshow.aide.event.ScanEvent;
import com.shareshow.airpc.Collocation;
import com.shareshow.airpc.model.LancherFile;
import com.shareshow.airpc.model.QMLocalFile;
import com.shareshow.airpc.model.RootPoint;
import com.shareshow.airpc.ports.ConnectFTPListener;
import com.shareshow.airpc.share.FileAdapter;
import com.shareshow.airpc.share.Friend;
import com.shareshow.airpc.share.FriendAdapter;
import com.shareshow.airpc.share.SearchDeviceService;
import com.shareshow.airpc.share.SpaceItemDecoration;
import com.shareshow.airpc.socket.common.FTPUtil;
import com.shareshow.airpc.socket.common.QMDevice;
import com.shareshow.airpc.socket.common.UploadFile;
import com.shareshow.airpc.util.DebugLog;
import com.shareshow.airpc.util.NetworkUtils;
import com.shareshow.airpc.util.QMDbUtil;
import com.shareshow.airpc.util.QMFileType;
import com.shareshow.airpc.util.QMUtil;
import com.shareshow.airpc.widget.ProgressWheel;
import com.umeng.analytics.MobclickAgent;
import com.xcg.ScanActivity;
import com.xcg.helper.EventNetworkReceiver;
import com.xcg.helper.NetWorkManager;
import com.xcg.helper.RequestNetworkInfo;
import com.xcg.helper.WifiParams;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import qiu.niorgai.StatusBarCompat;

/**
 * Created by Administrator on 2017/7/4 0004.
 */

public class ShareActivity extends AppCompatActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private ArrayList<Friend> friends=new ArrayList<Friend>();

    private ArrayList<QMLocalFile> files =new ArrayList<QMLocalFile>();

    private final int itemSpace= 10;

    private static final String PACKAGE_NAME = "com.xtxk.airpc";


    private static final int CONNECT_OK = 1002;// 获取文件成功

    private static final int CONNECT_FAILED = 1003;// 获取文件成功

    private static final int SEND_NEXT=1004;  //下一个文件

    private static final int DISMISS_DILOGE=1005; //dialog消失

    private static final int FTP_TIMEOUT=1006;  //超时

    private ArrayList<RootPoint> sendAl=new ArrayList<>();

    private Intent intentService;

    private SearchServiceConnection conn =new SearchServiceConnection();

    private   FriendAdapter friendAdapter;

    private boolean isCancelFinish=true;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            switch (msg.what){
                case CONNECT_OK:
                    shareFile();
                    break;
                case CONNECT_FAILED:
                    DebugLog.showLog(this,"ftp断开连接失败！");
                    break;
                case SEND_NEXT:
                    //发送给下一个好友
                sendAl.remove(0);
                DebugLog.showLog(this,"发送下一个！size:"+sendAl.size());
                sendFileToFriend();
                    break;
                case DISMISS_DILOGE:
                    QMUtil.getInstance().closeDialog();
                    //   if(d.isShowing()){
                    DebugLog.showLog(this,d.isShowing()+" ");
                    d.dismiss();
                    break;
                case  FTP_TIMEOUT:
                    RootPoint rootPoint= (RootPoint) msg.obj;
                    Toast.makeText(ShareActivity.this,"上传失败,请稍后重试！", Toast.LENGTH_SHORT).show();
                    if(d!=null&&d.isShowing()){
                        d.dismiss();
                        if(rootPoint!=null){
                            QMDevice.getInstance().getAl().remove(rootPoint);
                            notifyData();
                            if(QMDevice.getInstance().getSize()==0){
                                setNoBox(Collocation.collocation.getIsScan());
                            }
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    FTPUtil.getInstance(FTPUtil.beforPoint).setDisconnect(new ConnectFTPListener() {
                                        @Override
                                        public void success(){

                                        }

                                        @Override
                                        public void fail(int state){
                                            handler.sendEmptyMessage(CONNECT_FAILED);

                                        }
                                    });
                                }
                            }).start();

                        }
                    }

                    break;
           }
        }
    };
    private TextView send_friend;
    private TextView send_product;
    private ProgressBar bar;
    private TextView gress;
    private TextView backTools;
    private TextView backApp;
    private LinearLayout file_close;
    private LinearLayout file_sending;
    private LinearLayout file_finish;
    private Dialog d;
    private NetworkUtils utils;
    private TextView mode;
    private ImageView code_img;
    private ImageView search_img;
    private TextView search_text;
    private TextView scanCode_text;
    private RequestNetworkInfo request;
   //  private TextView password;
   // private TextView wifiname;
   // private RelativeLayout wifi;
    private RelativeLayout friendList;
   // private LoadingView loading;
    private LinearLayout friend_send;
    private String name;
   // private String psw;
  //  private String netID;
   // private WifiParams scanParms;
   // private TextView wifipsw_line;
  //  private LinearLayout wifipsw;
    //private TextView scan;
   // private boolean isScan;
    private SwipeRefreshLayout swipe_refresh;
    private FileAdapter fileAdapter;
    private boolean isFirstNotNet;
    private String ssid;
    private boolean isScan;
    private String beforeSsid;
    private boolean isCode;
    private boolean isSameDevice;
    private TextView finish_text;
    private int failCount=0;
    private TextView nobox;
    private boolean isScanFinish;
    //private int scanCode=0;
    private boolean isResume =true;
    private Toast toast;
    private String netIp;


    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
     //   if(isPad()){
            setContentView(R.layout.activity_share_l);
            StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this, R.color.colorPrimaryDark));

        //   }
//    else{
//            setContentView(R.layout.activity_share);
//        }
        initService();
        initData();
        initView();
        initNet();
    }

    private void initNet(){
    //    setWifi();
        EventBus.getDefault().register(this);
        request = new RequestNetworkInfo(this);
        NetWorkManager.getManager().registerReceiver(this);
    }

//    private void setWifi(){
//               //TODO  建议开启wifi
//            Toast.makeText(this,"请手动开启WIFI", Toast.LENGTH_SHORT).show();
//            scan.setText("请设置连接到与好友同一wifi！");
//            String text = scan.getText().toString();
//            SpannableString builder = new SpannableString(text);
//            builder.setSpan(new ShareActivity.IntentSpan(new View.OnClickListener(){
//                public void onClick(View v){
//                    startActivityForResult(new Intent(Settings.ACTION_WIFI_SETTINGS),100);
//                }
//            }), 1, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//             scan.setText(builder);
//             scan.setMovementMethod(LinkMovementMethod.getInstance());
//             friend_send.setVisibility(View.GONE);
//             friendList.setVisibility(View.GONE);
//             scan.setVisibility(View.VISIBLE);
//             loading.setVisibility(View.GONE);
//        }


    private void initService(){
        intentService = new Intent(ShareActivity.this,
                SearchDeviceService.class);
        bindService(intentService, conn, Context.BIND_AUTO_CREATE);
    }

    private SearchDeviceService.SearcherBinder binder;

    @Override
    public void onRefresh(){
        boolean isConnected = NetworkUtils.isNetworkConnected();
        // 设置刷新可用和无用|| QMDevice.getInstance().hasScreenDevice()
        if ( !isConnected ||progress.getVisibility()== View.VISIBLE) {
            swipe_refresh.setRefreshing(false);
            return;
        }
        // show_search_error.setVisibility(8);
        // 每次刷新默认所有盒子不在线

        if (!Collocation.getCollocation().getIsScan()){
            QMDevice.getInstance().setOffline();
            showToast("正在搜索中...");
        }

        new Thread(){
            public void run(){
                if (Collocation.getCollocation().getIsScan()){
                    QMDevice.getInstance().removeOffline();
                    runOnUiThread(new Runnable(){
                        @Override
                        public void run(){
                            showToast("请扫码连接或者在设置中调整为自动搜索连接");
                          //  Toast.makeText(getApplicationContext(), , Toast.LENGTH_LONG).show();
                            swipe_refresh.setRefreshing(false);
                            return;
                        }
                    });

                    return;
                }else{
                   if(binder!=null){
                       binder.onSearch();
                   }
                }
            }
        }.start();
       searchFinish();
    }

    private void searchFinish(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run(){
                swipe_refresh.setRefreshing(false);
                }
        }, 3 * 1000);
    }

    public void getWifi(){
       request.setOnWifiParamsListener(new RequestNetworkInfo.OnWifiParamsListener(){
           @Override
           public void callback(WifiParams params){

                ssid=params.getSsid();
               // WifiManager wifimanager = (WifiManager) getSystemService(WIFI_SERVICE);
               // ssid =wifimanager.getConnectionInfo().getSSID();
                DebugLog.showLog(this,"当前的wifi："+ ssid);
           }
       });
  }


    private   class  SearchServiceConnection  implements ServiceConnection,SearchDeviceService.DataListener{


        @Override
        public void onServiceConnected(ComponentName name, IBinder service){
            binder =(SearchDeviceService.SearcherBinder)service;
            binder.setListener(this);
            DebugLog.showLog(this,"connte service");
        }

        @Override
        public void onServiceDisconnected(ComponentName name){


        }

        @Override
        public void SearchSuccess(int state){

            if(progress!=null){

                progress.setVisibility(View.GONE);

            }

            switch (state){
                   case 0:
                       showToast("没有连接网络");
                    //  Toast.makeText(ShareActivity.this ,"没有连接网络",Toast.LENGTH_SHORT).show();
                    break;

                   case 1:

                       if(!Collocation.getCollocation().getIsScan()){

                               if(nobox!=null){
                                   setNoBox(false);
                               }

                          // showToast("未搜索到其它任易屏终端，请确认是否连接在相同的wifi下并开启任易屏！");
                          // Toast.makeText(ShareActivity.this ,"",Toast.LENGTH_SHORT).show();
                       }else{

                           if(nobox!=null){

                               setNoBox(true);

                           }
                       }


                    break;

                   case 2:

                       if(nobox!=null){
                           nobox.setVisibility(View.GONE);
                       }
                       notifyData();

                    break;
              }
        }
    }

    private void notifyData(){

            getFriendData();

        if(friendAdapter!=null){
            friendAdapter.notifyDataSetChanged();
        }

        if(friendView!=null){
            friendView.setVisibility(View.VISIBLE);
        }
     }

    private ArrayList<RootPoint> alFirst ;

    private void getFriendData(){

        alFirst = QMDevice.getInstance().getAl();

        if(friends==null){
            friends= new ArrayList<Friend>();
        }

        friends.clear();

        for (int i = 0; i < alFirst.size(); i++){
            Friend  friend= new Friend();
            friend.setDiviceName(alFirst.get(i).getName());
            friend.setPostion(i);
            friend.setAddress(alFirst.get(i).getAddress());
            friend.setDiviceType(alFirst.get(i).getdType());
            if(Collocation.getCollocation().getIsScan()){
                friend.setSelect(true);
            }
            friends.add(friend);
        }

        DebugLog.showLog(this,"friends:"+friends);

    }

    private void initData(){
         getFileData();

        if(Collocation.getCollocation().getIsScan()){
            notifyData();
            if(QMDevice.getInstance().getSize()==0){
                showToast("请点击扫码连接其它设备！");
            }
        }
    }

    private void getFileData(){
        Intent intent = getIntent();
        String action = intent.getAction();
         String type = intent.getType();
         ComponentName conn=intent.getComponent();
         String name = conn.getPackageName();
        if (Intent.ACTION_SEND.equals(action) && type != null) {
           	files=  handleSendFile(intent);
        }else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
          	files= handleSendMultipleFiles(intent); // Handle multiple images being sent
        }
    }

    private ArrayList<QMLocalFile> handleSendMultipleFiles(Intent intent) {
        ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        ArrayList<QMLocalFile> al=new ArrayList<QMLocalFile>();
        if (imageUris != null) {
            for (int i = 0; i <imageUris.size(); i++){
                Uri imageUri=imageUris.get(i);
                String path=null;
                if(imageUri.toString().startsWith("content:"))
                    path = getRealPathFromURI(this, imageUri);
                else
                    path=imageUri.toString().substring(7, imageUri.toString().length());
                try {
                    path= URLDecoder.decode(path, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    finish();
                }
                File file=new File(path);
                QMLocalFile mMyLocalFile=new QMLocalFile();
                mMyLocalFile.setName(isReme(al,file));
                mMyLocalFile.setPath(path);
                mMyLocalFile.setSize(file.length());
                mMyLocalFile.setUpdate(file.lastModified());
                mMyLocalFile.setType(QMFileType.getType(file.getName()));
                mMyLocalFile.setChoose(true);
               // addLocalFile(al,file);
                al.add(mMyLocalFile);
            }
        }
//        else{
//            showToast("请选择文件进行分享！");
//        }
        return al;
    }


  //文件重命名操作
    private String isReme(ArrayList<QMLocalFile> al, File file) {
        for (QMLocalFile qmLocalFile : al) {
            if(qmLocalFile.getName().equals(file.getName())){
                ArrayList<String> rms=  new ArrayList<String>();
                for (QMLocalFile mFile : al){
                    rms.add(mFile.getName());
                }
              return  UploadFile.ReName(file.getName(),rms);
            }
        }
        return file.getName();
    }

    private ArrayList<QMLocalFile> handleSendFile(Intent intent){
        ArrayList<QMLocalFile> al = new ArrayList<QMLocalFile>();
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        DebugLog.showLog(this,"imageUri:"+imageUri);
        if (imageUri != null){
            String path = null;
            if (imageUri.toString().startsWith("content:"))
                path = getRealPathFromURI(this, imageUri);
            else
                path = imageUri.toString().substring(7, imageUri.toString().length());
            try {
                path = URLDecoder.decode(path, "utf-8");
            } catch (UnsupportedEncodingException e){
                e.printStackTrace();
                finish();
            }
            File file = new File(path);
            QMLocalFile mMyLocalFile = new QMLocalFile();
            mMyLocalFile.setName(file.getName());
            mMyLocalFile.setPath(path);
            mMyLocalFile.setSize(file.length());
            mMyLocalFile.setUpdate(file.lastModified());
            mMyLocalFile.setType(QMFileType.getType(file.getName()));
            mMyLocalFile.setChoose(true);
            al.add(mMyLocalFile);
        }
//        else{
//            showToast("请选择文件进行分享！");
//        }
        return  al;
    }

//    @Override
//    protected void onResume(){
//        super.onResume();
//        getFileData();
//        if(fileAdapter!=null){
//            fileAdapter.notifyDataSetChanged();
//        }
//    }

    /**
     * 分享到共享文件中
     * @param al
     */
    private void share(ArrayList<QMLocalFile> al){

        if(al==null||al.size()==0){
            QMUtil.getInstance().showToast(this, R.string.unabledFile);
            return;
        }

        try {
            QMDbUtil dbUtil= QMDbUtil.getIntance(this);
            for (int i = 0; i < al.size(); i++){
                if(dbUtil.hasPath(al.get(i).getPath(),0)){
                    continue ;
                }
                LancherFile mLancherFile=new LancherFile();
                mLancherFile.setName(al.get(i).getName());
                mLancherFile.setPath(al.get(i).getPath());
                mLancherFile.setSize(al.get(i).getSize());
                mLancherFile.setUpdate(al.get(i).getUpdate());
                mLancherFile.setType(al.get(i).getType());
                mLancherFile.setFileDir(1);
                mLancherFile.setPermit(0);
                dbUtil.insert(mLancherFile);
            }

            QMUtil.getInstance().showToast(this, R.string.share_success);
            showFileDialog(true);
            finish_text.setText("好友终端可自由查看");
            finish_text.setVisibility(View.VISIBLE);
        }catch (Exception e){
            QMUtil.getInstance().showToast(this, R.string.share_fail);
        }
    }


    /**
     * 通过Uri获取文件在本地存储的真实路径
     * @param act
     * @param contentUri
     * @return
     */
    public String getRealPathFromURI(Activity act, Uri contentUri) {
        // can post image
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = act.managedQuery(contentUri, proj, // Which columns to return
                null, // WHERE clause; which rows to return (all rows)
                null, // WHERE clause selection arguments (none)
                null); // Order-by clause (ascending by name)
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private ProgressWheel progress;

    private RecyclerView friendView;

    private void initView(){
        RecyclerView fileView= (RecyclerView) findViewById(R.id.file_recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        fileView.setLayoutManager(linearLayoutManager);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        fileAdapter = new FileAdapter(this, files, dm.widthPixels);
        fileView.addItemDecoration(new SpaceItemDecoration(itemSpace));
        fileView.setAdapter(fileAdapter);

        friendView = (RecyclerView)  findViewById(R.id.friend_recyclerView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,3);
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        friendView.setLayoutManager(gridLayoutManager);
        friendAdapter= new FriendAdapter(this,friends);
        friendView.setAdapter(friendAdapter);
        LinearLayout file_share= (LinearLayout) findViewById(R.id.file_share);
        file_share.setOnClickListener(this);
        friend_send = (LinearLayout) findViewById(R.id.file_send);
        friend_send.setOnClickListener(this);
        progress= (ProgressWheel) findViewById(R.id.progress_wheel);
        LinearLayout search= (LinearLayout) findViewById(R.id.search);
        LinearLayout sancode= (LinearLayout) findViewById(R.id.sancode);
      // wifipsw = (LinearLayout) findViewById(R.id.wifipsw);
       // wifi = (RelativeLayout) findViewById(R.id.wifi);
       // loading = (LoadingView) findViewById(R.id.loading);
        code_img = (ImageView) findViewById(R.id.scanCode_img);
        search_img = (ImageView) findViewById(R.id.search_img);
        search_text = (TextView) findViewById(R.id.search_text);
        scanCode_text = (TextView) findViewById(R.id.scanCode_text);
      //  wifiname = (TextView) findViewById(R.id.wifiname);
       // password = (TextView) findViewById(R.id.password);
       // wifipsw_line = (TextView) findViewById(R.id.wifipsw_line);
        mode = (TextView) findViewById(R.id.mode);
        friendList = (RelativeLayout) findViewById(R.id.friend_list);
       // scan = (TextView) findViewById(R.id.scan);
//        ScaleAnimation scaleAnimation = new ScaleAnimation(1, 0.5f, 1, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//        scaleAnimation.setDuration(2000);
//        scan.setAnimation(scaleAnimation);
        swipe_refresh = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh);
        swipe_refresh.setOnRefreshListener(this);
        nobox = (TextView) findViewById(R.id.nobox);
        finish_text = (TextView) findViewById(R.id.finish_text);
        swipe_refresh.setColorSchemeResources(android.R.color.holo_red_light, android.R.color.holo_green_light, android.R.color.holo_blue_bright, android.R.color.holo_orange_light);
        setMode();
        sancode.setOnClickListener(this);
        search.setOnClickListener(this);
        if(Collocation.getCollocation().getIsScan()){
            progress.setVisibility(View.GONE);
            friendView.setVisibility(View.VISIBLE);
            if(QMDevice.getInstance().getSize()==0){
                setNoBox(true);
            }
        }
//        if(QMDevice.getInstance().getSize()!=0){
//            progress.setVisibility(View.GONE);
//            friendView.setVisibility(View.VISIBLE);
//        }
     }

    private void setNoBox(boolean isScan) {
       if(isScan){
           nobox.setText("请点击扫码连接好友终端");
           nobox.setVisibility(View.VISIBLE);
       }else{
           nobox.setText("请下拉刷新搜索好友终端");
           nobox.setVisibility(View.VISIBLE);
       }
    }

    private void setMode(){
        if(Collocation.getCollocation().getIsScan()){
            code_img.setImageDrawable(getResources().getDrawable(R.mipmap.code_click));
            search_img.setImageDrawable(getResources().getDrawable(R.mipmap.search_none));
            search_text.setTextColor(getResources().getColor(R.color.black));
            scanCode_text.setTextColor(getResources().getColor(R.color.colorPrimary));
            mode.setText("(扫码连接)");
        }else{
            code_img.setImageDrawable(getResources().getDrawable(R.mipmap.code_none));
            search_img.setImageDrawable(getResources().getDrawable(R.mipmap.search_click));
            search_text.setTextColor(getResources().getColor(R.color.colorPrimary));
            scanCode_text.setTextColor(getResources().getColor(R.color.black));
            mode.setText("(自动搜索)");
        }
    }


    public void back(View view){
        finish();
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case  R.id.file_send:

                if(!isCancelFinish){
                    Toast.makeText(this,"稍等一下，请重试！", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(failCount>=5){
                    showToast("请您更换连接的网络传输!");
                    //Toast.makeText(this,"请您更换连接的网络传输！",Toast.LENGTH_SHORT).show();
                }else{
                    //  Toast.makeText(this,"分享给好友！",Toast.LENGTH_SHORT).show();
                    upLoad();
                }
                break;

            case  R.id.file_share:

             //   Toast.makeText(this,"分享给任易屏的共享文件！",Toast.LENGTH_SHORT).show();
                  share(files);

                break;
            case  R.id.sancode:
                     isCode = false;
                     Intent intent = new Intent(this, ScanActivity.class);
                   // startActivity(intent);
                     startActivityForResult(intent,300);
                     showToast("正在连接中...");
                     if(!Collocation.getCollocation().getIsScan()){
                       // Toast.makeText(this,"正在连接中...",Toast.LENGTH_SHORT).show();
                        friends.clear();
                        friendAdapter.notifyDataSetChanged();
                         setNoBox(true);
                        QMDevice.getInstance().clear();
                      }

                     Collocation.getCollocation().saveIsScan(true);
                     setMode();

                break;
            case  R.id.search:
                if(Collocation.getCollocation().getIsScan()){
                    showToast("正在搜索中...");
                    if(friendList.getVisibility()== View.GONE){
                        friendList.setVisibility(View.VISIBLE);
                        //scan.setVisibility(View.GONE);
                        //loading.setVisibility(View.GONE);
                        friend_send.setVisibility(View.VISIBLE);
                    }
                    progress.setVisibility(View.VISIBLE);
                    if(binder!=null){
                        binder.onSearch();
                    }
                 }
                Collocation.getCollocation().saveIsScan(false);
                setMode();
                break;

        }
    }

    private void showToast(String text){
        if(isResume){
            toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
        }
    }

    private void upLoad(){
        if(canLoad()){
            startUpLoad();
        }
    }

    /**
     * 上传文件...
     * @param
     */

    private void startUpLoad(){
        sendAl.clear();
        for (int i = 0; i < friends.size(); i++) {
            if(friends.get(i).isSelect()){
                sendAl.add(QMDevice.getInstance().getAl().get(i));
            }
        }

        if(sendAl.size()!=0){
            sendFileToFriend();
        }
    }

    private void sendFileToFriend(){

          if(sendAl.size()==0){
              DebugLog.showLog(this,"size==0");
              setDialogState(true);
              return;
            }

            final RootPoint point = sendAl.get(0);
            if (FTPUtil.beforPoint != null&&FTPUtil.getInstance(FTPUtil.beforPoint).isConnect()){
               new Thread(new Runnable(){
//                    @Override
                    public void run(){
//                        FTPUtil.getInstance(FTPUtil.beforPoint).unConnectException();
//                        handler.sendEmptyMessage(CONNECT_OK);
                        FTPUtil.getInstance(FTPUtil.beforPoint).setDisconnect(new ConnectFTPListener() {
                            @Override
                            public void success() {
                                handler.sendEmptyMessage(CONNECT_OK);
                            }

                            @Override
                            public void fail(int state){
                                handler.sendEmptyMessage(CONNECT_FAILED);

                            }
                        });
                   }
              }).start();
           }else{
                shareFile();
           }
         }

    private void shareFile(){
       // setBeforePoint(sendAl.get(0));
        showFileDialog(false);
        FTPUtil.beforPoint=sendAl.get(0);
        utils = new NetworkUtils();
        DebugLog.showLog(this,"发送给："+sendAl.get(0).getName());
        if(send_friend!=null){
            send_friend.setText("好友:"+sendAl.get(0).getName());
        }
        if(bar!=null){
            bar.setMax(100);
        }
        new UploadFile(this, files, sendAl.get(0), new ConnectFTPListener(){
            @Override
            public void success(){
                if(sendAl.size()!=0){
                       new Thread(new Runnable(){
                           @Override
                           public void run(){
                               try {
                                   FTPUtil.getInstance(sendAl.get(0)).getFTPClient().sendCustomCommand("FINISH&&DEVICENAME="+Collocation.getCollocation().getDeviceName()+"&&ADDRESS="+ utils.getNetworkIP());
                                   DebugLog.showLog(this,"FINISH&&DEVICENAME="+Collocation.getCollocation().getDeviceName()+"&&ADDRESS="+ utils.getNetworkIP());
                                                   handler.sendEmptyMessage(SEND_NEXT);
                                 }catch (Exception e){

                                 }
                           }
                       }).start();
                }
            }

            @Override
            public void fail(int state){
                if(state==0){
                    failCount++;
                    handler.sendEmptyMessage(DISMISS_DILOGE);
                }else if(state==-1){
                    DebugLog.showLog(this,"ftp连接失败。。");
                    handler.sendEmptyMessage(DISMISS_DILOGE);
                }else if(state==1){
                    DebugLog.showLog(this,"中断了");
                }else if(state==2){
                    Message msg=  new Message();
                    msg.obj=FTPUtil.beforPoint;
                    msg.what=FTP_TIMEOUT;
                    handler.sendMessage(msg);
                }
            }

        },true,send_product,bar,gress);
    }

    private void showFileDialog(boolean b){
          if(d!=null){
          d.dismiss();
          }
           final AlertDialog.Builder alert = new AlertDialog.Builder(this, R.style.file_style);
           d = alert.create();
           DebugLog.showLog(this,"d:"+d);
           d.setCancelable(false);
           d.show();
           d.getWindow().setContentView(R.layout.file_finish_dialog);
           backTools = (TextView) d.findViewById(R.id.back_tools);
           backApp = (TextView) d.findViewById(R.id.back_app);
           send_product = (TextView) d.findViewById(R.id.send_product);
           send_friend = (TextView) d.findViewById(R.id.send_friend);
           file_finish = (LinearLayout) d.findViewById(R.id.file_finish);
           file_sending = (LinearLayout) d.findViewById(R.id.file_sending);
           file_close = (LinearLayout) d.findViewById(R.id.file_send_close);
           bar = (ProgressBar) d.findViewById(R.id.progressBar);
           gress = (TextView) d.findViewById(R.id.progress);
           setDialogState(b);
    }

    private void setDialogState(boolean b){
        if(b){
            file_finish.setVisibility(View.VISIBLE);
            file_sending.setVisibility(View.GONE);
            file_close.setVisibility(View.GONE);
            send_product.setVisibility(View.GONE);
            backTools.setTextColor(getResources().getColor(R.color.file_dialog_font));
            backApp.setTextColor(getResources().getColor(R.color.file_dialog_font));
        }else{
            file_finish.setVisibility(View.GONE);
            file_sending.setVisibility(View.VISIBLE);
        }

        if(b){
            backTools.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    d.dismiss();
                    finish();
                }
            });

            backApp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v){
                    d.dismiss();
                    Intent intent=  new Intent(ShareActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
            file_close.setOnClickListener(null);
        }else{
            backApp.setOnClickListener(null);
            backTools.setOnClickListener(null);
            file_close.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){

                    if(file_finish.getVisibility()== View.GONE){
                        //TODO
                        isCancelFinish=false;
                        d.dismiss();
                      //  QMUtil.getInstance().showProgressDialog(ShareActivity.this,R.string.cancel_upload);

                        FTPUtil.getInstance(FTPUtil.beforPoint).deleteLocalFile(ShareActivity.this,files,new ConnectFTPListener(){

                            @Override
                            public void success(){
                                try{
                               //  FTPUtil.getInstance(FTPUtil.beforPoint).getFTPClient().sendCustomCommand("FINISH&&DEVICENAME="+Collocation.getCollocation().getDeviceName()+"&&ADDRESS="+ utils.getNetworkIP());
                              //      handler.sendEmptyMessage(SEND_NEXT);
                                 }catch (Exception e){

                                 }
                               // DebugLog.showLog(this,"cancel--success");
                                QMUtil.getInstance().closeDialog();
                               // d.dismiss();
                                 isCancelFinish=true;
                                 DebugLog.showLog(this,"delete Success!");
                            }

                            @Override
                             public void fail(int state){
                                //  DebugLog.showLog(this,"cancel--fail");
                               QMUtil.getInstance().closeDialog();
                                // d.dismiss();
                                isCancelFinish=true;
                                DebugLog.showLog(this,"delete fail");
                             }
                        });
                     }
                }
            });
        }
    }


    /**
     *
     * 上传给好友
     *
     */
    private boolean canLoad(){

           if(files==null||files.size()==0){
               QMUtil.getInstance().showToast(this, R.string.unabledFile);
               return false;
           }

         if(friends.size()==0){
                QMUtil.getInstance().showToast(this, R.string.unabledFriend);
                return false;
            }else if(!getSelectFriend()){
                QMUtil.getInstance().showToast(this, R.string.unSelectedFriend);
                return false;
            }else if(getSelectFriend()){
                return true;
            }

          return false;

        }

    private boolean getSelectFriend(){
        if(friends!=null){
            for (Friend friend : friends){
                if(friend.isSelect()){
                    return true;
                }
            }
        }
        return false ;
    }






    private boolean isAppAlive(){
        ActivityManager am = (ActivityManager) getApplicationContext().getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> rProcessList = am.getRunningAppProcesses();

        for (ListIterator<ActivityManager.RunningAppProcessInfo> it = rProcessList.listIterator(); it.hasNext(); ){
            ActivityManager.RunningAppProcessInfo info = it.next();

            if (info.processName.equals(PACKAGE_NAME)){
                //进程正在运行
               return true;
            }
        }
        return false;
    }


    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(intentService!=null){
            stopService(intentService);
        }
        EventBus.getDefault().unregister(this);
        friends=null;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==100){
            getWifi();
            if(ssid==null){
                return;
            }
            //if(name==null){
                if(!Collocation.getCollocation().getIsScan()){
                    showToast("你现在是自动搜索模式，正在搜索中...");
                    progress.setVisibility(View.VISIBLE);
                    if(binder!=null){
                        binder.onSearch();
                    }
                }else{
                    showToast("你现在是扫码模式，请您点击扫码");
                    //Toast.makeText(this,"你现在是扫码模式，请您点击扫码",Toast.LENGTH_SHORT).show();
                }
          //  }
          //clearData();
          //clearData();

            DebugLog.showLog(this,"ssid:"+ssid+"beforeSsid:"+beforeSsid+"name:"+name+"size:"+QMDevice.getInstance().getSize());
//            if(ssid.equals(name)){
//                if(scanParms!=null&&binder!=null&&QMDevice.getInstance().getSize()==0){
//                    binder.onScanForsearch(scanParms.getIp(),isScan);
//                }
//             }
//            else if(!ssid.equals(name)&&!ssid.equals(beforeSsid)){
//                clearData();
//                scan.setVisibility(View.GONE);
//                friendList.setVisibility(View.VISIBLE);
//                friend_send.setVisibility(View.VISIBLE);
//            }
        }

        if(requestCode==300&&!isCode){
            if(Collocation.getCollocation().getIsScan()&& QMDevice.getInstance().getSize()==0){
                showToast("请点击扫码连接其它设备");
                setNoBox(true);
               // Toast.makeText(this,"请点击扫码连接其它设备",Toast.LENGTH_SHORT).show();
            }
        }

    }
  //  网络状态监听
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(EventNetworkReceiver event){
        failCount=0;
        if (EventNetworkReceiver.Status.LOADING == event.getStatus()){
           // scanCode=0;
            friendList.setVisibility(View.GONE);
            friend_send.setVisibility(View.GONE);
        } else if (EventNetworkReceiver.Status.SUCCESS == event.getStatus()){
            DebugLog.showLog(this, "wifi成功了！  beforeSsid:"+beforeSsid);
            getWifi();
            //   isFirstNotNet=false;
//            if (scanParms != null){
//                //扫码获取的
//                if(ScreenFragment.LOCK_LINK.equals(scanParms.getSsid())){
//                    //scan.setVisibility(View.GONE);
//                    friendList.setVisibility(View.VISIBLE);
//                    friend_send.setVisibility(View.VISIBLE);
//                    //loading.setVisibility(View.GONE);
//                    Toast.makeText(this,"您连接是有线网络！", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//              //  chooseNetDialog(scanParms);
//            }

//            if (NetworkUtils.getInstance(this).getNetworkType() == 1){
//                //ssid:当前的wifi名称  name：扫码获得wifi的名称  isScan：是表示有扫码过的标志
//                if (ssid != null){
//                    if (!ssid.equals(name) && isScan){
//                         //以前的名称与现在的名称不一样并且是扫码模式进了
//                        friendList.setVisibility(View.GONE);
//                        friend_send.setVisibility(View.GONE);
//                        //scan.setVisibility(View.VISIBLE);
//                       // loading.setVisibility(View.GONE);
//                        //  wifi.setVisibility(View.GONE);
//                        DebugLog.showLog(this, "2");
//                    }else{
                        DebugLog.showLog(this, "1");
                        DebugLog.showLog(this, "type:" + NetworkUtils.getNetworkType());
                        //scan.setVisibility(View.GONE);
                        friendList.setVisibility(View.VISIBLE);
                        friend_send.setVisibility(View.VISIBLE);
                       // loading.setVisibility(View.GONE);
//                      wifi.setVisibility(View.VISIBLE);
//                          if(scanParms!=null){
//                                if (ssid.equals(scanParms.getSsid())){
//                                    if (binder != null){
//                                        binder.onScanForsearch(scanParms.getIp(),isScan);
//                                    }
//                                }
//                           }else{
                              if(!Collocation.getCollocation().getIsScan()){
                                  if(binder!=null){
                                      binder.onSearch();
                                  }
                              //}
                          }
                      //}
//                   }
//               }else{
//                if(QMDevice.getInstance().getSize()!=0){
//                   // scan.setVisibility(View.GONE);
//                    friendList.setVisibility(View.VISIBLE);
//                    friend_send.setVisibility(View.VISIBLE);
//                    //loading.setVisibility(View.GONE);
//                }else{
//                    //setWifi();
//                   }
//                }
              }else if(EventNetworkReceiver.Status.CLOSE == event.getStatus()){
                  DebugLog.showLog(this,"wifi失败了！");
                  clearData();
                 // setWifi();
              }
//              else if(EventNetworkReceiver.Status.TIMEOUT == event.getStatus()){
//                      DebugLog.showLog(this,"TimeOut！");
//                      scanCode ++;
//                     // loading.setVisibility(View.GONE);
//                      if(scanCode==1){
//                          if (ssid!=null&&name!=null){
//                              if (!ssid.equals(name) && isScanFinish){
//                                  //  loading.setVisibility(View.GONE);
//                                 // scan.setVisibility(View.VISIBLE);
//                                  friendList.setVisibility(View.GONE);
//                                  friend_send.setVisibility(View.GONE);
//                                  //wifi.setVisibility(View.GONE);
//                              }else{
//                                 // scan.setVisibility(View.GONE);
//                                  friendList.setVisibility(View.VISIBLE);
//                                  friend_send.setVisibility(View.VISIBLE);
//                                  //   wifi.setVisibility(View.VISIBLE);
//                              }
//                                 isScanFinish=false;
//                          }
//                      }
//               }
        }




    private void clearData(){
        QMDevice.getInstance().clear();
        friends.clear();
        friendAdapter.notifyDataSetChanged();
    }


    //扫描结果回调
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(ScanEvent event){
        DebugLog.showLog(this,"扫描结果回调："+event.getResult());
        String result = event.getResult();
//        isScan =true;
//        isCode=true;
//        isScanFinish =true;
//        SampleWifiManager.getManager(this).connect(result, getFragmentManager());
//        scanParms = SampleWifiManager.getManager(this).transfrom(result);
//        isSameDevice = isSameDevice(scanParms.getIp());
//        ScreenFragment.netIp=scanParms.getIp();
//        DebugLog.showLog(this,"ssid:"+ssid);
//        DebugLog.showLog(this,"scanParms ssid:"+scanParms.getSsid());
//    //    beforeSsid=ssid;
//        if (ssid.equals(scanParms.getSsid())){
//            DebugLog.showLog(this,"binder:"+binder);
//            if(binder!=null){
//                binder.onScanForsearch(scanParms.getIp(),isScan);
//            }
//        }else{
//            DebugLog.showLog(this,"else");
//        }
//        chooseNetDialog(scanParms);
  //  }
        handleResult(result);
    }

    private void handleResult(String result){
        DebugLog.showLog(this, "二维码：" + result);
        String json = result.subSequence(result.indexOf("?") + 1, result.length()).toString();
        try {
            JSONObject jsonObject = new JSONObject(json);
            netIp = jsonObject.getString("ip");
            if (netIp != null) {
                String net = netIp.substring(0, netIp.lastIndexOf("."));
                String localIp = NetworkUtils.getNetworkIP();
                if (TextUtils.isEmpty(localIp)) {
                    //如果是空，就是没有联网
                    showToast("请调整到与被扫码设备同网络！");
                } else if ((localIp.substring(0, localIp.lastIndexOf("."))).equals(net)) {
                    //同一网段
                    RootPoint rootPoint = QMDevice.getInstance().getSameRootPoint(netIp);
                    if (rootPoint == null){
                        //如果为空，就是发起搜索添加
                        isScan = true;
                        if(binder!=null) {
                            binder.onScanForsearch(netIp,isScan);
                        }
                    }
                    else {
                        showToast("好友已连接！");
//                        rootPoint.setPlay(true);
//                        boxAdapter.notifyDataSetChanged();
                    }
                } else {
                    //不相同就要盒子调整到同一网段
                    showToast("请调整到与被扫码设备同网络！");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean isSameDevice(String ip){
        if(ip!=null){
            ArrayList<RootPoint> al=QMDevice.getInstance().getAl();
            for (RootPoint point : al){
                if(point.getAddress().equals(ip)){
                    return true;
                }
            }
        }
        return false;
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onEventMainThread(EventString event){
//        if (event.getEvent().equals(EventString.ALREADY_NETWORK_CONNECT)){
//           DebugLog.showLog(this,"网络名称相同");
//           if(isSameDevice){
//               showToast("好友已连接！");
//              // Toast.makeText(this,"你连接是同一设备",Toast.LENGTH_SHORT).show();
//               isSameDevice=false;
//           }
//        }else if(event.getEvent().equals(EventString.INPUT_WIFI_PASSWORD)){
//            if(scanParms!=null) {
//                if (scanParms.getSsid() != null && scanParms.getSsid().equals(ssid)) {
//                    DebugLog.showLog(this, "是热点网络...");
//                } else {
//                    scanParms.setPassword("无密码");
//                    SampleWifiManager.getManager(this).checkWifiEnable(scanParms);
//                    DebugLog.showLog(this, "scanParms:" + scanParms.getPassword() + scanParms.getSsid());
//                 }
//            }
//          }
//    }

  //  private void chooseNetDialog(WifiParams scanParms){
//        WifiManager wifimanager = (WifiManager) getSystemService(WIFI_SERVICE);
//      //  setTextWifiInfo(scanParms);
//        String netID = wifimanager.getConnectionInfo().getSSID();
//        //得到的目标的名称
//        name = scanParms.getSsid();
//        DebugLog.showLog(this,"name:"+name);
//       String psw = scanParms.getPassword();
//        if (psw != null){
//            if (psw.length() < 3){
//                psw = "无密码";
//                }
//            }
        //请设置连接到wifi：扫码得到的wifi名称
//        if(psw==null||(psw!=null&&psw.equals("无密码"))){
//            scan.setText("请设置连接到wifi:" + name );
//        }else{
//            scan.setText("请设置连接到wifi:" + name + " 密码：" + psw );
//        }

      //  String text = scan.getText().toString();
//        SpannableString builder = new SpannableString(text);
//        builder.setSpan(new ShareActivity.IntentSpan(new View.OnClickListener(){
//            public void onClick(View v){
//                startActivityForResult(new Intent(Settings.ACTION_WIFI_SETTINGS),100);
//            }
//        }), 1, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        scan.setText(builder);
//        scan.setMovementMethod(LinkMovementMethod.getInstance());
   //     }

    public class IntentSpan extends ClickableSpan {

        private final View.OnClickListener listener;

        public IntentSpan(View.OnClickListener listener){
            this.listener = listener;
        }

        @Override
        public void onClick(View widget){
            // TODO Auto-generated method stub
            listener.onClick(widget);
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        MobclickAgent.onResume(this);
        isResume=true;
        if(NetworkUtils.getNetworkType() == 0){
            if(QMDevice.getInstance().getSize()!=0){
                //scan.setVisibility(View.GONE);
                friend_send.setVisibility(View.VISIBLE);
                friendList.setVisibility(View.VISIBLE);
                notifyData();
            }
        }else{
             notifyData();
        }
      }


    @Override
    protected void onPause(){
        super.onPause();
        MobclickAgent.onPause(this);
        isResume=false;
        if(toast!=null){
            toast.cancel();
        }
    }


}