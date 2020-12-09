package com.shashanksrikanth.knowyourgovernment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final ArrayList<Official> officials = new ArrayList<>();
    private RecyclerView recyclerView;
    private OfficialAdapter adapter;
    private static int LOCATION_REQUEST_CODE_ID = 111;
    private LocationManager locationManager;
    private Criteria criteria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        adapter = new OfficialAdapter(officials, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Location permissions
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE_ID);
        }
        else setLocation();
    }

    @Override
    public void onClick(View v) {
        // Logic that dictates what to do when you click on an official
        int index = recyclerView.getChildLayoutPosition(v);
        Official official = officials.get(index);
        Intent officialActivityIntent = new Intent(this, OfficialActivity.class);
        officialActivityIntent.putExtra("official", official);
        TextView locationBar = findViewById(R.id.locationBar);
        officialActivityIntent.putExtra("locationBar", locationBar.getText().toString());
        startActivity(officialActivityIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Logic that dictates what to do when a menu item is pressed
        switch(item.getItemId()) {
            case R.id.about:
                Intent aboutIntent = new Intent(this, AboutActivity.class);
                startActivity(aboutIntent);
                return true;
            case R.id.search:
                chooseLocation();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void chooseLocation() {
        // Helper function that lets the user choose the location
        if(!checkNetworkConnection()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Data cannot be accessed/loaded without an internet connection");
            builder.setTitle("No network connection");
            AlertDialog dialog = builder.create();
            dialog.show();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText editText = new EditText(this);
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        editText.setGravity(Gravity.CENTER_HORIZONTAL);
        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        builder.setView(editText);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String locationQuery = editText.getText().toString().trim();
                officials.clear();
                adapter.notifyDataSetChanged();
                CivicInformationDownloader downloader = new CivicInformationDownloader(MainActivity.this, locationQuery);
                new Thread(downloader).start();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
            }
        });
        builder.setTitle("Location Search");
        builder.setMessage("Enter a City or Zip Code:");
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private boolean checkNetworkConnection() {
        // Helper function that checks if device is connected to the network
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == LOCATION_REQUEST_CODE_ID) {
            if(permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION) && grantResults[0] == PERMISSION_GRANTED) {
                setLocation();
                return;
            }
        }
        TextView locationBar = findViewById(R.id.locationBar);
        locationBar.setText(R.string.location_warning);
    }

    @SuppressLint("MissingPermission")
    private void setLocation() {
        // Helper function that sets the location and downloads appropriate candidate info
        String bestProvider = locationManager.getBestProvider(criteria, true);
        Location currentLocation = null;
        if(bestProvider != null) {
            currentLocation = locationManager.getLastKnownLocation(bestProvider);
        }
        if(currentLocation != null) {
            double longitude = currentLocation.getLongitude();
            double latitude = currentLocation.getLatitude();
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try{
                List<Address> locations = geocoder.getFromLocation(latitude, longitude, 1);
                Address address = locations.get(0);
                String zip = address.getPostalCode();
                CivicInformationDownloader downloader = new CivicInformationDownloader(this, zip);
                new Thread(downloader).start();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            TextView locationBar = findViewById(R.id.locationBar);
            locationBar.setText(R.string.location_warning);
        }
    }

    public void setLocationBar(String location) {
        // Helper function that sets the text of the location bar
        TextView locationBar = findViewById(R.id.locationBar);
        locationBar.setText(location);
    }

    public void addOfficials(ArrayList<Official> officialsList) {
        for(Official official : officialsList) officials.add(official);
        adapter.notifyDataSetChanged();
    }
}