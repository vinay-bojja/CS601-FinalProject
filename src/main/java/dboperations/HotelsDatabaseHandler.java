package dboperations;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import hotelapp.Hotel;

import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.*;

public class HotelsDatabaseHandler {

    private static HotelsDatabaseHandler dbHandler = new HotelsDatabaseHandler("database.properties"); // singleton pattern
    private Properties config;
    private String uri = null;

    /**
     * DataBaseHandler is a singleton, we want to prevent other classes
     * from creating objects of this class using the constructor
     */
    private HotelsDatabaseHandler(String propertiesFile) {
        this.config = loadConfigFile(propertiesFile);
        this.uri = "jdbc:mysql://" + config.getProperty("hostname") + "/" + config.getProperty("username") + "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
        this.createHotelTable();
        this.createFavoriteHotelTable();
    }

    /**
     * Returns the instance of the database handler.
     *
     * @return instance of the database handler
     */
    public static HotelsDatabaseHandler getInstance() {
        return dbHandler;
    }


    /**
     * Load info from config file database.properties
     *
     * @param propertyFile
     */
    public Properties loadConfigFile(String propertyFile) {
        Properties config = new Properties();
        try (FileReader fr = new FileReader(propertyFile)) {
            config.load(fr);
        } catch (IOException e) {
            System.out.println(e);
        }

        return config;
    }

