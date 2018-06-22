package com.shareshow.airpc.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.shareshow.aide.R;
import com.shareshow.airpc.Collocation;
import com.shareshow.airpc.record.RecordManager;
import com.shareshow.airpc.util.HairUtils;
import com.shareshow.airpc.util.NetworkUtils;

import qiu.niorgai.StatusBarCompat;

public class SettingActivity extends Activity implements OnClickListener {

//    private Switch ck;
//    private Switch expand;
//    private LineEditText et;
//    private String oldname;
//    private TextView carcheSize;
//    private String formatSize;
//
//    private Switch audio;
//    private ImageView isScan_ck;
//    private ImageView auto_ck;
//    private Toolbar toolbar;
//    private Switch float_window;

    public static final int MAIN_CODE = 0x00980;
    private RelativeLayout rl_carch;
    private ImageView bt_0;
    private ImageView bt_1;
    private ImageView bt_2;
    private ImageView bt_3;
    private RelativeLayout flucncy;
    private RelativeLayout default_;
    private RelativeLayout distinct;
    private RelativeLayout csan_connect;
    private RelativeLayout auto_connect;
//    private  RelativeLayout float_window_x;
//    private  ImageView  float_window_img;
//    private ImageView net_0;
//    private ImageView net_1;
//    private ImageView net_2;
//    private ImageView net_3;
//    private RelativeLayout net_false;
//    private RelativeLayout soso;
//    private RelativeLayout good;
    private RelativeLayout hander_open;
    private RelativeLayout file_open;
    private ImageView file_1;
    private ImageView file_0;
    private boolean isResume =true;

    private static final int LOW_BITRETE =0X001 ;
    private static final int MIDDLE_BITRETE =0X002 ;
    private static final int HIGH_BITRETE =0X003 ;
    private static final int SUPER_HIGH_BITRETE =0X004 ;
    private RelativeLayout high_distinct;
    private boolean is2KScreen;
    private int state;
    private ImageView saleImg;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_setting_x);
        StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this, R.color.colorPrimaryDark));
        getMessage();
        initEvent();
