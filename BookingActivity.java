package com.example.resort;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;

public class BookingActivity extends AppCompatActivity {

    private TextView roomTitleTextView, roomDescriptionTextView, roomPriceTextView, roomCountTextView, receiptTextView;
    private Button confirmBookingButton;
    private ImageButton minusButton, plusButton, homeButton;
    private DatabaseHelper databaseHelper;
    private int userId, roomCount = 1;
    private double roomPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);


        roomTitleTextView = findViewById(R.id.roomTitleTextView);
        roomDescriptionTextView = findViewById(R.id.roomDescriptionTextView);
        roomPriceTextView = findViewById(R.id.roomPriceTextView);
        roomCountTextView = findViewById(R.id.roomCountTextView);
        receiptTextView = findViewById(R.id.receiptTextView);
        confirmBookingButton = findViewById(R.id.confirmBookingButton);
        minusButton = findViewById(R.id.minusButton);
        plusButton = findViewById(R.id.plusButton);
        homeButton = findViewById(R.id.homeButton);

        databaseHelper = new DatabaseHelper(this);


        Intent intent = getIntent();
        String roomTitle = intent.getStringExtra("ROOM_TITLE");
        String roomDescription = intent.getStringExtra("ROOM_DESCRIPTION");


        roomTitleTextView.setText(roomTitle);
        roomDescriptionTextView.setText(roomDescription);


        String roomPriceString = intent.getStringExtra("ROOM_PRICE");
        if (roomPriceString != null) {
            try {
                roomPriceString = roomPriceString.replaceAll("[^0-9.]", "");
                roomPrice = Double.parseDouble(roomPriceString);
            } catch (NumberFormatException e) {
                Log.e("BookingActivity", "Invalid price format: " + roomPriceString);
                roomPrice = -1;
            }
        } else {
            roomPrice = getRoomPriceByTitle(roomTitle);
        }

        if (roomPrice != -1) {
            roomPriceTextView.setText(String.format("Price: %.2f", roomPrice));
        } else {
            roomPriceTextView.setText("Price not available");
        }

        userId = 1;


        updateReceipt();


        minusButton.setOnClickListener(v -> {
            if (roomCount > 1) {
                roomCount--;
                roomCountTextView.setText(String.valueOf(roomCount));
                updateReceipt();
            }
        });

        plusButton.setOnClickListener(v -> {
            roomCount++;
            roomCountTextView.setText(String.valueOf(roomCount));  // Update room count display
            updateReceipt();  // Update receipt when room count changes
        });


        confirmBookingButton.setOnClickListener(v -> {
            String checkInDate = getCurrentDate();
            String checkOutDate = getNextDayDate();

            double totalPrice = roomPrice * roomCount;

            long bookingId = insertBooking(userId, roomTitle, checkInDate, checkOutDate, totalPrice);

            if (bookingId != -1) {
                Toast.makeText(BookingActivity.this, "Booking Confirmed!", Toast.LENGTH_SHORT).show();
                showBookingConfirmationNotification();
                Intent confirmationIntent = new Intent(BookingActivity.this, ConfirmationActivity.class);
                startActivity(confirmationIntent);
            } else {
                Toast.makeText(BookingActivity.this, "Booking Failed, Try Again!", Toast.LENGTH_SHORT).show();
            }
        });

        homeButton.setOnClickListener(v -> {
            Intent homeIntent = new Intent(BookingActivity.this, DashboardActivity.class);
            startActivity(homeIntent);
        });
    }

    private void updateReceipt() {
        if (roomPrice != -1) {
            double totalPrice = roomPrice * roomCount;
            String receiptText = String.format("Room Type: %s\nPrice per Night: %.2f\nTotal Price: %.2f",
                    roomTitleTextView.getText().toString(), roomPrice, totalPrice);
            receiptTextView.setText(receiptText);
        } else {
            receiptTextView.setText("Price not available");
        }
    }

    private double getRoomPriceByTitle(String roomTitle) {
        double price = -1;
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.query("Rooms", new String[]{"price_per_night"}, "room_type = ?", new String[]{roomTitle}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            price = cursor.getDouble(cursor.getColumnIndex("price_per_night"));
            cursor.close();
        }
        return price;
    }

    private long insertBooking(int userId, String roomTitle, String checkInDate, String checkOutDate, double totalPrice) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("room_id", getRoomIdByTitle(roomTitle));
        values.put("check_in_date", checkInDate);
        values.put("check_out_date", checkOutDate);
        values.put("total_price", totalPrice);

        return db.insert("Room_Bookings", null, values);
    }

    private int getRoomIdByTitle(String roomTitle) {
        int roomId = -1;
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.query("Rooms", new String[]{"room_id"}, "room_type = ?", new String[]{roomTitle}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            roomId = cursor.getInt(cursor.getColumnIndex("room_id"));
            cursor.close();
        }
        return roomId;
    }

    private String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        return String.format("%04d-%02d-%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
    }

    private String getNextDayDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        return String.format("%04d-%02d-%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
    }

    private void showBookingConfirmationNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "booking_channel"; // Unique ID

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = "Booking Notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            notificationManager.createNotificationChannel(channel);
        }

        Notification notification = new Notification.Builder(this, channelId)
                .setContentTitle("Booking Confirmed")
                .setContentText("Your booking has been confirmed!")
                .setSmallIcon(R.drawable.luxe)
                .setAutoCancel(true)
                .build();

        notificationManager.notify(1, notification); 
        Log.d("BookingActivity", "Booking confirmation notification sent.");


        insertNotification(userId, "Booking Confirmed", "Your booking has been confirmed!");
    }

    private void insertNotification(int userId, String title, String message) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("title", title);
        values.put("message", message);

        db.insert("Notifications", null, values);
    }


}
