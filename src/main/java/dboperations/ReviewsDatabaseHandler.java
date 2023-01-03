package dboperations;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import hotelapp.Review;

import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class ReviewsDatabaseHandler {

    private static ReviewsDatabaseHandler dbHandler = new ReviewsDatabaseHandler("database.properties"); // singleton pattern
    private Properties config;
    private String uri = null;

    /**
     * DataBaseHandler is a singleton, we want to prevent other classes
     * from creating objects of this class using the constructor
     */
    private ReviewsDatabaseHandler(String propertiesFile) {
        this.config = loadConfigFile(propertiesFile);
        this.uri = "jdbc:mysql://" + config.getProperty("hostname") + "/" + config.getProperty("username") + "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
        this.createReviewsTable();
    }

    /**
     * Returns the instance of the database handler.
     *
     * @return instance of the database handler
     */
    public static ReviewsDatabaseHandler getInstance() {
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
     * Create reviews table.
     */
    public void createReviewsTable() {
        Statement statement;
        try (Connection dbConnection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            System.out.println("dbConnection Successful.");
            statement = dbConnection.createStatement();
            PreparedStatement usersQuery = dbConnection.prepareStatement(ReviewsPreparedStatements.SELECT_ALL_REVIEWS);
            try {
                usersQuery.executeQuery();
            } catch (SQLException e) {
                statement.executeUpdate(ReviewsPreparedStatements.CREATE_REVIEWS_TABLE);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Insert Reviews.
     *
     * @param reviews
     */
    public void insertReviews(List<Review> reviews) {
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            System.out.println("dbConnection Successful.");
            for (Review review : reviews) {
                insertReview(review, connection);
            }
        } catch (SQLException ex) {
            System.out.println(ex);
        }
    }

    /**
     * Insert a review.
     *
     * @param review
     * @param connection
     */
    public void insertReview(Review review, Connection connection) {
        PreparedStatement statement;
        try {
            statement = connection.prepareStatement(ReviewsPreparedStatements.GET_REVIEW);
            statement.setString(1, review.getReviewId());
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                System.out.println("Review already present");
            } else {
                statement = connection.prepareStatement(ReviewsPreparedStatements.INSERT_REVIEW);
                statement.setString(1, review.getReviewId());
                statement.setString(2, review.getHotelId());
                statement.setString(3, review.getTitle());
                statement.setString(4, review.getReviewText());
                statement.setString(5, String.valueOf(review.getReviewSubmissionTime()));
                statement.setString(6, String.valueOf(review.getRatingOverall()));
                statement.setString(7, review.getUserNickname());
                statement.executeUpdate();
                statement.close();

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get paginated reviews.
     *
     * @param hotel_Id
     * @param limit
     * @param offset
     * @return
     */
    public String getPaginatedReviews(String hotel_Id, String limit, String offset) {
        PreparedStatement statement;
        List<Review> reviews = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            System.out.println("dbConnection Successful.");
            statement = connection.prepareStatement(ReviewsPreparedStatements.GET_PAGINATED_REVIEWS);
            statement.setString(1, hotel_Id);
            statement.setInt(2, Integer.parseInt(limit));
            statement.setInt(3, Integer.parseInt(offset));
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String reviewId = rs.getString("reviewId");
                String hotelId = rs.getString("hotelId");
                String title = rs.getString("title");
                String reviewText = rs.getString("reviewText");
                String reviewSubmissionTime = rs.getString("reviewSubmissionTime");
                String ratingOverall = rs.getString("ratingOverall");
                String userNickname = rs.getString("userNickname");

                Review review = new Review(hotelId, reviewId, title, reviewText, reviewSubmissionTime, Double.parseDouble(ratingOverall), userNickname);
                reviews.add(review);
            }
            if (reviews.size() == 0) {
                return null;
            } else {
                String reviewJSON = getReviewsJson(reviews, limit, hotel_Id);
                return reviewJSON;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get reviews Json.
     *
     * @param reviews
     * @param num
     * @param hotelId
     * @return
     */
    private String getReviewsJson(List<Review> reviews, String num, String hotelId) {
        JsonObject jo = new JsonObject();
        if (reviews != null && num != null && num.matches("[0-9]+")) {
            jo.addProperty("success", true);
            jo.addProperty("hotelId", hotelId);
            int numOfReviews = 0;
            JsonArray paginatedReviews = new JsonArray();
            for (Review review : reviews) {
                JsonObject joReview = new JsonObject();
                if (numOfReviews >= Integer.parseInt(num)) {
                    break;
                }
                joReview.addProperty("reviewId", review.getReviewId());
                joReview.addProperty("title", review.getTitle());
                joReview.addProperty("user", review.getUserNickname());
                joReview.addProperty("reviewText", review.getReviewText());
                joReview.addProperty("date", review.getReviewSubmissionTime().toString());
                joReview.addProperty("overallRating", review.getRatingOverall());
                paginatedReviews.add(joReview);
                numOfReviews = numOfReviews + 1;
            }
            jo.add("reviews", paginatedReviews);
        } else {
            jo.addProperty("success", false);
            jo.addProperty("hotelId", "invalid");
        }
        return jo.toString();
    }

    /**
     * Get all reviews by hotelId.
     *
     * @param hotel_Id
     * @return
     */
    public Set<Review> getAllReviewsByHotelId(String hotel_Id) {
        PreparedStatement statement;
        Set<Review> reviews = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            System.out.println("dbConnection Successful.");
            statement = connection.prepareStatement(ReviewsPreparedStatements.GET_ALLREVIEWSBYHOTELID);
            statement.setString(1, hotel_Id);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String reviewId = rs.getString("reviewId");
                String hotelId = rs.getString("hotelId");
                String title = rs.getString("title");
                String reviewText = rs.getString("reviewText");
                String reviewSubmissionTime = rs.getString("reviewSubmissionTime");
                String ratingOverall = rs.getString("ratingOverall");
                String userNickname = rs.getString("userNickname");

                Review review = new Review(hotelId, reviewId, title, reviewText, reviewSubmissionTime, Double.parseDouble(ratingOverall), userNickname);
                reviews.add(review);
            }
            return reviews;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Generate ReviewId and insert in the table.
     *
     * @param username
     * @param hotelId
     * @param title
     * @param reviewText
     * @param rating
     * @return
     */
    public String generateReviewIdAndInsertReview(String username, String hotelId, String title, String reviewText, String rating) {
        PreparedStatement statement;
        Set<Review> reviews = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            statement = connection.prepareStatement(ReviewsPreparedStatements.GET_HOTEL);
            statement.setString(1, hotelId);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {

                Random r = new Random();
                int n = r.nextInt();
                String randomNumber = Integer.toHexString(n);
                UUID uuid = UUID.nameUUIDFromBytes((username + hotelId + randomNumber).getBytes());
                String reviewId = String.valueOf(uuid);

                DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                String reviewSubmissionTime = df.format(Calendar.getInstance().getTime());
                Review review = new Review(hotelId, reviewId, title, reviewText, reviewSubmissionTime.toString(), Double.parseDouble(rating), username);

                insertReview(review, connection);
                return null;
            } else {
                return "Invalid Hotel Id.";
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get all reviews by username.
     *
     * @param username
     * @return
     */
    public Set<Review> getAllReviewsByUsername(String username) {
        PreparedStatement statement;
        Set<Review> reviews = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            System.out.println("dbConnection Successful.");
            statement = connection.prepareStatement(ReviewsPreparedStatements.GET_ALLREVIEWSBYUSERNAME);
            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String reviewId = rs.getString("reviewId");
                String hotelId = rs.getString("hotelId");
                String title = rs.getString("title");
                String reviewText = rs.getString("reviewText");
                String reviewSubmissionTime = rs.getString("reviewSubmissionTime");
                String ratingOverall = rs.getString("ratingOverall");
                String userNickname = rs.getString("userNickname");

                Review review = new Review(hotelId, reviewId, title, reviewText, reviewSubmissionTime, Double.parseDouble(ratingOverall), userNickname);
                reviews.add(review);
            }
            return reviews;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Edit review.
     *
     * @param reviewId
     * @param reviewTitle
     * @param reviewText
     */
    public void editReview(String reviewId, String reviewTitle, String reviewText) {
        PreparedStatement statement;
        Set<Review> reviews = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            System.out.println("dbConnection Successful.");
            statement = connection.prepareStatement(ReviewsPreparedStatements.GET_REVIEW);
            statement.setString(1, reviewId);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                statement = connection.prepareStatement(ReviewsPreparedStatements.UPDATE_REVIEW);
                statement.setString(1, reviewTitle);
                statement.setString(2, reviewText);
                statement.setString(3, reviewId);
                statement.executeUpdate();
            } else {
                System.out.println("Query not found.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Delete review.
     *
     * @param reviewId
     */
    public void deleteReview(String reviewId) {
        PreparedStatement statement;
        Set<Review> reviews = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            System.out.println("dbConnection Successful.");
            statement = connection.prepareStatement(ReviewsPreparedStatements.GET_REVIEW);
            statement.setString(1, reviewId);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                statement = connection.prepareStatement(ReviewsPreparedStatements.DELETE_REVIEW);
                statement.setString(1, reviewId);
                statement.executeUpdate();
            } else {
                System.out.println("Query not found.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
