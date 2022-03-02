package shortener;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;
import static shortener.UrlShortenerImpl.PREFIX;

public class ShortenerTest {

    final String URL = "http://gmail.com/my/sent/12345";
    UrlShortener urlShortener;

    @BeforeEach
    void before() {
        urlShortener = new UrlShortenerImpl(10);
    }

    @Test
    void encode_shouldEncodesFullUrlToShortUrl() {
        String shortUrl = urlShortener.encode(URL);
        assertTrue(shortUrl.startsWith(PREFIX));
        assertTrue(shortUrl.length() > PREFIX.length());
    }

    @Test
    void encode_shouldEncodesFullUrlToShortUrl_whenRegisterSameUrls() {
        assertEquals(urlShortener.encode(URL), urlShortener.encode(URL));
    }

    @Test
    void decode_shouldDecodesShortUrlToFullUrl() {
        String encodedUrl = urlShortener.encode(URL);
        String decodedUrl = urlShortener.decode(encodedUrl);
        assertEquals(URL, decodedUrl);
    }

    @Test
    void decode_shouldDecodesShortUrlToFullUrl_doMultiConcurrentRequestsCase() throws InterruptedException {
        List<String> fullUrls = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            fullUrls.add(URL + i);
        }

        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        fullUrls.stream()
                .map(fullUrl -> CompletableFuture.supplyAsync(() -> new AbstractMap.SimpleEntry<>(
                        fullUrl,
                        urlShortener.encode(fullUrl)), executorService))
                .map(CompletableFuture::join)
                .forEach(entry -> assertEquals(urlShortener.decode(entry.getValue()), entry.getKey()));
    }

    @Test
    void decode_shouldThrowsException_ifUrlNotRegisteredYet() {
        assertThrows(ShortUrlNotFoundException.class, () -> urlShortener.decode(PREFIX + 1));
    }

    @Test
    void encode_shouldThrowsException_ifUrlInvalid() {
        assertThrows(InvalidUrlException.class, () -> urlShortener.encode(null));
    }

    @Test
    void decode_shouldThrowsException_ifUrlInvalid() {
        assertThrows(InvalidUrlException.class, () -> urlShortener.decode(null));
    }
}
