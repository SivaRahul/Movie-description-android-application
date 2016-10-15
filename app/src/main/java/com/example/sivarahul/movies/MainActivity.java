package com.example.sivarahul.movies;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {
    // Google Console APIs developer key
    // Replace this key with your's
    public static final String API_KEY = "AIzaSyDFC_L6jgEIu2kXdSZT0P9ymxxC09EoRX8";
//// YouTube video id
    private String VIDEO_ID = "kHue-HaXXzg";
    private static final int RECOVERY_DIALOG_REQUEST = 1;

    // YouTube player view
    private YouTubePlayerView youTubeView;

    EditText search;
    TextView mname;
    TextView direct;
    TextView casts;
    TextView genres;
    TextView reld;
    TextView plots;
    ImageButton srcbtn;
    String name;
    RatingBar rates;
   // ImageView img;
    String image;
    private YouTubePlayer.Provider provider;
    private YouTubeInitializationResult errorReason;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        getids();
        getlisteners();
        YouTubePlayerView youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
        youTubeView.initialize(API_KEY, this);
        if(isConnected()){
            Toast.makeText(getBaseContext(), "Connected!", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getBaseContext(), "NotConnected!", Toast.LENGTH_SHORT).show();
        }

    }
    private void getlisteners(){
        srcbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name=search.getText().toString();
                new HttpAsyncTask().execute("http://www.omdbapi.com/?t="+name+"&y=&plot=short&r=json");

            }
        });
    }

    private boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    private void getids() {
        search=(EditText)findViewById(R.id.searchit);
        mname=(TextView)findViewById(R.id.mvname);
        direct=(TextView)findViewById(R.id.director);
        casts=(TextView)findViewById(R.id.cast);
        genres=(TextView)findViewById(R.id.genre);
        plots=(TextView)findViewById(R.id.plot);
        srcbtn=(ImageButton)findViewById(R.id.srcbt);
        reld=(TextView)findViewById(R.id.reldate);
     //  img=(ImageView)findViewById(R.id.poster);
        rates=(RatingBar)findViewById(R.id.ratingBar);
        LayerDrawable stars = (LayerDrawable) rates.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);
    }

    public static String GET(String url){
        InputStream inputStream = null;
        String result = "";
        try {

            URL ur = new URL(url);
            HttpURLConnection urlConnection = (HttpURLConnection) ur.openConnection();

            // receive response as inputStream
            inputStream =urlConnection.getInputStream();

            // convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }
    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return GET(urls[0]);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            try {
                JSONObject jsonRootObject = new JSONObject(result);
                String title=jsonRootObject.optString("Title").toString();
                String reldate=jsonRootObject.optString("Released").toString();
                String runtime=jsonRootObject.optString("Runtime").toString();
                String director=jsonRootObject.optString("Director").toString();
                String cast=jsonRootObject.optString("Actors").toString();
                String genre=jsonRootObject.optString("Genre").toString();
                String plot=jsonRootObject.optString("Plot").toString();
                 image=jsonRootObject.optString("Poster").toString();
                Double rating=jsonRootObject.optDouble("imdbRating");
                float rateit= (float) (rating/2);
               // String poster=jsonRootObject.optString("Poster").toString();
                mname.setText(title);
                direct.setText("Director: "+director);
                casts.setText("Cast: "+cast);
                genres.setText("Genre: "+genre);
                reld.setText("ReleaseDate: "+reldate +"\n"+"RunTime: "+runtime);
                plots.setText("PLOT: "+plot);
                rates.setRating(rateit);

            } catch (JSONException e) {e.printStackTrace();}
        }
    }
    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                        YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show();
        } else {
            String errorMessage = String.format("YouTube Error (%1$s)",
                    errorReason.toString());
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                        YouTubePlayer player, boolean wasRestored) {
        if (!wasRestored) {
            player.cueVideo(VIDEO_ID);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_DIALOG_REQUEST) {
            // Retry initialization if user performed a recovery action
            getYouTubePlayerProvider().initialize(API_KEY, this);
        }
    }

    protected YouTubePlayer.Provider getYouTubePlayerProvider() {
        return (YouTubePlayerView) findViewById(R.id.youtube_view);
    }

    private class FullScreenListener implements YouTubePlayer.OnFullscreenListener{

        @Override
        public void onFullscreen(boolean isFullscreen) {
            //Called when fullscreen mode changes.

        }

    }

    private class PlaybackListener implements YouTubePlayer.PlaybackEventListener{

        @Override
        public void onBuffering(boolean isBuffering) {
            // Called when buffering starts or ends.

        }

        @Override
        public void onPaused() {
            // Called when playback is paused, either due to pause() or user action.

        }

        @Override
        public void onPlaying() {
            // Called when playback starts, either due to play() or user action.

        }

        @Override
        public void onSeekTo(int newPositionMillis) {
            // Called when a jump in playback position occurs,
            //either due to the user scrubbing or a seek method being called

        }

        @Override
        public void onStopped() {
            // Called when playback stops for a reason other than being paused.

        }

    }

    private class PlayerStateListener implements YouTubePlayer.PlayerStateChangeListener{

        @Override
        public void onAdStarted() {
            // Called when playback of an advertisement starts.

        }

        @Override
        public void onError(YouTubePlayer.ErrorReason reason) {
            // Called when an error occurs.

        }

        @Override
        public void onLoaded(String arg0) {
            // Called when a video has finished loading.

        }

        @Override
        public void onLoading() {
            // Called when the player begins loading a video and is not ready to accept commands affecting playback

        }

        @Override
        public void onVideoEnded() {
            // Called when the video reaches its end.

        }

        @Override
        public void onVideoStarted() {
            // Called when playback of the video starts.

        }

    }

    private class PlayListListener implements YouTubePlayer.PlaylistEventListener{

        @Override
        public void onNext() {
            // Called before the player starts loading the next video in the playlist.

        }

        @Override
        public void onPlaylistEnded() {
            // Called when the last video in the playlist has ended.

        }

        @Override
        public void onPrevious() {
            // Called before the player starts loading the previous video in the playlist.

        }

    }


}
