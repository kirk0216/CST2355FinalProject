package com.example.nasaiotd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import java.io.File;

public class SettingsActivity extends ActivityBase {

    private static final String[] IMAGE_TYPES = new String[] { "png", "jpg", "jpeg", "gif" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setupNavigation(R.string.SettingsTitle);

        updateImagesCount();
    }

    private void updateImagesCount() {
        File directory = getBaseContext().getFilesDir();

        int count = 0;

        for (File file : directory.listFiles()) {
            if (file.isFile()) {
                String fileExtension = MimeTypeMap.getFileExtensionFromUrl(file.getAbsolutePath());

                for (String extension : IMAGE_TYPES) {
                    if (fileExtension.equalsIgnoreCase(extension)) {
                        count++;
                        break;
                    }
                }
            }
        }

        TextView cachedImageCount = findViewById(R.id.SettingsCachedImageCount);
        cachedImageCount.setText(String.valueOf(count));
    }

    @Override
    protected void showHelp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Press ");
        stringBuilder.append(getResources().getString(R.string.SettingsDeleteCachedImagesText));
        stringBuilder.append(" to delete all cached images.");
        stringBuilder.append("\n\n");

        stringBuilder.append("Press ");
        stringBuilder.append(getResources().getString(R.string.SettingsDeleteSavedDataText));
        stringBuilder.append(" to delete all saved data (including cached images).");

        builder
                .setTitle(R.string.HelpTitle)
                .setMessage(stringBuilder.toString())
                .setPositiveButton(R.string.HelpOkay, (click, arg) -> {

                })
                .create()
                .show();
    }
}