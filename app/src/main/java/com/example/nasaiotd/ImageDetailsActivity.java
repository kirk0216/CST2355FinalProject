package com.example.nasaiotd;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.URLUtil;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.io.FileInputStream;
import java.io.IOException;

public class ImageDetailsActivity extends ActivityBase {

    private static final String LOG_TAG = "IMAGE_DETAILS_ACTIVITY";

    private ImageDetailsFragment detailsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_details);

        setupNavigation(R.string.DetailsTitle);

        Bundle data = getIntent().getExtras();

        FragmentManager fragmentManager = getSupportFragmentManager();

        detailsFragment = new ImageDetailsFragment();
        detailsFragment.setArguments(data);

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.DetailsFrame, detailsFragment);
        fragmentTransaction.commit();
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