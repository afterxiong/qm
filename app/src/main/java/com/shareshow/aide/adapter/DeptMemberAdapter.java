package com.shareshow.aide.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.shareshow.aide.R;
import com.shareshow.aide.impl.OnUserCheckListener;
import com.shareshow.aide.retrofit.entity.DeptMember;
import com.shareshow.aide.util.CacheUserInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiongchengguang on 2018/3/20.
 */

public class DeptMemberAdapter extends RecyclerView.Adapter<DeptMemberAdapter.DeptMemberHolder> implements View.OnClickListener {
    private Context context;
    private List<DeptMember.DeptMemberInfo> deptMemberInfos;
    private OnUserCheckListener listener;

    public DeptMemberAdapter(Context mContext, List<DeptMember.DeptMemberInfo> deptMemberInfos) {
        this.context = mContext;
        this.deptMemberInfos = deptMemberInfos;
    }

    @Override
    public DeptMemberHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dept_member_info, parent, false);
        DeptMemberHolder holder = new DeptMemberHolder(view);
        holder.item_agree.setOnClickListener(this);
        holder.item_refuse.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(DeptMemberHolder holder, int position) {
        DeptMember.DeptMemberInfo member = deptMemberInfos.get(position);
        String name = member.getUserName();
        holder.item_name.setText(name);
        holder.item_agree.setTag(position);
        holder.item_refuse.setTag(position);

        if (member.getResponsible() == 1) {
            holder.item_icon.setVisibility(View.VISIBLE);
        } else {
            holder.item_icon.setVisibility(View.INVISIBLE);
        }

        if (CacheUserInfo.get().getResponsible() == 1) {
            if (member.getUrbrId() != null) {
                holder.item_refuse.setVisibility(View.VISIBLE);
                holder.item_agree.setVisibility(View.VISIBLE);
                holder.item_tip.setVisibility(View.VISIBLE);
            } else {
                holder.item_refuse.setVisibility(View.GONE);
                holder.item_agree.setVisibility(View.GONE);
                holder.item_tip.setVisibility(View.GONE);
            }
        } else {
            holder.item_refuse.setVisibility(View.GONE);
            holder.item_agree.setVisibility(View.GONE);
            holder.item_tip.setVisibility(View.GONE);
        }
    }


    @Override
    public int getItemCount() {
        if (CacheUserInfo.get().getResponsible() != 1) {
            List<DeptMember.DeptMemberInfo> cache = new ArrayList<>();
            for (DeptMember.DeptMemberInfo member : deptMemberInfos) {
                if (member.getUrbrId() == null) {
                    cache.add(member);
                }
            }
            deptMemberInfos.clear();
            deptMemberInfos.addAll(cache);
        }
        return deptMemberInfos.size();
    }

    @Override
    public void onClick(View v) {
        int position = (int) v.getTag();
        DeptMember.DeptMemberInfo deptMember = deptMemberInfos.get(position);
        String urbrId = deptMember.getUrbrId();
        if (v.getId() == R.id.item_agree) {
            if (listener != null && urbrId != null) {
                listener.agree(urbrId, position);
            }
        } else if (v.getId() == R.id.item_refuse) {
            if (listener != null && urbrId != null) {
                listener.refuse(urbrId, position);
            }
        }
    }

    public void setOnExamineListener(OnUserCheckListener listener) {
        this.listener = listener;
    }

    public class DeptMemberHolder extends RecyclerView.ViewHolder {
        private TextView item_name;
        private TextView item_agree;
        private TextView item_refuse;
        private TextView item_tip;
        private ImageView item_icon;

        public DeptMemberHolder(View itemView) {
            super(itemView);
            item_name = itemView.findViewById(R.id.item_name);
            item_tip = itemView.findViewById(R.id.item_tip);
            item_agree = itemView.findViewById(R.id.item_agree);
            item_refuse = itemView.findViewById(R.id.item_refuse);
            item_icon = itemView.findViewById(R.id.item_icon);
        }
    }
}
