package com.wohhie.www.myworksafe.model;

import java.util.List;

public class Incident {
    private String note;
    private String latitude;
    private String longitude;
    private String dateTime;
    private String photoUrl;
    private String videoUrl;
    private String fileUrl;
    private String level;
    private int userId;
    private String incidentType;
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private List<String> fileModelList;

    public Incident(){}

    public Incident(String note, String latitude, String longitude, String dateTime, String photoUrl, String videoUrl, String fileUrl, String level, int userId, String incidentType) {
        this.note = note;
        this.latitude = latitude;
        this.longitude = longitude;
        this.dateTime = dateTime;
        this.photoUrl = photoUrl;
        this.videoUrl = videoUrl;
        this.fileUrl = fileUrl;
        this.level = level;
        this.userId = userId;
        this.incidentType = incidentType;
    }


    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getIncidentType() {
        return incidentType;
    }

    public void setIncidentType(String incidentType) {
        this.incidentType = incidentType;
    }

    public List<String> getFileModelList() {
        return fileModelList;
    }

    public void setFileModelList(List<String> fileModelList) {
        this.fileModelList = fileModelList;
    }
}
