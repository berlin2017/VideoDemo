package com.berlin.livedemo;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;


/**
 * Created by AnHuiNews on 2017/4/25.
 */

public class VideoLiveActivity extends AppCompatActivity implements View.OnClickListener {

    private VideoView videoView;
    private RecyclerView recyclerView;
    private String url = "http://v.cctv.com/flash/mp4video6/TMS/2011/01/05/cf752b1c12ce452b3040cab2f90bc265_h264818000nero_aac32-1.mp4";
    private List<String> list = new ArrayList<>();
    private MediaController mc;
    private GestureDetector mGestureDetector;
    //    private RelativeLayout video_layout;
    private ImageView start_btn;
    private ImageView back_btn;
    private SeekBar seekBar;
    private ImageView switch_btn;
    private FrameLayout layer;
    private TextView name;
    private ProgressBar loadingBar;
    private EditText editText;
    private MyAdapter myAdapter;
    private RelativeLayout commite_layout;
    private Button send_btn;
    private RelativeLayout option_layout;
    private FrameLayout danmu_layout;
    private FrameLayout root_layout;

    private int mCurrentProgress = 0;

    private AudioManager mAudioManager;
    //最大声音
    private int mMaxVolume;
    // 当前声音
    private int mVolume = -1;
    //当前亮度
    private float mBrightness = -1f;
    private int width_portrait = 0;
    private int height_portrait = 0;
    private int width_land = 0;
    private int height_land = 0;
    final Handler handler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //透明状态栏
//        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Window window = getWindow();
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
//                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(Color.TRANSPARENT);
//            window.setNavigationBarColor(Color.TRANSPARENT);
//        }

        if (getWindowManager().getDefaultDisplay().getHeight() < getWindowManager().getDefaultDisplay().getWidth()) {
            height_land = getWindowManager().getDefaultDisplay().getHeight();
            width_land = getWindowManager().getDefaultDisplay().getWidth();
            width_portrait = height_land;
            height_portrait = width_portrait * 3 / 4;
        } else {
            height_land = getWindowManager().getDefaultDisplay().getWidth();
            width_land = getWindowManager().getDefaultDisplay().getHeight();
            width_portrait = height_land;
            height_portrait = width_portrait * 3 / 4;
        }

        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        mMaxVolume = mAudioManager
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        setList();
        setContentView(R.layout.layout_video);
        initView();
        mGestureDetector = new GestureDetector(this, new MyGestureListener());

    }

    private Handler progressHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int progress = (int) (videoView.getCurrentPosition() * 100 / videoView.getDuration());
            if (seekBar != null && progress != mCurrentProgress) {
                seekBar.setProgress(progress);
                mCurrentProgress = progress;
            }
            sendEmptyMessage(0);
        }
    };

    private void initView() {
        root_layout = (FrameLayout) findViewById(R.id.video_root_layout);
        option_layout = (RelativeLayout) findViewById(R.id.video_layer_option_layout);
        danmu_layout = (FrameLayout) findViewById(R.id.video_layer_danmu);

        send_btn = (Button) findViewById(R.id.video_commit_send);
        send_btn.setOnClickListener(this);
        commite_layout = (RelativeLayout) findViewById(R.id.video_commite_layout);
        editText = (EditText) findViewById(R.id.video_commite_edit);
        loadingBar = (ProgressBar) findViewById(R.id.video_layer_loading);
        layer = (FrameLayout) findViewById(R.id.video_layer_layout);
        videoView = (VideoView) findViewById(R.id.videoview_video);
//        video_layout = (RelativeLayout) findViewById(R.id.layout_video);
        recyclerView = (RecyclerView) findViewById(R.id.listview_video);
        start_btn = (ImageView) findViewById(R.id.video_start_btn);
        back_btn = (ImageView) findViewById(R.id.video_back_btn);
        switch_btn = (ImageView) findViewById(R.id.video_switch_btn);
        seekBar = (SeekBar) findViewById(R.id.video_seekbar);
        name = (TextView) findViewById(R.id.video_name);
        name.setText(url);
        switch_btn.setOnClickListener(this);
        back_btn.setOnClickListener(this);
        start_btn.setOnClickListener(this);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                videoView.seekTo(videoView.getDuration() * progress / 100);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                videoView.seekTo(videoView.getDuration() * seekBar.getProgress() / 100);
            }
        });

        videoView.setVideoPath(url);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        myAdapter = new MyAdapter(list, this);
        recyclerView.setAdapter(myAdapter);
        recyclerView.scrollToPosition(list.size() - 1);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            height_land = getWindowManager().getDefaultDisplay().getHeight();
            width_land = getWindowManager().getDefaultDisplay().getWidth();
            width_portrait = height_land;
            height_portrait = height_land * 3 / 4;
            fullScreen();
