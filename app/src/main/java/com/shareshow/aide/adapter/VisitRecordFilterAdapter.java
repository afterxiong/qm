package com.shareshow.aide.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shareshow.aide.R;
import com.shareshow.aide.impl.OnItemClickListener;
import com.shareshow.aide.retrofit.entity.VisitData;
import com.shareshow.aide.retrofit.entity.VisitRecord;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by xiongchengguang on 2018/3/16.
 */

public class VisitRecordFilterAdapter extends RecyclerView.Adapter<VisitRecordFilterAdapter.VisitRecordHolder> implements View.OnClickListener {
    private List<VisitData> visitDataList;
    private Context context;
    private OnItemClickListener listener;

    public VisitRecordFilterAdapter(List<VisitData> visitDataList, Context context) {
        this.visitDataList = visitDataList;
        this.context = context;
    }

    @Override
    public VisitRecordHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_visit_record_filter, parent, false);
        VisitRecordHolder holder = new VisitRecordHolder(view);
        holder.itemView.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(VisitRecordHolder holder, int position) {
        holder.itemView.setTag(position);
        VisitData visitData = visitDataList.get(position);
        String visitName = visitData.getVrGuestname();
        String visitDate = visitData.getVrDate();
        holder.date.setText(visitDate);
        holder.visitName.setText(String.format(context.getResources().getString(R.string.visit_name), visitName));
    }

    @Override
    public int getItemCount() {
        return visitDataList.size();
    }

    @Override
    public void onClick(View v) {
        int position = (int) v.getTag();
        if (listener != null) {
            VisitData visitData = visitDataList.get(position);
            listener.onItemClick(position, visitData);
        }

    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public class VisitRecordHolder extends RecyclerView.ViewHolder {
        private TextView date;
        private TextView visitName;

        public VisitRecordHolder(View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.item_date);
            visitName = itemView.findViewById(R.id.item_visit_name);
        }
    }
}
