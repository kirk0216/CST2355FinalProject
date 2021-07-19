package com.example.nasaiotd;

import java.util.Date;

public class ImageData {
    private String date;

    private String title;
    private String explanation;

    private String url;
    private String hdUrl;

    public ImageData(String date, String title, String explanation, String url, String hdUrl) {
        this.date = date;
        this.title = title;
        this.explanation = explanation;
        this.url = url;
        this.hdUrl = hdUrl;
    }

    public String getDate() {
        return date;
    }

    public String getTitle() {
        return title;
    }

    public String getExplanation() {
        return explanation;
    }

    public String getUrl() {
        return url;
    }

    public String getHdUrl() {
        return hdUrl;
    }
}
