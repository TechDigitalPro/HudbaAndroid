package com.uru.hudba.model;

import com.google.gson.annotations.SerializedName;

public class TrackModel {
    @SerializedName("id")
    private String id;
    @SerializedName("p_listid")
    private String p_listid;
    @SerializedName("track_id")
    private String track_id;
    @SerializedName("artist_id")
    private String artist_id;
    @SerializedName("artist_name")
    private String artist_name;
    @SerializedName("videoId")
    private String videoId;
    @SerializedName("cover_image")
    private String cover_image;
    @SerializedName("title")
    private String title;
    @SerializedName("album_id")
    private String album_id;
    @SerializedName("album_name")
    private String album_name;
    @SerializedName("description")
    private String description;
    @SerializedName("favorite")
    private String favorite;

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



    public String getCover_image() {
        return cover_image;
    }

    public void setCover_image(String cover_image) {
        this.cover_image = cover_image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getArtist_id() {
        return artist_id;
    }

    public String getP_listid() {
        return p_listid;
    }

    public String getTrack_id() {
        return track_id;
    }

    public void setArtist_id(String artist_id) {
        this.artist_id = artist_id;
    }

    public void setP_listid(String p_listid) {
        this.p_listid = p_listid;
    }

    public void setTrack_id(String track_id) {
        this.track_id = track_id;
    }

    public String getAlbum_id() {
        return album_id;
    }

    public String getAlbum_name() {
        return album_name;
    }

    public void setAlbum_id(String album_id) {
        this.album_id = album_id;
    }

    public void setAlbum_name(String album_name) {
        this.album_name = album_name;
    }

    public String getFavorite() {
        return favorite;
    }

    public void setFavorite(String favorite) {
        this.favorite = favorite;
    }
}
