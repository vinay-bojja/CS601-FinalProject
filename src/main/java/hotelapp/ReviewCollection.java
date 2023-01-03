package hotelapp;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Review Collection has methods and implementation of Review HashMaps.
 */
public class ReviewCollection {
    private Map<String, SortedSet<Review>> reviewsByHotelId = new HashMap<>();
    private Map<String, SortedSet<ReviewWithFrequency>> reviewsByWord = new HashMap<>();
    private Map<String, Set<Review>> reviewsByUsername = new HashMap<>();
    private Set<String> stopWords;

    /**
     * Constructor to set values of the instance variables.
     */
    public ReviewCollection() {
        StopWords stopWordObj = new StopWords();
        this.stopWords = stopWordObj.getStopWords("/Users/vinaybojja/Desktop/USFCA_601/Project4_WebServer/Project4_WebServer/input/stopWord");
    }

    /**
     * Add review to the hashmaps.
     *
     * @param username
     * @param hotelId
     * @param title
     * @param reviewText
     * @param rating
     * @return
     */
    public String createReviewsByUsername(String username, String hotelId, String title, String reviewText, String rating) {

        if (reviewsByHotelId.containsKey(hotelId)) {

            Random r = new Random();
            int n = r.nextInt();
            String randomNumber = Integer.toHexString(n);
            UUID uuid = UUID.nameUUIDFromBytes((username + hotelId + randomNumber).getBytes());
            String reviewId = String.valueOf(uuid);

            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            String reviewSubmissionTime = df.format(Calendar.getInstance().getTime());
            Review review = new Review(hotelId, reviewId, title, reviewText, reviewSubmissionTime.toString(), Double.parseDouble(rating), username);
            if (!reviewsByUsername.containsKey(username)) {
                Set<Review> sortedReviews = new HashSet<>();
                sortedReviews.add(review);
                reviewsByUsername.put(username, sortedReviews);
            } else {
                Set<Review> sortedReviews = reviewsByUsername.get(username);
                sortedReviews.add(review);
            }

            //add to reviewsByhotelId
            List<Review> reviewList = new ArrayList<>();
            reviewList.add(review);
            this.createFindReviewHashMap(reviewList);
            return null;
        } else {
            return "Invalid Hotel Id.";
        }
    }

    /**
     * A Set of reviews will return on passing username as a parameter
     *
     * @param username
     * @return
     */
    public Set<Review> getReviewsByUsername(String username) {
        Set<Review> reviews = reviewsByUsername.get(username);
        if (reviews != null) {
            return Collections.unmodifiableSet(reviews);
        }
        return null;
    }

    /**
     * Edits review of the hashmaps.
     *
     * @param reviewId
     * @param username
     * @param reviewTitle
     * @param reviewText
     */
    public void editReview(String reviewId, String username, String reviewTitle, String reviewText) {
        //update values in new reviewsByUsername
        Set<Review> userReviews = reviewsByUsername.get(username);
        Review toBeDeletedFromReviewsByUsername = null;
        for (Review review : userReviews) {
            if (review.getReviewId().equals(reviewId)) {
                toBeDeletedFromReviewsByUsername = review;
                //update reviewsByHotelId
                SortedSet<Review> hotelReviews = reviewsByHotelId.get(review.getHotelId());
                hotelReviews.remove(toBeDeletedFromReviewsByUsername);
            }
        }
        createReviewsByUsername(toBeDeletedFromReviewsByUsername.getUserNickname(), toBeDeletedFromReviewsByUsername.getHotelId(), reviewTitle, reviewText, String.valueOf(toBeDeletedFromReviewsByUsername.getRatingOverall()));
        userReviews.remove(toBeDeletedFromReviewsByUsername);
    }

    /**
     * Delete review from the hashMaps.
     *
     * @param reviewId
     * @param username
     */
    public void deleteReview(String reviewId, String username) {
        Set<Review> userReviews = reviewsByUsername.get(username);
        Review reviewObject = null;
        SortedSet<Review> hotelReviews = null;
        for (Review review : userReviews) {
            if (review.getReviewId().equals(reviewId)) {
                hotelReviews = reviewsByHotelId.get(review.getHotelId());
                reviewObject = review;
            }
        }
        userReviews.remove(reviewObject);
        hotelReviews.remove(reviewObject);
    }

