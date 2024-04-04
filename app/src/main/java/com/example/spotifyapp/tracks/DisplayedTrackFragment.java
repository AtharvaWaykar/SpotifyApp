package com.example.spotifyapp.tracks;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.spotifyapp.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DisplayedTrackFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DisplayedTrackFragment extends Fragment {
    private static final String ARG_IMAGE_URL = "image_url";
    private static final String ARG_ARTIST_NAME = "track_name";
    private static final String ARG_PREVIEW_URL = "preview_url";

    private ImageView trackImageView;
    private TextView trackNameTextView;
    private String imageUrl;
    private String trackName;
    private String previewUrl;

    public DisplayedTrackFragment() {
        // Required empty public constructor
    }

    public static DisplayedTrackFragment newInstance(String name, String imageUrl, String previewUrl) {
        DisplayedTrackFragment fragment = new DisplayedTrackFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ARTIST_NAME, name);
        args.putString(ARG_IMAGE_URL, imageUrl);
        args.putString(ARG_PREVIEW_URL, previewUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_displayed_track, container, false);
        trackImageView = view.findViewById(R.id.track_imageview);
        trackNameTextView = view.findViewById(R.id.track_name_textview);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imageUrl = getArguments().getString(ARG_IMAGE_URL);
        trackName = getArguments().getString(ARG_ARTIST_NAME);
        previewUrl = getArguments().getString(ARG_PREVIEW_URL);
        System.out.println("Song name: " + trackName);
        System.out.println("Load image");
        loadImage(imageUrl, trackImageView);
        trackNameTextView.setText(trackName);
    }

    private void loadImage(String imageUrl, ImageView imageView) {
        Glide.with(requireContext())
                .load(imageUrl)
                .into(imageView);
    }
}