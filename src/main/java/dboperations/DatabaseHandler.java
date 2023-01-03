package dboperations;


import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.sql.*;
import java.util.*;

/**
 * Modified from the example of Prof. Engle
 */
public class DatabaseHandler {

    private static DatabaseHandler dbHandler = new DatabaseHandler("database.properties");
    private Properties config;
    private String uri = null;
    private Random random = new Random();

    /**
     * DataBaseHandler is a singleton, we want to prevent other classes
     * from creating objects of this class using the constructor
     */
    private DatabaseHandler(String propertiesFile) {
        this.config = loadConfigFile(propertiesFile);
        this.uri = "jdbc:mysql://" + config.getProperty("hostname") + "/" + config.getProperty("username") + "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
        this.createTable();
        this.createExpediaHistoryTable();
        this.createLastLoginDateTime();
    }

    /**
     * Returns the instance of the database handler.
     *
     * @return instance of the database handler
     */
    public static DatabaseHandler getInstance() {
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
     * Creates user table only if the table is absent.
     */
    public void createTable() {
        Statement statement;
        try (Connection dbConnection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            System.out.println("dbConnection successful");
            statement = dbConnection.createStatement();
            PreparedStatement usersQuery = dbConnection.prepareStatement(PreparedStatements.SELECT_ALL_USER);
            try {
                usersQuery.executeQuery();
            } catch (SQLException e) {
                statement.executeUpdate(PreparedStatements.CREATE_USER_TABLE);
            }
        } catch (SQLException ex) {
            System.out.println(ex);
        }
    }

    /**
     * Creates expediahistory table only if the table is absent.
     */
    public void createExpediaHistoryTable() {
        Statement statement;
        try (Connection dbConnection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            System.out.println("dbConnection successful");
            statement = dbConnection.createStatement();
            PreparedStatement usersQuery = dbConnection.prepareStatement(PreparedStatements.SELECT_ALL_USERHISTORY);
            try {
                usersQuery.executeQuery();
            } catch (SQLException e) {
                statement.executeUpdate(PreparedStatements.CREATE_EXPEDIALINKVISITHISTORY_TABLE);
            }
        } catch (SQLException ex) {
            System.out.println(ex);
        }
    }

    /**
     * Creates lastlogindatetimetable table only if the table is absent.
     */
    public void createLastLoginDateTime() {
        Statement statement;
        try (Connection dbConnection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            System.out.println("dbConnection successful");
            statement = dbConnection.createStatement();
            PreparedStatement usersQuery = dbConnection.prepareStatement(PreparedStatements.SELECT_ALL_USERLASTLOGIN);
            try {
                usersQuery.executeQuery();
            } catch (SQLException e) {
                statement.executeUpdate(PreparedStatements.CREATE_LASTLOGIN_TABLE);
            }
        } catch (SQLException ex) {
            System.out.println(ex);
        }
    }

    /**
     * Returns the hex encoding of a byte array.
     *
     * @param bytes  - byte array to encode
     * @param length - desired length of encoding
     * @return hex encoded byte array
     */
    public static String encodeHex(byte[] bytes, int length) {
        BigInteger bigint = new BigInteger(1, bytes);
        String hex = String.format("%0" + length + "X", bigint);

        assert hex.length() == length;
        return hex;
    }

    /**
     * Calculates the hash of a password and salt using SHA-256.
     *
     * @param password - password to hash
     * @param salt     - salt associated with user
     * @return hashed password
     */
    public static String getHash(String password, String salt) {
        String salted = salt + password;
        String hashed = salted;

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salted.getBytes());
            hashed = encodeHex(md.digest(), 64);
        } catch (Exception ex) {
            System.out.println(ex);
        }

