package ie.dit.birdr;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import models.Users;

public class SignUp extends Activity {
    Context context;

    //for reading from/writing to db
    DatabaseReference rootReference;
    DatabaseReference usersReference;

    //for username/password from forms
    String username;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //for reading from/writing to db
        rootReference = FirebaseDatabase.getInstance().getReference();
        usersReference = rootReference.child("users");

        context = getApplicationContext();
    }

    //**Register Button**//
    public void signUp(View view){
        //get username and password from forms
        username = ((EditText) findViewById(R.id.signUpUsername)).getText().toString();
        password = ((EditText) findViewById(R.id.signUpPassword)).getText().toString();

        if(username.isEmpty() || password.isEmpty()){
            Toast.makeText(SignUp.this,"Please fill in all fields.",Toast.LENGTH_SHORT).show();
        }else{
            checkUsername(username, password);
        }

    }

    //**check the username is unique, else write**//
    //check username doesn't already exist; if not, create user
    public void checkUsername(final String username, final String password) {
        Query query = usersReference.orderByChild("user_name").equalTo(username);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    Toast.makeText(SignUp.this, "Username unavailable.", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    registerUser(username,password);
                    Toast.makeText(SignUp.this, "Welcome to Birdr, "+username+"!", Toast.LENGTH_SHORT).show();

                    UserSingleton.getInstance().setUsername(username);

                    Intent intent = new Intent(context, MainActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(SignUp.this, "Error, please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //**Register a User**//
    private void registerUser(String user_name, String user_password) {
        Users user = new Users(user_name,user_password);
        usersReference.push().setValue(user);
    }

    //**Sign In Button**//
    public void goToSignIn(View view) {
        Intent intent = new Intent(this, SignIn.class);
        startActivity(intent);
    }
}
