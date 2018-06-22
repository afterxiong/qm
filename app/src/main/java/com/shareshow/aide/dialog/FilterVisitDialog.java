package com.shareshow.aide.dialog;

import android.app.FragmentManager;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.shareshow.aide.R;
import com.shareshow.aide.retrofit.Api;
import com.shareshow.aide.retrofit.RetrofitProvider;
import com.shareshow.aide.retrofit.entity.Team;
import com.shareshow.aide.util.CacheUserInfo;
import com.shareshow.airpc.util.DebugLog;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

/**
 * 过滤拜访记录
 * Created by xiongchengguang on 2018/1/31.
 */

public class FilterVisitDialog extends BaseDialog implements View.OnClickListener {

    private TextView cancel;
    private TextView agree;
    private RecyclerView recycler;
    private LinearLayoutManager manager;
    private FilterVisitAdapter adapter;
    private Api api;
    private List<Team.TeamMember> teamMemberList = new ArrayList<>();
    private Context context;
    private Set<String> selectUserId = new HashSet<>();
    private Set<String> defaultUserIds = new HashSet<>();

    @Override
    public View layout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fliter_visit, container, false);
        initView(view);
        return view;
    }

    private void initView(View view){
        if (api == null) {
            Retrofit retrofit = RetrofitProvider.get();
            api = retrofit.create(Api.class);
        }
        this.getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = this.getDialog().getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        // lp.windowAnimations = R.style.BottomDialog;
        window.setAttributes(lp);
        window.setBackgroundDrawable(new ColorDrawable());
        view.findViewById(R.id.cancel).setOnClickListener(this);
        view.findViewById(R.id.agree).setOnClickListener(this);

        cancel = view.findViewById(R.id.cancel);
        agree = view.findViewById(R.id.agree);
        adapter = new FilterVisitAdapter();
        recycler = view.findViewById(R.id.recycler);
        cancel.setOnClickListener(this);
        agree.setOnClickListener(this);
        manager = new LinearLayoutManager(context);
        recycler.setLayoutManager(manager);
        recycler.setAdapter(adapter);
        getTeamMember();
    }

    public void setData(List<Team.TeamMember> members){
         if(members!=null){
            teamMemberList.clear();
            teamMemberList.addAll(members);
            if(adapter!=null){
                adapter.notifyDataSetChanged();
            }
         }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.cancel) {
            this.dismiss();
        } else if (v.getId() == R.id.agree) {
            this.dismiss();
            if (listener != null) {
                listener.select(selectUserId, teamMemberList.size() == selectUserId.size());
            }
        }
    }

    @Override
    public void show(FragmentManager manager, String tag){
        super.show(manager, tag);
        selectUserId.clear();
        defaultUserIds.clear();
    }

    public interface OnSelectListener {

        public void select(Set<String> selectUserId, boolean checkAll);
    }

    private OnSelectListener listener;

    public void setOnSelectListener(Set<String> defaultUserIds, OnSelectListener listener) {
        this.listener = listener;
        this.defaultUserIds = defaultUserIds;
    }

    public void getTeamMember(){
        String teamId = CacheUserInfo.get().getTeamId();
        api.teamMember(teamId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Team>(){
                    @Override
                    public void accept(Team team) throws Exception {
                        if (team.getReturnCode() == 1){
                            List<Team.TeamMember> arr = team.getDatas();
                            teamMemberList.clear();
                            teamMemberList.addAll(arr);
                            adapter.notifyDataSetChanged();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }


    class FilterVisitAdapter extends RecyclerView.Adapter<FilterVisitAdapter.FilterHolder> {

        @Override
        public FilterHolder onCreateViewHolder(ViewGroup parent, int viewType){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_fliter_visit_item, parent, false);
            FilterHolder holder = new FilterHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(FilterHolder holder, int position) {
            Team.TeamMember teamMember = teamMemberList.get(position);
            holder.user_name.setText(teamMember.getName());
            if (defaultUserIds.contains(teamMember.getUserId())){
                holder.check.setChecked(true);
                selectUserId.add(teamMember.getUserId());
            } else {
                holder.check.setChecked(false);
            }
            holder.check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked){
                        selectUserId.add(teamMember.getUserId());
                    } else {
                        selectUserId.remove(teamMember.getUserId());
                    }
                }
            });
        }

        @Override
        public int getItemCount(){
            return teamMemberList.size();
        }

        class FilterHolder extends RecyclerView.ViewHolder {
            private TextView user_name;
            private CheckBox check;

            public FilterHolder(View itemView) {
                super(itemView);
                check = itemView.findViewById(R.id.check);
                user_name = itemView.findViewById(R.id.user_name);
            }
        }
    }
}