//        toolbar= (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);
//        toolbar.setNavigationOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });
    }

    public void back(View view){
        onBackPressed();
    }

    private void getMessage(){
//        oldname = Collocation.getCollocation().getDeviceName();
//        long folderSize = CleanCache.getFolderSize(new File(QMFileType.CACHE));
//        formatSize = CleanCache.getFormatSize(folderSize);
    }



    private void initEvent(){
//        ck = (Switch) findViewById(R.id.chebox);
//        ck.setChecked(Collocation.getCollocation().getPermit());
//        ck.setOnCheckedChangeListener(this);
//        expand = (Switch) findViewById(R.id.expand);
//        expand.setChecked(Collocation.getCollocation().getExpand());
//        expand.setOnCheckedChangeListener(this);
//        audio = (Switch) findViewById(R.id.audio_ck);
//        audio.setOnCheckedChangeListener(this);
//        audio.setChecked(Collocation.getCollocation().getAudio());
//        isScan_ck = (ImageView) findViewById(R.id.isScan_ck);
//        csan_connect = (RelativeLayout) findViewById(R.id.scan_connect);
//        csan_connect.setOnClickListener(this);
//        auto_connect = (RelativeLayout) findViewById(R.id.auto_connect);
//        auto_connect.setOnClickListener(this);
//        float_window = (Switch) findViewById(R.id.float_window_selector);
//        float_window.setOnClickListener(this);
      //  float_window.setChecked(Collocation.getCollocation().getFloatWindow());
        bt_0 = (ImageView) findViewById(R.id.bt_0);
        bt_1 = (ImageView) findViewById(R.id.bt_1);
        bt_2 = (ImageView) findViewById(R.id.bt_2);
        bt_3 = (ImageView) findViewById(R.id.bt_3);
        flucncy = (RelativeLayout) findViewById(R.id.fluency);
        default_ = (RelativeLayout) findViewById(R.id.default_);
        distinct = (RelativeLayout) findViewById(R.id.distinct);
        high_distinct = (RelativeLayout) findViewById(R.id.high_distinct);
        flucncy.setOnClickListener(this);
        default_.setOnClickListener(this);
        distinct.setOnClickListener(this);
        high_distinct.setOnClickListener(this);
        is2KScreen = HairUtils.is2KScreen();
        int state=getClarityState(Collocation.getCollocation().getNetbitRate());
        setNetbitRate(state,true);
       // setChecked(Collocation.getCollocation().getprogress(),true);
//        auto_ck = (ImageView) findViewById(R.id.auto_ck);
//        if(Collocation.getCollocation().getIsScan()){
//           isScan_ck.setBackground(getResources().getDrawable(R.mipmap.selected2));
//            auto_ck.setBackground(null);
//        }else {
//            isScan_ck.setBackground(null);
//            isScan_ck.setBackground(null);
//            auto_ck.setBackground(getResources().getDrawable(R.mipmap.selected2));
//        }
//        et = (LineEditText) findViewById(R.id.et);
//        rl_carch = (RelativeLayout)  findViewById(R.id.rl_carche);
//        rl_carch.setOnClickListener(this);
//        carcheSize = (TextView)findViewById(R.id.tv_carche);
//        carcheSize.setText(formatSize);
//        et.setText(oldname);
//        et.setSelection(et.length());
//        et.addTextChangedListener(new TextWatcher(){
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count){
//                String editable = et.getText().toString();
////                String str = QMUtil.getInstance().stringFilter(editable);
// //               String editable = et.getText().toString();
////                String str = QMUtil.getInstance().stringFilter(editable);
//                String str=editable;
//                if(str!=null&&str.length()>10){
//                    Toast.makeText(SettingActivity.this,"名字最多输入10个字符！", Toast.LENGTH_SHORT).show();
//                    str=str.substring(0,10);
//                    et.setText(str);
//                    et.setSelection(str.length());
//                }else{
//                    if (!editable.equals(str)){
//                        et.setText(str);
//                        et.setSelection(str.length());
//                    }
//                }
//
//            }
//
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count,
//                                          int after){
//                new Handler().postDelayed(new Runnable(){
//
//                    @Override
//                    public void run(){
//                        showSoftInputView(et);
//                    }
//                }, 300);
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                // TODO Auto-generated method stub
//                  setEditTextInEmptySpace(et);
//            }
//        });


//        net_0 = (ImageView) findViewById(R.id.net_0);
//        net_1 = (ImageView) findViewById(R.id.net_1);
//        net_2 = (ImageView) findViewById(R.id.net_2);
//        net_2 = (ImageView) findViewById(R.id.net_3);
//        good = (RelativeLayout) findViewById(R.id.net_good);
//        soso = (RelativeLayout) findViewById(R.id.net_soso);
//        net_false = (RelativeLayout) findViewById(R.id.net_false);
//        good.setOnClickListener(this);
//        soso.setOnClickListener(this);
//        net_false.setOnClickListener(this);

        file_0 = (ImageView) findViewById(R.id.file_0);
        file_1 = (ImageView) findViewById(R.id.file_1);
        file_open = (RelativeLayout) findViewById(R.id.file_open);
        hander_open = (RelativeLayout) findViewById(R.id.hander_open);
        file_open.setOnClickListener(this);
        hander_open.setOnClickListener(this);
        setOpenfile();
//        TextView bindDevice = (TextView) findViewById(R.id.bind_device);
//        TextView registerServer = (TextView) findViewById(R.id.register_server);
//        bindDevice.setOnClickListener(this);
//        registerServer.setOnClickListener(this);
//        RelativeLayout saleManager = (RelativeLayout) findViewById(R.id.isSaleManager);
//        saleImg = (ImageView) findViewById(R.id.saleManager_img);
//        saleManager.setOnClickListener(this);
//        setSaleManager();

//        RelativeLayout linkSP= (RelativeLayout) findViewById(R.id.link_sp);
//        linkSP.setOnClickListener(this);

        //开启悬浮窗
//        float_window_x = (RelativeLayout) findViewById(R.id.float_window_x);
//        float_window_x.setOnClickListener(this);
//        float_window_img= (ImageView) findViewById(R.id.float_window_img);
//        if(Collocation.getCollocation().getFloatWindow()){
//            float_window_img.setBackground(getResources().getDrawable(R.mipmap.selected2));
//        }else{
//            float_window_img.setBackground(null);
//        }

//        //高清，标清的复选框
//        radiogroup1 = (RadioGroup) findViewById(R.id.radiogroup1);
//        ((RadioButton)radiogroup1.getChildAt(Collocation.getCollocation().getQXprogress())).setChecked(true);
//        radiogroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                //改变保存以后从新开始截屏投屏
//                if (checkedId==R.id.a0) {
//                    Collocation.getCollocation().saveQX(0);
//                    frameRate = 25;
//                    bitRate=2000;
//                }else {
//                    Collocation.getCollocation().saveQX(1);
//                    frameRate = 25;
//                    bitRate=1000;
//                }
//                resetTP();
//            }
//
//
//        });
//        seek.setProgress(Collocation.getCollocation().getprogress());
//        seek.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//
//                if (progress<33) {
//                    //流畅
//                    frameRate = 15;
//                }else if (progress>66) {
//                    //高清
//                    frameRate = 25;
//                }else {
//                    //默认
//                    frameRate = 20;
//                }
//                Collocation.getCollocation().savepro(progress);
//
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//                Collocation.getCollocation().saveLC(frameRate);
//
//                Toast.makeText(getApplicationContext(), "设置成功，下次启动生效", Toast.LENGTH_SHORT).show();
//            }
//        });


    }

    private void setSaleManager() {

        if(Collocation.getCollocation().getSaleManager()){
            saleImg.setBackground(getResources().getDrawable(R.mipmap.selected2));
        }else{
            saleImg.setBackground(null);
        }
    }


    public void setEditTextInEmptySpace(EditText et){
        InputFilter filter=  new InputFilter(){
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if(source.equals(" ")){
                    return "";
                }
                return null;
            }
        };
        et.setFilters(new InputFilter[]{filter});
    }

    private void setOpenfile(){
        if(Collocation.getCollocation().getFileOpen()){
            file_0.setBackground(getResources().getDrawable(R.mipmap.selected2));
            file_1.setBackground(null);
        }else{
            file_1.setBackground(getResources().getDrawable(R.mipmap.selected2));
            file_0.setBackground(null);
        }
    }

    /**
     *
     * 投屏设置更改后重新开始进行投屏
     * */
