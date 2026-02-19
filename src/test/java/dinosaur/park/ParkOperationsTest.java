package dinosaur.park;

import java.util.List;
import java.util.Random;

final class ParkOperationsTest {
    private ParkOperationsTest() {
    }

    static void runAll() {
        testDietClassification();
        testCapacityMessaging();
        testNumericUtilities();
        testRevenueProjection();
        testQuizGeneration();
        testSpeciesComparison();
    }

    private static void testDietClassification() {
        TestAssertions.assertEquals("Carnivore", ParkOperations.classifyDiet("meat"),
                "meat should classify as Carnivore");
        TestAssertions.assertEquals("Herbivore", ParkOperations.classifyDiet("plants"),
                "plants should classify as Herbivore");
        TestAssertions.assertEquals("Omnivore", ParkOperations.classifyDiet("both"),
                "both should classify as Omnivore");
        TestAssertions.expectThrows(IllegalArgumentException.class,
                () -> ParkOperations.classifyDiet("unknown"),
                "Unknown diet should throw IllegalArgumentException");
    }

    private static void testCapacityMessaging() {
        String accepted = ParkOperations.visitorCapacityMessage(32);
        String rejected = ParkOperations.visitorCapacityMessage(121);

        TestAssertions.assertTrue(accepted.contains("Visitor #32"),
                "Accepted capacity message should include visitor number");
        TestAssertions.assertTrue(!accepted.contains("Legacy"),
                "Accepted capacity message should not contain legacy terms");
        TestAssertions.assertTrue(rejected.contains("over safe capacity"),
                "Rejected capacity message should describe overflow");
    }

    private static void testNumericUtilities() {
        TestAssertions.assertApproximately(15.0, ParkOperations.averageWeight(10.0, 20.0), 0.001,
                "averageWeight should compute arithmetic mean");
        TestAssertions.assertEquals(8, ParkOperations.ageDifference(13, 5),
                "ageDifference should return absolute difference");

        ParkOperations.NutritionResult nutrition = ParkOperations.properNutrition(7000, 4);
        TestAssertions.assertEquals(25, nutrition.perFeedingPounds(),
                "Nutrition per feeding should be stable for known inputs");
        TestAssertions.assertEquals(100, nutrition.dailyPounds(),
                "Nutrition daily pounds should be stable for known inputs");
    }

    private static void testRevenueProjection() {
        ParkOperations.RevenueResult result = ParkOperations.revenueProjection(1000, 60.0, 20000.0, 12.0);

        TestAssertions.assertApproximately(60000.0, result.grossRevenue(), 0.001,
                "Gross revenue mismatch");
        TestAssertions.assertApproximately(32000.0, result.totalCosts(), 0.001,
                "Total costs mismatch");
        TestAssertions.assertApproximately(28000.0, result.netRevenue(), 0.001,
                "Net revenue mismatch");
        TestAssertions.assertTrue(result.marginPercent() > 0,
                "Margin should be positive for profitable inputs");
    }

    private static void testQuizGeneration() {
        ParkOperations.QuizQuestion question = ParkOperations.generateQuizQuestion(new Random(42));

        TestAssertions.assertTrue(question.prompt() != null && !question.prompt().isBlank(),
                "Quiz prompt should be populated");
        TestAssertions.assertEquals(4, question.options().size(),
                "Quiz should include exactly 4 options");
        TestAssertions.assertBetweenInclusive(0, 3, question.correctIndex(),
                "Correct answer index should be inside option bounds");
    }

    private static void testSpeciesComparison() {
        List<DinosaurCatalog.DinosaurRecord> dinosaurs = ParkOperations.allDinosaurs();
        TestAssertions.assertTrue(dinosaurs.size() >= 2, "At least two dinosaurs are required for comparison");

        ParkOperations.CompatibilityResult result = ParkOperations.compareSpecies(
                dinosaurs.get(0),
                dinosaurs.get(1)
        );

        TestAssertions.assertBetweenInclusive(0, 100, result.score(),
                "Compatibility score should remain in [0,100]");
        TestAssertions.assertTrue(result.summary().contains("Compatibility"),
                "Compatibility summary should include human-readable label");
    }
}
