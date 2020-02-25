/*
 * Copyright 2019 bernd32
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bernd32.weatherdemo.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * PrefManager utility to save/load/clear data
 */

public class PreferencesManager {

    /*    a thread-safe singleton class to make the global access method
        synchronized, so that only one thread can execute this method at a time */

    private static final String APP_PREFS = "com.bernd32.jlyrics.prefs";
    private static final String LAT = "com.example.app.lat";
    private static final String LON = "com.example.app.lon";
    private static final String CITY = "com.example.app.city";


    private static PreferencesManager sInstance;
    private final SharedPreferences mPref;

    private PreferencesManager(Context context) {
        mPref = context.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
    }

    public static synchronized void initializeInstance(Context context) {
        if (sInstance == null) {
            sInstance = new PreferencesManager(context);
        }
    }

    public static synchronized PreferencesManager getInstance() {
        if (sInstance == null) {
            throw new IllegalStateException(PreferencesManager.class.getSimpleName() +
                    " is not initialized, call initializeInstance(..) method first.");
        }
        return sInstance;
    }

    public void saveCity(String value) {
        mPref.edit()
                .putString(CITY, value)
                .apply();
    }

    public String getCity() {
        return mPref.getString(CITY, "");
    }

    public void saveLatitude(String value) {
        mPref.edit()
                .putString(LAT, value)
                .apply();
    }

    public void saveLongitude(String value) {
        mPref.edit()
                .putString(LON, value)
                .apply();
    }


    public String getLatitude() {
        return mPref.getString(LAT, "");
    }

    public String getLongitude() {
        return mPref.getString(LON, "");
    }

    public void remove(String key) {
        mPref.edit()
                .remove(key)
                .apply();
    }

    public boolean clear() {
        return mPref.edit()
                .clear()
                .commit();
    }
}
