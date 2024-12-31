package com.example.resort;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.content.res.Resources;
import java.util.List;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> {
    private List<Room> roomList;

    public RoomAdapter(List<Room> roomList) {
        this.roomList = roomList;
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.room_item, parent, false);
        return new RoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        Room room = roomList.get(position);
        holder.roomTitle.setText(room.getTitle());
        holder.roomDescription.setText(room.getDescription());
        holder.roomPrice.setText(room.getPrice());
        holder.roomAvailability.setText(room.getAvailability());

        holder.bookNowButton.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), BookingActivity.class);
            intent.putExtra("ROOM_TITLE", room.getTitle());
            intent.putExtra("ROOM_DESCRIPTION", room.getDescription());
            intent.putExtra("ROOM_PRICE", room.getPrice());
            holder.itemView.getContext().startActivity(intent);
        });

        // Decode and resize the image
        Bitmap roomImage = decodeSampledBitmapFromResource(holder.itemView.getContext().getResources(), room.getImageResourceId(), 500, 500);
        holder.roomImage.setImageBitmap(roomImage);
    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }

    public static class RoomViewHolder extends RecyclerView.ViewHolder {
        TextView roomTitle, roomDescription, roomPrice, roomAvailability;
        ImageView roomImage;
        Button bookNowButton;

        public RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            roomTitle = itemView.findViewById(R.id.roomTitle);
            roomDescription = itemView.findViewById(R.id.roomDescription);
            roomPrice = itemView.findViewById(R.id.roomPrice);
            roomAvailability = itemView.findViewById(R.id.roomAvailability);
            roomImage = itemView.findViewById(R.id.roomImage);
            bookNowButton = itemView.findViewById(R.id.bookNowButton);
        }
    }

    public void updateRoomList(List<Room> updatedList) {
        this.roomList = updatedList;
        notifyDataSetChanged();
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);


        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);


        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }


    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
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
