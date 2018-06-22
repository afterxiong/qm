package com.shareshow.aide.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import com.socks.library.KLog;
import com.tbruyelle.rxpermissions2.RxPermissions;

/**
 * Created by FENGYANG on 2018/2/6.
 */

public class PermissionUtil {

    public static final String AUDIO_PERMISSION = Manifest.permission.RECORD_AUDIO;

    public static final String SD_READ_PERMISSION = Manifest.permission.READ_EXTERNAL_STORAGE;

    public static final String SD_WRITE_PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE;

    public static void goAppDetailSettingIntent(Context context){
        Intent localIntent=new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if(Build.VERSION.SDK_INT>=9){
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package",context.getPackageName(),null));
        }else if(Build.VERSION.SDK_INT<=8){
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName("com.android.settings","com.android.setting.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName",context.getPackageName());
        }else if(Build.VERSION.SDK_INT>=11){
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", context.getPackageName(), null);
            intent.setData(uri);
        }
        context.startActivity(localIntent);
    }


    public static void getPermission(Activity mContext,String mPermission,PermissionListener listener){
        new RxPermissions(mContext).requestEach(mPermission)
                .subscribe(permission ->{
                    if (permission.granted){
                        // 用户已经同意该权限
                        //KLog.d(permission.name + " is granted.");
                        if(listener!=null){
                            listener.Success();
                        }
                    }else if (permission.shouldShowRequestPermissionRationale){
                        // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
                        KLog.d(permission.name + " is denied. More info should be provided.");
                        // getPermission(mContext,mPermission,listener);
                    }else{
                        if(listener!=null){
                            listener.Fail();
                        }
                        // 用户拒绝了该权限，并且选中『不再询问』
                        KLog.d(permission.name + " is denied.");
//                        Toast.makeText(BaseActivity.this, "拒绝后无法使用App", Toast.LENGTH_SHORT).show();
                    }

                });
    }

    public static void getPermission(Activity mContext,String[] mPermissions,PermissionListener listener){
        new RxPermissions(mContext).requestEach(mPermissions[0],mPermissions[1])
                .subscribe(permission ->{
                    if (permission.granted){
                        // 用户已经同意该权限
                        //KLog.d(permission.name + " is granted.");
                        if(listener!=null){
                            listener.Success();
                        }
                    }else if (permission.shouldShowRequestPermissionRationale){
                        // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
                        KLog.d(permission.name + " is denied. More info should be provided.");
                        // getPermission(mContext,mPermission,listener);
                    }else{
                        if(listener!=null){
                            listener.Fail();
                        }
                        // 用户拒绝了该权限，并且选中『不再询问』
                        KLog.d(permission.name + " is denied.");
//                        Toast.makeText(BaseActivity.this, "拒绝后无法使用App", Toast.LENGTH_SHORT).show();
                    }

                });
    }

    public static boolean isPremission(Activity mContext,String[] permissions){
        RxPermissions rxPermissions = new RxPermissions(mContext);
        for (String permission : permissions) {
            if(!rxPermissions.isGranted(permission)){
                return false;
            }
        }
        return true;
    }

    public interface PermissionListener{

        void Success();

        void Fail();
    }

}
