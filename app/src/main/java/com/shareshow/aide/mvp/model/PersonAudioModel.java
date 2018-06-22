package com.shareshow.aide.mvp.model;

import com.shareshow.aide.mvp.presenter.PersonAudioPresenter;
import com.shareshow.aide.retrofit.Api;
import com.shareshow.aide.retrofit.RetrofitProvider;
import com.shareshow.aide.retrofit.entity.TeamAudioData;
import com.shareshow.aide.retrofit.entity.TeamMorningData;
import com.shareshow.aide.util.CacheUserInfo;
import com.shareshow.aide.util.CopyFile;
import com.shareshow.aide.util.Fixed;
import com.shareshow.airpc.util.DebugLog;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

/**
 * Created by FENGYANG on 2018/3/16.
 */

public class PersonAudioModel implements BaseModel {

    private  Api api;
    private  PersonAudioPresenter presenter;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public PersonAudioModel(PersonAudioPresenter presenter) {
        this.presenter = presenter ;
        api= RetrofitProvider.getApi();
    }

    public void getDateFilterMorningRecord(String userId, String startTime, String endTime) {
        api.getDateFilterMorningRecord(userId,startTime,endTime)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<TeamAudioData>(){
                    @Override
                    public void accept(TeamAudioData teamAudioData) throws Exception{
                        if(teamAudioData.getReturnCode()==1){
                            List<TeamAudioData.DatasBean> datasBeans =  teamAudioData.getDatas();
                            DebugLog.showLog(this,"获取录音文件数据："+datasBeans.toString());
                            presenter.personAudioData(datasBeans);
                        }else{
                            presenter.personDataError();
                            DebugLog.showLog(this,"返回数据失败:"+teamAudioData.getReturnCode());
                        }
                    }
                }, new Consumer<Throwable>(){
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        DebugLog.showLog(this,"返回数据失败:"+throwable.getMessage());
                        presenter.personDataError();
                    }
                });
    }

    public void getMonthAudioData(String userId){
        Calendar time = Calendar.getInstance();
        int totleTime =time.get(Calendar.DAY_OF_MONTH);
        String endTime = dateFormat.format(new Date(System.currentTimeMillis()));
        String startTime = getStartTime();
        api.getDateFilterMorningRecord(userId,startTime,endTime)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<TeamAudioData>() {
                    @Override
                    public void accept(TeamAudioData teamAudioData) throws Exception {
                         if(teamAudioData.getReturnCode()==1){
                             List<TeamAudioData.DatasBean> datasBeans = teamAudioData.getDatas();
                             if(datasBeans!=null)
                              presenter.setPersonAudioMum(totleTime,datasBeans.size());
                         }else{
                             presenter.setPersonAudioMum(totleTime,-1);
                         }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        presenter.setPersonAudioMum(totleTime,-1);

                    }
                });
    }

    private String getStartTime(){
        Calendar time = Calendar.getInstance();
        int year = time.get(Calendar.YEAR);
        int month = time.get(Calendar.MONTH);
        time.set(year,month,1);
        return dateFormat.format(time.getTime());
    }


    public void downAudioFile(TeamMorningData datasBean, int position) {
        api.donwnload(datasBean.getUrl())
                .subscribeOn(Schedulers.io())
                .map(new Function<ResponseBody, File>(){
                    @Override
                    public File apply(ResponseBody responseBody) throws Exception{
                        File file =new File(Fixed.getRemouteMoningFile()+File.separator+ datasBean.getDay()+File.separator+CacheUserInfo.get().getUserPhone()+"_"+ CopyFile.getTime(datasBean.getTime())+".aac");
                        if(!file.exists()){
                            file.getParentFile().mkdirs();
                            file.createNewFile();
                            BufferedSource source = responseBody.source();
                            BufferedSink bufferedSink = Okio.buffer(Okio.sink(file));
                            Buffer buffer = new Buffer();
                            long total = 0;
                            while (!source.exhausted()){
                                long count = source.read(buffer, 1024);
                                total += count;
                                bufferedSink.write(buffer, count);
                            }
                            bufferedSink.flush();
                            bufferedSink.close();
                            source.close();
                            return file;
                        }else{
                            return file;
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<File>(){
                    @Override
                    public void accept(File file) throws Exception {
                        datasBean.setPath(file.getPath());
                        presenter.downloadComplite(true,datasBean,position);
                    }
                }, new Consumer<Throwable>(){
                    @Override
                    public void accept(Throwable throwable) throws Exception{
                        throwable.printStackTrace();
                        presenter.downloadComplite(false,datasBean,position);
                    }
                });
    }
}
