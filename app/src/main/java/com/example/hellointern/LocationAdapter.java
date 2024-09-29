package com.example.hellointern;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder> {
    private List<LocationModel> locationList;

    public LocationAdapter(List<LocationModel> locationList) {
        this.locationList = locationList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.location_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LocationModel location = locationList.get(position);
        holder.latitudeTextView.setText(String.valueOf(location.getLatitude()));
        holder.longitudeTextView.setText(String.valueOf(location.getLongitude()));
        holder.addressTextView.setText(location.getAddress()); // Set address text
    }

    @Override
    public int getItemCount() {
        return locationList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView latitudeTextView, longitudeTextView, addressTextView; // Address TextView

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            latitudeTextView = itemView.findViewById(R.id.latitudeTextView);
            longitudeTextView = itemView.findViewById(R.id.longitudeTextView);
            addressTextView = itemView.findViewById(R.id.addressTextView); // Initialize address TextView
        }
    }
}
