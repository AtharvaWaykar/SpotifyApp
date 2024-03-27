package com.example.spotifyapp;

public class Artist {
    private String name;
    private String imageUrl;

    Artist(String name, String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
