package com.shareshow.airpc;

public class QMCommander {

	//Box端口
	public static final int PORT_Box=20001;
	//Lancher端口
	public static final int PORT_Lancher=20002;
	//Lancher文件共享端口
	public static final int PORT_LancherFile=20003;
	//手机端口
	public static final int PORT_Mobile=20004;
	
	//==========================Box端====================================
	/** 连接 */
	public static final int TYPE_SET_CLIENT_MSG = 2001;
	/**
	 * 开始投屏的指令
	 */
	public static final int TYPE_START_PLAY = 1001;
	/**
	 * 停止投屏的指令
	 */
	public static final int TYPE_STOP_PLAY = 1003;
	/**
	 * 投屏成功的指令
	 */
	public static final int SEND_SCREEN_STATE = 300;
	/** 
	 * 投屏的心跳 
	 */
	public static final int TYPE_HEARTBEAT = 100;
	/** 
	 *盒子那边正常退出桌面后发送的接口 
	 **/
	public static final int TYPE_MOUSE_EXIT = 3000;
	/** 
	 * Box端主动投屏过来手机需要有个tip显示表示屏幕滑动过了，否则投屏不上
	 */
	public static final int TYPE_MOUSE_MODEL = 500;
	/**
	 * 验证
	 */
	public static final int TYPE_VALIDATE=2002;
	public static final int TYPE_VALIDATE2=400;

	//===================Lancher指令======================
	//搜索设备
	public static final int TYPE_SEARCH=102;

	public static final int TYPE_SEARCH_SCAN_PC=103;
	//连接设备
	public static final int TYPE_CONNECT=201;
	//取消链接
	public static final int TYPE_UNCONNECT=202;
	//更改手机名称
	public static final int TYPE_NAME =203;
	//开始远程控制
	public static final int TYPE_OPEN_CONTROL=300;
	//关闭远程控制
	public static final int TYPE_CLOSE_CONTROL=700;
	//检测远程控制是否正在被操作
	public static final int TYPE_JIANCE_CONTROL=701;
	//屏幕投屏有没有打开
	public static final int SCREEN_OPEN =800;
	//密码更改
	public static final int PASSWD_Alter =801;
	//收到获取手机内存大小的消息
	public static final int MEMORY_Request=900;
	//回复手机内存大小的消息
	public static final int MEMORY_Response=901;
	//与任盒保持心跳
	public static final int TYPE_BOX_ONLINE=702;
	//与手机端文件共享保持心跳
	public static final int TYPE_MOBILE_FILE_ONLINE=709;
	//与PC端文件共享保持心跳
	public static final int TYPE_PC_FILE_ONLINE=710;
	//投屏到pc
	public static final int TYPE_SCREEN_P=903;
	//收到的投屏到pc返回的指令
	public static final int TYPE_PC_TP=904;
	//发送关闭pc投屏的指令
	public static final int TYPE_STOP_PC=905;
	//收到pc端退出的指令
	public static final int TYPE_EXIT_PC=906;
	//收到pc的投屏指令
	public static final int TYPE_PCTP=601;
	//退出分享发给pc的指令
	public static final int TYPE_EXIT_SHARED=602;
	//返回给pc端的投屏成功的指令
	public static final int TYPE_SUCCESS_PC=603;
	//pc端发来的关闭投屏的指令
	public static final int TYPE_PC_STOP=604;
	//pc端发来的分享投屏的指令
	public static final int TYPE_PC_SHARED=605;
	//发给PC端拒绝它分享屏幕
	public static final int TYPE_SHARED_REFUSED=609;
	//发给pc端的分享投屏的指令
	public static final int TYPE_COVER_SHARED_PC=607;
	//接收pc端的拒绝投屏的指令
	public static final int TYPE_SCREEN_REFUSED=608;
	//接收pc端的拒绝分享的指令
	public static final int TYPE_PCSHARED_REFUSED=609;



