package com.shareshow.airpc.util;

import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.socks.library.KLog;
import com.shareshow.App;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiongchengguang on 2017/12/21.
 */

public class AMapLocationManager implements LocationSource {
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    private LocationSource.OnLocationChangedListener locationChangedListener = null;//定位监听器
    private boolean isFirstLoc = true;
    private AMap aMap;

    public static AMapLocationManager get() {
        return LocationHelper.helper;
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        this.locationChangedListener = onLocationChangedListener;
    }

    @Override
    public void deactivate() {
        this.locationChangedListener = null;
    }

    private static class LocationHelper {
        private static AMapLocationManager helper = new AMapLocationManager();
    }



    //开始定位
    public void startLocation(AMap aMap){
        this.aMap = aMap;
        MyLocationStyle myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.interval(20000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        aMap.getUiSettings().setMyLocationButtonEnabled(true);//设置默认定位按钮是否显示，非必需设置。
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);//定位一次，且将视角移动到地图中心点。
        myLocationStyle.showMyLocation(true);
        //初始化定位
        mLocationClient = new AMapLocationClient(App.getApp().getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(getDefaultOption());
        //启动定位
        mLocationClient.startLocation();
    }

    private AMapLocationClientOption getDefaultOption() {
        AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(true);
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
        return mLocationOption;
    }


    //声明定位回调监听器
    public AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation amapLocation) {
            if (amapLocation != null) {
                if (amapLocation.getErrorCode() == 0){
                    if(locationListener!=null){
                        locationListener.listener(amapLocation);
                    }
                    searchPoi(amapLocation.getLatitude(), amapLocation.getLongitude(), "", 0, amapLocation.getCityCode(), true);
                    if (isFirstLoc){
                        //设置缩放级别
                        aMap.clear();
                        aMap.moveCamera(CameraUpdateFactory.zoomTo(17));
                        //将地图移动到定位点
                        aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude())));
                        //点击定位按钮 能够将地图的中心移动到定位点
                        locationChangedListener.onLocationChanged(amapLocation);
                        //添加图钉
//                        aMap.addMarker(getMarkerOptions(amapLocation));
                        //获取定位信息
                        StringBuffer buffer = new StringBuffer();
                        buffer.append(amapLocation.getCountry() + "" + amapLocation.getProvince() + "" + amapLocation.getCity() + "" + amapLocation.getProvince() + "" + amapLocation.getDistrict() + "" + amapLocation.getStreet() + "" + amapLocation.getStreetNum());
                        Toast.makeText(App.getApp().getApplicationContext(), buffer.toString(), Toast.LENGTH_LONG).show();
                        isFirstLoc = false;
                    }
                } else {
                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                    KLog.d("AmapError", "location Error, ErrCode:" + amapLocation.getErrorCode() + ", errInfo:" + amapLocation.getErrorInfo());
                }
            }
        }
    };

    private void searchPoi(double latitude, double longitude, String key, int pageNum, String cityCode, boolean nearby) {
        PoiSearch.Query query = new PoiSearch.Query(key, "", cityCode);
        //keyWord表示搜索字符串，
        //第二个参数表示POI搜索类型，二者选填其一，
        //POI搜索类型共分为以下20种：汽车服务|汽车销售|
        //汽车维修|摩托车服务|餐饮服务|购物服务|生活服务|体育休闲服务|医疗保健服务|
        //住宿服务|风景名胜|商务住宅|政府机构及社会团体|科教文化服务|交通设施服务|
        //金融保险服务|公司企业|道路附属设施|地名地址信息|公共设施
        //cityCode表示POI搜索区域，可以是城市编码也可以是城市名称，也可以传空字符串，空字符串代表全国在全国范围内进行搜索
        query.setPageSize(50);// 设置每页最多返回多少条poiitem
        query.setPageNum(pageNum);//设置查询页码
        PoiSearch poiSearch = new PoiSearch(App.getApp(), query);
        poiSearch.setOnPoiSearchListener(poiSearchListener);
        if (nearby){
            poiSearch.setBound(new PoiSearch.SearchBound(new LatLonPoint(latitude, longitude), 1500));//设置周边搜索的中心点以及半径
            poiSearch.searchPOIAsyn();
        }
    }

    PoiSearch.OnPoiSearchListener poiSearchListener = new PoiSearch.OnPoiSearchListener() {
        @Override
        public void onPoiSearched(PoiResult poiResult, int i) {
            if(poiResult==null){
                return;
            }
            ArrayList<PoiItem> result = poiResult.getPois();
            if(searchListener!=null){
                searchListener.onPoiSearched(result);
            }
        }

        @Override
        public void onPoiItemSearched(PoiItem poiItem, int i) {

        }
    };
    private OnLocationListener locationListener;
    private OnPoiSearchListener searchListener;

    public void setManagerOnLocationListener(OnLocationListener locationListener) {
        this.locationListener = locationListener;
    }

    public void setManagerOnPoiSearchListener(OnPoiSearchListener searchListener) {
        this.searchListener = searchListener;
    }

   public interface OnLocationListener {
        public void listener(AMapLocation amapLocation);
    }

    public  interface OnPoiSearchListener {
        public void onPoiSearched(List<PoiItem> list);
    }
}