//    private void resetTP(){
//        int k=0;
//        if(expand.isChecked()==Collocation.getCollocation().getExpand())
//            k=1;
//        if (RecordManager.canT){
//            if(QMDevice.getInstance().hasScreenDevice())
//                readyTP();
//            QMDevice.getInstance().oprationScreenDevice(new BoxOnClickListener(){
//                @Override
//                public void onClick(RootPoint rootPoint){
//                    rootPoint.setTouPing(false);
//                    rootPoint.setStopByHandle(false);
//                    if (rootPoint.getdType()==0||rootPoint.getdType()==1){
//                        CommandExectuorMobile.getOnlyExecutor().connectMessage(
//                                QMCommander.TYPE_STOP_M, rootPoint.getAddress());
//                    }else if (rootPoint.getdType()==2){
//                        //发送pc的关闭的指令
//                        CommandExecutorLancher.getOnlyExecutor().connectPcMessage(QMCommander.TYPE_STOP_PC, rootPoint.getAddress());
//                    }else{
//                        // 已经进行投屏的盒子全部发送停止投屏的指令
//                        CommandExecutorBox.getOnlyExecutor().connectMessage(
//                                QMCommander.TYPE_STOP_PLAY, rootPoint.getAddress(), null);
//                    }
//                }
//            });
//        }
//
//        //保存现有
//        Collocation.getCollocation().savePermit(ck.isChecked());
//        Collocation.getCollocation().saveExpand(expand.isChecked());

   // }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
