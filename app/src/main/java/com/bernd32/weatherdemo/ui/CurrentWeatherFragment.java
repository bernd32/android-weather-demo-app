package com.bernd32.weatherdemo.ui;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bernd32.weatherdemo.R;
import com.bernd32.weatherdemo.models.WeatherItem;
import com.bernd32.weatherdemo.presenter.CurrentConditionsPresenter;
import com.bernd32.weatherdemo.ui.adapters.RecyclerAdapter;
import com.bernd32.weatherdemo.utils.PreferencesManager;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Here we display current weather data by overriding callback interface methods
 * sent by presenter.CurrentConditionsPresenter
 */

public class CurrentWeatherFragment extends Fragment implements CurrentConditionsPresenter.View {

    private static final String TAG = "CurrentWeatherFragment";

    private TextView mCity, mTemp, mWeatherText;
    private ImageView mIcon;
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private ArrayList<WeatherItem> mItems = new ArrayList<>();
    private String mImgUrl;

    public CurrentWeatherFragment() {
        // Required empty public constructor
    }

    /**
     * @return A new instance of fragment CurrentWeatherFragment.
     */
    public static CurrentWeatherFragment newInstance() {
        return new CurrentWeatherFragment();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Log.d(TAG, "onSaveInstanceState: start");
        outState.putString("image_url", mImgUrl);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: started");
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState == null) {
            getData();
        } else {
            updateIcon(mImgUrl);
        }
    }

    private void getData() {
        // Read longitude and latitude values from PrefManager
        PreferencesManager.initializeInstance(getContext());
        PreferencesManager prefManager = PreferencesManager.getInstance();
        String longitude = prefManager.getLongitude();
        String latitude = prefManager.getLatitude();
        String city = prefManager.getCity();
        // Invoke the Presenter
        CurrentConditionsPresenter conditionsPresenter = new CurrentConditionsPresenter(this, getContext());
        conditionsPresenter.getCurrentConditions(latitude, longitude, city);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: started");
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_current_weather, container, false);
        mCity = root.findViewById(R.id.city_tv);
        mTemp = root.findViewById(R.id.temperature_tv);
        mWeatherText = root.findViewById(R.id.weatherText);
        mIcon = root.findViewById(R.id.weatherIcon);
        mProgressBar = root.findViewById(R.id.progressBar);
        mRecyclerView = root.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(new RecyclerAdapter(getContext(), mItems));
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        return root;
    }

    @Override
    public void updateTemperature(String temp) {
        mTemp.setText(temp);
    }

    @Override
    public void updateLocation(String location) {
        mCity.setText(location);
    }

    @Override
    public void updateWeatherText(String weatherText) {
        mWeatherText.setText(weatherText);
    }

    @Override
    public void updateIcon(String url) {
        mImgUrl = url;
        Glide.with(this).load(mImgUrl).into(mIcon);
    }

    @Override
    public void updateDetails(String pressure, String wind, String humidity) {
        // Clear old information
        mItems.clear();

        mItems.add(new WeatherItem(R.drawable.ic_wi_barometer, getString(R.string.pressure), pressure));
        mItems.add(new WeatherItem(R.drawable.ic_wi_day_windy, getString(R.string.wind), wind));
        mItems.add(new WeatherItem(R.drawable.ic_wi_humidity, getString(R.string.humidity), humidity));

        saveItems(mItems);
    }

    @Override
    public void showProgressBar(boolean show) {
        if (show) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void showError(Throwable t) {
        String title = getString(R.string.error);
        String message = t.getLocalizedMessage();
        Activity act = getActivity();
        if (act instanceof MainActivity) {
            ((MainActivity) act).showInformationDialog(title, message);
        }
    }

    private void saveItems(ArrayList<WeatherItem> mItems) {
        RecyclerView.Adapter mAdapter = new RecyclerAdapter(getContext(), mItems);
        mRecyclerView.setAdapter(mAdapter);
    }

}
