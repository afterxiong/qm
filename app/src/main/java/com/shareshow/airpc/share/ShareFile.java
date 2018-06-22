package com.shareshow.airpc.share;

/**
 * Created by Administrator on 2017/7/5 0005.
 */

public class ShareFile {

    private String fileName;

    private int fileType;

    public ShareFile(String fileName, int fileType) {
        this.fileName = fileName;
        this.fileType = fileType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getFileType() {
        return fileType;
    }

    public void setFileType(int fileType) {
        this.fileType = fileType;
    }
}
