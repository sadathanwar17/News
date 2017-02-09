package com.example.android.news;

import android.graphics.Bitmap;

public class NewsFeed {

    String title, contributor;
    Bitmap image;

    public NewsFeed(String title, Bitmap image, String contributor) {
        this.title = title;
        this.image = image;
        this.contributor = contributor;
    }
}
