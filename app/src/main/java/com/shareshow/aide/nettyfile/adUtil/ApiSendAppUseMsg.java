package com.shareshow.aide.nettyfile.adUtil;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by wulong on 2017/9/25 0025.
 */

public interface ApiSendAppUseMsg {

    @FormUrlEncoded
    @POST("devAppUse.form")
    Observable<String> sendAppUseMsg(@FieldMap(encoded = true) Map<String, String> map);
}