    /**
     * Builds reviewsByHotelId HashMap
     *
     * @param reviews
     */
    public void createFindReviewHashMap(List<Review> reviews) {
        for (Review r : reviews) {
            if (!reviewsByHotelId.containsKey(r.getHotelId())) {
                FindReviewsComparator findReviewsComparatorObj = new FindReviewsComparator();
                TreeSet<Review> ts = new TreeSet<>(findReviewsComparatorObj);
                ts.add(r);
                reviewsByHotelId.put(r.getHotelId(), ts);
            } else {
                SortedSet<Review> ts = reviewsByHotelId.get(r.getHotelId());
                ts.add(r);
            }
        }
    }

    /**
     * Builds reviewsByWord HashMap
     *
     * @param reviews
     */
    public void createFindWordHashMap(List<Review> reviews) throws InterruptedException {
        for (Review r : reviews) {
            String reviewText = r.getReviewText();
            String[] reviewWords = reviewText.split("\\W");

            for (String word : reviewWords) {
                word = word.toLowerCase();
                if (!stopWords.contains(word) && !word.equals("")) {
                    if (reviewsByWord.containsKey(word)) {
                        SortedSet<ReviewWithFrequency> ts = reviewsByWord.get(word);
                        ReviewWithFrequency rwf = new ReviewWithFrequency(r, word);
                        if (!ts.contains(rwf)) {
                            ts.add(rwf);
                        }
                    } else {
                        FindWordComparator findWordComparatorObj = new FindWordComparator(word);
                        TreeSet<ReviewWithFrequency> ts = new TreeSet<>(findWordComparatorObj);
                        ReviewWithFrequency rwf = new ReviewWithFrequency(r, word);
                        ts.add(rwf);
                        reviewsByWord.put(word, ts);
                    }
                }
            }
        }
    }

    /**
     * return set of reviews on passing hotelId.
     *
     * @param hotelId
     * @return
     */
    public Set<Review> getReviews(String hotelId) {
        SortedSet<Review> review = reviewsByHotelId.get(hotelId);
        if (review != null) {
            return Collections.unmodifiableSet(review);
        }
        return null;
    }

    /**
     * Create and return reviews json string.
     *
     * @param reviews
     * @param num
     * @param hotelId
     * @return
     */
    public String getReviewsJson(Set<Review> reviews, String num, String hotelId) {
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
     * return set of reviewWithFrequency on passing word as a parameter.
     *
     * @param word
     * @return
     */
    public Set<ReviewWithFrequency> getWord(String word) {
        SortedSet<ReviewWithFrequency> reviewWithFrequency = reviewsByWord.get(word);
        if (reviewWithFrequency != null) {
            return Collections.unmodifiableSet(reviewWithFrequency);
        }
        return null;
    }

    /**
     * Create and return index json string.
     *
     * @param reviewWithFrequencies
     * @param num
     * @param word
     * @return
     */
    public String getIndexJson(Set<ReviewWithFrequency> reviewWithFrequencies, String num, String word) {
        JsonObject jo = new JsonObject();
        if (reviewWithFrequencies != null && num != null && num.matches("[0-9]+")) {
            jo.addProperty("success", true);
            jo.addProperty("word", word);
            int numOfReviews = 0;
            JsonArray paginatedReviews = new JsonArray();
            for (ReviewWithFrequency reviewWithFrequency : reviewWithFrequencies) {
                JsonObject joReview = new JsonObject();
                if (numOfReviews >= Integer.parseInt(num)) {
                    break;
                }
                joReview.addProperty("reviewId", reviewWithFrequency.getReview().getReviewId());
                joReview.addProperty("title", reviewWithFrequency.getReview().getTitle());
                joReview.addProperty("user", reviewWithFrequency.getReview().getUserNickname());
                joReview.addProperty("reviewText", reviewWithFrequency.getReview().getReviewText());
                joReview.addProperty("date", reviewWithFrequency.getReview().getReviewSubmissionTime().toString());
                paginatedReviews.add(joReview);
                numOfReviews = numOfReviews + 1;
            }
            jo.add("reviews", paginatedReviews);
        } else {
            jo.addProperty("success", false);
            jo.addProperty("word", "invalid");
        }
        return jo.toString();
    }

    @Override
    public String toString() {
        return "ReviewCollection{" +
                "reviewsByHotelId=" + reviewsByHotelId +
                ", reviewsByWordMap=" + reviewsByWord +
                ", stopWords=" + stopWords +
                '}';
    }
}
