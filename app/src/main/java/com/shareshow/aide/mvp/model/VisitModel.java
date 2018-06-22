package com.shareshow.aide.mvp.model;

import com.shareshow.aide.impl.OnResponseResultListener;
import com.shareshow.aide.mvp.presenter.VisitPresenter;
import com.shareshow.aide.retrofit.Api;
import com.shareshow.aide.retrofit.RetrofitProvider;
import com.shareshow.aide.retrofit.entity.VisitData;
import com.shareshow.aide.retrofit.entity.VisitRecord;
import com.shareshow.aide.util.CacheUserInfo;
import com.shareshow.aide.util.VisitCacheData;
import com.shareshow.db.GreenDaoManager;
import com.shareshow.db.VisitDataDao;

import java.text.SimpleDateFormat;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by xiongchengguang on 2017/12/11.
 */

public class VisitModel implements BaseModel {

    private VisitPresenter presenter;
    private Api api;

    public VisitModel(VisitPresenter presenter) {
        this.presenter = presenter;
        api= RetrofitProvider.getApi();
    }

    public void getSyncVisitRecord(OnResponseResultListener listener) {
        String userId = CacheUserInfo.get().getUserId();
        String startDate = VisitCacheData.get().getLastDate();
        api.getSyncVisitRecord(userId, startDate, getToDay())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<VisitRecord>() {
                    @Override
                    public void accept(VisitRecord record) throws Exception {
                        if (record.getReturnCode() == 1) {
                            listener.result(record);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        listener.error();
                        throwable.printStackTrace();
                    }
                });
    }


    public String getToDay() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(System.currentTimeMillis());
    }

//    public void commitVisitData(VisitData visitTrack) {
//        String[] photos = visitTrack.getPhoto().split("<#>");
//        MultipartBody.Builder builder = new MultipartBody.Builder();
//        List<MultipartBody.Part> parts = null;
//        boolean existPhoto = false;
//        for (String photo : photos) {
//            File file = new File(photo);
//            if (file.exists()) {
//                existPhoto=true;
//                RequestBody requestBody = RequestBody.create(MediaType.parse("application/otcet-stream"), file);
//                builder.addFormDataPart("file", file.getName(), requestBody);
//            }
//        }
//
//        if(existPhoto){
//            builder.setType(MultipartBody.FORM);
//            parts = builder.build().parts();
//        }
//
//        String id = CacheUserInfo.get().getUserId();
//        String userPhone = CacheUserInfo.get().getUserPhone();
//        String visitContent = visitTrack.getContent();
//        MediaType mediaType = MediaType.parse("multipart/form-data");
//        RequestBody userId = RequestBody.create(mediaType, id);
//        RequestBody phone = RequestBody.create(mediaType, userPhone);
//        RequestBody timeStart = RequestBody.create(mediaType, String.valueOf(visitTrack.getTimeStart()));
//        RequestBody timeEnd = RequestBody.create(mediaType, String.valueOf(visitTrack.getTimeEnd()));
//        RequestBody guestname = RequestBody.create(mediaType, visitTrack.getGuestname());
//        RequestBody addresss = RequestBody.create(mediaType, visitTrack.getAddresss());
//        RequestBody gps = RequestBody.create(mediaType, visitTrack.getGps());
//        RequestBody content = RequestBody.create(mediaType, visitContent);
//
//        Map<String, RequestBody> map = new HashMap<>();
//        map.put("userId", userId);
//        map.put("phone", phone);
//        map.put("timeStart", timeStart);
//        map.put("timeEnd", timeEnd);
//        map.put("guestname", guestname);
//        map.put("addresss", addresss);
//        map.put("gps", gps);
//        map.put("content", content);
//
//        api.submitVisitRecord(map, parts)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.newThread())
//                .subscribe(new Consumer<UserInfo>() {
//                    @Override
//                    public void accept(UserInfo userInfo) throws Exception {
//                        presenter.onVisitListener(userInfo);
//                    }
//                }, new Consumer<Throwable>() {
//                    @Override
//                    public void accept(Throwable throwable) throws Exception {
//                        throwable.printStackTrace();
//                        presenter.onVisitErrorListener();
//                    }
//                });
//    }


}
