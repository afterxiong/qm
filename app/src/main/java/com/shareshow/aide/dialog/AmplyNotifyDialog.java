package com.shareshow.aide.dialog;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.shareshow.App;
import com.shareshow.aide.R;
import com.shareshow.aide.retrofit.entity.AmplyNotify;
import com.shareshow.aide.util.CacheUserInfo;
import com.shareshow.aide.util.Fixed;
import com.shareshow.aide.util.OpenFileUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 通知详情
 * Created by xiongchengguang on 2018/1/9.
 */

public class AmplyNotifyDialog extends BaseDialog {

    private TextView notifyTitle;
    private TextView notifyContent;
    private TextView notifyTime;
    private TextView notifyOrg;
    private static AmplyNotify notify;
    private RecyclerView recycler;
    private LinearLayoutManager manager;
    private List<String> appendixUrlList = new ArrayList<>();
    private List<String> appendixNameList = new ArrayList<>();
    private List<String> appendixOrgList = new ArrayList<>();
    private TextView appeddixText;

    public static AmplyNotifyDialog newInstance(AmplyNotify notify) {
        AmplyNotifyDialog.notify = notify;
        AmplyNotifyDialog notifyDetailed = new AmplyNotifyDialog();
        return notifyDetailed;
    }

    @Override
    public void onResume() {
        super.onResume();
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int width = (int) (dm.widthPixels * 0.9);
        getDialog().getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
    }


    @Override
    public View layout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.affiche_notify_detailed, container, false);
        initView(view);
        return view;
    }


    private void initView(View itemView) {
        appeddixText = itemView.findViewById(R.id.appeddix_text);
        notifyTitle = itemView.findViewById(R.id.notify_title);
        notifyContent = itemView.findViewById(R.id.notify_content);
        notifyTime = itemView.findViewById(R.id.notify_time);
        notifyOrg = itemView.findViewById(R.id.notify_org);
        notifyTitle.setText(notify.getNosTitle());
        SpannableStringBuilder span = new SpannableStringBuilder("缩进" + notify.getNosContent());
        span.setSpan(new ForegroundColorSpan(Color.TRANSPARENT), 0, 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        notifyContent.setText(span);

        String time = notify.getNosCreatetime().trim();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
        long lon = Long.parseLong(time);
        String data = dateFormat.format(lon);
        notifyTime.setText(data);

        appeddixText.setVisibility(View.GONE);

        recycler = itemView.findViewById(R.id.recycler);
        manager = new LinearLayoutManager(getActivity());
        recycler.setLayoutManager(manager);
        recycler.setAdapter(new AppendixAdapter());
        String urlJson = notify.getNosFilepath();
        String nameJson = notify.getNosFilename();
        String orgJson = notify.getNosOrgnames();

        try {
            Gson gson = new Gson();
            JsonParser parser = new JsonParser();
            if (urlJson != null) {
                JsonArray urlJsonArray = parser.parse(urlJson).getAsJsonArray();
                for (JsonElement str : urlJsonArray) {
                    String string = gson.fromJson(str, String.class);
                    appendixUrlList.add(string);
                }
            }

            if (nameJson != null) {
                JsonArray nameJsonArray = parser.parse(nameJson).getAsJsonArray();
                for (JsonElement str : nameJsonArray) {
                    String string = gson.fromJson(str, String.class);
                    appendixNameList.add(string);
                }
            }
            String orgName = orgJson.replace("[", "").replace("]", "").replace("\"", "");
            notifyOrg.setText(orgName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (appendixUrlList.size() > 0) {
            appeddixText.setVisibility(View.VISIBLE);
        }
    }

    class AppendixAdapter extends RecyclerView.Adapter<AppendixAdapter.AppendixHolder> implements View.OnClickListener {

        @Override
        public AppendixHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_open_notif_item_appendix, parent, false);
            AppendixHolder holder = new AppendixHolder(view);
            holder.download.setOnClickListener(this);
            return holder;
        }

        @Override
        public void onBindViewHolder(AppendixHolder holder, int position) {
            holder.download.setTag(position);
            String name = appendixNameList.get(position);
            holder.name.setText(name);
        }

        @Override
        public int getItemCount() {
            return appendixUrlList.size();
        }


        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();
            String url = appendixUrlList.get(position);
            String name = appendixNameList.get(position);
            String cachePhone = CacheUserInfo.get().getUserPhone();
            String pathname = Fixed.getRootPath() + File.separator + cachePhone + File.separator + "notification" + File.separator + name;
            LoadingProgress.newInstance().show(getActivity().getFragmentManager(), "1");
            File file = new File(pathname);
            onFileDownload(url, file);
        }

        public void onFileDownload(String url, File file) {
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
                            startActivity(OpenFileUtil.openFile(getActivity(), file.getPath()));
                            LoadingProgress.newInstance().dismissAllowingStateLoss();
                        }

                        @Override
                        protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {

                        }

                        @Override
                        protected void error(BaseDownloadTask task, Throwable e) {
                            e.printStackTrace();
                            Toast.makeText(App.getApp(), "下载失败，请重试", Toast.LENGTH_LONG).show();
                            LoadingProgress.newInstance().dismissAllowingStateLoss();
                        }

                        @Override
                        protected void warn(BaseDownloadTask task) {

                        }
                    }).start();

        }


        class AppendixHolder extends RecyclerView.ViewHolder {

            private TextView download;
            private TextView name;

            public AppendixHolder(View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.name);
                download = itemView.findViewById(R.id.download);
            }
        }
    }
}
