package com.shareshow.aide.mvp.view;

import com.shareshow.aide.retrofit.entity.TeamAudioData;
import com.shareshow.aide.retrofit.entity.TeamMorningData;

import java.util.List;

/**
 * Created by FENGYANG on 2018/3/16.
 */

public interface PersonAudioView extends BaseView {

    void setPersonAudioData(List<TeamMorningData> datasBeans);

    void setPersonDataError();

    void setPersonAudioMum(int totleTime, int index);

    void downLoadFileSuccess(TeamMorningData data,int position);

    void downLoadFileFail(TeamMorningData data,int position);
}
