package com.example.spotifyapp.artists;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.media.AudioAttributes;
import android.media.MediaPlayer;

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
 * Use the {@link ArtistsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
//
public class ArtistsFragment extends Fragment {

    private static final String ARG_ACCESS_TOKEN = "ACCESS_TOKEN";
    private String accessToken;
    private ViewPager viewPager;
    private ArtistPagerAdapter artistPagerAdapter;
    private ImageButton btnPrevious, btnNext;
    private List<Artist> artistsList;


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
        View view = inflater.inflate(R.layout.fragment_artists, container, false);
        viewPager = view.findViewById(R.id.view_pager_artists);
        artistPagerAdapter = new ArtistPagerAdapter(getChildFragmentManager(), getContext());
        btnPrevious = view.findViewById(R.id.btn_previous_artists);
        btnNext = view.findViewById(R.id.btn_next_artists);

        artistsList = new ArrayList<>();
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        viewPager.setAdapter(artistPagerAdapter);
        btnPrevious.setOnClickListener(v -> viewPager.setCurrentItem(viewPager.getCurrentItem() - 1, true));
        btnNext.setOnClickListener(v -> viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true));
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

                    for (int i = 0; i < items.length(); i++) {
                        JSONObject artist = items.getJSONObject(i);
                        String name = artist.getString("name");
                        JSONArray images = artist.getJSONArray("images");
                        String imageUrl = images.getJSONObject(2).getString("url");
                        artistsList.add(new Artist(name, imageUrl));
                    }
                    getActivity().runOnUiThread(() -> {
                        artistPagerAdapter.setArtists(artistsList);
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}


