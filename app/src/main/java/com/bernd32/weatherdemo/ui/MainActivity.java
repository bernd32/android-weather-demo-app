package com.bernd32.weatherdemo.ui;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.bernd32.weatherdemo.R;
import com.bernd32.weatherdemo.ui.adapters.TabsPagerAdapter;
import com.bernd32.weatherdemo.utils.LocationProvider;
import com.bernd32.weatherdemo.utils.PreferencesManager;
import com.google.android.material.tabs.TabLayout;

import static com.bernd32.weatherdemo.utils.LocationProvider.PERMISSION_ID;

/**
 * Choose what parameters (city name or location coordinates) we're using to call
 * the weather API. Save params to PrefManager to use it across the app. Use
 * utils.LocationProvider to get user's latitude and longitude if we're using
 * location coordinates. Setup the UI (tabs, viewpager and fragments).
 */

public class MainActivity extends AppCompatActivity implements LocationProvider.Callback{

    private static final String TAG = "MainActivity";
    private PreferencesManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: started");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PreferencesManager.initializeInstance(this);
        prefManager = PreferencesManager.getInstance();
        if (savedInstanceState == null) {
            prefManager.clear();
        }

        startApplication();
    }

    private void startApplication() {
        /*
        If we have a city value in Intent then it means that user selected
        a location from AddNewLocation activity. In this case we just initialize
        the UI and use location name as a parameter
         */
        if (getIntent().hasExtra("city")) {
            prefManager.saveCity(getIntent().getStringExtra("city"));
            initUI();
        } else {
            /*
            Otherwise, we request an user location data (latitude and longitude) and
            use them as parameters. To achieve that we get user coordinates from the
            LocationProvider and then return results via Callback interface by overriding
            setResult() method.
             */
            LocationProvider locationProvider = new LocationProvider(this, this);
            locationProvider.getLastLocation();
        }
    }

    // Start the app after we got permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_ID) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult: granted");
                // Permission was granted, yay!
                startApplication();
            } else {
                Log.d(TAG, "onRequestPermissionsResult: denied");
                // permission denied :( Open AddNewLocation activity where user
                // can add a location for weather manually
                startActivity(new Intent(this, AddNewLocation.class));
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.open_saved_locations:
                startActivity(new Intent(this, AddNewLocation.class));
                return true;
            case R.id.update:
                startApplication();
                Toast.makeText(this, getString(R.string.updated_msg), Toast.LENGTH_SHORT).show();
                return true;
            case R.id.about_dialog:
                showInformationDialog(
                        getString(R.string.about),
                        getString(R.string.about_message)
                );
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void setResult(Location location) {
        // Get location from LocationProvider.Callback interface
        double lat = location.getLatitude();
        double lon = location.getLongitude();
        Log.d(TAG, "setResult: started");
        Log.d(TAG, "onNext: lat = " + lat);
        Log.d(TAG, "onNext: long = " + lon);

        // Save longitude and latitude values via PrefManager
        prefManager.saveLatitude(String.valueOf(lat));
        prefManager.saveLongitude(String.valueOf(lon));

        // Show tab fragments
        initUI();
    }

    @Override
    public void locationTurnedOff() {
        // Show an alert dialog if location is turned off
        FragmentManager fm = getSupportFragmentManager();
        TurnedOffLocationDialog alertDialog = TurnedOffLocationDialog.newInstance();
        alertDialog.show(fm, "fragment_alert");
    }

    private void initUI() {
        // Show tab fragments
        TabsPagerAdapter tabsPagerAdapter = new TabsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(tabsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
    }

    public void showInformationDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(title)
                .setMessage(message)
                .setIcon(R.drawable.ic_info_outline_black_24dp)
                .setCancelable(true)
                .setNegativeButton("OK", (dialog, id) -> dialog.cancel());
        AlertDialog alert = builder.create();
        alert.show();
    }
}
