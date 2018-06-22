package com.shareshow.aide.impl;

import com.shareshow.aide.retrofit.entity.TeamMorningData;

/**
 * Created by FENGYANG on 2018/4/3.
 */

public interface OnRecycleviewListener{

     void onItemClick(int position, Object obj);
     //点击可以播放本地音频

     void OnPlay(TeamMorningData data, int flag);
     //点击播放或者暂停的

     void OnSeekBar(int progress);
     //进度条的进度回调
}
