package hotelapp;

import java.util.Comparator;

/**
 * FindReviewsComparator is used sort reviews based on conditions mentioned in compare method.
 *
 * @implements Comparator
 */
public class FindReviewsComparator implements Comparator<Review> {

    @Override
    public int compare(Review o1, Review o2) {
        if (o1.getReviewSubmissionTime().compareTo(o2.getReviewSubmissionTime()) == 0) {
            return o1.getReviewId().compareTo(o2.getReviewId());
        }
        return o2.getReviewSubmissionTime().compareTo(o1.getReviewSubmissionTime());
    }
}
