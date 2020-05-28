package com.example.rsr.maps;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.List;
import java.util.Locale;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class MapsPresenterImplementation extends AppCompatActivity implements MapsPresenter{

    public GoogleMap mMap;
    public final float DEFAULT_ZOOM = 15;
    public PlacesClient placesClient;
    Geocoder geo;
    public Location mLastKnownLocation;
    public LocationCallback locationCallback;
    MapsView mapsView;

    public MapsPresenterImplementation(MapsView mapsView,Geocoder geo) {
            this.mapsView=mapsView;
            this.geo=geo;
        }

    // Getting current location address
    public String getCompleteAddress(double Latitude, double Longitude){

        String address="";

        try{
            List<Address> addresses = geo.getFromLocation(Latitude,Longitude,1);
            if(address!=null)
            {
                Address returnAddress = addresses.get(0);
                StringBuilder stringBuilderReturnAddress = new StringBuilder("");

                for(int i=0; i<=returnAddress.getMaxAddressLineIndex(); i++)
                {
                    stringBuilderReturnAddress.append(returnAddress.getAddressLine(i)).append("\n");
                }
                address =stringBuilderReturnAddress.toString();

            }
            else{
                Toast.makeText(this, "Address not found", Toast.LENGTH_SHORT).show();
            }

        }
        catch (Exception e)
        {
            Toast.makeText(this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
        }
        return address;
    }


}
