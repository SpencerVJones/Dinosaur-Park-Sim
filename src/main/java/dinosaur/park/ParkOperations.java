package dinosaur.park;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public final class ParkOperations {
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("EEE, MMM dd, yyyy", Locale.US);
    private static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern("hh:mm:ss a", Locale.US);
    private static final Random RANDOM = new Random();

    private static final DinosaurCatalog DATABASE = DinosaurCatalog.loadDefault();
    private static final Map<String, String> POSITION_TASKS = buildPositionTasks();

    private ParkOperations() {
    }

    public static String currentDate() {
        return LocalDate.now().format(DATE_FORMAT);
    }

    public static String currentTime() {
        return LocalTime.now().format(TIME_FORMAT);
    }

    public static String currentDateTime() {
        return currentDate() + " | " + currentTime();
    }

    public static int randomVisitorCount() {
        return RANDOM.nextInt(125) + 1;
    }

    public static String visitorCapacityMessage(int visitorNumber) {
        if (visitorNumber <= 100) {
            return "Visitor #" + visitorNumber + " admitted. Capacity remains available.";
        }
        return "Park is over safe capacity (" + visitorNumber + "/100). Hold entry and reroute guests.";
    }

    public static List<DinosaurCatalog.DinosaurRecord> allDinosaurs() {
        return DATABASE.all();
    }

    public static List<String> allPeriods() {
        return DATABASE.periods();
    }

    public static List<String> allDiets() {
        return DATABASE.diets();
    }

    public static List<DinosaurCatalog.DinosaurRecord> filterDinosaurs(String period, String diet, String search) {
        return DATABASE.filter(period, diet, search);
    }

    public static DinosaurCatalog.DinosaurRecord randomDinosaur() {
        List<DinosaurCatalog.DinosaurRecord> dinosaurs = gameplayPool();
        if (dinosaurs.isEmpty()) {
            throw new IllegalStateException("No dinosaurs found in database.");
        }
        return dinosaurs.get(RANDOM.nextInt(dinosaurs.size()));
    }

    public static String speciesFacts(String species) {
        if (species == null || species.isBlank()) {
            throw new IllegalArgumentException("Species is required.");
        }
        return DATABASE.findByCommonName(species)
                .map(DinosaurCatalog.DinosaurRecord::fact)
                .orElseThrow(() -> new IllegalArgumentException("No facts found for " + species + "."));
    }

    public static List<String> supportedSpecies() {
        return DATABASE.all().stream()
                .map(DinosaurCatalog.DinosaurRecord::commonName)
                .toList();
    }

    public static double averageWeight(double weightOne, double weightTwo) {
        if (weightOne <= 0 || weightTwo <= 0) {
            throw new IllegalArgumentException("Weights must be greater than zero.");
        }
        return (weightOne + weightTwo) / 2.0;
    }

    public static NutritionResult properNutrition(int weight, int feedingsPerDay) {
        if (weight <= 0 || feedingsPerDay <= 0) {
            throw new IllegalArgumentException("Weight and feedings must be greater than zero.");
        }
        int dailyFood = Math.max(1, weight / 70);
        int perFeeding = Math.max(1, dailyFood / feedingsPerDay);
        return new NutritionResult(perFeeding, dailyFood);
    }

    public static int ageDifference(int firstAge, int secondAge) {
        if (firstAge < 0 || secondAge < 0) {
            throw new IllegalArgumentException("Ages cannot be negative.");
        }
        return Math.abs(firstAge - secondAge);
    }

    public static String classifyDiet(String input) {
        if (input == null) {
            throw new IllegalArgumentException("Diet input is required.");
        }
        String value = input.trim().toLowerCase(Locale.US);
        return switch (value) {
            case "meat", "carnivore" -> "Carnivore";
            case "plants", "herbivore" -> "Herbivore";
            case "both", "omnivore" -> "Omnivore";
            default -> throw new IllegalArgumentException("Use meat, plants, or both.");
        };
    }

    public static String dinosaursByExperience(int years) {
        if (years < 0) {
            throw new IllegalArgumentException("Years cannot be negative.");
        }
        if (years <= 1) {
            return "Observer access only. Pair with a senior handler.";
        }
        if (years <= 4) {
            return "Can supervise low-risk herbivores and juvenile enclosures.";
        }
        if (years <= 7) {
            return "Can supervise mixed-risk habitats with approved backup.";
        }
        return "Full access clearance across all habitats.";
    }

    public static String tasksByPosition(String position) {
        String tasks = POSITION_TASKS.get(position);
        if (tasks == null) {
            throw new IllegalArgumentException("Unknown position: " + position);
        }
        return tasks;
    }

    public static String habitatSize(String species) {
        DinosaurCatalog.DinosaurRecord dinosaur = DATABASE.findByCommonName(species)
                .orElseThrow(() -> new IllegalArgumentException("We do not currently have that species."));

        if (dinosaur.lengthMeters() < 4) {
            return "Small enclosure";
        }
        if (dinosaur.lengthMeters() < 10) {
            return "Medium enclosure";
        }
        if (dinosaur.lengthMeters() < 18) {
            return "Large enclosure";
        }
        return "Mega enclosure";
    }

    public static double habitatAreaSqMeters(DinosaurCatalog.DinosaurRecord dinosaur, int population) {
        if (population <= 0) {
            throw new IllegalArgumentException("Population must be greater than zero.");
        }
        double dangerMultiplier = 1.0 + (dinosaur.dangerLevel() * 0.08);
        return dinosaur.lengthMeters() * dinosaur.lengthMeters() * 42.0 * population * dangerMultiplier;
    }

    public static double dailyFoodKg(DinosaurCatalog.DinosaurRecord dinosaur, int population) {
        if (population <= 0) {
            throw new IllegalArgumentException("Population must be greater than zero.");
        }
        double baseRate = dinosaur.diet().equalsIgnoreCase("Carnivore") ? 0.06 : 0.035;
        return dinosaur.massKg() * baseRate * population;
    }

    public static RevenueResult revenueProjection(int visitors, double ticketPrice, double fixedCosts, double variableCostPerVisitor) {
        if (visitors < 0 || ticketPrice < 0 || fixedCosts < 0 || variableCostPerVisitor < 0) {
            throw new IllegalArgumentException("Revenue inputs cannot be negative.");
        }

        double gross = visitors * ticketPrice;
        double costs = fixedCosts + (visitors * variableCostPerVisitor);
        double net = gross - costs;
        double margin = gross == 0 ? 0 : (net / gross) * 100.0;
        return new RevenueResult(gross, costs, net, margin);
    }

    public static CompatibilityResult compareSpecies(DinosaurCatalog.DinosaurRecord first,
                                                     DinosaurCatalog.DinosaurRecord second) {
        double lengthGap = Math.abs(first.lengthMeters() - second.lengthMeters());
        double eraGap = Math.abs(first.timeframeMya() - second.timeframeMya());
        boolean sharedDiet = first.diet().equalsIgnoreCase(second.diet());

        int score = 100;
        score -= Math.min(45, (int) Math.round(lengthGap * 2.2));
        score -= Math.min(35, (int) Math.round(eraGap * 0.25));
        if (!sharedDiet) {
            score -= 18;
        }
        score = Math.max(0, score);

        String tier;
        if (score >= 75) {
            tier = "High";
        } else if (score >= 50) {
            tier = "Moderate";
        } else if (score >= 30) {
            tier = "Low";
        } else {
            tier = "Critical";
        }

        String summary = "Compatibility " + tier + " (" + score + "/100). "
                + "Length gap: " + format(lengthGap) + " m, "
                + "time gap: " + format(eraGap) + " million years, "
                + "diet alignment: " + (sharedDiet ? "matched" : "conflict") + ".";

        return new CompatibilityResult(score, tier, summary);
    }

    public static QuizQuestion generateQuizQuestion(Random random) {
        List<DinosaurCatalog.DinosaurRecord> pool = gameplayPool();
        if (pool.size() < 4) {
            throw new IllegalStateException("Need at least 4 dinosaurs in the database for quiz mode.");
        }

        DinosaurCatalog.DinosaurRecord dinosaur = pool.get(random.nextInt(pool.size()));
        int type = random.nextInt(3);

        return switch (type) {
            case 0 -> questionForPeriod(dinosaur, pool, random);
            case 1 -> questionForDiet(dinosaur, pool, random);
            default -> questionForRegion(dinosaur, pool, random);
        };
    }

    public static List<StaffMember> staffRoster() {
        return List.of(
                new StaffMember("Spencer", 24, "Zookeeper", 10),
                new StaffMember("Brooke", 22, "Veterinarian", 8),
                new StaffMember("Landon", 26, "Dinosaur Caretaker", 6),
                new StaffMember("Jessie", 20, "Tour Guide", 3),
                new StaffMember("Chloe", 19, "Guest Service Agent", 2),
                new StaffMember("Kyle", 18, "Intern", 1)
        );
    }

    private static QuizQuestion questionForPeriod(DinosaurCatalog.DinosaurRecord dinosaur,
                                                  List<DinosaurCatalog.DinosaurRecord> pool,
                                                  Random random) {
        String prompt = "Which period did " + dinosaur.commonName() + " live in?";
        return buildQuestion(prompt, dinosaur.period(),
                pool.stream().map(DinosaurCatalog.DinosaurRecord::period).distinct().toList(),
                "It lived around " + format(dinosaur.timeframeMya()) + " million years ago.",
                random);
    }

    private static QuizQuestion questionForDiet(DinosaurCatalog.DinosaurRecord dinosaur,
                                                List<DinosaurCatalog.DinosaurRecord> pool,
                                                Random random) {
        String prompt = "What diet best matches " + dinosaur.commonName() + "?";
        return buildQuestion(prompt, dinosaur.diet(),
                pool.stream().map(DinosaurCatalog.DinosaurRecord::diet).distinct().toList(),
                dinosaur.commonName() + " is classified as a " + dinosaur.diet() + ".",
                random);
    }

    private static QuizQuestion questionForRegion(DinosaurCatalog.DinosaurRecord dinosaur,
                                                  List<DinosaurCatalog.DinosaurRecord> pool,
                                                  Random random) {
        String prompt = "Where were notable fossils of " + dinosaur.commonName() + " found?";
        return buildQuestion(prompt, dinosaur.region(),
                pool.stream().map(DinosaurCatalog.DinosaurRecord::region).distinct().toList(),
                "Primary fossil finds were reported from " + dinosaur.region() + ".",
                random);
    }

    private static QuizQuestion buildQuestion(String prompt,
                                              String correctAnswer,
                                              List<String> candidates,
                                              String explanation,
                                              Random random) {
        List<String> options = new ArrayList<>();
        options.add(correctAnswer);

        List<String> distractors = new ArrayList<>(candidates);
        distractors.removeIf(item -> item.equals(correctAnswer));
        while (options.size() < 4 && !distractors.isEmpty()) {
            int index = random.nextInt(distractors.size());
            options.add(distractors.remove(index));
        }

        while (options.size() < 4) {
            options.add("Unknown");
        }

        java.util.Collections.shuffle(options, random);
        int correctIndex = options.indexOf(correctAnswer);
        return new QuizQuestion(prompt, options, correctIndex, explanation);
    }

    private static Map<String, String> buildPositionTasks() {
        Map<String, String> tasks = new LinkedHashMap<>();
        tasks.put("Intern", "- Cleaning\n- Assisting mentors\n- Preparing supplies");
        tasks.put("Tour Guide", "- Lead guided tours\n- Explain exhibits\n- Manage visitor flow");
        tasks.put("Zookeeper", "- Plan diets\n- Feed dinosaurs\n- Monitor behavior");
        tasks.put("Veterinarian", "- Health checks\n- Treatment plans\n- Emergency response");
        tasks.put("Dinosaur Caretaker", "- Habitat prep\n- Enrichment sessions\n- Daily inspections");
        tasks.put("Guest Service Agent", "- Ticketing\n- Wayfinding support\n- Guest recovery");
        return tasks;
    }

    private static String format(double value) {
        return String.format(Locale.US, "%.1f", value);
    }

    private static List<DinosaurCatalog.DinosaurRecord> gameplayPool() {
        List<DinosaurCatalog.DinosaurRecord> curated = DATABASE.curated();
        if (curated.size() >= 4) {
            return curated;
        }
        return DATABASE.all();
    }

    public record NutritionResult(int perFeedingPounds, int dailyPounds) {
    }

    public record RevenueResult(double grossRevenue, double totalCosts, double netRevenue, double marginPercent) {
    }

    public record CompatibilityResult(int score, String tier, String summary) {
    }

    public record QuizQuestion(String prompt, List<String> options, int correctIndex, String explanation) {
    }

    public record StaffMember(String name, int age, String position, int yearsOfExperience) {
        public String summary() {
            return name + " (" + position + ") - age " + age + ", experience " + yearsOfExperience + " years";
        }
    }
}
