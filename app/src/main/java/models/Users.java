package models;

/**
 * Created by Lily on 10/06/2017.
 */

public class Users {
    private String user_name;
    private String user_password;

    public Users() {}

    public Users(String user_name, String user_password){
        this.user_name = user_name;
        this.user_password = user_password;
    }


    public String getUser_name() { return user_name; }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_password() {
        return user_password;
    }

    public void setUser_password(String user_password) {
        this.user_password = user_password;
    }
}