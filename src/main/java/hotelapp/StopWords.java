package hotelapp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * StopWord Class
 */
public class StopWords {

    /**
     * The getStopWords function reads all the stop words from the specified file and stores in an arraylist.
     *
     * @param fileName
     * @return ArrayList of stop words.
     */
    public Set<String> getStopWords(String fileName) {
        Set<String> stopWords = new HashSet<>();
        try (FileReader f = new FileReader(fileName)) {
            BufferedReader br = new BufferedReader(f);
            String line;
            while ((line = br.readLine()) != null) {
                stopWords.add(line);
            }
            return Collections.unmodifiableSet(stopWords);
        } catch (Exception e) {
            System.out.println("getStopWords Exception:" + e);
        }
        return null;
    }

}
