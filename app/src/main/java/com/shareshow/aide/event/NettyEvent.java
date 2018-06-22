package com.shareshow.aide.event;

/**
 * Created by FENGYANG on 2018/4/16.
 */

public class NettyEvent {

    public final static  String SEND_TO_BOX_FILE_COMPLETE="box_send_file_complete";

    public final static  String SEND_TO_BOX_AD_COMPLETE="box_send_ad_complete";

    public final static  String SEND_TO_BOX_COMPLETE="box_send_complete";

    public final static  String SEND_DATA_TO_SERVER = "box_data_to_server";

    private String cmd;

    public NettyEvent(String cmd){
        this.cmd =cmd;
    }


    public String getCmd(){
        return cmd;
    }


}
