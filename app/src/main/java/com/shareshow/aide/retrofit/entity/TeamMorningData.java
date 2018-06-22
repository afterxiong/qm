package com.shareshow.aide.retrofit.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

import java.util.UUID;

/**
 * Created by FENGYANG on 2018/1/26.
 */

@Entity
public class TeamMorningData{

    @Id(autoincrement = true)
    private Long id; //数据库自增ID

    @Property
    private String uuid;  //数据的唯一标示，用于播放

    @Property
    private String time; //创建录音文件的具体时间

    @Property
    private String duration;//录音的录制的时间

    @Property
    private String path; //录音存储的本地位置

    @Property
    private String author; //录音的用户名

    @Property
    private String day; //录音的具体日期（天）

    @Property
    private String userName;//用户名

    @Property
    private boolean isRemoteAudio; //是否从服务器获取的数据

    @Property
    private String url; //从服务器获取地址

    @Property
    private boolean isUpload;//是否上传到服务器

    @Property
    private String chmId;//服务器给到id，用于上拉和下拉加载

    @Property
    private String phoneNum; //用户的电话号码

    @Property
    private boolean isNeedUpload;//是否需要上传，暂停的录音不需要上传

    @Property
    private int max; //录音播放的总时间

    @Property
    private int progress; //当前录音播放的进度

    @Property
    private boolean isPlay; //是否在播放

    @Property
    private boolean isPlayItem; //是否为播放条目


    @Generated(hash = 57668520)
    public TeamMorningData(Long id, String uuid, String time, String duration,
            String path, String author, String day, String userName,
            boolean isRemoteAudio, String url, boolean isUpload, String chmId,
            String phoneNum, boolean isNeedUpload, int max, int progress,
            boolean isPlay, boolean isPlayItem) {
        this.id = id;
        this.uuid = uuid;
        this.time = time;
        this.duration = duration;
        this.path = path;
        this.author = author;
        this.day = day;
        this.userName = userName;
        this.isRemoteAudio = isRemoteAudio;
        this.url = url;
        this.isUpload = isUpload;
        this.chmId = chmId;
        this.phoneNum = phoneNum;
        this.isNeedUpload = isNeedUpload;
        this.max = max;
        this.progress = progress;
        this.isPlay = isPlay;
        this.isPlayItem = isPlayItem;
    }

    @Generated(hash = 1708512389)
    public TeamMorningData() {
    }


    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDay() {
        return this.day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getAuthor() {
        return this.author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDuration() {
        return this.duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getTime() {
        return this.time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean getIsRemoteAudio() {
        return this.isRemoteAudio;
    }

    public void setIsRemoteAudio(boolean isRemoteAudio) {
        this.isRemoteAudio = isRemoteAudio;
    }

    public boolean getIsUpload() {
        return this.isUpload;
    }

    public void setIsUpload(boolean isUpload) {
        this.isUpload = isUpload;
    }

    public String getChmId() {
        return this.chmId;
    }

    public void setChmId(String chmId){
        this.chmId = chmId;
    }

    public String getPhoneNum() {
        return this.phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public boolean isRemoteAudio() {
        return isRemoteAudio;
    }

    public void setRemoteAudio(boolean remoteAudio) {
        isRemoteAudio = remoteAudio;
    }

    public boolean isUpload() {
        return isUpload;
    }

    public void setUpload(boolean upload) {
        isUpload = upload;
    }

    public boolean getIsNeedUpload() {
        return this.isNeedUpload;
    }

    public void setIsNeedUpload(boolean isNeedUpload) {
        this.isNeedUpload = isNeedUpload;
    }

    public boolean getIsPlayItem() {
        return this.isPlayItem;
    }

    public void setIsPlayItem(boolean isPlayItem) {
        this.isPlayItem = isPlayItem;
    }

    public boolean getIsPlay() {
        return this.isPlay;
    }

    public void setIsPlay(boolean isPlay) {
        this.isPlay = isPlay;
    }

    public int getProgress() {
        return this.progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getMax() {
        return this.max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public String getUuid(){

        return this.uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void clear(){
        setMax(0);
        setProgress(0);
        setIsPlayItem(false);
        setIsPlay(false);
    }


    @Override
    public String toString() {
        return "TeamMorningData{" +
                "id=" + id +
                ", uuid='" + uuid + '\'' +
                ", time='" + time + '\'' +
                ", duration='" + duration + '\'' +
                ", path='" + path + '\'' +
                ", author='" + author + '\'' +
                ", day='" + day + '\'' +
                ", userName='" + userName + '\'' +
                ", isRemoteAudio=" + isRemoteAudio +
                ", url='" + url + '\'' +
                ", isUpload=" + isUpload +
                ", chmId='" + chmId + '\'' +
                ", phoneNum='" + phoneNum + '\'' +
                ", isNeedUpload=" + isNeedUpload +
                ", max=" + max +
                ", progress=" + progress +
                ", isPlay=" + isPlay +
                ", isPlayItem=" + isPlayItem +
                '}';
    }
}
