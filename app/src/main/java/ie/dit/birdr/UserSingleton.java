package ie.dit.birdr;

import android.app.Application;

/**
 * From https://stackoverflow.com/questions/8748444/passing-strings-between-activities-in-android
 */

public class UserSingleton extends Application {

    private static UserSingleton mInstance = null;

    private String username;

    private UserSingleton(){
        username="null";
    }

    public static UserSingleton getInstance(){
        if(mInstance==null){
            mInstance = new UserSingleton();
        }

        return mInstance;
    }

    public String getUsername(){
        return this.username;
    }

    public void setUsername(String newUsername){
        username=newUsername;
    }

}
