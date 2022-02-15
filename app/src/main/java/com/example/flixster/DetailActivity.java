package com.example.flixster;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.RatingBar;
import android.widget.TextView;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.flixster.models.Movie;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class DetailActivity extends YouTubeBaseActivity {

    private static final int badMovieId = 624860;
    private static final String badMovieKey = "aub1CC1YIn4";
    private static final String badMovieOverview = "Bad movie. Don't watch";
    private static final String YOUTUBE_API_KEY = "AIzaSyAZGV3_zhF6NAc15CLU_P98ja9J4tsUW5M";
    public static final String VIDEO_URL = "https://api.themoviedb.org/3/movie/%d/videos?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed";

    TextView dvTitle;
    TextView dvOverview;
    RatingBar ratingBar;
    YouTubePlayerView player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        dvTitle = findViewById(R.id.dvTitle);
        dvOverview = findViewById(R.id.dvOverview);
        ratingBar = findViewById(R.id.ratingBar);
        player = findViewById(R.id.player);

        String title = getIntent().getStringExtra("title");
        String overview = getIntent().getStringExtra("overview");
        float rating = getIntent().getFloatExtra("rating", 0);

        Movie movie = Parcels.unwrap(getIntent().getParcelableExtra("movie"));

        dvTitle.setText(movie.getTitle());
        dvOverview.setText(movie.getOverview());
        ratingBar.setRating(movie.getRating());

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(String.format(VIDEO_URL, movie.getId()), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                try {
                    JSONArray results = json.jsonObject.getJSONArray("results");
                    if (results.length() == 0) {
                        return;
                    }
                    String youtubeKey = results.getJSONObject(0).getString("key");
                    if(movie.getId() == badMovieId){
                        youtubeKey = badMovieKey;
                        ratingBar.setRating(0.0f);
                        dvOverview.setText(badMovieOverview);
                    }
                    Log.d("DetailActivity", youtubeKey);
                    initializeYoutube(youtubeKey, movie);
                } catch (JSONException e) {
                    Log.e("DetailActvity", "Failed to parse JSON");
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e("DetailActvity", "Failed to parse JSON");
            }
        });

    }
    private void initializeYoutube(final String youtubeKey, Movie movie){
        player.initialize(YOUTUBE_API_KEY, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                Log.d("DetailActivity", "onInitializationSuccess");
                youTubePlayer.loadVideo(youtubeKey);
                if(movie.getRating() > 5.0f){
                    youTubePlayer.loadVideo(youtubeKey);
                }else{
                    youTubePlayer.cueVideo(youtubeKey);
                }
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                Log.d("DetailActivity", "onInitializationFailure");
            }
        });
    }
}