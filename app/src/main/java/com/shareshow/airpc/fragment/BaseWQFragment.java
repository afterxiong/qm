package com.shareshow.airpc.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.shareshow.aide.R;
import com.shareshow.airpc.Collocation;
import com.shareshow.airpc.QMCommander;
import com.shareshow.airpc.activity.LocalPhotoScanActivity;
import com.shareshow.airpc.activity.QuiteFileActivity;
import com.shareshow.airpc.adapter.AlbumFileAdapter;
import com.shareshow.airpc.adapter.DocumentOtherAdapter;
import com.shareshow.airpc.adapter.SelectedDeviceListAdapter;
import com.shareshow.airpc.adapter.VideoFileAdapter;
import com.shareshow.airpc.model.QMLocalFile;
import com.shareshow.airpc.model.RootPoint;
import com.shareshow.airpc.ports.ConnectFTPListener;
import com.shareshow.airpc.ports.ImgOnClick;
import com.shareshow.airpc.ports.PassWDCallBack;
import com.shareshow.airpc.socket.command.CommandExecutorLancher;
import com.shareshow.airpc.socket.common.FTPUtil;
import com.shareshow.airpc.socket.common.PassWDValidate;
import com.shareshow.airpc.socket.common.QMDevice;
import com.shareshow.airpc.socket.common.QMShareDao;
import com.shareshow.airpc.socket.common.UploadFile;
import com.shareshow.airpc.util.DebugLog;
import com.shareshow.airpc.util.NetworkUtils;
import com.shareshow.airpc.util.OpenAppUtils;
import com.shareshow.airpc.util.QMDialog;
import com.shareshow.airpc.util.QMUtil;
import com.shareshow.airpc.widget.ProgressWheel;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * Created by TEST on 2017/7/18.
 * 微信和QQ的基类
 */
public abstract class BaseWQFragment extends Fragment implements ListView.OnItemClickListener, ListView.OnItemLongClickListener, SwipeRefreshLayout.OnRefreshListener, AbsListView.OnScrollListener {

    @BindView(R.id.document)
    LinearLayout document;
    @BindView(R.id.img)
    LinearLayout img;
    @BindView(R.id.video)
    LinearLayout video;
    //    @BindView(R.id.textname)
//    TextView  textView;
    @BindView(R.id.progress_wheel)
    ProgressWheel wheel;
    @BindView(R.id.list)
    ListView list;
    @BindView(R.id.buttom)
    LinearLayout bottom;
    @BindView(R.id.textname)
    TextView textName;
    @BindView(R.id.share)
    ImageView share;
    @BindView(R.id.up_load)
    ImageView up_load;
    @BindView(R.id.selectAll)
    ImageView selectAll;
    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout refresh;
    @BindView(R.id.text)
    TextView text;
    @BindView(R.id.show_upLoad)
    LinearLayout showUpLoad;

