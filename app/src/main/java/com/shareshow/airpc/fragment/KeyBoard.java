package com.shareshow.airpc.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.shareshow.aide.R;
import com.shareshow.airpc.QMCommander;
import com.shareshow.airpc.model.RootPoint;
import com.shareshow.airpc.socket.command.CommandExecutorLancher;

public class KeyBoard extends Fragment implements OnClickListener {
	
	private TextView tab,save,close;
	private View vv;
	private RootPoint rootPoint;
	private EditText edit;
	private Button send,delete;
	
	public KeyBoard() {
	}

	@SuppressLint("ValidFragment")
	public KeyBoard(RootPoint rootPoint) {
		this.rootPoint=rootPoint;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		if(vv==null)
			vv=inflater.inflate(R.layout.jianpan, container, false);
		return vv;
		
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();
		initEvent();
	}

	private void initEvent() {
		tab.setOnClickListener(this);
		save.setOnClickListener(this);
		close.setOnClickListener(this);
		send.setOnClickListener(this);
		delete.setOnClickListener(this);
	}

	private void initView() {
		tab=(TextView) vv.findViewById(R.id.tab);
		save=(TextView) vv.findViewById(R.id.save);
		close=(TextView) vv.findViewById(R.id.close);
		edit=(EditText) vv.findViewById(R.id.edit);
		send= (Button) vv.findViewById(R.id.send);
		delete=(Button) vv.findViewById(R.id.delete);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tab:
			control(QMCommander.JIANPAN.TYPE_JIANPAN_TAB);
			break;
		case R.id.save:
			control(QMCommander.JIANPAN.TYPE_JIANPAN_EDIT);
			break;
		case R.id.close:
			control(QMCommander.JIANPAN.TYPE_JIANPAN_CLOSE);
			break;
		case R.id.send:
			String edits=edit.getText().toString();
			if(edits.equals("")){
				return ;
			}
			control2(QMCommander.JIANPAN.TYPE_JIANPAN_SEND,edits);
			edit.setText("");
			break;
		case R.id.delete:
			control(QMCommander.JIANPAN.TYPE_JIANPAN_DELETE);
			break;
		}
		
	}
	public void control(int rec){
		CommandExecutorLancher.getOnlyExecutor().controlMessage(rec,
				rootPoint.getAddress());
	}
	public void control2(int rec,String info){
		CommandExecutorLancher.getOnlyExecutor().controlMessageInput(rec,
				rootPoint.getAddress(), info);
	}
}
