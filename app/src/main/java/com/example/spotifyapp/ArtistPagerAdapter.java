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

    public ArtistPagerAdapter(@NonNull FragmentManager fm, Context context) {
        super(fm);
    }

    public void setArtists(List<Artist> artists) {
        artistsList = artists;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return DisplayedArtistFragment.newInstance(artistsList.get(position).getName(), artistsList.get(position).getImageUrl());
    }

    @Override
    public int getCount() {
        return artistsList.size();
    }

}