//            case R.id.rl_carche:
//                CleanCache.deleteFolderFile(QMFileType.CACHE);
//                long folderSize2 = CleanCache.getFolderSize(new File(QMFileType.CACHE));
//                formatSize = CleanCache.getFormatSize(folderSize2);
//                carcheSize.setText(formatSize);
//                QMUtil.getInstance().showToast(this, R.string.clear_carche);
//                break;
//            case R.id.float_window_selector:
//
//                if(float_window!=null&&float_window.isChecked()){
//                   Collocation.getCollocation().saveFloatWindow(true);
//                }else{
//                    Collocation.getCollocation().saveFloatWindow(false);
//                }
//
//                break;

            case R.id.back:
                    Intent intent = new Intent();
                    setResult(MAIN_CODE, intent);
                    closeSoftInputView();
                    finish();

                break;
            case R.id.fluency:
                //流畅
                setNetbitRate(LOW_BITRETE,false);
                break;
            case R.id.default_:
                //默认
                setNetbitRate(MIDDLE_BITRETE,false);
                break;
            case R.id.distinct:
                //流畅
                setNetbitRate(HIGH_BITRETE,false);
                break;
            case R.id.high_distinct:
                //超清
                setNetbitRate(SUPER_HIGH_BITRETE,false);
                break;
//            case R.id.scan_connect:
//                //掃描連接
//                if (!Collocation.getCollocation().getIsScan()){
//                    QMDevice.getInstance().clear();
//                    ScreenFragment.show_search_error.setVisibility(View.VISIBLE);
//                }
//                Collocation.getCollocation().saveIsScan(true);
//                isScan_ck.setBackground(getResources().getDrawable(R.mipmap.selected2));
//                auto_ck.setBackground(null);
//                QMDevice.getInstance().setOffline();
//                QMDevice.getInstance().removeOffline();
//             if (!Collocation.getCollocation().getIsScan()){
//                   ScreenFragment.nearby_nobox.setText(getResources().getString(R.string.nearby_nobox));
//               }else{
//                  ScreenFragment.nearby_nobox.setText("您选择的是扫码模式，请扫描连接！");
//               }
//                break;
//            case R.id.auto_connect:
//                //自動連接
//                isScan_ck.setBackground(null);
//                auto_ck.setBackground(getResources().getDrawable(R.mipmap.selected2));
//                Collocation.getCollocation().saveIsScan(false);
//                if (!Collocation.getCollocation().getIsScan()){
//                    ScreenFragment.nearby_nobox.setText(getResources().getString(R.string.nearby_nobox));
//                } else {
//                    ScreenFragment.nearby_nobox.setText("您选择的是扫码模式，请扫描连接！");
//                }
//
//                break;

//            case  R.id.register_server:
//                Intent registerIntent = new Intent(this, ScanActivity.class);
//                startActivity(registerIntent);
//                registerIntent.putExtra("enter_flag",1);
//                startActivity(registerIntent);
//
//                 break;
//
//            case R.id.bind_device:
//                Intent bindIntent = new Intent(this, ScanActivity.class);
//                bindIntent.putExtra("enter_flag",2);
//                startActivity(bindIntent);
//
//                break;

//            case R.id.isSaleManager:
//                  if(Collocation.getCollocation().getSaleManager()){
//                      Collocation.getCollocation().saveSaleManager(false);
//                      saleImg.setBackground(null);
//                  }else{
//                      Collocation.getCollocation().saveSaleManager(true);
//                      saleImg.setBackground(getResources().getDrawable(R.mipmap.selected2));
//                  }
//                break;


//            case R.id.float_window_x:
//                if(Collocation.getCollocation().getFloatWindow()){
//                    float_window_img.setBackground(null);
//                    Collocation.getCollocation().saveFloatWindow(false);
//                }else{
//                    float_window_img.setBackground(getResources().getDrawable(R.mipmap.selected2));
//                    Collocation.getCollocation().saveFloatWindow(true);
//                }
//                break;

