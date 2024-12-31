package com.example.resort;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {
    private RecyclerView roomsRecyclerView;
    private RoomAdapter roomAdapter;
    private List<Room> roomList;
    private List<Room> filteredRoomList;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        roomsRecyclerView = findViewById(R.id.roomsRecyclerView);


        roomList = new ArrayList<>();
        filteredRoomList = new ArrayList<>();
        dbHelper = new DatabaseHelper(this);

        roomList.add(new Room("Ocean View Suite", "A luxurious suite with a panoramic view of the ocean.", "LKR 36,000", "Available for the next 2 weeks", R.drawable.oceanview));
        roomList.add(new Room("Deluxe Room", "A stylish room with a queen-sized bed.", "LKR 35,000", "Not available for the next 1 week", R.drawable.deluxeroom));
        roomList.add(new Room("Garden View Room", "A peaceful room with a beautiful garden view.", "LKR 25,000", "Available", R.drawable.gardenview));
        roomList.add(new Room("Presidential Suite", "The epitome of luxury.", "LKR 120,000", "Available for the next 3 weeks", R.drawable.presidentialsuite));
        roomList.add(new Room("Standard Room", "A cozy and comfortable room.", "LKR 18,000", "Available", R.drawable.standardroom));
        roomList.add(new Room("Mountain View Suite", "A spacious suite with breathtaking views.", "LKR 45,000", "Available for the next 1 week", R.drawable.mountainviewsuite));
        roomList.add(new Room("Seaside Villa", "A private villa right on the beach.", "LKR 80,000", "Available for the next 2 weeks", R.drawable.seasidevilla));
        roomList.add(new Room("Family Suite", "A spacious suite designed for families.", "LKR 35,000", "Available for the next 3 weeks", R.drawable.familysuite));


        for (Room room : roomList) {
            dbHelper.insertRoom(room);
        }


        filteredRoomList.addAll(roomList);


        roomAdapter = new RoomAdapter(filteredRoomList);
        roomsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        roomsRecyclerView.setAdapter(roomAdapter);


        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterRooms(newText);
                return true;
            }
        });

        ImageView dropdownMenuIcon = findViewById(R.id.dropdownMenuIcon);
        dropdownMenuIcon.setOnClickListener(this::showDropdownMenu);


        Button priceFilterButton = findViewById(R.id.priceFilterButton);
        priceFilterButton.setOnClickListener(v -> showPriceFilterDialog());
    }


    private void filterRooms(String query) {
        filteredRoomList.clear();

        for (Room room : roomList) {
            if (room.getTitle().toLowerCase().contains(query.toLowerCase())) {
                filteredRoomList.add(room);
            }
        }

        roomAdapter.notifyDataSetChanged();
    }

    private void showDropdownMenu(View anchor) {
        PopupMenu popupMenu = new PopupMenu(this, anchor);
        popupMenu.getMenuInflater().inflate(R.menu.dropdown_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId(); // Debugging variable
            if (itemId == R.id.menu_services) {
                startActivity(new Intent(DashboardActivity.this, ServicesActivity.class));
                return true;
            } else if (itemId == R.id.menu_attractions) {
                startActivity(new Intent(DashboardActivity.this, AttractionsActivity.class));
                return true;
            } else if (itemId == R.id.menu_about_us) {
                startActivity(new Intent(DashboardActivity.this, AboutUsActivity.class));
                return true;
            }
            return false;
        });

        popupMenu.show();
    }


    private void showPriceFilterDialog() {
        // Create an AlertDialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Price Range");


        LayoutInflater inflater = LayoutInflater.from(this);
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.dialog_price_filter, null);


        final SeekBar minSeekBar = layout.findViewById(R.id.minSeekBar);
        final SeekBar maxSeekBar = layout.findViewById(R.id.maxSeekBar);

        minSeekBar.setMax(200000);
        maxSeekBar.setMax(200000);

        minSeekBar.setProgress(0);
        maxSeekBar.setProgress(200000);


        final TextView minPriceTextView = layout.findViewById(R.id.minPriceTextView);
        final TextView maxPriceTextView = layout.findViewById(R.id.maxPriceTextView);

        minPriceTextView.setText("Min Price: LKR 0");
        maxPriceTextView.setText("Max Price: LKR 200,000");


        minSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                minPriceTextView.setText("Min Price: LKR " + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        maxSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                maxPriceTextView.setText("Max Price: LKR " + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });


        builder.setView(layout);

        builder.setPositiveButton("Apply", (dialog, which) -> {
            int minPrice = minSeekBar.getProgress();
            int maxPrice = maxSeekBar.getProgress();

            filterRoomsByPrice(minPrice, maxPrice);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.create().show();
    }


    private void filterRoomsByPrice(int minPrice, int maxPrice) {
        filteredRoomList.clear();

        for (Room room : roomList) {
            int roomPrice = Integer.parseInt(room.getPrice().replaceAll("[^0-9]", "")); // Extract numeric value from price string

            if (roomPrice >= minPrice && roomPrice <= maxPrice) {
                filteredRoomList.add(room);
            }
        }
        roomAdapter.notifyDataSetChanged();
    }

    public void goToProfile(View view) {
        Intent intent = new Intent(DashboardActivity.this, ProfileActivity.class);
        startActivity(intent);
    }

}
