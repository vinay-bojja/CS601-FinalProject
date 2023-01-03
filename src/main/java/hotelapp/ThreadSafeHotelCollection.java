package hotelapp;

import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * ThreadSafeHotelCollection is a thread safe class of HotelCollection.
 */
public class ThreadSafeHotelCollection extends HotelCollection {
    private ReentrantReadWriteLock lock;

    public ThreadSafeHotelCollection() {
        lock = new ReentrantReadWriteLock();
    }

    /**
     * Build findHotel hashmap.
     *
     * @param hotels
     */
    @Override
    public void createFindHotelHashMap(List<Hotel> hotels) {
        try {
            lock.writeLock().lock();
            super.createFindHotelHashMap(hotels);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Build HotelsByKeyword map.
     *
     * @param hotels
     */
    @Override
    public void buildHotelsByKeywordMap(List<Hotel> hotels) {
        try {
            lock.writeLock().lock();
            super.buildHotelsByKeywordMap(hotels);
        } finally {
            lock.writeLock().unlock();
        }

    }

    /**
     * On passing a word asa parameter, the function will return a set of hotel.
     *
     * @param keyword
     * @return
     */
    @Override
    public Set<Hotel> getHotelsByKeyword(String keyword) {
        try {
            lock.readLock().lock();
            return super.getHotelsByKeyword(keyword);
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Method return Hotel object of a particular hotelID.
     *
     * @param hotelId
     * @return
     */
    @Override
    public Hotel getHotel(String hotelId) {
        try {
            lock.readLock().lock();
            return super.getHotel(hotelId);
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Create and return hotel json.
     *
     * @param hotel
     * @return
     */
    @Override
    public String getHotelInfoJson(Hotel hotel) {
        try {
            lock.readLock().lock();
            return super.getHotelInfoJson(hotel);
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Returns all the hotelIds in the hashMap.
     *
     * @return
     */
    @Override
    public Set<String> getAllHotelIds() {
        try {
            lock.readLock().lock();
            return super.getAllHotelIds();
        } finally {
            lock.readLock().unlock();
        }

    }

    /**
     * The function returns json containing weather details if the co-ordinates of hotel are valid otherwise it returns json containing invalid result.
     *
     * @param hotel
     * @param hotelId
     * @return
     */
    @Override
    public String getWeatherJson(Hotel hotel, String hotelId) {
        try {
            lock.readLock().lock();
            return super.getWeatherJson(hotel, hotelId);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
