package com.shareshow.airpc;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.shareshow.App;
import com.shareshow.aide.util.CacheConfig;
import com.shareshow.aide.util.CacheUserInfo;
import com.shareshow.airpc.util.NetworkUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class Collocation {

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    public static Collocation collocation = null;

    public static Collocation getCollocation() {
        if (collocation == null) {
            synchronized (Collocation.class) {
                if (collocation == null) {
                    collocation = new Collocation();
                }
            }
        }
        return collocation;
    }

    public Collocation() {
        preferences= PreferenceManager.getDefaultSharedPreferences(App.getApp());
        editor=preferences.edit();
    }

    /**
     * 修改进度
     */
    public int getprogress() {
        return preferences.getInt("pro",30);
    }
    public void savepro(int progress) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("pro",progress);
        editor.commit();
    }

    /*
    * 码率
     */

    public int getNetbitRate(){
        if(NetworkUtils.is5GLocalNet()){
            return preferences.getInt("netBitRate",4000);
        }else{
            return preferences.getInt("netBitRate",2500);
        }
    }

    public void saveNetbitRate(int bitrote) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("netBitRate",bitrote);
        editor.commit();
    }


    /**
     * 保存盒子的密码
     *
     * @param address
     * @param passwd
     */
    public void savePassWd(String address, String passwd) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(address, passwd);
        editor.commit();
    }

    public String getPassWd(String address){
        return preferences.getString(address, "");
    }

    /**
     * 保存分享的引导功能执行了
     */
    public void saveGuide(){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("guide", false);
        editor.commit();
    }

    public boolean getGuide(){
        return preferences.getBoolean("guide", true);
    }


    /**
     * 保存设备名称
     *
     * @return
     */
    public String getDeviceName() {
        return preferences.getString("DeviceName", CacheUserInfo.get().getUserName());
    }

    public void saveDeviceName(String s) {
        editor.putString("DeviceName", s);
        editor.commit();
    }
    /**
     * 是否扫码进行连接
     * @param
     */
    public void saveIsScan(boolean a) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isScan",a);
        editor.commit();
    }

    public Boolean getIsScan() {
        return preferences.getBoolean("isScan",false);
    }
    /**
     * 是否截取音频
     *
     */
    public void saveAudio(boolean b) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("audio",b);
        editor.commit();
    }

    public Boolean getAudio() {
        return preferences.getBoolean("audio",false);
    }

    /**
     * 修改帧率
     *
     * @return
     */
    public int getLCprogress() {

        return preferences.getInt("lc", 1);
    }

    public void saveLC(int frameRate) {
        editor.putInt("lc", frameRate);
        editor.commit();
    }
    public int getSort() {

        return preferences.getInt("sort", 0);
    }

    public void saveSort(int frameRate) {
        editor.putInt("sort", frameRate);
        editor.commit();
    }

    /**
     * 修改码率
     */
    public int getQXprogress(){
        return preferences.getInt("qx", 0);
    }

    public void saveQX(int bitRate){
        editor.putInt("qx", bitRate);
        editor.commit();
    }

    /**
     * 保存是第一次登陆
     *
     * @param
     */
    public void saveLogo() {
        editor.putBoolean("logo", true);
        editor.commit();
    }

    public Boolean getLogo() {
        return preferences.getBoolean("logo", false);
    }

    /**
     * 保证是第一次更名
     */
    public void saveName(){
        editor.putBoolean("Name", true);
        editor.commit();
    }

    public Boolean getName() {
        return preferences.getBoolean("Name", false);
    }




    /**
     * 允许投屏能否被分享
     */
    public void savePermit(boolean a) {
        editor.putBoolean("permit", a);
        editor.commit();
    }

    public Boolean getPermit() {
        return preferences.getBoolean("permit", true);
    }

    /**
     * 是否拉伸
     */
    public void saveExpand(boolean a) {
        editor.putBoolean("expand", a);
        editor.commit();
    }

    public Boolean getExpand() {
        return preferences.getBoolean("expand", false);
    }

    /**
     * 是否自动连接
     */
    public void saveSearched(boolean a) {
        editor.putBoolean("searched", a);
        editor.commit();
    }

    public Boolean getSearched() {
        return preferences.getBoolean("searched", false);
    }

    /**
     * 是否开启浮窗
     */
    public void saveFloatWindow(boolean a) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("floatwindow", a);
        editor.commit();
    }

    public Boolean getFloatWindow() {
        return preferences.getBoolean("floatwindow", false);
    }

    public Boolean getFileOpen() {
        return preferences.getBoolean("fileOpen", false);
    }

    public void saveFileOpen(boolean a){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("fileOpen", a);
        editor.commit();
    }

    public Boolean getSaleManager() {
        return preferences.getBoolean("isSaleManager", false);
    }

    public void saveSaleManager(boolean a){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isSaleManager", a);
        editor.commit();
    }


    public void saveFileList(ArrayList<String> files) {
        SharedPreferences.Editor editor = preferences.edit();
        Set<String> set=null;
        if(files!=null){
            set=new HashSet<String>();
            for (String file : files){
                set.add(file);
            }
        }
        editor.putStringSet("FILES",set);
        editor.commit();
    }

    public ArrayList<String> getFileList(){
        Set<String> set= preferences.getStringSet("FILES",null);
        ArrayList<String> list=null;
        if(set!=null){
            list=new ArrayList<>();
            for (String s : set) {
                list.add(s);
            }
        }

        return list;
    }

    public void saveBindIds(String ids){
        if(ids==null){
            CacheConfig.get().saveBindDeviceName(CacheUserInfo.get().getUserPhone(),"");
        }
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("BindIds",ids);
        editor.commit();
    }


    public String getIDS(){
        return preferences.getString("BindIds",null);
    }




    public String getFileFriendName(){
      return preferences.getString("friendName","");
    }

    public void savaFileFriendName(String name){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("friendName", name);
        editor.commit();
    }

    public String getFileFriendAddress(){
        return preferences.getString("friendAddress",null);
    }

    public void savaFileFriendAddress(String name){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("friendAddress", name);
        editor.commit();
    }

    public String getUpDataTime(String upDataTime){
        return preferences.getString(upDataTime, "-1");
    }

    /**
     * @param upDataTime 当前广告类型及当前图片下标组合
     * @param time 设置当前广告类型及图片下标的更新时间
     */
    public void setUpDataTime(String upDataTime , String time){
        editor.putString(upDataTime, time);
        editor.commit();
    }


    /**
     * @return 获取广告标记
     */
    public String getAdHotFlag(){
        return preferences.getString("AdHotFlag", "");
    }

    /**
     * @param adFlag 设置广告标记
     */
    public void setAdHotFlag(String adFlag){
        editor.putString("AdHotFlag", adFlag);
        editor.commit();
    }
    /**
     * @return 获取广告标记
     */
    public String getAdScreenFlag(){
        return preferences.getString("AdScreenFlag", "");
    }

    /**
     * @param adFlag 设置广告标记
     */
    public void setAdScreenFlag(String adFlag){
        editor.putString("AdScreenFlag", adFlag);
        editor.commit();
    }



 }
