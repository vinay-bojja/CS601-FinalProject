package hotelapp;

/**
 * ReviewWithFrequency Store a review, word and its frequency in the review.
 */
public class ReviewWithFrequency {
    private Review review;
    private int frequency;
    private String word;

    /**
     * Constructor to calculate and set values of the instance variables.
     *
     * @param review
     * @param word
     */
    public ReviewWithFrequency(Review review, String word) {
        this.review = review;
        this.word = word;
        //calculate frequency and store in frequency
        String[] reviewWords = review.getReviewText().split("\\W");
        int count = 0;
        for (String reviewWord : reviewWords) {
            if (reviewWord.toLowerCase().equals(word)) {
                count = count + 1;
            }
        }
        this.frequency = count;
    }

    /**
     * Get review.
     *
     * @return
     */
    public Review getReview() {
        return review;
    }

    /**
     * Get frequency.
     *
     * @return
     */
    public int getFrequency() {
        return frequency;
    }

    /**
     * Get Word.
     *
     * @return
     */
    public String getWord() {
        return word;
    }

    @Override
    public String toString() {
        return review + System.lineSeparator() +
                "frequency=" + frequency;
    }
}
