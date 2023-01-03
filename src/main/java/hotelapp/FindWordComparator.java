package hotelapp;

import java.util.Comparator;

/**
 * FindWordComparator is used sort reviews based on word count in a particular review.
 *
 * @implements Comparator
 */
public class FindWordComparator implements Comparator<ReviewWithFrequency> {
    private String key;

    public FindWordComparator(String word) {
        this.key = word.toLowerCase();
    }

    @Override
    public int compare(ReviewWithFrequency o1, ReviewWithFrequency o2) {
        int review1Count = o1.getFrequency();
        int review2Count = o2.getFrequency();

        if (review1Count == review2Count) {
            if (o2.getReview().getReviewSubmissionTime().compareTo(o1.getReview().getReviewSubmissionTime()) == 0) {
                return o2.getReview().getReviewId().compareTo(o1.getReview().getReviewId());
            }
            return o2.getReview().getReviewId().compareTo(o1.getReview().getReviewId());
        }
        return review2Count - review1Count;
    }
}
