package shortener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class UrlShortenerImpl implements UrlShortener {

    public static final String PREFIX = "tinyurl.com/";

    private final List<String> fullUrls = new ArrayList<>();

    /**
     * Cache
     * key: fullUrl
     * value: shortUrl
     */
    private final Map<String, String> cache = new HashMap<>();
    private final int cacheCapacity;

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    public UrlShortenerImpl(int cacheCapacity) {
        this.cacheCapacity = cacheCapacity;
    }

    @Override
    public String encode(String fullUrl) {
        if (isNull(fullUrl) || fullUrl.isBlank()) {
            throw new InvalidUrlException();
        }

        readWriteLock.writeLock().lock();

        try {
            String urlInCache = findUrlInCache(fullUrl);
            if (nonNull(urlInCache)) {
                return urlInCache;
            }
            fullUrls.add(fullUrl);
            String shortUrl = PREFIX + (fullUrls.size() - 1);
            if (cache.size() >= cacheCapacity) {
                cache.clear();
            }
            cache.put(fullUrl, shortUrl);
            return shortUrl;
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public String decode(String shortUrl) {
        if (isNull(shortUrl) || shortUrl.isBlank()) {
            throw new InvalidUrlException();
        }

        int index = Integer.parseInt(shortUrl.replace(PREFIX, ""));

        readWriteLock.readLock().lock();

        try {
            if (index > (fullUrls.size() - 1)) {
                throw new ShortUrlNotFoundException();
            }
            return fullUrls.get(index);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    private String findUrlInCache(String fullUrl) {
        return cache.get(fullUrl);
    }
}
