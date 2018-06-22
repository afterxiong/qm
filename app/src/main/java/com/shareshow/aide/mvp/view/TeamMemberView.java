package com.shareshow.aide.mvp.view;

import com.shareshow.aide.retrofit.entity.Team;

/**
 * Created by xiongchengguang on 2018/2/2.
 */

public interface TeamMemberView extends BaseView {

    /**
     * 获取团队成员列表
     *
     * @param team
     */
    public void onViewTeamMenberList(Team team);


    public void onViewError(int tag);

}
