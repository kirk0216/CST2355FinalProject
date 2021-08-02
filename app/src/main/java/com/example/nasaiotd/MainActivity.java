package com.example.nasaiotd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;
import java.util.List;

public class MainActivity extends ActivityBase {

    private static final String LOG_TAG = "NASA_MAIN";

    /**
     * An instance of ImagesAdapter that backs ImagesList.
     */
    private ImagesAdapter imagesAdapter;

    private DrawerLayout navigationDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupNavigation(R.string.MainTitle);

        final ImageDao imageDao = new ImageDao(this);
        List<ImageData> images = imageDao.load();

        imagesAdapter = new ImagesAdapter(getLayoutInflater(), R.layout.imagelist_item);
        imagesAdapter.addRange(images);

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
            detailsBundle.putLong("id", imageData.getId());

            Intent intent = new Intent(MainActivity.this, ImageDetailsActivity.class);
            intent.putExtras(detailsBundle);

            startActivity(intent);
        });

        SharedPreferences preferences =
            getSharedPreferences(getString(R.string.Preferences), Context.MODE_PRIVATE);
        final String lastDate = preferences
            .getString(getString(R.string.SavedDateKey), getCurrentDateString());

        EditText dateInput = findViewById(R.id.DateInput);
        dateInput.setText(lastDate);

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

            SharedPreferences.Editor editor =
                getSharedPreferences(getString(R.string.Preferences), Context.MODE_PRIVATE)
                    .edit();

            editor.putString(getString(R.string.SavedDateKey), date)
                .apply();

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

    @Override
    protected void showHelp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Enter a date into the input field in YYYY-MM-DD format.");
        stringBuilder.append("\n\n");

        stringBuilder.append("Press ");
        stringBuilder.append(getResources().getString(R.string.SelectDateButtonText));
        stringBuilder.append(" to show a calendar for selecting a date.");
        stringBuilder.append("\n\n");

        stringBuilder.append("Press ");
        stringBuilder.append(getResources().getString(R.string.FetchImageButtonText));
        stringBuilder.append(" to fetch the image for the selected date.");
        stringBuilder.append("\n\n");

        stringBuilder.append("Click on an image to go to its details.");
        stringBuilder.append("\n\n");

        stringBuilder.append("Long click on an image to delete it.");

        builder
                .setTitle(R.string.HelpTitle)
                .setMessage(stringBuilder.toString())
                .setPositiveButton(R.string.HelpOkay, (click, arg) -> {

                })
                .create()
                .show();
    }
}