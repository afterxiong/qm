package com.shareshow.airpc.util;

import java.util.regex.PatternSyntaxException;

/**
 * Created by wulong on 2017/7/11 0011.
 */

public class FileNameRuleUtils {

    private static String Escape_Left_slash_mark = "~1~";//左斜杠
    private static String Escape_Colon = "~2~";//冒号
    private static String Escape_Question = "~3~";//问号
    private static String Left_slash_mark = "/";//左斜杠
    private static String Colon = ":";//冒号
    private static String Question = "?";//问号

    public static String[] spiltFileName(String fileName) throws PatternSyntaxException {
        return fileName.split("_");
    }

    public static String toEscape(String url){
        try {
            String url1 = url.replace(Left_slash_mark, Escape_Left_slash_mark);
            String url2 = url1.replace(Colon, Escape_Colon);
            String url_relust = url2.replace(Question, Escape_Question);
            return url_relust;
        } catch (Exception e){
            return "";
        }
    }
    public static String restoreEscape(String url) {
        try {
            String url1 = url.replace(Escape_Left_slash_mark, Left_slash_mark);
            String url2 = url1.replace(Escape_Colon, Colon);
            String url_relust = url2.replace(Escape_Question, Question);
            return url_relust;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
