package dinosaur.park;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public final class RangerProgression {
    private static final int BASE_XP_TARGET = 120;
    private static final int XP_TARGET_STEP = 55;
    private static final int MIN_DANGER = 1;
    private static final int MAX_DANGER = 10;

    private int level = 1;
    private int xp = 0;
    private int credits = 300;
    private int commandTokens = 0;
    private int streak = 0;

    private int encounters = 0;
    private int safariLogs = 0;
    private int quizzesAttempted = 0;
    private int quizzesCorrect = 0;
    private int labCompletions = 0;
    private int warAttempts = 0;
    private int warSuccesses = 0;

    private final Set<String> discoveredSpecies = new LinkedHashSet<>();
    private final Set<String> completedMissions = new LinkedHashSet<>();
    private final Set<String> achievements = new LinkedHashSet<>();

    public ProgressEvent recordDiscovery(String species, int dangerLevel) {
        String safeSpecies = normalizeName(species, "Unknown specimen");
        int danger = clamp(dangerLevel, MIN_DANGER, MAX_DANGER);

        safariLogs++;
        boolean firstDiscovery = discoveredSpecies.add(safeSpecies);
        streak = Math.max(1, streak + 1);

        int xpReward = firstDiscovery ? 24 + (danger * 2) : 8 + danger;
        int creditReward = firstDiscovery ? 42 + (danger * 4) : 10 + (danger * 2);

        EventAccumulator event = new EventAccumulator();
        grant(event, xpReward, creditReward);
        if (firstDiscovery && discoveredSpecies.size() % 5 == 0) {
            commandTokens++;
            event.notes.add("Collection milestone: +1 command token");
        }

        checkAchievements(event);
        claimMissions(event);
        String detail = firstDiscovery
                ? safeSpecies + " added to your codex."
                : "Repeated intel logged for " + safeSpecies + ".";
        return toProgressEvent(firstDiscovery ? "New species logged" : "Species intel updated", detail, event);
    }

    public ProgressEvent recordEncounter(int dangerLevel) {
        int danger = clamp(dangerLevel, MIN_DANGER, MAX_DANGER);
        encounters++;
        streak++;

        int xpReward = 16 + (danger * 2) + Math.min(8, streak);
        int creditReward = 22 + (danger * 3);

        EventAccumulator event = new EventAccumulator();
        grant(event, xpReward, creditReward);
        checkAchievements(event);
        claimMissions(event);
        return toProgressEvent(
                "Encounter resolved",
                "Field team handled a threat level " + danger + " encounter.",
                event
        );
    }

    public ProgressEvent recordQuizAnswer(boolean correct) {
        quizzesAttempted++;
        EventAccumulator event = new EventAccumulator();

        if (correct) {
            quizzesCorrect++;
            streak++;
            int xpReward = 20 + Math.min(12, streak * 2);
            int creditReward = 30 + Math.min(20, streak * 2);
            grant(event, xpReward, creditReward);
            checkAchievements(event);
            claimMissions(event);
            return toProgressEvent("Quiz solved", "Correct answer logged in the archive.", event);
        }

        streak = 0;
        grant(event, 6, 0);
        checkAchievements(event);
        claimMissions(event);
        return toProgressEvent("Quiz miss", "Wrong answer, but the team still learned from it.", event);
    }

    public ProgressEvent recordLabOperation(String operationName, int complexity) {
        String operation = normalizeName(operationName, "Lab simulation");
        int intensity = clamp(complexity, 1, 3);

        labCompletions++;
        streak++;

        int xpReward = 12 + (intensity * 8) + Math.min(6, streak);
        int creditReward = 18 + (intensity * 12);

        EventAccumulator event = new EventAccumulator();
        grant(event, xpReward, creditReward);
        checkAchievements(event);
        claimMissions(event);
        return toProgressEvent(
                operation + " complete",
                "Steward lab pipeline executed at complexity " + intensity + ".",
                event
        );
    }

    public ProgressEvent recordWarRoomAction(String operationName, boolean success, int intensity) {
        String operation = normalizeName(operationName, "War room operation");
        int tier = clamp(intensity, 1, 4);
        warAttempts++;

        EventAccumulator event = new EventAccumulator();
        if (success) {
            warSuccesses++;
            streak++;
            int xpReward = 18 + (tier * 10) + Math.min(10, streak);
            int creditReward = 28 + (tier * 18);
            grant(event, xpReward, creditReward);
            checkAchievements(event);
            claimMissions(event);
            return toProgressEvent(operation + " succeeded", "Command decision executed successfully.", event);
        }

        streak = 0;
        grant(event, 5 + tier, 0);
        checkAchievements(event);
        claimMissions(event);
        return toProgressEvent(operation + " blocked", "Operation failed or was denied by constraints.", event);
    }

    public String rankTitle() {
        if (level >= 9) {
            return "Ancient Eden Legend";
        }
        if (level >= 7) {
            return "Command Marshal";
        }
        if (level >= 5) {
            return "Senior Warden";
        }
        if (level >= 3) {
            return "Field Ranger";
        }
        return "Trail Scout";
    }

    public int level() {
        return level;
    }

    public int xp() {
        return xp;
    }

    public int xpToNextLevel() {
        return BASE_XP_TARGET + ((level - 1) * XP_TARGET_STEP);
    }

    public int credits() {
        return credits;
    }

    public int commandTokens() {
        return commandTokens;
    }

    public int streak() {
        return streak;
    }

    public int discoveredSpeciesCount() {
        return discoveredSpecies.size();
    }

    public int encounters() {
        return encounters;
    }

    public int quizzesAttempted() {
        return quizzesAttempted;
    }

    public int quizzesCorrect() {
        return quizzesCorrect;
    }

    public int labCompletions() {
        return labCompletions;
    }

    public int warAttempts() {
        return warAttempts;
    }

    public int warSuccesses() {
        return warSuccesses;
    }

    public int completedMissionCount() {
        return completedMissions.size();
    }

    public int achievementCount() {
        return achievements.size();
    }

    public List<String> achievementsSnapshot() {
        return Collections.unmodifiableList(new ArrayList<>(achievements));
    }

    public List<MissionStatus> missionStatuses() {
        List<MissionStatus> statuses = new ArrayList<>();
        statuses.add(missionStatus("species-collector", "Bestiary Sweep",
                discoveredSpecies.size(), 8, 220, 70));
        statuses.add(missionStatus("quiz-cadet", "Quiz Cadet",
                quizzesCorrect, 5, 180, 60));
        statuses.add(missionStatus("lab-veteran", "Lab Veteran",
                labCompletions, 6, 200, 65));
        statuses.add(missionStatus("war-strategist", "War Strategist",
                warSuccesses, 4, 280, 85));
        statuses.add(missionStatus("field-patrol", "Field Patrol",
                encounters, 6, 150, 55));
        return Collections.unmodifiableList(statuses);
    }

    public String missionBoardText() {
        StringBuilder text = new StringBuilder();
        for (MissionStatus mission : missionStatuses()) {
            String state;
            if (mission.claimed()) {
                state = "[DONE]";
            } else if (mission.progress() >= mission.target()) {
                state = "[READY]";
            } else {
                state = "[LIVE]";
            }
            text.append(state)
                    .append(' ')
                    .append(mission.title())
                    .append(" ")
                    .append(mission.progress())
                    .append("/")
                    .append(mission.target())
                    .append(" | Reward +$")
                    .append(mission.rewardCredits())
                    .append(" +")
                    .append(mission.rewardXp())
                    .append(" XP")
                    .append('\n');
        }
        text.append('\n')
                .append("Rank: ").append(rankTitle())
                .append(" | Level ").append(level)
                .append(" | Credits $").append(credits)
                .append(" | Tokens ").append(commandTokens);
        return text.toString();
    }

    private void grant(EventAccumulator event, int xpGain, int creditGain) {
        event.xpGained += Math.max(0, xpGain);
        event.creditsGained += Math.max(0, creditGain);

        xp += Math.max(0, xpGain);
        credits += Math.max(0, creditGain);

        while (xp >= xpToNextLevel()) {
            xp -= xpToNextLevel();
            level++;
            commandTokens++;
            int stipend = 140 + (level * 20);
            credits += stipend;
            event.creditsGained += stipend;
            event.levelUps++;
            event.notes.add("Level up to " + level + " (" + rankTitle() + ")");
        }
    }

    private void claimMissions(EventAccumulator event) {
        claimMission(event, "species-collector", "Bestiary Sweep",
                discoveredSpecies.size(), 8, 220, 70);
        claimMission(event, "quiz-cadet", "Quiz Cadet",
                quizzesCorrect, 5, 180, 60);
        claimMission(event, "lab-veteran", "Lab Veteran",
                labCompletions, 6, 200, 65);
        claimMission(event, "war-strategist", "War Strategist",
                warSuccesses, 4, 280, 85);
        claimMission(event, "field-patrol", "Field Patrol",
                encounters, 6, 150, 55);
    }

    private void claimMission(EventAccumulator event,
                              String key,
                              String title,
                              int progress,
                              int target,
                              int rewardCredits,
                              int rewardXp) {
        if (progress < target || !completedMissions.add(key)) {
            return;
        }
        grant(event, rewardXp, rewardCredits);
        event.notes.add("Mission complete: " + title + " (+$" + rewardCredits + ", +" + rewardXp + " XP)");
    }

    private void checkAchievements(EventAccumulator event) {
        unlockAchievement(event, discoveredSpecies.size() >= 1,
                "First Steps", "Logged your first species entry.");
        unlockAchievement(event, discoveredSpecies.size() >= 10,
                "Catalog Scholar", "Documented 10 unique species.");
        unlockAchievement(event, quizzesCorrect >= 10,
                "Quiz Sharpshooter", "Answered 10 quiz prompts correctly.");
        unlockAchievement(event, labCompletions >= 10,
                "Lab Chief", "Completed 10 steward lab simulations.");
        unlockAchievement(event, warSuccesses >= 8,
                "War Chief", "Led 8 successful war room operations.");
    }

    private void unlockAchievement(EventAccumulator event,
                                   boolean condition,
                                   String title,
                                   String detail) {
        if (!condition || !achievements.add(title)) {
            return;
        }
        commandTokens++;
        event.notes.add("Achievement unlocked: " + title + " | " + detail + " (+1 token)");
    }

    private MissionStatus missionStatus(String key,
                                        String title,
                                        int progress,
                                        int target,
                                        int rewardCredits,
                                        int rewardXp) {
        int clampedProgress = Math.min(progress, target);
        boolean claimed = completedMissions.contains(key);
        return new MissionStatus(key, title, clampedProgress, target, rewardCredits, rewardXp, claimed);
    }

    private ProgressEvent toProgressEvent(String title, String detail, EventAccumulator event) {
        StringBuilder summary = new StringBuilder();
        summary.append(detail)
                .append(" Reward +")
                .append(event.xpGained)
                .append(" XP, +$")
                .append(event.creditsGained)
                .append('.');

        if (!event.notes.isEmpty()) {
            summary.append(" ").append(String.join(" | ", event.notes));
        }

        summary.append(" Current: L")
                .append(level)
                .append(" ")
                .append(rankTitle())
                .append(", $")
                .append(credits)
                .append(", XP ")
                .append(xp)
                .append("/")
                .append(xpToNextLevel())
                .append('.');

        return new ProgressEvent(title, summary.toString(), event.xpGained, event.creditsGained, event.levelUps, level, credits);
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private String normalizeName(String value, String fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        return value.trim();
    }

    private static final class EventAccumulator {
        private int xpGained;
        private int creditsGained;
        private int levelUps;
        private final List<String> notes = new ArrayList<>();
    }

    public record ProgressEvent(String title,
                                String summary,
                                int xpGained,
                                int creditsGained,
                                int levelUps,
                                int level,
                                int credits) {
        public ProgressEvent {
            Objects.requireNonNull(title, "title");
            Objects.requireNonNull(summary, "summary");
        }
    }

    public record MissionStatus(String key,
                                String title,
                                int progress,
                                int target,
                                int rewardCredits,
                                int rewardXp,
                                boolean claimed) {
        public MissionStatus {
            Objects.requireNonNull(key, "key");
            Objects.requireNonNull(title, "title");
        }
    }
}
