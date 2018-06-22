package com.shareshow.aide.nettyfile.Utils;

import java.io.File;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by TEST on 2017/11/9.
 */

public class OkHttpUtil {


    private OkHttpClient okHttpClient;

    private static OkHttpUtil okHttpUtil;

    private static final String FILE_TYPE ="mp3";

    private OkHttpUtil(){
        okHttpClient = new OkHttpClient();
    }

    public static OkHttpUtil getHttpUtil(){
        if(okHttpUtil==null){
            synchronized (OkHttpUtil.class){
                if(okHttpUtil==null){
                    okHttpUtil=new OkHttpUtil();
                }
            }
        }

        return okHttpUtil;
    }


    /**
     * post请求无JSON数据的
     *
     * @param url  请求服务器路径
     * @param params  请求的参数
     * @param callback  请求回调的结果
     */
    public void doPost(String url, Map<String,String> params, Callback callback){

        FormBody.Builder builder = new FormBody.Builder();
        for (String key : params.keySet()) {
            builder.add(key, params.get(key));
        }

        Request request = new Request.Builder()
                .url(url)
                .post(builder.build())
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(callback);
    }


    /**
     * 上传文件
     * @param url       服务器接口
     * @param params    传的参数
     * @param filePath  文件路径
     * @param fileName  文件名称
     * @param callback
     */
    public void doFile(String url, Map<String,String> params, String filePath, String fileName, Callback callback){

        //判断文件类型
        MediaType MEDIA_TYPE = MediaType.parse(FILE_TYPE);

        //创建文件参数
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(MEDIA_TYPE.type(), fileName,
                        RequestBody.create(MEDIA_TYPE, new File(filePath)));

        for (String key : params.keySet()){
            builder.addFormDataPart(key,params.get(key));
        }

        //发出请求参数
        Request request = new Request.Builder()
                .url(url)
                .post(builder.build())
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(callback);
    }


    public void downLoadFile(String url, Callback callBack){
        Request request = new Request.Builder()
                .url(url)
                .build();
       Call call = okHttpClient.newCall(request);
       call.enqueue(callBack);
    }


    public void doGet(String url, Callback callback){
       Request request = new Request.Builder().url(url).get().build();
       Call call =okHttpClient.newCall(request);
       call.enqueue(callback);

    }

}
