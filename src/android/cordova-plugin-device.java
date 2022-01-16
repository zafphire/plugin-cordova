package com.zafphire.greenGo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import android.app.PendingIntent;
import android.content.Intent;
import android.support.design.widget.Snackbar;


import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import static xdroid.toaster.Toaster.toast;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.preference.PreferenceManager;
import android.util.Log;
import com.google.android.gms.location.LocationResult;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


import static android.content.Context.NOTIFICATION_SERVICE;



import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.text.DateFormat;
import java.util.Date;

import android.content.Context;
import android.content.SharedPreferences;


/**
 * Created by FreakyJolly on 01-04-2018.
 */

public class LocationRequestHelper {


    private static LocationRequestHelper mSharedPreferenceUtils;
    protected Context mContext;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mSharedPreferencesEditor;

    private LocationRequestHelper(Context context) {
        mContext = context;
        mSharedPreferences = context.getSharedPreferences("com.freakyjolly.demobackgroundlocation", Context.MODE_PRIVATE);
        mSharedPreferencesEditor = mSharedPreferences.edit();
    }

    public static synchronized LocationRequestHelper getInstance(Context context) {

        if (mSharedPreferenceUtils == null) {
            mSharedPreferenceUtils = new LocationRequestHelper(context.getApplicationContext());
        }
        return mSharedPreferenceUtils;
    }

    /**
     * Stores String value in preference
     */
    public void setValue(String key, String value) {
        mSharedPreferencesEditor.putString(key, value);
        mSharedPreferencesEditor.commit();
    }

    /**
     * Stores int value in preference
     */
    public void setValue(String key, int value) {
        mSharedPreferencesEditor.putInt(key, value);
        mSharedPreferencesEditor.commit();
    }

    /**
     * Stores Double value in String format in preference
     */
    public void setValue(String key, double value) {
        setValue(key, Double.toString(value));
    }

    /**
     * Stores long value in preference
     */
    public void setValue(String key, long value) {
        mSharedPreferencesEditor.putLong(key, value);
        mSharedPreferencesEditor.commit();
    }

    /**
     * Stores boolean value in preference
     */
    public void setValue(String key, boolean value) {
        mSharedPreferencesEditor.putBoolean(key, value);
        mSharedPreferencesEditor.commit();
    }

    /**
     * Retrieves String value from preference
     */
    public String getStringValue(String key, String defaultValue) {
        return mSharedPreferences.getString(key, defaultValue);
    }

    /**
     * Retrieves int value from preference
     */
    public int getIntValue(String key, int defaultValue) {
        return mSharedPreferences.getInt(key, defaultValue);
    }

    /**
     * Retrieves long value from preference
     */
    public long getLongValue(String key, long defaultValue) {
        return mSharedPreferences.getLong(key, defaultValue);
    }

    /**
     * Retrieves boolean value from preference
     */
    public boolean getBoolanValue(String keyFlag, boolean defaultValue) {
        return mSharedPreferences.getBoolean(keyFlag, defaultValue);
    }

    /**
     * Removes key from preference
     *
     * @param key key of preference that is to be deleted
     */
    public void removeKey(String key) {
        if (mSharedPreferencesEditor != null) {
            mSharedPreferencesEditor.remove(key);
            mSharedPreferencesEditor.commit();
        }
    }

    /**
     * Clears all the preferences stored
     */
    public void clear() {
        mSharedPreferencesEditor.clear().commit();
    }

}



public class LocationUpdatesBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "LUBroadcastReceiver";
    public static final String ACTION_PROCESS_UPDATES =
            "com.freakyjolly.demobackgroundlocation.action" + ".PROCESS_UPDATES";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {

            final String action = intent.getAction();
            if (ACTION_PROCESS_UPDATES.equals(action)) {
                Utils.setLocationUpdatesResult(context, DateFormat.getDateTimeInstance().format(new Date()));
                Utils.getLocationUpdates(context,intent,"PROCESS_UPDATES");

            }
        }
    }
}


public class Utils {
    private static final String TAG = "UtilsClass";
    final static String KEY_LOCATION_UPDATES_RESULT = "location-update-result";
    public static float accuracy;
    static String addressFragments = "";
    static List<Address> addresses = null;
    public static final long UPDATE_INTERVAL = 3 * 1000;
    public static final float SMALLEST_DISPLACEMENT = 1.0F;
    public static final long FASTEST_UPDATE_INTERVAL = UPDATE_INTERVAL / 2;
    public static final long MAX_WAIT_TIME = UPDATE_INTERVAL * 2;

    static void setLocationUpdatesResult(Context context, String value) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(KEY_LOCATION_UPDATES_RESULT, value)
                .apply();
    }

    @SuppressLint("MissingPermission")
    public static void getLocationUpdates(final Context context, final Intent intent, String broadcastevent)  {

        LocationResult result = LocationResult.extractResult(intent);
        if (result != null) {

            Date today = Calendar.getInstance().getTime();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss");
            String nowDate = formatter.format(today);


            List<Location> locations = result.getLocations();
            Location firstLocation = locations.get(0);

            getAddress(firstLocation,context);
            //firstLocation.getAccuracy();
            //firstLocation.getLatitude();
            //firstLocation.getLongitude();
            //firstLocation.getAccuracy();
            //firstLocation.getSpeed();
            //firstLocation.getBearing();
            LocationRequestHelper.getInstance(context).setValue("locationTextInApp","You are at "+getAddress(firstLocation,context)+"("+nowDate+") with accuracy "+firstLocation.getAccuracy()+" Latitude:"+firstLocation.getLatitude()+" Longitude:"+firstLocation.getLongitude()+" Speed:"+firstLocation.getSpeed()+" Bearing:"+firstLocation.getBearing());
            //showNotificationOngoing(context, broadcastevent,"");
        }
    }

}

