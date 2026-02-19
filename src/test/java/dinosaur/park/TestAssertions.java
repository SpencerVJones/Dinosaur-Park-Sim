package dinosaur.park;

final class TestAssertions {
    private TestAssertions() {
    }

    static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    static void assertEquals(Object expected, Object actual, String message) {
        if (expected == null ? actual != null : !expected.equals(actual)) {
            throw new AssertionError(message + " | expected=" + expected + " actual=" + actual);
        }
    }

    static void assertApproximately(double expected, double actual, double epsilon, String message) {
        if (Math.abs(expected - actual) > epsilon) {
            throw new AssertionError(message + " | expected=" + expected + " actual=" + actual);
        }
    }

    static void assertBetweenInclusive(int min, int max, int actual, String message) {
        if (actual < min || actual > max) {
            throw new AssertionError(message + " | expected between " + min + " and " + max + " but was " + actual);
        }
    }

    static void expectThrows(Class<? extends Throwable> expectedType, Runnable action, String message) {
        try {
            action.run();
        } catch (Throwable t) {
            if (expectedType.isInstance(t)) {
                return;
            }
            throw new AssertionError(message + " | expected " + expectedType.getSimpleName()
                    + " but got " + t.getClass().getSimpleName(), t);
        }
        throw new AssertionError(message + " | expected " + expectedType.getSimpleName() + " but nothing was thrown.");
    }
}
