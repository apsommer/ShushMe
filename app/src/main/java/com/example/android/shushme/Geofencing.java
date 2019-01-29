package com.example.android.shushme;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;

import java.util.ArrayList;
import java.util.List;

// TODO (1) Create a Geofencing class with a Context and GoogleApiClient constructor that
// initializes a private member ArrayList of Geofences called mGeofenceList

public class Geofencing implements ResultCallback {

    private List<Geofence> mGeofenceList;
    private PendingIntent mGeofencePendingIntent;
    private GoogleApiClient mGoogleApiClient;
    private Context mContext;

    public Geofencing(Context context, GoogleApiClient googleApiClient) {
        mContext = context;
        mGoogleApiClient = googleApiClient;
        mGeofenceList = null;
        mGeofencePendingIntent = null;
    }

    // TODO (2) Inside Geofencing, implement a public method called updateGeofencesList that
    // given a PlaceBuffer will create a Geofence object for each Place using Geofence.Builder
    // and add that Geofence to mGeofenceList

    public void updateGeofenceList(PlaceBuffer placeBuffer) {

        mGeofenceList = new ArrayList<>();
        if (placeBuffer == null || placeBuffer.getCount() == 0) return;

        for (Place place: placeBuffer) {

            String placeID = place.getId();
            double placeLat = place.getLatLng().latitude;
            double placeLong = place.getLatLng().longitude;

            Geofence geofence = new Geofence.Builder()
                    .setRequestId(placeID)
                    .setExpirationDuration(100)
                    .setCircularRegion(placeLat, placeLong, 50)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build();

            mGeofenceList.add(geofence);

        }

    }

    // TODO (3) Inside Geofencing, implement a private helper method called getGeofencingRequest that
    // uses GeofencingRequest.Builder to return a GeofencingRequest object from the Geofence list

    private GeofencingRequest getGeofencingRequest() {

        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();

    }

    // TODO (5) Inside Geofencing, implement a private helper method called getGeofencePendingIntent that
    // returns a PendingIntent for the GeofenceBroadcastReceiver class

    private PendingIntent getmGeofencePendingIntent() {

        if (mGeofencePendingIntent != null) return mGeofencePendingIntent;

        Intent intent = new Intent(mContext, GeofenceBroadcastReciever.class);
        mGeofencePendingIntent = PendingIntent.getBroadcast(
                mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        return mGeofencePendingIntent;
    }

    // TODO (6) Inside Geofencing, implement a public method called registerAllGeofences that
    // registers the GeofencingRequest by calling LocationServices.GeofencingApi.addGeofences
    // using the helper functions getGeofencingRequest() and getGeofencePendingIntent()

    public void registerAllGeofences() {

        // ensure inputs are valid
        if (mGoogleApiClient == null || mGoogleApiClient.isConnected()
                || mGeofenceList == null || mGeofenceList.isEmpty()) {
            return;
        }

        try {
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient, getGeofencingRequest(), getmGeofencePendingIntent());
        } catch (SecurityException e) {
            Log.e("~~", e.getMessage());
        }

    }

    // TODO (7) Inside Geofencing, implement a public method called unRegisterAllGeofences that
    // unregisters all geofences by calling LocationServices.GeofencingApi.removeGeofences
    // using the helper function getGeofencePendingIntent()

    public void unRegisterAllGeofences() {

        // ensure inputs are valid
        if (mGoogleApiClient == null || mGoogleApiClient.isConnected()) return;

        try {
            LocationServices.GeofencingApi.removeGeofences(
                    mGoogleApiClient, getmGeofencePendingIntent()).setResultCallback(this);
        } catch (SecurityException e) {
            Log.e("~~", e.getMessage());
        }

    }

    @Override
    public void onResult(@NonNull Result result) {
        Log.e("~~", result.getStatus().toString());
    }
}
