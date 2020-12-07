package com.example.plansplit.Models.Objects;

public class FriendRequest{
    private String key;
    private String name;
    private String email;
    private int foto;

    public FriendRequest(int foto, String name, String email, String key){
        this.foto = foto;
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

    public int getFoto(){
        return foto;
    }

    public String getKey(){
        return key;
    }
}
