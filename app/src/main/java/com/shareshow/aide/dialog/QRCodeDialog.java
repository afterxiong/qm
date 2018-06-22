package com.shareshow.aide.dialog;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.GsonBuilder;
import com.shareshow.aide.R;
import com.shareshow.aide.retrofit.entity.MyQrCode;
import com.shareshow.aide.retrofit.entity.UserInfo;
import com.shareshow.aide.util.Fixed;
import com.xcg.qrcode.EncodQrCode;

/**
 * Created by xiongchengguang on 2018/1/25.
 */

public class QRCodeDialog extends BaseDialog {

    private ImageView image;
    private TextView addtip;

    public static String USER_INFO = "user_info";
    public static String TAG_TYPE = "tag_type";

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public View layout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_qr_code, container, false);
        image = view.findViewById(R.id.qr_code);
        addtip = view.findViewById(R.id.add_tip);
        init();
        return view;
    }

    private void init() {
        MyQrCode code = new MyQrCode();
        Bundle bundle = getArguments();
        UserInfo userInfo = bundle.getParcelable(USER_INFO);
        if (userInfo == null) {
            Toast.makeText(getActivity(), "二维码生成失败", Toast.LENGTH_LONG).show();
            dismiss();
            return;
        }
        int type = bundle.getInt(TAG_TYPE, 666);
        if (type == Fixed.TAG_ADD_ORG_DEPT) {
            String name;
            if (userInfo.getData().getDeptId().isEmpty()) {
                name = userInfo.getData().getOrgName();
                code.setOrgName(userInfo.getData().getOrgName());
            } else {
                name = userInfo.getData().getDeptName();
                code.setDeptName(userInfo.getData().getDeptName());
            }
            code.setType(Fixed.TAG_ADD_ORG_DEPT);
            addtip.setText("扫一扫，加入" + name );
        } else if (type == Fixed.TAG_ADD_TEAM) {
            String name = userInfo.getData().getTeamName();
            addtip.setText("扫一扫，加入" + name );
            code.setType(Fixed.TAG_ADD_TEAM);
            code.setTeamName(userInfo.getData().getTeamName());
        }

        code.setPhone(userInfo.getData().getUserPhone());
        code.setOrgId(userInfo.getData().getOrgId());
        code.setDeptId(userInfo.getData().getDeptId());
        code.setTeamId(userInfo.getData().getTeamId());
        code.setQrCodeUserId(userInfo.getData().getUserId());
        String json = Fixed.Official_Website + new GsonBuilder().serializeNulls().create().toJson(code);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.icon2);
        Bitmap map = EncodQrCode.createQRCode(json, 200, 200, bitmap);
        image.setImageBitmap(map);
    }
}
