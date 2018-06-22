package com.shareshow.aide.mvp.presenter;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMap;
import com.shareshow.App;
import com.shareshow.aide.impl.OnResponseResultListener;
import com.shareshow.aide.mvp.model.VisitModel;
import com.shareshow.aide.mvp.view.VisitView;
import com.shareshow.aide.retrofit.entity.VisitData;
import com.shareshow.aide.retrofit.entity.VisitRecord;
import com.shareshow.aide.util.Fixed;
import com.shareshow.aide.util.VisitCacheData;
import com.shareshow.airpc.util.AMapLocationManager;
import com.shareshow.db.GreenDaoManager;
import com.shareshow.db.VisitDataDao;
import com.socks.library.KLog;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by xiongchengguang on 2017/12/11.
 */

public class VisitPresenter extends BasePresenter<VisitView> {
    private Activity activity;

    //标识，用于判断是否只显示一次定位信息和用户重新定位
    private boolean isFirstLoc = true;
    private AMap aMap;
    private VisitModel model;


    public VisitPresenter(Activity activity) {
        this.activity = activity;
        model = new VisitModel(this);
    }

    //   4.3接收客户拜访记录
    //    http://localhost:8080/NetShare/visitRecord.form
    //    携带参数：
    //    手机号: String vrPhone=？
    //    拜访开始时间：long vrTimestart=？（时间戳）
    //    拜访结束时间：long vrTimeend=？（时间戳）
    //    拜访客户姓名：String vrGuestname=？
    //    拜访客户地址：String vrAddresss=？
    //    拜访客户GPS地址：String vrGps=？
//    public void commitVisitData(String visitContent, VisitData visitTrack, List<String> selectPhotos) {
//        model.commitVisitData(visitContent, visitTrack, selectPhotos);
//    }

//    public void commitVisitData(VisitData visitData) {
//        model.commitVisitData(visitData);
//    }

    public void onActivityResultContacts(Intent data) {
        try {
            ContentResolver reContentResolverol = activity.getContentResolver();
            Uri contactData = data.getData();
            Cursor cursor = activity.managedQuery(contactData, null, null, null, null);
            cursor.moveToFirst();
            String username = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            if (isViewAttached()) {
                getView().setSelectorContacts(username);
            }
//            String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
//            Cursor phone = reContentResolverol.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
//                    null,
//                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId,
//                    null,
//                    null);
//            while (phone.moveToNext()) {
//                String usernumber = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(App.getApp(), "读取联系人失败请手动输入!", Toast.LENGTH_SHORT).show();
        }
    }

    public void startLocation(AMap aMap){
        AMapLocationManager.get().startLocation(aMap);
        AMapLocationManager.get().setManagerOnLocationListener(new AMapLocationManager.OnLocationListener() {
            @Override
            public void listener(AMapLocation amapLocation) {
                getView().setLocationText(amapLocation);
            }
        });
    }

    /**
     * 同步拜访记录
     */
    public void getSyncVisitRecord(){
        model.getSyncVisitRecord(new OnResponseResultListener() {
            @Override
            public void result(Object obj) {
                KLog.d("拜访记录同步完毕");
                VisitRecord record = (VisitRecord) obj;
                VisitCacheData.get().saveLastDate(Fixed.getToDay());
                List<VisitData> visitDatas = record.getDatas();
                VisitDataDao dao = GreenDaoManager.getDaoSession().getVisitDataDao();
                for (VisitData data : visitDatas) {
                    if (dao.load(data.getVrId()) == null) {
                        dao.insert(data);
                    }
                }
            }

            @Override
            public void error() {
                KLog.d("拜访记录同步失败");
            }
        });
    }


}
