package com.shareshow.airpc.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shareshow.aide.R;
import com.umeng.analytics.MobclickAgent;
import com.shareshow.airpc.Collocation;
import com.shareshow.airpc.QMCommander;
import com.shareshow.airpc.fragment.MouseControl;
import com.shareshow.airpc.fragment.RemoteControl;
import com.shareshow.airpc.model.RootPoint;
import com.shareshow.airpc.ports.BoxOnClickListener;
import com.shareshow.airpc.socket.command.CommandControlListener;
import com.shareshow.airpc.socket.command.CommandExecutorLancher;
import com.shareshow.airpc.socket.command.CommandListenerLancher;
import com.shareshow.airpc.socket.common.QMDevice;
import com.shareshow.airpc.util.NetworkUtils;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 遥控控制
 *
 * @author tanwei
 */
public class ControlActivity extends Activity implements CommandListenerLancher,CommandControlListener {

    @BindView(R.id.back)
    LinearLayout back;//返回键
    @BindView(R.id.text)
    TextView text;//顶部中间的文字
    @BindView(R.id.remout_img)
    ImageView remoutImg;
    @BindView(R.id.remout_text)
    TextView remoutText;
    @BindView(R.id.mouse_img)
    ImageView mouseImg;
    @BindView(R.id.mouse_text)
    TextView mouseText;
    @BindView(R.id.remount)
    LinearLayout remount;
    //	private QMPopupWindow right_popupWindow;// 弹出右上角的菜单
//	@BindView(R.id.my_anyscreen)
//	 LinearLayout my_anyscreen;//启动我的投屏
//	@BindView(R.id.my_job)
//	 LinearLayout my_job;//启动我的工作
    private RootPoint rp;//盒子的bean对象
    private Timer timer;//定时器用来检测与lancher有没有时刻保持联系
    private int receiveCount = 0;
    public static final int code2 = 887;
    private RemoteControl remoteControl;
    private FragmentManager manager;
    private MouseControl mouseControl;
    private int index;

    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);
        //x.view().inject(this);
        ButterKnife.bind(this);
        manager = getFragmentManager();
        initFragment(savedInstanceState);
        initData();
    }

    private void initFragment(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            index = (int) savedInstanceState.get("index");
            remoteControl = (RemoteControl) manager.findFragmentByTag("remoteControl");
            mouseControl = (MouseControl) manager.findFragmentByTag("mouseControl");
        }
    }


    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    private void initData() {
        back.setVisibility(View.VISIBLE);
        //initPopupWindow();
        // text.setText(getResources().getString(R.string.remotecontrol));
        rp = (RootPoint) getIntent().getSerializableExtra("rootPoint");
        timer = new Timer();
        timer.schedule(new GetPCbyTimer(), 5 * 100, 2 * 1000);
        CommandExecutorLancher.getOnlyExecutor().setListener(this);//注册20002端口的监听
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//保持屏幕常亮
        showFragment(index);

        //getFragmentManager().beginTransaction().replace(R.id.frame, new RemoteControl()).commit();
    }

    private void showFragment(int index) {
        FragmentTransaction transaction = manager.beginTransaction();
        hideFragment(transaction);
        switch (index) {
            case 0:
                if (remoteControl == null) {
                    remoteControl = new RemoteControl();
                    transaction.add(R.id.frame, remoteControl, "remoteControl");
                } else {
                    transaction.show(remoteControl);
                }

                break;

            case 1:
                if (mouseControl == null) {
                    mouseControl = new MouseControl();
                    transaction.add(R.id.frame, mouseControl, "mouseControl");
                } else {
                    transaction.show(mouseControl);
                }

                break;
        }
        setChange(index);

        transaction.commit();
    }

    private void setChange(int index) {
        switch (index) {
            case 0:
                remoutText.setTextColor(getResources().getColor(R.color.textselector_));
                remoutImg.setImageDrawable(getResources().getDrawable(R.drawable.remout_click_select));
                mouseText.setTextColor(getResources().getColor(R.color.textselector));
                mouseImg.setImageDrawable(getResources().getDrawable(R.drawable.mouse_select));
                text.setText("遥控器");
                break;

            case 1:
                mouseText.setTextColor(getResources().getColor(R.color.textselector_));
                mouseImg.setImageDrawable(getResources().getDrawable(R.drawable.mouse_click_select));
                remoutText.setTextColor(getResources().getColor(R.color.textselector));
                remoutImg.setImageDrawable(getResources().getDrawable(R.drawable.remout_select));
                text.setText("鼠标");
                break;

        }
    }


    private void hideFragment(FragmentTransaction transaction) {
        if (remoteControl != null) {
            transaction.hide(remoteControl);
        }

        if (mouseControl != null) {
            transaction.hide(mouseControl);
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("index", index);
        super.onSaveInstanceState(outState);
    }

    @OnClick(R.id.back)
    public void back(View v) {
        finish();
    }

    @Override
    public void control(int cmd, int count) {
        for (int i = 0; i < count; i++) {
            CommandExecutorLancher.getOnlyExecutor().controlMessage(cmd,
                    rp.getAddress());
        }
    }

    @Override
    public void scroll(int cmd, float moveX, float moveY) {
        CommandExecutorLancher.getOnlyExecutor().scrollMessage(cmd,moveX,moveY,rp.getAddress());
    }


    @OnClick(R.id.remount)
    public void remout() {
        showFragment(0);
    }

    @OnClick(R.id.mouse)
    public void mouse() {
        showFragment(1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        control(QMCommander.TYPE_CLOSE_CONTROL, 3);//取消控制的链接
        if (timer != null)
            timer.cancel();
    }

    /**
     * 此功能一方面给lancher发701消息，说明此手机在控制盒子，另一方面判断lancher那边有没有在线
     */
    private class GetPCbyTimer extends TimerTask {

        @Override
        public void run() {
            boolean isConnected = NetworkUtils.isNetworkConnected();
            if (isConnected) {
                receiveCount++;
                //监听接口是netc（）
                CommandExecutorLancher.getOnlyExecutor().controlMessage(QMCommander.TYPE_JIANCE_CONTROL,
                        rp.getAddress());
                if (receiveCount == 3) {
                    Intent intent = new Intent();
                    setResult(code2, intent);
                    ControlActivity.this.finish();
                }
            } else {
                Intent intent = new Intent();
                setResult(code2, intent);
                ControlActivity.this.finish();
            }
        }

    }

    @Override
    public void searchLancher(RootPoint rootPoint) {

    }


    @Override
    public void connectLancher(RootPoint rootPoint) {

    }


    @Override
    public void controlLancher(RootPoint rootPoint) {

    }


    @Override
    public void screenOpenLancher(RootPoint rootPoint) {

    }


    @Override
    public void passwdAlterLancher(final RootPoint rootPoint) {
        if (rp.getAddress().equals(rootPoint.getAddress())) {//当前控制的设备密码改变了
            if (rootPoint.getEnablepwd().equals("true")) {//有密码  回到主页
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable("rootPoint", rp);
                intent.putExtras(bundle);
                setResult(code2, intent);
                finish();
            } else {
                QMDevice.getInstance().oprationSameDevice(rootPoint, new BoxOnClickListener() {

                    @Override
                    public void onClick(RootPoint rp) {
                        rp.setEnablepwd("false");
                    }
                });
            }
        } else {//其他设备  密码改变
            QMDevice.getInstance().oprationSameDevice(rootPoint, new BoxOnClickListener() {
                @Override
                public void onClick(RootPoint rp) {
                    rp.setEnablepwd(rootPoint.getEnablepwd());
                    Collocation.getCollocation().savePassWd(rp.getUuid(), "");
                }
            });
        }
    }

    @Override
    public void controlHeartBeatLancher(RootPoint rootPoint) {
        receiveCount = 0;
    }

    @Override
    public void touPingPc(RootPoint rp) {
        // TODO Auto-generated method stub

    }

    @Override
    public void stopPc(RootPoint rp) {
        // TODO Auto-generated method stub

    }

    @Override
    public void pcTouPing(RootPoint rp) {

    }

    @Override
    public void pcCoverShare(RootPoint rp) {

    }


}