//            case R.id.net_good:
//                setNetbitRate(1000,false);
//                Collocation.getCollocation().saveNetbitRate(1000);
//
//                break;
//            case R.id.net_soso:
//                setNetbitRate(1500,false);
//                Collocation.getCollocation().saveNetbitRate(1500);
//
//                break;
//            case R.id.net_false:
//                setNetbitRate(2000,false);
//                Collocation.getCollocation().saveNetbitRate(2000);
//
//                break;

            case R.id.file_open:
                Collocation.getCollocation().saveFileOpen(true);
                file_0.setBackground(getResources().getDrawable(R.mipmap.selected2));
                file_1.setBackground(null);

                break;
            case R.id.hander_open:
                Collocation.getCollocation().saveFileOpen(false);
                file_1.setBackground(getResources().getDrawable(R.mipmap.selected2));
                file_0.setBackground(null);

                break;
        }

    }

    /**
     * @param netbitRate
     * @return
     *
     * 获取码率的等级
     */
    private int getClarityState(int netbitRate){
     switch (netbitRate){
         case 1000:
         case 1500:
             return LOW_BITRETE;

         case 2500:
         case 3000:
             return MIDDLE_BITRETE;

         case 4000:
         case 5000:
             return HIGH_BITRETE;

         case 6000:
         case 7000:
             return SUPER_HIGH_BITRETE;

         default:
             if(NetworkUtils.is5GLocalNet()){

                 return HIGH_BITRETE;
             }else{

                 return MIDDLE_BITRETE;
             }

          }
    }


    /**
     * @param clarity
     * @param isFirst
     * 分别对应的码率：1000 2500 4000 6000
     * 如果是2k设备则是：1500 3000 5000 7000
     */

    private void setNetbitRate(int clarity,boolean isFirst){
        if(state!=clarity&&RecordManager.canT&&!isFirst){
            Toast.makeText(this,"投屏清晰度设置下次开启生效！", Toast.LENGTH_SHORT).show();
        }
        state = clarity;
        switch (clarity){
            case LOW_BITRETE:
                bt_0.setBackground(getResources().getDrawable(R.mipmap.selected2));
                bt_1.setBackground(null);
                bt_2.setBackground(null);
                bt_3.setBackground(null);
                if(is2KScreen){
                    Collocation.getCollocation().saveNetbitRate(1500);
                }else{
                    Collocation.getCollocation().saveNetbitRate(1000);
                }
                break;
            case MIDDLE_BITRETE:
                bt_1.setBackground(getResources().getDrawable(R.mipmap.selected2));
                bt_0.setBackground(null);
                bt_2.setBackground(null);
                bt_3.setBackground(null);
                if(is2KScreen){
                    Collocation.getCollocation().saveNetbitRate(3000);
                }else{
                    Collocation.getCollocation().saveNetbitRate(2500);
                }
                break;
            case HIGH_BITRETE:
                bt_2.setBackground(getResources().getDrawable(R.mipmap.selected2));
                bt_1.setBackground(null);
                bt_0.setBackground(null);
                bt_3.setBackground(null);
                if(is2KScreen){
                    Collocation.getCollocation().saveNetbitRate(5000);
                }else{
                    Collocation.getCollocation().saveNetbitRate(4000);
                }
                break;

            case SUPER_HIGH_BITRETE:
                bt_3.setBackground(getResources().getDrawable(R.mipmap.selected2));
                bt_1.setBackground(null);
                bt_2.setBackground(null);
                bt_0.setBackground(null);
                if(is2KScreen){
                    Collocation.getCollocation().saveNetbitRate(7000);
                }else{
                    Collocation.getCollocation().saveNetbitRate(6000);
                }
                break;
        }
    }



    //进行切换后，将设置保存在首选项里
 //   @Override
//    public void onCheckedChanged(CompoundButton buttonView,
//                                 boolean isChecked) {
//        switch (buttonView.getId()) {
//            //允许拉伸
//            case R.id.expand:
//                Collocation.getCollocation().saveExpand(isChecked);
//                Toast.makeText(getApplicationContext(), "设置成功，下次启动生效", Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.chebox:
//                Collocation.getCollocation().savePermit(isChecked);
//                resetTP();
//                break;
//            case R.id.audio_ck:
//                Collocation.getCollocation().saveAudio(isChecked);
//                break;
//
//        }
//
//    }


