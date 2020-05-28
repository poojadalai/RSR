package com.example.rsr;

import androidx.annotation.ContentView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rsr.maps.MapsActivity;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class MainActivity extends AppCompatActivity {
    boolean mIsDualPane;
    Toolbar toolbar;
    AlertDialog.Builder builder;
    Button btn_rsr_pechhulp,privacy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        builder = new AlertDialog.Builder(this);
        privacy = findViewById(R.id.btn_privacy);

        btn_rsr_pechhulp = findViewById(R.id.btn_rsr_pechhulp);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("RSR Revalidatieservice");

        // Shows privacy dialog form main menu tablet screen
        View privacy = findViewById(R.id.privacy);
        mIsDualPane = privacy != null && privacy.getVisibility() == View.VISIBLE;

        // Navigation between phone screen to tablet screen
        if (mIsDualPane) {
           privacy.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   builder.setMessage(R.string.privacydialog_message)
                           .setCancelable(false)
                           .setPositiveButton("BEVESTING", new DialogInterface.OnClickListener() {
                               public void onClick(DialogInterface dialog, int id) {
                                   dialog.dismiss();
                               }
                           });

                   AlertDialog alert = builder.create();
                   alert.setTitle(R.string.dialog_title);
                   alert.show();
                   // Make the textview clickable. Must be called after show()
                   ((TextView)alert.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());

               }
           });
        }
        else {
        }

        // Go to RSR Pechhulp screen from main menu On button click of RSR Pechhulp
        btn_rsr_pechhulp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Dexter.withActivity(MainActivity.this)
                        .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse response) {
                                Intent i = new Intent(MainActivity.this, com.example.rsr.maps.MapsActivity.class);
                                startActivity(i);

                            }

                            //Getting enable location permission from the user using Alert Dialog
                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse response) {
                                if (response.isPermanentlyDenied()) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                    builder.setTitle("Permission Denied")
                                            .setMessage("Permission to access device location is permanently denied. you need to go to setting to allow the permission.")
                                            .setNegativeButton("Cancel", null)
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Intent intent = new Intent();
                                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                                    intent.setData(Uri.fromParts("package", getPackageName(), null));
                                                }
                                            })
                                            .show();
                                } else {
                                    Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();

                                }
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                token.continuePermissionRequest();
                            }
                        })
                        .check();

                 }

            });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        // Show privacy dialog message of main menu screen toolbar button 'i'
        if (id == R.id.privacy) {
            builder.setMessage(R.string.privacydialog_message)
                    .setCancelable(false)
                    .setPositiveButton("BEVESTING", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });

            AlertDialog alert = builder.create();
            alert.setTitle(R.string.dialog_title);
            alert.show();

           // Make the textview clickable. Must be called after show()
            ((TextView)alert.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
