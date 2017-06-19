package ie.dit.birdr;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.Manifest;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import models.Sightings;

public class CheckInBird extends FragmentActivity implements OnMapReadyCallback {

    //for associating with a bird and picture
    String birdName;
    String picture_name="null";
    String username = "null";
    String currentDate;

    //for writing to db
    DatabaseReference rootReference;
    DatabaseReference sightingsReference;

    //for the map
    private GoogleMap mMap;
    private Marker myMarker;
    private FusedLocationProviderClient mFusedLocationClient;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    //for the camera/photos
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    //for storing images to Firebase
    private Uri birdUri;
    private StorageReference mStorageRef;

    //for getting the comments
    private EditText birdComments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in_bird);

        //get username from singleton
        username = UserSingleton.getInstance().getUsername();

        //for associating with a bird
        birdName = getIntent().getExtras().getString("birdName","Blackbird");

        //for writing to db
        rootReference = FirebaseDatabase.getInstance().getReference();
        sightingsReference = rootReference.child("sightings");

        //for the map
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //for the image storage
        mStorageRef = FirebaseStorage.getInstance().getReference();

        //for getting the comments
        birdComments = (EditText) findViewById(R.id.birdComment);

        //for the date
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        currentDate = dateFormat.format(cal.getTime());

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
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
                Manifest.permission.ACCESS_COARSE_LOCATION)
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
                            myMarker = mMap.addMarker(new MarkerOptions()
                                    .position(here).draggable(true));
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(here));
                            mMap.moveCamera( CameraUpdateFactory.zoomTo( 13.0f ) );
                        } else {
                            LatLng dublin = new LatLng(53.349, -6.260);
                            myMarker = mMap.addMarker(new MarkerOptions()
                                    .position(dublin).draggable(true));
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(dublin));
                            mMap.moveCamera( CameraUpdateFactory.zoomTo( 13.0f ) );
                        }
                    }
                });
    }


    //**Check-In Button**//
    public void checkInBird(View view){
        Toast.makeText(CheckInBird.this,"You checked in a "+birdName+"!",Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

        LatLng birdLocation = myMarker.getPosition();
        Double sLat = birdLocation.latitude;
        Double sLong = birdLocation.longitude;

        String bird_comment = birdComments.getText().toString();

        //add sighting to db
        writeNewSighting(birdName, bird_comment, sLat, sLong, picture_name,currentDate,username);
    }

    //**Record a Sighting**//
    private void writeNewSighting(String bird_name, String bird_comment, Double sLat, Double sLong, String picture_name, String sDate, String username) {
        Sightings sighting = new Sightings(bird_name,bird_comment,sLat,sLong,picture_name,sDate,username);
        sightingsReference.push().setValue(sighting);
    }

    //**Take Photo Button**//
    public void takePhoto(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==REQUEST_IMAGE_CAPTURE && resultCode==RESULT_OK){
            Bundle extras = data.getExtras();
            Bitmap birdBitmap = (Bitmap) extras.get("data");
            ImageView birdView = (ImageView) findViewById(R.id.BirdPic);
            birdView.setImageBitmap(birdBitmap);
            birdUri = data.getData();
            uploadPicture();

        } else {
            Toast.makeText(CheckInBird.this,"There was an error, please try again.",Toast.LENGTH_SHORT).show();
        }

    }

    //**Upload picture**//
    public void uploadPicture(){
        //upload the picture
        StorageReference filepath = mStorageRef.child("Photos").child(birdUri.getLastPathSegment());
        picture_name=birdUri.getLastPathSegment();

        filepath.putFile(birdUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(CheckInBird.this,"Upload completed.",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CheckInBird.this,"There was an error, no photo added.",Toast.LENGTH_SHORT).show();
            }
        });
    }

}

