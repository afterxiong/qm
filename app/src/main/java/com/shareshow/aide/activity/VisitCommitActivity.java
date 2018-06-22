package com.shareshow.aide.activity;



import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.shareshow.aide.R;
import com.shareshow.aide.dialog.VisitAudioDialog;
import com.shareshow.aide.util.Fixed;
import com.shareshow.aide.util.VisitCacheData;
import com.shareshow.airpc.adapter.PhotoAdapter;
import com.socks.library.KLog;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;
import me.iwf.photopicker.PhotoPicker;
import me.iwf.photopicker.PhotoPreview;
import me.iwf.photopicker.utils.AndroidLifecycleUtils;

/**
 * Created by xiongchengguang on 2018/3/30.
 */

public class VisitCommitActivity extends SimpleActivity {

    @BindView(R.id.toolbar)
    public Toolbar toolbar;
    @BindView(R.id.visit_name)
    public TextView visit_name;
    @BindView(R.id.visit_address)
    public TextView visit_address;
    @BindView(R.id.visit_time)
    public TextView visit_time;
    @BindView(R.id.visit_content)
    public EditText visit_content;
    @BindView(R.id.visit_audio_content_layout)
    public FrameLayout visit_audio_content_layout;

    public ArrayList<String> selectPhotos = new ArrayList<>();
    private SelectPhotoAdapter adapter;
    @BindView(R.id.photo_recycler)
    public RecyclerView recycler;
    private static final String SPILE_SYMBOL = ",";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visit_commit);
        ButterKnife.bind(this);
        initToolbar();
        initView();
        initSelectPhoto();
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



    private void initView() {
        VisitCacheData cacheData = VisitCacheData.get();
        visit_name.setText(String.format(getResources().getString(R.string.commit_visit_name), cacheData.getVrGuestname()));
        visit_address.setText(String.format(getResources().getString(R.string.commit_visit_address), cacheData.getVrAddresss()));
        visit_time.setText(String.format(getResources().getString(R.string.commit_visit_date), cacheData.getVrDate()));
        visit_content.setText(cacheData.getVrContent());
        if (cacheData.getVisitAudioPath().isEmpty()) {
            visit_audio_content_layout.setVisibility(View.GONE);
        } else {
            visit_audio_content_layout.setVisibility(View.VISIBLE);
        }
        List<String> list = cacheData.getVrPicurls();
        if (list != null) {
            selectPhotos.addAll(list);
        }
        visit_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                cacheData.setVrContent(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    private void initSelectPhoto(){
        adapter = new SelectPhotoAdapter();
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recycler.setLayoutManager(manager);
        recycler.setAdapter(adapter);
        View view = LayoutInflater.from(this).inflate(R.layout.activity_visit_commit_add_footer, recycler, false);
        adapter.setFooterView(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhotoPicker.builder()
                        .setPhotoCount(PhotoAdapter.MAX)
                        .setShowCamera(true)
                        .setSelected(selectPhotos)
                        .setPreviewEnabled(false)
                        .start(VisitCommitActivity.this, PhotoPreview.REQUEST_CODE);
            }
        });
    }

    /**
     * 开始录音
     */
    @OnClick(R.id.visit_start_audio)
    public void visit_start_audio(){
        new RxPermissions(this).requestEach(Manifest.permission.RECORD_AUDIO)
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        if (permission.granted){
                            startAudio();
                        } else {
                            Toast.makeText(VisitCommitActivity.this, "权限拒绝，不能录音", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void startAudio() {
        VisitAudioDialog dialog = new VisitAudioDialog();
        dialog.setCancelable(false);
        dialog.show(getFragmentManager(), "dialog");
        dialog.setOnVisitAudioListener(new VisitAudioDialog.OnVisitAudioListener() {
            @Override
            public void close() {
                visit_audio_content_layout.setVisibility(View.VISIBLE);
                VisitCacheData.get().setVisitAudioPath(Fixed.getVisitAudioPath());
            }

            @Override
            public void compile() {
                visit_audio_content_layout.setVisibility(View.VISIBLE);
                VisitCacheData.get().setVisitAudioPath(Fixed.getVisitAudioPath());
            }
        });
    }

    @OnClick(R.id.delete)
    public void delete() {
        File file = new File(Fixed.getVisitAudioPath());
        file.delete();
        visit_audio_content_layout.setVisibility(View.GONE);
        VisitCacheData.get().setVisitAudioPath("");
    }

    /**
     * 播放录音
     */
    @OnClick(R.id.visit_audio_content_layout)
    public void visit_audio_content_layout() {
        File param = new File(Fixed.getVisitAudioPath());
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

    /**
     * 拜访记录提交
     */
    @OnClick(R.id.btn_visit_commit)
    public void btn_visit_commit() {
        VisitCacheData.get().setVrTimeend(String.valueOf(System.currentTimeMillis()));
        VisitCacheData.get().setVisitWay(false);
        presenter.setVisitCommit();
        finish();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PhotoPreview.REQUEST_CODE) {
            if (data != null) {
                selectPhotos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                VisitCacheData.get().setVrPicurls(selectPhotos);
                adapter.notifyDataSetChanged();
            }
        }
    }


    class SelectPhotoAdapter extends RecyclerView.Adapter<SelectPhotoAdapter.SelectPhotoHolder> implements View.OnClickListener {

        public static final int TYPE_FOOTER = 0;
        public static final int TYPE_NORMAL = 1;

        public View footerView;

        public void setFooterView(View view) {
            this.footerView = view;
            notifyItemChanged((selectPhotos.size() + 1));
        }

        public View getFooterView() {
            return footerView;
        }

        @Override
        public int getItemViewType(int position) {
            if (footerView == null) {
                return TYPE_NORMAL;
            }
            if (selectPhotos.size() == position) {
                return TYPE_FOOTER;
            }
            return TYPE_NORMAL;
        }

        @Override
        public SelectPhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (footerView != null && viewType == TYPE_FOOTER) {
                return new SelectPhotoHolder(footerView);
            }
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_visit_photo_selector, parent, false);
            SelectPhotoHolder holder = new SelectPhotoHolder(view);
            holder.delete.setOnClickListener(this);
            holder.image.setOnClickListener(this);
            return holder;
        }


        @Override
        public void onBindViewHolder(SelectPhotoHolder holder, int position) {
            if (getItemViewType(position) == TYPE_FOOTER) return;
            holder.delete.setTag(R.id.delete, position);
            holder.image.setTag(R.id.image, position);
            File param = new File(selectPhotos.get(position));
            Uri uri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                uri = FileProvider.getUriForFile(VisitCommitActivity.this, getPackageName() + ".fileProvider", param);
            } else {
                uri = Uri.fromFile(param);
            }
            boolean canLoadImage = AndroidLifecycleUtils.canLoadImage(holder.image.getContext());
            if (canLoadImage) {
                Glide.with(VisitCommitActivity.this)
                        .load(uri)
                        .centerCrop()
                        .thumbnail(0.1f)
                        .placeholder(me.iwf.photopicker.R.drawable.__picker_ic_photo_black_48dp)
                        .error(me.iwf.photopicker.R.drawable.__picker_ic_broken_image_black_48dp)
                        .into(holder.image);
            }
        }

        @Override
        public int getItemCount() {
            return footerView == null ? selectPhotos.size() : selectPhotos.size() + 1;
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.image) {
                int position = (int) v.getTag(R.id.image);
                PhotoPreview.builder()
                        .setPhotos(selectPhotos)
                        .setShowDeleteButton(false)
                        .setCurrentItem(position)
                        .start(VisitCommitActivity.this);
            } else if (v.getId() == R.id.delete) {
                int position = (int) v.getTag(R.id.delete);
                selectPhotos.remove(position);
                VisitCacheData.get().setVrPicurls(selectPhotos);
                notifyDataSetChanged();
            }
        }

        public class SelectPhotoHolder extends RecyclerView.ViewHolder {
            private ImageView image;
            private ImageView delete;

            public SelectPhotoHolder(View itemView) {
                super(itemView);
                image = itemView.findViewById(R.id.image);
                delete = itemView.findViewById(R.id.delete);
            }
        }
    }
}
