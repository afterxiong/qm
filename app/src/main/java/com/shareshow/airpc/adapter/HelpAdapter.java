package com.shareshow.airpc.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.shareshow.App;
import com.shareshow.aide.R;
import com.shareshow.airpc.activity.LoadingHelpActivity;
import com.shareshow.airpc.socket.common.HelpItem;

import java.util.ArrayList;

/**
 * Created by TEST on 2017/9/18.
 */

public class HelpAdapter extends BaseExpandableListAdapter {


    private Context mContext;
    private ArrayList<HelpItem> list;
    private int position;

    public HelpAdapter(ArrayList<HelpItem> list, Context context){
        this.list=list;
        this.mContext=context;
    }


    public void setData(ArrayList<HelpItem> list, int position){
        this.list=list;
        this.position = position;
        notifyDataSetChanged();
    }


    @Override
    public int getGroupCount() {
        return list.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return list.get(groupPosition).getChildNames().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return list.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return list.get(groupPosition).getChildNames().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent){
        GroupViewHolder holder;
        if(convertView==null){
            holder=new GroupViewHolder();
            convertView= View.inflate(mContext, R.layout.help_item,null);
            holder.group= (TextView) convertView.findViewById(R.id.item);
            convertView.setTag(holder);
        }else{
            holder= (GroupViewHolder) convertView.getTag();
        }
        holder.group.setTextSize(18);
        holder.group.setText(list.get(groupPosition).getItemName());
        return convertView;
    }


    /**
     * @param groupPosition
     * @param childPosition
     * @param isLastChild
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent){
        ChildViewHolder holder;
        if(convertView==null){
            holder=new ChildViewHolder();
            convertView= View.inflate(mContext, R.layout.help_item,null);
            holder.child= (TextView) convertView.findViewById(R.id.item);
            convertView.setTag(holder);
        }else{
            holder= (ChildViewHolder) convertView.getTag();
        }

        holder.child.setTextSize(14);
        holder.child.setTextColor(mContext.getResources().getColor(R.color.viewfinder_mask));
        String text=list.get(groupPosition).getChildNames().get(childPosition);
        if(position==0&&groupPosition==0&&childPosition==2){
            openShareshowNet(holder, text);
        }else if(position==1&&groupPosition==0&&childPosition==0){
            openShareshowNet(holder, text);
        }else if(position==2&&groupPosition==0&&childPosition==2){
            openShareshowNet(holder, text);
        }
        else{
            holder.child.setText(text);
        }

       // holder.child.setMovementMethod(LinkMovementMethod.getInstance());
       // holder.child.setText(Html.fromHtml(list.get(groupPosition).getChildNames().get(childPosition)));

        return convertView;

    }

    private void openShareshowNet(ChildViewHolder holder, String text) {
        SpannableString builder = new SpannableString(text);
        builder.setSpan(new UnderlineSpan(),9,29, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        final Intent userintent = new Intent();
        userintent.setClass(App.getApp(), LoadingHelpActivity.class);
        builder.setSpan(new IntentSpan(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Uri uri= Uri.parse("http://www.shareshow.com.cn");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                mContext.startActivity(intent);
            }
        }), 9, 29, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.child.setText(builder);
        holder.child.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition){
        return false;
    }

    public class GroupViewHolder{

        TextView group;
    }

    public class ChildViewHolder{

        TextView child;
    }


    public class IntentSpan extends ClickableSpan {

        private final View.OnClickListener listener;

        public IntentSpan(View.OnClickListener listener) {
            this.listener = listener;
        }

        @Override
        public void onClick(View widget) {
            // TODO Auto-generated method stub
            listener.onClick(widget);
        }
    }


}
