package com.example.nasaiotd;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

/**
 * Performs HTTP requests to the NASA Image of the Day API.
 */
public class NasaApiQuery extends AsyncTask<String, Integer, ImageData> {

    private static final String LOG_TAG = "NASA_API_QUERY";
    private static final String API_URL = "https://api.nasa.gov/planetary/apod?api_key=AD83pecRZgvpNZRi1pfDdCruHXIvCV7KGjBI2j0B";

    private final Activity context;
    private final ProgressBar progressBar;
    private final ImagesAdapter imagesAdapter;
    private String errorMessage;

    public NasaApiQuery(Activity context, ImagesAdapter imagesAdapter) {
        this.context = context;
        this.progressBar = context.findViewById(R.id.SelectDateProgressBar);
        this.imagesAdapter = imagesAdapter;
    }

    @Override
    protected ImageData doInBackground(String... dates) {
        String date = Arrays.asList(dates).get(0);
        ImageData imageData = null;

        if (date != null) {
            imageData = getImageData(date);
        }

        return imageData;
    }

    private ImageData getImageData(String date) {
        ImageData imageData = null;

        try {
            final int sleepTime = 100;
            publishProgress(0);

            String urlString = String.format("%s&date=%s", API_URL, date);
            Log.i(LOG_TAG, urlString);

            URL url = new URL(urlString);

            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            InputStream response = connection.getInputStream();
            publishProgress(25);
            Thread.sleep(sleepTime);

            BufferedReader reader = new BufferedReader(new InputStreamReader(response, "UTF-8"));
            StringBuilder sb = new StringBuilder();

            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }

            String result = sb.toString();

            publishProgress(50);
            Thread.sleep(sleepTime);

            JSONObject jsonData = new JSONObject(result);

            final int progressStep = jsonData.has("hdurl") ? 50 / 5 : 50 / 4;

            String imageDate = jsonData.getString("date");
            publishProgress(50 + progressStep);
            Thread.sleep(sleepTime);

            String imageTitle = jsonData.getString("title");
            publishProgress(50 + progressStep * 2);
            Thread.sleep(sleepTime);

            String imageExplanation = jsonData.getString("explanation");
            publishProgress(50 + progressStep * 3);
            Thread.sleep(sleepTime);

            String imageUrl = jsonData.getString("url");
            publishProgress(50 + progressStep * 4);
            Thread.sleep(sleepTime);

            String imageHdUrl = "";
            if (jsonData.has("hdurl")) {
                imageHdUrl = jsonData.getString("hdurl");
                publishProgress(50 + progressStep * 5);
            }

            imageData = new ImageData(imageDate, imageTitle, imageExplanation, imageUrl, imageHdUrl);

            reader.close();
            response.close();
            connection.disconnect();
        }
        catch (IOException | JSONException | InterruptedException e) {
            e.printStackTrace();

            errorMessage = e.getMessage();
        }

        return imageData;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);

        int progress = values[0];

        progressBar.setProgress(progress);
        progressBar.setVisibility(ProgressBar.VISIBLE);

        Log.v(LOG_TAG, "Progress: " + progress);
    }

    @Override
    protected void onPostExecute(ImageData imageData) {
        super.onPostExecute(imageData);

        if (imageData != null) {
            imagesAdapter.add(imageData);
            imagesAdapter.notifyDataSetChanged();
        }
        else {
            Log.i(LOG_TAG, "ImageData was not set: " + errorMessage);

            if (errorMessage == null) {
                errorMessage = "Unknown error.";
            }

            Toast.makeText(context, "Image could not be retrieved: " + errorMessage,
                    Toast.LENGTH_LONG)
                    .show();
        }

        progressBar.setVisibility(ProgressBar.GONE);
    }
}
