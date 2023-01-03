package hotelapp;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.URL;
import java.util.*;

/**
 * Hotel Collection has methods and implementation of Hotel HashMaps.
 */
public class HotelCollection {
    private Map<String, Hotel> hotelByHotelId = new TreeMap<>();

    private Map<String, Set<Hotel>> hotelsByKeyword = new HashMap<>();

    /**
     * Build findHotel hashmap.
     *
     * @param hotels
     */
    public void createFindHotelHashMap(List<Hotel> hotels) {
        for (Hotel h : hotels) {
            hotelByHotelId.put(h.getHotelId(), h);
        }
    }

    /**
     * Build HotelsByKeyword map.
     *
     * @param hotels
     */
    public void buildHotelsByKeywordMap(List<Hotel> hotels) {
        for (Hotel hotel : hotels) {
            //add h to ""
            if (hotelsByKeyword.containsKey("")) {
                Set<Hotel> hotelsSet = hotelsByKeyword.get("");
                hotelsSet.add(hotel);
            } else {
                Set<Hotel> hotelsSet = new HashSet<>();
                hotelsSet.add(hotel);
                hotelsByKeyword.put("", hotelsSet);
            }

            //split hotel name
            String[] keywords = hotel.getHotelName().split("\\W");
            for (String keyword : keywords) {
                keyword = keyword.toLowerCase();
                if (hotelsByKeyword.containsKey(keyword)) {
                    Set<Hotel> hotelsSet = hotelsByKeyword.get(keyword);
                    hotelsSet.add(hotel);
                } else {
                    Set<Hotel> hotelsSet = new HashSet<>();
                    hotelsSet.add(hotel);
                    hotelsByKeyword.put(keyword, hotelsSet);
                }
            }
        }
    }

    /**
     * On passing a word asa parameter, the function will return a set of hotel.
     *
     * @param keyword
     * @return
     */
    public Set<Hotel> getHotelsByKeyword(String keyword) {
        Set<Hotel> hotels = hotelsByKeyword.get(keyword);
        return hotels;
    }

    /**
     * Method return Hotel object of a particular hotelID.
     *
     * @param hotelId
     * @return
     */
    public Hotel getHotel(String hotelId) {
        Hotel hotel;
        if (hotelId != null) {
            hotel = hotelByHotelId.get(hotelId);
        } else {
            hotel = null;
        }
        return hotel;
    }

    /**
     * Create and return hotel json.
     *
     * @param hotel
     * @return
     */
    public String getHotelInfoJson(Hotel hotel) {
        JsonObject jo = new JsonObject();
        if (hotel != null) {
            jo.addProperty("name", hotel.getHotelName());
            jo.addProperty("hotelId", hotel.getHotelId());
            jo.addProperty("success", true);
            jo.addProperty("addr", hotel.getAddress());
            jo.addProperty("city", hotel.getCity());
            jo.addProperty("state", hotel.getState());
            jo.addProperty("lat", hotel.getCoordinates().getLatitude());
            jo.addProperty("lng", hotel.getCoordinates().getLongitude());
        } else {
            jo.addProperty("hotelId", "invalid");
            jo.addProperty("success", false);
        }
        return jo.toString();
    }

    /**
     * Returns all the hotelIds in the hashMap.
     *
     * @return
     */
    public Set<String> getAllHotelIds() {
        Set<String> hotelIds = hotelByHotelId.keySet();
        return hotelIds;
    }

    /**
     * The function returns json containing weather details if the co-ordinates of hotel are valid otherwise it returns json containing invalid result.
     *
     * @param hotel
     * @param hotelId
     * @return
     */
    public String getWeatherJson(Hotel hotel, String hotelId) {
        String temperature = "";
        if (hotel != null) {
            temperature = getWeatherJson(hotel);
        } else {
            temperature = returnInvalidResults(hotelId);
        }
        return temperature;
    }

    /**
     * The function gets weather using the co-ordinates and returns weather json of hotel.
     *
     * @param hotel
     * @return
     */
    private String getWeatherJson(Hotel hotel) {
        String longitude = hotel.getCoordinates().getLongitude();
        String latitude = hotel.getCoordinates().getLatitude();
        String urlString = "https://api.open-meteo.com/v1/forecast?latitude=" + latitude + "&longitude=" + longitude + "&current_weather=true";
        PrintWriter out = null;
        BufferedReader in = null;
        SSLSocket socket = null;

        try {
            URL url = new URL(urlString);
            SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            socket = (SSLSocket) factory.createSocket(url.getHost(), 443);
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            String request = getRequest(url.getHost(), url.getPath() + "?" + url.getQuery());

            out.println(request);
            out.flush();

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line;
            StringBuffer sb = new StringBuffer();
            while ((line = in.readLine()) != null) {
                if (line.contains("{")) {
                    sb.append(line);
                }
            }
            String weatherJson = parseJSONAndReturnCurrentWeather(sb.toString(), hotel);

            return weatherJson;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                out.close();
                in.close();
                socket.close();
            } catch (IOException e) {
                System.out.println("An exception occured while trying to close the streams or the socket: " + e);
            }
        }

    }

    /**
     * The function builds and returns weather json to getWeatherJson function based on the data passed to the function.
     *
     * @param s
     * @param hotel
     * @return
     */
    private String parseJSONAndReturnCurrentWeather(String s, Hotel hotel) {
        JsonParser parser = new JsonParser();
        JsonObject jo = (JsonObject) parser.parse(s);
        JsonObject currentWeather = jo.getAsJsonObject("current_weather");
        currentWeather.addProperty("name", hotel.getHotelName());
        currentWeather.addProperty("hotelId", hotel.getHotelId());
        currentWeather.addProperty("success", true);
        return currentWeather.toString();
    }

    /**
     * The function returns json containing invalid results.
     *
     * @param hotelId
     * @return
     */
    private String returnInvalidResults(String hotelId) {
        JsonObject jo = new JsonObject();
        jo.addProperty("success", false);
        jo.addProperty("hotelId", "invalid");
        return jo.toString();
    }

    /**
     * Returns a request string.
     *
     * @param host
     * @param pathResourceQuery
     * @return
     */
    private String getRequest(String host, String pathResourceQuery) {
        String request = "GET " + pathResourceQuery + " HTTP/1.1" + System.lineSeparator()
                + "Host: " + host + System.lineSeparator()
                + "Connection: close" + System.lineSeparator()
                + System.lineSeparator();
        return request;
    }


    @Override
    public String toString() {
        return "HotelCollection{" +
                "hotelByHotelId=" + hotelByHotelId +
                '}';
    }
}
