package com.example.spotifyapp.tracks;

public class Track {
    private String name;
    private String imageUrl;
private String previewUrl;
    Track(String name, String imageUrl, String previewUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.previewUrl = previewUrl;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }
}
