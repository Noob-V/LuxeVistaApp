package com.example.resort;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AttractionAdapter extends RecyclerView.Adapter<AttractionAdapter.ViewHolder> {

    private List<AttractionItem> attractionList;

    public AttractionAdapter(List<AttractionItem> attractionList) {
        this.attractionList = attractionList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_attraction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AttractionItem attraction = attractionList.get(position);
        holder.nameTextView.setText(attraction.getName());
        holder.descriptionTextView.setText(attraction.getDescription());
        holder.offerTextView.setText(attraction.getOfferDetails());
        holder.imageView.setImageBitmap(attraction.getImage());
    }

    @Override
    public int getItemCount() {
        return attractionList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public TextView descriptionTextView;
        public TextView offerTextView;
        public ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.attractionName);
            descriptionTextView = itemView.findViewById(R.id.attractionDescription);
            offerTextView = itemView.findViewById(R.id.attractionOffer);
            imageView = itemView.findViewById(R.id.attractionPhoto);
        }
    }
}
