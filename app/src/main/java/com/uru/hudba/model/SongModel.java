package com.uru.hudba.model;

import com.google.gson.annotations.SerializedName;

public class SongModel {
    @SerializedName("id")
    private String id;
    @SerializedName("title")
    private String title;
    @SerializedName("artist_name")
    private String artist_name;
    @SerializedName("videoId")
    private String videoId;
    @SerializedName("cover_image")
    private String cover_image;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setArtist_name(String artist_name) {
        this.artist_name = artist_name;
    }

    public String getArtist_name() {
        return artist_name;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getCover_image() {
        return cover_image;
    }

    public void setCover_image(String cover_image) {
        this.cover_image = cover_image;
    }
}
