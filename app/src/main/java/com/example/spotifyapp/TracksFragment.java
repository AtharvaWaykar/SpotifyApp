package com.example.spotifyapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TracksFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TracksFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_ACCESS_TOKEN = "ACCESS_TOKEN";

    // TODO: Rename and change types of parameters
    private String accessToken;
    TextView firstTrack, secondTrack, thirdTrack, fourthTrack, fifthTrack;
    ImageView firstTrackImage, secondTrackImage, thirdTrackImage, fourthTrackImage, fifthTrackImage;

    public TracksFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param accessToken Parameter 1.
     * @return A new instance of fragment TracksFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TracksFragment newInstance(String accessToken) {
        TracksFragment fragment = new TracksFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ACCESS_TOKEN, accessToken);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            accessToken = getArguments().getString(ARG_ACCESS_TOKEN);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tracks, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        setComponents(view);
        fetchTopTracks(accessToken);
    }

    private void fetchTopTracks(String accessToken) {
        final Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me/top/tracks?limit=5")
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();

        MainActivity.mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("HTTP", "Failed to fetch data: " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String responseBody = response.body().string();
                    JSONObject json = new JSONObject(responseBody);
                    JSONArray items = json.getJSONArray("items");
                    StringBuilder tracks = new StringBuilder();
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject track = items.getJSONObject(i);
                        String name = track.getString("name");
                        tracks.append(name).append("\n");
                        JSONArray images = track.getJSONObject("album").getJSONArray("images");
                        String imageUrl = images.getJSONObject(0).getString("url");
                        loadTrackImage(imageUrl, getTrackImageView(i));
                    }
                    final String topTracks = tracks.toString();
                    setTracksText(topTracks);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }




    // Helper method to load image into ImageView using Glide
    private void loadTrackImage(String imageUrl, ImageView imageView) {
        getActivity().runOnUiThread(() -> {
            Glide.with(getActivity())
                    .load(imageUrl)
                    .into(imageView);
        });
    }

    private void setTracksText(String topTracks) {
        String[] tracks = topTracks.split("\n");
        setTextAsync("1. " + tracks[0], firstTrack);
        setTextAsync("2. " + tracks[1], secondTrack);
        setTextAsync("3. " + tracks[2], thirdTrack);
        setTextAsync("4. " + tracks[3], fourthTrack);
        setTextAsync("5. " + tracks[4], fifthTrack);
    }

    // Helper method to get the appropriate ImageView based on index
    private ImageView getTrackImageView(int index) {
        switch (index) {
            case 0:
                return firstTrackImage;
            case 1:
                return secondTrackImage;
            case 2:
                return thirdTrackImage;
            case 3:
                return fourthTrackImage;
            case 4:
                return fifthTrackImage;
            default:
                return null;
        }
    }
    private void setComponents(View view) {
        firstTrack = (TextView) view.findViewById(R.id.first_track_name);
        secondTrack = (TextView) view.findViewById(R.id.second_track_name);
        thirdTrack = (TextView) view.findViewById(R.id.third_track_name);
        fourthTrack = (TextView) view.findViewById(R.id.fourth_track_name);
        fifthTrack = (TextView) view.findViewById(R.id.fifth_track_name);
        firstTrackImage = (ImageView) view.findViewById(R.id.first_track_image);
        secondTrackImage = (ImageView) view.findViewById(R.id.second_track_image);
        thirdTrackImage = (ImageView) view.findViewById(R.id.third_track_image);
        fourthTrackImage = (ImageView) view.findViewById(R.id.fourth_track_image);
        fifthTrackImage = (ImageView) view.findViewById(R.id.fifth_track_image);
    }

    /**
     * Creates a UI thread to update a TextView in the background
     * Reduces UI latency and makes the system perform more consistently
     *
     * @param text the text to set
     * @param textView TextView object to update
     */
    private void setTextAsync(final String text, TextView textView) {
        getActivity().runOnUiThread(() -> textView.setText(text));
    }

}