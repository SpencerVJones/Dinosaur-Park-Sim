package dinosaur.park;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public final class DinosaurCatalog {
    private static final String DETAILED_RESOURCE_PATH = "/dinosaur/park/data/real_dinosaurs.psv";
    private static final String CATALOG_RESOURCE_PATH = "/dinosaur/park/data/dino_catalog_names.txt";

    private static final List<Path> DETAILED_FALLBACK_PATHS = List.of(
            Path.of("src/main/resources/dinosaur/park/data/real_dinosaurs.psv"),
            Path.of("resources/dinosaur/park/data/real_dinosaurs.psv"),
            Path.of("dinosaur/park/data/real_dinosaurs.psv")
    );

    private static final List<Path> CATALOG_FALLBACK_PATHS = List.of(
            Path.of("src/main/resources/dinosaur/park/data/dino_catalog_names.txt"),
            Path.of("resources/dinosaur/park/data/dino_catalog_names.txt"),
            Path.of("dinosaur/park/data/dino_catalog_names.txt")
    );

    private static final String[] PERIOD_BUCKETS = {
            "Early Jurassic",
            "Middle Jurassic",
            "Late Jurassic",
            "Early Cretaceous",
            "Late Cretaceous",
            "Late Triassic"
    };

    private static final double[] PERIOD_AGE_MYA = {196, 170, 152, 120, 72, 215};
    private static final String[] REGIONS = {
            "North America",
            "South America",
            "Europe",
            "Asia",
            "Africa",
            "Mongolia",
            "China",
            "Antarctica",
            "Australia"
    };

    private static volatile DinosaurCatalog cached;

    private final List<DinosaurRecord> dinosaurs;

    private DinosaurCatalog(List<DinosaurRecord> dinosaurs) {
        this.dinosaurs = List.copyOf(dinosaurs);
    }

    public static DinosaurCatalog loadDefault() {
        DinosaurCatalog local = cached;
        if (local != null) {
            return local;
        }

        synchronized (DinosaurCatalog.class) {
            local = cached;
            if (local == null) {
                List<DinosaurRecord> merged = readDetailedDatabaseFile();
                appendCatalogEntries(merged);
                local = new DinosaurCatalog(merged);
                cached = local;
            }
        }
        return local;
    }

    public List<DinosaurRecord> all() {
        return dinosaurs;
    }

    public List<DinosaurRecord> curated() {
        return dinosaurs.stream()
                .filter(d -> "Curated Dataset".equals(d.profileSource()))
                .toList();
    }

    public Optional<DinosaurRecord> findByCommonName(String commonName) {
        if (commonName == null) {
            return Optional.empty();
        }
        String lookup = normalize(commonName);
        return dinosaurs.stream()
                .filter(d -> normalize(d.commonName()).equals(lookup))
                .findFirst();
    }

    public List<String> periods() {
        Set<String> periods = new LinkedHashSet<>();
        for (DinosaurRecord dinosaur : dinosaurs) {
            periods.add(dinosaur.period());
        }
        return List.copyOf(periods);
    }

    public List<String> diets() {
        Set<String> diets = new LinkedHashSet<>();
        for (DinosaurRecord dinosaur : dinosaurs) {
            diets.add(dinosaur.diet());
        }
        return List.copyOf(diets);
    }

    public List<DinosaurRecord> filter(String period, String diet, String searchTerm) {
        String normalizedSearch = searchTerm == null ? "" : searchTerm.trim().toLowerCase(Locale.US);
        return dinosaurs.stream()
                .filter(d -> period == null || period.isBlank() || "All".equals(period) || d.period().equals(period))
                .filter(d -> diet == null || diet.isBlank() || "All".equals(diet) || d.diet().equals(diet))
                .filter(d -> normalizedSearch.isBlank()
                        || d.commonName().toLowerCase(Locale.US).contains(normalizedSearch)
                        || d.scientificName().toLowerCase(Locale.US).contains(normalizedSearch)
                        || d.region().toLowerCase(Locale.US).contains(normalizedSearch))
                .sorted(Comparator.comparing(DinosaurRecord::commonName))
                .toList();
    }

    private static List<DinosaurRecord> readDetailedDatabaseFile() {
        List<DinosaurRecord> dinosaurs = new ArrayList<>();
        try (BufferedReader reader = openReader(DETAILED_RESOURCE_PATH, DETAILED_FALLBACK_PATHS)) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (lineNumber == 1 && line.startsWith("common_name|")) {
                    continue;
                }
                if (line.isBlank() || line.startsWith("#")) {
                    continue;
                }

                String[] parts = line.split("\\|", -1);
                if (parts.length != 14) {
                    throw new IllegalStateException("Invalid dinosaur database row at line " + lineNumber + ": " + line);
                }

                dinosaurs.add(new DinosaurRecord(
                        parts[0].trim(),
                        parts[1].trim(),
                        parts[2].trim(),
                        parseDouble(parts[3], "timeframe_mya", lineNumber),
                        parts[4].trim(),
                        parseDouble(parts[5], "length_m", lineNumber),
                        parseDouble(parts[6], "mass_kg", lineNumber),
                        parseDouble(parts[7], "top_speed_kph", lineNumber),
                        parts[8].trim(),
                        parts[9].trim(),
                        parts[10].trim(),
                        parseInt(parts[11], "sound_hz", lineNumber),
                        parseInt(parts[12], "danger_level", lineNumber),
                        parts[13].trim(),
                        "Curated Dataset"
                ));
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to load dinosaur database.", ex);
        }
        return dinosaurs;
    }

    private static void appendCatalogEntries(List<DinosaurRecord> dinosaurs) {
        Map<String, DinosaurRecord> byName = new LinkedHashMap<>();
        for (DinosaurRecord dinosaur : dinosaurs) {
            byName.put(normalize(dinosaur.commonName()), dinosaur);
        }

        List<String> names = readCatalogNames();
        int imported = 0;
        for (String rawName : names) {
            String cleaned = cleanCatalogName(rawName);
            if (cleaned.isBlank()) {
                continue;
            }

            String key = normalize(cleaned);
            if (byName.containsKey(key)) {
                continue;
            }

            DinosaurRecord estimated = estimateProfile(cleaned, imported);
            dinosaurs.add(estimated);
            byName.put(key, estimated);
            imported++;
        }
    }

    private static List<String> readCatalogNames() {
        List<String> names = new ArrayList<>();
        try (BufferedReader reader = openReader(CATALOG_RESOURCE_PATH, CATALOG_FALLBACK_PATHS)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank() || line.startsWith("#")) {
                    continue;
                }
                names.add(line.trim());
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to load dinosaur catalog names.", ex);
        }
        return names;
    }

    private static String cleanCatalogName(String name) {
        String cleaned = name == null ? "" : name.trim();
        cleaned = cleaned.replaceAll("\\s*\\(.*?\\)", "");
        cleaned = cleaned.replace("_", " ");
        return cleaned;
    }

    private static DinosaurRecord estimateProfile(String commonName, int seedIndex) {
        int hash = Math.abs((commonName.toLowerCase(Locale.US) + seedIndex).hashCode());

        int periodIndex = hash % PERIOD_BUCKETS.length;
        String period = PERIOD_BUCKETS[periodIndex];
        double timeframe = PERIOD_AGE_MYA[periodIndex] + (((hash / 11) % 20) - 10) * 0.7;
        timeframe = clamp(timeframe, 66, 235);

        String diet = inferDiet(commonName, hash);
        double length = estimateLengthMeters(commonName, diet, hash);
        double mass = estimateMassKg(length, diet, hash);
        double speed = estimateSpeedKph(length, diet, hash);
        int soundHz = estimateSoundHz(mass, hash);
        int danger = estimateDangerLevel(length, diet, commonName);

        String region = REGIONS[hash % REGIONS.length];
        String fact = "Imported from the Wikipedia dinosaur genera catalog. Profile values are estimated for gameplay scale.";

        return new DinosaurRecord(
                commonName,
                commonName,
                period,
                timeframe,
                diet,
                length,
                mass,
                speed,
                region,
                "Catalog Import",
                commonName,
                soundHz,
                danger,
                fact,
                "Catalog Estimate"
        );
    }

    private static String inferDiet(String commonName, int hash) {
        String lower = commonName.toLowerCase(Locale.US);
        if (lower.contains("raptor") || lower.contains("venator") || lower.contains("dromeus")
                || lower.contains("draco") || lower.contains("onychus") || lower.contains("tor")) {
            return "Carnivore";
        }
        if (lower.contains("ceratops") || lower.contains("hadro") || lower.contains("iguan")
                || lower.contains("ankylo") || lower.contains("pelta") || lower.contains("saurus")) {
            return (hash % 10 < 2) ? "Omnivore" : "Herbivore";
        }
        int roll = hash % 10;
        if (roll < 5) {
            return "Herbivore";
        }
        if (roll < 9) {
            return "Carnivore";
        }
        return "Omnivore";
    }

    private static double estimateLengthMeters(String name, String diet, int hash) {
        double length;
        if ("Carnivore".equals(diet)) {
            length = 3.0 + (hash % 120) / 10.0;
        } else if ("Herbivore".equals(diet)) {
            length = 2.8 + (hash % 240) / 10.0;
        } else {
            length = 2.0 + (hash % 90) / 10.0;
        }

        String lower = name.toLowerCase(Locale.US);
        if (lower.contains("micro") || lower.contains("nano")) {
            length *= 0.4;
        }
        if (lower.contains("mega") || lower.contains("giga") || lower.contains("titan")) {
            length *= 1.35;
        }

        return clamp(length, 0.4, 32.0);
    }

    private static double estimateMassKg(double length, String diet, int hash) {
        double factor;
        if ("Carnivore".equals(diet)) {
            factor = 10.5;
        } else if ("Herbivore".equals(diet)) {
            factor = 16.2;
        } else {
            factor = 8.4;
        }
        double mass = Math.pow(length, 3) * factor + (hash % 900);
        return clamp(mass, 1.0, 90000.0);
    }

    private static double estimateSpeedKph(double length, String diet, int hash) {
        double speed;
        if ("Carnivore".equals(diet)) {
            speed = 20 + (hash % 42);
        } else if ("Herbivore".equals(diet)) {
            speed = 12 + (hash % 28);
        } else {
            speed = 15 + (hash % 33);
        }

        if (length > 20) {
            speed -= 9;
        } else if (length > 12) {
            speed -= 4;
        }

        return clamp(speed, 6.0, 70.0);
    }

    private static int estimateSoundHz(double mass, int hash) {
        double freq = 620 - Math.sqrt(mass) * 1.7 + ((hash % 50) - 25);
        return (int) Math.round(clamp(freq, 70.0, 650.0));
    }

    private static int estimateDangerLevel(double length, String diet, String name) {
        double score;
        if ("Carnivore".equals(diet)) {
            score = 3.8 + length * 0.34;
        } else if ("Herbivore".equals(diet)) {
            score = 1.8 + length * 0.22;
        } else {
            score = 2.6 + length * 0.28;
        }

        String lower = name.toLowerCase(Locale.US);
        if (lower.contains("raptor") || lower.contains("giga") || lower.contains("rex")) {
            score += 1.2;
        }

        return (int) Math.round(clamp(score, 1.0, 10.0));
    }

    private static BufferedReader openReader(String resourcePath, List<Path> fallbackPaths) throws IOException {
        InputStream resourceStream = DinosaurCatalog.class.getResourceAsStream(resourcePath);
        if (resourceStream != null) {
            return new BufferedReader(new InputStreamReader(resourceStream, StandardCharsets.UTF_8));
        }

        for (Path path : fallbackPaths) {
            if (Files.exists(path)) {
                return Files.newBufferedReader(path, StandardCharsets.UTF_8);
            }
        }

        throw new IOException("Could not locate data file for path: " + resourcePath);
    }

    private static String normalize(String value) {
        return value.trim().toLowerCase(Locale.US);
    }

    private static double parseDouble(String value, String field, int lineNumber) {
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException ex) {
            throw new IllegalStateException("Invalid " + field + " at line " + lineNumber + ": " + value);
        }
    }

    private static int parseInt(String value, String field, int lineNumber) {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException ex) {
            throw new IllegalStateException("Invalid " + field + " at line " + lineNumber + ": " + value);
        }
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    public record DinosaurRecord(
            String commonName,
            String scientificName,
            String period,
            double timeframeMya,
            String diet,
            double lengthMeters,
            double massKg,
            double topSpeedKph,
            String region,
            String firstDescribed,
            String wikiTitle,
            int soundHz,
            int dangerLevel,
            String fact,
            String profileSource
    ) {
        @Override
        public String toString() {
            return commonName + " (" + scientificName + ")";
        }
    }
}
