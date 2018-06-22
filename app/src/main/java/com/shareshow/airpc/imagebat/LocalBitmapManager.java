package com.shareshow.airpc.imagebat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LocalBitmapManager {  
	  
    private static HashMap<String, SoftReference<Bitmap>> cache;
    private static ExecutorService pool;
    private Context context;
    
    static {  
        cache = new HashMap<String, SoftReference<Bitmap>>();
        pool = Executors.newFixedThreadPool(8);
    }  
    
    public LocalBitmapManager(Context context){
    	this.context=context;
    }
	
  
    public void loadBitmap(String img_name, ImageView imageView) {
    		Bitmap bitmap = getBitmapFromCache(img_name);
    		if (bitmap != null) {  
    			imageView.setImageBitmap(bitmap);
    		} else {  
    			queueJob(img_name, imageView);
    		}
    }

   
  
    public Bitmap getBitmapFromCache(String img_name) {
    	Bitmap bitmap = null;
        if (cache.containsKey(img_name)) {  
            bitmap = cache.get(img_name).get();  
        }  
        return bitmap;  
    }  
    
    
    public void queueJob(final String img_name, final ImageView imageView) {
         
        final Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                    if (msg.obj != null&&imageView.getTag().equals(img_name)) {
                    	System.out.println("------+++++++-------++++");
                    	imageView.setImageBitmap((Bitmap) msg.obj);
                    }
            }  
        };  
  
        pool.execute(new Runnable() {
            public void run() {  
                Message message = Message.obtain();
                message.obj = downloadBitmap(img_name);  
                handler.sendMessage(message);  
            }  
        });  
    } 
  
    public Bitmap downloadBitmap(String img_name) {
        Bitmap bitmap = null;
        try {
			bitmap = readBitMap(context,img_name);
			cache.put(img_name, new SoftReference<Bitmap>(bitmap));
		} catch (Exception e) {
			e.printStackTrace();
		}
        return bitmap;  
    }  
    
    public Bitmap readBitMap(Context context, String fileName) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Config.RGB_565;
		opt.inSampleSize = 2;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		// 锟斤拷取锟斤拷源图片
		InputStream is = null;
		try {
			is = new FileInputStream(new File(fileName));
		} catch (FileNotFoundException e) {
			// TODO 锟皆讹拷锟斤拷傻锟� catch 锟斤拷
			e.printStackTrace();
		}
		//return compressImage(BitmapFactory.decodeStream(is, null, opt),100);
    	Bitmap bitmap =null;
    	bitmap=compressImageFromFile(fileName);
		return bitmap;

	}

	public Bitmap compressImage(Bitmap image, int size) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		int options = 100;
		// 循环判断如果压缩后图片是否大于100kb,大于继续压缩
		while (baos.toByteArray().length / 1024 > size) {
			// 重置baos即清空baos
			baos.reset();
			// 每次都减少10
			options -= 10;
			// 这里压缩options%，把压缩后的数据存放到baos中
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);

		}
		// 把压缩后的数据baos存放到ByteArrayInputStream中
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
		// 把ByteArrayInputStream数据生成图片
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
		return bitmap;
	}
	

	// 根据路径获得图片并压缩，返回bitmap用于显示
	public Bitmap compressImageFromFile(String srcPath) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		newOpts.inJustDecodeBounds = true;// 只读边,不读内容
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		float hh = 800f;//
		float ww = 480f;//
		int be = 1;
		if (w > h && w > ww) {
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// 设置采样率

		newOpts.inPreferredConfig = Config.ARGB_8888;// 该模式是默认的,可不设
		newOpts.inPurgeable = true;// 同时设置才会有效
		newOpts.inInputShareable = true;// 当系统内存不够时候图片自动被回收

		bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
		return bitmap;
	}
}