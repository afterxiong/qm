package com.shareshow.airpc.socket.common;

import com.shareshow.airpc.model.RootPoint;
import com.shareshow.airpc.ports.BoxOnClickListener;

import java.util.ArrayList;

/**
 * 设备数据的操作  WFDevice.getInstance().
 * @author tanwei
 */
public class QMDevice{

	private static QMDevice instance;
	
	private QMDevice(){
	}
	
	public static QMDevice getInstance(){
		if(instance==null)
			instance=new QMDevice();
		return instance;
	}
	
	//保存设备的数据
	private ArrayList<RootPoint> al = new ArrayList<RootPoint>();

	//获取数据
	public ArrayList<RootPoint> getAl() {
		return al;
	}
	//获取数据大小
	public int getSize(){
		return al.size();
	}
	//清空数据
	public void clear(){
		al.clear();
	}
	//通过position获取对象
	public RootPoint getRootPoint(int position){
		return al.get(position);
	}
	//通过address获取相同的对象
	public RootPoint getSameRootPoint(RootPoint rootPoint){
		for (int i = 0; i < al.size(); i++) {
			if (al.get(i).getAddress().equals(rootPoint.getAddress())) {
				return al.get(i);
			}
		}
		return null;
	}

	public RootPoint getSameRootPoint(String address){
		for (int i = 0; i < al.size(); i++) {
			String listAddress =al.get(i).getAddress();
			if (listAddress.equals(address)) {
				return al.get(i);
			}
		}
		return null;
	}



	//添加设备
	public void add(RootPoint rootPoint){
		if("".equals(rootPoint.getName()))
			return ;
		for (RootPoint p : al){
			// 如果搜索到得盒子与本类保存的盒子数据中有相同的就不添加到集合中，修改相关属性
			if (rootPoint.getAddress().equals(p.getAddress())) {
				p.setOnline(true);// 设置此盒子是在线的
				p.setName(rootPoint.getName());// 更新盒子的名称
				p.setNew(rootPoint.isNew());
				p.setEnablepwd(rootPoint.getEnablepwd());// 更新是否需要密码
				return;
			}
		}
		//集合没有才添加
		al.add(rootPoint);
	}
	
	//通过position移除对象
	public void remove(int position){
		al.remove(position);
	}
	
	//=======================================对象的操作======================================
	//返回有没有正在投屏的盒子或者PC
	public boolean hasBoxScreen(){
		for (int i = 0; i <getSize(); i++) {
			if(getRootPoint(i).isTouPing()&&getRootPoint(i).getdType()==-1||getRootPoint(i).isTouPing()&&getRootPoint(i).getdType()==2){
				return true;
			}
		}
		return false;
	}
	
	//返回有没有正在投屏的手机
	public boolean hasMoblieScreen(){
		for (int i = 0; i < getSize(); i++) {
			if(getRootPoint(i).isTouPing()&&(getRootPoint(i).getdType()==0||getRootPoint(i).getdType()==1)){
				return true;
			}
		}
		return false;
	}
	
	//有没有选中的设备
	public boolean hasSelectDevice(){
		for (int i = 0; i <getSize(); i++) {
			if(getRootPoint(i).isPlay())
				return true;
		}
		return false;
	}
	//选中设备的个数
	public int selectDeviceCount(){
		int count=0;
		for (int i = 0; i <getSize(); i++) {
			if(getRootPoint(i).isPlay())
				count++;
		}
		return count;
	}

	//有没有投屏的设备
	public boolean hasScreenDevice(){
		for (int i = 0; i <getSize(); i++) {
			if(getRootPoint(i).isTouPing())
				return true;
		}
		return false; 
	}

	//对投屏的设备进行操作
	public void oprationScreenDevice(BoxOnClickListener boxOnClickListener){
		for (int i = 0; i <getSize(); i++) {
			if(getRootPoint(i).isTouPing())
				boxOnClickListener.onClick(getRootPoint(i));
		}
	}

	//对选中的设备进行操作
	public void oprationSelectDevice(BoxOnClickListener boxOnClickListener){
		for (int i = 0; i <getSize(); i++){
			if(getRootPoint(i).isPlay()){
				boxOnClickListener.onClick(getRootPoint(i));
			  //	break;
			}
		}
	}
	//操作address相同的对象
	public void oprationSameDevice(RootPoint rootPoint,BoxOnClickListener boxOnClickListener){
		for (int i = 0; i <getSize(); i++){
			if (getRootPoint(i).getAddress().equals(rootPoint.getAddress())){
				boxOnClickListener.onClick(getRootPoint(i));
				return;
			}
		}
	}
	//获取选中的设备
	public ArrayList<RootPoint> getSelectDevices(){
		ArrayList<RootPoint> roots= new ArrayList<>();
		for (int i = 0; i <getSize(); i++){
			if(getRootPoint(i).isPlay())
				roots.add(getRootPoint(i));
		}
		return roots;
	}

	public RootPoint getSelectDevice(){
		ArrayList<RootPoint> roots=getSelectDevices();
		if(roots.size()==0){
			return null;
		}else{
			return roots.get(0);
		}
	}

	public void canclePlay(){
		for (int i = 0; i <getSize(); i++) {
			getRootPoint(i).setPlay(false);
		}
	}

	//设置所有设备不在线
	public void setOffline(){
		for (int i = 0; i <getSize(); i++) {
			getRootPoint(i).setOnline(false);
		}
	}
	//移除不在线设备
	public void removeOffline(){
		for (int i = 0; i < getSize(); i++) {
			if (!getRootPoint(i).isOnline())
				 remove(i);
		}
	}
	//设置进度条属性
	public void setProgress(){
		for (int i = 0; i <getSize(); i++) {
			getRootPoint(i).setProgress(false);
		}
	}
	//取消与任盒文件共享的链接
	public void cancelFileShare(){
		for (int i = 0; i < getSize(); i++){
			if (getRootPoint(i).isPlay()&&getRootPoint(i).getdType()==-1) {//取消与任盒文件共享的链接
				getRootPoint(i).setLcount(-1);
			}
		}
	}

	public void removeAllPlay() {
		for (int i = 0; i < getSize(); i++){
			if(getRootPoint(i).isPlay()){
				getRootPoint(i).setPlay(false);
			}
		}
	}
}
