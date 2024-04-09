package com.example.spotifyapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.example.spotifyapp.artists.ArtistsFragment;
import com.example.spotifyapp.tracks.TracksFragment;
import com.google.android.material.tabs.TabLayout;

import java.io.IOException;

public class HomeActivity extends AppCompatActivity {
    private String accessToken;
    private FrameLayout fragmentContainer;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        accessToken = getIntent().getStringExtra("ACCESS_TOKEN");
        fragmentContainer = (FrameLayout) findViewById(R.id.fragmentContainer);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, ArtistsFragment.newInstance(accessToken))
                .addToBackStack(null)
                .commit();
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {


                Fragment artistsFragment = ArtistsFragment.newInstance(accessToken);
                Fragment tracksFragment = TracksFragment.newInstance(accessToken);

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