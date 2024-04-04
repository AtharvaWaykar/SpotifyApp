package com.example.spotifyapp.tracks;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.spotifyapp.MainActivity;
import com.example.spotifyapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TracksFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
//
public class TracksFragment extends Fragment {

    private static final String ARG_ACCESS_TOKEN = "ACCESS_TOKEN";
    private String accessToken;
    private ViewPager viewPager;
    private TrackPagerAdapter trackPagerAdapter;
    private ImageButton btnPrevious, btnNext;
    private MediaPlayer mediaPlayer;
    List<Track> tracksList;
    private boolean playing;


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
        accessToken = getArguments().getString(ARG_ACCESS_TOKEN);

        // Initialize media player
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tracks, container, false);
        viewPager = view.findViewById(R.id.view_pager_tracks);
        trackPagerAdapter = new TrackPagerAdapter(getChildFragmentManager(), getContext());
        btnPrevious = view.findViewById(R.id.btn_previous_tracks);
        btnNext = view.findViewById(R.id.btn_next_tracks);

        tracksList = new ArrayList<>();

        playing = false;
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        fetchTopTracks(accessToken);

        viewPager.setAdapter(trackPagerAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // This method will be invoked when the ViewPager is scrolled
                // You can add your code here if needed
            }

            @Override
            public void onPageSelected(int position) {
                // This method will be invoked when a new page becomes selected
                // 'position' parameter will provide you the current position of ViewPager
                Log.d("ViewPagerPosition", "Current position: " + position);

                // Check if tracksList is initialized and not empty
                if (tracksList != null && !tracksList.isEmpty()) {
                    if (!playing) {

                        mediaPlayer = new MediaPlayer();
                        mediaPlayer.setAudioAttributes(
                                new AudioAttributes.Builder()
                                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                        .setUsage(AudioAttributes.USAGE_MEDIA)
                                        .build()
                        );

                        try {
                            mediaPlayer.setDataSource(tracksList.get(position).getPreviewUrl());
                        } catch (IOException e) {
                            System.out.println("error song 3 set data source");
                            throw new RuntimeException(e);
                        }
                        try {
                            mediaPlayer.prepare(); // might take long! (for buffering, etc)
                        } catch (IOException e) {
                            System.out.println("error song 3 prepare");
                            throw new RuntimeException(e);
                        }
                        mediaPlayer.start();
                        System.out.println("SONG SHOULD BE PLAYING: " + tracksList.get(position).getName());
                        playing = true;
                    } else {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        mediaPlayer = null;

                        mediaPlayer = new MediaPlayer();
                        mediaPlayer.setAudioAttributes(
                                new AudioAttributes.Builder()
                                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                        .setUsage(AudioAttributes.USAGE_MEDIA)
                                        .build()
                        );

                        try {
                            mediaPlayer.setDataSource(tracksList.get(position).getPreviewUrl());
                        } catch (IOException e) {
                            System.out.println("error song 3 set data source");
                            throw new RuntimeException(e);
                        }
                        try {
                            mediaPlayer.prepare(); // might take long! (for buffering, etc)
                        } catch (IOException e) {
                            System.out.println("error song 3 prepare");
                            throw new RuntimeException(e);
                        }
                        mediaPlayer.start();
                        System.out.println("SONG SHOULD BE PLAYING: " + tracksList.get(position).getName());
                        playing = true;

                    }
                } else {
                    // Handle the case when tracksList is not initialized or empty
                    Log.e("ViewPagerPosition", "tracksList is not initialized or empty");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // This method will be invoked when the scroll state changes
                // You can add your code here if needed
            }
        });



        btnPrevious.setOnClickListener(v -> viewPager.setCurrentItem(viewPager.getCurrentItem() - 1, false));
        btnNext.setOnClickListener(v -> viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, false));

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

                    for (int i = 0; i < items.length(); i++) {
                        JSONObject track = items.getJSONObject(i);
                        String name = track.getString("name");
                        JSONArray images = track.getJSONObject("album").getJSONArray("images");
                        String imageUrl = images.getJSONObject(0).getString("url");

                        String previewUrl = track.getString("preview_url");
                        tracksList.add(new Track(name, imageUrl, previewUrl));
                    }
                    getActivity().runOnUiThread(() -> {
                        trackPagerAdapter.setTracks(tracksList);
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


}