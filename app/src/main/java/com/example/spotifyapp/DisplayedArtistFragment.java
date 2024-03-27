package com.example.spotifyapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DisplayedArtistFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DisplayedArtistFragment extends Fragment {
    private static final String ARG_IMAGE_URL = "image_url";

    public DisplayedArtistFragment() {
        // Required empty public constructor
    }

    public static DisplayedArtistFragment newInstance(String imageUrl) {
        DisplayedArtistFragment fragment = new DisplayedArtistFragment();
        Bundle args = new Bundle();
        args.putString(ARG_IMAGE_URL, imageUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_displayed_artist, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView artistImageView = view.findViewById(R.id.artist_imageview);
        String imageUrl = getArguments().getString(ARG_IMAGE_URL);
        loadImage(imageUrl, artistImageView);
    }

    private void loadImage(String imageUrl, ImageView imageView) {
        Glide.with(requireContext())
                .load(imageUrl)
                .into(imageView);
    }
}