package com.shareshow.aide.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.shareshow.aide.R;
import com.shareshow.aide.impl.OnItemClickListener;
import com.shareshow.aide.retrofit.entity.VisitData;
import com.shareshow.aide.retrofit.entity.VisitRecord;
import com.shareshow.db.GreenDaoManager;
import com.shareshow.db.ReadVisitTrackId;
import com.shareshow.db.ReadVisitTrackIdDao;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiongchengguang on 2017/12/27.
 */


public class VisitAdapter extends RecyclerView.Adapter<VisitAdapter.VisitHolder> implements View.OnClickListener {
    private List<VisitData> visitDataList=new ArrayList<>();
    private OnItemClickListener listener;
    private Context context;

    public VisitAdapter(List<VisitData> visitDataList, Context context) {
        this.visitDataList = visitDataList;
        this.context = context;
    }

    @Override
    public VisitHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_visit, parent, false);
        VisitHolder holder = new VisitHolder(view);
        view.setOnClickListener(this);
        return holder;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onBindViewHolder(VisitHolder holder, int position) {
        holder.itemView.setTag(position);
        VisitData datas = visitDataList.get(position);
        holder.visit_time.setText(datas.getVrTimestart());
        holder.user_name.setText(datas.getVrPlanid());
        List<ReadVisitTrackId> visitId = GreenDaoManager.getDaoSession().getReadVisitTrackIdDao().queryBuilder().where(ReadVisitTrackIdDao.Properties.VisitId.eq(datas.getVrId())).list();
        if (visitId.size() == 0) {
            holder.read_flag.setText("未读");
            holder.read_flag.setTextColor(Color.RED);
        } else {
            holder.read_flag.setText("已读");
            holder.read_flag.setTextColor(Color.GRAY);
        }
    }

    @Override
    public int getItemCount() {
        if (visitDataList == null) {
            return 0;
        } else {
            return visitDataList.size();
        }
    }

    @Override
    public void onClick(View v) {
        if (listener != null) {
            int position = (int) v.getTag();
            if (visitDataList.size() > position) {
                VisitData datas = visitDataList.get(position);
                listener.onItemClick(position, datas);
            }
        }
    }


    class VisitHolder extends RecyclerView.ViewHolder {
        public ImageView drawName;
        public TextView user_name;
        public TextView visit_time;
        public TextView read_flag;

        public VisitHolder(View itemView) {
            super(itemView);
            drawName = itemView.findViewById(R.id.draw_name);
            user_name = itemView.findViewById(R.id.user_name);
            visit_time = itemView.findViewById(R.id.visit_time);
            read_flag = itemView.findViewById(R.id.read_flag);
        }
    }
}
