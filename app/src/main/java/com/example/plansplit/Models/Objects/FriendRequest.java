package com.example.plansplit.Models.Objects;

import android.net.Uri;

public class FriendRequest{
    private String key;
    private String name;
    private String email;
    private Uri image;

    public FriendRequest(String image, String name, String email, String key){
        this.image = Uri.parse(image);;
        this.name = name;
        this.email = email;
        this.key = key;
    }

    public String getName(){
        return name;
    }

    public String getEmail(){
        return email;
    }

    public Uri getFoto(){
        return image;
    }

    public String getKey(){
        return key;
    }
}
