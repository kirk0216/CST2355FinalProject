package com.example.nasaiotd;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private ImagesAdapter imagesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imagesAdapter = new ImagesAdapter(getLayoutInflater());

        ListView imagesList = findViewById(R.id.ImageList);
        imagesList.setAdapter(imagesAdapter);

        imagesList.setOnItemClickListener((list, view, position, id) -> {
            ImageData imageData = (ImageData) imagesAdapter.getItem(position);

            Bundle detailsBundle = new Bundle();
            detailsBundle.putString("date", imageData.getDate());
            detailsBundle.putString("title", imageData.getTitle());
            detailsBundle.putString("url", imageData.getUrl());
            detailsBundle.putString("hdUrl", imageData.getHdUrl());
            detailsBundle.putString("explanation", imageData.getExplanation());

            Intent intent = new Intent(MainActivity.this, ImageDetailsActivity.class);
            intent.putExtras(detailsBundle);

            startActivity(intent);
        });

        Button selectDateButton = findViewById(R.id.SelectDateButton);
        selectDateButton.setOnClickListener(v -> {
            DialogFragment fragment = new DatePickerFragment(s -> {
                NasaApiQuery query = new NasaApiQuery(this, imagesAdapter);
                query.execute(s);
            });

            fragment.show(getSupportFragmentManager(), "datePicker");
        });
    }
}