package com.shareshow.airpc.imagebat;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore.Video.Thumbnails;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VideoThumbLoader {

	private LruCache<String, Bitmap> lruCache;
	  private static ExecutorService pool;
	public VideoThumbLoader() {
		int maxMemory = (int) Runtime.getRuntime().maxMemory();// ��ȡ���������ڴ�
		int maxSize = maxMemory / 4;
		lruCache = new LruCache<String, Bitmap>(maxSize) {
			@Override
			protected int sizeOf(String key, Bitmap value) {
				return value.getByteCount();
        }
    };
    pool = Executors.newFixedThreadPool(8);
}

public void addVideoThumbToCache(String path, Bitmap bitmap) {
    if (getVideoThumbToCache(path) == null&&bitmap!=null) {
        lruCache.put(path, bitmap);
    }
}

public Bitmap getVideoThumbToCache(String path) {

    return lruCache.get(path);

}

public void showThumbByAsynctack(String path, ImageView imgview) {

    if (getVideoThumbToCache(path) == null) {
       // new MyBobAsynctack(imgview, path).execute(path);
    	queueJob(path, imgview);
    } else {
        imgview.setImageBitmap(getVideoThumbToCache(path));
    }

}

public void queueJob(final String img_name, final ImageView imageView) {
    
    final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
               // if (msg.obj != null) {
                	if (imageView.getTag().equals(img_name)) {
                		imageView.setImageBitmap((Bitmap) msg.obj);
                    }
               // }
               //     imageView.setImageBitmap((Bitmap) msg.obj);
              //  else 
              //  	imageView.setImageResource(R.drawable.base_article_bigimage);
        }  
    };  
  /*  new Thread(){
    	 public void run() {  
             Message message = Message.obtain();  
             Bitmap bitmap = null;
     		// 获取视频的缩略图
     		bitmap = ThumbnailUtils.createVideoThumbnail(img_name, Thumbnails.MICRO_KIND);
     		bitmap = ThumbnailUtils.extractThumbnail(bitmap, 100, 100,
     				ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
     		addVideoThumbToCache(img_name, bitmap);
             message.obj =bitmap;  
             handler.sendMessage(message);  
         }  
    }.start();*/
    pool.execute(new Runnable() {
    	public void run() {  
            Message message = Message.obtain();
            Bitmap bitmap = null;
    		// 获取视频的缩略图
    		bitmap = ThumbnailUtils.createVideoThumbnail(img_name, Thumbnails.MICRO_KIND);
    		bitmap = ThumbnailUtils.extractThumbnail(bitmap, 100, 100,
    				ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
    		addVideoThumbToCache(img_name, bitmap);
            message.obj =bitmap;  
            handler.sendMessage(message);  
        } 
    });  
} 
/*class MyBobAsynctack extends AsyncTask<String, Void, Bitmap> {
    private ImageView imgView;
    private String path;

    public MyBobAsynctack(ImageView imageView, String path) {
        this.imgView = imageView;
        this.path = path;
    }

    @Override
    protected Bitmap doInBackground(String... params) {

        Bitmap bitmap = null;

        try {
            bitmap = ThumbnailUtils.createVideoThumbnail(params[0],
                    Thumbnails.MICRO_KIND);

            Bitmap bitmap2 = ThumbnailUtils.extractThumbnail(bitmap,100,100,
                    ThumbnailUtils.OPTIONS_RECYCLE_INPUT);

            if (getVideoThumbToCache(params[0]) == null) {
                addVideoThumbToCache(path, bitmap2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (imgView.getTag().equals(path)) {
            imgView.setImageBitmap(bitmap);
        }
    }
}*/
}
