package com.shareshow.aide.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.shareshow.aide.R;
import com.shareshow.aide.retrofit.entity.OrgAndDept;

import java.util.List;

/**
 * Created by xiongchengguang on 2017/12/18.
 */

public class DeptChildAdapter extends BaseAdapter {

    private List<?> arr;

    public DeptChildAdapter(List<?> arr) {
        this.arr = arr;
    }

    @Override
    public int getCount() {
        if (arr == null) {
            return 0;
        }
        return arr.size();
    }

    @Override
    public Object getItem(int position) {
        return arr.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolde holde;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.itme_org_and_dept, parent, false);
            holde = new ViewHolde();
            holde.tvOrgDept = convertView.findViewById(R.id.tv_org_dept);
            convertView.setTag(holde);
        } else {
            holde = (ViewHolde) convertView.getTag();
        }

        Object obj=arr.get(position);
        if(obj instanceof String){
            holde.tvOrgDept.setText((String) obj);
        }else if(obj instanceof OrgAndDept){
            OrgAndDept orgAndDept= (OrgAndDept) obj;
            holde.tvOrgDept.setText(orgAndDept.getGiGroupname());
        }
        return convertView;
    }

    static class ViewHolde {
        public TextView tvOrgDept;
    }

//
//    @Override
//    public OrgHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itme_org_and_dept, parent, false);
//        OrgHolder holder = new OrgHolder(view);
//        return holder;
//    }
//
//    @Override
//    public void onBindViewHolder(OrgHolder holder, int position) {
//        holder.tvOrgDept.setText(arr.get(position).getGiGroupname());
//    }
//
//    @Override
//    public int getItemCount() {
//        if (arr == null) {
//            return 0;
//        }
//        return arr.size();
//    }
//
//    class OrgHolder extends RecyclerView.ViewHolder {
//        private TextView tvOrgDept;
//
//        public OrgHolder(View itemView) {
//            super(itemView);
//            tvOrgDept = itemView.findViewById(R.id.tv_org_dept);
//        }
//    }
}
