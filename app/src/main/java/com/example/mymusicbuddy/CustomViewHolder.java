package com.example.mymusicbuddy;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class CustomViewHolder extends RecyclerView.ViewHolder {
    ImageView iv_track_pic;
    TextView tv_artist, tv_collection, tv_price;
    private static final String TAG = "CustomViewHolder";

    public CustomViewHolder(@NonNull View itemView) {
        super(itemView);
        iv_track_pic = itemView.findViewById(R.id.iv_album_pic);
        tv_artist = itemView.findViewById(R.id.tv_artist_name);
        tv_collection = itemView.findViewById(R.id.tv_collection_name);
        tv_price = itemView.findViewById(R.id.tv_track_price);
    }

    public void onBindViewHolder(final TrackPojo item, final CustomListener listener){
        tv_artist.setText(Html.fromHtml("<b>" + item.artistName +"</b>"));
        tv_collection.setText(Html.fromHtml("<b>" + item.collectionName +"</b>"));
        String priceTag = item.trackPrice + " " + item.currency;
        tv_price.setText(priceTag);

        if(HomeScreen.getInternetStatus()){
            Log.d(TAG, "onBindViewHolder : internet exists");
            Picasso.get().load(item.artworkUrl100).into(iv_track_pic);
        }
        else{
            Log.d(TAG, "onBindViewHolder: no internet");
//            loadImageFromStorage(item.artworkUrl100);
            iv_track_pic.setImageResource(android.R.drawable.ic_menu_report_image);
        }

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onclick(item);
            }
        });
    }

    private void loadImageFromStorage(String path)
    {
        try {
            File f = new File(path);
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            iv_track_pic.setImageBitmap(b);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

    }
}
