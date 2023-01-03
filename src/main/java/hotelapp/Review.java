package hotelapp;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Reviews class
 */
public class Review {
    private String hotelId;

    private String reviewId;

    private String title;

    private String reviewText;

    private LocalDate reviewSubmissionTime;

    private double ratingOverall;

    private String userNickname;

    /**
     * Constructor to set values of the variables
     *
     * @param hotelId
     * @param reviewId
     * @param title
     * @param reviewText
     * @param reviewSubmissionTime
     * @param ratingOverall
     * @param userNickname
     */
    public Review(String hotelId, String reviewId, String title, String reviewText, String reviewSubmissionTime, double ratingOverall, String userNickname) {
        this.hotelId = hotelId;
        this.reviewId = reviewId;
        this.title = title;
        this.reviewText = reviewText;
        try {
            this.reviewSubmissionTime = LocalDate.from(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(reviewSubmissionTime));
        } catch (Exception e) {
            this.reviewSubmissionTime = LocalDate.parse(reviewSubmissionTime);
        }
        this.ratingOverall = ratingOverall;
        if (!userNickname.trim().equals("")) {
            this.userNickname = userNickname;
        } else {
            this.userNickname = "Anonymous";
        }
    }

    /**
     * Get hotel id.
     *
     * @return
     */
    public String getHotelId() {
        return hotelId;
    }

    /**
     * Get review id.
     *
     * @return
     */
    public String getReviewId() {
        return reviewId;
    }

    /**
     * Get submissionDate
     *
     * @return
     */
    public LocalDate getReviewSubmissionTime() {
        return reviewSubmissionTime;
    }

    /**
     * Get review text.
     *
     * @return
     */
    public String getReviewText() {
        return reviewText;
    }

    /**
     * Get title.
     *
     * @return
     */
    public String getTitle() {
        return title;
    }

    /**
     * Get overall rating.
     *
     * @return
     */
    public double getRatingOverall() {
        return ratingOverall;
    }

    /**
     * Get username.
     *
     * @return
     */
    public String getUserNickname() {
        return userNickname;
    }

    /**
     * @return Appended string of all field of the class.
     */
    @Override
    public String toString() {
        return "hotelId=" + hotelId + System.lineSeparator() +
                "reviewId=" + reviewId + System.lineSeparator() +
                "title=" + title + System.lineSeparator() +
                "reviewText=" + reviewText + System.lineSeparator() +
                "reviewSubmissionTime=" + reviewSubmissionTime + System.lineSeparator() +
                "userNickname=" + userNickname;
    }
}
