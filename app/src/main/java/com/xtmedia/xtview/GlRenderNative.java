package com.xtmedia.xtview;

import android.util.Log;
import android.view.Surface;

@SuppressWarnings("unused,JniMissingFunction")
public class GlRenderNative {

	static {
		try {
//			System.loadLibrary("gnustl_shared");

//			System.loadLibrary("ffmpeg");
			System.loadLibrary("xt_config");
			System.loadLibrary("rv_adapter");
			System.loadLibrary("xt_media_server");
			System.loadLibrary("xt_media_client");
//			System.loadLibrary("common_lib");
//			System.loadLibrary("xt_media_player");
			System.loadLibrary("xt_router");
			System.loadLibrary("SimplePlayer-jni");

 			Log.e("GlRenderNative", "static block");
		} catch (UnsatisfiedLinkError e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

/*********************************************************************************
 *                            jni interface
 * *******************************************************************************/
	/*
	 * ��ȾͼƬ
	 */
	public static native boolean render(int nIndexTask);

	/*
	 * ����
	 */
	public static native void harizonplay();
	
	/*
	 * ����
	 */
	public static native void vericalplay();

	
	public native static long     SetVideoSurface(Surface surface);
	
	/*
	 * ��ͼ ����ͼƬ��׺.bmp
	 */
	public static native boolean capturepic(int nTaskIndex , String scappath);
	
	public static native boolean StartRecord(int nTaskIndex , String scappath);
	
	public static native boolean StopRecord(int nTaskIndex);
	
	/*
	 * ����
	 */
	public static native boolean ClearScreen(int nTaskIndex);
	
	/* ������Ƶ��ʾ��Χ
	 * 
	 */
	public static native void    setscreenrect(int x, int y, int w,int h,int screentype);
	
	/*
	 * ͨ��URL�򿪲�����
	 * url:          ý������url
	 * ntaskindex:   ������ 
	 */
	public static native int     openurl(String url , int x, int y, int w, int h);
	
	//�����ӵ�
	public static native int     setviewpoint(int nTaskIndex, int x,int y,int w,int h);
	
	//�����ӵ�
	public static native int     setdisplayrect(int nTaskIndex, int x,int y,int w,int h);
	
	public static native int     opensingleurl(String url);
	 
	/*
	 * UDP�㲥
	 */
	public static native int     openstd(String url , long holderobj);
	
	/*
	 * udp�����㲥
	 */
	
	public static native int     opensingle(String ip, int nport , int channel, int x, int y , int w, int h);
	
	public static native int     opensinglemulcast(String ip, int nport , int channel, String mulcastip, int mulcastport, int x, int y , int w, int h);

	public static native int     opensingleex(String ip, int nport , int channel, long holderobj);
	
	/*
	 * udp�����رյ㲥
	 * lobj Ϊ opensinglepriv����ֵ
	 */
	public static native int    closesingle(int lobj);
	

	/*
	 * ��ʼ����
	 * channel     ͨ����
	 * nTaskIndex  �����
	 */
	public static native int    startplay(int channel,int x , int y ,int w, int h);

	/*
	 * ֹͣ������������������·ý���ת��Ҳ��ֹͣ
	 * channel     ͨ����
	 */
	public static native void   stopplay(int channel);
	
	
	public static native void   mediaSetAudioCallback(int nTaskIndex , Object obj);
	
	public static native void   mediaResetAudioCallback(int nTaskIndex);
	
	/*
	 *�쳣�Ͽ�֪ͨ
	 *����ԭ��Ϊ void breaktrack(int ctx,int handle); 
	 */
	public static native void   rtRegisterDataBreakCallback(int nTimeOut, Object obj, long ctx);
	
	public static native int    setStreamDownCallback(int index, int milltime);
	
	/*
	 * ֹͣrtsp����
	 * nTaskIndex �����
	 */
	public static native void   stoprtspplay(int nTaskIndex);
	
	/*
	 * set display mode 
	 * if bpps = 0, the pictute is match with src frames, or else is stretched
	 */
	public static native void   setdistype(int nType, boolean bPPs);
	
	/*
	 * ���÷�������
	 * screentype         ��Ļ����   0/1/2/3/4 -- 1��/4����/9����/16����/2����
	 */
	public static native void   setscreenlayout(int srceentype);
	
	/*
	 * ����ȫ��
	 * nTaskIndex �����
	 */
	public static native void   setfullsrceen(int nTaskIndex);
	
	/*
	 * �˳�ȫ��
	 * nTaskIndex �����
	 */
	public static native void   exitfullsrceen(int nTaskIndex);
	
	/**
	 * 
	 * @param udp_bind_ip
	 * @param udp_bind_port
	 * @param data_port_start
	 * @param data_port_num
	 * @return
	 */
	public static native int    mediaClientInit(String udp_bind_ip, int udp_bind_port, int data_port_start, int data_port_num);

	/**
	 * 
	 */
	public static native void   mediaClientTerm();
	
	/**
	 * 
	 * @param chan_num
	 * @param local_ip
	 * @param snd_port_start
	 * @param msg_listen_port
	 * @param rtsp_listen_port
	 * @param tcp_listen_port
	 * @param snd_std_rtp
	 * @param multiplex
	 * @return
	 */
	public static native int mediaServerInit(int chan_num, String local_ip, int snd_port_start, int msg_listen_port, int rtsp_listen_port, int tcp_listen_port, boolean snd_std_rtp, boolean multiplex);
	
	/**
	 * @param chan_num  通道数     多路转发 目前兴图快投只有一路   
	 * @param local_ip  本地IP 
	 * @param snd_port_start  媒体数据发送端口
	 * @param msg_listen_port
	 * @param rtsp_listen_port  RTSP  监听端口
	 * @param tcp_listen_port
	 * @param snd_std_rtp  是否转发标准流  RTSP协商标准流  true  否则就是私有流  false
	 * @param multiplex  
	 * @param multSend
	 * @return
	 */
	public static native int mediaServerInitEx(int chan_num, String local_ip, int snd_port_start, int msg_listen_port, int rtsp_listen_port, int tcp_listen_port, boolean snd_std_rtp, boolean multiplex , boolean multSend);
	/**
	 * 
	 */
	public static native void mediaServerTerm();
	
	/**
	 * 
	 * @param tracknum
	 * @param trackids
	 * @param src_and_chan
	 * @return
	 */
	public static native int mediaServerCreateSrc(int tracknum, int []trackids, int [] src_and_chan);
	
	/**
	 * 
	 * @param srcno
	 * @return
	 */
	public static native int mediaServerDestroySrc(int srcno);
	
	/**
	 * 
	 * @param srcno
	 * @param sdp
	 * @param sdp_len
	 * @param data_type
	 * @return
	 */
	public static native int mediaServerSetKey(int srcno, byte []sdp, long sdp_len, long data_type);
	
	/** 
	 * 
	 * @param srcno
	 * @param trackid
	 * @param data
	 * @param len
	 * @param frame_type
	 * @param device_type
	 * @return
	 */
	public static native int mediaServerSendData(int srcno, int trackid, byte[] data, long len, int frame_type, long device_type);
	
	/**
	 * 
	 * @param srcno
	 * @param trackid
	 * @param data
	 * @param len
	 * @param frame_type
	 * @param device_type
	 * @return
	 */
	public static native int mediaServerSendStdData(int srcno, int trackid, byte[] data, long len, int frame_type, long device_type);
	
	/**
	 * 
	 * @param chan
	 * @param ip
	 * @param port
	 * @param demux
	 * @param demuxid
	 * @return
	 */
	public static native int mediaServerAddSend(int chan, String ip, int port, boolean demux, int demuxid);
	
	/**
	 * 
	 * @param chan
	 * @param ip
	 * @param port
	 * @param demux
	 * @param demuxid
	 * @return
	 */
	public static native int mediaServerDelSend(int chan, String ip, int port, boolean demux, int demuxid);
	
	/**
	 * 
	 * @param srcno
	 * @param trackid
	 * @param ip
	 * @param port
	 * @return
	 */
	public static native int mediaServerAddStdSend(int srcno, int trackid, String ip, int port);
	
	/**
	 * 
	 * @param srcno
	 * @param trackid
	 * @param ip
	 * @param port
	 * @return
	 */
	public static native int mediaServerDelStdSend(int srcno, int trackid, String ip, int port);
	
	/**
	 * 
	 * @param device_type
	 * @param ip
	 * @param port
	 * @param channel
	 * @param chan
	 * @return
	 */
	public static native long rtStartPlay(int device_type, String ip, int port, long channel, int [] chan);
	
	/** 组播点播的接口
	 * @param device_type  默认9
	 * @param ip		点播的IP
	 * @param port      点播的端口
	 * @param channel   0
	 * @param mulcastip 组播地址
	 * @param mulcastport 组播端口
	 * @param chan    数组
	 * @return
	 */
	public static native long rtStartMulticastPlay(int device_type, String ip, int port, long channel, String mulcastip, int mulcastport , int [] chan);
	
	/**
	 * 
	 * @param handle
	 * @return
	 */
	public static native int rtStopPlay(long handle);


	public static native int rtRestartPlay(int device_type, String ip, int port, long channel, long old_chan , long [] new_chan);
	
	/**
	 * 
	 * @param sdp
	 * @param sdp_len
	 * @param data_type
	 * @param chan
	 * @return
	 */
	public static native long rtCustomPlay(byte [] sdp, int sdp_len, int data_type, int [] chan);
	
	/**
	 * 
	 * @param handle
	 * @param data
	 * @param len
	 * @param frame_type
	 * @param data_type
	 * @param timestamp
	 * @return
	 */
	public static native int rtSendData(long handle, byte [] data, int len, int frame_type, int data_type, int timestamp);
	
	
	public static native boolean rtSendVideoData(long lh, byte [] data, int len, int data_type, long timestamp, String sps, String pps);
	
	/**
	 * 
	 * @param handle
	 * @param prof
	 * @return
	 */
	public static native int rtQueryProf(long handle, int [] prof);
	
	/*
	 * prof[0]: 去花屏处理流程造成的丢帧数
	 * prof[1]: 解码能力不够造成的丢帧数
	 * prof[2]: 音视频同步跳过不显示的视频帧
	 */
	public static native int QueryDecodeStatus(long nTaskIndex, int [] prof);
	
	/**
	 * 
	 * @param handle
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @return
	 */
	public static native int mediaLocalPlay(long handle, int x, int y, int w, int h);
	
	public static native int mediaLocalPlayEx(long handle ,long obj);
	
	/**
	 * 
	 * @param nTaskIndex
	 * @return
	 */
	public static native int mediaLocalStop(int nTaskIndex , long handle);
	
	public static native int xtSendDataByByte(int srcno,int trackid,int MediaType,int ChunkCount,int TimeStamp,byte[] buff,long len, int frame_type, int device_type);
	
	public static native int rtGetChan(long ctx);
	
	public static native boolean InitXmlConfig(String configpath);
	
	public static native void PutRouterCfg(String key, String value);
	
	public static native void EnableStream(int nPlayerIndex, int nType , boolean bIsEnable);
	
	public static native void setVolume(int nTaskIndex , int nVolume);
	
	public static native int  xtRegister(String sz_ids, String sz_server_ip, int server_port, int millisec);

	public static native int  xtStopRegister(String sz_server_ip, int server_port, int millisec);

	public static native void setRegisterCallBack(Object obj);
	
	public static native void setRegisterResponseCallBack(Object obj);

	public static native <SipLinkOpt> boolean xtMediaClientSipLink(SipLinkOpt[]  opt, long[] new_handle);
	
	public static native int OpenSipSdp(String sdp , long handle, int x, int y, int w, int h);
	
	public static native int OpenSipSdpEx(String sdp , long handle , long obj);
	
	public static native void setMecType(boolean bMecType);
	
	//编码转发
	//返回的是实际个流个数
	public static native int  getSvrInfo(int srcno ,  Object[] opt);
	public static native int  addSend(int srcno , String track , String ip , int port , boolean demux , int demuxid);
	public static native int  delSend(int srcno , String track , String ip , int port , boolean demux , int demuxid);
	
	
	public static native int  getSDP(long lh , byte[] jsdp);
	
	/**
	 * isSync   是否开启音视频同步
	 * SyncMode 同步模式 0/1/2/3 -- rtcp同步/时间戳之差同步/帧到达时间同步/多媒体播放模式
	 * TimeUs   预播放延时   单位为微妙
	 * false  标准模式   true 多媒体模式   SyncMode=3
	 */
	public static native void setSyncInfo(boolean isSync , int SyncMode, int TimeUs);
	
	public static native int PraseProfile(String profile , int[] pix);


	public static native int setPayload(int srcno, String track, int payload, boolean update);
	
	public static native void setFrameRate(int nFrameRate);
	
	public static native void    setPlayStatusEnable(Object obj);
	
	/*
	 * 该函数获取因为同步的因素造成的限时内点播失败的原因,点播停止前调用
	 * indextask (in)  - 任务号
	 * int return   0  - 成功
	 *             -1  - 未收到视频RTCP
	 *             -2  - 未收到音频RTCP
	 *            -10  - 数据的缓存未达到设定的缓存时间长度
	 *            -100 - 点播已关闭
	 */
	public static native int GetErrorInfo(int indextask);
	
	/**  演示模式开启  媒体模式关闭
	 * @param bkipIFrame 是否打开差错隐藏
	 * @param maxFrames  文本模式  缓存的帧数  建议30
	 */
	public static native void MediaSkipToIFrame(boolean bkipIFrame , int maxFrames);
	
	/**强制I帧
	 * @param indextask
	 * @return
	 */
	public static native int RequestIframe(int indextask);
	
	public static native int RequestIframeX(long handle);
	
	/** 标准组播数据接口
	 * @param ip
	 * @param media_type
	 * @param demux
	 * @param multicastip
	 * @param multicastport
	 * @param obj
	 * @return
	 */
	public static native int  openStdMulticast(String ip, int media_type, boolean demux , String multicastip, int multicastport , long obj);
	
}
