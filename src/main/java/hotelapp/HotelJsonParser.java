package hotelapp;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dboperations.HotelsDatabaseHandler;

import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

/**
 * HotelJsonParser contains all methods to parse json and create HashMap
 */
public class HotelJsonParser {
    private ThreadSafeHotelCollection threadSafeHotelCollection;

    /**
     * Constructor to set values of instance variables.
     *
     * @param threadSafeHotelCollection
     */
    public HotelJsonParser(ThreadSafeHotelCollection threadSafeHotelCollection) {
        this.threadSafeHotelCollection = threadSafeHotelCollection;
    }

    /**
     * Parse hotel json.
     *
     * @param filepath
     * @throws IOException
     */
    public void parseHotelJson(String filepath) {
        Gson gson = new Gson();

        try (FileReader br = new FileReader(filepath)) {
            JsonParser parser = new JsonParser();
            JsonObject jo = (JsonObject) parser.parse(br);

            JsonArray jsonArr = jo.getAsJsonArray("sr");
            Hotel[] hotels = gson.fromJson(jsonArr, Hotel[].class);

            //Add hotel data to Database
//            HotelsDatabaseHandler dbHandler = HotelsDatabaseHandler.getInstance();
//            dbHandler.addHotels(Arrays.asList(hotels));
//            dbHandler.addKeyword(Arrays.asList(hotels));

            threadSafeHotelCollection.createFindHotelHashMap(Arrays.asList(hotels));
            threadSafeHotelCollection.buildHotelsByKeywordMap(Arrays.asList(hotels));
        } catch (IOException e) {
            System.out.println("parseHotelJson Exception:" + e);
        } catch (Exception e) {
            System.out.println("parseHotelJson Exception:" + e);
        }
    }

}
