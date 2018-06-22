package com.shareshow.aide.mvp.view;

/**
 * Created by xiongchengguang on 2018/3/30.
 */

public interface SimpleView extends BaseView {
    public void getResult(int tag,Object obj);
    public void error(int tag);
}
