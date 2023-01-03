package hotelapp;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dboperations.ReviewsDatabaseHandler;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;

/**
 * ReviewsJsonParser contains all methods to parse json of every directory and create HashMap.
 */
public class ReviewsJsonParser {

    private ThreadSafeReviewCollection threadSafeReviewCollection;
    private ExecutorService poolThreads;
    private Phaser phaser;
    private Boolean isFindWordHashMapRequired;

    public ReviewsJsonParser(int noOfThreads, ThreadSafeReviewCollection threadSafeReviewCollection){
        this.threadSafeReviewCollection = threadSafeReviewCollection;
        poolThreads = Executors.newFixedThreadPool(noOfThreads);
        phaser =  new Phaser();
    }

    /**
     * The directoryParser method process every directory and checks if it contains json files. If the directory is a json file, it is further sent for parsing.
     * @param directory
     * @throws IOException
     */
    public void traverseDirectory(String directory) {
        Path p = Paths.get(directory);
        try(DirectoryStream<Path> pathsInDir = Files.newDirectoryStream(p)) {
            for (Path filePath : pathsInDir){
                if(!Files.isDirectory(filePath) && filePath.toString().endsWith(".json")){
                    Worker worker = new Worker(filePath);
                    phaser.register();
                    poolThreads.submit(worker);
                }
                else if(Files.isDirectory(filePath)){
                    traverseDirectory(filePath.toString());
                }
            }
        }
        catch(IOException e){
            System.out.println("processFiles Exception:" + e);
        }
    }

    /**
     * Waits for the phaser to complete all the thread and shuts down the pool after that.
     */
    public void shutDownExecutor() throws InterruptedException {
        try{
            phaser.awaitAdvance(phaser.getPhase());
            poolThreads.shutdown();
        }
        finally {
            poolThreads.awaitTermination(2, TimeUnit.SECONDS);
        }
    }


    /**
     * Worker class to run a particular thread.
     */
    class Worker implements Runnable {
        private Path path;

        public Worker(Path path){
            this.path = path;
        }
        @Override
        public void run() {
            try{
                parseReviewJson(path.toString());
            }
            finally {
                phaser.arriveAndDeregister();
            }
        }
    }

    /**
     * Parse review json. Create hashmap to find review and find word.
     * @param filepath
     */
    private void parseReviewJson(String filepath){
        try(FileReader br =new FileReader(filepath)) {
            List<Review> reviews = new ArrayList<>();
            JsonParser parser = new JsonParser();
            JsonObject jo = (JsonObject) parser.parse(br);
            JsonArray jsonArr = jo.getAsJsonObject("reviewDetails").getAsJsonObject("reviewCollection").getAsJsonArray("review");

            //create a list of reviews
            for (JsonElement e : jsonArr) {
                JsonObject review = e.getAsJsonObject();
                String hotelId = review.get("hotelId").getAsString();
                String title = review.get("title").getAsString();
                String reviewId = review.get("reviewId").getAsString();
                String reviewText = review.get("reviewText").getAsString();
                String reviewSubmissionTime = review.get("reviewSubmissionTime").getAsString();
                double ratingOverall = review.get("ratingOverall").getAsDouble();
                String userNickname = review.get("userNickname").getAsString();
                Review reviewObj = new Review(hotelId, reviewId, title, reviewText, reviewSubmissionTime, ratingOverall, userNickname);
                reviews.add(reviewObj);
            }

            //Add review to database
//            ReviewsDatabaseHandler dbHandler = ReviewsDatabaseHandler.getInstance();
//            dbHandler.insertReviews(reviews);

            threadSafeReviewCollection.createFindReviewHashMap(reviews);
            if(isFindWordHashMapRequired){
                threadSafeReviewCollection.createFindWordHashMap(reviews);
            }
        }
        catch(IOException e){
            System.out.println("parseReviewJson Exception:" + e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * function setIsFindWordWordHashMapRequired is used to set the isFindWordHashMapRequired flag.
     * @param isFindWordHashMapRequired
     */
    public void setIsFindWordWordHashMapRequired(Boolean isFindWordHashMapRequired){
        this.isFindWordHashMapRequired = isFindWordHashMapRequired;
    }

}
