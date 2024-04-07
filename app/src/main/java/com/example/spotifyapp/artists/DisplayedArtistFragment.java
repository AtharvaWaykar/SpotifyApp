package com.example.spotifyapp.artists;

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
 * Use the {@link DisplayedArtistFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DisplayedArtistFragment extends Fragment {
    private static final String ARG_IMAGE_URL = "image_url";
    private static final String ARG_ARTIST_NAME = "artist_name";

    private ImageView artistImageView;
    private TextView artistNameTextView;
    private String imageUrl;
    private String artistName;

    public DisplayedArtistFragment() {
        // Required empty public constructor
    }

    public static DisplayedArtistFragment newInstance(String name, String imageUrl) {
        DisplayedArtistFragment fragment = new DisplayedArtistFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ARTIST_NAME, name);
        args.putString(ARG_IMAGE_URL, imageUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_displayed_artist, container, false);
        artistImageView = view.findViewById(R.id.track_imageview);
        artistNameTextView = view.findViewById(R.id.track_name_textview);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imageUrl = getArguments().getString(ARG_IMAGE_URL);
        artistName = getArguments().getString(ARG_ARTIST_NAME);
        loadImage(imageUrl, artistImageView);
    }

    private void loadImage(String imageUrl, ImageView imageView) {
        if (imageUrl != null) {
            Glide.with(requireContext())
                    .load(imageUrl)
                    .into(imageView);
            artistNameTextView.setText(artistName);
        } else {
            int resourceId = getResources().getIdentifier(artistName, "drawable", requireContext().getPackageName());
            imageView.setImageResource(resourceId);
            artistNameTextView.setText("");

        }
    }
}