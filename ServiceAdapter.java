package com.example.resort;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder> {

    private List<ServiceItem> serviceList;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(ServiceItem serviceItem);
    }

    public ServiceAdapter(List<ServiceItem> serviceList, OnItemClickListener listener) {
        this.serviceList = serviceList;
        this.listener = listener;
    }

    @Override
    public ServiceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.service_item, parent, false);
        return new ServiceViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ServiceViewHolder holder, int position) {
        ServiceItem serviceItem = serviceList.get(position);

        holder.serviceNameTextView.setText(serviceItem.getServiceName());
        holder.serviceDescriptionTextView.setText(serviceItem.getServiceDescription());
        holder.servicePriceTextView.setText(String.format("LKR %.2f", serviceItem.getServicePrice()));

        int imageResourceId = serviceItem.getServiceImageResourceId();
        Bitmap scaledBitmap = decodeSampledBitmapFromResource(holder.itemView.getContext().getResources(), imageResourceId, 200, 200);
        holder.serviceImageView.setImageBitmap(scaledBitmap);

        // Handle the button click separately
        holder.reserveButton.setOnClickListener(v -> listener.onItemClick(serviceItem));
    }


    @Override
    public int getItemCount() {
        return serviceList != null ? serviceList.size() : 0;
    }

    public static class ServiceViewHolder extends RecyclerView.ViewHolder {

        public TextView serviceNameTextView;
        public TextView serviceDescriptionTextView;
        public TextView servicePriceTextView;
        public ImageView serviceImageView;
        public Button reserveButton; // Add this line for reserve button

        public ServiceViewHolder(View itemView) {
            super(itemView);
            serviceNameTextView = itemView.findViewById(R.id.serviceNameTextView);
            serviceDescriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            servicePriceTextView = itemView.findViewById(R.id.priceTextView);
            serviceImageView = itemView.findViewById(R.id.serviceImageView);
            reserveButton = itemView.findViewById(R.id.reserveButton); // Reference the button
        }
    }

    public Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            int halfHeight = height / 2;
            int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

}
