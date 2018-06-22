package com.shareshow.aide.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.shareshow.App;
import com.shareshow.aide.R;
import com.shareshow.aide.dialog.LoadingProgress;
import com.shareshow.aide.retrofit.entity.VisitData;
import com.shareshow.aide.util.Fixed;
import com.shareshow.aide.util.OpenFileUtil;
import com.socks.library.KLog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.iwf.photopicker.PhotoPreview;

/**
 * Created by xiongchengguang on 2018/2/2.
 */

public class ReadVisitRecordActivity extends AppCompatActivity {
    @BindView(R.id.visit_name)
    public TextView visit_name;
    @BindView(R.id.visit_address)
    public TextView visit_address;
    @BindView(R.id.visit_time)
    public TextView visit_time;
    @BindView(R.id.visit_content)
    public TextView visit_content;
    @BindView(R.id.recycler)
    public RecyclerView picRecycler;
    @BindView(R.id.audio_recycler)
    public RecyclerView audioRecycler;
    @BindView(R.id.toolbar)
    public Toolbar toolbar;
    private VisitData visitData;
    private ArrayList<String> picList = new ArrayList<>();
    private List<String> audioList = new ArrayList<>();
    private ReadPhotoAdapter picAdapter;
    private ReadAudioAdapter audioAdapter;
    private LinearLayoutManager manager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_visit_track);
        ButterKnife.bind(this);
        initToolbar();
        visitData = getIntent().getParcelableExtra("VisitData");
        setVisitData();
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


    private void setVisitData() {
        if (visitData == null) {
            return;
        }
        String name = visitData.getVrGuestname();
        String address = visitData.getVrAddresss();
        String time = visitData.getVrTimestart();
        visit_name.setText(String.format(getResources().getString(R.string.commit_visit_name), name));
        visit_address.setText(String.format(getResources().getString(R.string.commit_visit_address), address));
        visit_time.setText(String.format(getResources().getString(R.string.commit_visit_date), time));
        visit_content.setText(visitData.getVrContent());
        if (visitData.getVrPicurls() != null) {
            for (String urlPath : visitData.getVrPicurls()) {
                if(urlPath.contains("visit_audio_cache.aac")){
                    KLog.d(urlPath);
                    audioList.add(urlPath);
                }else{
                    picList.add(urlPath);
                }
            }
        }
        picAdapter = new ReadPhotoAdapter();
        manager = new LinearLayoutManager(ReadVisitRecordActivity.this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        picRecycler.setLayoutManager(manager);
        picRecycler.setAdapter(picAdapter);

        if (audioList.size() == 0) {
            audioRecycler.setVisibility(View.GONE);
        } else {
            audioRecycler.setVisibility(View.VISIBLE);
        }
        audioAdapter = new ReadAudioAdapter();
        manager = new LinearLayoutManager(ReadVisitRecordActivity.this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        audioRecycler.setLayoutManager(manager);
        audioRecycler.setAdapter(audioAdapter);
    }

    class ReadAudioAdapter extends RecyclerView.Adapter<ReadAudioAdapter.ReadAudioHolder> implements View.OnClickListener {

        @Override
        public ReadAudioHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itme_visit_audio, parent, false);
            ReadAudioHolder holder = new ReadAudioHolder(view);
            holder.itemView.setOnClickListener(this);
            return holder;
        }

        @Override
        public void onBindViewHolder(ReadAudioHolder holder, int position) {
            holder.itemView.setTag(position);
            String url = audioList.get(position);

        }

        @Override
        public int getItemCount() {
            return audioList.size();
        }

        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();
            download(audioList.get(position));
        }

        class ReadAudioHolder extends RecyclerView.ViewHolder {

            private ImageView image;

            public ReadAudioHolder(View itemView) {
                super(itemView);
                image = itemView.findViewById(R.id.image);
            }
        }
    }

    private void download(String url) {
        File file = new File(Fixed.getVisitAudioCache(), url);
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
                        startMediaPlayer(task.getPath());
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

    //播放音频文件
    public void startMediaPlayer(String path) {
        File param = new File(path);
        if (!param.exists()) {
            Toast.makeText(this, "文件不存在", Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(this, "com.shareshow.aide.fileProvider", param);
        } else {
            uri = Uri.fromFile(param);
        }
        KLog.d(uri);
        intent.setDataAndType(uri, "audio/*");
        startActivity(intent);

    }


    class ReadPhotoAdapter extends RecyclerView.Adapter<ReadPhotoAdapter.ReadPhotoHolder> implements View.OnClickListener {

        @Override
        public ReadPhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_visit_photo_selector, parent, false);
            ReadPhotoHolder holder = new ReadPhotoHolder(view);
            holder.itemView.setOnClickListener(this);
            return holder;
        }

        @Override
        public void onBindViewHolder(ReadPhotoHolder holder, int position) {
            holder.itemView.setTag(position);
            String url = picList.get(position);
            Glide.with(ReadVisitRecordActivity.this)
                    .load(url)
                    .centerCrop()
                    .override(180, 180)
                    .thumbnail(0.1f)
                    .placeholder(me.iwf.photopicker.R.drawable.__picker_ic_photo_black_48dp)
                    .error(me.iwf.photopicker.R.drawable.__picker_ic_broken_image_black_48dp)
                    .into(holder.image);
        }

        @Override
        public int getItemCount() {
            return picList.size();
        }

        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();
            PhotoPreview.builder()
                    .setPhotos(picList)
                    .setShowDeleteButton(false)
                    .setCurrentItem(position)
                    .start(ReadVisitRecordActivity.this);
        }

        class ReadPhotoHolder extends RecyclerView.ViewHolder {

            private ImageView image;
            private ImageView delete;

            public ReadPhotoHolder(View itemView) {
                super(itemView);
                image = itemView.findViewById(R.id.image);
                delete = itemView.findViewById(R.id.delete);
                delete.setVisibility(View.GONE);
            }
        }
    }

}
