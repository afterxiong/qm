package com.shareshow.aide.util;

/**
 * Created by xiongchengguang on 2017/11/12.
 */

public class SoundRecorder {
//    private String dstPath;
//
//    private String dstFolder;
//    private MediaRecorder mediaRecorder;
//    private static final int MAX_DURATION_MS = 1000 * 60 * 10;
//    private boolean isAudioing = false;
//
//    public String getDstPath() {
//        return dstPath;
//    }
//
//    public SoundRecorder() {
//        this(Environment.getExternalStorageDirectory() + File.separator + "AudioRecoder");
//    }
//
//    public static SoundRecorder get() {
//        return AudioRecorderHolder.holder;
//    }
//
//    private static class AudioRecorderHolder {
//        private static SoundRecorder holder = new SoundRecorder();
//    }
//
//
//    public SoundRecorder(String dstPath) {
//        File file = new File(dstPath);
//        if (!file.exists()) {
//            file.mkdirs();
//        }
//        this.dstPath = dstPath;
//    }
//
//    public void startAudioRecorder() {
//        if (isAudioing) {
//            KLog.d("录音进行中");
//            return;
//        }
//        isAudioing = true;
//        if (mediaRecorder == null) {
//            mediaRecorder = new MediaRecorder();
//        }
//        String cachePhone = CacheConfig.get().getUserPhone();
//        if (cachePhone.isEmpty()) {
//            cachePhone = "cacheAudio";
//        }
//        dstFolder = Fixed.getRootPath() + File.separator + cachePhone + File.separator + File.separator + "MorningAudio" + File.separator + getNormalTime() + ".aac";
//        File file = new File(dstFolder);
//        File parent = file.getParentFile();
//        if (!parent.exists()) {
//            parent.mkdirs();
//        }
//        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
//        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.HE_AAC);
//        mediaRecorder.setOutputFile(dstFolder);
//        mediaRecorder.setMaxDuration(MAX_DURATION_MS);
//        try {
//            mediaRecorder.prepare();
//            mediaRecorder.start();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public String getDstFolder() {
//        return dstFolder;
//    }
//
//    public void stopAudioRecorder() {
//        if (mediaRecorder == null) {
//            return;
//        }
//        try {
//            isAudioing = false;
//            mediaRecorder.stop();
//            mediaRecorder.reset();
//            mediaRecorder.release();
//            mediaRecorder = null;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    public String getNormalTime() {
//        long value = System.currentTimeMillis();
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String time = format.format(new Date(value));
//        return time;
//    }
//

}
