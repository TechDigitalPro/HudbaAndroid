package com.uru.hudba.model;

public class DownloadModel {
    private TrackModel trackModel;
    private String video_id;
    private String type;
    private int position;
    private String action;

//    public  DownloadModel = new DownloadModel(){
//
//    };

    public String getAction() {
        return action;
    }

    public int getPosition() {
        return position;
    }

    public String getType() {
        return type;
    }

    public String getVideo_id() {
        return video_id;
    }

    public TrackModel getTrackModel() {
        return trackModel;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setTrackModel(TrackModel trackModel) {
        this.trackModel = trackModel;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setVideo_id(String video_id) {
        this.video_id = video_id;
    }
}
