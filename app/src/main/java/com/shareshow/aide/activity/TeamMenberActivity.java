package com.shareshow.aide.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.shareshow.App;
import com.shareshow.aide.R;
import com.shareshow.aide.dialog.LoadingProgress;
import com.shareshow.aide.dialog.QRCodeDialog;
import com.shareshow.aide.dialog.ShowTeamWindow;
import com.shareshow.aide.event.MessageEvent;
import com.shareshow.aide.mvp.presenter.TeamMemberPresenter;
import com.shareshow.aide.mvp.view.TeamMemberView;
import com.shareshow.aide.retrofit.entity.Team;
import com.shareshow.aide.retrofit.entity.UserInfo;
import com.shareshow.aide.util.CacheUserInfo;
import com.shareshow.aide.util.Fixed;
import com.shareshow.airpc.widget.TeamDeletePopupWindow;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 团队成员
 * Created by xiongchengguang on 2018/1/26.
 */

public class TeamMenberActivity extends BaseActivity<TeamMemberView, TeamMemberPresenter> implements TeamMemberView, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.swipe_refresh)
    public SwipeRefreshLayout swipe_refresh;
    @BindView(R.id.toolbar)
    public Toolbar toolbar;
    @BindView(R.id.recycler)
    public RecyclerView recycler;
    @BindView(R.id.title)
    public TextView title;
    @BindView(R.id.team_show_window)
    public ImageView team_show_window;
    private List<Team.TeamMember> teamMemberList = new ArrayList<>();
    private TeamAdapter adapter;
    private LinearLayoutManager manager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_member_info);
        ButterKnife.bind(this);
        initToolbar();
        EventBus.getDefault().register(this);
        initTile();
    }

    public void initToolbar() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        swipe_refresh.post(new Runnable() {
            @Override
            public void run() {
                swipe_refresh.setRefreshing(true);
                presenter.getTeamMemberList();
            }
        });

    }

    public void initTile() {
        String teamName = getIntent().getStringExtra("teamName");
        if (teamName != null) {
            title.setText(teamName);
        }
        manager = new LinearLayoutManager(this);
        recycler.setLayoutManager(manager);
        adapter = new TeamAdapter(this);
        recycler.setAdapter(adapter);

        swipe_refresh.setProgressBackgroundColorSchemeResource(android.R.color.white);
        swipe_refresh.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryDark);
        swipe_refresh.setOnRefreshListener(this);
    }


    @Override
    public void onRefresh() {
        swipe_refresh.setRefreshing(true);
        presenter.getTeamMemberList();
    }

    @Override
    public TeamMemberPresenter createPresenter() {
        return new TeamMemberPresenter(this);
    }

    @Override
    public Toolbar getToolbar() {
        return toolbar;
    }

    @OnClick(R.id.team_show_window)
    public void showTeamWindow() {
        ShowTeamWindow window = new ShowTeamWindow(getFragmentManager(), this);
        window.showAsDropDown(team_show_window);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if (event.getSign() == MessageEvent.SHOW_TEAM_QR_CODE) {
            UserInfo userInfo = CacheUserInfo.get().getUserInfo();
            QRCodeDialog dialog = new QRCodeDialog();
            Bundle bundle = new Bundle();
            bundle.putParcelable(QRCodeDialog.USER_INFO, userInfo);
            bundle.putInt(QRCodeDialog.TAG_TYPE, Fixed.TAG_ADD_TEAM);
            dialog.setArguments(bundle);
            dialog.show(getFragmentManager(), "dialog");
        } else if (event.getSign() == MessageEvent.EVENT_DISMISS_TEAM) {
            presenter.getDismiisTeam();
        } else if (event.getSign() == MessageEvent.EVENT_EXIT_TEAM) {
            presenter.getExitTeam();
        } else if (event.getSign() == MessageEvent.EVENT_DELETE_TEAM_FRIEND) {
            String exitUserId = event.getString();
            presenter.getRemoveTeam(exitUserId);
        }
    }

    @Override
    public void onViewTeamMenberList(Team team) {
        swipe_refresh.setRefreshing(false);
        List<Team.TeamMember> arr = team.getDatas();
        teamMemberList.clear();
        teamMemberList.addAll(arr);
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onViewError(int tag) {
        swipe_refresh.setRefreshing(false);
    }


    class TeamAdapter extends RecyclerView.Adapter<TeamAdapter.TeamHolder> implements View.OnClickListener {
        private Context mContext;

        public TeamAdapter(Context context) {
            this.mContext = context;
        }

        @Override
        public TeamHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_team_menber_info, parent, false);
            TeamHolder holder = new TeamHolder(view);
            holder.item_morning_record.setOnClickListener(this);
            holder.item_visit_record.setOnClickListener(this);
            return holder;
        }

        @Override
        public void onBindViewHolder(TeamHolder holder, int position) {
            Team.TeamMember member=teamMemberList.get(position);
            String name = member.getName();
            holder.item_team.setText(name);
            holder.item_visit_record.setTag(position);
            holder.item_morning_record.setTag(position);
            if (position == 0) {
                holder.item_icon.setVisibility(View.VISIBLE);
            } else {
                holder.item_icon.setVisibility(View.INVISIBLE);
            }


            if(CacheUserInfo.get().getUserTeanCreate()){
                holder.item_morning_record.setVisibility(View.VISIBLE);
                holder.item_visit_record.setVisibility(View.VISIBLE);
            }else{
                if(member.getUserId().equals(CacheUserInfo.get().getUserId())){
                    holder.item_morning_record.setVisibility(View.VISIBLE);
                    holder.item_visit_record.setVisibility(View.VISIBLE);
                }else{
                    holder.item_morning_record.setVisibility(View.GONE);
                    holder.item_visit_record.setVisibility(View.GONE);
                }
            }
            String userId = CacheUserInfo.get().getUserId();
            String teamCreateId = CacheUserInfo.get().getTeamCreaterId();
            if (userId.equals(teamCreateId)) {
                String deleteTeamId = teamMemberList.get(position).getUserId();
                if (userId.equals(deleteTeamId)) {
                    return;
                }
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        deleteTeamUser(deleteTeamId, v);
                        return true;
                    }
                });
            }
        }


        private void deleteTeamUser(String deleteTeamId, View v) {
            TeamDeletePopupWindow popupWindow = new TeamDeletePopupWindow(mContext, deleteTeamId);
            popupWindow.showAsDropDown(v, v.getWidth() * 1 / 2, 0);
        }

        @Override
        public int getItemCount() {
            return teamMemberList.size();
        }

        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();
            Team.TeamMember teamMember = teamMemberList.get(position);
            String userId = teamMember.getUserId();
            String userName=teamMember.getName();
            if (v.getId() == R.id.item_morning_record) {
                Intent intent = new Intent(TeamMenberActivity.this,PersonAudioActivity.class);
                intent.putExtra(Fixed.USER_ID, userId);
                intent.putExtra(Fixed.USER_NAME, userName);
                intent.putExtra(Fixed.USER_PHONE, teamMember.getPhone());
                startActivity(intent);
            } else if (v.getId() == R.id.item_visit_record) {
                Intent intent = new Intent(TeamMenberActivity.this, VisitRecordFilterActivity.class);
                intent.putExtra(Fixed.USER_ID, userId);
                intent.putExtra(Fixed.USER_NAME, userName);
                startActivity(intent);
            }
        }

        public class TeamHolder extends RecyclerView.ViewHolder {
            private TextView item_team;
            private TextView item_morning_record;
            private TextView item_visit_record;
            private ImageView item_icon;

            public TeamHolder(View itemView) {
                super(itemView);
                item_team = itemView.findViewById(R.id.item_team);
                item_morning_record = itemView.findViewById(R.id.item_morning_record);
                item_visit_record = itemView.findViewById(R.id.item_visit_record);
                item_icon = itemView.findViewById(R.id.item_icon);
            }
        }
    }

    public int px2dp(int val) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, val, getResources().getDisplayMetrics());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}
