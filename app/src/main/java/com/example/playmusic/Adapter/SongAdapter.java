package com.example.playmusic.Adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.playmusic.Model.SongInfo;
import com.example.playmusic.R;

import java.util.ArrayList;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongHolder>{

    public static final String TAG = SongAdapter.class.getSimpleName();
    ArrayList<SongInfo> songList;
    Context context;

    int currentPos;

    private ButtonClickListener buttonClickListener;

    public SongAdapter(Context context, ArrayList<SongInfo> songList,ButtonClickListener buttonClickListener) {


        this.songList = songList;
        this.context = context;
        this.buttonClickListener=buttonClickListener;
    }

    @Override
    public long getItemId(int position) {

        return super.getItemId(position);
    }

    @Override
    public SongHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_songs,
                parent, false);
        return new SongAdapter.SongHolder(view);
    }

    @Override
    public void onBindViewHolder(final SongHolder holder, final int position) {

        SongInfo songInfo = songList.get(position);

        holder.textViewSongName.setText(songInfo.getSongName());
        holder.textViewSongArtist.setText(songInfo.getSongArtist());

        holder.imageViewPlaying.setVisibility(View.GONE);

        if(currentPos==position)
        {
            holder.imageViewPlaying.setVisibility(View.VISIBLE);
        }


    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    public class SongHolder extends RecyclerView.ViewHolder {

        TextView textViewSongName, textViewSongArtist;
        RelativeLayout relativeLayout;
        ImageView imageViewPlaying;


        public SongHolder(final View itemView) {
            super(itemView);

            textViewSongName = (TextView)itemView.findViewById(R.id.textViewSongName);
            textViewSongArtist = (TextView)itemView.findViewById(R.id.textViewSongArtist);
            imageViewPlaying = (ImageView)itemView.findViewById(R.id.imageViewPlaying);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.relativeLayoutSongs);
            relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    currentPos=getAdapterPosition();
                     SongInfo songInfo = songList.get(getAdapterPosition());
                     int itemcount = getItemCount();

                     String songUrl = songInfo.getSongUrl();
                    imageViewPlaying.setVisibility(View.VISIBLE);
                    buttonClickListener.onButtonClick(songUrl,getAdapterPosition(),itemcount);
                    notifyDataSetChanged();
                }
            });
        }


    }

    public interface ButtonClickListener{

        void onButtonClick(String songUrl,int position,int itemcount);

    }

}
