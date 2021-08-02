package com.example.nasaiotd;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

/**
 * Class used to access and manage ImageData instances that are stored in a database.
 */
public class ImageDao {
    /**
     * The name of the database file that will be used.
     */
    public static final String DATABASE_NAME = "NasaIotdDatabase";

    /**
     * The name of the table that contains images.
     */
    private static final String TABLE_NAME = "IMAGE";
    /**
     * The name of the column that stores the id.
     */
    private static final String COLUMN_ID = "id";
    /**
     * The name of the column that stores the date.
     */
    private static final String COLUMN_DATE = "date";
    /**
     * The name of the column that stores the title.
     */
    private static final String COLUMN_TITLE = "title";
    /**
     * The name of the column that stores the explanation.
     */
    private static final String COLUMN_EXPLANATION = "explanation";
    /**
     * The name of the column that stores the URL.
     */
    private static final String COLUMN_URL = "url";
    /**
     * The name of the column that stores the HD URL.
     */
    private static final String COLUMN_HDURL = "hdUrl";

    /**
     * An array containing all column names.
     */
    private static final String[] COLUMNS = {
            COLUMN_ID, COLUMN_DATE, COLUMN_TITLE, COLUMN_EXPLANATION,
            COLUMN_URL, COLUMN_HDURL
    };

    /**
     * Context object used to access the database.
     */
    private final Context context;
    /**
     * Helper class for opening database connections.
     */
    private final DatabaseOpener dbOpener;

    public ImageDao(Context context) {
        this.context = context;
        dbOpener = new DatabaseOpener(context);
    }

    /**
     * Locates and returns an ImageData object from the database based on id.
     * @param id The id of the ImageData object.
     * @return ImageData instance.
     */
    public ImageData get(long id) {
        return find("id = ?", String.valueOf(id));
    }

    /**
     * Locates and returns an ImageData object based on the provided filter.
     * @param whereClause The SQL WHERE clause used to filter the objects.
     * @param whereArgs Arguments to be provided for SQL parameters in the WHERE clause.
     * @return ImageData instance.
     */
    public ImageData find(String whereClause, String... whereArgs) {
        final SQLiteDatabase db = dbOpener.getWritableDatabase();

        Cursor result = db.query(false, TABLE_NAME, COLUMNS,
                whereClause, whereArgs, null, null, null, null);

        ImageData image = null;

        if (result.getCount() > 0) {
            final int idIndex = result.getColumnIndex(COLUMN_ID);
            final int dateIndex = result.getColumnIndex(COLUMN_DATE);
            final int titleIndex = result.getColumnIndex(COLUMN_TITLE);
            final int explanationIndex = result.getColumnIndex(COLUMN_EXPLANATION);
            final int urlIndex = result.getColumnIndex(COLUMN_URL);
            final int hdUrlIndex = result.getColumnIndex(COLUMN_HDURL);

            result.moveToFirst();

            long identifier = result.getLong(idIndex);
            String date = result.getString(dateIndex);
            String title = result.getString(titleIndex);
            String explanation = result.getString(explanationIndex);
            String url = result.getString(urlIndex);
            String hdUrl = result.getString(hdUrlIndex);

            image = new ImageData(identifier, date, title, explanation, url, hdUrl);

            String fileName = image.getFileName();

            Bitmap bitmap = ImageUtils.getImageFromLocal(context, fileName);
            image.setImage(bitmap);
        }

        result.close();
        db.close();

        return image;
    }

    /**
     * Loads all ImageData instances from the database.
     * @return List of ImageData instances.
     */
    public List<ImageData> load() {
        final SQLiteDatabase db = dbOpener.getWritableDatabase();

        Cursor result = db.query(false, TABLE_NAME, COLUMNS,
                null, null, null, null, null, null);

        List<ImageData> imageList = new ArrayList<ImageData>();

        if (result.getCount() > 0) {
            final int idIndex = result.getColumnIndex(COLUMN_ID);
            final int dateIndex = result.getColumnIndex(COLUMN_DATE);
            final int titleIndex = result.getColumnIndex(COLUMN_TITLE);
            final int explanationIndex = result.getColumnIndex(COLUMN_EXPLANATION);
            final int urlIndex = result.getColumnIndex(COLUMN_URL);
            final int hdUrlIndex = result.getColumnIndex(COLUMN_HDURL);

            while (result.moveToNext()) {
                long identifier = result.getLong(idIndex);
                String date = result.getString(dateIndex);
                String title = result.getString(titleIndex);
                String explanation = result.getString(explanationIndex);
                String url = result.getString(urlIndex);
                String hdUrl = result.getString(hdUrlIndex);

                ImageData image = new ImageData(identifier, date, title, explanation, url, hdUrl);
                imageList.add(image);

                String fileName = image.getFileName();

                Bitmap bitmap = ImageUtils.getImageFromLocal(context, fileName);
                image.setImage(bitmap);
            }
        }

        result.close();
        db.close();

        return imageList;
    }

    /**
     * Saves an ImageData instance to the database.
     * @param image The ImageData instance to save.
     * @return The id returned from SQLite for the saved instance.
     */
    public long save(ImageData image) {
        final SQLiteDatabase db = dbOpener.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_DATE, image.getDate());
        contentValues.put(COLUMN_TITLE, image.getTitle());
        contentValues.put(COLUMN_EXPLANATION, image.getExplanation());
        contentValues.put(COLUMN_URL, image.getUrl());
        contentValues.put(COLUMN_HDURL, image.getHdUrl());

        long id = image.getId();

        if (id == 0) {
            id = db.insert(TABLE_NAME, null, contentValues);
        }
        else {
            contentValues.put(COLUMN_ID, image.getId());
            db.insert(TABLE_NAME, null, contentValues);
        }

        db.close();

        return id;
    }

    /**
     * Deletes an ImageData instance from the database.
     * @param image The ImageData instance to be deleted.
     */
    public void delete(ImageData image) {
        final SQLiteDatabase db = dbOpener.getWritableDatabase();
        db.delete(TABLE_NAME, "id = ?", new String[] { String.valueOf(image.getId()) });
        db.close();
    }

    private class DatabaseOpener extends SQLiteOpenHelper {

        /**
         * The current version of the database.
         */
        private static final int DATABASE_VERSION = 1;

        public DatabaseOpener(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            final String sql = String.format("CREATE TABLE %s (" +
                            "%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "%s TEXT NOT NULL, " +
                            "%s TEXT NOT NULL, " +
                            "%s TEXT NOT NULL, " +
                            "%s TEXT NOT NULL, " +
                            "%s TEXT NOT NULL);",
                    TABLE_NAME, COLUMN_ID, COLUMN_DATE, COLUMN_TITLE, COLUMN_EXPLANATION,
                    COLUMN_URL, COLUMN_HDURL);
            db.execSQL(sql);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            final String sql = String.format("DROP TABLE IF EXISTS %s;", TABLE_NAME);
            db.execSQL(sql);

            onCreate(db);
        }
    }
}
