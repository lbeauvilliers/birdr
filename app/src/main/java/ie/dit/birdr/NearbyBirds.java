package ie.dit.birdr;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

import models.Bird;
import models.Sightings;

public class NearbyBirds extends FragmentActivity implements OnMapReadyCallback {

    //for the map
    private GoogleMap mMap;
    private Marker myMarker;
    private FusedLocationProviderClient mFusedLocationClient;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    //for reading from db
    DatabaseReference rootReference;
    DatabaseReference sightingsReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_birds);

        //reading from db
        rootReference = FirebaseDatabase.getInstance().getReference();
        sightingsReference = rootReference.child("sightings");

        //for the map
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapAll);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {

            @Override
            public void onMarkerDragStart(Marker myMarker) {
            }

            @Override
            public void onMarkerDragEnd(Marker myMarker) {
            }

            @Override
            public void onMarkerDrag(Marker myMarker) {
            }
        });


        // REQUESTING PERMISSIONS
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);

        }

        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {

                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            Double lat = location.getLatitude();
                            Double lon = location.getLongitude();
                            LatLng here = new LatLng(lat, lon);
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(here));
                            mMap.moveCamera( CameraUpdateFactory.zoomTo( 13.0f ) );
                        } else {
                            LatLng dublin = new LatLng(53.349, -6.260);
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(dublin));
                            mMap.moveCamera( CameraUpdateFactory.zoomTo( 13.0f ) );
                        }

                        getSightingData();
                    }
                }
        );
    }

    private void getSightingData(){

        //get bird lats, lons, names from firebase
        sightingsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot sightingSnapshot: dataSnapshot.getChildren()){
                    Sightings sighting = sightingSnapshot.getValue(Sightings.class);

                    Double lat = sighting.getsLat();
                    Double lon = sighting.getsLong();
                    String name = sighting.getBird_name();

                    LatLng birdLoc = new LatLng(lat,lon);

                    mMap.addMarker(new MarkerOptions()
                            .position(birdLoc)
                            .title(name));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
