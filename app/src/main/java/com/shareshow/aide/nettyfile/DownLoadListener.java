package com.shareshow.aide.nettyfile;

/**
 * Created by TEST on 2017/12/21.
 */

public interface DownLoadListener {

      void OnSuccess(int index, int isUpdate);

      void OnFail(int index);

}
