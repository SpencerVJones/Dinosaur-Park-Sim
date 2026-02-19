package dinosaur.park;

public final class TestRunner {
    private TestRunner() {
    }

    public static void main(String[] args) {
        int passed = 0;
        int total = 0;

        total++;
        passed += run("DinosaurCatalogTest", DinosaurCatalogTest::runAll);

        total++;
        passed += run("ParkOperationsTest", ParkOperationsTest::runAll);

        total++;
        passed += run("CampaignEngineTest", CampaignEngineTest::runAll);

        total++;
        passed += run("RangerProgressionTest", RangerProgressionTest::runAll);

        System.out.println();
        System.out.println("Test summary: " + passed + "/" + total + " test groups passed.");
        if (passed != total) {
            System.exit(1);
        }
    }

    private static int run(String name, Runnable tests) {
        try {
            tests.run();
            System.out.println("[PASS] " + name);
            return 1;
        } catch (Throwable t) {
            System.out.println("[FAIL] " + name);
            t.printStackTrace(System.out);
            return 0;
        }
    }
}
