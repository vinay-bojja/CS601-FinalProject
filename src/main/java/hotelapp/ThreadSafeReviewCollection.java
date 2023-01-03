package hotelapp;

import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * ThreadSafeReviewCollection is a thread safe class of ReviewsCollection.
 */
public class ThreadSafeReviewCollection extends ReviewCollection {
    private ReentrantReadWriteLock lock;

    /**
     * Constructor to set values of the instance variables.
     */
    public ThreadSafeReviewCollection() {
        lock = new ReentrantReadWriteLock();
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
    @Override
    public String createReviewsByUsername(String username, String hotelId, String title, String reviewText, String rating) {
        try {
            lock.writeLock().lock();
            return super.createReviewsByUsername(username, hotelId, title, reviewText, rating);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * A Set of reviews will return on passing username as a parameter
     *
     * @param username
     * @return
     */
    @Override
    public Set<Review> getReviewsByUsername(String username) {
        try {
            lock.readLock().lock();
            return super.getReviewsByUsername(username);
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Edits review of the hashmaps.
     *
     * @param reviewId
     * @param username
     * @param reviewTitle
     * @param reviewText
     */
    @Override
    public void editReview(String reviewId, String username, String reviewTitle, String reviewText) {
        try {
            lock.writeLock().lock();
            super.editReview(reviewId, username, reviewTitle, reviewText);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Delete review from the hashMaps.
     *
     * @param reviewId
     * @param username
     */
    @Override
    public void deleteReview(String reviewId, String username) {
        try {
            lock.writeLock().lock();
            super.deleteReview(reviewId, username);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Builds reviewsByHotelId HashMap
     *
     * @param reviews
     */
    @Override
    public void createFindReviewHashMap(List<Review> reviews) {
        try {
            lock.writeLock().lock();
            super.createFindReviewHashMap(reviews);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Builds reviewsByWord HashMap
     *
     * @param reviews
     */
    @Override
    public void createFindWordHashMap(List<Review> reviews) throws InterruptedException {
        try {
            lock.writeLock().lock();
            super.createFindWordHashMap(reviews);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * return set of reviews on passing hotelId.
     *
     * @param hotelId
     * @return
     */
    @Override
    public Set<Review> getReviews(String hotelId) {
        try {
            lock.readLock().lock();
            return super.getReviews(hotelId);
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Create and return reviews json string.
     *
     * @param reviews
     * @param num
     * @param hotelId
     * @return
     */
    @Override
    public String getReviewsJson(Set<Review> reviews, String num, String hotelId) {
        try {
            lock.readLock().lock();
            return super.getReviewsJson(reviews, num, hotelId);
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * return set of reviewWithFrequency on passing word as a parameter.
     *
     * @param word
     * @return
     */
    @Override
    public Set<ReviewWithFrequency> getWord(String word) {
        try {
            lock.readLock().lock();
            return super.getWord(word);
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Create and return index json string.
     *
     * @param reviewWithFrequencies
     * @param num
     * @param word
     * @return
     */
    @Override
    public String getIndexJson(Set<ReviewWithFrequency> reviewWithFrequencies, String num, String word) {
        try {
            lock.readLock().lock();
            return super.getIndexJson(reviewWithFrequencies, num, word);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
