package com.example.nocash.DAO;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "money_luv.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, getDatabasePath(context), null, DATABASE_VERSION);
    }

    private static String getDatabasePath(Context context) {
        // Get the directory for the app's private databases
        String path = context.getDatabasePath(DATABASE_NAME).getAbsolutePath();
        Log.d("DatabasePath", path);
        return path;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create your database tables here if needed
        // This method is called only once when the database is created
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database upgrades if needed
        // This method is called when the database needs to be upgraded
    }
}
