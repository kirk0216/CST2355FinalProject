package com.example.nasaiotd;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    /**
     * An instance of ImagesAdapter that backs ImagesList.
     */
    private ImagesAdapter imagesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imagesAdapter = new ImagesAdapter(getLayoutInflater());

        ListView imagesList = findViewById(R.id.ImageList);
        imagesList.setAdapter(imagesAdapter);

        imagesList.setOnItemLongClickListener((list, view, position, id) -> {
            ImageData image = (ImageData)imagesAdapter.getItem(position);

            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
            alertBuilder
                    .setTitle(R.string.ImageListDeleteTitle)
                    .setMessage(image.getTitle())
                    .setPositiveButton(R.string.ImageListDeleteYes, (click, arg) -> {
                        imagesAdapter.removeAt(position);
                        imagesAdapter.notifyDataSetChanged();

                        Snackbar
                            .make(imagesList, image.getTitle() + " deleted.", Snackbar.LENGTH_SHORT)
                            .setAction(R.string.ImageListDeleteUndo, v -> {
                                imagesAdapter.insertAt(position, image);
                                imagesAdapter.notifyDataSetChanged();
                            })
                            .show();
                    })
                    .setNegativeButton(R.string.ImageListDeleteNo, (click, arg) -> {

                    })
                    .create()
                    .show();

            return true;
        });

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

        EditText dateInput = findViewById(R.id.DateInput);
        dateInput.setText(getCurrentDateString());

        Button selectDateButton = findViewById(R.id.SelectDateButton);
        selectDateButton.setOnClickListener(v -> {
            DialogFragment fragment = new DatePickerFragment(s -> {
                dateInput.setText(s);
                hideKeyboard();
            });

            Bundle dateData = new Bundle();
            dateData.putString("date", dateInput.getText().toString());
            fragment.setArguments(dateData);

            fragment.show(getSupportFragmentManager(), "datePicker");
        });

        Button fetchImageButton = findViewById(R.id.FetchImageButton);
        fetchImageButton.setOnClickListener(v -> {
            String date = dateInput.getText().toString();
            hideKeyboard();

            NasaApiQuery query = new NasaApiQuery(this, imagesAdapter);
            query.execute(date);
        });
    }

    /**
     * @return The current date in "YYYY-MM-DD" format.
     */
    private String getCurrentDateString() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        return String.format("%d-%02d-%02d", year, month + 1, day);
    }

    /**
     * Hides the software keyboard.
     * https://stackoverflow.com/a/17789187
     */
    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = getCurrentFocus();

        if (view == null) {
            view = new View(this);
        }

        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}