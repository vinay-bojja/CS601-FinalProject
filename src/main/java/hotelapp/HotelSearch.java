package hotelapp;

import server.JettyServer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 * The driver class for project 4
 * Copy your project 3 classes to the hotelapp package.
 * The main function should take the following command line arguments:
 * -hotels hotelFile -reviews reviewsDirectory -threads numThreads -output filepath
 * (order may be different)
 * and read general information about the hotels from the hotelFile (a JSON file),
 * as read hotel reviews from the json files in reviewsDirectory (can be multithreaded or
 * single-threaded).
 * The data should be loaded into data structures that allow efficient search.
 * If the -output flag is provided, hotel information (about all hotels and reviews)
 * should be output into the given file.
 * Then in the main method, you should start an HttpServer that responds to
 * requests about hotels and reviews.
 * See pdf description of the project for the requirements.
 * You are expected to add other classes and methods from project 3 to this project,
 * and take instructor's / TA's feedback from a code review into account.
 * Please download the input folder from Canvas.
 */
public class HotelSearch {
    private ReviewsJsonParser reviewsJsonParser;
    private HotelJsonParser hotelJsonParser;
    private ThreadSafeReviewCollection threadSafeReviewCollection;
    private ThreadSafeHotelCollection threadSafeHotelCollection;

    HotelSearch(int threads) {
        threadSafeReviewCollection = new ThreadSafeReviewCollection();
        reviewsJsonParser = new ReviewsJsonParser(threads, threadSafeReviewCollection);
        threadSafeHotelCollection = new ThreadSafeHotelCollection();
        hotelJsonParser = new HotelJsonParser(threadSafeHotelCollection);
    }

