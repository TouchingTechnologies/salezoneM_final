package objects;

import com.google.gson.annotations.SerializedName;

public class FeedItem {
    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("status")
    private String status;

    @SerializedName("image")
    private String imageUrl;

    @SerializedName("dateupload")
    private String dateupload;

    public String getDiscout() {
        return discout;
    }

    public void setDiscout(String discout) {
        this.discout = discout;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOld_price() {
        return old_price;
    }

    public void setOld_price(String old_price) {
        this.old_price = old_price;
    }

    public String getNew_price() {
        return new_price;
    }

    public void setNew_price(String new_price) {
        this.new_price = new_price;
    }

    public String getDateupload() {
        return dateupload;
    }

    public void setDateupload(String dateupload) {
        this.dateupload = dateupload;
    }

    @SerializedName("discout")
    private String discout;

    @SerializedName("new_price")
    private String new_price;

    @SerializedName("old_price")
    private String old_price;

    @SerializedName("title")
    private String title;

    @SerializedName("profilePic")
    private String profilePic;

    @SerializedName("timeStamp")
    private String timeStamp;

    @SerializedName("latitude")
    private String latitude;

    @SerializedName("longititude")
    private String longititude;

    public String getLongitude() {
        return longititude;
    }

    public void setLongitude(String longititude) {
        this.longititude = longititude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }




//    @SerializedName("url")
 //   private String url;

    @SerializedName("code")
    private String code;

    public FeedItem() {
    }

    public FeedItem(int id, String name, String imageUrl, String status,
                    String profilePic, String timeStamp,String code,String latitude,String longititude,String title
    ,String discout,String new_price,String old_price,String dateupload) {
        super();
        this.id = id;
        this.title =title;
        this.discout = discout;
        this.new_price = new_price;
        this.old_price = old_price;
        this.dateupload =dateupload;
        this.name = name;
        this.imageUrl = imageUrl;
        this.status = status;
        this.profilePic = profilePic;
        this.timeStamp = timeStamp;
        this.latitude = latitude;
        this.longititude = longititude;

       // this.url = url;
        this.code =code;
    }
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

   /* public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }*/
}
