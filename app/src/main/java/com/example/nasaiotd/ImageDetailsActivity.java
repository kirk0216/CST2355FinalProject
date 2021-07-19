package com.example.nasaiotd;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class ImageDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_details);

        Bundle data = getIntent().getExtras();

        TextView dateText = findViewById(R.id.ImageDetailsDate);
        dateText.setText(data.getString("date"));

        TextView titleText = findViewById(R.id.ImageDetailsTitle);
        titleText.setText(data.getString("title"));

        TextView urlText = findViewById(R.id.ImageDetailsUrl);
        urlText.setText(data.getString("url"));

        TextView hdUrlText = findViewById(R.id.ImageDetailsHdUrl);
        hdUrlText.setText(data.getString("hdUrl"));

        TextView explanationText = findViewById(R.id.ImageDetailsExplanation);
        explanationText.setText(data.getString("explanation"));
    }
}