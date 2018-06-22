package com.shareshow.aide.util;

import java.util.List;

/**
 * Created by xiongchengguang on 2018/2/7.
 */

public class StringUtil {
    static public String join(List<String> list) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String item : list) {
            if (first)
                first = false;
            else
                sb.append("<#>");
            sb.append(item);
        }
        return sb.toString();
    }

    static public String join(List<String> list, String conjunction) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String item : list) {
            if (first)
                first = false;
            else
                sb.append(conjunction);
            sb.append(item);
        }
        return sb.toString();
    }
}
