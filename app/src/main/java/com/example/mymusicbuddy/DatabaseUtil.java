package com.example.mymusicbuddy;

public class DatabaseUtil {
    public static final String databaseName = "musicdb";
    public static final int databaseVersion = 1;

    public class TrackTable{
        public static final String rockTableName = "rock";
        public static final String classicTableName = "classic";
        public static final String popTableName = "pop";
        public static final String trackNum = "id";
        public static final String artistColumn = "artist";
        public static final String collectionColumn = "collection";
        public static final String imageColumn = "image";
        public static final String priceColumn = "price";
    }
}
