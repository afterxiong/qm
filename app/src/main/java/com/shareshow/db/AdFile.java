package com.shareshow.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

/**
 * Created by FENGYANG on 2018/1/24.
 */

@Entity
public class AdFile {

    @Id(autoincrement = true)
    private Long id;

    @Property
    private String filePath;

    @Property
    private String fileId;

    @Property
    private String type;

    @Property
    private String shemeIndex;

    @Property
    private String adcIndex;

    @Property
    private String hotUrl;

    @Property
    private String videoFlag;

    @Property
    private String phone;

    @Property
    private String fileName;



    @Generated(hash = 2093577980)
    public AdFile(Long id, String filePath, String fileId, String type,
            String shemeIndex, String adcIndex, String hotUrl, String videoFlag,
            String phone, String fileName) {
        this.id = id;
        this.filePath = filePath;
        this.fileId = fileId;
        this.type = type;
        this.shemeIndex = shemeIndex;
        this.adcIndex = adcIndex;
        this.hotUrl = hotUrl;
        this.videoFlag = videoFlag;
        this.phone = phone;
        this.fileName = fileName;
    }

    @Generated(hash = 261148992)
    public AdFile() {
    }



    public String getFileId(){
        return this.fileId;
    }

    public void setFileId(String fileId){
        this.fileId = fileId;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public void setFilePath(String filePath){
        this.filePath = filePath;
    }

    public Long getId(){
        return this.id;
    }

    public void setId(long id){
        this.id = id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getvideoFlag() {
        return this.videoFlag;
    }

    public void setvideoFlag(String videoFlag) {
        this.videoFlag = videoFlag;
    }

    public String getHotUrl() {
        return this.hotUrl;
    }

    public void setHotUrl(String hotUrl) {
        this.hotUrl = hotUrl;
    }

    public String getAdcIndex() {
        return this.adcIndex;
    }

    public void setAdcIndex(String adcIndex) {
        this.adcIndex = adcIndex;
    }

    public String getShemeIndex() {
        return this.shemeIndex;
    }

    public void setShemeIndex(String shemeIndex) {
        this.shemeIndex = shemeIndex;
    }


    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getVideoFlag() {
        return this.videoFlag;
    }

    public void setVideoFlag(String videoFlag) {
        this.videoFlag = videoFlag;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }





}
