package com.shareshow.airpc.record;

import android.view.Surface;

public class NatvieCapture {
	
	static {
		try {
			//System.loadLibrary("RecordScreen");
			
		} catch (UnsatisfiedLinkError e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}
	
	public static native void capture(String path, Surface view, int w, int h);
	
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
	
	
	//mediaServerInit(JNIEnv *env, jclass, jint chan_num, jstring local_ip, jint snd_port_start, jint msg_listen_port, jint rtsp_listen_port, jint tcp_listen_port, jboolean snd_std_rtp, jboolean multiplex)

	
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
	
	public static native int rtStopPlay(long handle);
}
