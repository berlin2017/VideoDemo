package com.berlin.livedemo;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.net.URI;


/**
 * Created by AnHuiNews on 2017/5/8.
 */

public class ListVideoAdapter extends RecyclerView.Adapter<ListVideoAdapter.MyHolder> {

    private Context context;
    private String url = "http://v.cctv.com/flash/mp4video6/TMS/2011/01/05/cf752b1c12ce452b3040cab2f90bc265_h264818000nero_aac32-1.mp4";
    private int index = 0;
    ListVideoAdapter.MyHolder mHolder;
    private long mCurrentProgress = 0;

    public int getIndex() {
        return index;
    }

    public ListVideoAdapter(Context context) {
        this.context = context;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyHolder(LayoutInflater.from(context).inflate(R.layout.item_listvideo_layout, parent, false));
    }

    @Override
    public void onViewRecycled(MyHolder holder) {
        super.onViewRecycled(holder);
        holder.videoView.stopPlayback();
        if (holder.videoView != null) {
            holder.videoView.suspend();
            mHolder = null;
        }
        holder.seekBar.setEnabled(false);
        holder.play_img.setImageResource(R.mipmap.ic_pause);
        holder.back_img.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBindViewHolder(final MyHolder holder, final int position) {
        holder.name_tv.setText("this is title " + position + " !!!!");
        ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
        params.height = LinearLayout.LayoutParams.WRAP_CONTENT;

        holder.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                Toast.makeText(context, "准备播放", Toast.LENGTH_SHORT).show();
                progressHandler.sendEmptyMessage(0);
                holder.seekBar.setEnabled(true);
            }
        });
        holder.videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Toast.makeText(context.getApplicationContext(), "complete", Toast.LENGTH_SHORT).show();
                holder.videoView.seekTo(0);
                holder.seekBar.setProgress(0);

                holder.videoView.stopPlayback();
                if (holder.videoView != null) {
                    holder.videoView.suspend();
                    mHolder = null;
                }
                holder.seekBar.setEnabled(false);
                holder.play_img.setImageResource(R.mipmap.ic_pause);
                holder.back_img.setVisibility(View.VISIBLE);
            }
        });

        holder.videoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                switch (what) {
                    case io.vov.vitamio.MediaPlayer.MEDIA_INFO_BUFFERING_START://开始缓冲
                        holder.progressBar.setVisibility(View.VISIBLE);
                        break;
                    case io.vov.vitamio.MediaPlayer.MEDIA_INFO_BUFFERING_END://缓冲结束
                        holder.progressBar.setVisibility(View.GONE);
                        break;
                    case io.vov.vitamio.MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED://当前网速

                        break;
                }
                return false;
            }
        });

        holder.switch_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index = position;
                if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    ((ListVideoActivity) context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                } else {
                    if (!holder.videoView.isPlaying()) {
                        playOrPause(holder);
                    }
                    ((ListVideoActivity) context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
            }
        });
        holder.play_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index = position;
                playOrPause(holder);
            }
        });
    }

    Handler progressHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mHolder!=null){
                int progress = (int) (mHolder.videoView.getCurrentPosition() * 100 / mHolder.videoView.getDuration());
                if (mHolder.seekBar != null && mHolder.videoView.getCurrentPosition()-mCurrentProgress>=1000) {
                    mHolder.seekBar.setProgress(progress);
                    mCurrentProgress = progress;
                    mHolder.time.setText(TimeUtil.tansTime(mHolder.videoView.getCurrentPosition()));
                }
            }
            sendEmptyMessage(0);
        }
    };

    public void playOrPause(MyHolder holder) {
        if (mHolder == holder) {
            if (holder.videoView.isPlaying()) {
                holder.videoView.pause();
                holder.play_img.setImageResource(R.mipmap.ic_pause);
            } else {
                holder.videoView.start();
                holder.play_img.setImageResource(R.mipmap.ic_start);
                holder.back_img.setVisibility(View.GONE);
            }
        } else {
            if (mHolder != null && mHolder.videoView.isPlaying()) {
                mHolder.videoView.stopPlayback();
                if (mHolder.videoView != null) {
                    mHolder.videoView.suspend();
                }
                mHolder.seekBar.setEnabled(false);
                mHolder.time.setText("00:00");
                mHolder.play_img.setImageResource(R.mipmap.ic_pause);
                mHolder.back_img.setVisibility(View.VISIBLE);
                mHolder = null;
            }
            if (holder.videoView.isPlaying()) {
                holder.videoView.pause();
                holder.play_img.setImageResource(R.mipmap.ic_pause);
            } else {
                mHolder = holder;
                holder.videoView.setVideoURI(Uri.parse(url));
                holder.videoView.start();
                holder.play_img.setImageResource(R.mipmap.ic_start);
                holder.back_img.setVisibility(View.GONE);
            }
        }
    }


    @Override
    public int getItemCount() {
        return 20;
    }

    public class MyHolder extends RecyclerView.ViewHolder {

        VideoView videoView;
        ImageView back_img;
        ImageView play_img;
        FrameLayout frameLayout;
        ImageView switch_img;
        TextView name_tv;
        ProgressBar progressBar;
        SeekBar seekBar;
        RelativeLayout layer;
        TextView time;

        public MyHolder(View itemView) {
            super(itemView);
            videoView = (VideoView) itemView.findViewById(R.id.item_videolist_videoview);
            back_img = (ImageView) itemView.findViewById(R.id.item_listvideo_img);
            play_img = (ImageView) itemView.findViewById(R.id.item_listvideo_play);
            frameLayout = (FrameLayout) itemView.findViewById(R.id.item_listvideo_framelayout);
            switch_img = (ImageView) itemView.findViewById(R.id.item_listvideo_switch);
            name_tv = (TextView) itemView.findViewById(R.id.item_listvideo_name);
            progressBar = (ProgressBar) itemView.findViewById(R.id.item_listvideo_loading);
            seekBar = (SeekBar) itemView.findViewById(R.id.item_listvideo_seekbar);
            layer = (RelativeLayout) itemView.findViewById(R.id.item_listvideo_layer);
            time = (TextView) itemView.findViewById(R.id.item_listvideo_time);
            frameLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showOrHideLayer();
                }
            });
            seekBar.setEnabled(false);
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    videoView.seekTo(videoView.getDuration() * seekBar.getProgress() / 100);
                    time.setText(TimeUtil.tansTime(videoView.getCurrentPosition()));
                }
            });
            normalScreen();
        }

        public void fullScreen() {
            WindowManager.LayoutParams lp = ((ListVideoActivity) context).getWindow().getAttributes();
            lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            ((ListVideoActivity) context).getWindow().setAttributes(lp);
            ((ListVideoActivity) context).getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            name_tv.setVisibility(View.GONE);
            itemView.getLayoutParams().height = LinearLayout.LayoutParams.MATCH_PARENT;
            itemView.getLayoutParams().width = LinearLayout.LayoutParams.MATCH_PARENT;

            ViewGroup.LayoutParams params = frameLayout.getLayoutParams();
            params.width = ((ListVideoActivity) context).getWindowManager().getDefaultDisplay().getWidth();
            params.height = ((ListVideoActivity) context).getWindowManager().getDefaultDisplay().getHeight();
            frameLayout.setLayoutParams(params);

            RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            params2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            params2.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            params2.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            params2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            videoView.setLayoutParams(params2);
        }

        public void normalScreen() {
            WindowManager.LayoutParams attr = ((ListVideoActivity) context).getWindow().getAttributes();
            attr.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            ((ListVideoActivity) context).getWindow().setAttributes(attr);
            ((ListVideoActivity) context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            name_tv.setVisibility(View.VISIBLE);
            itemView.getLayoutParams().height = LinearLayout.LayoutParams.WRAP_CONTENT;
            itemView.getLayoutParams().width = LinearLayout.LayoutParams.MATCH_PARENT;

            ViewGroup.LayoutParams params = frameLayout.getLayoutParams();
            params.width = ((ListVideoActivity) context).getWindowManager().getDefaultDisplay().getWidth();
            params.height = params.width / 4 * 3;
            frameLayout.setLayoutParams(params);

            RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            params2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            params2.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            params2.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            params2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            videoView.setLayoutParams(params2);

        }

        public void showOrHideLayer() {
            if (!videoView.isPlaying()){
                return;
            }
            if (layer.getVisibility() == View.VISIBLE) {
                layer.setVisibility(View.GONE);
            } else {
                layer.setVisibility(View.VISIBLE);
            }
        }
    }

    public int getCurrentPosition() {
        return index;
    }

}
