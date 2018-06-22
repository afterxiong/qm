package com.shareshow.aide.event;

import com.shareshow.aide.retrofit.entity.TeamMorningData;

/**
 * Created by FENGYANG on 2018/3/6.
 */

public class UpdateMorningDataEvent {

    private TeamMorningData data;

    public UpdateMorningDataEvent(TeamMorningData data){
        this.data=data;
    }

    public TeamMorningData getEvent(){
        return data;
    }
}
