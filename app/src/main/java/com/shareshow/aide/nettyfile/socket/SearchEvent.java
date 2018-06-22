package com.shareshow.aide.nettyfile.socket;

/**
 * Created by TEST on 2017/12/12.
 */

public class SearchEvent {

    private String result;

    public SearchEvent(String result){

        this.result =result;
    }

    public String getEvent(){
        return result;
    }
}
