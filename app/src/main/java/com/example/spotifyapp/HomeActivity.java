package com.example.spotifyapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Button;
import android.widget.FrameLayout;

import com.example.spotifyapp.artists.ArtistsFragment;
import com.example.spotifyapp.tracks.TracksFragment;
import com.example.spotifyapp.ai.AiFragment;
import com.google.android.material.tabs.TabLayout;

import java.io.IOException;

public class HomeActivity extends AppCompatActivity {
    private String accessToken, timeRange;
    private FrameLayout fragmentContainer;
    private TabLayout tabLayout;
    private Button backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        accessToken = getIntent().getStringExtra("ACCESS_TOKEN");
        timeRange = getIntent().getStringExtra("TIME_RANGE");

        fragmentContainer = (FrameLayout) findViewById(R.id.fragmentContainer);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        backBtn = (Button) findViewById(R.id.back_to_welcome_btn);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, ArtistsFragment.newInstance(accessToken, timeRange))
                .addToBackStack(null)
                .commit();


        backBtn.setOnClickListener(v -> {
            if (TracksFragment.mediaPlayer != null) {
                TracksFragment.mediaPlayer.stop();
                TracksFragment.mediaPlayer.release();
                TracksFragment.mediaPlayer = null;
            }
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            startActivity(intent);
        });

        System.out.println("TIME RANGE IS: " + timeRange);



        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {


                Fragment artistsFragment = ArtistsFragment.newInstance(accessToken, timeRange);
                Fragment tracksFragment = TracksFragment.newInstance(accessToken, timeRange);
                Fragment aiFragment = AiFragment.newInstance(accessToken, timeRange);

                Fragment selectedFragment = artistsFragment;

                switch (tab.getPosition()) {
                    case 0:
                        selectedFragment = artistsFragment;

                        if (TracksFragment.mediaPlayer != null) {
                            TracksFragment.mediaPlayer.stop();
                            TracksFragment.mediaPlayer.release();
                            TracksFragment.mediaPlayer = null;
                        }

                        // Initialize media player
                        TracksFragment.mediaPlayer = new MediaPlayer();
                        TracksFragment.mediaPlayer.setAudioAttributes(
                                new AudioAttributes.Builder()
                                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                        .setUsage(AudioAttributes.USAGE_MEDIA)
                                        .build()
                        );



                        break;
                    case 1:
                        selectedFragment = tracksFragment;

                        break;
                    case 2:
                        selectedFragment = aiFragment;

                        if (TracksFragment.mediaPlayer != null) {
                            TracksFragment.mediaPlayer.stop();
                            TracksFragment.mediaPlayer.release();
                            TracksFragment.mediaPlayer = null;
                        }

                        // Initialize media player
                        TracksFragment.mediaPlayer = new MediaPlayer();
                        TracksFragment.mediaPlayer.setAudioAttributes(
                                new AudioAttributes.Builder()
                                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                        .setUsage(AudioAttributes.USAGE_MEDIA)
                                        .build()
                        );


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

    }

}