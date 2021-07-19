package com.example.nasaiotd;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

public class NasaApiQuery extends AsyncTask<String, Integer, ImageData> {

    private static final String LOG_TAG = "NASA_API_QUERY";
    private static final String API_URL = "https://api.nasa.gov/planetary/apod?api_key=AD83pecRZgvpNZRi1pfDdCruHXIvCV7KGjBI2j0B";

    private final ImagesAdapter imagesAdapter;

    public NasaApiQuery(ImagesAdapter imagesAdapter) {
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

            String imageDate = jsonData.getString("date");
            String imageTitle = jsonData.getString("title");
            String imageExplanation = jsonData.getString("explanation");
            String imageUrl = jsonData.getString("url");
            String imageHdUrl = jsonData.getString("hdurl");

            imageData = new ImageData(imageDate, imageTitle, imageExplanation, imageUrl, imageHdUrl);

            reader.close();
            response.close();
            connection.disconnect();
        }
        catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return imageData;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(ImageData imageData) {
        super.onPostExecute(imageData);

        imagesAdapter.add(imageData);
        imagesAdapter.notifyDataSetChanged();
    }
}
