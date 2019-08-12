package com.example.mymusicbuddy;

import java.util.List;

public class ResultPojo {
    int resultCount;
    List<TrackPojo> results;

    ResultPojo(int count, List<TrackPojo> data){
        this.resultCount = count;
        this.results = data;
    }
}
