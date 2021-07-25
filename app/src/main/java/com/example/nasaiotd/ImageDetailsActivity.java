package com.example.nasaiotd;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.FileInputStream;
import java.io.IOException;

public class ImageDetailsActivity extends ActivityBase {

    private static final String LOG_TAG = "IMAGE_DETAILS_ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_details);

        setupNavigation(R.string.DetailsTitle);

        Bundle data = getIntent().getExtras();
        String urlString = data.getString("url");
        String hdUrlString = data.getString("hdUrl");

        ImageView image = findViewById(R.id.ImageDetailsImage);
        Bitmap bitmap = loadImage(urlString);
        image.setImageBitmap(bitmap);

        TextView dateText = findViewById(R.id.ImageDetailsDate);
        dateText.setText(data.getString("date"));

        TextView titleText = findViewById(R.id.ImageDetailsTitle);
        titleText.setText(data.getString("title"));

        TextView urlText = findViewById(R.id.ImageDetailsUrl);
        urlText.setText(urlString);

        urlText.setOnClickListener(v -> {
            openInBrowser(urlString);
        });

        TextView hdUrlText = findViewById(R.id.ImageDetailsHdUrl);
        hdUrlText.setText(hdUrlString);

        if (hdUrlString != null && hdUrlString.length() > 0) {
            hdUrlText.setOnClickListener(v -> {
                openInBrowser(hdUrlString);
            });
        }

        TextView explanationText = findViewById(R.id.ImageDetailsExplanation);
        explanationText.setText(data.getString("explanation"));
    }

    private void openInBrowser(String urlString) {
        Uri uri = Uri.parse(urlString);

        Intent intent = new Intent(Intent.ACTION_VIEW, uri);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private Bitmap loadImage(String urlString) {
        Bitmap image = null;
        FileInputStream inputStream = null;
        String fileName = URLUtil.guessFileName(urlString, null, null);

        try {
            inputStream = openFileInput(fileName);
            image = BitmapFactory.decodeStream(inputStream);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return image;
    }

    @Override
    protected void showHelp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Details about the selected image are provided");
        stringBuilder.append("\n\n");

        stringBuilder.append("- Image title");
        stringBuilder.append("\n\n");

        stringBuilder.append("- Date it was the image of the day");
        stringBuilder.append("\n\n");

        stringBuilder.append("- URL for the image");
        stringBuilder.append("\n\n");

        stringBuilder.append("- URL for a high-quality version of the image (if available)");
        stringBuilder.append("\n\n");

        stringBuilder.append("- Explanation of the image");
        stringBuilder.append("\n\n");

        stringBuilder.append("The URL and HD URL can be clicked to open the image in your browser.");

        builder
                .setTitle(R.string.HelpTitle)
                .setMessage(stringBuilder.toString())
                .setPositiveButton(R.string.HelpOkay, (click, arg) -> {

                })
                .create()
                .show();
    }
}