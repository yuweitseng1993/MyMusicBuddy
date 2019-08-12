package com.example.mymusicbuddy;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {
    //https://itunes.apple.com/search?term=rock&amp;media=music&amp;entity=song&amp;limit=50
    //https://itunes.apple.com/search?term=classick&amp;media=music&amp;entity=song&amp;limit=50
    //https://itunes.apple.com/search?term=pop&amp;media=music&amp;entity=song&amp;limit=50
    @GET("search/1?")
    Call<ResultPojo> getTrackInfo(
            @Query("term")String term,
            @Query("media")String media,
            @Query("entity")String entity,
            @Query("limit")String limit);

}
