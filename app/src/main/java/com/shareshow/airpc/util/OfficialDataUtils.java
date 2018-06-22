package com.shareshow.airpc.util;

import android.content.Context;

import com.shareshow.aide.nettyfile.adUtil.ApiAdService;
import com.shareshow.aide.retrofit.RetrofitProvider;
import com.shareshow.aide.util.CacheUserInfo;
import com.shareshow.airpc.Collocation;
import com.shareshow.db.Adertisement;
import com.socks.library.KLog;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * @author wulong
 *         网络获取Ad数据，加载布局
 */
public class OfficialDataUtils {

    private Context context;
    private FinalHttp fl;
    private String url;
    private final ApiAdService retrofit;
    private static OfficialDataUtils adNewDataUtils;
    public static final String BASE_URL = RetrofitProvider.ENDPOINT;

    private OfficialDataUtils(Context context){
        super();
        this.context = context;
         url = BASE_URL;
        retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(ApiAdService.class);
        fl = new FinalHttp();
    }

    public static OfficialDataUtils getSingle(Context context) {
        if (adNewDataUtils == null) {
            synchronized (OfficialDataUtils.class) {
                adNewDataUtils = new OfficialDataUtils(context);
            }
        }
        return adNewDataUtils;
    }

    public void getData(){
        KLog.d("official下载了");
        File official_fileDir = new File(FileContent.JSONFILE_AD_OFFICIAL_DIR);
        if (!official_fileDir.exists()) {
            official_fileDir.mkdirs();
        }
        HashMap<String, String> map_official = new HashMap<>();
        map_official.put("phoneNumber", CacheUserInfo.get().getUserPhone());
        map_official.put("adType", "5");
        retrofit.getBootAd(map_official)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new OfficialObserver());
    }

    private class OfficialObserver implements Observer<Adertisement> {

        Disposable disposable;

        @Override
        public void onSubscribe(@NonNull Disposable d) {
            disposable = d;
        }

        @Override
        public void onNext(@NonNull Adertisement adDataNewBean){
            if (adDataNewBean == null) {
                return;
            }
            List<Adertisement.ListBean> adList = adDataNewBean.getList();
            for (Adertisement.ListBean ad : adList){
                final String adcUpdatetime = ad.getAdcUpdatetime();
                String localUpdataTime = Collocation.getCollocation().getUpDataTime("5_1");
                if (!localUpdataTime.equals(adcUpdatetime)) {//更新下载
                    // WARNING: 2017/9/28 0028  url地址错误,\应该改为/
                    String file_name = FileContent.JSONFILE_AD_OFFICIAL_DIR + File.separator + "official.png";
                    String image_url = ad.getAdcPicurl().replaceAll("\\\\", "/");
                    final String official_url = ad.getAdcHoturl();
                    final String name = ad.getAdcVideourl();
                    fl.download(image_url, file_name+".temp", new AjaxCallBack<File>() {
                        @Override
                        public void onSuccess(File file){
                            super.onSuccess(file);
                            String path_file = file.getPath();
                            String fileName_result = path_file.substring(0, path_file.indexOf(".temp"));
                            file.renameTo(new File(fileName_result));
                            KLog.d("OfficialSuccess==="+file.getName());
                            Collocation.getCollocation().setUpDataTime("5_1", adcUpdatetime);
                            AdSettingUtils.getSingle().updataOfficialElement("name", name);
                            AdSettingUtils.getSingle().updataOfficialElement("url", official_url);
                            if (updataOfficialLisenter != null){
                                updataOfficialLisenter.updataOfficial();
                            }
                        }

                        @Override
                        public void onFailure(Throwable t, int errorNo, String strMsg) {
                            super.onFailure(t, errorNo, strMsg);
                            CleanCache.deleteFolderFileFilter(FileContent.JSONFILE_AD_OFFICIAL_DIR,".temp");
                            Collocation.getCollocation().setUpDataTime("5_1", "");
                        }
                    });
                }
            }
        }

        @Override
        public void onError(@NonNull Throwable e){
            disposable.dispose();
            KLog.d("官网下载失败");
        }

        @Override
        public void onComplete() {
            disposable.dispose();
        }
    }

    public interface UpdataOfficialLisenter{
        void updataOfficial();
    }
    public static UpdataOfficialLisenter updataOfficialLisenter;
    public static void setUpdataOfficialLisenter(UpdataOfficialLisenter lis){
        updataOfficialLisenter = lis;
    }
}
