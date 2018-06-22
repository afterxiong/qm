package com.shareshow.airpc.update;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;


import com.shareshow.airpc.util.QMFileType;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;

/*
public class DownloadDialog extends BaseDialog {

	private static final String TAG = "DownloadDialog";
	private View view;
	private TextView content,confirm,cancel;
	private Context context;
	private UpdateInfo info;
	private ProgressDialog dialog;

	*//** 当前下载文件长度 *//*
	private int downloadLength = 0;
	*/

/** 文件下载路径 *//*
private URL downloadUrl;

private BufferedInputStream bis = null;
private RandomAccessFile raf = null;
private File folder=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
private File path=new File(folder+File.separator+"new_com.xtxk.ipmatrixplay1.apk");


public DownloadDialog(Context context, UpdateInfo info) {
    super();
    this.context = context;
    this.info = info;
}

public View layout(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
    view = inflater.inflate(R.layout.download_dialog, null);
    init();
    return view;
}

public void init() {
    //content=(TextView) view.findViewById(R.id.content);
    //confirm=(TextView) view.findViewById(R.id.confirm);
    cancel=(TextView) view.findViewById(R.id.cancel);

    content.setText(info.getReborn());

    confirm.setOnClickListener(new OnClickListener() {

        public void onClick(View v) {
            if(path.exists()){
                install();
                Log.d(TAG, "发现本地文件");
            }else{
                new DownloadTask().execute(info.getUrl());
                Log.d(TAG, "开始从网络下载文件");
            }
            DownloadDialog.this.dismiss();
        }
    });

    cancel.setOnClickListener(new OnClickListener() {

        public void onClick(View v) {
            DownloadDialog.this.dismiss();
        }
    });
}
*/
public class DownloadTask extends AsyncTask<String, Integer, Boolean> {
    private String path=null;
    private Activity context=null;
    private ProgressDialog dialog;

    /** 当前下载文件长度 */
    private int downloadLength = 0;
    /** 文件下载路径 */
    private URL downloadUrl;

    private BufferedInputStream bis = null;
    private RandomAccessFile raf = null;

    public DownloadTask( Activity context) {
        this.context=context;
        path= QMFileType.SDCARD+"/任易屏.apk";
        if(new File(path).exists())
            new File(path).delete();
    }
    public DownloadTask() {

    }

    protected Boolean doInBackground(String... params) {

        try {
            downloadUrl=new URL(params[0]);
            URLConnection conn = downloadUrl.openConnection();
            conn.setReadTimeout(8000);
            conn.setConnectTimeout(3000);
            int fileSize = conn.getContentLength();
            dialog.setMax(fileSize/1024/1024);
        //	Log.d(TAG, "文件大小:"+fileSize/1024/1024);
            byte[] buffer = new byte[8192];
            bis = new BufferedInputStream(conn.getInputStream());
            raf = new RandomAccessFile(path, "rwd");
            int len;

            while((len=bis.read(buffer))!=-1){
                downloadLength += len;
                raf.write(buffer, 0, len);
                publishProgress(downloadLength/1024/1024);
                //Log.d(TAG,"已经下载:"+ downloadLength);
            }

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (raf != null) {
                try {
                    raf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    protected void onPreExecute() {
        super.onPreExecute();
        dialog = new ProgressDialog(context);
        dialog.setTitle("启目更新中...");
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setCancelable(false);
        dialog.setProgressNumberFormat("%1d M/%2d M");
        dialog.show();
    }

    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        if(result){
            install();
        //	Log.d(TAG, "下载完毕");
        }else{
            /*if(path.isFile()){
                path.delete();
                Log.d(TAG, "下载失败，删除残留文件");
            }*/
            new File(path).delete();
            Toast.makeText(context, "下载失败，删除残留文件", Toast.LENGTH_SHORT).show();
        }
        dialog.dismiss();
    }

    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        dialog.setProgress(values[0]);
    }

    public void install(){
        Intent installIntent = new Intent();
        installIntent.setAction("android.intent.action.VIEW");
        installIntent.addCategory("android.intent.category.DEFAULT");
        installIntent.setDataAndType(Uri.fromFile(new File(path)),"application/vnd.android.package-archive");
        installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(installIntent);
    }
}