public class MainActivity extends AppCompatActivity  implements
        SharedPreferences.OnSharedPreferenceChangeListener {


    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private SettingsClient mSettingsClient;
    private LocationSettingsRequest mLocationSettingsRequest;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private ActivityRecognitionClient mActivityRecognitionClient;
    private LocationCallback mLocationCallback;
    private Button mRequestUpdatesButton;
    private Button mRemoveUpdatesButton;
    private static TextView mLocationUpdatesResultView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        createLocationRequest();
        buildLocationSettingsRequest();
    }


    @Override
    protected void onStart() {
        super.onStart();
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);

        if (!checkPermissions()) {
            requestPermissions();
        } else {
        }
    }


    @Override
    protected void onStop() {
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        toast("GPS turned on");
                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            toast("Please Provide Location Permission.");
                            return;
                        }
                        changeStatusAfterGetLastLocation("1","Manual");

                        break;
                    case Activity.RESULT_CANCELED:
                        if (!checkPermissions()) {
                            requestPermissions();
                        }
                        toast("GPS is required to Start Tracking");
                        break;
                }
                break;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                Log.i(TAG, "User interaction was cancelled.");
                requestPermissions();
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                // Permission denied.
                Snackbar.make(
                        findViewById(R.id.activity_main),
                        R.string.permission_denied_explanation,
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.settings, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        })
                        .show();
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        updateTextField(this);
    }

    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(Utils.UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(Utils.FASTEST_UPDATE_INTERVAL);

        //mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        //mLocationRequest.setSmallestDisplacement(Utils.SMALLEST_DISPLACEMENT);
        //mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
        mLocationRequest.setMaxWaitTime(Utils.MAX_WAIT_TIME);
    }

    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(this, LocationUpdatesBroadcastReceiver.class);
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        intent.setAction(LocationUpdatesBroadcastReceiver.ACTION_PROCESS_UPDATES);
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION);

        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");

            showSnackbar(R.string.permission_rationale, android.R.string.ok,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            startLocationPermissionRequest();
                        }
                    });

        } else {
            Log.i(TAG, "Requesting permission");
            startLocationPermissionRequest();
        }
    }

    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }

    private void startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                REQUEST_PERMISSIONS_REQUEST_CODE);
    }



    public void requestLocationUpdates() {
        if (!checkPermissions()) {
        
            requestPermissions();
            return;
        }
        try {
            mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                    .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                        @Override
                        public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                            //toast("All location settings are satisfied.");

                            changeStatusAfterGetLastLocation("1","Manual");
                        }
                    })
                    .addOnFailureListener(this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            int statusCode = ((ApiException) e).getStatusCode();
                            switch (statusCode) {
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                            "location settings ");
                                    try {
                                        ResolvableApiException rae = (ResolvableApiException) e;
                                        rae.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                                    } catch (IntentSender.SendIntentException sie) {
                                        Log.i(TAG, "PendingIntent unable to execute request.");
                                    }
                                    break;
                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                    String errorMessage = "Location settings are inadequate, and cannot be " +
                                            "fixed here. Fix in Settings.";
                                    Log.e(TAG, errorMessage);
                                    Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                                case LocationSettingsStatusCodes.DEVELOPER_ERROR:
                                    Log.e(TAG, "DEVELOPER_ERROR");
                            }
                        }
                    });

        } catch (SecurityException e) {
            LocationRequestHelper.getInstance(getApplicationContext()).setValue("RequestingLocationUpdates",false);
            e.printStackTrace();
        }
    }

    public void removeLocationUpdates() {
        changeStatusAfterGetLastLocation("0","Manual");
    }

    public static void updateTextField(Context context) {
        //mLocationUpdatesResultView.setText(LocationRequestHelper.getInstance(context).getStringValue("locationTextInApp",""));
    }

    @SuppressLint("MissingPermission")
    private void changeStatusAfterGetLastLocation(final String value, final String changeby) {
            if(value == "1"){
                toast("Location Updates Started!");

                mFusedLocationClient.requestLocationUpdates(mLocationRequest, getPendingIntent());
                LocationRequestHelper.getInstance(getApplicationContext()).setValue("RequestingLocationUpdates",true);

                Task<Void> task = mActivityRecognitionClient.requestActivityUpdates(
                        Utils.UPDATE_INTERVAL,
                        getPendingIntent());

                task.addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void result) {

                    }
                });
                task.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, "addOnFailureListener mActivityRecognitionClient "+e);
                    }
                });

            }else if(value == "0"){

                LocationRequestHelper.getInstance(getApplicationContext()).setValue("RequestingLocationUpdates",false);
                mFusedLocationClient.removeLocationUpdates(getPendingIntent());
                Utils.removeNotification(getApplicationContext());

                toast("Location Updates Stopped!");

                Task<Void> task = mActivityRecognitionClient.removeActivityUpdates(
                        getPendingIntent());
                task.addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                    }
                });
                task.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, "removeActivityUpdates addOnFailureListener "+e);

                    }
                });
            }
            updateButtonsState(LocationRequestHelper.getInstance(this).getBoolanValue("RequestingLocationUpdates", false));
    }

    public void updateButtonsState(boolean requestingLocationUpdates) {

        if (requestingLocationUpdates) {
            //mRequestUpdatesButton.setVisibility(View.GONE);
            //mRemoveUpdatesButton.setVisibility(View.VISIBLE);
        } else {
            //mRequestUpdatesButton.setVisibility(View.VISIBLE);
            //mRemoveUpdatesButton.setVisibility(View.GONE);
        }
    }

}
