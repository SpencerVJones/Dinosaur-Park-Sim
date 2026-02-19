package dinosaur.park;

import java.util.Objects;
import java.util.Random;

public final class CampaignEngine {
    private static final int UPGRADE_HABITAT_MIN_COST = 12_000;
    private static final int UPGRADE_HABITAT_SPREAD = 7_000;
    private static final int FUND_RESEARCH_MIN_COST = 9_000;
    private static final int FUND_RESEARCH_SPREAD = 6_000;
    private static final int RESCUE_SPECIMEN_MIN_COST = 7_500;
    private static final int RESCUE_SPECIMEN_SPREAD = 5_500;

    private final Random random;

    private int day = 1;
    private int budgetUsd = 165_000;
    private int reputation = 42;
    private int welfare = 53;
    private int research = 18;
    private int guestsServed = 0;
    private int habitatUpgradeCost;
    private int researchFundingCost;
    private int rescueSpecimenCost;

    public CampaignEngine() {
        this(new Random());
    }

    CampaignEngine(Random random) {
        this.random = Objects.requireNonNull(random, "random");
        rollActionCosts();
    }

    public CampaignResult runGuidedTour() {
        int guests = 80 + random.nextInt(240);
        int ticketPrice = 52 + random.nextInt(18);
        int gross = guests * ticketPrice;
        int operatingCost = 4_000 + random.nextInt(3_500);
        int reputationGain = 2 + random.nextInt(4);

        budgetUsd += gross - operatingCost;
        guestsServed += guests;
        reputation += reputationGain;
        welfare -= random.nextInt(3);
        research += random.nextInt(2);

        clampStats();
        return new CampaignResult(
                "Guided tour completed",
                "Guests: " + guests + " | Net revenue: $" + (gross - operatingCost) + " | Reputation +" + reputationGain,
                true,
                isGoalReached()
        );
    }

    public CampaignResult upgradeHabitat() {
        int cost = habitatUpgradeCost;
        if (budgetUsd < cost) {
            return new CampaignResult(
                    "Habitat upgrade blocked",
                    "Need at least $" + cost + " budget. Current budget: $" + budgetUsd + ".",
                    false,
                    false
            );
        }

        budgetUsd -= cost;
        welfare += 6 + random.nextInt(9);
        reputation += 1 + random.nextInt(4);
        research += random.nextInt(2);
        clampStats();
        habitatUpgradeCost = rollCost(UPGRADE_HABITAT_MIN_COST, UPGRADE_HABITAT_SPREAD);

        return new CampaignResult(
                "Habitat expanded",
                "Cost: $" + cost + " | Welfare improved and guest confidence increased.",
                true,
                isGoalReached()
        );
    }

    public CampaignResult fundResearch() {
        int cost = researchFundingCost;
        if (budgetUsd < cost) {
            return new CampaignResult(
                    "Research funding denied",
                    "Need at least $" + cost + " budget. Current budget: $" + budgetUsd + ".",
                    false,
                    false
            );
        }

        budgetUsd -= cost;
        research += 5 + random.nextInt(11);
        reputation += 2 + random.nextInt(4);
        welfare += random.nextInt(2);
        clampStats();
        researchFundingCost = rollCost(FUND_RESEARCH_MIN_COST, FUND_RESEARCH_SPREAD);

        return new CampaignResult(
                "Research milestone",
                "Cost: $" + cost + " | New fossil analysis improved science output.",
                true,
                isGoalReached()
        );
    }

    public CampaignResult rescueSpecimen() {
        int cost = rescueSpecimenCost;
        if (budgetUsd < cost) {
            return new CampaignResult(
                    "Rescue mission failed",
                    "Need at least $" + cost + " budget. Current budget: $" + budgetUsd + ".",
                    false,
                    false
            );
        }

        budgetUsd -= cost;
        reputation += 3 + random.nextInt(6);
        welfare += 4 + random.nextInt(5);
        research += 2 + random.nextInt(4);
        clampStats();
        rescueSpecimenCost = rollCost(RESCUE_SPECIMEN_MIN_COST, RESCUE_SPECIMEN_SPREAD);

        return new CampaignResult(
                "Specimen rescue complete",
                "Public sentiment improved after a successful rescue operation.",
                true,
                isGoalReached()
        );
    }

