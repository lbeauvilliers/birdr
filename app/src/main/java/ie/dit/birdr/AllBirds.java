package ie.dit.birdr;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import models.Bird;

public class AllBirds extends Activity {
    ListView birdListView;
    EditText filterBirds;
    ArrayAdapter adapter;
    LazyAdapter lazyAdapter;

    List<Bird> allBirds = new ArrayList<>();
    ArrayList<String> birdNames = new ArrayList<>();
    ArrayList<String> birdURLs = new ArrayList<>();

    DatabaseReference rootReference;
    DatabaseReference birdsReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_birds);

        rootReference = FirebaseDatabase.getInstance().getReference();
        birdsReference = rootReference.child("birds");

        populateBirdList();

    }

    private void populateBirdList(){

        //populate birdNames and birdURLs lists from Firebase
        birdsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot birdSnapshot: dataSnapshot.getChildren()){
                    Bird bird = birdSnapshot.getValue(Bird.class);
                    allBirds.add(bird);
                }

                for (Bird bird : allBirds){
                    birdNames.add(bird.getBird_name());
                    birdURLs.add(bird.getBird_url());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //get the view
        birdListView = (ListView)findViewById(R.id.bird_listview);

        //lazy adapter
        lazyAdapter = new LazyAdapter(this, birdNames, birdURLs);
        birdListView.setAdapter(lazyAdapter);

        //filter the list view DOESN'T WORK
        /*filterBirds = (EditText) findViewById(R.id.filterTextView);
        filterBirds.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                AllBirds.this.adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });*/
    }

    //from https://stackoverflow.com/questions/18953632/how-to-set-image-from-url-for-imageview
    public class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

        private String url;
        private ImageView imageView;

        public ImageLoadTask(String url, ImageView imageView) {
            this.url = url;
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                URL urlConnection = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlConnection
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            imageView.setImageBitmap(result);
        }

    }



}
