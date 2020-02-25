package com.bernd32.weatherdemo.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bernd32.weatherdemo.R;
import com.bernd32.weatherdemo.models.UserLocation;
import com.bernd32.weatherdemo.ui.MainActivity;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

/**
 * RecyclerView adapter used in ui.AddNewLocation activity
 */

public class LocationsRecyclerAdapter extends RecyclerView.Adapter<LocationsRecyclerAdapter.BaseViewHolder> {

    private List<UserLocation> mItems;
    private Context mContext;
    private static final String TAG = "LocationsRecyclerAdapter";

    public LocationsRecyclerAdapter(Context context, List<UserLocation> items) {
        this.mItems = items;
        this.mContext = context;
    }

    public void addItems(List<UserLocation> postItems) {
        mItems.addAll(postItems);
        mItems = postItems;
        notifyDataSetChanged();
    }

    public void clear() {
        mItems.clear();
        notifyDataSetChanged();
    }

    public UserLocation getItem(int position) {
        return mItems.get(position);
    }

    @NonNull
    @Override
    public LocationsRecyclerAdapter.BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.location_item, parent, false);
        return new BaseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        UserLocation currentItem = mItems.get(position);

        // Since we have location names in getCity(), we should capitalize the first letter
        String cityName = currentItem.getCity();
        String capitalizedCityName = cityName.substring(0, 1).toUpperCase() + cityName.substring(1);

        holder.itemValue.setText(capitalizedCityName);

        holder.parentLayout.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, MainActivity.class);
            intent.putExtra("city", holder.itemValue.getText().toString());
            mContext.startActivity(intent);
        });

        // Show a tooltip on a long click
        holder.parentLayout.setOnLongClickListener(view -> {
            Toast.makeText(mContext, mContext.getString(R.string.tooltip_msg), Toast.LENGTH_SHORT).show();
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return mItems == null ? 0 : mItems.size();
    }


    public static class BaseViewHolder extends RecyclerView.ViewHolder {

        private TextView itemValue;
        MaterialCardView parentLayout;

        BaseViewHolder(@NonNull View itemView) {
            super(itemView);
            itemValue = itemView.findViewById(R.id.item_value);
            parentLayout = itemView.findViewById(R.id.parent_layout);

        }
    }

}