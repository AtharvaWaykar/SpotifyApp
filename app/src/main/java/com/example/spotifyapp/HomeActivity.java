package com.example.spotifyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomeActivity extends AppCompatActivity {

    private TextView topArtistsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        String accessToken = getIntent().getStringExtra("ACCESS_TOKEN");

        topArtistsTextView = findViewById(R.id.top_artists_text_view);
        Button fetchArtistsButton = findViewById(R.id.fetch_artists_button);

        // Set click listener for the fetchArtistsButton
        fetchArtistsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get authentication token from Intent extras

                // Fetch top artists using accessToken
                fetchTopArtists(accessToken);
            }
        });
    }

    private void fetchTopArtists(String accessToken) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me/top/artists")
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                topArtistsTextView.setText("Failed to fetch top artists: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                int responseCode = response.code();
                String responseBody = response.body().string();

                if (!response.isSuccessful()) {
                    topArtistsTextView.setText("Failed to fetch top artists. Response code: " + responseCode);
                    System.out.println("Error response: " + responseBody);
                    return;
                }

                try {
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




}