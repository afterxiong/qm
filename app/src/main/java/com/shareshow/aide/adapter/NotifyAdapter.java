package com.shareshow.aide.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.shareshow.aide.R;
import com.shareshow.aide.impl.OnItemClickListener;
import com.shareshow.aide.retrofit.entity.AmplyNotify;

import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by TEST on 2017/12/27.
 */

public class NotifyAdapter extends RecyclerView.Adapter<NotifyAdapter.NotifyHolder> implements View.OnClickListener {

    private List<AmplyNotify> amplyNotifyList;

    public NotifyAdapter(List<AmplyNotify> arr) {
        this.amplyNotifyList = arr;
    }

    @Override
    public NotifyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notifycation, parent, false);
        NotifyHolder holder = new NotifyHolder(view);
        view.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(NotifyHolder holder, int position) {
        holder.itemView.setTag(position);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
        AmplyNotify notify = amplyNotifyList.get(position);
        String time = notify.getNosCreatetime().trim();
        String del = notify.getNosDel();
        String date= dateFormat.format(Long.parseLong(time));
        if (del.equals("0")) {
            holder.icon.setImageResource(R.mipmap.notifcation_icon_unread);
        } else {
            holder.icon.setImageResource(R.mipmap.notifcation_icon);
        }
        holder.title.setText(notify.getNosTitle());
        holder.content.setText(notify.getNosContent().replaceAll(" ",""));
        holder.date.setText(date);

    }

    @Override
    public int getItemCount() {
        return amplyNotifyList == null ? 0 : amplyNotifyList.size();
    }

    @Override
    public void onClick(View v) {
        if (listener != null) {
            int position = (int) v.getTag();
            listener.onItemClick(position, amplyNotifyList.get(position));
        }
    }

    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public static class NotifyHolder extends RecyclerView.ViewHolder {

        public ImageView icon;

        public TextView title;

        public TextView content;

        public TextView date;

        public NotifyHolder(View itemView) {
            super(itemView);
            icon= itemView.findViewById(R.id.notifcation_icon);
            title= itemView.findViewById(R.id.notifcation_title);
            content= itemView.findViewById(R.id.notifcation_content);
            date= itemView.findViewById(R.id.notifcation_date);
        }
    }
}
