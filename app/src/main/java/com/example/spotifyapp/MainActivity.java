package com.example.spotifyapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.auth.FirebaseUser;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import com.google.firebase.auth.FirebaseAuth;

import okhttp3.Call;
import okhttp3.OkHttpClient;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class MainActivity extends AppCompatActivity {

    public static final String CLIENT_ID = "bcac1ff3762a400f80669cc131690aa7";
    public static final String REDIRECT_URI = "spotifyapp://auth";

    public static final int AUTH_TOKEN_REQUEST_CODE = 0;
    public static final int AUTH_CODE_REQUEST_CODE = 1;
    private FirebaseAuth auth;

    public static final OkHttpClient mOkHttpClient = new OkHttpClient();
    private String mAccessToken, mAccessCode, timeRange;
    private Call mCall;
    private int deleteCounter = 0;

    private TextView tokenTextView, codeTextView;
    private RadioButton oneMonth, sixMonths, year;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the views
        tokenTextView = (TextView) findViewById(R.id.token_text_view);
        codeTextView = (TextView) findViewById(R.id.code_text_view);
        oneMonth = (RadioButton) findViewById(R.id.oneMonth_radio);
        sixMonths = (RadioButton) findViewById(R.id.sixMonths_radio);
        year = (RadioButton) findViewById(R.id.year_radio);
        // Initialize the buttons
        Button tokenBtn = (Button) findViewById(R.id.token_btn);
        Button codeBtn = (Button) findViewById(R.id.code_btn);
        Button profileBtn = (Button) findViewById(R.id.profile_btn);
        Button nextBtn = (Button) findViewById(R.id.next_btn);

        auth = FirebaseAuth.getInstance();
        getToken();

        timeRange = "short_term";
        oneMonth.setOnClickListener((v) -> {
            timeRange = "short_term";
        });

        sixMonths.setOnClickListener((v) -> {
            timeRange = "medium_term";
        });

        year.setOnClickListener((v) -> {
            timeRange = "long_term";
        });
        tokenBtn.setOnClickListener((v) -> {
            getCode();
        });

        codeBtn.setOnClickListener((v) -> {
            deleteCounter++;
            if (deleteCounter % 2 != 0) {
                Toast.makeText(MainActivity.this, "Are you sure?", Toast.LENGTH_LONG).show();

            } else {
                FirebaseUser currentUser = auth.getCurrentUser();
                currentUser.delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                // Account deleted successfully
                                Toast.makeText(MainActivity.this, "Firebase account deleted successfully", Toast.LENGTH_SHORT).show();
                                // Perform sign out
                                auth.signOut();
                                Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
                                intent.putExtra("ACCESS_TOKEN", mAccessToken);
                                startActivity(intent);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Failed to delete account
                                Toast.makeText(MainActivity.this, "You need to logout and login first", Toast.LENGTH_SHORT).show();
                                System.out.println(e.getMessage());
                            }
                        });
            }

        });

        profileBtn.setOnClickListener((v) -> {
            FirebaseUser currentUser = auth.getCurrentUser();
            currentUser = null;
            auth.signOut();

            Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
            intent.putExtra("ACCESS_TOKEN", mAccessToken);
            startActivity(intent);

        });

        nextBtn.setOnClickListener((v) -> {
            goHomeActivity();
        });

    }

    /**
     * Get token from Spotify
     * This method will open the Spotify login activity and get the token
     * What is token?
     * https://developer.spotify.com/documentation/general/guides/authorization-guide/
     */
    public void getToken() {
        final AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.TOKEN);
        AuthorizationClient.openLoginActivity(MainActivity.this, AUTH_TOKEN_REQUEST_CODE, request);
    }

    /**
     * Get code from Spotify
     * This method will open the Spotify login activity and get the code
     * What is code?
     * https://developer.spotify.com/documentation/general/guides/authorization-guide/
     */
    public void getCode() {
        final AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.CODE);
        AuthorizationClient.openLoginActivity(MainActivity.this, AUTH_CODE_REQUEST_CODE, request);
    }


    /**
     * When the app leaves this activity to momentarily get a token/code, this function
     * fetches the result of that external activity to get the response from Spotify
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, data);

        // Check which request code is present (if any)
        if (AUTH_TOKEN_REQUEST_CODE == requestCode) {
            mAccessToken = response.getAccessToken();
            //setTextAsync(mAccessToken, tokenTextView);

        } else if (AUTH_CODE_REQUEST_CODE == requestCode) {

            mAccessCode = response.getCode();
            saveAccessCodeToFirebase(mAccessCode);
            listenForAccessCodeChanges();

            //setTextAsync(mAccessCode, codeTextView);
        }
    }
    private void listenForAccessCodeChanges() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
            userRef.child("accessCode").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String accessCode = snapshot.getValue(String.class);
                    if (accessCode != null) {
                        Log.d("Firebase", "Access code retrieved from Firebase: " + accessCode);
                        // Now you can do whatever you want with the access code
                        // For example, you can set it to a variable or use it directly
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("Firebase", "Failed to retrieve access code from Firebase: " + error.getMessage());
                }
            });
        } else {
            Log.e("Firebase", "User is null, cannot listen for access code changes from Firebase");
        }
    }


    private void saveAccessCodeToFirebase(String accessCode) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
            userRef.child("accessCode").setValue(accessCode)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d("Firebase", "Access code saved successfully to Firebase");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("Firebase", "Failed to save access code to Firebase: " + e.getMessage());
                        }
                    });
        } else {
            Log.e("Firebase", "User is null, cannot save access code to Firebase");
        }
    }

    public void goHomeActivity() {
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        intent.putExtra("ACCESS_TOKEN", mAccessToken);
        intent.putExtra("TIME_RANGE", timeRange);
        startActivity(intent);
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

    /**
     * Get authentication request
     *
     * @param type the type of the request
     * @return the authentication request
     */
    private AuthorizationRequest getAuthenticationRequest(AuthorizationResponse.Type type) {
        return new AuthorizationRequest.Builder(CLIENT_ID, type, getRedirectUri().toString())
                .setShowDialog(true)
                .setScopes(new String[] { "user-read-email", "user-top-read" }) // <--- Change the scope of your requested token here
                .setCampaign("your-campaign-token")
                .build();
    }


    /**
     * Gets the redirect Uri for Spotify
     *
     * @return redirect Uri object
     */
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
        setContentView(R.layout.dialog_forgot);
    }
}