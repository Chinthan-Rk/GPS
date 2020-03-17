package com.chinthanrk.gps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView editLocation;
    private ProgressBar progressBar;

    private static final String TAG = "DEBUG";

    private LocationManager locationManager = null;
    private LocationListener locationListener = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

              progressBar = findViewById(R.id.progressBar1);
        editLocation = findViewById(R.id.textView2);
        Button button = findViewById(R.id.button1);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (GPS_STATUS()) {
                    Log.v(TAG, "Button CLICKED");
                    progressBar.setVisibility(View.VISIBLE);
                    editLocation.setText("");



                    locationListener = new MyLocationListener();

                    checkLocationPermission();


                } else {
                    alert();
                }
            }
        });


    }

    private void checkLocationPermission() {

        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                new AlertDialog.Builder(this)
                        .setTitle("LOCATION PERMISSION")
                        .setMessage("PLEASE GIVE PERMISSIONS DUDE")
                        .setPositiveButton("AAV AAV", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        99);
                            }
                        })
                        .create()
                        .show();
            }
            else{
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        99);
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 99 : {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
                        //Request location updates:
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
            }
        }
    }

    private void alert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(" YOUR GPS IS TURNED OFF , YOU DUMBFUCK !!")
                .setCancelable(false)
                .setTitle("GPS STATUS !!!")
                .setPositiveButton("Turn on GPS", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_SECURITY_SETTINGS));
                        dialog.cancel();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private Boolean GPS_STATUS() {
        ContentResolver contentResolver = getBaseContext().getContentResolver();
        return Settings.Secure.isLocationProviderEnabled(contentResolver, LocationManager.GPS_PROVIDER);
    }

    private class MyLocationListener implements LocationListener {

        @SuppressLint("SetTextI18n")
        @Override
        public void onLocationChanged(Location location) {

            String longitude = "" + location.getLongitude();
            String latitude = "" + location.getLatitude();

            editLocation.setText("");
            progressBar.setVisibility(View.INVISIBLE);

            Toast.makeText(MainActivity.this, "LOCATION CHANGED\nLATITUDE : " + latitude + "\nLONGITUDE : " + longitude, Toast.LENGTH_SHORT).show();

            String city = null;
            Geocoder geocoder = new Geocoder(getBaseContext(), Locale.getDefault());
            List<Address> addresses;

            try {
                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if (addresses.size() > 0) {
                    System.out.println(addresses.get(0).getLocality());
                    city = addresses.get(0).getLocality();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            editLocation.setText(String.format("LOCATION CHANGED\nLATITUDE : %s\nLONGITUDE : %s\nCity : %s", latitude, longitude, city));


        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }
}


