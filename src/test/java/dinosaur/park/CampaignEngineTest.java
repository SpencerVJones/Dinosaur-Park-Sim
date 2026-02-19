package dinosaur.park;

import java.util.Random;

final class CampaignEngineTest {
    private CampaignEngineTest() {
    }

    static void runAll() {
        testStateBoundsAcrossSimulation();
        testDailyAdvanceChangesDay();
        testAffordabilityFlagsTrackBudget();
    }

    private static void testStateBoundsAcrossSimulation() {
        CampaignEngine engine = new CampaignEngine(new Random(7));

        for (int i = 0; i < 30; i++) {
            CampaignEngine.CampaignResult tour = engine.runGuidedTour();
            CampaignEngine.CampaignResult habitat = engine.upgradeHabitat();
            CampaignEngine.CampaignResult research = engine.fundResearch();
            CampaignEngine.CampaignResult rescue = engine.rescueSpecimen();

            verifyResultShape(tour);
            verifyResultShape(habitat);
            verifyResultShape(research);
            verifyResultShape(rescue);

            TestAssertions.assertBetweenInclusive(0, 100, engine.reputation(),
                    "Reputation should remain clamped");
            TestAssertions.assertBetweenInclusive(0, 100, engine.welfare(),
                    "Welfare should remain clamped");
            TestAssertions.assertBetweenInclusive(0, 100, engine.research(),
                    "Research should remain clamped");
            TestAssertions.assertTrue(engine.budgetUsd() >= -250_000,
                    "Budget should remain clamped to lower bound");
            TestAssertions.assertTrue(engine.guestsServed() >= 0,
                    "Guests served should never be negative");
        }
    }

    private static void testDailyAdvanceChangesDay() {
        CampaignEngine engine = new CampaignEngine(new Random(17));
        int startDay = engine.day();

        CampaignEngine.CampaignResult result = engine.nextDay();

        TestAssertions.assertEquals(startDay + 1, engine.day(),
                "nextDay should increment day counter by one");
        verifyResultShape(result);
    }

    private static void testAffordabilityFlagsTrackBudget() {
        CampaignEngine engine = new CampaignEngine(new Random(29));

        for (int i = 0; i < 35; i++) {
            TestAssertions.assertEquals(engine.budgetUsd() >= engine.habitatUpgradeCost(),
                    engine.canAffordHabitatUpgrade(),
                    "Habitat affordability flag should reflect current budget and action cost");
            TestAssertions.assertEquals(engine.budgetUsd() >= engine.researchFundingCost(),
                    engine.canAffordResearchFunding(),
                    "Research affordability flag should reflect current budget and action cost");
            TestAssertions.assertEquals(engine.budgetUsd() >= engine.rescueSpecimenCost(),
                    engine.canAffordRescueSpecimen(),
                    "Rescue affordability flag should reflect current budget and action cost");

            engine.runGuidedTour();
            engine.upgradeHabitat();
            engine.fundResearch();
            engine.rescueSpecimen();
            engine.nextDay();
        }
    }

    private static void verifyResultShape(CampaignEngine.CampaignResult result) {
        TestAssertions.assertTrue(result.title() != null && !result.title().isBlank(),
                "Campaign result title should be populated");
        TestAssertions.assertTrue(result.detail() != null && !result.detail().isBlank(),
                "Campaign result detail should be populated");
    }
}