	//===================手机指令======================
	//搜索
	public static final int TYPE_SEARCH_M=101000;
	//扫描情况下的开始搜索
	public static final int TYPE_SEARCH_SCAN = 101001;
	//开始投屏
	public static final int TYPE_SCREEN_M=102000;
	//投屏方主动断开投屏
	public static final int TYPE_STOP_M=103000;
	//分享方断开投屏
	public static final int TYPE_SHARE_STOP_M=104000;
	//心跳保持		
	public static final int TYPE_HEARTBEAT_M=106000;
	//禁止其他手机投屏,此手机现在在进行其他的操作的标示
	public static final int TYPE_FORBIDDEN_M=105000;
	//投屏被覆盖,在A在B上的投屏被C挤掉以后，B发送给A的
	public static final int TYPE_COVER_M=109000;
	//提示对方进行投屏
	public static final int TYPE_NEED_SHARED = 108000;
	//拒绝对方的分享请求
	public static final int TYPE_REFUSED_M = 107000;

	//xi新的分享协议
	 public static final int TYPE_STOP_SHARE_SCREEN=110000;
	//断开分享
	public static final int TYPE_SCREEN_SCREEN_SUCCESS=110001;
	//分享成功
	public static final int TYPE_COVER_SCREEN_SHARE=110002;
	//请求手机重点（视频源切换了）
	public static final int TYPE_SCREEN_REQUEST_FRAME=110003;
	//请求I帧
	public static final int TYPE_NO_SCREEN_M=110004;
	//对方没有视频源


	//XML字段相关协议
	    public class XML{

		public static final String XTXK = "xtxk";
		
		public static final String XTXK_ANDROID = "xtxk.ipz.android";
	
		public static final String RESPONSE = "response";
		
		public static final String REQUEST = "request";
		
		public static final String SEVER= "server";
		
		public static final String HOST= "host";
		
		public static final String SETTING= "setting";
		
		public static final String CLIENT= "client";
		
		public static final String POS= "pos";
		
	}
	
	//遥控相关协议
	public class YAOKONG{
		//上
		public static final int TYPE_YAOKONG_UP=301;
		//下
		public static final int TYPE_YAOKONG_DOWN=302;
		//左
		public static final int TYPE_YAOKONG_LEFT=303;
		//右
		public static final int TYPE_YAOKONG_RIGHT=304;
		//OK
		public static final int TYPE_YAOKONG_OK=305;
		//长按OK键
		public static final int TYPE_YAOKONG_OK_LONG=315;
		//音量增加
		public static final int TYPE_YAOKONG_ADD_YINLIANG=306;
		//音量减小
		public static final int TYPE_YAOKONG_MINUS_YINLIANG=307;
		//增加亮度
		public static final int TYPE_YAOKONG_ADD_LIANGDU=308;
		//减少亮度
		public static final int TYPE_YAOKONG_MINUS_LIANGDU=309;
		//关机
		public static final int TYPE_YAOKONG_GUANJI=310;
		//静音
		public static final int TYPE_YAOKONG_JINGYIN=314;
		//返回
		public static final int TYPE_YAOKONG_BACK=311;
		//回到主页
		public static final int TYPE_YAOKONG_HOME=312;
		//菜单
		public static final int TYPE_YAOKONG_MENU=313;
	}
	
	//鼠标相关协议
	public class SHUBIAO{
		//鼠标左键
		public static final int TYPE_SHUBIAO_LEFT=401;
		//鼠标右键
		public static final int TYPE_SHUBIAO_RIGHT=402;
		//滚轮上
		public static final int TYPE_SHUBIAO_UP=403;
		//滚轮下
		public static final int TYPE_SHUBIAO_DOWN=404;
		//滑动鼠标
		public static final int TYPE_SHUBIAO_MOVE=405;
	}
	
	
	//键盘相关协议
	public class JIANPAN{
		//Tab键
		public static final int TYPE_JIANPAN_TAB=501;
		//保存当前编辑文档
		public static final int TYPE_JIANPAN_EDIT=502;
		//关闭
		public static final int TYPE_JIANPAN_CLOSE=503;
		//send
		public static final int TYPE_JIANPAN_SEND = 504;
		// delete
		public static final int TYPE_JIANPAN_DELETE = 505;
	}

	
	
	//盒子设置
	public class SETTING{
		//启动任易屏
		public static final int TYPE_SETTING_START=601;
		//我的工作
		public static final int TYPE_SETTING_MYJOB=602;
		//热点设置
		public static final int TYPE_SETTING_REDIAN=603;
		//Wi-Fi设置
		public static final int TYPE_SETTING_WIFI=604;
	}


	public static String icon=">_<";
}
