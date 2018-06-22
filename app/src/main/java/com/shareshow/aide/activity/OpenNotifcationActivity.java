package com.shareshow.aide.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.shareshow.App;
import com.shareshow.aide.R;
import com.shareshow.aide.dialog.LoadingProgress;
import com.shareshow.aide.retrofit.entity.AmplyNotify;
import com.shareshow.aide.util.CacheUserInfo;
import com.shareshow.aide.util.Fixed;
import com.shareshow.aide.util.OpenFileUtil;
import com.shareshow.db.GreenDaoManager;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 打开通知详情
 * Created by xiongchengguang on 2018/3/28.
 */

public class OpenNotifcationActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.notif_title)
    public TextView title;
    @BindView(R.id.notif_content)
    public TextView content;
    @BindView(R.id.parent_layout)
    public LinearLayout parentLayout;
    @BindView(R.id.notif_study_time)
    public TextView studyTime;
    @BindView(R.id.notif_study_date)
    public TextView studyDate;
    @BindView(R.id.notif_issue_date)
    public TextView issueDdate;
    @BindView(R.id.notif_dept)
    public TextView dept;
    @BindView(R.id.toolbar)
    public Toolbar toolbar;
    private AmplyNotify an;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_notifcation);
        ButterKnife.bind(this);
        initData();
        initToolbar();
    }

    public void initToolbar() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void initData() {
        an = getIntent().getParcelableExtra("AmplyNotify");
        if (an != null) {
            an.setNosDel("1");
            GreenDaoManager.getDaoSession().getAmplyNotifyDao().update(an);
            setDate();
        }
    }

    public void setDate() {
        title.setText(an.getNosTitle());
        content.setText(an.getNosContent());
        String duration = "无";
        if (an.getNosTimelength() != 0) {
            duration = "学习时长要求:" + an.getNosTimelength() + "分钟";
        } else {
            duration = "学习时长要求: 无";
        }
        studyTime.setText(duration);
        String validityDate = "无";
        if (an.getNosEndtime() != null) {
            validityDate = "学习有效期:" + an.getNosEndtime();
        } else {
            validityDate = "学习有效期: 无";
        }
        studyDate.setText(validityDate);
        issueDdate.setText(getDate(Long.parseLong(an.getNosCreatetime())));
        String name = an.getNosOrgnames().replace("[", "").replace("]", "").replace("\"", "");
        dept.setText(name);
        Gson gson = new GsonBuilder().serializeNulls().create();
        List<String> urlList = gson.fromJson(an.getNosFilepath(), new TypeToken<List<String>>() {
        }.getType());
        List<String> nameList = gson.fromJson(an.getNosFilename(), new TypeToken<List<String>>() {
        }.getType());

        if (urlList != null && nameList != null && nameList.size() > 0) {
            if (nameList.size() == urlList.size()) {
                for (int i = 0; i < nameList.size(); i++) {
                    View view = LayoutInflater.from(this).inflate(R.layout.activity_open_notif_item_appendix, null);
                    TextView tvName = view.findViewById(R.id.name);
                    TextView download = view.findViewById(R.id.download);
                    tvName.setText(nameList.get(i));
                    download.setTag(R.id.tag_url, urlList.get(i));
                    download.setTag(R.id.tag_name, nameList.get(i));
                    download.setOnClickListener(this);
                    parentLayout.addView(view);
                }
            }
        } else {
            View view = LayoutInflater.from(this).inflate(R.layout.activity_open_notif_item_appendix, null);
            TextView tvName = view.findViewById(R.id.name);
            TextView download = view.findViewById(R.id.download);
            download.setVisibility(View.GONE);
            tvName.setText("无");
            parentLayout.addView(view);
        }
    }

    @Override
    public void onClick(View v) {
        String url = (String) v.getTag(R.id.tag_url);
        String name = (String) v.getTag(R.id.tag_name);
        File file = getFile(name);
        LoadingProgress.newInstance().show(getFragmentManager(), "1");
        FileDownloader.getImpl().create(url)
                .setPath(file.getPath())
                .setAutoRetryTimes(5)
                .setListener(new FileDownloadListener() {

                    @Override
                    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {

                    }

                    @Override
                    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {

                    }

                    @Override
                    protected void completed(BaseDownloadTask task) {
                        startActivity(OpenFileUtil.openFile(OpenNotifcationActivity.this, file.getPath()));
                        LoadingProgress.newInstance().dismiss();
                    }

                    @Override
                    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {

                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(App.getApp(), "下载失败，请重试", Toast.LENGTH_LONG).show();
                        LoadingProgress.newInstance().dismiss();
                    }

                    @Override
                    protected void warn(BaseDownloadTask task) {

                    }
                }).start();
    }

    public File getFile(String name) {
        String cachePhone = CacheUserInfo.get().getUserPhone();
        String pathname = Fixed.getRootPath() + File.separator + cachePhone + File.separator + "notification" + File.separator + name;
        File file = new File(pathname);
        return file;
    }

    public String getDate(long time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
        return dateFormat.format(time);
    }
}
