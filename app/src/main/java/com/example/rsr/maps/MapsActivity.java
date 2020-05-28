package com.example.rsr.maps;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.rsr.ContactActivity;
import com.example.rsr.MainActivity;
import com.example.rsr.R;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, MapsView, View.OnClickListener {

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private final float DEFAULT_ZOOM = 15;
    private PlacesClient placesClient;
    private Location mLastKnownLocation;
    private LocationCallback locationCallback;
    Geocoder geo;
    GoogleMap mMap;
    private Button btn_back, btn_bel_rsrnu, btn_close, btn_call,contact_rsr;
    boolean mIsDualPane;
    Toolbar toolbar_maps;
    Context context;
    Dialog dialog;
    private MapsPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MapsActivity.this);
        Places.initialize(MapsActivity.this, getString(R.string.google_maps_key));
        placesClient = Places.createClient(this);
        final AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();


        toolbar_maps = findViewById(R.id.tool_map);
        btn_back = findViewById(R.id.bck);
        btn_bel_rsrnu = findViewById(R.id.bel);
        contact_rsr = findViewById(R.id.contact_rsr);
        dialog = new Dialog(this);
        context = this;

        geo = new Geocoder(MapsActivity.this,Locale.getDefault());
        presenter = new MapsPresenterImplementation(this,geo);

        btn_back.setOnClickListener(this);

        // Shows contact RSR customer service for tablet view
        View contact = findViewById(R.id.contact_rsr);
        mIsDualPane = contact != null &&
                contact.getVisibility() == View.VISIBLE;

        // Navigation between phone screen to tablet screen
        if (mIsDualPane) {
            contact_rsr.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(MapsActivity.this, ContactActivity.class);
                    startActivity(i);
                }
            });
        }
        else {
            btn_bel_rsrnu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialog();

                }
            });
        }

    }
        // Go back to main menu screen
        @Override
        public void onClick(View v) {
            Intent i = new Intent(MapsActivity.this, MainActivity.class);
            startActivity(i);
        }

        // Displays the dialog for contact RSR customer service when BelRSRnu button pressed
        public void showDialog() {
            dialog.setContentView(R.layout.dialog_fragment);
            btn_close = dialog.findViewById(R.id.bnt_image);
            btn_call = dialog.findViewById(R.id.bel_nu);

            // close the dialog
            btn_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            // calls directly to RSR customer service
            btn_call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final int REQUEST_PHONE_CALL = 1;
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:09007788990"));
                    //Checks for permission before placing the call
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                        if(ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
                            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
                        }else{
                            //Places the call
                            startActivity(callIntent);
                        }
                    }

                }
            });
            Window window = dialog.getWindow();
            window.setGravity(Gravity.BOTTOM);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }


        @Override
        public void onMapReady(final GoogleMap googleMap) {
            mMap = googleMap;
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                // Use default InfoWindow frame
                @Override
                public View getInfoWindow(Marker arg0) {
                    return null;
                }
                // Defines the contents of the InfoWindow
                @Override
                public View getInfoContents(Marker arg0) {
                    View v = null;
                    try {
                        // Getting view from the layout file custom_info_window
                        v = getLayoutInflater().inflate(R.layout.custom_info_window, null);
                        TextView addressTxt = (TextView) v.findViewById(R.id.msg);
                        addressTxt.setText(arg0.getTitle());

                    } catch (Exception ev) {
                        System.out.print(ev.getMessage());
                    }
                    v.setBackground(new ColorDrawable(Color.TRANSPARENT));
                    return v;
                }
            });


            //check if gps is enabled or not and then request user to enable it
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setInterval(10000);
            locationRequest.setFastestInterval(5000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
            SettingsClient settingsClient = LocationServices.getSettingsClient(MapsActivity.this);
            Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());

            //if gps is enabled this function is called
            task.addOnSuccessListener(MapsActivity.this, new OnSuccessListener<LocationSettingsResponse>() {
                @Override
                public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                     getDeviceLocation();

                }
            });

            //if gps is not enabled this function is called
            task.addOnFailureListener(MapsActivity.this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if (e instanceof ResolvableApiException) {
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        try {
                            resolvable.startResolutionForResult(MapsActivity.this, 51);
                        } catch (IntentSender.SendIntentException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            });


        }

        @Override
        public void onActivityResult ( int requestCode, int resultCode, Intent data){
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == 51) {
                if (resultCode == RESULT_OK) {
                    getDeviceLocation();
                }
            }

        }

        // Marker custom view according to requirements.Custom marker style
        public void updateUi(Location loc) {
            MarkerOptions markerOptions= new MarkerOptions();
            LatLng latLng=new LatLng(loc.getLatitude(),loc.getLongitude());
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker));
            Marker marker = mMap.addMarker(markerOptions.position(latLng).title(presenter.getCompleteAddress(loc.getLatitude(),loc.getLongitude())));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15f));
            marker.showInfoWindow();
        }

        // Getting current location
        public void getDeviceLocation() {

        // Fetching user last location
        mFusedLocationProviderClient.getLastLocation()
                .addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() ) {
                            mLastKnownLocation = task.getResult();
                            if (mLastKnownLocation != null) {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                                updateUi(mLastKnownLocation);
                            } else {
                                // Request for an updated location
                                final LocationRequest locationRequest = LocationRequest.create();
                                locationRequest.setInterval(10000);
                                locationRequest.setFastestInterval(5000);
                                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                                // This function called after updated location is received
                                locationCallback = new LocationCallback() {
                                    @Override
                                    public void onLocationResult(LocationResult locationResult) {
                                        super.onLocationResult(locationResult);
                                        if (locationResult == null) {
                                            return;
                                        }
                                        mLastKnownLocation = locationResult.getLastLocation();
                                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                                    }
                                };
                                mFusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
                            }
                        } else {
                            Toast.makeText(MapsActivity.this, "Unable to get last location", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

}
