package com.bernd32.weatherdemo.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bernd32.weatherdemo.R;
import com.bernd32.weatherdemo.models.WeatherItem;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * RecyclerView adapter used in our fragments (ui.CurrentWeatherFragment and ui.ForecastFragment)
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.BaseViewHolder> {

    private ArrayList<WeatherItem> mWeatherItems;
    private Context mContext;
    private static final String TAG = "RecyclerAdapter";

    public RecyclerAdapter(Context context, ArrayList<WeatherItem> items) {
        this.mWeatherItems = items;
        this.mContext = context;
    }

    public static class BaseViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView titleText, valueText;

        BaseViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
            titleText = itemView.findViewById(R.id.card_title);
            valueText = itemView.findViewById(R.id.card_value);
        }
    }

    @NonNull
    @Override
    public RecyclerAdapter.BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.forecast_item, parent, false);
        return new BaseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        WeatherItem currentItem = mWeatherItems.get(position);
        holder.imageView.setImageResource(currentItem.getImgResource());
        if (currentItem.getImgResource() != 0) {
            holder.imageView.setImageResource(currentItem.getImgResource());
        } else if (currentItem.getImgUrl() != null) {
            Glide.with(mContext).load(currentItem.getImgUrl()).into(holder.imageView);
        }
        holder.titleText.setText(currentItem.getTitle());
        holder.valueText.setText(currentItem.getValue());
    }

    @Override
    public int getItemCount() {
        return mWeatherItems.size();
    }
}
