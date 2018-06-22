package com.shareshow.aide.retrofit;

import com.google.gson.GsonBuilder;
import com.shareshow.App;
import com.shareshow.aide.util.SampleNetworkInterface;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by xiongchengguang on 2017/12/19.
 */

public class RetrofitProvider {
    //public static final String ENDPOINT = "http://119.96.242.135:10000/NetShare1/";
    //public static final String ENDPOINT = "http://172.16.21.124:8080/NetShare1/";
    //public static final String ENDPOINT = "http://172.16.21.159:8080/NetShare/";
//    public static final String ENDPOINT = "http://www.shareshow.com.cn/NetShare1/";
    public static final String ENDPOINT = "http://10.42.0.95:8080/NetShare1/";
    private static Retrofit retrofit;
    private static Api api;

    public static Retrofit get() {
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        File cacheHttp = new File(App.getApp().getCacheDir(), "responses");
        int cacheSize = 100 * 1024 * 1024;
        Cache cache = new Cache(cacheHttp, cacheSize);
        builder.addInterceptor(new JobInterceptor(3));
        builder.cache(cache);
        builder.readTimeout(9, TimeUnit.SECONDS);
        builder.connectTimeout(10, TimeUnit.SECONDS);

        return new Retrofit.Builder().baseUrl(ENDPOINT)
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().serializeNulls().create()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    public static class JobInterceptor implements Interceptor {

        private int maxRetry;//最大重试次数
        private int retryNum = 0;//假如设置为3次重试的话，则最大可能请求4次（默认1次+3次重试）

        public JobInterceptor(int maxRetry) {
            this.maxRetry = maxRetry;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            CacheControl.Builder cacheBuilder = new CacheControl.Builder();
            cacheBuilder.maxAge(0, TimeUnit.SECONDS);
            cacheBuilder.maxStale(365, TimeUnit.DAYS);
            CacheControl cacheControl = cacheBuilder.build();
            Request request = chain.request();
            if (!SampleNetworkInterface.isNetworkAvailable()) {
                request = request.newBuilder()
                        .cacheControl(cacheControl)
                        .build();
            }
            Response originalResponse = chain.proceed(request);
            //若请求失败，则重复请求
            while (!originalResponse.isSuccessful() && retryNum < maxRetry) {
                retryNum++;
                originalResponse = chain.proceed(request);
            }
            if (SampleNetworkInterface.isNetworkAvailable()) {
                int maxAge = 0;
                return originalResponse.newBuilder()
                        .removeHeader("Pragma")
                        .addHeader("Connection", "close")
                        .header("Cache-Control", "public ,max-age=" + maxAge)
                        .build();
            } else {
                int maxStale = 60 * 60 * 24 * 28;
                return originalResponse.newBuilder()
                        .removeHeader("Pragma")
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                        .build();
            }
        }
    }

    public static Api getApi() {
        if (retrofit == null || api == null) {
            retrofit = RetrofitProvider.get();
            api = retrofit.create(Api.class);
        }
        return api;
    }

}
