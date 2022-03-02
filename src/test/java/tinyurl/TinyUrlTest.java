package tinyurl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TinyUrlTest {

    @Test
    void whenAddServer_shouldAddCorrectlyAdd() {
        TinyUrlService tinyUrlService = new TinyUrlService();
        String longUrl = "https://leetcode.com/problems/design-tinyurl";
        String shortUrl = tinyUrlService.encode(longUrl);

        Assertions.assertEquals(longUrl, tinyUrlService.decode(shortUrl));
    }

    @Test
    void whenGetServer_ShouldThrowException_IfNotServersExist() {
        TinyUrlService tinyUrlService = new TinyUrlService();
        String longUrl = "https://leetcode.com/problems/design-tinyurl";
        Assertions.assertThrows(RuntimeException.class, () -> tinyUrlService.decode(longUrl));
    }
}
