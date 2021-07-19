package com.example.nasaiotd;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.FileInputStream;
import java.io.IOException;

public class ImageDetailsActivity extends AppCompatActivity {

    private static final String LOG_TAG = "IMAGE_DETAILS_ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_details);

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
}