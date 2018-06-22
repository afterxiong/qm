package com.shareshow.aide.util.uuid.cache;

import android.os.Environment;

import com.shareshow.aide.util.uuid.CacheUuid;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;


/**
 * Android目录下缓存
 * Created by xiongchengguang on 2016/11/4.
 */

public class AndroidFileCache implements CacheUuid {
    private final static String DEFAULT_FILE_NAME = "system_device_id";
    private final static String FILE_ANDROID = Environment.getExternalStoragePublicDirectory("Android") + File.separator + DEFAULT_FILE_NAME;

    public void put(String key) {
        try {
            File file = new File(FILE_ANDROID);
            FileWriter writer = new FileWriter(file);
            writer.write(key);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String get() {
        BufferedReader reader = null;
        try {
            File file = new File(FILE_ANDROID);
            reader = new BufferedReader(new FileReader(file));
            return reader.readLine();
        } catch (Exception e) {
            return null;
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
