package com.example.rsr.maps;


import android.location.Location;

public interface MapsView {

    void getDeviceLocation();

    void showDialog();

    void updateUi(Location loc);

}