        return hashed;
    }

    /**
     * Registers a new user, placing the username, password hash, and
     * salt into the database.
     *
     * @param newUser - username of new user
     * @param newPass - password of new user
     * @return Map<Boolean, String> - <true,message> if successful. If error registering user the <false,message>.
     */
    public Map<Boolean, String> registerUser(String newUser, String newPass) {
        // Generate salt
        Map<Boolean, String> result = new HashMap<>(); //<did registered? , message>
        PreparedStatement statement;
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            System.out.println("dbConnection successful");
            statement = connection.prepareStatement(PreparedStatements.GET_USER);
            statement.setString(1, newUser);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                result.put(false, "User already exists.");
                return result;
            } else {
                byte[] saltBytes = new byte[16];
                random.nextBytes(saltBytes);

                String usersalt = encodeHex(saltBytes, 32); // salt
                String passhash = getHash(newPass, usersalt); // hashed password
                System.out.println(usersalt);

                statement = connection.prepareStatement(PreparedStatements.REGISTER_SQL);
                statement.setString(1, newUser);
                statement.setString(2, passhash);
                statement.setString(3, usersalt);
                statement.executeUpdate();
                statement.close();

                result.put(true, "User Registered successfully.");
                return result;
            }
        } catch (SQLException ex) {
            System.out.println(ex);
        }
        return null;
    }

    /**
     * Returns true if username and password are correct else returns false.
     *
     * @param userName
     * @param password
     * @return
     */
    public Boolean authenticateUser(String userName, String password) {
        PreparedStatement statement = null;
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            statement = connection.prepareStatement(PreparedStatements.AUTH_SQL);
            String userSalt = getSalt(connection, userName);
            String passhash = getHash(password, userSalt);

            statement.setString(1, userName);
            statement.setString(2, passhash);
            ResultSet results = statement.executeQuery();
            boolean flag = results.next();
            return flag;
        } catch (SQLException e) {
            System.out.println(e);
        }
        return false;
    }

    /**
     * Returns salt.
     *
     * @param connection
     * @param user
     * @return
     */
    private String getSalt(Connection connection, String user) {
        String salt = null;
        try (PreparedStatement statement = connection.prepareStatement(PreparedStatements.SALT_SQL)) {
            statement.setString(1, user);
            ResultSet results = statement.executeQuery();
            if (results.next()) {
                salt = results.getString("usersalt");
                return salt;
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return salt;
    }

    /**
     * Insert in Expedia history table.
     *
     * @param hotelId
     * @param username
     * @param expediaLink
     */
    public void insertInExpediaHistoryTable(String hotelId, String username, String expediaLink) {
        PreparedStatement statement;
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            statement = connection.prepareStatement(PreparedStatements.INSERT_EXPEDIALINKVISITHISTORY);
            statement.setString(1, username);
            statement.setString(2, hotelId);
            statement.setString(3, expediaLink);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns expedia visit history of a user.
     *
     * @param username
     * @return
     */
    public Map<String, String> getUserExpediaVisitHistory(String username) {
        PreparedStatement statement;
        Map<String, String> expediaLinkByHotelId = new HashMap<>();
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            statement = connection.prepareStatement(PreparedStatements.SELECT_USER_USERHISTORY);
            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String hotelId = rs.getString("id");
                String expedialink = rs.getString("expedialink");
                expediaLinkByHotelId.put(hotelId, expedialink);
            }
            statement.close();
            if (expediaLinkByHotelId.size() == 0) {
                return null;
            } else {
                return expediaLinkByHotelId;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Delete all expedia links.
     *
     * @param username
     */
    public void deleteAllExpediaLink(String username) {
        PreparedStatement statement;
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            System.out.println("dbConnection Successful.");
            statement = connection.prepareStatement(PreparedStatements.DELETE_ALLEXPEDIALINK);
            statement.setString(1, username);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Deletes Expedia links based on id.
     *
     * @param id
     */
    public void deleteExpediaLink(String id) {
        PreparedStatement statement;
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            System.out.println("dbConnection Successful.");
            statement = connection.prepareStatement(PreparedStatements.DELETE_EXPEDIALINK);
            statement.setInt(1, Integer.parseInt(id));
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Inserts or update login date and time.
     *
     * @param loginDateTime
     * @param username
     */
    public void insertOrUpdateLoginDateTime(String loginDateTime, String username) {
        PreparedStatement statement;
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            statement = connection.prepareStatement(PreparedStatements.SELECT_USERLASTLOGIN);
            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                statement = connection.prepareStatement(PreparedStatements.UPDATE_USERLASTLOGIN);
                statement.setString(1, loginDateTime);
                statement.setString(2, username);
                statement.executeUpdate();
            } else {
                statement = connection.prepareStatement(PreparedStatements.INSERT_USERLASTLOGIN);
                statement.setString(1, username);
                statement.setString(2, loginDateTime);
                statement.executeUpdate();
            }
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get Login Date of the user.
     *
     * @param username
     * @return
     */
    public String getLoginDateTime(String username) {
        PreparedStatement statement;
        String date = "N/A";
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            statement = connection.prepareStatement(PreparedStatements.SELECT_USERLASTLOGIN);
            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                date = rs.getString("date");
            }
            statement.close();
            return date;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

