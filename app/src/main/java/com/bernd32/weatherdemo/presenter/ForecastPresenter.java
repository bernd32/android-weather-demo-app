package com.bernd32.weatherdemo.presenter;

import android.util.Log;

import com.bernd32.weatherdemo.EndpointInterface;
import com.bernd32.weatherdemo.RetrofitAdapter;
import com.bernd32.weatherdemo.models.forecastdata.ForecastData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

import static com.bernd32.weatherdemo.Constants.API_KEY;

/**
 * Here we get current conditions information by calling our weather API,
 * and then update our UI via the callback interface. We also performing
 * a lot of formatting to make everything look clean :)
 */

public class ForecastPresenter {

    private static final String TAG = "ForecastPresenter";
    private View mView;
    private List<String> mDates = new ArrayList<>();
    private List<Double> mMaxTempAll = new ArrayList<>();
    private List<Double> mMinTempAll = new ArrayList<>();
    private List<String> mDescriptions = new ArrayList<>();
    private List<String> mImgUrls = new ArrayList<>();


    public ForecastPresenter(View view) {
        this.mView = view;
    }

    public void getForecast(String latitude, String longitude, String city) {
        Log.d(TAG, "getForecast: started");
        Retrofit retrofit = RetrofitAdapter.getInstance();
        EndpointInterface apiService = retrofit.create(EndpointInterface.class);
        apiService.getForecast("metric", API_KEY, city,
                Locale.getDefault().getLanguage(), latitude, longitude)
                .throttleFirst(10, TimeUnit.MINUTES)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ForecastData>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(ForecastData forecastData) {
                        forecastDataHandle(forecastData);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError: started");
                        Log.e(TAG, "onError: ", e.fillInStackTrace());
                        Log.e(TAG, "onError: ", e.getCause());
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    private void forecastDataHandle(ForecastData forecastData) {
        // Populate array list with values emitted from rxjava
        int size=  forecastData.getList().size();
        for (int i = 0; i < size; i++) {
            mMaxTempAll.add(forecastData.getList().get(i).getMain().getTempMax());
            mMinTempAll.add(forecastData.getList().get(i).getMain().getTempMin());
            mDates.add(formatUnixTime(forecastData.getList().get(i).getDt()));
            mDescriptions.add(forecastData.getList().get(i).getWeather().get(0).getDescription());
            mImgUrls.add(formatImgUrl(forecastData.getList().get(i).getWeather().get(0).getIcon()));
        }
        Log.d(TAG, "forecastDataHandle: mImgUrls = " + mImgUrls);
        // We get 5 day / 3 hour forecast data, 40 items in total
        // But we don't need such precise forecast, we just need
        // to get 5 day / 1 day forecast, with min and max temps.
        // But first we should calculate an arithmetic mean for the
        // min/max temperature and add it to result array
        final List<Double> maxTempAvg = getMaxAverage(mMaxTempAll, 8);
        final List<Double> minTempAvg = getMinAverage(mMinTempAll, 8);
        final List<String> avgMinMax = formatMinMaxValues(maxTempAvg, minTempAvg);
        // Now we need to take every 8th element from mTemperatures, mDates, etc.
        final List<String> dates = takeEveryNthElement(mDates, 8);
        final List<String> descriptions = takeEveryNthElement(mDescriptions, 8);
        final List<String> imgUrls = takeEveryNthElement(mImgUrls, 8);
        // Send all of this to the UI
        mView.updateForecast(avgMinMax, dates, descriptions, imgUrls);
    }

    private List<String> formatMinMaxValues(List<Double> maxTempAvg, List<Double> minTempAvg) {
        List<String> result = new ArrayList<>();
        for (int i = 0; i<maxTempAvg.size(); i++) {
            long max = Math.round(maxTempAvg.get(i));
            long min = Math.round(minTempAvg.get(i));
            String fString = max + " .. " + min;
            result.add(fString);
        }
        return result;
    }

    private List<Double> getMinAverage(List<Double> list, int offset) {
        List<Double> temp = new ArrayList<>();
        for (int i = 0; i < list.size(); i += offset) {
            int toIndex = i + offset;
            if (toIndex > list.size()) break;
            temp.add(findMin(list.subList(i, toIndex)));
        }
        return temp;
    }

    private List<Double> getMaxAverage(List<Double> list, int offset) {
        List<Double> temp = new ArrayList<>();
        for (int i = 0; i < list.size(); i += offset) {
            int toIndex = i + offset;
            if (toIndex > list.size()) break;
            temp.add(findMax(list.subList(i, toIndex)));
        }
        return temp;
    }

    private List<String> takeEveryNthElement(List<String> list, int nth) {
        return IntStream.range(0, list.size())
                .filter(n -> n % nth == 0)
                .mapToObj(list::get)
                .collect(Collectors.toList());
    }

    private double findMin(List<Double> subList) {
        return subList.stream().mapToDouble(val -> val).min().orElse(0.0);
    }

    private double findMax(List<Double> subList) {
        return subList.stream().mapToDouble(val -> val).max().orElse(0.0);
    }

    private String formatImgUrl(String iconCode) {
        return String.format("http://openweathermap.org/img/wn/%s@2x.png", iconCode);
    }

    private String formatUnixTime(Integer unixSeconds) {
        String[] locale = {Locale.getDefault().getLanguage(), Locale.getDefault().getCountry()};
        Date date = new java.util.Date(unixSeconds*1000L);
        SimpleDateFormat sdf = new java.text.SimpleDateFormat(
                "EEEE dd MMMM",
                new Locale(locale[0], locale[1]));
        sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT+5"));
        return sdf.format(date);
    }


    public interface View {
        void updateForecast(List<String> avgMinMax, List<String> mDates,
                            List<String> mDescriptions, List<String> mImgUrls);
    }
}
