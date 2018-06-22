package com.shareshow.airpc.activity;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.shareshow.aide.R;
import com.shareshow.airpc.adapter.SelectedDeviceListAdapter;
import com.shareshow.airpc.model.RootPoint;
import com.shareshow.airpc.ports.ConnectFTPListener;
import com.shareshow.airpc.socket.common.FTPUtil;
import com.shareshow.airpc.socket.common.QMDevice;
import com.shareshow.airpc.util.QMDialog;
import com.shareshow.airpc.util.QMUtil;


/**
 * 显示多个设备----连接FTP服务
 * @author tanwei
 *
 */
public abstract class ConnectFTPActivity extends ConnectLancherActivity {
	

	private QMDialog listDialog;
	
	@Override
	protected void onResume(){
		super.onResume();
	}
	
	/**
	 * 显示可用的设备列表
	 */
	public void showDevice() {
		View vv = View.inflate(this, R.layout.device_list_dialog, null);
		ListView listView=(ListView) vv.findViewById(R.id.list);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				onDeviceItemClick(QMDevice.getInstance().getAl().get(position));
			}
		});
		BaseAdapter adapter=new SelectedDeviceListAdapter<RootPoint>(this,QMDevice.getInstance().getAl());
		listView.setAdapter(adapter);
		listDialog=new QMDialog(this,vv,true);
	}
	
	/**
	 * 点击设备列表事件
	 * @param rp
	 */
	private void onDeviceItemClick(final RootPoint rp) {
		//手机设备或PC设备不用密码验证直接连接设备
		if(rp.getdType()==0||rp.getdType()==1){
			connectFTPServer(rp, false);
			return ;
		}
		if (rp.getEnablepwd().equals("true")) {
			passwdVertify(rp);
		}else{
			connectFTPServer(rp,true);
		}
	}
	
	/**
	 * 连接远程服务
	 * @param rp
	 * @param flag
	 */
	protected void connectFTPServer(final RootPoint rp,final boolean flag) {
		if(listDialog!=null)
			listDialog.dismiss();
		QMUtil.getInstance().showProgressDialog(this, R.string.connect_divice);
		FTPUtil.getInstance(rp).connectFTP(new ConnectFTPListener() {

			@Override
			public void success(){
				QMUtil.getInstance().closeDialog();
				rp.setPlay(true);
				if(flag){//没有密码验证的任盒需要做连接处理
					sendEmptyPasswdConnect(rp);
				}
				connectFTPSuccess();
			}

			@Override
			public void fail(int state) {
				QMUtil.getInstance().closeDialog();
				QMUtil.getInstance().showToast(ConnectFTPActivity.this, R.string.upload_fail_error);
			}
		});
	}
	

	@Override
	public void passwdVertifyCallBack(RootPoint rp) {
		connectFTPServer(rp,false);
	}
	
	public abstract void connectFTPSuccess();

}
