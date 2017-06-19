package ie.dit.birdr;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import models.Bird;

public class ConfirmBird extends Activity {

    String birdName;
    String birdUrl;
    private ImageView birdImageView;

    DatabaseReference rootReference;
    DatabaseReference birdsReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_bird);

        rootReference = FirebaseDatabase.getInstance().getReference();
        birdsReference = rootReference.child("birds");

        TextView birdView = (TextView) findViewById(R.id.confirm_bird_name);
        birdName = getIntent().getExtras().getString("birdName","Blackbird");
        birdView.setText(birdName);

        getBirdUrl();

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

    private void getBirdUrl(){

        //get bird URL
        Query query = birdsReference.orderByChild("bird_name").equalTo(birdName);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot birdSnapshot: dataSnapshot.getChildren()){
                    Bird bird = birdSnapshot.getValue(Bird.class);
                    birdUrl = bird.getBird_url();
                    birdImageView = (ImageView) findViewById(R.id.confirmBirdImage);
                    new ImageLoadTask(birdUrl, birdImageView).execute();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //**Check-In Button**//
    public void checkInBird(View view){
        Intent intent = new Intent(this, CheckInBird.class);
        intent.putExtra("birdName",birdName);
        startActivity(intent);
    }
}
