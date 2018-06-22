package com.shareshow.aide.nettyfile.adUtil;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by wulong on 2017/9/25 0025.
 */

public interface ApiSendDevicesMsg {

    @FormUrlEncoded
    @POST("devUseInfo.form")
    Observable<String> sendDevicesMsg(@FieldMap(encoded = true) Map<String, String> map);
}
