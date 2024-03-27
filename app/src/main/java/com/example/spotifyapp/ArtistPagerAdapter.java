package com.example.spotifyapp;

import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ArtistPagerAdapter extends FragmentPagerAdapter {

    private List<Artist> artistsList = new ArrayList<>();
    private List<ImageView> imageViews = new ArrayList<>();
    private Context context;

    public ArtistPagerAdapter(@NonNull FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    public void setArtists(List<Artist> artists) {
        this.artistsList.clear();
        this.artistsList.addAll(artists);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        // Create a new placeholder fragment for the artist
        imageViews.add(new ImageView(context)); // Initialize ImageView with context
        return DisplayedArtistFragment.newInstance(artistsList.get(position).getImageUrl());
    }

    @Override
    public int getCount() {
        return artistsList.size();
    }

    // Method to update the ImageView with the loaded image
    public void updateImageView(int position, ImageView imageView) {
        if (position >= 0 && position < imageViews.size()) {
            imageViews.set(position, imageView);
        }
    }

    // Method to retrieve the ImageView for the current artist
    public ImageView getCurrentImageView(int position) {
        if (position >= 0 && position < imageViews.size()) {
            return imageViews.get(position);
        }
        return null;
    }
}




