package ie.dit.birdr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import models.Bird;

public class MainActivity extends Activity {

    private AutoCompleteTextView actv;
    List<Bird> allBirds = new ArrayList<>();
    ArrayList<String> birdNames = new ArrayList<>();
    String username;

    DatabaseReference rootReference;
    DatabaseReference birdsReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        actv = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        rootReference = FirebaseDatabase.getInstance().getReference();
        birdsReference = rootReference.child("birds");

        //populate birdNames list from Firebase
        birdsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot birdSnapshot: dataSnapshot.getChildren()){
                    Bird bird = birdSnapshot.getValue(Bird.class);
                    allBirds.add(bird);
                }

                for (Bird bird : allBirds){
                    birdNames.add(bird.getBird_name());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this,android.R.layout.simple_list_item_1,birdNames);

        actv.setAdapter(adapter);

    }

    //**Check-In Button**//
    public void confirmBird(View view){
        Intent intent = new Intent(this, ConfirmBird.class);
        String birdName = actv.getText().toString();
        intent.putExtra("birdName",birdName);
        startActivity(intent);
    }

    //**My Account Button**//
    public void viewMyAccount(View view){
        Intent intent = new Intent(this, YourAccount.class);
        startActivity(intent);
    }

    //**All Birds Button**//
    public void viewAllBirds(View view){
        Intent intent = new Intent(this, AllBirds.class);
        startActivity(intent);
    }

    //**Nearby Birds Button**//
    public void viewNearbyBirds(View view){
        Intent intent = new Intent(this, NearbyBirds.class);
        startActivity(intent);
    }

    //**Identify Bird Buttom**//
    public void identifyBird(View view){
        Intent intent = new Intent(this, IdentifyBird.class);
        startActivity(intent);
    }
}
