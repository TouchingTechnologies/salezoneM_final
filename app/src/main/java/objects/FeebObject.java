package objects;

/**
 * Created by MOBICASH on 24-Jun-15.
 */



public class FeebObject {

    private String id;
    private String name;
    private String location;
    private String latitude;
    private String longititude;
    private String mobile;
    private String picname;
    String error;
    String status;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }


    public void setLocation(String location) {
        this.location = location;
    }


    public String getLatitude() {
        return latitude;
    }


    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }


    public String getLongititude() {
        return longititude;
    }


    public void setLongititude(String longititude) {
        this.longititude = longititude;
    }


    public String getMobile() {
        return mobile;
    }


    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPicname() {
        return picname;
    }


    public void setPicname(String picname) {
        this.picname = picname;
    }


}