package models;

/**
 * Created by Lily on 10/06/2017.
 */

public class Bird {
    private Integer bird_id;
    private String bird_name;
    private String bird_url;

    public Bird() {}

    public Bird(Integer bird_id, String bird_name, String bird_url){
        this.bird_id = bird_id;
        this.bird_name = bird_name;
        this.bird_url = bird_url;
    }

    public Integer getBird_id() {
        return bird_id;
    }

    public void setBird_id(Integer bird_id) {
        this.bird_id = bird_id;
    }

    public String getBird_name() {
        return bird_name;
    }

    public void setBird_name(String bird_name) {
        this.bird_name = bird_name;
    }

    public String getBird_url() {
        return bird_url;
    }

    public void setBird_url(String bird_url) {
        this.bird_url = bird_url;
    }
}