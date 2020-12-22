package com.uru.hudba.model;

import com.google.gson.annotations.SerializedName;

public class SearchModel {
    @SerializedName("id")
    private String id;
    @SerializedName("search_id")
    private String search_id;
    @SerializedName("title")
    private String title;
    @SerializedName("cover_image")
    private String cover_image;

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSearch_id() {
        return search_id;
    }

    public void setSearch_id(String search_id) {
        this.search_id = search_id;
    }
}
