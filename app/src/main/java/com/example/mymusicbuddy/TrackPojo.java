package com.example.mymusicbuddy;

public class TrackPojo {
    String artistName;
    String collectionName;
    String artworkUrl100;
    String previewUrl;
    double trackPrice;
    String currency;

    TrackPojo(String a, String c, String i, String pre, double p, String cu){
        this.artistName = a;
        this.collectionName = c;
        this.artworkUrl100 = i;
        this.previewUrl = pre;
        this.trackPrice = p;
        this.currency = cu;
    }
}
