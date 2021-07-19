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
    private final ImagesAdapter imagesAdapter;
    private String errorMessage;

    public NasaApiQuery(Activity context, ImagesAdapter imagesAdapter) {
        this.context = context;
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
            String urlString = String.format("%s&date=%s", API_URL, date);
            Log.i(LOG_TAG, urlString);

            URL url = new URL(urlString);

            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            InputStream response = connection.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(response, "UTF-8"));
            StringBuilder sb = new StringBuilder();

            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }

            String result = sb.toString();

            JSONObject jsonData = new JSONObject(result);

            final int progressStep = jsonData.has("hdurl") ? 100 / 6 : 100 / 5;

            String imageDate = jsonData.getString("date");
            publishProgress(progressStep);

            String imageTitle = jsonData.getString("title");
            publishProgress(progressStep * 2);

            String imageExplanation = jsonData.getString("explanation");
            publishProgress(progressStep * 3);

            String imageUrl = jsonData.getString("url");
            publishProgress(progressStep * 4);

            String imageHdUrl = "";
            if (jsonData.has("hdurl")) {
                imageHdUrl = jsonData.getString("hdurl");
                publishProgress(progressStep * 5);
            }

            imageData = new ImageData(imageDate, imageTitle, imageExplanation, imageUrl, imageHdUrl);

            reader.close();
            response.close();
            connection.disconnect();
        }
        catch (IOException | JSONException e) {
            e.printStackTrace();

            errorMessage = e.getMessage();
        }

        return imageData;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);

        int progress = values[0];

        ProgressBar progressBar = context.findViewById(R.id.SelectDateProgressBar);
        progressBar.setProgress(progress);
        progressBar.setVisibility(ProgressBar.VISIBLE);
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

        ProgressBar progressBar = context.findViewById(R.id.SelectDateProgressBar);
        progressBar.setVisibility(ProgressBar.GONE);
    }
}
