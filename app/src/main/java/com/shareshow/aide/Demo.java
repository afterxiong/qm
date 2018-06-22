package com.shareshow.aide;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiongchengguang on 2018/1/4.
 */

public class Demo {
    public static void main(String[] args) throws Exception {
        List<String> list = new ArrayList<>(null);
        System.out.println(list.size());
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

    public void s2() {
        List<Integer> arr = new ArrayList<>();
        arr.add(0, 0);
        arr.add(0, 1);
        arr.add(0, 2);
        arr.add(0, 3);
        arr.add(0, 4);
        arr.add(0, 5);

        for (Integer i : arr) {
            System.out.println(i);
        }
    }

    public void s1() {
        String string = "http://www.shareshow.com.cn/download/index.html?";
        int length = string.length();
        System.out.println(length);

        String s1 = "http://www.shareshow.com.cn/download/index.html?{'ssid':'1223','password':'1223','devicepwd':'1223','ip':'127.0.0.1','dType':'1','dValue':'13971518451'}";
        String s2 = s1.substring(48, s1.length());
        System.out.println(s2);
    }
}
