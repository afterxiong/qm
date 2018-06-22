package com.shareshow.aide.dialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Selection;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shareshow.App;
import com.shareshow.aide.R;
import com.shareshow.airpc.Collocation;
import com.shareshow.airpc.util.DebugLog;


/**
 * Created by xiongchengguang on 2017/5/22.
 */

public class InputNameDialog extends BaseDialog implements View.OnClickListener,CompoundButton.OnCheckedChangeListener {

    private View view;
    private CheckBox saomiao;
    private CheckBox auto;
    private EditText inputname;
    private TextView ensure;
    private LinearLayout scanConn;
    private LinearLayout autoConn;
    private OnDialogDismissListener listener;
    private Handler mHandler =new Handler();

    public InputNameDialog(){

    }


    @Override
    public View layout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.style_setting, null);
        initView();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        DisplayMetrics dm = getResources().getDisplayMetrics();
        getDialog().getWindow().setLayout(dm.widthPixels - 200, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    public int px2dp(int val) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, val, getResources().getDisplayMetrics());
    }

    private void initView() {
        inputname = (EditText) view.findViewById(R.id.et);
        saomiao = (CheckBox) view.findViewById(R.id.saomiao);
        saomiao.setOnCheckedChangeListener(this);
        auto = (CheckBox) view.findViewById(R.id.auto);
        auto.setOnCheckedChangeListener(this);
        ensure = (TextView) view.findViewById(R.id.ensure);
        scanConn = (LinearLayout) view.findViewById(R.id.scanConn);
        scanConn.setOnClickListener(this);
        autoConn = (LinearLayout) view.findViewById(R.id.autoConn);
        autoConn.setOnClickListener(this);
        ensure.setOnClickListener(this);
        Boolean isScan =Collocation.getCollocation().getIsScan();
        inputname.setHint("请输入设备名称");
        inputname.setText(Collocation.getCollocation().getDeviceName());
        //设置光标到最后
        Editable text = inputname.getText();
        Selection.setSelection(text,text.length());
        inputname.addTextChangedListener(textWatcher);
        if (isScan) {
            saomiao.setChecked(true);
            auto.setChecked(false);
        }else {
            saomiao.setChecked(false);
            auto.setChecked(true);
        }

    }


    private TextWatcher textWatcher= new TextWatcher(){

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count){
            String editable = inputname.getText().toString();
//                String str = QMUtil.getInstance().stringFilter(editable);
            String str=editable;
            if(str!=null&&str.length()>10){
                Toast.makeText(getActivity(),"名字最多输入10个字符！", Toast.LENGTH_SHORT).show();
                str=str.substring(0,10);
                inputname.setText(str);
                inputname.setSelection(str.length());
            }else if(!editable.equals(str))
            {
                inputname.setText(str);
                inputname.setSelection(str.length());
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after){

            mHandler.postDelayed(new Runnable(){

                @Override
                public void run(){
                    showSoftInputView(inputname);
                }

            }, 300);

        }

        @Override
        public void afterTextChanged(Editable s){
            // TODO Auto-generated method stub
            setEditTextInEmptySpace(inputname);
        }

    };

    public void setEditTextInEmptySpace(EditText et){
        InputFilter filter=  new InputFilter(){
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if(source.equals(" ")){
                    return "";
                }
                return null;
            }
        };
        et.setFilters(new InputFilter[]{filter});
    }

    private void showSoftInputView(EditText text){
        ((InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE)).showSoftInput(text, 0);
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.ensure:
                inputname.removeTextChangedListener(textWatcher);
                String tempName = inputname.getEditableText().toString();
                if(!tempName.equals("")||!TextUtils.isEmpty(inputname.getText().toString().trim())){
                    Collocation.getCollocation().saveDeviceName(tempName);
                    Collocation.getCollocation().saveName();
                    Collocation.getCollocation().saveIsScan(saomiao.isChecked());
                    this.dismiss();
                }else{
                      inputname.setText(Collocation.getCollocation().getDeviceName());
                      Toast.makeText(App.getApp(), "您没有设置名称", Toast.LENGTH_LONG).show();
                }

                break;
            case R.id.autoConn:
                saomiao.setChecked(false);
                auto.setChecked(true);
                break;
            case R.id.scanConn:
                saomiao.setChecked(true);
                auto.setChecked(false);
                break;
        }
  }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
        switch (buttonView.getId()) {
            case R.id.saomiao:
                saomiao.setChecked(isChecked);
                auto.setChecked(!isChecked);
                break;

            case R.id.auto:
                auto.setChecked(isChecked);
                saomiao.setChecked(!isChecked);
                break;
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog){
        super.onDismiss(dialog);
        DebugLog.showLog(this,"dialog 关闭了...");
        if(listener!=null){
            listener.OnDialogDismiss();
        }
    }

    public interface OnDialogDismissListener{

        void OnDialogDismiss();

    }

    public void setOnDialogDismissListener(OnDialogDismissListener listener){
        this.listener = listener;
    }
}
