package com.example.nasaiotd;

import android.graphics.Bitmap;

/**
 * Represents a NASA Image of the Day entry.
 */
public class ImageData {
    /**
     * The date the image was the Image of the Day.
     */
    private String date;

    /**
     * The title of the image.
     */
    private String title;
    /**
     * A description of the image.
     */
    private String explanation;

    /**
     * The URL where the image can be accessed.
     */
    private String url;
    /**
     * A bitmap copy of the image.
     */
    private Bitmap image;

    /**
     * The URL to a high-definition copy of the image.
     */
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

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getHdUrl() {
        return hdUrl;
    }
}
