package com.example.resort;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

public class ReservationsActivity extends AppCompatActivity {

    private TextView serviceNameTextView;
    private TextView serviceDescriptionTextView;
    private TextView servicePriceTextView;
    private CalendarView reservationCalendarView;
    private Button confirmReservationButton;
    private TextView timeTextView;
    private Button selectTimeButton;

    private String selectedDate = "";
    private String selectedTime = "";
    private ServiceItem serviceItem;

    private static final String CHANNEL_ID = "reservation_channel";
    private static final int NOTIFICATION_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservations);

        serviceNameTextView = findViewById(R.id.serviceNameTextView);
        serviceDescriptionTextView = findViewById(R.id.serviceDescriptionTextView);
        servicePriceTextView = findViewById(R.id.servicePriceTextView);
        reservationCalendarView = findViewById(R.id.reservationCalendarView);
        confirmReservationButton = findViewById(R.id.confirmReservationButton);
        timeTextView = findViewById(R.id.timeTextView);
        selectTimeButton = findViewById(R.id.selectTimeButton);

        Intent intent = getIntent();
        serviceItem = (ServiceItem) intent.getSerializableExtra("reservation_item");

        if (serviceItem != null) {
            serviceNameTextView.setText(serviceItem.getServiceName());
            serviceDescriptionTextView.setText(serviceItem.getServiceDescription());
            servicePriceTextView.setText("Price: " + serviceItem.getServicePrice());
        }

        reservationCalendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;
        });

        selectTimeButton.setOnClickListener(v -> showTimePickerDialog());

        confirmReservationButton.setOnClickListener(v -> {
            if (!selectedDate.isEmpty() && !selectedTime.isEmpty() && serviceItem != null) {
                int userId = 1;
                String status = "Confirmed";

                ReservationItem newReservation = new ReservationItem(
                        userId,
                        serviceItem.getServiceId(),
                        serviceItem.getServiceName(),
                        serviceItem.getServiceDescription(),
                        selectedDate,
                        selectedTime,
                        serviceItem.getServicePrice(),
                        status
                );

                DatabaseHelper dbHelper = new DatabaseHelper(this);
                dbHelper.insertReservation(newReservation);

                showNotification(newReservation);
                storeNotificationInDatabase(dbHelper, newReservation);

                Toast.makeText(this, "Reservation confirmed for " + selectedDate + " at " + selectedTime, Toast.LENGTH_LONG).show();

                Intent confirmationIntent = new Intent();
                confirmationIntent.putExtra("reservation_success", true);
                setResult(RESULT_OK, confirmationIntent);
                finish();
            } else {
                Toast.makeText(this, "Please select both a date and a time for the reservation.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void showTimePickerDialog() {
        int defaultHour = 10; // Default time
        int defaultMinute = 0;

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (TimePicker view, int hourOfDay, int minute) -> {
            selectedTime = String.format("%02d:%02d %s",
                    (hourOfDay % 12 == 0 ? 12 : hourOfDay % 12),
                    minute,
                    (hourOfDay >= 12 ? "PM" : "AM"));
            timeTextView.setText("Selected Time: " + selectedTime);
        }, defaultHour, defaultMinute, false);

        timePickerDialog.show();
    }

    private void showNotification(ReservationItem reservation) {
        createNotificationChannel();

        Intent notificationIntent = new Intent(this, ServicesActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.luxe) // Replace with your app's notification icon
                .setContentTitle("Reservation Confirmed")
                .setContentText("Your reservation for " + reservation.getServiceName() + " on " + selectedDate + " at " + selectedTime + " has been confirmed.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Reservation Notifications";
            String description = "Notifications for confirmed reservations";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void storeNotificationInDatabase(DatabaseHelper dbHelper, ReservationItem reservation) {
        ContentValues values = new ContentValues();
        values.put("title", "Reservation Confirmed");
        values.put("message", "Your reservation for " + reservation.getServiceName() + " on " + selectedDate + " at " + selectedTime + " has been confirmed.");
        values.put("date", selectedDate);

        long result = dbHelper.getWritableDatabase().insert("Notifications", null, values);
        if (result == -1) {
            Log.e("ReservationsActivity", "Failed to store notification in database.");
        } else {
            Log.d("ReservationsActivity", "Notification stored in database.");
        }
    }
}
