package com.berlin.livedemo;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by AnHuiNews on 2017/5/8.
 */

public class ListVideoActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ListVideoAdapter adapter;
    private int mCurrentPosition = 0;
    private boolean canScroll = true;
    private int scrollY = 0;
    private LinearLayoutManager lm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_list_video);
        recyclerView = (RecyclerView) findViewById(R.id.list_video_listview);
        lm = new LinearLayoutManager(this){
            @Override
            public boolean canScrollVertically() {
                return canScroll&&super.canScrollVertically();
            }
        };
        recyclerView.setLayoutManager(lm);
        adapter = new ListVideoAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (adapter.getIndex()<lm.findFirstVisibleItemPosition()||adapter.getIndex()>lm.findLastVisibleItemPosition()){
                    if (adapter.mHolder!=null){
                        adapter.mHolder.videoView.stopPlayback();
                        adapter.mHolder.play_img.setImageResource(R.mipmap.ic_pause);
                        adapter.mHolder.back_img.setVisibility(View.VISIBLE);
                        adapter.mHolder = null;
                    }
                }
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mCurrentPosition = adapter.getIndex();
            recyclerView.scrollToPosition(mCurrentPosition);
            if (adapter.mHolder!=null){
                adapter.mHolder.fullScreen();
                canScroll = false;
            }
        } else {
            if (adapter.mHolder!=null){
                adapter.mHolder.normalScreen();
                canScroll = true;
            }
        }
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adapter.mHolder!=null){
            adapter.mHolder.videoView.stopPlayback();
            adapter.mHolder.play_img.setImageResource(R.mipmap.ic_pause);
            adapter.mHolder.back_img.setVisibility(View.VISIBLE);
            adapter.mHolder = null;
            if (adapter.progressHandler!=null){
                adapter.progressHandler.removeMessages(0);
                adapter.progressHandler = null;
            }
        }
    }
}
