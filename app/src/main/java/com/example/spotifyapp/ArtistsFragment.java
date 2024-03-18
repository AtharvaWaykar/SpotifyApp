package com.example.spotifyapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.SQLOutput;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ArtistsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ArtistsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_ACCESS_TOKEN = "ACCESS_TOKEN";

    // TODO: Rename and change types of parameters
    private String accessToken;
    TextView firstArtist, secondArtist, thirdArtist, fourthArtist, fifthArtist;
    ImageView firstArtistImage, secondArtistImage, thirdArtistImage, fourthArtistImage, fifthArtistImage;

    public ArtistsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param accessToken Parameter 1.
     * @return A new instance of fragment ArtistsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ArtistsFragment newInstance(String accessToken) {
        ArtistsFragment fragment = new ArtistsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ACCESS_TOKEN, accessToken);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        accessToken = getArguments().getString(ARG_ACCESS_TOKEN);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_artists, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        setComponents(view);
        fetchTopArtists(accessToken);
    }

    private void fetchTopArtists(String accessToken) {
        final Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me/top/artists?limit=5")
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
                    StringBuilder artists = new StringBuilder();
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject artist = items.getJSONObject(i);
                        String name = artist.getString("name");
                        artists.append(name).append("\n");
                        JSONArray images = artist.getJSONArray("images");
                        String imageUrl = images.getJSONObject(2).getString("url");
                        loadArtistImage(imageUrl, getArtistImageView(i));

                    }
                    final String topArtists = artists.toString();
                    setArtistsText(topArtists);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // Helper method to load image into ImageView using Glide
    private void loadArtistImage(String imageUrl, ImageView imageView) {
        getActivity().runOnUiThread(() -> {
            Glide.with(getActivity())
                    .load(imageUrl)
                    .into(imageView);
        });
    }

    private void setArtistsText(String topArtists) {
        String[] artists = topArtists.split("\n");
        setTextAsync("1. " + artists[0], firstArtist);
        setTextAsync("2. " + artists[1], secondArtist);
        setTextAsync("3. " + artists[2], thirdArtist);
        setTextAsync("4. " + artists[3], fourthArtist);
        setTextAsync("5. " + artists[4], fifthArtist);
    }

    // Helper method to get the appropriate ImageView based on index
    private ImageView getArtistImageView(int index) {
        switch (index) {
            case 0:
                return firstArtistImage;
            case 1:
                return secondArtistImage;
            case 2:
                return thirdArtistImage;
            case 3:
                return fourthArtistImage;
            case 4:
                return fifthArtistImage;
            default:
                return null;
        }
    }
    private void setComponents(View view) {
        firstArtist = (TextView) view.findViewById(R.id.first_artist_name);
        secondArtist = (TextView) view.findViewById(R.id.second_artist_name);
        thirdArtist = (TextView) view.findViewById(R.id.third_artist_name);
        fourthArtist = (TextView) view.findViewById(R.id.fourth_artist_name);
        fifthArtist = (TextView) view.findViewById(R.id.fifth_artist_name);
        firstArtistImage = (ImageView) view.findViewById(R.id.first_artist_image);
        secondArtistImage = (ImageView) view.findViewById(R.id.second_artist_image);
        thirdArtistImage = (ImageView) view.findViewById(R.id.third_artist_image);
        fourthArtistImage = (ImageView) view.findViewById(R.id.fourth_artist_image);
        fifthArtistImage = (ImageView) view.findViewById(R.id.fifth_artist_image);
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