package dboperations;

import java.time.LocalDate;

public class ReviewsPreparedStatements {
    /**
     * Create reviews table
     */
    public static final String CREATE_REVIEWS_TABLE =
            "CREATE TABLE reviews (" +
                    "reviewId VARCHAR(320) NOT NULL UNIQUE," +
                    "hotelId VARCHAR(32) NOT NULL," +
                    "title VARCHAR(3200)," +
                    "reviewText VARCHAR(3200)," +
                    "reviewSubmissionTime DATE NOT NULL," +
                    "ratingOverall DOUBLE(32, 2) NOT NULL," +
                    "userNickname VARCHAR(50) NOT NULL);";

    /**
     * Get all reviews.
     */
    public static final String SELECT_ALL_REVIEWS = "SELECT * FROM reviews;";

    /**
     * Get all reviews of a particular reviewId.
     */
    public static final String GET_REVIEW = "SELECT * FROM reviews WHERE reviewId = ?;";

    /**
     * Insert review.
     */
    public static final String INSERT_REVIEW = "INSERT INTO reviews (reviewId, hotelId, title, reviewText, reviewSubmissionTime, ratingOverall, userNickname) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?);";

    /**
     * Get reviews having an offset and limit.
     */
    public static final String GET_PAGINATED_REVIEWS = "SELECT * FROM reviews WHERE hotelId = ? order by reviewSubmissionTime desc limit ? offset ?;";

    /**
     * Get all reviews by HotelId.
     */
    public static final String GET_ALLREVIEWSBYHOTELID = "SELECT * FROM reviews WHERE hotelId = ?;";
    /**
     * Get all hotels on providing hotelId.
     */
    public static final String GET_HOTEL = "SELECT * FROM hotels WHERE hotelId = ?;";

    /**
     * Get from users based on usernickname.
     */
    public static final String GET_ALLREVIEWSBYUSERNAME = "SELECT * FROM reviews WHERE userNickname = ?;";

    /**
     * Update reviews.
     */
    public static final String UPDATE_REVIEW = "UPDATE reviews SET title = ?, reviewText = ? WHERE reviewId = ?;";

    /**
     * Delete reviews.
     */
    public static final String DELETE_REVIEW = "DELETE FROM reviews WHERE reviewId = ?;";

}
