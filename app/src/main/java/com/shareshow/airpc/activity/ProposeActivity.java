package com.shareshow.airpc.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.shareshow.aide.R;
import com.shareshow.airpc.adapter.PhotoAdapter;

import com.shareshow.airpc.util.DebugLog;
import com.shareshow.airpc.util.QMUtil;
import com.shareshow.airpc.widget.RecyclerItemClickListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.iwf.photopicker.PhotoPicker;
import me.iwf.photopicker.PhotoPreview;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import qiu.niorgai.StatusBarCompat;
import com.shareshow.airpc.widget.BaseActivity;

/**
 * Created by xiongchengguang on 2017/4/10.
 */

//@ContentView(R.layout.x_activity_propose)
public class ProposeActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    public Toolbar toolbar;
//  @BindView(R.id.icon_img)
//  public ImageView image;
    @BindView(R.id.recycler)
    public RecyclerView recycler;
    @BindView(R.id.text_su)
    public EditText et_su;
    @BindView(R.id.text_tel)
    public EditText et_tel;

    private PhotoAdapter photoAdapter;

    private ArrayList<String> selectedPhotos = new ArrayList<>();
    private String code ="";

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.x_activity_propose);
        ButterKnife.bind(this);
        client = new OkHttpClient();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this, R.color.colorPrimaryDark));
        initToolbar();
        initRecyclerView();
        initVisionCode();
    }

    private void initVisionCode(){
        try {
            code = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void back(View view){
        onBackPressed();
    }

    /**
     * 上面的大范围的点击获取焦点
     */
    public void edit(View v){
        et_su.requestFocus();
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void initToolbar() {
//		setSupportActionBar(toolbar);
//		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//		getSupportActionBar().setDisplayShowTitleEnabled(false);
//		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				onBackPressed();
//			}
//		});
    }

    private void initRecyclerView() {
//		manager = new LinearLayoutManager(this);
//		manager.setOrientation(LinearLayoutManager.HORIZONTAL);
//		picRecycler.setLayoutManager(manager);
//		adapter = new ImageAdapter();
//		picRecycler.setAdapter(adapter);
        photoAdapter = new PhotoAdapter(this, selectedPhotos);
        recycler.setLayoutManager(new StaggeredGridLayoutManager(4, OrientationHelper.VERTICAL));
        recycler.setAdapter(photoAdapter);

        recycler.addOnItemTouchListener(new RecyclerItemClickListener(this,
                new RecyclerItemClickListener.OnItemClickListener(){
                    @Override
                    public void onItemClick(View view, int position){
                        if (photoAdapter.getItemViewType(position) == PhotoAdapter.TYPE_ADD) {
                            PhotoPicker.builder()
                                    .setPhotoCount(PhotoAdapter.MAX)
                                    .setShowCamera(true)
                                    .setPreviewEnabled(false)
                                    .setSelected(selectedPhotos)
                                    .start(ProposeActivity.this);
                        }else{
                            PhotoPreview.builder()
                                    .setPhotos(selectedPhotos)
                                    .setCurrentItem(position)
                                    .start(ProposeActivity.this);
                        }
                    }
                }));
    }

    public void addImage(View view) {
//		PhotoPicker.builder()
//				.setPhotoCount(3)
//				.setShowCamera(true)
//				.setPreviewEnabled(false)
//				.setSelected(imageList)
//				.start(this, PhotoPicker.REQUEST_CODE);

//
//		PhotoPicker.builder()
//				.setPhotoCount(3)
//				.setShowCamera(true)
//				.setShowGif(true)
//				.setPreviewEnabled(false)
//				.start(this, PhotoPicker.REQUEST_CODE);
//		PhotoPreview.builder()
//				.setShowDeleteButton(true);


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//		if (resultCode == RESULT_OK && requestCode == PhotoPicker.REQUEST_CODE) {
//			if (data != null) {
//				ArrayList<String> photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
//				selectedPhotos.clear();
//				selectedPhotos.addAll(photos);
//				for (String str : photos) {
//					KLog.d(str);
//				}
//				adapter.notifyDataSetChanged();
//			}
//		}
        if (resultCode == RESULT_OK && (requestCode == PhotoPicker.REQUEST_CODE || requestCode == PhotoPreview.REQUEST_CODE)) {
            List<String> photos = null;
            if (data != null) {
                photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
            }
            selectedPhotos.clear();
            if (photos != null) {
                selectedPhotos.addAll(photos);
            }
            photoAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 提交信息
     */
    private String suggestion;
    private String tele;

    @OnClick(R.id.propose_commit)
    public void commit(View v) {
        suggestion = et_su.getEditableText().toString().trim();
        tele = et_tel.getEditableText().toString().trim();
        if (TextUtils.isEmpty(suggestion) && selectedPhotos.size() == 0) {
            Toast.makeText(getApplicationContext(), "亲！您所提交信息为空！", Toast.LENGTH_LONG).show();
            return;
        }
        //当联系方式为空的时候，提示输入联系方式
        if (TextUtils.isEmpty(tele)) {
            inputName();
        } else {
            commitData();
        }

    }

    /**
     * 提交数据给后台
     * 设备信号：
     * android版本
     * 任易屏版本
     */

    String constantpath = "http://www.shareshow.com.cn/NetShare/suggestion.form";
    public static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
    private OkHttpClient client;
    private MultipartBody.Builder builder;

    private void commitData(){
        // TODO Auto-generated method stub
        builder = new MultipartBody.Builder()
                .addFormDataPart("MessageText", suggestion)
                .addFormDataPart("ContactWay", tele)
                .addFormDataPart("DeviceInfo", android.os.Build.MODEL)
                .addFormDataPart("AppSystemInfo", android.os.Build.VERSION.RELEASE)
                .addFormDataPart("AppInfo", code)
                .setType(MultipartBody.FORM);
        for (int i = 0; i < selectedPhotos.size(); i++){
            File file = new File(selectedPhotos.get(i));
            if (file != null){
                builder.addFormDataPart("img", file.getName(), RequestBody.create(MEDIA_TYPE_PNG, file));
            }
        }

        RequestBody formBody = builder.build();

        DebugLog.showLog(this,"DeviceInfo："+android.os.Build.MODEL+"AppSystemInfo:"+ android.os.Build.VERSION.RELEASE+"AppInfo:"+code);
        Request request = new Request.Builder().url(constantpath).post(formBody).build();
        //提示正在提交中
        QMUtil.getInstance().showProgressDialog(this, R.string.tijiao);

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call arg0, IOException arg1) {
                // TODO Auto-generated method stub
                QMUtil.getInstance().closeDialog();
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "网络连接失败！", Toast.LENGTH_LONG).show();
                    }
                });

            }

            @Override
            public void onResponse(Call arg0, Response arg1) throws IOException {
                QMUtil.getInstance().closeDialog();
                final String temp = arg1.body().string();

                if (!temp.contains("SUCCESS")) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            QMUtil.getInstance().showToast2(getApplicationContext(), "提交失败!");
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run(){
                            // TODO Auto-generated method stub
                            et_su.setText(null);
                            et_tel.setText(null);
                            selectedPhotos.clear();
//							setRecyvlerWidth();
                            photoAdapter.notifyDataSetChanged();
                            QMUtil.getInstance().showToast2(getApplicationContext(), "提交成功!");
                            finish();
                        }
                    });
                }

            }
        });
    }

    //弹出的询问框
    public void inputName() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, R.style.MyDialognotitle);
        final AlertDialog create = alertDialog.create();
        View view = LayoutInflater.from(this).inflate(R.layout.commit_qu, null);
        create.setCancelable(false);
        create.show();
        create.getWindow().setContentView(view);
        create.getWindow().setLayout(getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().widthPixels);
        TextView confirm = (TextView) view.findViewById(R.id.ok);
        TextView cancel = (TextView) view.findViewById(R.id.no);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commitData();
                create.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                create.dismiss();
                return;
            }
        });
    }


//	class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
//
//		@Override
//		public ImageViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
//			View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.x_propose_item, null);
//			ImageViewHolder holder = new ImageViewHolder(view);
//			holder.btn = (Button) view.findViewById(R.id.child_delete);
//			holder.btn.setOnClickListener(new View.OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					//点击后移除图片
//					imageList.remove(viewType);
//					//更新UI
//					adapter.notifyDataSetChanged();
//				}
//			});
//			return holder;
//		}
//
//		@Override
//		public void onBindViewHolder(ImageViewHolder holder, int position) {
//			File file = new File(imageList.get(position));
//			Glide.with(ProposeActivity.this).load(file).override(200, 200).into(holder.imageItem);
//		}
//
//		@Override
//		public int getItemCount() {
//			KLog.d(imageList.size());
//			return imageList.size();
//		}
//
//		class ImageViewHolder extends RecyclerView.ViewHolder {
//
//			private ImageView imageItem;
//			private Button btn;
//
//			public ImageViewHolder(View itemView) {
//				super(itemView);
//				imageItem = (ImageView) itemView.findViewById(R.id.image_item);
//				btn= (Button) itemView.findViewById(R.id.child_delete);
//			}
//		}
//	}
}
