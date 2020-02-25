package com.bernd32.weatherdemo.presenter;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.bernd32.weatherdemo.EndpointInterface;
import com.bernd32.weatherdemo.R;
import com.bernd32.weatherdemo.RetrofitAdapter;
import com.bernd32.weatherdemo.models.weatherdata.WeatherData;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

import static com.bernd32.weatherdemo.Constants.API_KEY;

/**
 * Here we get current conditions information by calling our weather API,
 * and then update our UI via the callback interface
 */

public class CurrentConditionsPresenter {

    private View mView;
    private Context mContext;
    private static final String TAG = "CurrentConditionsPresenter";

    public CurrentConditionsPresenter(View view, Context context) {
        mView = view;
        mContext = context;
    }

    public void getCurrentConditions(String latitude, String longitude, String city) {
        Log.d(TAG, "getCurrentConditions: started");
        Log.d(TAG, "getCurrentConditions: lat/long=" + latitude + "/"+ longitude);
        Log.d(TAG, "getCurrentConditions: city=" + city);
        Retrofit retrofit = RetrofitAdapter.getInstance();
        EndpointInterface apiService = retrofit.create(EndpointInterface.class);
        // Format latitude and longitude values
/*        String formattedLat = new DecimalFormat("###.###").format(latitude);
        String formattedLon = new DecimalFormat("###.###").format(longitude);*/

        // Get the observable Weather object
        apiService.getCurrentConditions("metric", API_KEY, city,
                Locale.getDefault().getLanguage(), latitude, longitude)
            .throttleFirst(10, TimeUnit.MINUTES)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe(__ -> mView.showProgressBar(true))
            .doOnTerminate(() -> mView.showProgressBar(false))
            .subscribe(new Observer<WeatherData>() {
                @Override
                public void onSubscribe(Disposable d) {
                }

                @Override
                public void onNext(WeatherData weatherData) {
                    String location = String.format("%s, %s",
                            weatherData.getName(), weatherData.getSys().getCountry());

                    String url = String.format("http://openweathermap.org/img/wn/%s@2x.png",
                            weatherData.getWeather().get(0).getIcon());

                    String temperature = String.format(Locale.getDefault(), "%d%s",
                            Math.round(weatherData.getMain().getTemp()), "°C");

                    String pressure = String.format(Locale.getDefault(), "%d %s",
                            weatherData.getMain().getPressure(),
                            mContext.getString(R.string.pressure_unit));

                    String humidity = String.format(Locale.getDefault(), "%d %%",
                            weatherData.getMain().getHumidity());

                    String wind = String.format(Locale.getDefault(), "%.2f %s",
                            weatherData.getWind().getSpeed(),
                            mContext.getString(R.string.meters_per_second));

                    mView.updateTemperature(temperature);
                    mView.updateLocation(location);
                    mView.updateWeatherText(weatherData.getWeather().get(0).getDescription());
                    mView.updateIcon(url);
                    mView.updateDetails(pressure, wind, humidity);
                }

                @Override
                public void onError(Throwable t) {
                    Log.e(TAG, "onError: ", t.fillInStackTrace());
                    Log.e(TAG, "onError: ", t.getCause());
                    mView.showError(t);
                }

                @Override
                public void onComplete() {
                    Log.d(TAG, "onComplete: ");
                }
        });
    }

    public interface View {
        void updateTemperature(String temp);
        void updateLocation(String location);
        void updateWeatherText(String weatherText);
        void updateIcon(String url);
        void updateDetails(String pressure, String wind, String humidity);
        void showProgressBar(boolean show);
        void showError(Throwable t);
    }
}


