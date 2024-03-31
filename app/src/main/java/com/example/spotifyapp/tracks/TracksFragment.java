package com.example.spotifyapp.tracks;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tracks, container, false);
        viewPager = view.findViewById(R.id.view_pager_tracks);
        trackPagerAdapter = new TrackPagerAdapter(getChildFragmentManager(), getContext());
        btnPrevious = view.findViewById(R.id.btn_previous_tracks);
        btnNext = view.findViewById(R.id.btn_next_tracks);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        viewPager.setAdapter(trackPagerAdapter);
        btnPrevious.setOnClickListener(v -> viewPager.setCurrentItem(viewPager.getCurrentItem() - 1, true));
        btnNext.setOnClickListener(v -> viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true));
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
                    List<Track> tracksList = new ArrayList<>();
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject track = items.getJSONObject(i);
                        String name = track.getString("name");
                        JSONArray images = track.getJSONObject("album").getJSONArray("images");
                        String imageUrl = images.getJSONObject(0).getString("url");
                        tracksList.add(new Track(name, imageUrl));
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