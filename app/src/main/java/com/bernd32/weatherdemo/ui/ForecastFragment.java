package com.bernd32.weatherdemo.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bernd32.weatherdemo.R;
import com.bernd32.weatherdemo.models.WeatherItem;
import com.bernd32.weatherdemo.presenter.ForecastPresenter;
import com.bernd32.weatherdemo.ui.adapters.RecyclerAdapter;
import com.bernd32.weatherdemo.utils.PreferencesManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Here we display forecast data by overriding callback interface methods
 * sent by presenter.ForecastPresenter
 */

public class ForecastFragment extends Fragment implements ForecastPresenter.View{

    private static final String TAG = "ForecastFragment";
    private ArrayList<WeatherItem> mItems = new ArrayList<>();
    private RecyclerView mRecyclerView;

    public ForecastFragment() {
        // Required empty public constructor
    }

    /**
     * @return A new instance of fragment ForecastFragment.
     */
    public static ForecastFragment newInstance() {
        return new ForecastFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: started");
        setRetainInstance(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState == null) {
            getData();
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
        ForecastPresenter forecastPresenter = new ForecastPresenter(this);
        forecastPresenter.getForecast(latitude, longitude, city);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_forecast, container, false);
        mRecyclerView = root.findViewById(R.id.recyclerView2);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(new RecyclerAdapter(getContext(), mItems));
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);



        return root;
    }

    private void saveItems(ArrayList<WeatherItem> mItems) {
        RecyclerView.Adapter mAdapter = new RecyclerAdapter(getContext(), mItems);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void updateForecast(List<String> avgMinMax, List<String> mDates,
                               List<String> mDescriptions, List<String> mImgUrls) {
        mItems.clear();
        for (int i = 0; mDates.size() > i; i++) {
            mItems.add(new WeatherItem(mImgUrls.get(i), mDates.get(i), avgMinMax.get(i)));
        }
        Log.d(TAG, "updateForecast: mImgUrls" + mImgUrls);
        saveItems(mItems);
    }
}
