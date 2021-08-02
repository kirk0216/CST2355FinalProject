package com.example.nasaiotd;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.FileInputStream;
import java.io.IOException;

public final class ImageUtils {
    /**
     * Retrieves an image from the local file system.
     * @param fileName The name of the saved image file.
     * @return Bitmap image.
     */
    public static Bitmap getImageFromLocal(Context context, String fileName) {
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
}