//    public void setChecked(int a,boolean isFirst){
//        if(frameRate!=a&&canT&&!isFirst){
//            Toast.makeText(this,"图像质量下次开启生效！",Toast.LENGTH_SHORT).show();
//        }
//        if (a==15){
//            bt_0.setBackground(getResources().getDrawable(R.mipmap.selected2));
//            bt_1.setBackground(null);
//            bt_2.setBackground(null);
//            frameRate = 15;
//        }
//        else if (a==20){
//            bt_1.setBackground(getResources().getDrawable(R.mipmap.selected2));
//            bt_0.setBackground(null);
//            bt_2.setBackground(null);
//            frameRate = 20;
//        }
//        else if (a==25){
//            bt_2.setBackground(getResources().getDrawable(R.mipmap.selected2));
//            bt_1.setBackground(null);
//            bt_0.setBackground(null);
//            frameRate = 25;
//        }
//
//    }
    @Override
    protected void onStop(){
        // TODO Auto-generated method stub
        super.onStop();
//        String str = et.getText().toString();
//        if(TextUtils.isEmpty(et.getText().toString().trim())||str.equals("")){
//            QMUtil.getInstance().showToast(this, R.string.name_needed);
//            et.setText(Collocation.getCollocation().getDeviceName());
//            return;
//        }
//
//        if(!oldname.equals(str)){
//            Collocation.getCollocation().saveDeviceName(str);
//            CommandExecutorLancher.getOnlyExecutor().sendNameModify(QMCommander.TYPE_NAME, oldname);
//            CommandExecutorLancherFile.getOnlyExecutor().sendNameModify(QMCommander.TYPE_NAME, oldname);
//        }

    }
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    // 显示软键盘
//    private void showSoftInputView(EditText text) {
//        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).showSoftInput(text, 0);
//    }

    //关闭软键盘
    private void closeSoftInputView(){
            InputMethodManager manager= (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
             if(manager.isActive()){
                 manager.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
             }
    }

    /**
     * 开始投屏
     */
//    private void readyTP(){
//        ScreenFragment.TorS = 1;// TorS=1表示要投屏的操作，TorS=2表示要分享的操作.
//        ScreenFragment.endStop = false;
//        QMUtil.getInstance().showProgressDialog(this, R.string.touping);
//        QMDevice.getInstance().oprationSelectDevice(new BoxOnClickListener() {
//
//            @Override
//            public void onClick(final RootPoint rp) {
//                if (rp.getdType()==0||rp.getdType()==1) {
//                    // 发送手机投屏的指令
//                    CommandExectuorMobile.getOnlyExecutor().connectMessage(QMCommander.TYPE_SCREEN_M, rp.getAddress());
//                }
//                else if(rp.getdType()==2){
//                    //发送pc投屏指令
//                    CommandExecutorLancher.getOnlyExecutor().connectPcMessage(QMCommander.TYPE_SCREEN_P, rp.getAddress());
//                }
//                else {
//                    // 首先请求屏幕投屏有没有打开 监听接口是run2（）方法中
//                    CommandExecutorLancher.getOnlyExecutor().controlMessage(QMCommander.SCREEN_OPEN, rp.getAddress());
//                }
//                rp.setResponse(false);// 多个设备投屏没有响应的处理，可以直接使用handler来处理
//                rp.startHandler(new PositionListener() {
//
//                    @Override
//                    public void method(int position) {
//                        QMUtil.getInstance().closeDialog();
//                        if (QMDevice.getInstance().hasSelectDevice()) {
//                            rp.setPlay(false);
//                            ScreenFragment.boxAdapter.notifyDataSetChanged();
//                            if (rp.getdType() == -1)
//                                rp.setLcount(-1);
//                        }
//                        QMUtil.getInstance().showToast2(SettingActivity.this, rp.getName() + "\t" + getString(R.string.no_response));
//                    }
//                });
//            }
//        });
//    }

}

