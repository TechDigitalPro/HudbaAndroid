package com.uru.hudba.model;

import com.google.gson.annotations.SerializedName;

public class AlbumModel {
    @SerializedName("list_id")
    private String list_id;
    @SerializedName("title")
    private String title;
    @SerializedName("tracklist")
    private String tracklist;
    @SerializedName("cover_image")
    private String cover_image;
    @SerializedName("track_cnt")
    private String track_cnt;
    @SerializedName("user_name")
    private String user_name;
    @SerializedName("description")
    private String description;

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }



    public String getCover_image() {
        return cover_image;
    }

    public void setCover_image(String cover_image) {
        this.cover_image = cover_image;
    }


    public String getList_id() {
        return list_id;
    }

    public String getTracklist() {
        return tracklist;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getTrack_cnt() {
        return track_cnt;
    }

    public void setTrack_cnt(String track_cnt) {
        this.track_cnt = track_cnt;
    }

    public void setList_id(String list_id) {
        this.list_id = list_id;
    }

    public void setTracklist(String tracklist) {
        this.tracklist = tracklist;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
