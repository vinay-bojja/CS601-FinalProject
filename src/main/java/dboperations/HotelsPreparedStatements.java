package dboperations;

public class HotelsPreparedStatements {

    /**
     * Create hotel table
     */
    public static final String CREATE_HOTEL_TABLE =
            "CREATE TABLE hotels (" +
                    "hotelId VARCHAR(32) NOT NULL," +
                    "hotelName VARCHAR(320) NOT NULL UNIQUE," +
                    "longitude VARCHAR(32)," +
                    "latitude VARCHAR(32)," +
                    "address VARCHAR(32) NOT NULL," +
                    "city VARCHAR(32) NOT NULL," +
                    "state VARCHAR(32) NOT NULL," +
                    "country VARCHAR(32) NOT NULL);";

    /**
     * Select all hotels.
     */
    public static final String SELECT_ALL_HOTELS = "SELECT * FROM hotels;";

    /**
     * Select hotel matching with hotelId.
     */
    public static final String GET_HOTEL = "SELECT * FROM hotels WHERE hotelId = ?;";

    /**
     * Insert hotel.
     */
    public static final String INSERT_HOTEL = "INSERT INTO hotels (hotelId, hotelName, longitude, latitude, address, city, state, country) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
//
//    /**
//     *
//     */
//    public static final String SELECT_ALL_HOTELIDBYKEYWORD = "SELECT * FROM hotelidbykeyword;";
//
//    public static final String CREATE_HOTELIDBYKEYWORD_TABLE = "CREATE TABLE hotelidbykeyword (" +
//            "id INTEGER AUTO_INCREMENT PRIMARY KEY, " +
//            "keyword VARCHAR(32) NOT NULL," +
//            "hotelId VARCHAR(320) NOT NULL);";
//
//    public static final String GET_HOTELIDBYKEYWORD = "SELECT * FROM hotelidbykeyword WHERE keyword = ? AND hotelId = ?;";
//
//    public static final String INSERT_HOTELIDBYKEYWORD = "INSERT INTO hotelidbykeyword (keyword, hotelId) " +
//            "VALUES (?, ?);";

    /**
     * Get hotel having hotelName passed as parameter.
     */
    public static final String GET_HOTELS = "SELECT * FROM hotels where hotelName LIKE ?;";

//    public static final String GET_HOTELBYHOTELID = "SELECT * FROM hotels WHERE hotelId = ?";

    /**
     * Create favorite hotel table.
     */
    public static final String CREATE_FAVORITEHOTELS_TABLE =
            "CREATE TABLE favoritehotels (" +
                    "ID INTEGER AUTO_INCREMENT PRIMARY KEY," +
                    "username VARCHAR(32) NOT NULL," +
                    "hotelId VARCHAR(32) NOT NULL," +
                    "hotelName VARCHAR(320) NOT NULL);";

    /**
     * Get data from favorite hotel.
     */
    public static final String SELECT_ALL_FAVORITEHOTELS = "SELECT * FROM favoritehotels;";

    /**
     * Insert favorite hotel.
     */
    public static final String INSERT_HOTELTOFAVORITE = "INSERT INTO favoritehotels (username, hotelId, hotelName) " +
            "VALUES (?, ?, ?);";

    /**
     * Get favorite hotel having username and hotelId passed as a parameter.
     */
    public static final String SELECT_FAVORITEHOTELS_BYHOTELIDUSERNAME = "SELECT * FROM favoritehotels WHERE username = ? AND hotelId = ?;";

    /**
     * Get favorite hotel having username passed as a parameter.
     */
    public static final String SELECT_ALL_FAVORITEHOTELS_BYUSERNAME = "SELECT * FROM favoritehotels WHERE username = ?;";

    /**
     * Delete favorite hotel on passing username.
     */
    public static final String DELETE_ALLFAVORITEHOTELS = "DELETE FROM favoritehotels WHERE username = ?;";

    /**
     * Delete favorite hotel on passing username and hotelId.
     */
    public static final String DELETE_FAVORITEHOTELS = "DELETE FROM favoritehotels WHERE username = ? AND hotelName = ?;";
}
