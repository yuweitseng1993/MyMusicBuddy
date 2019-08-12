package com.example.mymusicbuddy;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class MusicDatabase extends SQLiteOpenHelper {
    public MusicDatabase(@Nullable Context context) {
        super(context, DatabaseUtil.databaseName, null, DatabaseUtil.databaseVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + DatabaseUtil.TrackTable.rockTableName +
                " (" + DatabaseUtil.TrackTable.trackNum + " INTEGER PRIMARY KEY," +
                DatabaseUtil.TrackTable.artistColumn + " VARCHAR(255)," +
                DatabaseUtil.TrackTable.collectionColumn + " VARCHAR(255)," +
                DatabaseUtil.TrackTable.imageColumn + " VARCHAR(255)," +
                DatabaseUtil.TrackTable.priceColumn + " VARCHAR(255))" );

        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + DatabaseUtil.TrackTable.classicTableName +
                " (" + DatabaseUtil.TrackTable.trackNum + " INTEGER PRIMARY KEY," +
                DatabaseUtil.TrackTable.artistColumn + " VARCHAR(255)," +
                DatabaseUtil.TrackTable.collectionColumn + " VARCHAR(255)," +
                DatabaseUtil.TrackTable.imageColumn + " VARCHAR(255)," +
                DatabaseUtil.TrackTable.priceColumn + " VARCHAR(255))" );

        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + DatabaseUtil.TrackTable.popTableName +
                " (" + DatabaseUtil.TrackTable.trackNum + " INTEGER PRIMARY KEY," +
                DatabaseUtil.TrackTable.artistColumn + " VARCHAR(255)," +
                DatabaseUtil.TrackTable.collectionColumn + " VARCHAR(255)," +
                DatabaseUtil.TrackTable.imageColumn + " VARCHAR(255)," +
                DatabaseUtil.TrackTable.priceColumn + " VARCHAR(255))" );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
