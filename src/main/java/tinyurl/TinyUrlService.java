package tinyurl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class TinyUrlService {

    private static final List<Character> CHARACTERS = new ArrayList<>();
    private static final String PREFIX = "http://tinyurl.com/";
    private static final int NUMBER_OF_ENCODED_SYMBOLS = 6;
    private final Map<String, byte[]> longUrlByShortUrl = new HashMap<>();

    static {
        for (int i = 0; i < 26; i++) {
            CHARACTERS.add((char)('a' + i));
        }

        for (int i = 0; i < 26; i++) {
            CHARACTERS.add((char)('A' + i));
        }

        for (int i = 0; i < 10; i++) {
            CHARACTERS.add((char)('0' + i));
        }

        CHARACTERS.add('.');
        CHARACTERS.add('?');
        CHARACTERS.add('/');
    }


    // Encodes a URL to a shortened URL.
    public String encode(String longUrl) {
        ThreadLocalRandom current = ThreadLocalRandom.current();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < NUMBER_OF_ENCODED_SYMBOLS; i++) {
            sb.append(CHARACTERS.get(current.nextInt(CHARACTERS.size())));
        }

        longUrlByShortUrl.put(sb.toString(), longUrl.getBytes());
        return sb.insert(0, PREFIX).toString();
    }

    // Decodes a shortened URL to its original URL.
    public String decode(String shortUrl) {
        byte[] shortUrlAsBytes = longUrlByShortUrl.get(shortUrl.substring(PREFIX.length()));
        if (shortUrlAsBytes == null || shortUrlAsBytes.length == 0) {
            throw new RuntimeException("Short Url Not Found");
        }
        return new String(shortUrlAsBytes);
    }
}
