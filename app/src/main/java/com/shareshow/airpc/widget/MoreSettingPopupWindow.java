package com.shareshow.airpc.widget;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.shareshow.aide.R;
import com.shareshow.airpc.activity.AboutActivity;
import com.shareshow.airpc.activity.SettingActivity;
import com.shareshow.airpc.activity.UserHelpActivity;


/**
 * 更多设置
 */
public class MoreSettingPopupWindow extends BasePopupWindow implements View.OnClickListener{


    private Context context;
    private TextView setting;
//    private TextView about;
  //  private TextView help;

    public MoreSettingPopupWindow(Context context){
        super(context);
        this.context = context;
    }

    @Override
    public View inflater(LayoutInflater inflater) {
        return inflater.inflate(R.layout.activity_main_popup, null);
    }

    /**
     * @param view
     */
    @Override
    public void initView(View view){
        setting = (TextView) view.findViewById(R.id.setting);
      //  about = (TextView) view.findViewById(R.id.about);
      //  help = (TextView) view.findViewById(R.id.help);
        setting.setOnClickListener(this);
      //  about.setOnClickListener(this);
      //  help.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.setting){
             dismiss();
            context.startActivity(new Intent(context,SettingActivity.class));
//             Intent intent = new Intent(context, SettingActivity.class);
//             MainActivity activity= (MainActivity) context;
//             activity.startActivityForResult(intent, MainActivity.SETTING_CODE);
          //  context.startActivity(intent);
//        } else if (v.getId() == R.id.about){
//            dismiss();
//            context.startActivity(new Intent(context, AboutActivity.class));
        }

//       else if (v.getId() == R.id.help){
//            dismiss();
//            context.startActivity(new Intent(context, UserHelpActivity.class));
//
//        }

    }

}
