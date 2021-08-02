package com.example.nasaiotd;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class HomeActivity extends ActivityBase {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setupNavigation(R.string.HomeTitle);

        final String today = ImageUtils.getCurrentDateString();

        final ImageDao imageDao = new ImageDao(this);
        ImageData imageData = imageDao.find("date = ?", today);

        final SharedPreferences sharedPreferences =
                getSharedPreferences(getString(R.string.Preferences), Context.MODE_PRIVATE);

        boolean autoDownload = sharedPreferences
                .getBoolean(getString(R.string.AutoDownloadKey), true);

        final ImageView imageView = findViewById(R.id.HomeImage);
        final TextView titleView = findViewById(R.id.HomeImageTitle);

        if (imageData == null) {
            if (autoDownload) {
                final SingleImageResult imageResult = new SingleImageResult(image -> {
                    imageView.setImageBitmap(image.getImage());
                    titleView.setText(image.getTitle());
                });

                final NasaApiQuery nasaApi = new NasaApiQuery(this, imageResult);
                nasaApi.execute(today);
            } else {
                imageView.setVisibility(View.GONE);

                final Button downloadTodayButton = findViewById(R.id.DownloadTodayButton);
                downloadTodayButton.setOnClickListener(v -> {
                    final SingleImageResult imageResult = new SingleImageResult(image -> {
                        imageView.setImageBitmap(image.getImage());
                        titleView.setText(image.getTitle());

                        imageView.setVisibility(View.VISIBLE);
                        downloadTodayButton.setVisibility(View.GONE);
                    });

                    final NasaApiQuery nasaApi = new NasaApiQuery(this, imageResult);
                    nasaApi.execute(today);
                });

                downloadTodayButton.setVisibility(View.VISIBLE);
            }
        }
        else {
            imageView.setImageBitmap(imageData.getImage());
            titleView.setText(imageData.getTitle());
        }
    }

    @Override
    protected void showHelp() {

    }
}