package com.techster_media.cesa;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomButtonsController;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.techster_media.cesa.SendNotificationPack.Data;
import com.techster_media.cesa.SendNotificationPack.MyFireBaseMessagingService;

import java.util.HashMap;

import kotlin.jvm.internal.BooleanSpreadBuilder;

public class viewlocation extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = viewlocation.class.getSimpleName();
    private HashMap<String, Marker> mMarkers = new HashMap<>();
    private GoogleMap mMap;
    LatLng  targetlocation;
    double lat,lng,mylat,mylng;
    String userid;
    Button ring;
    TextView myspeed, targetspeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewlocation);
        myspeed = findViewById(R.id.myspeed);
        ring = findViewById(R.id.ring);
        targetspeed = findViewById(R.id.targetspeed);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userid = extras.getString("userid");
            Log.d(TAG, "onCreate: " + userid);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Authenticate with Firebase when the Google map is loaded
        mMap = googleMap;
        mMap.setMaxZoomPreference(24);
        subscribeToUpdates();
    }

    private void subscribeToUpdates() {
        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mylat = Double.parseDouble(snapshot.child("inaccloc").child("latitude").getValue().toString());
                mylng = Double.parseDouble(snapshot.child("inaccloc").child("longitude").getValue().toString());
                if(snapshot.child("location").child("speed").exists())
                myspeed.setText("Your Speed: "+snapshot.child("location").child("speed").getValue().toString());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        FirebaseDatabase.getInstance().getReference().child("Users").child(userid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.child("location").child("speed").exists()) {
                    usermarker(snapshot);
                    targetspeed.setText("Target Speed: " + snapshot.child("location").child("speed").getValue().toString());
                }
                else
                {
                    lat= Double.parseDouble(snapshot.child("inaccloc").child("latitude").getValue().toString());
                    lng = Double.parseDouble(snapshot.child("inaccloc").child("longitude").getValue().toString());
                    String tempkey = snapshot.getKey();
                    usermarkerwithoutlocation(lat,lng,tempkey);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void usermarkerwithoutlocation(Double latitude, Double longitude, String key)
    {
        targetlocation = new LatLng(latitude, longitude);
        if (!mMarkers.containsKey(key)) {
            mMarkers.put(key, mMap.addMarker(new MarkerOptions().title(key).position(targetlocation).title("Target Location")));
        } else {
            mMarkers.get(key).setPosition(targetlocation);
        }
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : mMarkers.values()) {
            builder.include(marker.getPosition());
        }
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 300));
        mMap.isBuildingsEnabled();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().isCompassEnabled();
        mMap.getUiSettings().isZoomControlsEnabled();
        mMap.getUiSettings().isRotateGesturesEnabled();
        mMap.getUiSettings().isMyLocationButtonEnabled();
    }



    private void usermarker(DataSnapshot dataSnapshot) {

        String key = dataSnapshot.getKey();
        lat = Double.parseDouble(dataSnapshot.child("location").child("latitude").getValue().toString());
        lng = Double.parseDouble(dataSnapshot.child("location").child("longitude").getValue().toString());
        targetlocation = new LatLng(lat, lng);
        if (!mMarkers.containsKey(key)) {
            mMarkers.put(key, mMap.addMarker(new MarkerOptions().title(key).position(targetlocation).title("Target Location")));
        } else {
            mMarkers.get(key).setPosition(targetlocation);
        }
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : mMarkers.values()) {
            builder.include(marker.getPosition());
        }
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 300));
        mMap.isBuildingsEnabled();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().isCompassEnabled();
        mMap.getUiSettings().isZoomControlsEnabled();
        mMap.getUiSettings().isRotateGesturesEnabled();
        mMap.getUiSettings().isMyLocationButtonEnabled();
    }
    public void ringg(View view)
    {
        GPSTracker gps = new GPSTracker(viewlocation.this);
        if(gps.canGetLocation()) {
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("inaccloc").child("longitude").setValue(longitude);
            FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("inaccloc").child("latitude").setValue(latitude);
        }
        else{
            gps.showSettingsAlert();
        }
        if(distance(lat,lng,mylat,mylng)<0.03) {
            if (ring.getBackground().getConstantState()==getResources().getDrawable(R.drawable.ic_alarm_off).getConstantState()) {
                FirebaseDatabase.getInstance().getReference().child("Users").child(userid).child("ringer").setValue("true");
                ring.setBackgroundResource(R.drawable.ic_alarmon_foreground);
            } else if (ring.getBackground().getConstantState()==getResources().getDrawable(R.drawable.ic_alarmon_foreground).getConstantState()) {
                FirebaseDatabase.getInstance().getReference().child("Users").child(userid).child("ringer").setValue("false");
                ring.setBackgroundResource(R.drawable.ic_alarm_off);
            }
        }
        else
            Toast.makeText(viewlocation.this, "Please press after reaching the location", Toast.LENGTH_SHORT).show();

    }

    private double distance(double lat1, double lng1, double lat2, double lng2) {

        double earthRadius = 6371; // in miles, change to 6371 for kilometers

        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);

        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);

        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        double dist = earthRadius * c;

        return dist;
    }

}