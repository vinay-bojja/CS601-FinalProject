package dboperations;

public class PreparedStatements {
    /** Prepared Statements  */
    /**
     * For creating the users table
     */
    public static final String CREATE_USER_TABLE =
            "CREATE TABLE users (" +
                    "userid INTEGER AUTO_INCREMENT PRIMARY KEY, " +
                    "username VARCHAR(32) NOT NULL UNIQUE, " +
                    "password CHAR(64) NOT NULL, " +
                    "usersalt CHAR(32) NOT NULL);";

    /**
     * Used to insert a new user into the database.
     */
    public static final String REGISTER_SQL =
            "INSERT INTO users (username, password, usersalt) " +
                    "VALUES (?, ?, ?);";

    /**
     * Used to select all the rows from users
     */
    public static final String SELECT_ALL_USER = "SELECT * FROM users;";

    /**
     * Get username from users on matching provided username.
     */
    public static final String GET_USER = "SELECT username FROM users WHERE username = ?";

    /**
     * Get username from users on matching provided username and password.
     */
    public static final String AUTH_SQL = "SELECT username from users WHERE username = ? AND password = ?";

    /**
     * Get usersalt from users on matching provided username.
     */
    public static final String SALT_SQL = "SELECT usersalt FROM users WHERE username = ?";

    /**
     * Get all rows from table. Used to check if table is created.
     */
    public static final String SELECT_ALL_USERHISTORY = "SELECT * FROM expedialinkvisithistory;";

    /**
     * Creates expedia table history table.
     */
    public static final String CREATE_EXPEDIALINKVISITHISTORY_TABLE =
            "CREATE TABLE expedialinkvisithistory (" +
                    "ID INTEGER AUTO_INCREMENT PRIMARY KEY, " +
                    "username VARCHAR(50) NOT NULL, " +
                    "hotelId VARCHAR(64) NOT NULL, " +
                    "expedialink VARCHAR(3200) NOT NULL);";

    /**
     * Inserts in expedia link history table.
     */
    public static final String INSERT_EXPEDIALINKVISITHISTORY = "INSERT INTO expedialinkvisithistory (username, hotelId, expedialink) " +
            "VALUES (?, ?, ?);";

    /**
     * Get user expedia visit link history on passing username.
     */
    public static final String SELECT_USER_USERHISTORY = "SELECT * FROM expedialinkvisithistory WHERE username = ?;";

    /**
     * Deletes all expedia link history of a user.
     */
    public static final String DELETE_ALLEXPEDIALINK = "DELETE FROM expedialinkvisithistory WHERE username = ?;";

    /**
     * Deletes a particular expedia link history.
     */
    public static final String DELETE_EXPEDIALINK = "DELETE FROM expedialinkvisithistory WHERE ID = ?;";

    /**
     * Get all rows from table. Used to check if table is created.
     */
    public static final String SELECT_ALL_USERLASTLOGIN = "SELECT * FROM userlastlogin;";

    /**
     * Create users last login table.
     */
    public static final String CREATE_LASTLOGIN_TABLE = "CREATE TABLE userlastlogin (" +
            "id INTEGER AUTO_INCREMENT PRIMARY KEY, " +
            "username VARCHAR(50) NOT NULL, " +
            "date VARCHAR(100) NOT NULL);";

    /**
     * returns users last login details.
     */
    public static final String SELECT_USERLASTLOGIN = "SELECT * FROM userlastlogin WHERE username = ?;";

    /**
     * Inserts user last login details.
     */
    public static final String INSERT_USERLASTLOGIN = "INSERT INTO userlastlogin ( username, date)" + "VALUES (?, ?);";

    /**
     * Updates user last login.
     */
    public static final String UPDATE_USERLASTLOGIN = "UPDATE userlastlogin SET date = ? WHERE username = ?;";

}
