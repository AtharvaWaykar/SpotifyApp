package com.example.spotifyapp.tracks;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class TrackPagerAdapter extends FragmentPagerAdapter {

    private List<Track> tracksList = new ArrayList<>();

    public TrackPagerAdapter(@NonNull FragmentManager fm, Context context) {
        super(fm);
    }

    public void setTracks(List<Track> tracks) {
        tracksList = tracks;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return DisplayedTrackFragment.newInstance(tracksList.get(position).getName(), tracksList.get(position).getImageUrl(), tracksList.get(position).getPreviewUrl());
    }

    @Override
    public int getCount() {
        return tracksList.size();
    }

}




