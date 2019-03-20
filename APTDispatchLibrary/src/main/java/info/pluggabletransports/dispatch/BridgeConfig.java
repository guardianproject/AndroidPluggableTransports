package info.pluggabletransports.dispatch;

import android.content.Intent;
import android.net.Uri;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

/**
 * Parse Pluggable Transport Bridge configuration from {@code torrc Bridge} lines
 * or Bridge URLs.
 *
 * @see <a href="https://www.torproject.org/docs/tor-manual-dev.html.en#Bridge><tt>Bridge</tt> config option</a>
 * @see <a href="https://www.torproject.org/docs/bridges">Tor: Bridges</a>
 * @see <a href="https://trac.torproject.org/projects/tor/ticket/15035">URI format for bridges</a>
 */
public class BridgeConfig {

    public static String toBridgeLine(Intent intent) throws URISyntaxException {
        return toBridgeLine(intent.getDataString());
    }

    public static String toBridgeLine(Uri uri) throws URISyntaxException {
        return toBridgeLine(uri.toString());
    }

    public static String toBridgeLine(String urlString) throws URISyntaxException {
        return toBridgeLine(new URI(urlString));
    }

    public static String toBridgeLine(URL url) throws URISyntaxException {
        return toBridgeLine(url.toURI());
    }

    public static String toBridgeLine(URI uri) throws URISyntaxException {
        StringBuilder builder = new StringBuilder("Bridge ");
        Map<String, String> query = splitQuery(uri);
        if ("http".equals(uri.getScheme()) || "https".equals(uri.getScheme())) {
            String transport = query.get("transport");
            if (transport != null && transport.length() > 0) {
                builder.append(transport);
                builder.append(' ');
            }
            builder.append(query.get("ip"));
            builder.append(':');
            builder.append(query.get("orport"));
            query.remove("transport");
            query.remove("ip");
            query.remove("orport");
        } else if ("bridge".equals(uri.getScheme())) {
            String transport = uri.getUserInfo();
            if (transport != null && transport.length() > 0) {
                builder.append(transport);
                builder.append(' ');
            }
            builder.append(uri.getHost());
            builder.append(':');
            builder.append(uri.getPort());
        } else {
            throw new URISyntaxException(uri.toString(), "Unsupported bridge URI scheme!");
        }
        builder.append(' ');
        builder.append(uri.getPath().substring(1));
        for (Entry<String, String> entry : query.entrySet()) {
            String key = entry.getKey();
            if (key != null) {
                builder.append(' ');
                builder.append(key);
                String value = entry.getValue();
                if (key.length() > 0 && value != null && value.length() > 0) {
                    builder.append('=');
                    builder.append(value);
                }
            }
        }
        return builder.toString().trim();
    }

    public static URI toBridgeURI(String bridgeLine) throws IllegalArgumentException, URISyntaxException {
        if (bridgeLine.length() < 48) {
            throw new IllegalArgumentException("Bridge lines must have an IP address and a fingerprint.");
        }
        String[] segments = bridgeLine.split(" ");
        if (segments.length < 2) {
            throw new IllegalArgumentException("Bridge lines must have at least 2 space-separated elements.");
        }
        int i = 0;
        if ("bridge".equalsIgnoreCase(segments[0])) {
            i++;
        }
        StringBuilder builder = new StringBuilder();
        builder.append("bridge://");
        if (Pattern.matches("[a-z0-9_]+", segments[i])) {
            builder.append(segments[i]);
            i++;
            builder.append('@');
        }
        builder.append(segments[i]);
        i++;
        builder.append('/');
        builder.append(segments[i]);
        i++;
        int start = i;
        for (; i < segments.length; i++) {
            if (i == start) {
                builder.append('?');
            } else {
                builder.append('&');
            }
            String[] keyValuePair = segments[i].split("=", 2);
            builder.append(keyValuePair[0]);
            builder.append('=');
            builder.append(urlEncode(keyValuePair[1]));
        }
        return new URI(builder.toString().trim());
    }

    public static Map<String, String> splitQuery(URI uri) {
        final Map<String, String> query_pairs = new LinkedHashMap<>();
        String query = uri.getQuery();
        if (query == null || query.length() == 0) {
            return Collections.emptyMap();
        }
        for (String unsplit : uri.getQuery().split("&")) {
            String[] pair = unsplit.split("=", 2);
            if (pair.length > 1) {
                query_pairs.put(pair[0], pair[1]);
            } else if (pair.length == 1) {
                query_pairs.put(pair[0], null);
            }
        }
        return query_pairs;
    }

    private static String urlEncode(String s) throws URISyntaxException {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new URISyntaxException(s, "UTF-8 encoding on this device is broken");
        }
    }
}
