package com.example.resort;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ServicesActivity extends AppCompatActivity implements ServiceAdapter.OnItemClickListener {

    private RecyclerView servicesRecyclerView;
    private ServiceAdapter serviceAdapter;
    private List<ServiceItem> serviceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services);

        servicesRecyclerView = findViewById(R.id.servicesRecyclerView);

        serviceList = new ArrayList<>();

        initializeServices();

        serviceAdapter = new ServiceAdapter(serviceList, this);
        servicesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        servicesRecyclerView.setAdapter(serviceAdapter);
    }

    private void initializeServices() {
        SQLiteDatabase db = new DatabaseHelper(this).getReadableDatabase();
        serviceList.clear();

        String query = "SELECT * FROM Services";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("service_id"));
                String serviceName = cursor.getString(cursor.getColumnIndexOrThrow("service_name"));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));
                int imageResourceId = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow("photo")));

                ServiceItem serviceItem = new ServiceItem(id, serviceName, description, price, imageResourceId);
                serviceList.add(serviceItem);

            } while (cursor.moveToNext());
        } else {
            Log.d("ServicesActivity", "No services found in the database.");
        }

        cursor.close();
        db.close();
    }


    @Override
    public void onItemClick(ServiceItem serviceItem) {
        Intent intent = new Intent(this, ReservationsActivity.class);
        intent.putExtra("reservation_item", serviceItem);
        startActivity(intent);
    }


    public Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);


        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }


}
