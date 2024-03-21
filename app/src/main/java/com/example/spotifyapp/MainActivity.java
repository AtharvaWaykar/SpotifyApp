package com.example.spotifyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioAttributes;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.media.AudioManager;
import android.media.MediaPlayer;

import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    public static final String CLIENT_ID = "bcac1ff3762a400f80669cc131690aa7";

    public static final String CLIENT_SECRET = "4b529297a7c04c47948113c5287229e9";
    public static final String REDIRECT_URI = "spotifyapp://auth";



    public static final int AUTH_TOKEN_REQUEST_CODE = 0;
    public static final int AUTH_CODE_REQUEST_CODE = 1;

    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    private String mAccessToken, mAccessCode;
    private Call mCall;

    private MediaPlayer mediaPlayer;

    private boolean playing;

    private String[] songUrls;

    private Button song1Button, song2Button, song3Button;
    private TextView tokenTextView, codeTextView, profileTextView, topSongsTextView1, topSongsTextView2, topSongsTextView3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the views
        tokenTextView = findViewById(R.id.token_text_view);
        codeTextView = findViewById(R.id.code_text_view);
        profileTextView = findViewById(R.id.response_text_view);
        topSongsTextView1 = findViewById(R.id.top_songs_view1);
        topSongsTextView2 = findViewById(R.id.top_songs_view2);
        topSongsTextView3 = findViewById(R.id.top_songs_view3);

        // Initialize the combined button
        Button combinedButton = findViewById(R.id.combined_button);

        // Initialize Get top songs button
        Button getTopSongsButton = findViewById(R.id.get_top_songs_button);

        // Initialize other buttons
        song1Button = findViewById(R.id.song_button_1);
        song2Button = findViewById(R.id.song_button_2);
        song3Button = findViewById(R.id.song_button_3);

        // Initialize songUrls array
        songUrls = new String[3];

        // Initialize media player
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        );

        // Set the click listener for the combined button
        combinedButton.setOnClickListener((v) -> {
            getToken();
        });

        // Set click listener for get top songs button
        getTopSongsButton.setOnClickListener((v -> {
            onGetTopSongsClicked();
        }));

        // Set click listener for song button
        song1Button.setOnClickListener((v) -> {
            if (!playing) {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioAttributes(
                        new AudioAttributes.Builder()
                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                .setUsage(AudioAttributes.USAGE_MEDIA)
                                .build()
                );
                try {
                    mediaPlayer.setDataSource(songUrls[0]);
                } catch (IOException e) {
                    System.out.println("error song 1 set data source");
                    throw new RuntimeException(e);
                }
                try {
                    mediaPlayer.prepare(); // might take long! (for buffering, etc)
                } catch (IOException e) {
                    System.out.println("error song 1 prepare");
                    throw new RuntimeException(e);
                }
                mediaPlayer.start();
                playing = true;
            } else {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
                playing = false;
            }
        });

        // Set click listener for song button 2
        song2Button.setOnClickListener((v) -> {
            if (!playing) {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioAttributes(
                        new AudioAttributes.Builder()
                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                .setUsage(AudioAttributes.USAGE_MEDIA)
                                .build()
                );
                try {
                    mediaPlayer.setDataSource(songUrls[1]);
                } catch (IOException e) {
                    System.out.println("error song 2 set data source");
                    throw new RuntimeException(e);
                }
                try {
                    mediaPlayer.prepare(); // might take long! (for buffering, etc)
                } catch (IOException e) {
                    System.out.println("error song 2 prepare");
                    throw new RuntimeException(e);
                }
                mediaPlayer.start();
                playing = true;
            } else {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
                playing = false;
            }
        });

        // Set click listener for song button 3
        song3Button.setOnClickListener((v) -> {
            if (!playing) {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioAttributes(
                        new AudioAttributes.Builder()
                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                .setUsage(AudioAttributes.USAGE_MEDIA)
                                .build()
                );
                try {
                    mediaPlayer.setDataSource(songUrls[2]);
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
                playing = true;
            } else {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
                playing = false;
            }
        });


    }

    public void getToken() {
        final AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.TOKEN);
        AuthorizationClient.openLoginActivity(MainActivity.this, AUTH_TOKEN_REQUEST_CODE, request);
    }

    public void getCode() {
        final AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.CODE);
        AuthorizationClient.openLoginActivity(MainActivity.this, AUTH_CODE_REQUEST_CODE, request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, data);

        if (AUTH_TOKEN_REQUEST_CODE == requestCode) {
            mAccessToken = response.getAccessToken();
            setTextAsync(mAccessToken, tokenTextView);
            getCode();
        } else if (AUTH_CODE_REQUEST_CODE == requestCode) {
            mAccessCode = response.getCode();
            setTextAsync(mAccessCode, codeTextView);
            //onGetUserProfileClicked();
        }
    }

    private void onGetTopSongsClicked() {
        if (mAccessToken == null || mAccessCode == null) {
            Toast.makeText(this, "You need to get an access token and access code first!", Toast.LENGTH_SHORT).show();
            return;
        }

        final Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me/top/tracks?limit=3")
                .addHeader("Authorization", "Bearer " + mAccessToken)
                .build();

        cancelCall();
        mCall = mOkHttpClient.newCall(request);

        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("HTTP", "Failed to fetch data: " + e);
                Toast.makeText(MainActivity.this, "Failed to fetch data, watch Logcat for more details",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    //getting response
                    final JSONObject jsonObject = new JSONObject(response.body().string());

                    //getting items as array
                    final JSONArray itemsArray = jsonObject.getJSONArray("items");

                    //artist name array
                    String[] songNames = new String[3];

                    // adds 3 song urls to array
                    for (int i = 0; i < itemsArray.length(); i++) {
                        JSONObject item = itemsArray.getJSONObject(i);

                        String previewUrl = item.getString("preview_url");

                        songUrls[i] = previewUrl;
                    }

                    // adds 3 song names to array
                    for (int i = 0; i < itemsArray.length(); i++) {
                        JSONObject item = itemsArray.getJSONObject(i);

                        String name = item.getString("name");

                        songNames[i] = name;
                    }

                    //Setting textView
                    setTextAsync(songUrls[0], topSongsTextView1);
                    setTextAsync(songUrls[1], topSongsTextView2);
                    setTextAsync(songUrls[2], topSongsTextView3);

                    //Setting button text
                    setButtonTextAsync(songNames[0], song1Button);
                    setButtonTextAsync(songNames[1], song2Button);
                    setButtonTextAsync(songNames[2], song3Button);

                } catch (JSONException e) {
                    Log.d("JSON", "Failed to parse data: " + e);
                    //Toast.makeText(MainActivity.this, "Failed to parse data, watch Logcat for more details",
                            //Toast.LENGTH_SHORT).show();
                    System.out.println("error");
                }
            }
        });
    }

    public void onGetUserProfileClicked() {
        if (mAccessToken == null || mAccessCode == null) {
            Toast.makeText(this, "You need to get an access token and access code first!", Toast.LENGTH_SHORT).show();
            return;
        }

        final Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me")
                .addHeader("Authorization", "Bearer " + mAccessToken)
                .build();

        cancelCall();
        mCall = mOkHttpClient.newCall(request);

        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("HTTP", "Failed to fetch data: " + e);
                Toast.makeText(MainActivity.this, "Failed to fetch data, watch Logcat for more details",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    final JSONObject jsonObject = new JSONObject(response.body().string());
                    setTextAsync(jsonObject.toString(3), profileTextView);
                } catch (JSONException e) {
                    Log.d("JSON", "Failed to parse data: " + e);
                    Toast.makeText(MainActivity.this, "Failed to parse data, watch Logcat for more details",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



    private void setTextAsync(final String text, TextView textView) {
        runOnUiThread(() -> textView.setText(text));
    }

    // sets button text
    private void setButtonTextAsync(final String text, Button button) {
        runOnUiThread(() -> button.setText(text));
    }



    private AuthorizationRequest getAuthenticationRequest(AuthorizationResponse.Type type) {
        return new AuthorizationRequest.Builder(CLIENT_ID, type, getRedirectUri().toString())
                .setShowDialog(false)
                .setScopes(new String[] { "user-top-read" })
                .setCampaign("your-campaign-token")
                .build();
    }

    private Uri getRedirectUri() {
        return Uri.parse(REDIRECT_URI);
    }

    private void cancelCall() {
        if (mCall != null) {
            mCall.cancel();
        }
    }

    @Override
    protected void onDestroy() {
        cancelCall();
        super.onDestroy();
    }
}
