package com.shareshow.aide.util;

import com.shareshow.aide.retrofit.entity.VisitData;
import com.shareshow.aide.retrofit.entity.VisitRecord;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by xiongchengguang on 2018/3/5.
 */

public class ArrayUnique {

    public static <T> List<T> unique(List<T> list) {
        List<T> temp = new LinkedList<T>();
        for (T a : list) {
            if (!temp.contains(a)) {
                temp.add(a);
            }
        }
        return temp;
    }


    public static class VisitTrackVid implements Comparator<VisitData> {
        @Override
        public int compare(VisitData o1, VisitData o2) {
            return Integer.valueOf(o2.getVrId()) - Integer.valueOf(o1.getVrId());
        }
    }

}
