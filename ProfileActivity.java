package com.example.resort;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private EditText preferencesInput, travelDatesInput;
    private TextView bookingHistory;
    private ImageView notificationBell;
    private Button homeButton, logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        preferencesInput = findViewById(R.id.preferencesInput);
        travelDatesInput = findViewById(R.id.travelDatesInput);
        bookingHistory = findViewById(R.id.bookingHistory);
        notificationBell = findViewById(R.id.notificationBell);
        homeButton = findViewById(R.id.homeButton);
        logoutButton = findViewById(R.id.logoutButton);  // Initialize logout button

        Button saveProfileButton = findViewById(R.id.saveProfileButton);

        dbHelper = new DatabaseHelper(this);

        loadUserData(1);

        saveProfileButton.setOnClickListener(v -> saveUserData(1));

        notificationBell.setOnClickListener(v -> showNotifications(1));

        homeButton.setOnClickListener(v -> navigateToDashboard());

        logoutButton.setOnClickListener(v -> logoutUser());
    }

    private void loadUserData(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("Users", null, "user_id = ?", new String[]{String.valueOf(userId)},
                null, null, null);

        if (cursor.moveToFirst()) {
            String preferences = cursor.getString(cursor.getColumnIndexOrThrow("preferences"));
            String travelDates = cursor.getString(cursor.getColumnIndexOrThrow("travel_dates"));
            String bookingHistoryData = cursor.getString(cursor.getColumnIndexOrThrow("booking_history"));

            preferencesInput.setText(preferences);
            travelDatesInput.setText(travelDates);
            bookingHistory.setText(bookingHistoryData);
        }
        cursor.close();
        db.close();
    }

    private void saveUserData(int userId) {
        String preferences = preferencesInput.getText().toString();
        String travelDates = travelDatesInput.getText().toString();

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("preferences", preferences);
        values.put("travel_dates", travelDates);

        db.update("Users", values, "user_id = ?", new String[]{String.valueOf(userId)});
        db.close();

        Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
    }

    private void showNotifications(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor notificationsCursor = db.query("Notifications", new String[]{"title", "message"}, "user_id = ?",
                new String[]{String.valueOf(userId)}, null, null, "sent_at DESC");

        ArrayList<String> notifications = new ArrayList<>();
        while (notificationsCursor.moveToNext()) {
            String title = notificationsCursor.getString(notificationsCursor.getColumnIndexOrThrow("title"));
            String message = notificationsCursor.getString(notificationsCursor.getColumnIndexOrThrow("message"));

            String notification = title + ": " + message;

            if (!notifications.contains(notification)) {
                notifications.add(notification);
            }
        }
        notificationsCursor.close();

        Cursor bookingCursor = db.query("Room_Bookings", new String[]{"room_id", "check_in_date", "check_out_date", "total_price"},
                "user_id = ?", new String[]{String.valueOf(userId)}, null, null, null);

        String roomDetails = "";
        if (bookingCursor.moveToFirst()) {
            int roomId = bookingCursor.getInt(bookingCursor.getColumnIndexOrThrow("room_id"));
            String checkInDate = bookingCursor.getString(bookingCursor.getColumnIndexOrThrow("check_in_date"));
            String checkOutDate = bookingCursor.getString(bookingCursor.getColumnIndexOrThrow("check_out_date"));
            double totalPrice = bookingCursor.getDouble(bookingCursor.getColumnIndexOrThrow("total_price"));

            Cursor roomCursor = db.query("Rooms", new String[]{"room_type"}, "room_id = ?", new String[]{String.valueOf(roomId)}, null, null, null);
            if (roomCursor.moveToFirst()) {
                String roomTitle = roomCursor.getString(roomCursor.getColumnIndexOrThrow("room_type"));
                roomDetails = "Room Booked: " + roomTitle + "\n" +
                        "Check-In: " + checkInDate + "\n" +
                        "Check-Out: " + checkOutDate + "\n" +
                        "Total Price: " + String.format("%.2f", totalPrice);
            }
            roomCursor.close();
        }
        bookingCursor.close();

        if (notifications.isEmpty()) {
            Toast.makeText(this, "No notifications", Toast.LENGTH_SHORT).show();
        } else {
            StringBuilder sb = new StringBuilder();
            for (String notification : notifications) {
                sb.append(notification).append("\n");
            }

            if (!roomDetails.isEmpty()) {
                sb.append("\n").append(roomDetails);
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Notifications and Booking Details")
                    .setMessage(sb.toString())
                    .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                    .show();
        }

        db.close();
    }

    private void navigateToDashboard() {
        Intent intent = new Intent(this, DashboardActivity.class);
        startActivity(intent);
    }

    private void logoutUser() {
        Toast.makeText(ProfileActivity.this, "Logging out...", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        finish();
    }
}
