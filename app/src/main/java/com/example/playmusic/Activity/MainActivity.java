package com.example.playmusic.Activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.playmusic.Adapter.SongAdapter;
import com.example.playmusic.Model.SongInfo;
import com.example.playmusic.MusicService;
import com.example.playmusic.MusicService.MusicBinder;
import com.example.playmusic.R;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SongAdapter.ButtonClickListener {


    private static final String TAG = MainActivity.class.getSimpleName();
    private ArrayList<SongInfo> songList = new ArrayList<SongInfo>();
    private LinearLayoutManager linearLayoutManager;


    private Toolbar toolbar;
    RecyclerView recyclerView;
    ImageView imageViewPause, imageViewNext, imageViewPrevious;
    SongAdapter songAdapter;
    SongAdapter.ButtonClickListener buttonClickListener;

    SeekBar seekBar;
    BottomSheetBehavior bottomSheetBehavior;

    MediaPlayer mediaPlayer = new MediaPlayer();
    String songPath;
    int currentPosition = 0, position;

    private MusicService musicService;
    private Intent playIntent;
    private boolean musicBound = false;


    public void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolBarMain);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        toolbar.setTitleTextColor(Color.WHITE);
    }

    private void playBackInit() {

        imageViewPause = (ImageView) findViewById(R.id.imagePause);
        imageViewNext = (ImageView) findViewById(R.id.imageNext);
        imageViewPrevious = (ImageView) findViewById(R.id.imagePrevious);
        seekBar=(SeekBar)findViewById(R.id.seekBar);

    }

    private void initView() {

        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewMusic);
        playBackInit();
        View bottomSheetView = findViewById(R.id.bottomsheetView);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetView);

        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        buttonClickListener = this;


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        Toast.makeText(MainActivity.this, "Service Un-Binded", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        setToolbar();
        loadSongs();


        playIntent = new Intent(this, MusicService.class);
//            Boolean bind = bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
        bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
        startService(playIntent);


        imageViewNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPosition += 1;

                musicService.setSong(seekBar,position, songList.get(currentPosition).getSongUrl());
                musicService.playSong();

            }

        });


        imageViewPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                System.out.println("lsdjfslfkjsdf");
                currentPosition -= 1;
                if (currentPosition < 0) {
                    currentPosition = songList.size() - 1;
                }


                musicService.setSong(seekBar,position, songList.get(currentPosition).getSongUrl());
                musicService.playSong();
            }

        });


        imageViewPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                musicService.playPause(imageViewPause);

            }
        });

    }


    //connect to service






    private void loadSongs() {

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC;  //+"!=0";
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                    String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                    String url = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));

                    SongInfo songInfo = new SongInfo(name, artist, url);
                    songList.add(songInfo);

                } while (cursor.moveToNext());
            }

            cursor.close();
            songAdapter = new SongAdapter(MainActivity.this, songList, buttonClickListener);
            recyclerView.setAdapter(songAdapter);

        }
    }

    @Override
    public void onButtonClick(String songUrl, int pos, int itemcount) {

        //playSong(songUrl,pos,itemcount);
        Log.d(TAG, "onButtonClick: inside interface method");
        this.songPath = songUrl;
        this.currentPosition = pos;

        musicService.setSong(seekBar,currentPosition, songUrl);
        musicService.playSong();

        Log.d(TAG, "onButtonClick: interface" + pos);

    }


    private ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            Log.d(TAG, "onServiceConnected: inside onServiceConnected");
            MusicBinder musicBinder = (MusicBinder) service;
            //get service
            musicService = musicBinder.getService();
            //pass list

//            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

            musicBound = true;


        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected: ");
            musicBound = false;

        }
    };



}
