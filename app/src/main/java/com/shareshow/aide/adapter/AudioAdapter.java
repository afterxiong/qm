package com.shareshow.aide.adapter;


import android.app.FragmentManager;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.shareshow.App;
import com.shareshow.aide.R;
import com.shareshow.aide.dialog.DeleteDialog;
import com.shareshow.aide.impl.OnItemClickListener;
import com.shareshow.aide.impl.OnRecycleviewListener;
import com.shareshow.aide.retrofit.entity.TeamMorningData;
import com.shareshow.airpc.util.DebugLog;
import com.shareshow.db.GreenDaoManager;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.LogRecord;

/**
 * Created by xiongchengguang on 2017/11/12.
 */

public class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.AudioHolder> implements View.OnClickListener {
    private Context mContext;
    private List<TeamMorningData> morningData;
    private OnRecycleviewListener listener;
    private int position =-1;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private TeamMorningData playData;
    private int itemPosition;
    private boolean isRecord;



    public AudioAdapter(List<TeamMorningData> arr, Context context){
        this.morningData = arr;
        this.mContext = context;
    }

    @Override
    public AudioHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fragment_morning, parent, false);
        AudioHolder holder = new AudioHolder(view);
        holder.itemView.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(AudioHolder holder, int position){
        TeamMorningData data = morningData.get(position);
        holder.itemView.setTag(position);
        holder.audioName.setText(data.getUserName());
        holder.audioLength.setText(data.getDuration());
        holder.audioTime.setText(data.getTime());
        holder.setData(position);
        if(playData!=null&&morningData.get(position).getUuid().equals(playData.getUuid())){
            holder.setAdapterChange(1);
        }else{
            holder.setAdapterChange(0);
        }

    }

    public void notifyUpdate(RecyclerView recyclerView,List<TeamMorningData> datas){
        DebugLog.showLog(this,"notifyUpdate:"+recyclerView.isComputingLayout());
        morningData=datas;
        recyclerView.getRecycledViewPool().clear();
        if(recyclerView.isComputingLayout()){
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run(){
                    notifyDataSetChanged();
                }
            },1000);
        }else{
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount(){
        return morningData.size();
    }

    @Override
    public void onClick(View v){
        int position = (int) v.getTag();
        if(listener != null){
            if(position>=morningData.size()){
                return;
            }
            listener.onItemClick(position, morningData.get(position));
        }
    }


    public void setProgressVisible(int position){
        this.position = position;
        setItemPosition(position);
        notifyDataSetChanged();
    }

    public void setProgressInVisible(){
        this.position=-1;
        notifyDataSetChanged();
    }


    public void setPlayItem(TeamMorningData playMorningData) {
        this.playData = playMorningData;
    }

    public void setItemPosition(int position) {
        this.itemPosition = position;
    }

    public int getItemPosition(){

        return itemPosition;
    }

    public void setRecord(boolean b) {
        this.isRecord =b;
    }

    public class AudioHolder extends RecyclerView.ViewHolder{

        private TextView startTime;
        private TextView endTime;
        private ImageView play;
        private LinearLayout playItem;
        private TextView audioName;
        private TextView audioTime;
        private TextView audioLength;
        private ProgressBar progress;
        private SeekBar bar;
        private ImageView delete;
        private RelativeLayout audioItem;


        public AudioHolder(View itemView){
            super(itemView);
            audioItem =itemView.findViewById(R.id.audio_item);
            audioName = (TextView) itemView.findViewById(R.id.audio_name);
            audioTime = (TextView) itemView.findViewById(R.id.audio_time);
            audioLength = (TextView) itemView.findViewById(R.id.duration);
            progress = (ProgressBar) itemView.findViewById(R.id.progress);
            startTime = itemView.findViewById(R.id.startTime);
            endTime = itemView.findViewById(R.id.endTime);
            bar = itemView.findViewById(R.id.seekbar);
            play = itemView.findViewById(R.id.play);
            playItem =itemView.findViewById(R.id.play_item);
            delete =itemView.findViewById(R.id.play_delete);
            bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if(fromUser&&listener!=null){
                        listener.OnSeekBar(progress);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            play.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v){
                    if(playData==null){
                        DebugLog.showLog(this,"录音条目为null");
                        return;
                    }
                    if(isRecord){
                        Toast.makeText(App.getApp(),mContext.getResources().getString(R.string.morningFragment_recoding),Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(listener!=null){
                        if(!playData.getIsPlay()){
                            listener.OnPlay(playData,1);
                            playData.setIsPlay(true);
                            play.setImageResource(R.mipmap.audio_play);
                        }else{
                            listener.OnPlay(playData,0);
                            playData.setIsPlay(false);
                            play.setImageResource(R.mipmap.audio_pause);
                        }
                    }
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v){
                     if(playData==null){
                        DebugLog.showLog(this,"播放条目为null");
                        return;
                      }

                    for (TeamMorningData morningDatum : morningData){
                        if(morningDatum.getUuid().equals(playData.getUuid())){
                            morningDatum.clear();
                            break;
                        }
                     }
                    if(listener!=null){
                        listener.OnPlay(playData,2);
                    }
                      playData=null;
                      notifyDataSetChanged();
                }
            });

        }

        public void setData(int pos){
            if(position==pos){
                progress.setVisibility(View.VISIBLE);
                audioLength.setVisibility(View.GONE);
            }else{
                progress.setVisibility(View.GONE);
                audioLength.setVisibility(View.VISIBLE);
            }
        }


        public void setAdapterChange(int flag){
            if(playItem==null){
                DebugLog.showLog(this,"录音条目为null");
                return;
            }
            if(flag==0){
                audioItem.setVisibility(View.VISIBLE);
                playItem.setVisibility(View.GONE);
            }else{
                audioItem.setVisibility(View.GONE);
                playItem.setVisibility(View.VISIBLE);
                bar.setMax(playData.getMax());
                bar.setProgress(playData.getProgress());
                endTime.setText(secondToTime(playData.getMax()));
                startTime.setText(secondToTime(playData.getProgress()));
                if(!playData.getIsPlay()){
                    play.setImageResource(R.mipmap.audio_play);
                }else{
                    play.setImageResource(R.mipmap.audio_pause);
                }
            }
        }
    }

    public String secondToTime(int second){
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        String time = formatter.format(Integer.valueOf(second - TimeZone.getDefault().getRawOffset()));
        if (time.equals("23:59:59")){
            return "00:00:00";
        } else {
            return time;
        }
    }

    public void setOnItemClickListener(OnRecycleviewListener listener) {
          this.listener = listener;
    }

}
