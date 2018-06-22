package com.xcg;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.shareshow.aide.R;
import com.shareshow.aide.retrofit.entity.MyQrCode;
import com.shareshow.aide.util.Fixed;
import com.shareshow.airpc.util.DebugLog;
import com.xcg.camera.CameraPreview;
import com.xcg.camera.ScanCallback;
import com.xcg.helper.BeepManager;

/**
 * Created by xiongchengguang on 2017/6/22.
 */

public class ScanActivity1 extends AppCompatActivity {

    private RelativeLayout mScanCropView;
    private ImageView mScanLine;
    private ValueAnimator mScanAnimator;
    private CameraPreview mPreviewView;
    private BeepManager beepManager;
    private int REQUEST_PERMISSION_CANERA = 0x0001;
    public final static int BACK_SCAN_CODE_RESULT = 1001;
    public final static String SCAN_CODE_RESULT = "scan_code_result";
    public final static String SCAN_PHONE_RESULT = "scan_phone_result";
    public final static String SCAN_IDS_RESULT = "scan_ids_result";
    public int scan_type = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        mPreviewView = (CameraPreview) findViewById(R.id.capture_preview);
        mScanCropView = (RelativeLayout) findViewById(R.id.capture_crop_view);
        mScanLine = (ImageView) findViewById(R.id.capture_scan_line);
        mPreviewView.setScanCallback(resultCallback);
        beepManager = new BeepManager(this);
        scan_type = getIntent().getIntExtra(Fixed.SCAN_TYPE, 0);
    }

    private ScanCallback resultCallback = new ScanCallback() {
        @Override
        public void onScanResult(String result) {
            beepManager.playBeepSoundAndVibrate();
            try {
                disposeJson(result);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(ScanActivity1.this, "二维码不正确", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    };

    public void disposeJson(String string) throws Exception {
        String json = string.replace(Fixed.Official_Website, "");
        Gson gson = new GsonBuilder().serializeNulls().create();
        MyQrCode code = gson.fromJson(json, MyQrCode.class);
        Intent intent = new Intent();
        intent.putExtra(Fixed.QR_CONTENT,code);
        setResult(RESULT_OK, intent);
        finish();
//        int dt = code.getDt();
//        int type = code.getType();
//        if (type == Fixed.TAG_ADD_ORG){
//            String value = code.getOrgId();
//            intent.putExtra(Fixed.SCAN_RESULT, value);
//            setResult(Fixed.TAG_ADD_ORG, intent);
//        } else if (type == Fixed.TAG_ADD_DEPT){
//            String value = code.getOrgId() + "<#>" + code.getDeptId();
//            intent.putExtra(Fixed.SCAN_RESULT, value);
//            setResult(Fixed.TAG_ADD_DEPT, intent);
//        } else if (type == Fixed.TAG_ADD_TEAM){
//            String value = code.getTeamId();
//            intent.putExtra(Fixed.SCAN_RESULT, value);
//            Bundle bundle = new Bundle();
//            bundle.putParcelable(Fixed.QR_CONTENT, code);
//            intent.putExtras(bundle);
//            setResult(Fixed.TAG_ADD_TEAM, intent);
//        } else if (dt == Fixed.TAG_BINDING){
//            String value = code.getDs()+"<#>"+code.getDn();
//            intent.putExtra(Fixed.SCAN_RESULT, value);
//            setResult(Fixed.TAG_BINDING, intent);
//        } else {
//            Toast.makeText(this, "二维码错误", Toast.LENGTH_SHORT).show();
//        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (mScanAnimator != null) {
            startScanUnKnowPermission();
        }
    }

    @Override
    public void onPause() {
        stopScan();
        super.onPause();
    }

    /**
     * Do not have permission to request for permission and start scanning.
     */
    private void startScanUnKnowPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                startScanWithPermission();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISSION_CANERA);
            }
        } else {
            startScanWithPermission();
        }
    }

    /**
     * There is a camera when the direct scan.
     */
    private void startScanWithPermission() {
        if (mPreviewView.start()) {
            mScanAnimator.start();
        } else {
            Toast.makeText(this, "权限被拒绝", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Stop scan.
     */
    private void stopScan() {
        mScanAnimator.cancel();
        mPreviewView.stop();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (mScanAnimator == null) {
            int height = mScanCropView.getMeasuredHeight() - 25;
            mScanAnimator = ObjectAnimator.ofFloat(mScanLine, "translationY", 0F, height).setDuration(3000);
            mScanAnimator.setInterpolator(new LinearInterpolator());
            mScanAnimator.setRepeatCount(ValueAnimator.INFINITE);
            mScanAnimator.setRepeatMode(ValueAnimator.REVERSE);
            startScanUnKnowPermission();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CANERA) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startScanWithPermission();
            } else {
                Toast.makeText(this, "权限被拒绝后无法扫描", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
