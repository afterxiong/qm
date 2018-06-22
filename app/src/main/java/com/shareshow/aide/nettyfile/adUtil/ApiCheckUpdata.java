package com.shareshow.aide.nettyfile.adUtil;

import io.reactivex.Observable;
import retrofit2.http.GET;

/**
 * Created by wulong on 2017/9/25 0025.
 */

public interface ApiCheckUpdata {

    @GET("download/update/index.html")
    Observable<String> checkUpdata();
}
