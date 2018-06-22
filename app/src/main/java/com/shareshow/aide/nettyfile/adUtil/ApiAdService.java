package com.shareshow.aide.nettyfile.adUtil;


import com.shareshow.db.Adertisement;

import java.util.HashMap;

import io.reactivex.Observable;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by wulong on 2017/9/25 0025.
 */

public interface ApiAdService {

    @FormUrlEncoded
    @POST("getAdCurrentForDevice.form")
    Observable<Adertisement> getBootAd(@FieldMap(encoded = true) HashMap<String, String> map);
}
