package com.bernd32.weatherdemo.models;

/**
 * Model for RecyclerView
 */

public class WeatherItem {

    private int mImgResource;
    private String mTitle;
    private String mValue;
    private String imgUrl;

    public WeatherItem(int mImgResource, String mTitle, String mValue) {
        this.mImgResource = mImgResource;
        this.mTitle = mTitle;
        this.mValue = mValue;
    }

    public WeatherItem(String imgUrl, String mTitle, String mValue) {
        this.imgUrl = imgUrl;
        this.mTitle = mTitle;
        this.mValue = mValue;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public int getImgResource() {
        return mImgResource;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getValue() {
        return mValue;
    }
}