    public ArrayList<QMLocalFile> wxFile = new ArrayList<QMLocalFile>();
    public ArrayList<QMLocalFile> wxImg = new ArrayList<QMLocalFile>();
    public ArrayList<QMLocalFile> wxVideo = new ArrayList<QMLocalFile>();
    public DocumentOtherAdapter fileAdpter;
    public VideoFileAdapter videoAdapter;
    public AlbumFileAdapter imgAdapter;
    public static final int WX_FILE_SUCCESS = 0x001;
    public static final int WX_IMG_VIDEO_SUCCESS = 0x002;
    public static final int QQ_FILE_SUCCESS = 0x003;
    public static final int QQ_IMG_VIDEO_SUCCESS = 0x004;
    public static final int QQ_OUT_TIME = 0x005;
    public static final int CONNECT_OK = 1002;// 获取文件成功
    public static final int CONNECT_FAILED = 1003;// 获取文件成功
    public ExecutorService threadPool;
    public boolean isLongclickState;
    public boolean chooseAll;
    public QMDialog listDialog;
    public boolean isEmptyPasswd;
    public ArrayList<QMLocalFile> al = new ArrayList<>();
    private Unbinder unbinder;
    public BaseWQFragment.callBackActivity callBackActivity;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View vv = inflater.inflate(R.layout.fragment_wx, container, false);
        // x.view().inject(this, vv);
        unbinder = ButterKnife.bind(this, vv);
        return vv;
    }

    public void onRefresh(){
        if (wheel.getVisibility() == View.VISIBLE) {
            refresh.setRefreshing(false);
            return;
        }
        getData(false);
        selectAll.setBackgroundResource(R.drawable.choose_all_bg);
        cancelSelect();
        handler.postDelayed(stopRefreshRunnable, 3000);
    }

    private Runnable stopRefreshRunnable=new Runnable() {
        @Override
        public void run() {
            refresh.setRefreshing(false);
        }

    };


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
        getData(true);
    }

    public abstract void getData(boolean isRefresh);

    public Handler handler = new Handler(){
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            switch (msg.what) {
                case CONNECT_OK:
//
//                    if(callBackActivity!=null){
//                        callBackActivity.onDeviceName(QMDevice.getInstance().getSelectDevice());
//                    }
                    DebugLog.showLog(this,"ftp 断开成功，开始连接");

                    break;

                case CONNECT_FAILED:
                    DebugLog.showLog(this, "ftp连接失败！");
                    break;
            }

        }
    };

    public void initData(){

        threadPool = Executors.newFixedThreadPool(5);
        fileAdpter = new DocumentOtherAdapter(getActivity(), wxFile);
        list.setAdapter(fileAdpter);
        imgAdapter = new AlbumFileAdapter<QMLocalFile>(getActivity(), wxImg);
        videoAdapter = new VideoFileAdapter<QMLocalFile>(getActivity(), wxVideo);
        list.setOnItemClickListener(this);
        list.setOnItemLongClickListener(this);
        list.setOnScrollListener(this);
        refresh.setOnRefreshListener(this);
        refresh.setColorSchemeResources(android.R.color.holo_red_light, android.R.color.holo_green_light, android.R.color.holo_blue_bright, android.R.color.holo_orange_light);
        //   wheel.setVisibility(View.VISIBLE);
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        callBackActivity = (BaseWQFragment.callBackActivity)activity;
        DebugLog.showLog(this,"onAttach");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        DebugLog.showLog(this, "click");
        if (isLongclickState) {
            if (list.getAdapter().equals(fileAdpter)) {
                notifyAdapter(wxFile, 1, position);
            } else if (list.getAdapter().equals(imgAdapter)) {
                notifyAdapter(wxImg, 2, position);
            } else if (list.getAdapter().equals(videoAdapter)) {
                notifyAdapter(wxVideo, 3, position);
            }
        } else {
            if (list.getAdapter().equals(fileAdpter)) {
                new OpenAppUtils(getActivity()).openFiles(wxFile.get(position).getPath());
            } else if (list.getAdapter().equals(imgAdapter)){
               // new OpenAppUtils(getActivity()).openFiles(wxImg.get(position).getPath());
                Intent intent=new Intent(getActivity(),LocalPhotoScanActivity.class);
                QMUtil.getInstance().getPhotos().clear();
                QMUtil.getInstance().getPhotos().addAll(al);
                intent.putExtra("position",position);
                intent.putExtra("scan",0);
                startActivity(intent);
            } else if (list.getAdapter().equals(videoAdapter)){
                new OpenAppUtils(getActivity()).openFiles(wxVideo.get(position).getPath());
            }
        }
    }

    public void notifyAdapter(ArrayList<QMLocalFile> al, int i, int position) {
        if (al.get(position).isChoose()) {
            chooseAll = false;
            selectAll.setBackgroundResource(R.drawable.choose_all_bg);
            al.get(position).setChoose(false);
        } else {
            al.get(position).setChoose(true);
        }
        int kk = 0;
        for (int k = 0; k < al.size(); k++) {
            if (al.get(k).getPermit() == 1)
                continue;
            if (!al.get(k).isChoose()) {
                kk = 1;
                break;
            }
        }
        if (kk == 0) {
            chooseAll = true;
            selectAll.setBackgroundResource(R.drawable.undo_choose);
        }
        if (i == 1) {
            fileAdpter.notifyDataSetChanged();

        } else if (i == 2) {

            imgAdapter.notifyDataSetChanged();

        } else if (i == 3) {

            videoAdapter.notifyDataSetChanged();

        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        isLongclickState = true;
        if (list.getAdapter().equals(fileAdpter)) {
            fileAdpter.isLongclick(isLongclickState);
        } else if (list.getAdapter().equals(imgAdapter)) {
            imgAdapter.isLongclick(isLongclickState);
        } else if (list.getAdapter().equals(videoAdapter)) {
            videoAdapter.isLongclick(isLongclickState);
        }
        Animation animation = AnimationUtils.loadAnimation(getActivity(),
                R.anim.photo_dialog_in_anim);
        animation.setDuration(800);
        animation.setFillAfter(true);
        bottom.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                bottom.setVisibility(View.VISIBLE);

            }
        });

        ((QuiteFileActivity) getActivity()).hiddenCancel(0);

        DebugLog.showLog(this, "longclick");
        return false;
    }

    public boolean isLongclick() {
        return isLongclickState;
    }

    public void cancelSelect() {
        if (list.getAdapter().equals(fileAdpter)) {
            for (int i = 0; i < wxFile.size(); i++) {
                wxFile.get(i).setChoose(false);
            }
            isLongclickState = false;
            fileAdpter.isLongclick(isLongclickState);
        } else if (list.getAdapter().equals(imgAdapter)) {
            for (int i = 0; i < wxImg.size(); i++) {
                wxImg.get(i).setChoose(false);
            }
            isLongclickState = false;
            imgAdapter.isLongclick(isLongclickState);
        } else if (list.getAdapter().equals(videoAdapter)) {
            for (int i = 0; i < wxVideo.size(); i++) {
                wxVideo.get(i).setChoose(false);
            }
            isLongclickState = false;
            videoAdapter.isLongclick(isLongclickState);
        }

        if (bottom.getVisibility() == View.VISIBLE) {
            Animation animation2 = AnimationUtils.loadAnimation(getActivity(),
                    R.anim.photo_dialog_out_anim);
            animation2.setDuration(800);
            animation2.setFillAfter(true);
            bottom.startAnimation(animation2);
            bottom.setVisibility(View.GONE);
            chooseAll = false;
            selectAll.setBackgroundResource(R.drawable.choose_all_bg);
            ((QuiteFileActivity) getActivity()).hiddenCancel(8);


        }
    }

    public void sendFileOk(RootPoint newRP, boolean flag) {
        connectFTPServer(newRP, flag);
    }


    @OnClick(R.id.document)
    public void document(View v){
        if (!list.getAdapter().equals(fileAdpter)) {
            list.setAdapter(fileAdpter);
        }
        al = wxFile;
        textName.setText("文档");
        cancelSelect();
        setText(0);
    }


    @OnClick(R.id.img)
    public void album(View v){
        if (!list.getAdapter().equals(imgAdapter)) {
            imgAdapter.notifyDataSetChanged();
            list.setAdapter(imgAdapter);
        }
        al = wxImg;
        textName.setText("图片");
        cancelSelect();
        setText(1);
    }

    @OnClick(R.id.video)
    public void video(View v){
        if (!list.getAdapter().equals(videoAdapter)) {
            videoAdapter.notifyDataSetChanged();
            list.setAdapter(videoAdapter);
        }
        al = wxVideo;
        textName.setText("视频");
        cancelSelect();
        setText(2);
    }

    @OnClick(R.id.up_load)
    public void up_load(View v){
        if (canUpLoad()){
            sendFile();
        }
    }

    public void sendFile(){
//        FTPUtil.beforPoint = QMDevice.getInstance().getSelectDevice();
//        if (FTPUtil.beforPoint != null&&FTPUtil.getInstance(FTPUtil.beforPoint).isConnect()){
//            new Thread(new Runnable(){
//
//                public void run(){
//
//                    FTPUtil.getInstance(FTPUtil.beforPoint).setDisconnect(new ConnectFTPListener(){
//                        @Override
//                        public void success() {
//                            handler.sendEmptyMessage(CONNECT_OK);
//                        }
//
//                        @Override
//                        public void fail(int state){
//                            handler.sendEmptyMessage(CONNECT_FAILED);
//
//                        }
//                    });
//                }
//            }).start();
//         }else{
            startUpLoad();
 //       }
    }

    public void startUpLoad(){
        new UploadFile(getActivity(), al, QMDevice.getInstance().getSelectDevice(), new ConnectFTPListener() {

            @Override
            public void success(){
                new Thread(new Runnable(){
                    @Override
                    public void run(){
                        try {
                            FTPUtil.getInstance(QMDevice.getInstance().getSelectDevice()).getFTPClient().sendCustomCommand("FINISH&&DEVICENAME="+ Collocation.getCollocation().getDeviceName()+"&&ADDRESS="+ NetworkUtils.getNetworkIP());
                            DebugLog.showLog(this,"FINISH&&DEVICENAME="+Collocation.getCollocation().getDeviceName()+"&&ADDRESS="+ NetworkUtils.getNetworkIP());
                        }catch (Exception e){

                        }
                    }
                }).start();
                cancel();
            }

            @Override
            public void fail(int state) {

            }
        });
    }

    //取消选中
    public void cancel(){
        for (int i = 0; i < al.size(); i++) {
            al.get(i).setChoose(false);
        }
        if (list.getAdapter().equals(fileAdpter)) {
            isLongclickState = false;
            fileAdpter.isLongclick(isLongclickState);
            fileAdpter.notifyDataSetChanged();

        } else if (list.getAdapter().equals(imgAdapter)) {
            isLongclickState = false;
            imgAdapter.isLongclick(isLongclickState);
            imgAdapter.notifyDataSetChanged();

        } else if (list.getAdapter().equals(videoAdapter)) {
            isLongclickState = false;
            videoAdapter.isLongclick(isLongclickState);
            videoAdapter.notifyDataSetChanged();
        }

        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.photo_dialog_out_anim);
        animation.setDuration(800);
        animation.setFillAfter(false);
        bottom.startAnimation(animation);
        bottom.setVisibility(View.GONE);
        chooseAll = false;
        selectAll.setBackgroundResource(R.drawable.choose_all_bg);
        ((QuiteFileActivity) getActivity()).hiddenCancel(8);


    }

    public boolean canUpLoad(){

        if (list.getAdapter().equals(fileAdpter)) {
            al = wxFile;
        } else if (list.getAdapter().equals(imgAdapter)) {
            al = wxImg;
        } else if (list.getAdapter().equals(videoAdapter)) {
            al = wxVideo;
        }
        int kk = 0;
        for (int i = 0; i < al.size(); i++) {
            if (al.get(i).isChoose()) {
                kk++;
                break;
            }
        }
        if (kk == 0) {
            QMUtil.getInstance().showToast(getActivity(), R.string.unselectedUpLoadFile);
            return false;
        }
        if (QMDevice.getInstance().getSelectDevice() == null) {//主页没有选中设备进入文件共享页面来的
            if (QMDevice.getInstance().getAl().size() == 0)
                QMUtil.getInstance().showToast(getActivity(), R.string.unabledDivice);
            else
                showDevice();//展示可用上传的设备
            return false;
        }

        return true;
    }

    public void onDeviceItemClick(final RootPoint rp){
        //手机设备或PC设备不用密码验证直接连接设备
        if (rp.getdType() == 0 || rp.getdType() == 1 || rp.getdType() == 2) {
            connectFTPServer(rp, false);
            return;
        }

        if (rp.getEnablepwd().equals("true")) {
            DebugLog.showLog(this, "密码验证");
            passwdVertify(rp);
        } else {
            connectFTPServer(rp, true);
        }
    }

    /**
     * 密码验证
     */
    public void passwdVertify(RootPoint rp){
        isEmptyPasswd = false;
        String info = Collocation.getCollocation().getPassWd(rp.getUuid());
        rp.setPassword(info);
        // 本地如果没有保存密码的话弹出输入密码对话框
        if (info.equals(""))
            showPasswdDialog(rp);
        else {
            sendVertify(rp, info);
        }
    }

    /**
     * 显示输入密码的对话框
     */
    public void showPasswdDialog(final RootPoint rp){
        new PassWDValidate(getActivity()).showPassWDInput(new PassWDCallBack() {
            @Override
            public void callBack(String inputContent) {
                rp.setPassword(inputContent + "");
                pass_input = inputContent + "";
                sendVertify(rp, inputContent);
            }
        });
    }

    /**
     * 向任盒发送密码验证
     *
     * @param rp
     * @param info
     */
    public void sendVertify(RootPoint rp, String info){
        QMUtil.getInstance().showProgressDialog(getActivity(), R.string.passwd_verify);
        // 本地保存了密码的话，发送密码验证请求，回回调接口为
        CommandExecutorLancher.getOnlyExecutor().connectMessage(
                QMCommander.TYPE_CONNECT,
                rp.getAddress(), info);
        // 用于验证密码请求有没有响应
        handler.postDelayed(passwd, 5000);
    }

    public String pass_input;
    /**
     * 5秒内密码验证是否有响应
     */
    public Runnable passwd = new Runnable(){
        @Override
        public void run(){
            QMUtil.getInstance().closeDialog();
            QMUtil.getInstance().showToast(getActivity(), R.string.no_response);
            pass_input = "";
        }
    };

    public void showDevice(){
        View vv = View.inflate(getActivity(), R.layout.device_list_dialog, null);
        ListView listView = (ListView) vv.findViewById(R.id.list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DebugLog.showLog(this, "onItemClick");
                onDeviceItemClick(QMDevice.getInstance().getAl().get(position));
            }
        });

        BaseAdapter adapter = new SelectedDeviceListAdapter<RootPoint>(getActivity(), QMDevice.getInstance().getAl());
        listView.setAdapter(adapter);
        listDialog = new QMDialog(getActivity(), vv, true);
    }

    protected void connectFTPServer(final RootPoint rp, final boolean flag) {
        if (listDialog != null)
            listDialog.dismiss();
        QMUtil.getInstance().showProgressDialog(getActivity(), R.string.connect_divice);
        FTPUtil.getInstance(rp).connectFTP(new ConnectFTPListener(){

            @Override
            public void success(){
                QMUtil.getInstance().closeDialog();
                rp.setPlay(true);
                DebugLog.showLog(this,"连接FTP成功");
                if (flag) {//没有密码验证的任盒需要做连接处理
                    sendEmptyPasswdConnect(rp);
                }
                connectFTPSuccess();
                handler.sendEmptyMessage(CONNECT_OK);
            }

            @Override
            public void fail(int state) {
                QMUtil.getInstance().closeDialog();
                QMUtil.getInstance().showToast(getActivity(), R.string.upload_fail_error);
                handler.sendEmptyMessage(CONNECT_FAILED);
            }
        });
    }

    public void sendEmptyPasswdConnect(RootPoint rp) {
        isEmptyPasswd = true;
        CommandExecutorLancher.getOnlyExecutor().connectMessage(QMCommander.TYPE_CONNECT, rp.getAddress(), "");
    }

    public void connectFTPSuccess() {
        sendFile();
    }


    @OnClick(R.id.selectAll)
    public void selectAll(View v) {
        BaseAdapter adapter = null;
        if (list.getAdapter().equals(fileAdpter)) {
            adapter = fileAdpter;
        } else if (list.getAdapter().equals(imgAdapter)) {
            adapter = imgAdapter;
        } else if (list.getAdapter().equals(videoAdapter)) {
            adapter = videoAdapter;
        }
        if (chooseAll) {
            for (int i = 0; i < al.size(); i++) {
                al.get(i).setChoose(false);
            }
            chooseAll = false;
            selectAll.setBackgroundResource(R.drawable.choose_all_bg);
        } else {
            for (int i = 0; i < al.size(); i++) {
                al.get(i).setChoose(true);
            }
            chooseAll = true;
            selectAll.setBackgroundResource(R.drawable.undo_choose);
        }
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @OnClick(R.id.share)
    public void share(View v) {
        new QMShareDao(getActivity(), al, 1, new ImgOnClick() {
            @Override
            public void imgClick(int position) {
                if (position == 0)
                    cancel();
            }
        });
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        if (threadPool != null){
            threadPool.shutdown();
            threadPool = null;
        }

        handler.removeCallbacks(stopRefreshRunnable);
        handler.removeCallbacks(passwd);

//        if (unbinder != null) {
//            unbinder.unbind();
//        }
    }

    @Override
    public void onResume(){
        super.onResume();
        getData(true);
    }

    @Override
    public void onHiddenChanged(boolean hidden){
        super.onHiddenChanged(hidden);
        if (!hidden) {
            getData(true);
        }
    }


    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        boolean enable = false;
        if (list != null && list.getChildCount() > 0){
            boolean firstItemVisible = list.getFirstVisiblePosition() == 0;
            boolean topoffFirstItemVisible = list.getChildAt(0).getTop() == 0;
            enable = firstItemVisible && topoffFirstItemVisible;
        }
        refresh.setEnabled(enable);
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        if(unbinder!=null){
            unbinder.unbind();
        }
    }


    public void setText(int i){
        text.setVisibility(View.GONE);
        if(al.size()==0){
            switch (i){
                case 0:
                    text.setText("暂时没有可分享的文件！");
                    break;
                case 1:
                    text.setText("暂时没有可分享的图片！");
                    break;
                case 2:
                    text.setText("暂时没有可分享的视频！");
                    break;
            }
            text.setVisibility(View.VISIBLE);
        }
    }


    public interface callBackActivity{

        void onDeviceName(RootPoint rp);
    }
}

