package models;

/**
 * Created by Lily on 10/06/2017.
 */

public class Sightings {
    private String bird_name;
    private String bird_comment;
    private Double sLat;
    private Double sLong;
    private String image_name;
    private String today;
    private String username;

    public Sightings() {}

    public Sightings(String bird_name, String bird_comment, Double sLat, Double sLong, String image_name, String today, String username){
        this.bird_name = bird_name;
        this.bird_comment = bird_comment;
        this.sLat = sLat;
        this.sLong = sLong;
        this.image_name = image_name;
        this.today = today;
        this.username = username;
    }

    public String getToday() { return today; }

    public void setToday(String today) { this.today = today;}

    public String getBird_name() {
        return bird_name;
    }

    public void setBird_name(String bird_name) {
        this.bird_name = bird_name;
    }

    public String getBird_comment() {
        return bird_comment;
    }

    public void setBird_comment(String bird_comment) {
        this.bird_comment = bird_comment;
    }

    public Double getsLat() {
        return sLat;
    }

    public void setsLat(Double sLat) {
        this.sLat = sLat;
    }

    public Double getsLong() {
        return sLong;
    }

    public void setsLong(Double sLong) {
        this.sLong = sLong;
    }

    public String getImage_name() { return image_name; }

    public void setImage_name(String image_name) {
        this.image_name = image_name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}