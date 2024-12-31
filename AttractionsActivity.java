package com.example.resort;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AttractionsActivity extends AppCompatActivity {

    private RecyclerView attractionsRecyclerView;
    private AttractionAdapter attractionAdapter;
    private List<AttractionItem> attractionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attractions);

        setTitle("Attractions");

        attractionsRecyclerView = findViewById(R.id.attractionsRecyclerView);
        attractionList = new ArrayList<>();

        attractionAdapter = new AttractionAdapter(attractionList);
        attractionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        attractionsRecyclerView.setAdapter(attractionAdapter);

        populateAttractionsAsync();
    }

    private void populateAttractionsAsync() {
        new Thread(() -> {
            DatabaseHelper dbHelper = new DatabaseHelper(this);


            List<AttractionItem> attractions = dbHelper.getAllAttractions();

            runOnUiThread(() -> {
                if (attractions != null && !attractions.isEmpty()) {
                    attractionList.addAll(attractions);
                    attractionAdapter.notifyDataSetChanged();
                }
            });
        }).start();
    }


    private Bitmap getBitmapFromResource(int resourceId) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // Do not load into memory
        BitmapFactory.decodeResource(getResources(), resourceId, options);

        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;

        int maxWidth = 800;
        int maxHeight = 800;

        int inSampleSize = 1;
        if (imageHeight > maxHeight || imageWidth > maxWidth) {
            int halfHeight = imageHeight / 2;
            int halfWidth = imageWidth / 2;

            while ((halfHeight / inSampleSize) > maxHeight
                    && (halfWidth / inSampleSize) > maxWidth) {
                inSampleSize *= 2;
            }
        }

        options.inJustDecodeBounds = false;
        options.inSampleSize = inSampleSize;

        return BitmapFactory.decodeResource(getResources(), resourceId, options);
    }
}
