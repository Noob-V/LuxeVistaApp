package com.example.resort;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "LuxeVista.db";
    private static final int DATABASE_VERSION = 1;

    private Context context;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context; // Save the context
    }

    private static final String CREATE_USERS_TABLE = "CREATE TABLE Users (" +
            "user_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "email TEXT UNIQUE NOT NULL, " +
            "password_hash TEXT NOT NULL, " +
            "first_name TEXT, " +
            "last_name TEXT, " +
            "preferences TEXT, " +
            "travel_dates TEXT, " +
            "booking_history TEXT, " +
            "created_at DATETIME DEFAULT CURRENT_TIMESTAMP);";

    private static final String CREATE_ROOMS_TABLE = "CREATE TABLE Rooms (" +
            "room_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "room_type TEXT NOT NULL, " +
            "description TEXT, " +
            "photos TEXT, " +
            "price_per_night REAL NOT NULL, " +
            "availability INTEGER DEFAULT 1);";

    private static final String CREATE_ROOM_BOOKINGS_TABLE = "CREATE TABLE Room_Bookings (" +
            "booking_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "user_id INTEGER NOT NULL, " +
            "room_id INTEGER NOT NULL, " +
            "check_in_date DATE NOT NULL, " +
            "check_out_date DATE NOT NULL, " +
            "total_price REAL NOT NULL, " +
            "booking_status TEXT DEFAULT 'Confirmed', " +
            "created_at DATETIME DEFAULT CURRENT_TIMESTAMP, " +
            "FOREIGN KEY(user_id) REFERENCES Users(user_id), " +
            "FOREIGN KEY(room_id) REFERENCES Rooms(room_id));";

    private static final String CREATE_SERVICES_TABLE = "CREATE TABLE Services (" +
            "service_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "service_name TEXT NOT NULL, " +
            "description TEXT, " +
            "price REAL NOT NULL, " +
            "photo TEXT);";

    private static final String CREATE_SERVICE_RESERVATIONS_TABLE = "CREATE TABLE Service_Reservations (" +
            "reservation_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "user_id INTEGER NOT NULL, " +
            "service_id INTEGER NOT NULL, " +
            "reservation_date DATE NOT NULL, " +
            "time_slot TIME NOT NULL, " +
            "status TEXT DEFAULT 'Confirmed', " +
            "created_at DATETIME DEFAULT CURRENT_TIMESTAMP, " +
            "FOREIGN KEY(user_id) REFERENCES Users(user_id), " +
            "FOREIGN KEY(service_id) REFERENCES Services(service_id));";

    private static final String CREATE_LOCAL_ATTRACTIONS_TABLE = "CREATE TABLE Local_Attractions (" +
            "attraction_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "name TEXT NOT NULL, " +
            "description TEXT, " +
            "photo TEXT, " +
            "type TEXT, " +
            "offer_details TEXT, " +
            "available_from DATE, " +
            "available_to DATE);";

    private static final String CREATE_NOTIFICATIONS_TABLE = "CREATE TABLE Notifications (" +
            "notification_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "user_id INTEGER, " +
            "title TEXT NOT NULL, " +
            "message TEXT NOT NULL, " +
            "sent_at DATETIME DEFAULT CURRENT_TIMESTAMP, " +
            "FOREIGN KEY(user_id) REFERENCES Users(user_id));";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_ROOMS_TABLE);
        db.execSQL(CREATE_ROOM_BOOKINGS_TABLE);
        db.execSQL(CREATE_SERVICES_TABLE);
        db.execSQL(CREATE_SERVICE_RESERVATIONS_TABLE);
        db.execSQL(CREATE_LOCAL_ATTRACTIONS_TABLE);
        db.execSQL(CREATE_NOTIFICATIONS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Users");
        db.execSQL("DROP TABLE IF EXISTS Rooms");
        db.execSQL("DROP TABLE IF EXISTS Room_Bookings");
        db.execSQL("DROP TABLE IF EXISTS Services");
        db.execSQL("DROP TABLE IF EXISTS Service_Reservations");
        db.execSQL("DROP TABLE IF EXISTS Local_Attractions");
        db.execSQL("DROP TABLE IF EXISTS Notifications");
        onCreate(db);
    }


    public void insertRoom(Room room) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("room_type", room.getTitle());
        values.put("description", room.getDescription());
        values.put("photos", String.valueOf(room.getImageResourceId()));
        values.put("price_per_night", room.getPrice());
        values.put("availability", room.getAvailability().equals("Available") ? 1 : 0);

        db.insert("Rooms", null, values);
        db.close();
    }

    public void insertService(ServiceItem service) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("service_name", service.getServiceName());
        values.put("description", service.getServiceDescription());
        values.put("price", service.getServicePrice());
        values.put("photo", service.getServiceImageResourceId());

        db.insert("Services", null, values);
        db.close();
    }

    public void insertReservation(ReservationItem reservation) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("user_id", reservation.getUserId());
        values.put("service_id", reservation.getServiceId());
        values.put("reservation_date", reservation.getReservationDate());
        values.put("time_slot", reservation.getTimeSlot());
        values.put("status", reservation.getStatus());

        db.insert("Service_Reservations", null, values);
        db.close();
    }

    public void insertAttraction(AttractionItem attraction) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", attraction.getName());
        values.put("description", attraction.getDescription());
        values.put("photo", encodeImageToBase64(attraction.getImage()));  // Encode image to base64 string
        values.put("type", attraction.getType());
        values.put("offer_details", attraction.getOfferDetails());
        values.put("available_from", attraction.getAvailableFrom());
        values.put("available_to", attraction.getAvailableTo());

        db.insert("Local_Attractions", null, values);
        db.close();
    }

    private String encodeImageToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);  // Encoding the image
    }

    private String saveBitmapToFile(Bitmap bitmap) {
        if (bitmap == null) return null;

        File filesDir = context.getFilesDir();
        File imageFile = new File(filesDir, "attraction_" + System.currentTimeMillis() + ".png");

        try (FileOutputStream fos = new FileOutputStream(imageFile)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            return imageFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<AttractionItem> getAllAttractions() {
        List<AttractionItem> attractions = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM Local_Attractions";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                String photoPath = cursor.getString(cursor.getColumnIndexOrThrow("photo"));
                String type = cursor.getString(cursor.getColumnIndexOrThrow("type"));
                String offerDetails = cursor.getString(cursor.getColumnIndexOrThrow("offer_details"));
                String availableFrom = cursor.getString(cursor.getColumnIndexOrThrow("available_from"));
                String availableTo = cursor.getString(cursor.getColumnIndexOrThrow("available_to"));

                Bitmap photo = photoPath != null ? BitmapFactory.decodeFile(photoPath) : null;

                Log.d("DatabaseHelper", "Attraction: " + name + ", " + description);

                AttractionItem attraction = new AttractionItem(name, description, photo, type, offerDetails, availableFrom, availableTo);
                attractions.add(attraction);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return attractions;
    }

    public List<Room> getAllRooms() {
        List<Room> roomList = new ArrayList<>();
        String selectQuery = "SELECT * FROM Rooms";  // Correct table name

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Get the column names to ensure they exist
        String[] columnNames = cursor.getColumnNames();

        if (cursor.moveToFirst()) {
            do {

                String roomType = getColumnValue(cursor, columnNames, "room_type");
                String description = getColumnValue(cursor, columnNames, "description");
                String price = getColumnValue(cursor, columnNames, "price_per_night");
                String availability = getColumnValue(cursor, columnNames, "availability");
                int imageResourceId = getIntColumnValue(cursor, columnNames, "photos");


                Room room = new Room(roomType, description, price, availability, imageResourceId);
                roomList.add(room);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return roomList;
    }

    private String getColumnValue(Cursor cursor, String[] columnNames, String columnName) {
        if (arrayContains(columnNames, columnName)) {
            return cursor.getString(cursor.getColumnIndex(columnName));
        } else {
            return "";
        }
    }

    private int getIntColumnValue(Cursor cursor, String[] columnNames, String columnName) {
        if (arrayContains(columnNames, columnName)) {
            return cursor.getInt(cursor.getColumnIndex(columnName));
        } else {
            return -1;
        }
    }

    private boolean arrayContains(String[] array, String value) {
        for (String str : array) {
            if (str.equals(value)) {
                return true;
            }
        }
        return false;
    }


}
