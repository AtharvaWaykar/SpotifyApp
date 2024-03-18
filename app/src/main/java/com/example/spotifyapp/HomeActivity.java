package com.example.spotifyapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.SQLOutput;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomeActivity extends AppCompatActivity {

    private TextView topArtistsTextView;
    private FrameLayout fragmentContainer;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        String accessToken = getIntent().getStringExtra("ACCESS_TOKEN");
        fragmentContainer = (FrameLayout) findViewById(R.id.fragmentContainer);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new ArtistsFragment())
                .addToBackStack(null)
                .commit();
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment selectedFragment = null;
                switch (tab.getPosition()) {
                    case 0:
                        selectedFragment = new ArtistsFragment();
                        break;
                    case 1:
                        selectedFragment = new TracksFragment();
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, selectedFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });


        //topArtistsTextView = findViewById(R.id.top_artists_text_view);

        fetchTopArtists(accessToken);
    }

    private void fetchTopArtists(String accessToken) {
        final Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me/top/artists")
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();

        MainActivity.mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("HTTP", "Failed to fetch data: " + e);
                Toast.makeText(HomeActivity.this, "Failed to fetch data, watch Logcat for more details",
                        Toast.LENGTH_SHORT).show();
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
                    }
                    final String topArtists = artists.toString();
                    topArtistsTextView.setText(topArtists);
                } catch (JSONException e) {
                    e.printStackTrace();
                    topArtistsTextView.setText("Failed to parse top artists response");
                }
            }
        });
    }

    /**
     * Creates a UI thread to update a TextView in the background
     * Reduces UI latency and makes the system perform more consistently
     *
     * @param text the text to set
     * @param textView TextView object to update
     */
    private void setTextAsync(final String text, TextView textView) {
        runOnUiThread(() -> textView.setText(text));
    }



}