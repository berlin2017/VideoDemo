package com.berlin.livedemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.VideoView;

/**
 * Created by AnHuiNews on 2017/4/25.
 */

public class TextLiveActivity  extends AppCompatActivity{
    private VideoView videoView;
    private RecyclerView recyclerView;
    private MyAdapter myAdapter;
    private String url = "http://v.cctv.com/flash/mp4video6/TMS/2011/01/05/cf752b1c12ce452b3040cab2f90bc265_h264818000nero_aac32-1.mp4";
    private List<String> list = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_test);
        setList();
        videoView = (VideoView) findViewById(R.id.videoview_video1);
        recyclerView = (RecyclerView) findViewById(R.id.listview_video1);
        videoView.setVideoPath(url);
        recyclerView.setLayoutManager(new MyLinearLayoutManager(this));
        myAdapter = new MyAdapter(list, this);
        recyclerView.setAdapter(myAdapter);


        videoView.start();

        recyclerView.scrollToPosition(list.size() - 1);
    }

    public void setList() {
        for (int i = 0; i < 20; i++) {
            list.add("this is a message " + i + " !!!!");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        videoView.stopPlayback();
    }
}
