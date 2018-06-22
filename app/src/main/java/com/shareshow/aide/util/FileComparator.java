package com.shareshow.aide.util;

import java.io.File;
import java.util.Comparator;

/**
 * 文件排序
 * Created by xiongchengguang on 2018/1/24.
 */

public class FileComparator implements Comparator<File> {
    @Override
    public int compare(File o1, File o2) {
        if (o1.lastModified() < o2.lastModified()) {
            return 1;// 最后修改的文件在前
        } else {
            return -1;
        }
    }
}
