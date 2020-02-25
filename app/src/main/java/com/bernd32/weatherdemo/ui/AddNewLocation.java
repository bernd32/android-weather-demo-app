package com.bernd32.weatherdemo.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bernd32.weatherdemo.LocationsDao;
import com.bernd32.weatherdemo.LocationsRoomDatabase;
import com.bernd32.weatherdemo.R;
import com.bernd32.weatherdemo.models.UserLocation;
import com.bernd32.weatherdemo.ui.adapters.LocationsRecyclerAdapter;
import com.facebook.stetho.Stetho;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * In this activity user can add a new location manually or choose existing locations.
 * Also here we load/save this data in a local database using Room
 */

public class AddNewLocation extends AppCompatActivity {

    private static final String TAG = "AddNewLocation";
    private RecyclerView mRecyclerView;
    private LocationsDao mLocationsDao;
    private EditText cityEdit;
    private CompositeDisposable mDisposable;
    private LocationsRecyclerAdapter adapter;
    private LocationsRoomDatabase db;
    private Button addButton;
    private TextView emptyMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_location);
        Stetho.initializeWithDefaults(this);
        AddNewLocation.this.setTitle(getString(R.string.add_new_location));
        cityEdit = findViewById(R.id.city_edit_text);
        addButton = findViewById(R.id.button_add);
        emptyMsg = findViewById(R.id.empty_message);
        mRecyclerView = findViewById(R.id.recycler_view_locations);
        mRecyclerView.setHasFixedSize(true);
        adapter = new LocationsRecyclerAdapter(this, new ArrayList<>());
        mRecyclerView.setAdapter(adapter);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        db = LocationsRoomDatabase.getDatabase(this);
        mLocationsDao = db.locationsDao();
        mDisposable = new CompositeDisposable();
        addButton.setEnabled(false);
        cityEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().trim().length()==0){
                    addButton.setEnabled(false);
                } else {
                    addButton.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        mDisposable.add(
            mLocationsDao.getAllLocations()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<UserLocation>>() {
                    @Override
                    public void accept(List<UserLocation> userLocations) throws Exception {
                        adapter.addItems(userLocations);
                        // Show a message if the list is empty
                        emptyMsg.setVisibility(adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
                    }
                    }));

        deleteItemBySwipe();
    }

    private void deleteItemBySwipe() {
        ItemTouchHelper helper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(0,
                        ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView,
                                          @NonNull RecyclerView.ViewHolder viewHolder,
                                          @NonNull RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    // Delete an item from the database.
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                        int position = viewHolder.getAdapterPosition();
                        UserLocation location = adapter.getItem(position);
                        Toast.makeText(AddNewLocation.this,
                                getString(R.string.delete_item_msg),
                                Toast.LENGTH_SHORT).show();
                        deleteLocation(location);
                    }
                });
        // Attach the item touch helper to the recycler view
        helper.attachToRecyclerView(mRecyclerView);
    }

    private void deleteLocation(UserLocation location) {
        Completable.fromAction(() -> {
                mLocationsDao.deleteLocation(location);
            })
            .subscribeOn(Schedulers.io())
            .subscribe(new DisposableCompletableObserver() {
                @Override
                public void onComplete() {
                }

                @Override
                public void onError(Throwable e) {
                    Log.e(TAG, "onError: ", e.fillInStackTrace());
                    showInformationDialog(getString(R.string.error), e.getLocalizedMessage());
                }
            });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDisposable.dispose();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_new_location_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.delete_all) {
            deleteAllItems();
            Toast.makeText(this, getString(R.string.all_items_deleted_msg), Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteAllItems() {
        Completable.fromAction(() -> {
            mLocationsDao.deleteAll();
        })
                .subscribeOn(Schedulers.io())
                .subscribe(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: ", e.fillInStackTrace());
                        showInformationDialog(getString(R.string.error), e.getLocalizedMessage());
                    }
                });
    }

    public void onAddButton(View view) {
        String city = cityEdit.getText().toString();

        Completable.fromAction(() -> {
            UserLocation userLocation = new UserLocation(city);
            LocationsRoomDatabase.databaseWriteExecutor.execute(() -> {
                mLocationsDao.insert(userLocation);
            });
        }).subscribe(new DisposableCompletableObserver() {
            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete: done!");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: ", e.fillInStackTrace());
            }
        });
    }

    public void showInformationDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(AddNewLocation.this);
        builder.setTitle(title)
                .setMessage(message)
                .setIcon(R.drawable.ic_info_outline_black_24dp)
                .setCancelable(true)
                .setNegativeButton("OK", (dialog, id) -> dialog.cancel());
        AlertDialog alert = builder.create();
        alert.show();
    }
}