    /**
     * Create hotel table.
     */
    public void createHotelTable() {
        Statement statement;
        try (Connection dbConnection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            System.out.println("dbConnection Successful.");
            statement = dbConnection.createStatement();
            PreparedStatement usersQuery = dbConnection.prepareStatement(HotelsPreparedStatements.SELECT_ALL_HOTELS);
            try {
                usersQuery.executeQuery();
            } catch (SQLException e) {
                statement.executeUpdate(HotelsPreparedStatements.CREATE_HOTEL_TABLE);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Create Favorite hotels table.
     */
    public void createFavoriteHotelTable() {
        Statement statement;
        try (Connection dbConnection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            System.out.println("dbConnection Successful.");
            statement = dbConnection.createStatement();
            PreparedStatement usersQuery = dbConnection.prepareStatement(HotelsPreparedStatements.SELECT_ALL_FAVORITEHOTELS);
            try {
                usersQuery.executeQuery();
            } catch (SQLException e) {
                statement.executeUpdate(HotelsPreparedStatements.CREATE_FAVORITEHOTELS_TABLE);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Add hotels.
     *
     * @param hotelList
     */
    public void addHotels(List<Hotel> hotelList) {
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            System.out.println("dbConnection Successful.");
            for (Hotel hotel : hotelList) {
                insertHotel(hotel, connection);
            }
        } catch (SQLException ex) {
            System.out.println(ex);
        }
    }

    /**
     * Insert Hotel.
     *
     * @param hotel
     * @param connection
     */
    private void insertHotel(Hotel hotel, Connection connection) {
        PreparedStatement statement;
        try {
            statement = connection.prepareStatement(HotelsPreparedStatements.GET_HOTEL);
            statement.setString(1, hotel.getHotelId());
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                System.out.println("Hotel already present");
            } else {
                statement = connection.prepareStatement(HotelsPreparedStatements.INSERT_HOTEL);
                statement.setString(1, hotel.getHotelId());
                statement.setString(2, hotel.getHotelName());
                statement.setString(3, hotel.getCoordinates().getLongitude());
                statement.setString(4, hotel.getCoordinates().getLatitude());
                statement.setString(5, hotel.getAddress());
                statement.setString(6, hotel.getCity());
                statement.setString(7, hotel.getState());
                statement.setString(8, hotel.getCountry());
                statement.executeUpdate();
                statement.close();

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get hotel on passsing keyword as a parameter.
     *
     * @param keyword
     * @return
     */
    public List<Hotel> getHotels(String keyword) {
        PreparedStatement statement;
        List<Hotel> hotels = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            System.out.println("dbConnection Successful.");
            statement = connection.prepareStatement(HotelsPreparedStatements.GET_HOTELS);
            statement.setString(1, "%" + keyword + "%");
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String hotelId = rs.getString("hotelId");
                String hotelName = rs.getString("hotelName");
                String longitude = rs.getString("longitude");
                String latitude = rs.getString("latitude");
                String address = rs.getString("address");
                String city = rs.getString("city");
                String state = rs.getString("state");
                String country = rs.getString("country");

                Hotel hotel = new Hotel(hotelName, hotelId, longitude, latitude, address, city, state, country);
                hotels.add(hotel);
            }
            if (hotels.size() == 0) {
                return null;
            } else {
                return hotels;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get Hotel by Id.
     *
     * @param hotel_Id
     * @return
     */
    public Hotel getHotelById(String hotel_Id) {
        PreparedStatement statement;
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            System.out.println("dbConnection Successful.");
            Hotel hotel = null;
            statement = connection.prepareStatement(HotelsPreparedStatements.GET_HOTEL);
            statement.setString(1, hotel_Id);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                String hotelId = rs.getString("hotelId");
                String hotelName = rs.getString("hotelName");
                String longitude = rs.getString("longitude");
                String latitude = rs.getString("latitude");
                String address = rs.getString("address");
                String city = rs.getString("city");
                String state = rs.getString("state");
                String country = rs.getString("country");

                hotel = new Hotel(hotelName, hotelId, longitude, latitude, address, city, state, country);
            }
            return hotel;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Add hotel to favorites.
     *
     * @param hotelId
     * @param hotelName
     * @param sessionUsername
     */
    public void addHotelToFavorite(String hotelId, String hotelName, String sessionUsername) {
        PreparedStatement statement;
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            statement = connection.prepareStatement(HotelsPreparedStatements.INSERT_HOTELTOFAVORITE);
            statement.setString(1, sessionUsername);
            statement.setString(2, hotelId);
            statement.setString(3, hotelName);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get Favorite hotel on passing hotelId.
     *
     * @param hotelId
     * @param sessionUsername
     * @return
     */
    public Boolean getFavoriteHotel(String hotelId, String sessionUsername) {
        PreparedStatement statement;
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            statement = connection.prepareStatement(HotelsPreparedStatements.SELECT_FAVORITEHOTELS_BYHOTELIDUSERNAME);
            statement.setString(1, sessionUsername);
            statement.setString(2, hotelId);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                System.out.println("here");
                return true;
            }
            statement.close();
            return false;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get all favorite hotels of user.
     *
     * @param username
     * @return
     */
    public String getAllFavoriteHotels(String username) {
        PreparedStatement statement;
        List<String> allFavoriteHotels = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            statement = connection.prepareStatement(HotelsPreparedStatements.SELECT_ALL_FAVORITEHOTELS_BYUSERNAME);
            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                allFavoriteHotels.add(rs.getString("hotelName"));
            }
            statement.close();
            String favoritehotelJSON = createFavoriteHotelsJSON(allFavoriteHotels);
            return favoritehotelJSON;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Create favorite hotels Json.
     *
     * @param allFavoriteHotels
     * @return
     */
    private String createFavoriteHotelsJSON(List<String> allFavoriteHotels) {
        if (allFavoriteHotels.size() == 0) {
            return null;
        }
        JsonObject jo = new JsonObject();
        jo.addProperty("success", true);
        JsonArray allHotels = new JsonArray();
        for (String hotel : allFavoriteHotels) {
            allHotels.add(hotel);
        }
        jo.add("allHotels", allHotels);
        return jo.toString();
    }

    /**
     * Delete all favorite hotels of the user.
     *
     * @param sessionUsername
     */
    public void deleteAllFavoriteHotels(String sessionUsername) {
        PreparedStatement statement;
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            System.out.println("dbConnection Successful.");
            statement = connection.prepareStatement(HotelsPreparedStatements.DELETE_ALLFAVORITEHOTELS);
            statement.setString(1, sessionUsername);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Delete a favorite hotel of the user
     *
     * @param sessionUsername
     * @param hotelName
     */
    public void deleteFavoriteHotels(String sessionUsername, String hotelName) {
        PreparedStatement statement;
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            System.out.println("dbConnection Successful.");
            statement = connection.prepareStatement(HotelsPreparedStatements.DELETE_FAVORITEHOTELS);
            statement.setString(1, sessionUsername);
            statement.setString(2, hotelName);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