    public CampaignResult nextDay() {
        day++;

        int event = random.nextInt(4);
        if (event == 0) {
            int donation = 8_000 + random.nextInt(7_000);
            budgetUsd += donation;
            reputation += 2;
            clampStats();
            rollActionCosts();
            return new CampaignResult(
                    "Day " + day + " event: sponsor donation",
                    "You received a $" + donation + " donation for conservation outreach.",
                    true,
                    isGoalReached()
            );
        }
        if (event == 1) {
            int emergencyCost = 4_000 + random.nextInt(4_500);
            budgetUsd -= emergencyCost;
            welfare -= 2 + random.nextInt(3);
            clampStats();
            rollActionCosts();
            return new CampaignResult(
                    "Day " + day + " event: storm prep",
                    "Emergency reinforcement cost $" + emergencyCost + ".",
                    true,
                    isGoalReached()
            );
        }
        if (event == 2) {
            research += 3 + random.nextInt(4);
            reputation += 1 + random.nextInt(3);
            clampStats();
            rollActionCosts();
            return new CampaignResult(
                    "Day " + day + " event: new discovery",
                    "Field team recovered new data from a remote dig site.",
                    true,
                    isGoalReached()
            );
        }

        welfare += 1;
        reputation += 1;
        clampStats();
        rollActionCosts();
        return new CampaignResult(
                "Day " + day + " event: steady operations",
                "No incidents today. Team performance remains stable.",
                true,
                isGoalReached()
        );
    }

    public boolean isGoalReached() {
        return reputation >= 85 && welfare >= 80 && research >= 70 && budgetUsd > 0;
    }

    public String goalsSummary() {
        return "Campaign goals: Reputation >= 85 | Welfare >= 80 | Research >= 70 | Positive budget";
    }

    public String statusSummary() {
        return "Day " + day
                + " | Budget $" + budgetUsd
                + " | Reputation " + reputation
                + " | Welfare " + welfare
                + " | Research " + research
                + " | Guests " + guestsServed;
    }

    public int day() {
        return day;
    }

    public int budgetUsd() {
        return budgetUsd;
    }

    public int reputation() {
        return reputation;
    }

    public int welfare() {
        return welfare;
    }

    public int research() {
        return research;
    }

    public int guestsServed() {
        return guestsServed;
    }

    public int habitatUpgradeCost() {
        return habitatUpgradeCost;
    }

    public int researchFundingCost() {
        return researchFundingCost;
    }

    public int rescueSpecimenCost() {
        return rescueSpecimenCost;
    }

    public boolean canAffordHabitatUpgrade() {
        return budgetUsd >= habitatUpgradeCost;
    }

    public boolean canAffordResearchFunding() {
        return budgetUsd >= researchFundingCost;
    }

    public boolean canAffordRescueSpecimen() {
        return budgetUsd >= rescueSpecimenCost;
    }

    private void rollActionCosts() {
        habitatUpgradeCost = rollCost(UPGRADE_HABITAT_MIN_COST, UPGRADE_HABITAT_SPREAD);
        researchFundingCost = rollCost(FUND_RESEARCH_MIN_COST, FUND_RESEARCH_SPREAD);
        rescueSpecimenCost = rollCost(RESCUE_SPECIMEN_MIN_COST, RESCUE_SPECIMEN_SPREAD);
    }

    private int rollCost(int minimum, int spread) {
        return minimum + random.nextInt(spread);
    }

    private void clampStats() {
        budgetUsd = Math.max(-250_000, budgetUsd);
        reputation = clamp01to100(reputation);
        welfare = clamp01to100(welfare);
        research = clamp01to100(research);
        guestsServed = Math.max(0, guestsServed);
    }

    private int clamp01to100(int value) {
        return Math.max(0, Math.min(100, value));
    }

    public record CampaignResult(String title, String detail, boolean success, boolean milestoneReached) {
    }
}
