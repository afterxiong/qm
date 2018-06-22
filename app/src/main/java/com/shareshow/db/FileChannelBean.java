package com.shareshow.db;

import java.util.List;

/**
 * Created by wulong on 2017/12/11 0011.
 */

public class FileChannelBean {
    /**
     * command : 301
     * content : [{"fileName":"1.file_icon_txt","creatTime":2341221,"fileType":1,"fileId":"fwe324q1r"},{"fileName":"1.file_icon_txt","creatTime":2341221,"fileType":1,"fileId":"fwe324q1r"}]
     * type : 1
     */

    private String command;
    private List<ContentBean> content;

    public FileChannelBean(String command, List<ContentBean> content){
        this.command = command;
        this.content = content;
    }

    public String getCommand(){
        return command;
    }

    public List<ContentBean> getContent() {
        return content;
    }

    public static class ContentBean {
        /**
         * fileName : 1.file_icon_txt
         * creatTime : 2341221
         * fileType : 1
         * fileId : fwe324q1r
         */

        private String fileName;
        private int fileType; //广告1，学习文件2，拜访文件3
        private String fileId;
        private String path;
        private String fileVid;
        private long fileLength;

        public ContentBean(String fileName, int fileType, String fileId, String path, String fileVid, long fileLength) {
            this.fileName = fileName;
            this.fileType = fileType;
            this.fileId = fileId;
            this.path = path;
            this.fileVid = fileVid;
            this.fileLength = fileLength;
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

        public String getFileId() {
            return fileId;
        }

        public void setFileId(String fileId) {
            this.fileId = fileId;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getFileVid() {
            return fileVid;
        }

        public void setFileVid(String fileVid) {
            this.fileVid = fileVid;
        }

        public long getFileLength() {
            return fileLength;
        }

        public void setFileLength(long fileLength) {
            this.fileLength = fileLength;
        }

        @Override
        public String toString() {
            return "ContentBean{" +
                    "fileName='" + fileName + '\'' +
                    ", fileType=" + fileType +
                    ", fileId='" + fileId + '\'' +
                    ", path='" + path + '\'' +
                    ", fileVid='" + fileVid + '\'' +
                    ", fileLength=" + fileLength +
                    '}';
        }
    }

        //{command:301,content:[{fileName:"1.file_icon_txt",creatTime:2341221,fileType:1,fileId:"fwe324q1r"},{fileName:"1.file_icon_txt",creatTime:2341221,fileType:1,fileId:"fwe324q1r"}],type:1}


}
