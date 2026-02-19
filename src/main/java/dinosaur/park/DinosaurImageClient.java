package dinosaur.park;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.SwingWorker;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class DinosaurImageClient {
    private static final Pattern URL_FIELD_PATTERN = Pattern.compile("\\\"(?:source|thumburl|url)\\\":\\\"(https?:[^\\\"]+)\\\"");

    private final HttpClient client = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(8))
            .build();

    private final Map<String, ImageIcon> cache = new ConcurrentHashMap<>();

    public void loadImageAsync(String wikiTitle, int width, Consumer<ImageIcon> callback) {
        if (wikiTitle == null || wikiTitle.isBlank()) {
            callback.accept(createPlaceholder("No image", width));
            return;
        }

        String key = wikiTitle + ":" + width;
        ImageIcon cachedIcon = cache.get(key);
        if (cachedIcon != null) {
            callback.accept(cachedIcon);
            return;
        }

        callback.accept(createLoadingPlaceholder(wikiTitle, width));

        new SwingWorker<ImageIcon, Void>() {
            @Override
            protected ImageIcon doInBackground() {
                ImageIcon icon = fetchWikipediaImage(wikiTitle, width);
                if (icon == null) {
                    icon = createPlaceholder(wikiTitle, width);
                }
                cache.put(key, icon);
                return icon;
            }

            @Override
            protected void done() {
                try {
                    callback.accept(get());
                } catch (Exception ignored) {
                    callback.accept(createPlaceholder(wikiTitle, width));
                }
            }
        }.execute();
    }

    private ImageIcon fetchWikipediaImage(String wikiTitle, int width) {
        String encodedTitle = URLEncoder.encode(wikiTitle, StandardCharsets.UTF_8);

        String pageImageUrl = "https://en.wikipedia.org/w/api.php?action=query&format=json"
                + "&prop=pageimages&piprop=thumbnail|original&pithumbsize=" + Math.max(620, width)
                + "&redirects=1&titles=" + encodedTitle;
        String imageUrl = fetchImageUrlFromApi(pageImageUrl);

        if (imageUrl == null) {
            String summaryUrl = "https://en.wikipedia.org/api/rest_v1/page/summary/" + encodedTitle;
            imageUrl = fetchImageUrlFromApi(summaryUrl);
        }

        if (imageUrl == null) {
            String commonsSearchUrl = "https://commons.wikimedia.org/w/api.php?action=query&format=json"
                    + "&generator=search&gsrlimit=1&gsrsearch="
                    + URLEncoder.encode(wikiTitle + " dinosaur", StandardCharsets.UTF_8)
                    + "&prop=imageinfo&iiprop=url&iiurlwidth=" + Math.max(620, width);
            imageUrl = fetchImageUrlFromApi(commonsSearchUrl);
        }

        if (imageUrl == null) {
            return null;
        }

        return downloadAndScaleImage(imageUrl, width);
    }

    private String fetchImageUrlFromApi(String endpoint) {
        try {
            HttpRequest request = HttpRequest.newBuilder(URI.create(endpoint))
                    .timeout(Duration.ofSeconds(10))
                    .header("User-Agent", "AncientEden-DinoPark/1.1")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                return null;
            }

            return extractFirstImageUrl(response.body());
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            return null;
        } catch (Exception ignored) {
            return null;
        }
    }

    private ImageIcon downloadAndScaleImage(String imageUrl, int width) {
        try {
            HttpRequest imageRequest = HttpRequest.newBuilder(URI.create(imageUrl))
                    .timeout(Duration.ofSeconds(10))
                    .header("User-Agent", "AncientEden-DinoPark/1.1")
                    .GET()
                    .build();

            HttpResponse<byte[]> imageResponse = client.send(imageRequest, HttpResponse.BodyHandlers.ofByteArray());
            if (imageResponse.statusCode() != 200) {
                return null;
            }

            BufferedImage source = ImageIO.read(new ByteArrayInputStream(imageResponse.body()));
            if (source == null) {
                return null;
            }

            int targetWidth = Math.max(260, width);
            int targetHeight = Math.max(180, (int) (targetWidth * 0.58));
            BufferedImage scaled = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = scaled.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            double scale = Math.max((double) targetWidth / source.getWidth(), (double) targetHeight / source.getHeight());
            int drawWidth = (int) Math.round(source.getWidth() * scale);
            int drawHeight = (int) Math.round(source.getHeight() * scale);
            int x = (targetWidth - drawWidth) / 2;
            int y = (targetHeight - drawHeight) / 2;
            g2.drawImage(source, x, y, drawWidth, drawHeight, null);
            g2.dispose();

            return new ImageIcon(scaled);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            return null;
        } catch (IOException ex) {
            return null;
        }
    }

    private String extractFirstImageUrl(String json) {
        if (json == null || json.isBlank()) {
            return null;
        }

        Matcher matcher = URL_FIELD_PATTERN.matcher(json);
        while (matcher.find()) {
            String url = matcher.group(1).replace("\\/", "/");
            if (url.contains("upload.wikimedia.org") || url.contains("wikimedia.org")) {
                return url;
            }
        }
        return null;
    }

    private ImageIcon createLoadingPlaceholder(String text, int width) {
        int w = Math.max(260, width);
        int h = Math.max(180, (int) (w * 0.58));
        BufferedImage placeholder = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = placeholder.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(new Color(230, 238, 244));
        g2.fillRoundRect(0, 0, w, h, 22, 22);
        g2.setColor(new Color(45, 78, 99));
        g2.drawRoundRect(1, 1, w - 3, h - 3, 22, 22);

        g2.setFont(new Font("SansSerif", Font.BOLD, 16));
        g2.drawString("Loading image...", 22, h / 2 - 2);
        g2.setFont(new Font("SansSerif", Font.PLAIN, 13));
        String clipped = text.length() > 36 ? text.substring(0, 36) + "..." : text;
        g2.drawString(clipped, 22, h / 2 + 20);
        g2.dispose();

        return new ImageIcon(placeholder);
    }

    private ImageIcon createPlaceholder(String text, int width) {
        int w = Math.max(260, width);
        int h = Math.max(180, (int) (w * 0.58));
        BufferedImage placeholder = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = placeholder.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(new Color(239, 244, 248));
        g2.fillRoundRect(0, 0, w, h, 22, 22);
        g2.setColor(new Color(58, 87, 107));
        g2.drawRoundRect(1, 1, w - 3, h - 3, 22, 22);

        g2.setFont(new Font("SansSerif", Font.BOLD, 16));
        g2.drawString("Image unavailable", 22, h / 2 - 2);
        g2.setFont(new Font("SansSerif", Font.PLAIN, 13));
        String clipped = text.length() > 36 ? text.substring(0, 36) + "..." : text;
        g2.drawString(clipped, 22, h / 2 + 20);
        g2.dispose();

        return new ImageIcon(placeholder);
    }
}
