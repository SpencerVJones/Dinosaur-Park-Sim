package dinosaur.park;

import java.util.List;

final class DinosaurCatalogTest {
    private DinosaurCatalogTest() {
    }

    static void runAll() {
        testDataLoads();
        testLookups();
        testFiltering();
    }

    private static void testDataLoads() {
        DinosaurCatalog catalog = DinosaurCatalog.loadDefault();

        TestAssertions.assertTrue(!catalog.all().isEmpty(),
                "Catalog should load at least one dinosaur");
        TestAssertions.assertTrue(!catalog.curated().isEmpty(),
                "Catalog should include curated entries");
        TestAssertions.assertTrue(catalog.all().size() >= catalog.curated().size(),
                "Total catalog should be larger than or equal to curated subset");
        TestAssertions.assertTrue(catalog.periods().size() >= 3,
                "Catalog should expose multiple geological periods");
        TestAssertions.assertTrue(catalog.diets().size() >= 2,
                "Catalog should expose multiple diet categories");
    }

    private static void testLookups() {
        DinosaurCatalog catalog = DinosaurCatalog.loadDefault();
        DinosaurCatalog.DinosaurRecord tyrannosaurus = catalog.findByCommonName("Tyrannosaurus")
                .orElseThrow(() -> new AssertionError("Expected Tyrannosaurus record in catalog"));

        TestAssertions.assertEquals("Carnivore", tyrannosaurus.diet(),
                "Tyrannosaurus should be classified as Carnivore");
        TestAssertions.assertTrue(tyrannosaurus.lengthMeters() > 10.0,
                "Tyrannosaurus length should be larger than 10m");
    }

    private static void testFiltering() {
        DinosaurCatalog catalog = DinosaurCatalog.loadDefault();

        List<DinosaurCatalog.DinosaurRecord> lateCretaceousCarnivores = catalog.filter(
                "Late Cretaceous",
                "Carnivore",
                "tyranno"
        );

        TestAssertions.assertTrue(!lateCretaceousCarnivores.isEmpty(),
                "Expected at least one Late Cretaceous carnivore filtered by tyranno");
        TestAssertions.assertTrue(lateCretaceousCarnivores.stream()
                        .anyMatch(d -> d.commonName().equals("Tyrannosaurus")),
                "Filter result should include Tyrannosaurus");

        List<DinosaurCatalog.DinosaurRecord> regionSearch = catalog.filter("All", "All", "mongolia");
        TestAssertions.assertTrue(regionSearch.stream()
                        .anyMatch(d -> d.region().toLowerCase().contains("mongolia")),
                "Region search should include Mongolian records");
    }
}
