package com.shareshow.airpc.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.shareshow.aide.R;
import com.shareshow.aide.widget.DirectionKeyView;
import com.shareshow.airpc.QMCommander;
import com.shareshow.airpc.socket.command.CommandControlListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RemoteControl extends Fragment implements DirectionKeyView.OnDirectionClickListener{
	
	@BindView(R.id.direction_center)
    View direction_center;
//	@BindView(R.id.exit_box)
//	 Button exit_box;
	@BindView(R.id.yaokong_back)
    View yaokong_back;
	@BindView(R.id.yaokong_home)
	View yaokong_home;
	@BindView(R.id.yaokong_memu)
	View yaokong_memu;
//	@BindView(R.id.jingyin)
//	Button jingyin;
//	@BindView(R.id.minus_yinliang)
//	Button minus_yinliang;
//	@BindView(R.id.add_yinliang)
//	Button add_yinliang;
//	@BindView(R.id.minus_liangdu)
//    Button minus_liangdu;
//	@BindView(R.id.add_liangdu)
//	Button add_liangdu;
//	@BindView(R.id.direction_top)
//Button direction_top;
//	@BindView(R.id.direction_left)
//    Button direction_left;
//	@BindView(R.id.direction_right)
//    Button direction_right;
//	@BindView(R.id.direction_buttom)
//    Button direction_buttom;
	@BindView(R.id.direction_key)
    DirectionKeyView directionKeyView;
	private CommandControlListener mListener;


	public RemoteControl() {

	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
		View vv=inflater.inflate(R.layout.fragment_remotecontrol, container, false);
		//x.view().inject(this,vv);
		ButterKnife.bind(this,vv);
		inint();
		return vv;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if(activity instanceof CommandControlListener){
			this.mListener = (CommandControlListener)activity;
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener=null;
	}

	private void inint() {
		// TODO Auto-generated method stub
		direction_center.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				control(QMCommander.YAOKONG.TYPE_YAOKONG_OK_LONG);
				return true;
			}
		});
		directionKeyView.setDirectionClickListener(this);
	}

//	@OnClick(R.id.direction_top)//向上
//	public void directionTop(View v){
//		control(QMCommander.YAOKONG.TYPE_YAOKONG_UP);
//	}
//	@OnClick(R.id.direction_left)//向左
//	public void directionLeft(View v){
//		control(QMCommander.YAOKONG.TYPE_YAOKONG_LEFT);
//	}
//	@OnClick(R.id.direction_right)//向右
//	public void directionRight(View v){
//		control(QMCommander.YAOKONG.TYPE_YAOKONG_RIGHT);
//	}
//	@OnClick(R.id.direction_buttom)//向下
//	public void directionButtom(View v){
//		control(QMCommander.YAOKONG.TYPE_YAOKONG_DOWN);
//	}
	@OnClick(R.id.direction_center)//OK
	public void directionCenter(View v){
		control(QMCommander.YAOKONG.TYPE_YAOKONG_OK);
	}
	
	
//	@OnLongClick(R.id.direction_center)//OK长按
//	private void directionCenter_Long(View v){
//		control(QMCommander.YAOKONG.TYPE_YAOKONG_OK_LONG);
//	}
//	@OnClick(R.id.exit_box)//退出
//	public void exitBox(View v){
//		control(QMCommander.YAOKONG.TYPE_YAOKONG_GUANJI);
//	}
	@OnClick(R.id.voice_static)//静音
	public void jingYin(View v){
		control(QMCommander.YAOKONG.TYPE_YAOKONG_JINGYIN);
	}
	@OnClick(R.id.yaokong_back)//后退
	public void yaokongBack(View v){
		control(QMCommander.YAOKONG.TYPE_YAOKONG_BACK);
	}
	@OnClick(R.id.yaokong_home)//home键
	public void yaokongHome(View v){
		control(QMCommander.YAOKONG.TYPE_YAOKONG_HOME);
	}
	@OnClick(R.id.yaokong_memu)//菜单键
	public void yaokongMemu(View v){
		control(QMCommander.YAOKONG.TYPE_YAOKONG_MENU);
	}
	@OnClick(R.id.voice_minus)//减音量
	public void minusYinliang(View v){
		control(QMCommander.YAOKONG.TYPE_YAOKONG_MINUS_YINLIANG);
	}
	@OnClick(R.id.voice_pluse)//加音量
	public void addYinliang(View v){
		control(QMCommander.YAOKONG.TYPE_YAOKONG_ADD_YINLIANG);
	}
//	@OnClick(R.id.minus_liangdu)//减亮度
//	public void minusLiangdu(View v){
//		control(QMCommander.YAOKONG.TYPE_YAOKONG_MINUS_LIANGDU);
//	}
//	@OnClick(R.id.add_liangdu)//加亮度
//	public void addLiangdu(View v){
//		control(QMCommander.YAOKONG.TYPE_YAOKONG_ADD_LIANGDU);
//	}
//

	public void control(int rec){
		if(mListener!=null){
			mListener.control(rec,1);
		}
	}

	@Override
	public void onTopKeyClick() {
		control(QMCommander.YAOKONG.TYPE_YAOKONG_UP);
	}

	@Override
	public void onLeftKeyClick() {
		control(QMCommander.YAOKONG.TYPE_YAOKONG_LEFT);
	}

	@Override
	public void onRightKeyClick() {
		control(QMCommander.YAOKONG.TYPE_YAOKONG_RIGHT);
	}

	@Override
	public void onButtomKeyClick() {
		control(QMCommander.YAOKONG.TYPE_YAOKONG_DOWN);
	}
}
