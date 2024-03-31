package com.example.spotifyapp.tracks;

public class Track {
    private String name;
    private String imageUrl;

    Track(String name, String imageUrl) {
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
