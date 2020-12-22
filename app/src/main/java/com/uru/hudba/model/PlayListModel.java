package com.uru.hudba.model;

import com.google.gson.annotations.SerializedName;

public class PlayListModel {
    @SerializedName("id")
    private String id;
    @SerializedName("title")
    private String title;
    @SerializedName("sub_title")
    private String sub_title;
    @SerializedName("song_cnt")
    private String song_cnt;
    @SerializedName("image_path")
    private String image_path;

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSong_cnt() {
        return song_cnt;
    }

    public String getSub_title() {
        return sub_title;
    }

    public void setSong_cnt(String song_cnt) {
        this.song_cnt = song_cnt;
    }

    public void setSub_title(String sub_title) {
        this.sub_title = sub_title;
    }

    public String getImage() {
        return image_path;
    }

    public void setImage(String image) {
        this.image_path = image;
    }
}
