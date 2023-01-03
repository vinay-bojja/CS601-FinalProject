package hotelapp;

import com.google.gson.annotations.SerializedName;

/**
 * The Coordinates class holds values of latitude and longitude.
 */
public class Coordinates {

    @SerializedName("lat")
    private String latitude;

    @SerializedName("lng")
    private String longitude;

    public Coordinates(String latitude, String longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    @Override
    public String toString() {
        return "Coordinates{" +
                "latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                '}';
    }
}
