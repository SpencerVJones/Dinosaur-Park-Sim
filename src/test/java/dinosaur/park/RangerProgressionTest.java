package dinosaur.park;

final class RangerProgressionTest {
    private RangerProgressionTest() {
    }

    static void runAll() {
        testDiscoveryTracksUniqueSpecies();
        testProgressionLevelsUp();
        testMissionRewardClaimsOnlyOnce();
    }

    private static void testDiscoveryTracksUniqueSpecies() {
        RangerProgression progression = new RangerProgression();

        progression.recordDiscovery("Allosaurus", 7);
        progression.recordDiscovery("Allosaurus", 7);

        TestAssertions.assertEquals(1, progression.discoveredSpeciesCount(),
                "Duplicate discoveries should not increase unique species count");
        TestAssertions.assertTrue(progression.credits() > 300,
                "Discoveries should grant credits");
    }

    private static void testProgressionLevelsUp() {
        RangerProgression progression = new RangerProgression();

        for (int i = 0; i < 20; i++) {
            progression.recordWarRoomAction("Rescue Specimen", true, 4);
        }

        TestAssertions.assertTrue(progression.level() > 1,
                "Repeated successful operations should level up the player");
        TestAssertions.assertTrue(progression.commandTokens() > 0,
                "Leveling should award command tokens");
    }

    private static void testMissionRewardClaimsOnlyOnce() {
        RangerProgression progression = new RangerProgression();

        for (int i = 0; i < 6; i++) {
            progression.recordEncounter(6);
        }

        int missionCountAfterFirstComplete = progression.completedMissionCount();
        int creditsAfterFirstComplete = progression.credits();

        progression.recordEncounter(6);
        progression.recordEncounter(6);
        progression.recordEncounter(6);

        TestAssertions.assertEquals(missionCountAfterFirstComplete, progression.completedMissionCount(),
                "Mission should only be claimed once after reaching target progress");
        TestAssertions.assertTrue(progression.credits() > creditsAfterFirstComplete,
                "Normal rewards should continue after mission completion");
    }
}
