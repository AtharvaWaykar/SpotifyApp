package com.example.spotifyapp.ai;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.spotifyapp.MainActivity;
import com.example.spotifyapp.R;
import com.example.spotifyapp.tracks.TracksFragment;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

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
public class AiFragment extends Fragment {

    private String accessToken, timeRange;
    TextView AiResponseTextView;
    public  static List<String> tracksList;
    private List<String> artistsList;




    public AiFragment() {
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
    public static AiFragment newInstance(String accessToken, String timeRange) {
        AiFragment fragment = new AiFragment();
        Bundle args = new Bundle();
        args.putString("ACCESS_TOKEN", accessToken);
        args.putString("TIME_RANGE", timeRange);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        accessToken = getArguments().getString("ACCESS_TOKEN");
        timeRange = getArguments().getString("TIME_RANGE");


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ai, container, false);
        AiResponseTextView = view.findViewById(R.id.results);
        tracksList = new ArrayList<>();
        artistsList = new ArrayList<>();
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        fetchTopTracks(accessToken);
        fetchTopArtists(accessToken);
        try {
            request();
        } catch (Exception e){
          e.printStackTrace();
        }

    }
    private void request() throws Exception {
        GenerativeModel gm = new GenerativeModel(/* modelName */ "gemini-pro",
// Access your API key as a Build Configuration variable (see "Set up your API key" above)
                /* apiKey */ "AIzaSyDUs7iaYAVe2T3lpC2TJ5M3X5u3dI7Rg70");
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);
        String question = "Dynamically describe how someone who listens to "+ artistsList.toString()
                + "tends to act/think/dress and format the text simply and elegantly";
        Content content = new Content.Builder()
                .addText(question)
                .build();

                ListenableFuture <GenerateContentResponse> response = model.generateContent(content);
        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String resultText = result.getText();
                AiResponseTextView.setText(resultText);
                System.out.println(resultText);
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        }, this.getActivity().getMainExecutor());


    }

    private void fetchTopTracks(String accessToken) {
        final Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me/top/tracks?limit=5&time_range=" + timeRange)
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
                        JSONObject track = items.getJSONObject(i);
                        String name = track.getString("name");
                        tracksList.add(name);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void fetchTopArtists(String accessToken) {
        final Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me/top/artists?limit=5&time_range=" + timeRange)
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
                        artistsList.add(name);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}