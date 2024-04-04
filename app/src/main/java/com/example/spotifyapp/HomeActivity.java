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

                        //System.out.println("Track one name " + TracksFragment.tracksList.get(1).getName());

//
//                        try {
//                            TracksFragment.mediaPlayer.setDataSource(TracksFragment.tracksList.get(0).getPreviewUrl());
//                        } catch (IOException e) {
//                            System.out.println("error song 3 set data source");
//                            throw new RuntimeException(e);
//                        }
//                        try {
//                            TracksFragment.mediaPlayer.prepare(); // might take long! (for buffering, etc)
//                            System.out.println(TracksFragment.tracksList.get(0).getName() + " is prepared!");
//                        } catch (IOException e) {
//                            System.out.println("error song 3 prepare");
//                            throw new RuntimeException(e);
//                        }
//
//                        TracksFragment.mediaPlayer.start();
//                        System.out.println("SONG SHOULD BE PLAYING: " + TracksFragment.tracksList.get(0).getName());
//                        TracksFragment.playing = true;


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