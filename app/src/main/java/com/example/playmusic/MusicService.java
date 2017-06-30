package com.example.playmusic;


import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.Toast;



import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;


public class MusicService extends Service {

    SeekBar seekBar;
    public static final String TAG = MusicService.class.getSimpleName();
    //media player
    private MediaPlayer mediaPlayer = new MediaPlayer();
    //song list
    //private ArrayList<SongInfo> songList = new ArrayList<SongInfo>();
    //current position
    private int currentPosition;
    String songUrl;

    private IBinder musicBind = new MusicBinder();


    Thread t;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: inside ibinder");

        Toast.makeText(this, "Start Service", Toast.LENGTH_SHORT).show();


        return musicBind;
    }

    RemoteViews notificationView;
    Notification notification;
    @Override
    public void onCreate() {
        //  Log.d(TAG, "onCreate: musicservice"+currentPosition + songUrl);
        super.onCreate();

        t= new Thread();


        notificationView = new RemoteViews(getPackageName(),R.layout.notification);

        IntentFilter intentFilter =  new IntentFilter("Pause");
        Next next = new Next();
        registerReceiver(next,intentFilter);
        Intent intent = new Intent("Pause");
        PendingIntent pause = PendingIntent.getBroadcast(this, 0, intent, 0);


        IntentFilter intentFilter1 =  new IntentFilter("Next");
        registerReceiver(next,intentFilter1);
        Intent intentNext = new Intent("Next");
        PendingIntent nextBtn = PendingIntent.getBroadcast(this, 0, intentNext, 0);


        notificationView.setOnClickPendingIntent(R.id.imagePause,pause);
        notificationView.setOnClickPendingIntent(R.id.imageNext,nextBtn);

        notification = new NotificationCompat.Builder(this)
                .setContentTitle("Test")
                .setTicker("Test")
                .setContentText("Test")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContent(notificationView)
                .setOngoing(true).build();


        startForeground(101,
                notification);


    }

    @Override
    public boolean onUnbind(Intent intent) {

        Log.d(TAG, "onUnbind: ");
        //mediaPlayer.stop();
        // mediaPlayer.release();
        return false;
    }

    public void setSong(SeekBar seekBar,int position, String url) {
        System.out.println(url + "------------------------");
        Log.d(TAG, "setSong: song set" + position + url);
        if (url != null) {
            //  this.currentPosition = position;
            this.songUrl = url;

        }
        this.seekBar=seekBar;


    }

    public class MusicBinder extends Binder {
        public MusicService getService() {
            Log.d(TAG, "getService: return musicservice class to acitvity");
            return MusicService.this;
        }

    }

    public void playSong() {

        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
        }
        try {
            mediaPlayer.setDataSource(songUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaPlayer.prepareAsync();
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        do{

                            double cu = mediaPlayer.getCurrentPosition();
                            double du = mediaPlayer.getDuration();

                            double p = cu/du*100;


                            seekBar.setProgress((int)p);
                        }while(mediaPlayer.isPlaying());
                    }
                });
                t.start();

            }
        });



    }


    public void playPause(ImageView imageViewPause) {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            imageViewPause.setImageResource(R.drawable.ic_action_play);

        } else {
            mediaPlayer.start();
            imageViewPause.setImageResource(R.drawable.ic_action_pause);
        }
    }



    public  class Next extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {



           if(intent.getAction().equals("Next")) {

            }
            if(intent.getAction().equals("Pause"))
            { if (mediaPlayer.isPlaying())
            {
                mediaPlayer.pause();
                notificationView.setImageViewResource(R.id.imagePause,R.drawable.ic_action_play);
                notification = new NotificationCompat.Builder(getBaseContext())
                        .setContentTitle("Test")
                        .setTicker("Test")
                        .setContentText("Test")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContent(notificationView)
                        .setOngoing(true).build();


                startForeground(101,
                        notification);
            }
                else{
                    mediaPlayer.start();
                notificationView.setImageViewResource(R.id.imagePause,R.drawable.ic_action_pause);
                notification = new NotificationCompat.Builder(getBaseContext())
                        .setContentTitle("Test")
                        .setTicker("Test")
                        .setContentText("Test")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContent(notificationView)
                        .setOngoing(true).build();


                startForeground(101,
                        notification);}

            }

        }

    }







}
