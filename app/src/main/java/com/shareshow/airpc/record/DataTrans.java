package com.shareshow.airpc.record;

import android.os.SystemClock;
import android.util.Log;

import com.xtmedia.xtview.GlRenderNative;
import com.shareshow.airpc.util.DebugLog;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class DataTrans{
	
	private static final String TAG = "DataTrans";
	
    //private static final String TAG = SendMediaData.class.getSimpleName();
    private static int  srcNo   = -2;
    public  static long handle  = -1;
    public  static long Audiohandle  = -1;
    private static int iChunkCount = 0;
    private static int iChunkCount1 = 0;
    private static int ts;
    private static final int MAX = 24 + 3 * 1024 * 1024;
    private static byte[] sendData   = new byte[MAX];
    private static byte[] sendBuffer = new byte[MAX];
    
    /* ������Ƶͨ�� */
    public  static final boolean AUDIO_CHANNEL_CREATE = true;
    public  static final boolean AUDIO_CHANNEL_DESTORY = false;
    public  static boolean audioChannelState = AUDIO_CHANNEL_DESTORY;
    
    /* ������Ƶͨ�� */
    public  static final boolean VIDEO_CHANNEL_CREATE = true;
    public  static final boolean VIDEO_CHANNEL_DESTORY = false;
    public  static boolean videoChannelState = VIDEO_CHANNEL_CREATE;
    
    private static byte[] head = new byte[24];
    private static int[] tmp = { 0, 24, 0, 0, 0, 0 };
    
    public static boolean is_Iframe(byte nal_type)
    {
        return ((5 == nal_type) || (6 == nal_type) || (7 == nal_type) || (8 == nal_type));
    }
    
    public static byte[] buildHead(int length, int frameType) {
        for (int i = 0; i < head.length; i++) {
            head[i] = 0;
        }
        tmp[0] = 0;
        tmp[1] = 24;
        tmp[2] = frameType;
        tmp[3] = iChunkCount++;
        tmp[4] = length;
        tmp[5] = ts; 
        ByteBuffer.wrap(head).order(ByteOrder.LITTLE_ENDIAN).asIntBuffer().put(tmp);
        head[0] = 'C';
        head[1] = 'H';
        head[2] = 'U';
        head[3] = 'H';
        return head;
    }
    
	public void sendVideoData(byte[] sps, byte[] pps, byte[] h264buff, int length) {
		if (-1 != handle) 
		{
		
			size2=size2+h264buff.length;
			Log.d(TAG,"前：次数"+(++length2)+"===大小"+h264buff.length+"====总大小"+size2);
			ts = (int) SystemClock.elapsedRealtime() * 90;
			int headsize = 0;

			if ((h264buff[0] == 0) && (h264buff[1] == 0) && (h264buff[2] == 1))
			{
				headsize = 3;
			}

			else if ((h264buff[0] == 0) && (h264buff[1] == 0) && (h264buff[2] == 0) && (h264buff[3] == 1)) 
			{
				headsize = 4;

			}
			
			int frame = 0, bufferLength = 0, XTHeaderLen = 24, sLen = 0, pLen = 0;
			try {
				if (is_Iframe((byte) (h264buff[headsize] & 0x1F)))
				{
					if (sps != null && pps != null){
						sLen = sps.length+4;
						pLen = pps.length+4;
						bufferLength = XTHeaderLen + sLen + pLen + length;
						frame = 0;
					}
				} 
				else
				{
					bufferLength = XTHeaderLen + length;
					frame = 0x00010000;
				}


				if (sLen > 0) 
				{
					byte []  naulHead = {0x00,0x00,0x00,0x01};
					System.arraycopy(naulHead, 0, sendBuffer, XTHeaderLen, 4);
					System.arraycopy(sps, 0, sendBuffer, XTHeaderLen+4, sLen-4);
				}

				if (pLen > 0)
				{
                    byte []  naulHead = {0x00,0x00,0x00,0x01};
                    System.arraycopy(naulHead, 0, sendBuffer, XTHeaderLen+sLen, 4);
					System.arraycopy(pps, 0, sendBuffer, XTHeaderLen + sLen+4, pLen-4);
				}

				System.arraycopy(h264buff, 0, sendBuffer, XTHeaderLen + sLen + pLen, length);

				//Log.d("sendMeidaData", "send len=" + bufferLength + " frametype=" + frame);
				byte[] head = buildHead(bufferLength - 24, frame);

				System.arraycopy(head, 0, sendBuffer, 0, 24);
		
				//Log.e(TAG, "rtSendData length=" + bufferLength);
                size= size + bufferLength;
				GlRenderNative.rtSendData(handle, sendBuffer, bufferLength, frame, 172, ts);

			} 
			catch (Exception e)
			{

			}
		}
	}
	int length2=0;
	long size=0;
	long size2=0;
    public byte[] buildHead1(int length, int frameType) {
        for (int i = 0; i < head.length; i++) {
            head[i] = 0;
        }
        tmp[0] = 0;
        tmp[1] = 24;
        tmp[2] = frameType;
        tmp[3] = iChunkCount1++;
        tmp[4] = length;
        tmp[5] = ts; 
        ByteBuffer.wrap(head).order(ByteOrder.LITTLE_ENDIAN).asIntBuffer().put(tmp);
        head[0] = 'C';
        head[1] = 'H';
        head[2] = 'U';
        head[3] = 'H';
        return head;
    }
	
    public void sendAudioData(byte[] h264buff, int length)
    {
//        if (-1 != handle && -2 != handle && -3 != handle && 0 != handle)
//        {
//            ts = (int) SystemClock.elapsedRealtime() * 8;
//
//            System.arraycopy(h264buff, 0, sendBuffer,0, length);
//
//            byte[] headCopy = buildHead1(length, 1);
//
//            System.arraycopy(headCopy, 0, sendData, 0, 24);
//            System.arraycopy(sendBuffer, 0, sendData, 24, length);
//            GlRenderNative. rtSendData(handle, sendData, length + 24, 0x00120000 , 172, ts);
//            Log.v("sendAudioData", "send data length = " + length);
//            if (Audiohandle == -1 || Audiohandle == -2 || Audiohandle == 0 || -3 == Audiohandle) 
//            {
//                return;
//            }
//            else
//            {
//            	GlRenderNative. rtSendData(Audiohandle, sendData, length + 24, 0x00120000 , 172, ts);
//            }
//
//        }
     if (-1 != handle && -2 != handle && -3 != handle && 0 != handle) {
        	
            ts = (int) SystemClock.elapsedRealtime() * 44;
            /** 
             *  构建标准头
             */
            byte[] headCopy = buildHead1(length, 1);
          
            System.arraycopy(headCopy, 0, sendData, 0, 24);
            System.arraycopy(h264buff, 0, sendData, 24, length);
            
            GlRenderNative. rtSendData(handle, sendData, length + 24, 0x00120000 , 172, ts);
            
            Log.v("sendAudioData", "send data length = " + length);
        }
        Log.v("sendAudioData", "send data length = " + length);
    

    }
	
	
	public void sendVideoDataEx(byte[] h264buff, int length) {
		
		if (-1 != handle) 
		{
			ts = (int) SystemClock.elapsedRealtime() * 90;
			int headsize = 0;

			if ((h264buff[0] == 0) && (h264buff[1] == 0) && (h264buff[2] == 1))
			{
				headsize = 3;
			} 
			else if ((h264buff[0] == 0) && (h264buff[1] == 0) && (h264buff[2] == 0) && (h264buff[3] == 1)) 
			{
				headsize = 4;
			}
			
			int frame = 0, bufferLength = 0, XTHeaderLen = 24, sLen = 0, pLen = 0;
			try {
				if (is_Iframe((byte) (h264buff[headsize] & 0x1F)))
				{
					    bufferLength = XTHeaderLen + length;
						frame = 0;
				} 
				else
				{
					bufferLength = XTHeaderLen + length;
					frame = 0x00010000;
				}
			
				System.arraycopy(h264buff, 0, sendBuffer, XTHeaderLen, length);

				Log.d("sendMeidaData", "send len=" + bufferLength + " frametype=" + frame);
				byte[] head = buildHead(bufferLength - 24, frame);

				System.arraycopy(head, 0, sendBuffer, 0, 24);
		
				NatvieCapture.rtSendData(handle, sendBuffer, bufferLength, frame, 172, ts);

			} 
			catch (Exception e)
			{
			}
		}
	}
	
    //public static boolean CreateMeidaRouter(String sps, String pps) {
	public boolean CreateMeidaRouter(String sps, String pps) {

        if (handle != 0 && handle != -1 && handle != -2 && handle != -3){
            return false;
        }
        int streamindex = 1;
        String strSDP = "v=0\n";
        strSDP += "o=- 102 1 IN IP4 0.0.0.0\n";
        strSDP += "s=RTSP/RTP stream from IPNC\n";
        strSDP += "i=2?videoCodecType=H.264\n";
        strSDP += "t=0 0\n";
        strSDP += "a=tool:LIVE555 Streaming Media v2010.07.29\n";
        strSDP += "a=type:broadcast\n";
        strSDP += "a=control:*\n";
        strSDP += "a=range:npt=0-\n";
        strSDP += "a=x-qt-text-nam:RTSP/RTP stream from IPNC\n";
        strSDP += "a=x-qt-text-inf:2?videoCodecType=H.264\n";

        if(videoChannelState == true)
        {
        	strSDP += "m=video 0 RTP/AVP 96\n";
        	strSDP += "c=IN IP4 0.0.0.0\n";
        	strSDP += "b=AS:12000\n";
        	strSDP += "a=rtpmap:96 H264/90000\n";
        	strSDP += "a=fmtp:96 packetization-mode=1;profile-level-id=64001F;sprop-parameter-sets=" + sps +"," + pps +"\n";
        	strSDP += "a=control:track"; 
        	strSDP += streamindex++;
        	strSDP += "\n";
        }



//        byte audioConfig[] = new byte[2];
//        byte audioObjectType = 0x02;
//        byte samplingFrequencyIndex = 0x04;
//        byte channelConfiguration = 0x01;
//        audioConfig[0] = (byte) ((audioObjectType << 3) | (samplingFrequencyIndex >> 1));
//        audioConfig[1] = (byte) ((samplingFrequencyIndex << 7) | (channelConfiguration << 3));
//        System.out.printf("%02x%02x", audioConfig[0], audioConfig[1]);
//        String config = String.format("%02x%02x", audioConfig[0], audioConfig[1]);
//        if(audioChannelState == true)
//        {
//        	strSDP += "m=audio 0 RTP/AVP 96\n";
//        	strSDP += "c=IN IP4 0.0.0.0\n";
//        	strSDP += "a=rtpmap:96 MPEG4-GENERIC/44100/1\n";
//        	strSDP += "a=fmtp:96 profile-level-id=15;Profile=1;config="+config+";mode=AAC-hbr;sizelength=13;indexlength=3;indexdeltalength=3;\n";
//        	strSDP += "a=control:track";
//        	strSDP += streamindex;
//        	strSDP += "\n";
//        }


 
        int[] src = new int[] { -1 };

        handle = GlRenderNative.rtCustomPlay(strSDP.getBytes(),strSDP.getBytes().length, 172, src);

        //RCAuidoEncodeNative.SetSendParam(handle);
        
        int srcNo = src[0];
        
        return true;
    }
	
    public void DestroyMeidaRouter()
    {

        DebugLog.showLog("DataTran","handle:"+handle);

        if (handle == -1 || handle == -2 || handle == 0 || -3 == handle) 
        {
            return;
        }

        GlRenderNative.rtStopPlay(handle);

        handle = -1;
    }
    
//  public abstract int rtStopPlay(long handle);
//
//	public abstract long rtCustomPlay(byte [] sdp, int sdp_len, int data_type, int [] chan);
//	
//	public abstract int rtSendData(long handle, byte [] data, int len, int frame_type, int data_type, int timestamp);
//	
//	public abstract int mediaServerInit(int chan_num, String local_ip, int snd_port_start, int msg_listen_port, int rtsp_listen_port, int tcp_listen_port, boolean snd_std_rtp, boolean multiplex);



}
