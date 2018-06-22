package com.shareshow.aide.dialog;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.shareshow.aide.R;
import com.shareshow.aide.activity.MoreScreenActivity;
import com.shareshow.aide.event.ControlEvent;
import com.shareshow.aide.service.HeartBeatServer;
import com.shareshow.airpc.activity.SettingActivity;
import com.shareshow.airpc.ports.PositionListener;
import com.shareshow.airpc.util.QMDialog;
import com.shareshow.airpc.widget.BasePopupWindow;

import org.greenrobot.eventbus.EventBus;


/**
 * Created by hzk on 2018/3/19 0019.
 */

public class DevConrtolWindow extends PopupWindow implements View.OnClickListener{


    private final int  baseid = 7788;
    private Context context;
    private TextView createTeam;
    private TextView addTeam;
    private String[] items = null;
    private float dp_2 = 0;
    private float dp_15 = 0;
    private float dp_5 = 0;
    private float dp_3 = 0;
    private float sp_15 = 0;
    private int color_popup = 0;
    private OnItemClickListener itemClickListener = null;

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public DevConrtolWindow(Context context,String[] tiems) {
        super(context);
        this.context = context;
        init(tiems);
        LayoutInflater inflater = inflater = LayoutInflater.from(context);
        View view = inflater(inflater);
        initView(view);
        setPopupStyle(view);
    }

    public void setPopupStyle(View view) {
        setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        setBackgroundDrawable(new ColorDrawable(0x00000000));
        setOutsideTouchable(true);
        setFocusable(true);
        setAnimationStyle(R.style.timepopwindow_anim_style);
        setContentView(view);
    }

    private void init(String[] items){
        if (items != null) {
            this.items = items;
        }
        dp_2 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,2,context.getResources().getDisplayMetrics());
        dp_15 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,10,context.getResources().getDisplayMetrics());
        dp_5 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,5,context.getResources().getDisplayMetrics());
        dp_3 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,3,context.getResources().getDisplayMetrics());
        sp_15 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,15,context.getResources().getDisplayMetrics());
        color_popup = context.getResources().getColor(R.color.popup);
    }



    @Override
    public void onClick(View v) {
        if (itemClickListener != null) {
            int index =  v.getId() - baseid;
            if (index < 0 || index >= items.length) {
                return;
            }
            itemClickListener.OnItemClick(index,items[index]);
        }
        dismiss();
    }

    public View inflater(LayoutInflater inflater) {
        return inflater.inflate(R.layout.window_dev_conrtol, null);
    }

    public void initView(View view) {
        if (!(view instanceof ViewGroup) || items == null) {
            return;
        }
        ViewGroup viewGroup = (ViewGroup)view;
//        createTeam = view.findViewById(R.id.dev_set);
//        addTeam = view.findViewById(R.id.dev_interaction);
//        createTeam.setOnClickListener(this);
//        addTeam.setOnClickListener(this);
        int maxNumber = 0;
        int index = 0;
        for (int i = 0;i < items.length;i++){
            if (maxNumber < items[i].length()) {
                maxNumber = items[i].length();
                index = i;
            }
        }
        for (int i = 0;i < items.length;i++){
            TextView textView = new TextView(context);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            textView.getPaint().setTextSize(sp_15);
            LinearLayout.LayoutParams LinelayoutParams = new LinearLayout.LayoutParams((int)textView.getPaint().measureText(items[index]), 1);
            if (i == 0) {
                layoutParams.setMargins(0,(int)dp_15,0,(int)dp_2);
            } else {
                layoutParams.setMargins(0,(int)dp_5,0,(int)dp_2);
                View line = new View(context);
                line.setBackgroundColor(color_popup);
                line.setLayoutParams(LinelayoutParams);
                viewGroup.addView(line);
            }
            textView.setLayoutParams(layoutParams);
            textView.setTextColor(color_popup);
            textView.setPadding((int)dp_3,(int)dp_3,(int)dp_3,(int)dp_3);
            textView.setText(items[i]);
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
            textView.setId(baseid + i);
            textView.setOnClickListener(this);
            viewGroup.addView(textView);
        }
    }

    public interface OnItemClickListener {
        public void OnItemClick(int index,String timeString);
    }

}