//            Toast.makeText(this,"横屏",Toast.LENGTH_LONG).show();
        } else {
            normalScreen();
//            Toast.makeText(this,"竖屏",Toast.LENGTH_LONG).show();
        }

        videoView.start();
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                videoView.seekTo(0);
                videoView.start();
                seekBar.setProgress(0);
                Toast.makeText(VideoLiveActivity.this, "restart", Toast.LENGTH_SHORT).show();
            }
        });


        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                Toast.makeText(VideoLiveActivity.this, "准备播放", Toast.LENGTH_SHORT).show();
                progressHandler.sendEmptyMessage(0);
                videoView.start();
            }
        });
        videoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                switch (what) {
                    case MediaPlayer.MEDIA_INFO_BUFFERING_START://开始缓冲
                        loadingBar.setVisibility(View.VISIBLE);
                        break;
                    case MediaPlayer.MEDIA_INFO_BUFFERING_END://缓冲结束
                        loadingBar.setVisibility(View.GONE);
                        break;
                    case MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED://当前网速

                        break;
                }
                return false;
            }
        });
        editText.setImeOptions(EditorInfo.IME_ACTION_SEND);
        editText.setFocusableInTouchMode(true);
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    sendDanMu();
                }
                return false;
            }
        });
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (!hasFocus) {
                    im.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                } else {
                    im.showSoftInput(editText, 0);
                }
            }
        });
        root_layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                root_layout.setFocusable(true);
                root_layout.setFocusableInTouchMode(true);
                root_layout.requestFocus();
                InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                return false;
            }
        });
        hideLayerHander.sendEmptyMessageDelayed(0, 4000);
        danmuHandler.sendEmptyMessageDelayed(0,3000);
    }

    private Handler danmuHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            sendDanMu();
            sendEmptyMessageDelayed(0,3000);
        }
    };

    private void sendDanMu() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        //弹出一个弹幕
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int index = new Random().nextInt(list.size());
                DanMuTextView danMuTextView = new DanMuTextView(VideoLiveActivity.this, index % 2 == 0 ? true : false, index % 2 == 0 ? true : false);
                String text = list.get(index);
                danMuTextView.setText(text);
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    danmu_layout.addView(danMuTextView);
                }
                list.add(text);
                myAdapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(list.size() - 1);
            }
        };
        handler.postDelayed(runnable, 1000);
    }

    private Handler hideLayerHander = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (videoView.isPlaying()) {
                option_layout.setVisibility(View.GONE);
            }
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mGestureDetector.onTouchEvent(event))
            return true;

        // 处理手势结束
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                endGesture();
                break;
        }

        return super.onTouchEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            normalScreen();
            switch_btn.setImageResource(R.mipmap.ic_swith_small);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void setList() {
        for (int i = 0; i < 20; i++) {
            list.add("this is a message " + i + " !!!!");
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            fullScreen();
//            Toast.makeText(this,"横屏",Toast.LENGTH_LONG).show();
        } else {
            normalScreen();
//            Toast.makeText(this,"竖屏",Toast.LENGTH_LONG).show();
        }
        super.onConfigurationChanged(newConfig);
    }

    public void fullScreen() {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(lp);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        commite_layout.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        videoView.setVideoLayout(VideoView.VIDEO_LAYOUT_STRETCH, 0);
        ViewGroup.LayoutParams params = videoView.getLayoutParams();
        params.height = height_land;
        params.width = width_land;
        videoView.setLayoutParams(params);
        videoView.getHolder().setFixedSize(width_land, height_land);
//        ViewGroup.LayoutParams params2 = video_layout.getLayoutParams();
//        params2.height = height_land;
//        params2.width = width_land;
//        video_layout.setLayoutParams(params2);

        ViewGroup.LayoutParams params3 = layer.getLayoutParams();
        params3.height = height_land;
        params3.width = width_land;
        layer.setLayoutParams(params3);
    }

    public void normalScreen() {
        WindowManager.LayoutParams attr = getWindow().getAttributes();
        attr.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setAttributes(attr);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        commite_layout.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
        videoView.setVideoLayout(VideoView.VIDEO_LAYOUT_FIT_PARENT, 0);
        ViewGroup.LayoutParams params = videoView.getLayoutParams();
        params.height = height_portrait;
        params.width = width_portrait;
        videoView.setLayoutParams(params);
//        ViewGroup.LayoutParams params2 = video_layout.getLayoutParams();
//        params2.height = height_portrait;
//        params2.width = width_portrait;
//        video_layout.setLayoutParams(params2);
        videoView.getHolder().setFixedSize(width_portrait, height_portrait);

        ViewGroup.LayoutParams params3 = layer.getLayoutParams();
        params3.height = height_portrait;
        params3.width = width_portrait;
        layer.setLayoutParams(params3);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        videoView.stopPlayback();
        progressHandler.removeMessages(0);
        progressHandler = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.video_back_btn:
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    normalScreen();
//                    Toast.makeText(this,"竖屏",Toast.LENGTH_LONG).show();
                    switch_btn.setImageResource(R.mipmap.ic_swith_small);
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                } else {
                    finish();
                }
                break;
            case R.id.video_switch_btn:
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    fullScreen();
//                    Toast.makeText(this,"横屏",Toast.LENGTH_LONG).show();
                    switch_btn.setImageResource(R.mipmap.ic_switch_big);
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                } else {
                    normalScreen();
//                    Toast.makeText(this,"竖屏",Toast.LENGTH_LONG).show();
                    switch_btn.setImageResource(R.mipmap.ic_swith_small);
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
                break;

            case R.id.video_start_btn:
                playOrPause();
                hideLayerHander.sendEmptyMessageDelayed(0, 2000);
                break;
            case R.id.video_commit_send:
                sendDanMu();
                break;
        }
    }

    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {


        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (option_layout.getVisibility() == View.VISIBLE) {
                option_layout.setVisibility(View.GONE);

            } else {
                option_layout.setVisibility(View.VISIBLE);

                hideLayerHander.sendEmptyMessageDelayed(0, 2000);
            }
            return super.onSingleTapConfirmed(e);
        }

        /**
         * 双击
         */
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            option_layout.setVisibility(View.VISIBLE);
            hideLayerHander.sendEmptyMessageDelayed(0, 2000);
            playOrPause();
            return true;
        }

        /**
         * 滑动
         */
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            float mOldX = e1.getX(), mOldY = e1.getY();
            int y = (int) e2.getRawY();
            int x = (int) e2.getRawX();
            Display disp = getWindowManager().getDefaultDisplay();
            int windowWidth = disp.getWidth();
            int windowHeight = disp.getHeight();
            if (Math.abs(distanceX) >= Math.abs(distanceY)) {
                onSeekSlide((long) ((x - mOldX) / windowWidth * videoView.getDuration()));
            } else {
                if (mOldX > windowWidth * 4.0 / 5)// 右边滑动
                    onVolumeSlide((mOldY - y) / windowHeight);
                else if (mOldX < windowWidth / 5.0)// 左边滑动
                    onBrightnessSlide((mOldY - y) / windowHeight);

            }
            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    }

    private void playOrPause() {
        if (videoView != null)
            if (videoView.isPlaying()) {
                start_btn.setImageResource(R.mipmap.ic_pause);
                videoView.pause();
            } else {
                videoView.start();
                start_btn.setImageResource(R.mipmap.ic_start);
            }
    }

    private void onBrightnessSlide(float v) {

        if (mBrightness < 0) {
            mBrightness = getWindow().getAttributes().screenBrightness;
            if (mBrightness <= 0.00f)
                mBrightness = 0.50f;
            if (mBrightness < 0.01f)
                mBrightness = 0.01f;
        }


        WindowManager.LayoutParams lpa = getWindow().getAttributes();
        lpa.screenBrightness = mBrightness + v;
        if (lpa.screenBrightness > 1.0f)
            lpa.screenBrightness = 1.0f;
        else if (lpa.screenBrightness < 0.01f)
            lpa.screenBrightness = 0.01f;
        getWindow().setAttributes(lpa);

    }

    private void onVolumeSlide(float v) {
        if (mVolume == -1) {
            mVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            if (mVolume < 0)
                mVolume = 0;
        }

        int index = (int) (v * mMaxVolume) + mVolume;
        if (index > mMaxVolume)
            index = mMaxVolume;
        else if (index < 0)
            index = 0;
        // 变更声音
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);
    }

    private void onSeekSlide(long l) {
        videoView.seekTo(l + videoView.getCurrentPosition());
    }


    /**
     * 手势结束
     */
    private void endGesture() {
        mVolume = -1;
        mBrightness = -1f;
    }


}
