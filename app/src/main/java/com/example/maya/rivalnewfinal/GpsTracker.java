package com.example.maya.rivalnewfinal;

/**
 * Created by Maya on 20/03/2016.
 */
import android.Manifest;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.*;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.List;

/**
 * Created by Maya on 18/02/2016.
 */
public class GpsTracker extends Service implements LocationListener {

    private final Context context;
    boolean isEnable = false;
    boolean canGetLocation = false;
    boolean isNetwork = false;
    double latitude;
    /** rohav*/
    double longitude;
    /** oreh */
    private static final long MIN_DIS_UPDATE = 10;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;
    protected LocationManager locationManager;
    Location location;

    public GpsTracker(Context context) {
        this.context = context;
        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        isEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (isAvilable())
            location = getLocation();
    }

    public boolean isAvilable() {
        if (isEnable && isNetwork)
            return true;
        return false;

    }

    public Location getLocation() {
        try {

            isEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (!isEnable && !isNetwork) {


            } else {
                this.canGetLocation = true;
                if (isNetwork) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DIS_UPDATE, this);

                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                if (isEnable) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DIS_UPDATE, this);
                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }

                }
            }
        } catch (Exception e) {

        }
        return location;

    }

    /**
     * try to get the 'best' location selected from all providers
     */
    public Location getBestLocation() {
        Location gpslocation=null;
        Location networkLocation = null;
        try{
            gpslocation = getLocationByProvider(LocationManager.GPS_PROVIDER);}
        catch (Exception e){}
        try {
            networkLocation = getLocationByProvider(LocationManager.NETWORK_PROVIDER); }
        catch (Exception e){}
        // if we have only one location available, the choice is easy
        if (gpslocation == null) {
            Log.d("", "No GPS Location available.");
            return networkLocation;
        }
        if (networkLocation == null) {
            Log.d("", "No Network Location available");
            return gpslocation;
        }
        // a locationupdate is considered 'old' if its older than the configured
        // update interval. this means, we didn't get a
        // update from this provider since the last check
        long old = System.currentTimeMillis() -120000;
        boolean gpsIsOld = (gpslocation.getTime() < old);
        boolean networkIsOld = (networkLocation.getTime() < old);
        // gps is current and available, gps is better than network
        if (!gpsIsOld) {
            Log.d("", "Returning current GPS Location");
            return gpslocation;
        }
        // gps is old, we can't trust it. use network location
        if (!networkIsOld) {
            Log.d("", "GPS is old, Network is current, returning network");
            return networkLocation;
        }
        // both are old return the newer of those two
        if (gpslocation.getTime() > networkLocation.getTime()) {
            Log.d("", "Both are old, returning gps(newer)");
            return gpslocation;
        } else {
            Log.d("", "Both are old, returning network(newer)");
            return networkLocation;
        }
    }


    /**
     * get the last known location from a specific provider (network/gps)
     */
    public boolean isProviderSupported(String in_Provider) {
        //LocationManager locationManager = getLocationManager();
		/* locals */
        int lv_N;
        List<String> lv_List;

        // isProviderEnabled should throw a IllegalArgumentException if
        // provider is not
        // supported
        // But in sdk 1.1 the exception is catched by isProviderEnabled itself.
        // Therefore check out the list of providers instead (which indeed does
        // not
        // report a provider it does not exist in the device) Undocumented is
        // that
        // this call can throw a SecurityException
        try {
            lv_List = locationManager.getAllProviders();
        } catch (Throwable e) {
            return false;
        }

        // scan the list for the specified provider
        for (lv_N = 0; lv_N < lv_List.size(); ++lv_N)
            if (in_Provider.equals((String) lv_List.get(lv_N)))
                return true;

        // not supported
        return false;
    }
    private Location getLocationByProvider(String provider) {
        Location location = null;
        if (!isProviderSupported(provider)) {
            return null;
        }
        //   LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        try {
            if (locationManager.isProviderEnabled(provider)) {
                try{
                    location = locationManager.getLastKnownLocation(provider);}
                catch (Exception e){}
            }
        } catch (IllegalArgumentException e) {
            Log.d("", "Cannot acces Provider " + provider);
        }
        return location;
    }

    public void stopUsingGPS() {
        if (locationManager != null) {
       try{
            locationManager.removeUpdates(GpsTracker.this);}
       catch (Exception e){}
        }
    }

    public double getLatitude()
    {
        location = this.getLocation();
        if(location!=null)
            latitude=location.getLatitude();
        return latitude;
    }
    public double getLongitude()
    {
        location = this.getLocation();
        if(location!=null)
            longitude=location.getLongitude();
        return longitude;
    }
    public boolean canGetLocation(){
        return this.canGetLocation;
    }
    public void showSettingsAlert()
    {
        AlertDialog.Builder aldi=new AlertDialog.Builder(context);
        aldi.setTitle("GPS is settings");
        aldi.setMessage("GPS is not enabled.Do you want to go to settings menu?");
        aldi.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(intent);
            }
        });
        aldi.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        aldi.show();

    }
    @Override
    public void onLocationChanged(Location location) {

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

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}