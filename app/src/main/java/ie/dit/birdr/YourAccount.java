package ie.dit.birdr;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import models.Sightings;

public class YourAccount extends Activity {

    String username;
    Long numRecords;
    String currentDate;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

    DatabaseReference rootReference;
    DatabaseReference sightingsReference;
    StorageReference storageRef;

    TextView numberOfRecords;

    Integer counter = 0;

    ImageView imageView1;
    ImageView imageView2;
    ImageView imageView3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_account);

        //get date for sorting images
        Calendar cal = Calendar.getInstance();
        currentDate = dateFormat.format(cal.getTime());

        //get username from singleton and set as heading
        username = UserSingleton.getInstance().getUsername();
        TextView userTitle = (TextView) findViewById(R.id.YourUsername);
        userTitle.setText(username+"'s Account");

        //number of records and data for pictures
        numberOfRecords = (TextView) findViewById(R.id.NumberOfRecords);
        rootReference = FirebaseDatabase.getInstance().getReference();
        sightingsReference = rootReference.child("sightings");
        storageRef = FirebaseStorage.getInstance().getReference();

        //fill in sightings and pictures
        getPictures();

    }

    //fill in the number of sightings and pictures
    public void getPictures() {
        Query query = sightingsReference.orderByChild("username").equalTo(username);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                numRecords = dataSnapshot.getChildrenCount();
                numberOfRecords.setText("Number of sightings: " + numRecords);

                for (DataSnapshot sightingsSnapshot : dataSnapshot.getChildren()) {
                    Sightings sighting = sightingsSnapshot.getValue(Sightings.class);

                    String toAdd = sighting.getImage_name();

                    addPicture(toAdd);

                    if(counter==3) {
                        break;
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void addPicture(String pictureId){

        imageView1 = (ImageView) findViewById(R.id.bird1);
        imageView2 = (ImageView) findViewById(R.id.bird2);
        imageView3 = (ImageView) findViewById(R.id.bird3);

        String ref = "Pictures/"+pictureId;

        if(counter==0) {
            StorageReference imageRef = storageRef.child("/Pictures/" + pictureId);
            String url = imageRef.toString();
            new ImageLoadTask(url, imageView1).execute();
        }else if(counter==1) {
            new ImageLoadTask("http://images.all-free-download.com/images/graphiclarge/butterfly_flower_01_hd_pictures_166973.jpg", imageView2).execute();
        }else {
            new ImageLoadTask("https://static.pexels.com/photos/36764/marguerite-daisy-beautiful-beauty.jpg", imageView3).execute();
        }

        counter+=1;

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
