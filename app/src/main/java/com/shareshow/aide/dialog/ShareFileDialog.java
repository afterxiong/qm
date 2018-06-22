package com.shareshow.aide.dialog;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.shareshow.aide.R;
import com.shareshow.aide.util.OpenFileUtil;
import com.shareshow.aide.wxapi.QQutil;
import com.shareshow.aide.wxapi.WXutil;
import com.shareshow.airpc.util.DebugLog;
import com.tencent.connect.share.QQShare;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXFileObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by hzk on 2018/3/19 0019.
 */

public class ShareFileDialog extends BaseDialog implements View.OnClickListener{

    public static final String FILE_PATH = "filepath";

    @BindView(R.id.cancel)
    public View cancell;
    @BindView(R.id.share_we_chat_but)
    public View shareWeChatBut;
    @BindView(R.id.share_qq_but)
    public View shareQQBut;

    private String filepath;


//    public IWXAPI api;

    public View layout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.dialog_share_menu, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        api = WXutil.getApi(getActivity());
        getDialog().getWindow().getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = getDialog().getWindow().getAttributes();
        lp.gravity = Gravity.BOTTOM;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        this.filepath = getArguments().getString(FILE_PATH,null);
        if (filepath == null) {
            dismiss();
        }
        cancell.setOnClickListener(this);
        shareWeChatBut.setOnClickListener(this);
        shareQQBut.setOnClickListener(this);

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
//        if (api != null) {
//            api.unregisterApp();
//        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cancel:
                dismiss();
                break;
            case R.id.share_we_chat_but:
                DebugLog.showLog(this,"分享文件到微信好友："+filepath);
//                WXFileObject fileObject = new WXFileObject();
//                fileObject.fileData = inputStreamToByte(filepath);
//                //使用媒体消息分享
//                WXMediaMessage msg = new WXMediaMessage(fileObject);
//                msg.title = "title";
//
//                //发送请求
//                SendMessageToWX.Req req = new SendMessageToWX.Req();
//                //创建唯一标识
//                req.transaction = String.valueOf(System.currentTimeMillis());
//                req.message = msg;
//                req.scene = SendMessageToWX.Req.WXSceneSession;


//                WXTextObject textObject = new WXTextObject();
//                textObject.text = "hong";
//                WXMediaMessage msg = new WXMediaMessage();
//                msg.mediaObject = textObject;
//                msg.description = "hong";
//
//                 //发送请求
//                SendMessageToWX.Req req = new SendMessageToWX.Req();
//                //创建唯一标识
//                req.transaction = String.valueOf(System.currentTimeMillis());
//                req.message = msg;
//                req.scene = SendMessageToWX.Req.WXSceneSession;


//                api.handleIntent(getActivity().getIntent(), new IWXAPIEventHandler() {
//                    @Override
//                    public void onReq(BaseReq baseReq) {
//                        DebugLog.showLog(this,"onReq");
//                    }
//
//                    @Override
//                    public void onResp(BaseResp baseResp) {
//                        DebugLog.showLog(this,"onResp");
//                    }
//                });
//                api.sendReq(req);

                share("com.tencent.mm.ui.tools.ShareImgUI",filepath);
                dismiss();
                break;
            case R.id.share_qq_but:
//                DebugLog.showLog(this,"分享文件到QQ好友："+filepath);
                share("com.tencent.mobileqq.activity.JumpActivity",filepath);
                dismiss();

//                Bundle bundle = new Bundle();
//                bundle.putInt(QQShare.SHARE_TO_QQ_, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
//                bundle.putString(QQShare.SHARE_TO_QQ_TITLE,"要分享的标题");
//                bundle.putString(QQShare.SHARE_TO_QQ_TARGET_URL,"http://www.baidu.com/");
//                bundle.putString(QQShare.SHARE_TO_QQ_APP_NAME,"11111111111");
//                QQutil.getTencent(getActivity()).shareToQQ(getActivity(), bundle, new IUiListener() {
//                    @Override
//                    public void onComplete(Object o) {
//                       Toast.makeText(getActivity(),"分享成功",Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onError(UiError uiError) {
//                        Toast.makeText(getActivity(),"分享错误",Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onCancel() {
//                        Toast.makeText(getActivity(),"取消分享",Toast.LENGTH_SHORT).show();
//                    }
//                });


                break;
        }
    }


    private void share(String type,String filePath){
        File file = new File(filePath);
        if(!file.exists()){
            Toast.makeText(getActivity(), "文件不存在", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        String fileType = OpenFileUtil.getMIMEType(new File(filePath));
        intent.setType(fileType); // 查询所有可以分享的
        List<ResolveInfo> resInfo = getActivity().getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (!resInfo.isEmpty()) {
            List<Intent> targetedShareIntents = new ArrayList<Intent>();
            for (ResolveInfo info : resInfo){
                if (info.activityInfo.name.contains(type)){
                    Uri uri = Uri.fromFile(file);
                    Intent targeted = new Intent(Intent.ACTION_SEND);
                    targeted.setType(fileType);
                    ActivityInfo activityInfo = info.activityInfo;
                    targeted.putExtra(Intent.EXTRA_STREAM, uri); // 分享出去的标题
                    targeted.setPackage(activityInfo.packageName);
                    targeted.setClassName(activityInfo.packageName, info.activityInfo.name);
//                    PackageManager pm = getActivity().getApplication().getPackageManager(); // 微信有2个怎么区分-。- 朋友圈还有微信
                    targetedShareIntents.add(targeted);
                }
            } // 选择分享时的标题
            String tempName = file.getName();
            Intent chooserIntent = Intent.createChooser(targetedShareIntents.remove(0), "分享文件:"+tempName);
            if (chooserIntent == null)
            { return; }
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedShareIntents.toArray(new Parcelable[] {}));
            try {
                startActivity(chooserIntent);
            } catch (android.content.ActivityNotFoundException ex) {
                ex.printStackTrace();
                Toast.makeText(getActivity(), "找不到该分享应用组件", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 将输入的流转换为byte数组
     * @return byte数组
     */
    public static byte[] inputStreamToByte(String path)
    {
        try {
            FileInputStream fis = new FileInputStream(path);
            ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
            int ch;
            while ((ch = fis.read()) != -1) {
                bytestream.write(ch);
            }
            byte imgdata[] = bytestream.toByteArray();
            bytestream.close();
            return imgdata;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
