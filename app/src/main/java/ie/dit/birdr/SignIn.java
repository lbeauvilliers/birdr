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

public class SignIn extends Activity {
    Context context;

    //for reading from db
    DatabaseReference rootReference;
    DatabaseReference usersReference;

    //for signing in
    private String username;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        //for reading from db
        rootReference = FirebaseDatabase.getInstance().getReference();
        usersReference = rootReference.child("users");

        context = getApplicationContext();
    }

    //**Sign In Button**//
    public void signIn(View view) {
        //get username and password from forms
        username = ((EditText) findViewById(R.id.signInUsername)).getText().toString();
        password = ((EditText) findViewById(R.id.signInPassword)).getText().toString();

        if(username.isEmpty() || password.isEmpty()){
            Toast.makeText(SignIn.this,"Please fill in all fields.",Toast.LENGTH_SHORT).show();
        }else{
            checkUsername(username, password);
        }
    }

    private void checkUsername(final String username, final String password){

        //get username and password from db
        Query query = usersReference.orderByChild("user_name").equalTo(username);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()==null){
                    Toast.makeText(SignIn.this,"Incorrect username or password.",Toast.LENGTH_SHORT).show();
                    return;
                }

                for (DataSnapshot userSnapshot: dataSnapshot.getChildren()){
                    Users user = userSnapshot.getValue(Users.class);
                    String dbUsername = user.getUser_name();
                    String dbPassword = user.getUser_password();

                    if(dbPassword.equals(password)){
                        Toast.makeText(SignIn.this,"Welcome back, "+username+"!",Toast.LENGTH_SHORT).show();

                        UserSingleton.getInstance().setUsername(username);

                        Intent intent = new Intent(context, MainActivity.class);
                        startActivity(intent);

                    }else{
                        Toast.makeText(SignIn.this,"Incorrect username or password.",Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(SignIn.this,"Error, please try again.",Toast.LENGTH_SHORT).show();
            }
        });
    }

    //**Register Button**//
    public void goToRegister(View view) {
        Intent intent = new Intent(this, SignUp.class);
        startActivity(intent);
    }

}
