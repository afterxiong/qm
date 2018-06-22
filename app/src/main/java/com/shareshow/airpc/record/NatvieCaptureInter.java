package com.shareshow.airpc.record;

public interface NatvieCaptureInter {
	public long rtCustomPlay(byte[] sdp, int sdp_len, int data_type, int[] chan);
	
	public int rtSendData(long handle, byte[] data, int len, int frame_type, int data_type, int timestamp);
	
	public int mediaServerInit(int chan_num, String local_ip, int snd_port_start, int msg_listen_port, int rtsp_listen_port, int tcp_listen_port, boolean snd_std_rtp, boolean multiplex);
}
