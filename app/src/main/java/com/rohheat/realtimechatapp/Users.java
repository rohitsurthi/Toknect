package com.rohheat.realtimechatapp;

import android.content.Context;
import android.net.Uri;

import com.squareup.picasso.Picasso;

public class Users {

    public String name;
    public String image;
    public String status;
    public String thumbs;

    public Users(){

    }

    public Users(String name, String image, String status,String thumbs) {
        this.name = name;
        this.image = image;
        this.status = status;
        this.thumbs = thumbs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getThumb_image() {
        return thumbs;
    }

    public void setThumb_image(String thumbs) {
        this.thumbs = thumbs;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
