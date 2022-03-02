package shortener;

public interface UrlShortener {

    String encode(String fullUrl);

    String decode(String shortUrl);
}
