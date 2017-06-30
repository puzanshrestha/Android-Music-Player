package com.example.playmusic.Model;


public class SongInfo {

    public String songName , songArtist , songUrl;

    public SongInfo(String songName, String songArtist , String songUrl) {
        this.songName = songName;
        this.songArtist = songArtist;
        this.songUrl = songUrl;
    }

    public void setSongUrl(String songUrl) {
        this.songUrl = songUrl;
    }

    public String getSongUrl() {
        return songUrl;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getSongArtist() {
        return songArtist;
    }

    public void setSongArtist(String songArtist) {
        this.songArtist = songArtist;
    }
}
