package hotelapp;


import com.google.gson.annotations.SerializedName;

/**
 * Hotel Class
 */
public class Hotel {

    @SerializedName("f")
    private String hotelName;

    @SerializedName("id")
    private String hotelId;

    @SerializedName("ll")
    private Coordinates coordinates;

    @SerializedName("ad")
    private String address;

    @SerializedName("ci")
    private String city;

    @SerializedName("pr")
    private String state;

    @SerializedName("c")
    private String country;

    /**
     * Constructor to set values of the instance variables
     *
     * @param hotelName
     * @param hotelId
     * @param coordinates
     * @param address
     * @param city
     * @param state
     * @param country
     */
    public Hotel(String hotelName, String hotelId, Coordinates coordinates, String address, String city, String state, String country) {
        this.hotelName = hotelName;
        this.hotelId = hotelId;
        this.coordinates = coordinates;
        this.address = address;
        this.city = city;
        this.state = state;
        this.country = country;
    }

    public Hotel(String hotelName, String hotelId, String longitude, String latitude, String address, String city, String state, String country) {
        Coordinates coordinates = new Coordinates(latitude,longitude);
        this.hotelName = hotelName;
        this.hotelId = hotelId;
        this.coordinates = coordinates;
        this.address = address;
        this.city = city;
        this.state = state;
        this.country = country;
    }


    /**
     * Get hotel id.
     *
     * @return hotelId.
     */
    public String getHotelId() {
        return hotelId;
    }

    /**
     * Get hotel name.
     *
     * @return
     */
    public String getHotelName() {
        return hotelName;
    }

    /**
     * Get co-ordinates.
     *
     * @return
     */
    public Coordinates getCoordinates() {
        return coordinates;
    }

    /**
     * Get address.
     *
     * @return
     */
    public String getAddress() {
        return address;
    }

    /**
     * Get city.
     *
     * @return
     */
    public String getCity() {
        return city;
    }

    /**
     * Get city.
     *
     * @return
     */
    public String getState() {
        return state;
    }

    /**
     * Get country.
     *
     * @return
     */
    public String getCountry() {
        return country;
    }

    /**
     * @return Appended string of all field of the class.
     */
    @Override
    public String toString() {
        return "hotelName=" + hotelName + System.lineSeparator() +
                "hotelId=" + hotelId + System.lineSeparator() +
                "latitude=" + coordinates.getLatitude() + System.lineSeparator() +
                "longitude=" + coordinates.getLongitude() + System.lineSeparator() +
                "address=" + address + ',' + city + ',' + state + ',' + country;
    }

}
