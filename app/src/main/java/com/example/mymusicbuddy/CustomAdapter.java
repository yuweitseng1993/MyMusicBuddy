package com.example.mymusicbuddy;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;

public class CustomAdapter extends RecyclerView.Adapter<CustomViewHolder> {
    private ResultPojo dataSet;
    private CustomListener listener;
    private static final String TAG = "CustomAdapter";

    public void setDataSet(ResultPojo dataSet){
//        Log.d(TAG, "setDataSet: " + Arrays.toString(dataSet.results.toArray()));
        this.dataSet = dataSet;
        notifyDataSetChanged();
    }

    public void setListener(CustomListener listener) {
//        Log.d(TAG, "setListener: ");
        this.listener = listener;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        Log.d(TAG, "onCreateViewHolder: ");
        return new CustomViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.diplay_music_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
//        Log.d(TAG, "onBindViewHolder: ");
        holder.onBindViewHolder(dataSet.results.get(position), listener);
    }

    @Override
    public int getItemCount() {
//        Log.d(TAG, "getItemCount: ");
        return dataSet != null ? dataSet.results.size() : 0;
    }
}
