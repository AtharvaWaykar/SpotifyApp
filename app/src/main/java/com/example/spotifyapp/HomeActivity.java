package com.example.spotifyapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.widget.FrameLayout;

import com.example.spotifyapp.artists.ArtistsFragment;
import com.example.spotifyapp.tracks.TracksFragment;
import com.google.android.material.tabs.TabLayout;

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
                Fragment selectedFragment = null;
                switch (tab.getPosition()) {
                    case 0:
                        selectedFragment = ArtistsFragment.newInstance(accessToken);
                        break;
                    case 1:
                        selectedFragment = TracksFragment.newInstance(accessToken);
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