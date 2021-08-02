package com.example.nasaiotd;

import android.graphics.Bitmap;
import android.webkit.MimeTypeMap;

/**
 * Represents a NASA Image of the Day entry.
 */
public class ImageData {
    /**
     * The database identifier for this object.
     */
    private long id;

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

    public ImageData(long id, String date, String title, String explanation, String url, String hdUrl) {
        this(date, title, explanation, url, hdUrl);

        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    /**
     * Returns the file name, including extension, where this file is saved locally.
     * Credit: https://stackoverflow.com/a/49690119
     * @return The path to the locally saved file.
     */
    public String getFileName() {
        String url = getUrl();
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        String fileName = getDate() + "." + extension;

        return fileName;
    }
}
