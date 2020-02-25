package com.bernd32.weatherdemo;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.bernd32.weatherdemo.models.UserLocation;

import java.util.List;

import io.reactivex.Flowable;

/**
 * This class is used for accessing the database by defining DAO methods
 * https://developer.android.com/training/data-storage/room/accessing-data
 */

@Dao
public interface LocationsDao {

    // Notifies its active observers when the data has changed.
    @Query("SELECT * FROM locations")
    Flowable<List<UserLocation>> getAllLocations();

    @Query("SELECT * from locations ORDER BY id DESC")
    Flowable<List<UserLocation>> getLocationsById();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(UserLocation location);

    @Query("DELETE FROM locations")
    void deleteAll();

    @Delete
    void deleteLocation(UserLocation userLocation);

    @Query("DELETE FROM locations WHERE id = :id")
    void deleteLocationById(int id);
}