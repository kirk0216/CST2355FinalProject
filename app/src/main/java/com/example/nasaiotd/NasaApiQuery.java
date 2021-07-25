package com.example.nasaiotd;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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

    /**
     * Tag used to filter during logging.
     */
    private static final String LOG_TAG = "NASA_API_QUERY";
    /**
     * NASA Image of the Day API URL
     */
    private static final String API_URL = "https://api.nasa.gov/planetary/apod?api_key=AD83pecRZgvpNZRi1pfDdCruHXIvCV7KGjBI2j0B&thumbs=true";

    /**
     * Activity that is using the API. Used to show Toast and access file system.
     */
    private final Activity context;
    /**
     * Reference to the progress bar associated with this request, so the UI can be updated.
     */
    private final ProgressBar progressBar;
    private final ImagesAdapter imagesAdapter;
    /**
     * A string containing any error messages, so the user can be advised of errors.
     */
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

    /**
     * Retrieves image data from the NASA API, and downloads the associated image.
     * @param date The requested date to query for.
     * @return An ImageData object representing the information from the API.
     */
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
                sb.append(line);
                sb.append("\n");
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

            String mediaType = jsonData.getString("media_type");
            Bitmap image = null;

            if (mediaType.equalsIgnoreCase("image")) {
                image = retrieveImage(imageData.getUrl(), imageData.getDate());
            }
            else {
                if (jsonData.has("thumbnail_url")) {
                    Log.i(LOG_TAG, "Fetching thumbnail for type: " + mediaType);

                    String thumbnailUrl = jsonData.getString("thumbnail_url");
                    image = retrieveImage(thumbnailUrl, imageData.getDate());
                }
            }

            if (image != null) {
                imageData.setImage(image);
            }

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

    /**
     * Retrieves an image from either a remote source or local storage.
     * @param urlString The URL for the image.
     * @return A bitmap image.
     */
    private Bitmap retrieveImage(String urlString, String date) {
        Bitmap image = null;
        Log.i(LOG_TAG, "Loading: " + urlString);

        // https://stackoverflow.com/a/49690119
        String extension = MimeTypeMap.getFileExtensionFromUrl(urlString);
        String fileName = date + "." + extension;

        if (fileExists(fileName)) {
            Log.i(LOG_TAG, "Loading " + fileName + " from local storage.");
            image = getImageFromLocal(fileName);
        }
        else {
            Log.i(LOG_TAG, "Loading " + fileName + " from remote source.");
            image = getImageFromRemote(urlString, fileName);
        }

        return image;
    }

    /**
     * Retrieves an image from the local file system.
     * @param fileName The name of the saved image file.
     * @return Bitmap image.
     */
    private Bitmap getImageFromLocal(String fileName) {
        Bitmap image = null;
        FileInputStream inputStream = null;

        try {
            inputStream = context.openFileInput(fileName);
            image = BitmapFactory.decodeStream(inputStream);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return image;
    }

    /**
     * Retrieves an image from the specified URL.
     * @param urlString The remote URL where the image is located.
     * @return The bitmap located at the specified URL.
     */
    private Bitmap getImageFromRemote(String urlString, String fileName) {
        Bitmap image = null;

        try {
            URL url = new URL(urlString);

            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.connect();

            int responseCode = connection.getResponseCode();

            if (responseCode == 200) {
                image = BitmapFactory.decodeStream(connection.getInputStream());
            }

            if (image != null) {
                saveImage(fileName, image);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return image;
    }

    /**
     * Saves the provided bitmap image to local storage.
     * @param fileName The file name to save the image at.
     * @param image The bitmap image that will be saved.
     */
    private void saveImage(String fileName, Bitmap image) {
        try {
            FileOutputStream outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            image.compress(Bitmap.CompressFormat.PNG, 80, outputStream);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if a file exists in local storage.
     * @param fileName The name of the file.
     * @return True if file exists, false if it does not.
     */
    private boolean fileExists(String fileName) {
        File file = context.getBaseContext().getFileStreamPath(fileName);
        return file.exists();
    }
}
