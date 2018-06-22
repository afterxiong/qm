package com.shareshow.aide.mvp.view;

import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.shareshow.aide.retrofit.entity.Team;
import com.shareshow.aide.retrofit.entity.TeamMorningData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiongchengguang on 2017/12/8.
 */

public interface MroiningView extends BaseView, OnLoadMoreListener, OnRefreshLoadMoreListener {

    public void notifyTeamData(ArrayList<TeamMorningData> datas, int flag);

    public void downLoadFileSuccess(TeamMorningData data, int position);

    public void downLoadFileFail(TeamMorningData data, int position);

    public void getMorningDataError(int flag);

    public void notifyUpSuccess(TeamMorningData data);


    public void TeamMember(List<Team.TeamMember> teams);

}
