package com.example.mymusicbuddy;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PlaceholderFragment extends Fragment implements CustomListener{

    private static final String ARG_SECTION_NUMBER = "section_number";
    private PageViewModel pageViewModel;
    RecyclerView recyclerView;
    CustomAdapter adapter;
    SwipeRefreshLayout swipeRefreshLayout ;
    private String tabIndex;
    private static final String TAG = "PlaceholderFragment";


    public static PlaceholderFragment newInstance(int index) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        Log.d(TAG, "onCreate: index -> " + index);
        pageViewModel.setIndex(index);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {


        View root = inflater.inflate(R.layout.fragment_home_screen, container, false);
        recyclerView = root.findViewById(R.id.rv_track_container);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        pageViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                Log.d(TAG, "onChanged: String s -> " + s);
                tabIndex = s;
                if(HomeScreen.getInternetStatus()){
                    Log.d(TAG, "onChanged: internet connection exists");
                    initRetrofit(Integer.parseInt(s));
                }
                else{
                    Log.d(TAG, "onChanged: no internet connection");
                    adapter = new CustomAdapter();
                    if(tabIndex.equals("1")){
                        adapter.setDataSet(loadMusicGenere("rock"));
                    }
                    else if(tabIndex.equals("2")){
                        adapter.setDataSet(loadMusicGenere("classic"));
                    }
                    else if(tabIndex.equals("3")){
                        adapter.setDataSet(loadMusicGenere("pop"));
                    }
                    adapter.setListener(PlaceholderFragment.this);
                    recyclerView.setAdapter(adapter);
                }
            }
        });
        swipeRefreshLayout = root.findViewById(R.id.sr_refresh_view);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initRetrofit(Integer.parseInt(tabIndex));
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        return root;
    }

    Callback<ResultPojo> callback = new Callback<ResultPojo>() {
        @Override
        public void onResponse(Call<ResultPojo> call, Response<ResultPojo> response) {
            if(response.isSuccessful()){
                adapter = new CustomAdapter();
                adapter.setDataSet(response.body());
                adapter.setListener(PlaceholderFragment.this);
                recyclerView.setAdapter(adapter);
                if(tabIndex.equals("1") && !ifTableFilled(tabIndex) ||
                        tabIndex.equals("2") && !ifTableFilled(tabIndex) ||
                        tabIndex.equals("3") && !ifTableFilled(tabIndex)){
                    new SaveToInternalStorage().execute(response.body().results);
                }
            }
        }

        @Override
        public void onFailure(Call<ResultPojo> call, Throwable t) {
            Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    public void initRetrofit(int index){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://itunes.apple.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        if(index == 1){
            apiInterface.getTrackInfo("rock", "music", "song", "50").enqueue(callback);
        }
        else if(index == 2){
            apiInterface.getTrackInfo("classick", "music", "song", "50").enqueue(callback);
        }
        else{
            apiInterface.getTrackInfo("pop", "music", "song", "50").enqueue(callback);
        }
    }

    @Override
    public void onclick(TrackPojo item) {
        if(HomeScreen.getInternetStatus()){
            Uri vidUri = Uri.parse(item.previewUrl);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(vidUri, "audio/*");
            startActivity(intent);
        }
        else{
            Toast.makeText(getContext(), "Unable to load preview data without internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    public Boolean ifTableFilled(String index){
        MusicDatabase database = new MusicDatabase(getContext());
        SQLiteDatabase readableDB = database.getReadableDatabase();
        long rowCount;
        if(index.equals("1")){
            rowCount = DatabaseUtils.queryNumEntries(readableDB, DatabaseUtil.TrackTable.rockTableName);
        }
        else if(index.equals("2")){
            rowCount = DatabaseUtils.queryNumEntries(readableDB, DatabaseUtil.TrackTable.classicTableName);
        }
        else{
            rowCount = DatabaseUtils.queryNumEntries(readableDB, DatabaseUtil.TrackTable.popTableName);
        }
        Log.d(TAG, "ifTableFilled: rowCount -> " + rowCount);
        if(rowCount < 50){
            return false;
        }
        return true;
    }

    public ResultPojo loadMusicGenere(String index){
        MusicDatabase database = new MusicDatabase(getContext());
        SQLiteDatabase readableDB = database.getReadableDatabase();
        List<TrackPojo> newSet = new ArrayList<>();
        Cursor cursor;
        ResultPojo resultPojo;
        if(index.equals("1")){
            cursor = readableDB.query(DatabaseUtil.TrackTable.rockTableName, null, null, null, null, null, null, null);
        }
        else if(index.equals("2")){
            cursor = readableDB.query(DatabaseUtil.TrackTable.classicTableName, null, null, null, null, null, null, null);
        }
        else{
            cursor = readableDB.query(DatabaseUtil.TrackTable.popTableName, null, null, null, null, null, null, null);
        }
        while(cursor.moveToNext()){
            String artistName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseUtil.TrackTable.artistColumn));
            String collectionName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseUtil.TrackTable.collectionColumn));
            String imagePath = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseUtil.TrackTable.imageColumn));
            Double price = Double.valueOf((cursor.getString(cursor.getColumnIndexOrThrow(DatabaseUtil.TrackTable.priceColumn))).split(" ")[0]);
            String currency = (cursor.getString(cursor.getColumnIndexOrThrow(DatabaseUtil.TrackTable.priceColumn))).split(" ")[1];
            newSet.add(new TrackPojo(artistName, collectionName, imagePath, null, price, currency));
        }
        resultPojo = new ResultPojo(newSet.size(), newSet);
        Log.d(TAG, "loadMusicGenere: newSet.size() -> " + newSet.size());
        readableDB.close();
        return resultPojo;
    }

    class SaveToInternalStorage extends AsyncTask<List<TrackPojo>, Void, Void>{

        @Override
        protected Void doInBackground(List<TrackPojo>... lists) {
            MusicDatabase database = new MusicDatabase(getContext());
            SQLiteDatabase saveData = database.getWritableDatabase();
            ContentValues values = new ContentValues();
            Log.d(TAG, "doInBackground: lists[0] -> " + lists[0]);
            for(int i = 0; i < (lists[0]).size(); i++){
//            for(TrackPojo item : lists[0]){
//                values.put(DatabaseUtil.TrackTable.artistColumn, item.artistName);
//                values.put(DatabaseUtil.TrackTable.collectionColumn, item.collectionName);
//                values.put(DatabaseUtil.TrackTable.priceColumn, item.trackPrice + " " + item.currency);
//                values.put(DatabaseUtil.TrackTable.imageColumn, item.artworkUrl100);
                values.put(DatabaseUtil.TrackTable.artistColumn, lists[0].get(i).artistName);
                values.put(DatabaseUtil.TrackTable.collectionColumn, lists[0].get(i).collectionName);
                values.put(DatabaseUtil.TrackTable.priceColumn, lists[0].get(i).trackPrice + " " + lists[0].get(i).currency);
                values.put(DatabaseUtil.TrackTable.imageColumn, lists[0].get(i).artworkUrl100);
//                Bitmap imgBitmap;
//                try{
//                    URL url = new URL(item.artworkUrl100);
//                    URLConnection connection = url.openConnection();
//                    connection.setDoInput(true);
//                    InputStream input = connection.getInputStream();
//                    imgBitmap = BitmapFactory.decodeStream(input);
//                } catch(IOException e){
//                    e.printStackTrace();
//                    return null;
//                }
//                ContextWrapper cw = new ContextWrapper(getContext());
//                File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
//                File mypath = new File(directory, item.artworkUrl100);
//
//                FileOutputStream fos = null;
//                try {
//                    fos = new FileOutputStream(mypath);
//                    imgBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                finally {
//                    try {
//                        fos.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//                values.put(DatabaseUtil.TrackTable.imageColumn, directory.getAbsolutePath());
                if(tabIndex.equals("1")){
                    if(saveData.insert(DatabaseUtil.TrackTable.rockTableName, null, values) > 0){
//                    Toast.makeText(getContext(), "rock track added successfully", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "doInBackground: rock track added successfully");
                    }
                    else{
                        Toast.makeText(getContext(), "ERROR: unable to add rock track to databaase", Toast.LENGTH_SHORT).show();
                    }
                }
                else if(tabIndex.equals("2")){
                    if(saveData.insert(DatabaseUtil.TrackTable.classicTableName, null, values) > 0){
//                    Toast.makeText(getContext(), "classic track added successfully", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "doInBackground: classic track added successfully");
                    }
                    else{
                        Toast.makeText(getContext(), "ERROR: unable to add classic track to databaase", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    if(saveData.insert(DatabaseUtil.TrackTable.popTableName, null, values) > 0){
//                    Toast.makeText(getContext(), "pop track added successfully", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "doInBackground: pop track added successfully");
                    }
                    else{
                        Toast.makeText(getContext(), "ERROR: unable to add pop track to databaase", Toast.LENGTH_SHORT).show();
                    }
                }
            }
//            Log.d(TAG, "doInBackground: tabIndex -> " + tabIndex);
            if(tabIndex.equals("1")){
                if(saveData.insert(DatabaseUtil.TrackTable.rockTableName, null, values) > 0){
//                    Toast.makeText(getContext(), "rock track added successfully", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "doInBackground: rock track added successfully");
                }
                else{
                    Toast.makeText(getContext(), "ERROR: unable to add rock track to databaase", Toast.LENGTH_SHORT).show();
                }
            }
            else if(tabIndex.equals("2")){
                if(saveData.insert(DatabaseUtil.TrackTable.classicTableName, null, values) > 0){
//                    Toast.makeText(getContext(), "classic track added successfully", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "doInBackground: classic track added successfully");
                }
                else{
                    Toast.makeText(getContext(), "ERROR: unable to add classic track to databaase", Toast.LENGTH_SHORT).show();
                }
            }
            else{
                if(saveData.insert(DatabaseUtil.TrackTable.popTableName, null, values) > 0){
//                    Toast.makeText(getContext(), "pop track added successfully", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "doInBackground: pop track added successfully");
                }
                else{
                    Toast.makeText(getContext(), "ERROR: unable to add pop track to databaase", Toast.LENGTH_SHORT).show();
                }
            }
            saveData.close();
            return null;
        }
    }
}