    /**
     * Name: VINAY HARESH BOJJA.
     * Entry point to the project
     *
     * @param args Accepts hotel json file and review folder location as an argument in a specific format
     * @throws IOException
     */
    public static void main(String[] args) {
        try {
            if ((args.length) != 0) {
                Map<String, String> argumentsHashMap = new HashMap<>();
                int threads;
                for (int i = 0; i < args.length; i = i + 2) {
                    argumentsHashMap.put(args[i], args[i + 1]);
                }

                //Stores thread if present else stores default thread as 1
                if (argumentsHashMap.containsKey("-threads")) {
                    threads = Integer.parseInt(argumentsHashMap.get("-threads"));
                } else {
                    threads = 1;
                }
                HotelSearch hotelSearchObj = new HotelSearch(threads);

                //if output file location is given in arguments then just parse and print to outputFile.
                hotelSearchObj.parseHotelFilesAndLoadHashMaps(argumentsHashMap.get("-hotels"));
                if (argumentsHashMap.containsKey("-output")) {
                    if (argumentsHashMap.containsKey("-hotels") && !argumentsHashMap.containsKey("-reviews")) {
                        hotelSearchObj.printHotelsToOutputFile(argumentsHashMap.get("-output"));
                    } else {
                        hotelSearchObj.reviewsJsonParser.setIsFindWordWordHashMapRequired(true);
                        hotelSearchObj.parseReviewFilesAndLoadHashMap(argumentsHashMap.get("-reviews"));
                        hotelSearchObj.printReviewsToOutputFile(argumentsHashMap.get("-output"));
                    }
                }//search operation
                else {
                    hotelSearchObj.reviewsJsonParser.setIsFindWordWordHashMapRequired(true);
                    hotelSearchObj.parseReviewFilesAndLoadHashMap(argumentsHashMap.get("-reviews"));
                    //hotelSearchObj.performSearchOperations();

                    //jetty Server
                    hotelSearchObj.createJettyServer();
                }

            } else {
                System.out.println("Incorrect arguments.");
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            System.out.println("Error passing arguments." + e);
        }
    }

    /**
     * Creates Http server using Jetty
     */
    private void createJettyServer() {
        System.out.println("jetty server.");
        JettyServer server = new JettyServer();
        try {
            server.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Provides a menu to perform search operations based on the input provided.
     *
     * @return void
     */
    private void performSearchOperations() {
        Scanner sc = new Scanner(System.in);
        String command = "";
        do {
            System.out.println("------------------------------------------------------------");
            System.out.println("| find <hotelId> | findReviews <hotelId> | findWord <word> |");
            System.out.println("------------------------------------------------------------");
            System.out.print("Please enter one of the command as shown above: ");
            String input = sc.nextLine();
            String[] keywords = input.split(" ");
            if (keywords.length == 2) {
                if (keywords[0].toLowerCase().equals("q")) {
                    command = "exit";
                } else if (keywords[0].toLowerCase().equals("find")) {
                    performFindOperation(keywords[1]);
                } else if (keywords[0].toLowerCase().equals("findreviews")) {
                    performFindReviewsOperation(keywords[1].toLowerCase());
                } else if (keywords[0].toLowerCase().equals("findword")) {
                    performFindWordOperation(keywords[1].toLowerCase());
                } else {
                    System.out.println("Command not found. Please enter valid command." + System.lineSeparator());
                }
            } else {
                System.out.println("Invalid command. Please enter command only in above given format." + System.lineSeparator());
            }
        } while (!command.equals("exit"));
    }

    /**
     * Parse files and creates HashMaps of Hotel.
     *
     * @param hotelFile
     */
    private void parseHotelFilesAndLoadHashMaps(String hotelFile) {
        hotelJsonParser.parseHotelJson(hotelFile);
    }

    /**
     * Parse files and creates HashMaps of Review.
     *
     * @param reviewsDirectory
     */
    private void parseReviewFilesAndLoadHashMap(String reviewsDirectory) throws InterruptedException {
        reviewsJsonParser.traverseDirectory(reviewsDirectory);
        reviewsJsonParser.shutDownExecutor();
    }

    /**
     * Print hotel files data to output file.
     *
     * @param output
     */
    private void printHotelsToOutputFile(String output) {
        try (PrintWriter writer = new PrintWriter(output)) {
            Set<String> hotelIds = threadSafeHotelCollection.getAllHotelIds();
            for (String hotelId : hotelIds) {
                Hotel hotel = threadSafeHotelCollection.getHotel(hotelId);
                writer.println(System.lineSeparator() + "********************");
                writer.println(hotel.getHotelName() + ": " + hotel.getHotelId());
                writer.println(hotel.getAddress());
                writer.println(hotel.getCity() + ", " + hotel.getState());
                writer.println();
            }
            writer.flush();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Print review files data to output file.
     *
     * @param output
     */
    private void printReviewsToOutputFile(String output) {
        try (PrintWriter writer = new PrintWriter(output)) {
            Set<String> hotelIds = threadSafeHotelCollection.getAllHotelIds();
            for (String hotelId : hotelIds) {
                Hotel hotel = threadSafeHotelCollection.getHotel(hotelId);
                Set<Review> reviews = threadSafeReviewCollection.getReviews(hotelId);
                writer.println(System.lineSeparator() + "********************");
                writer.println(hotel.getHotelName() + ": " + hotel.getHotelId());
                writer.println(hotel.getAddress());
                writer.println(hotel.getCity() + ", " + hotel.getState());
                if (reviews != null) {
                    for (Review review : reviews) {
                        writer.println("--------------------");
                        writer.println("Review by " + review.getUserNickname() + " on " + review.getReviewSubmissionTime());
                        writer.println("Rating: " + (int) review.getRatingOverall());
                        writer.println("ReviewId: " + review.getReviewId());
                        writer.println(review.getTitle());
                        writer.println(review.getReviewText());
                    }
                }
            }
            writer.flush();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Performs find operation.
     *
     * @param hotelId
     */
    private void performFindOperation(String hotelId) {
        Hotel hotel = threadSafeHotelCollection.getHotel(hotelId);
        if (hotel != null) {
            System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
            System.out.println(hotel);
            System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        } else {
            System.out.println("Invalid HotelId.");
        }
        System.out.println(System.lineSeparator());
    }

    /**
     * Performs findReviews operation.
     *
     * @param hotelId
     */
    private void performFindReviewsOperation(String hotelId) {
        Set<Review> reviews = threadSafeReviewCollection.getReviews(hotelId);
        if (reviews != null) {
            for (Review r : reviews) {
                System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                System.out.println(r.toString());
            }
            System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++" + System.lineSeparator());
        } else {
            System.out.println("No reviews found." + System.lineSeparator());
        }
    }

    /**
     * Performs findWord operation.
     *
     * @param word
     */
    private void performFindWordOperation(String word) {
        Set<ReviewWithFrequency> returnedTreeSet = threadSafeReviewCollection.getWord(word);
        if (returnedTreeSet != null) {
            for (ReviewWithFrequency reviewWithFrequency : returnedTreeSet) {
                System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                System.out.println(reviewWithFrequency);
            }
            System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++" + System.lineSeparator());
        } else {
            System.out.println("No word found." + System.lineSeparator());
        }
    }
}
