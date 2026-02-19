package dinosaur.park.web.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import dinosaur.park.CampaignEngine;
import dinosaur.park.RangerProgression;
import dinosaur.park.web.ui.MainLayout;
import dinosaur.park.web.ui.SessionState;

import java.util.Locale;

@PageTitle("Ancient Eden | War Room")
@Route(value = "war-room", layout = MainLayout.class)
public class WarRoomView extends VerticalLayout {
    private final CampaignEngine engine = SessionState.campaign();
    private final Paragraph status = new Paragraph();
    private final Paragraph progressionStatus = new Paragraph();
    private final TextArea budgetTracker = new TextArea("Budget Tracker");
    private final TextArea eventLog = new TextArea("Campaign Log");
    private final Button guidedTourButton;
    private final Button upgradeHabitatButton;
    private final Button fundResearchButton;
    private final Button rescueSpecimenButton;
    private final Button advanceDayButton;

    public WarRoomView() {
        addClassName("page");
        setSizeFull();
        setPadding(false);
        setSpacing(false);

        Div header = new Div();
        header.addClassNames("panel-card", "section-header");
        header.setText("War Room: manage budget, welfare, research, and reputation across daily operations.");

        Div controls = new Div();
        controls.addClassNames("panel-card", "war-controls");
        controls.add(new H3("Operations"));

        guidedTourButton = actionButton("Guided Tour", 1, engine::runGuidedTour, true);
        upgradeHabitatButton = actionButton("Upgrade Habitat", 2, engine::upgradeHabitat, false);
        fundResearchButton = actionButton("Fund Research", 2, engine::fundResearch, false);
        rescueSpecimenButton = actionButton("Rescue Specimen", 3, engine::rescueSpecimen, false);
        advanceDayButton = actionButton("Advance Day", 1, engine::nextDay, false);

        HorizontalLayout row = new HorizontalLayout(guidedTourButton, upgradeHabitatButton, fundResearchButton,
                rescueSpecimenButton, advanceDayButton);
        row.setWidthFull();
        row.addClassName("tool-row");
        controls.add(row);

        status.addClassName("campaign-status");
        progressionStatus.addClassName("muted-text");
        budgetTracker.setReadOnly(true);
        budgetTracker.setHeight("260px");
        budgetTracker.addClassName("details-text");

        Div budgetPanel = new Div();
        budgetPanel.addClassNames("panel-card", "war-feed");
        budgetPanel.add(new H3("War Chest"), budgetTracker);

        eventLog.setReadOnly(true);
        eventLog.setHeight("340px");
        eventLog.addClassName("details-text");
        eventLog.setValue("Campaign initialized.\n" + engine.goalsSummary());

        Div feed = new Div(status, progressionStatus, eventLog);
        feed.addClassNames("panel-card", "war-feed");

        HorizontalLayout workspace = new HorizontalLayout(budgetPanel, feed);
        workspace.setWidthFull();
        workspace.addClassName("workspace");
        workspace.setFlexGrow(1.0, budgetPanel);
        workspace.setFlexGrow(1.4, feed);

        add(header, controls, workspace);
        refreshStatus();
    }

    private Button actionButton(String label, int intensity, Action action, boolean primary) {
        Button button = new Button(label, event -> runAction(label, intensity, action));
        button.addClassName(primary ? "btn-primary" : "btn-ghost");
        return button;
    }

    private void runAction(String label, int intensity, Action action) {
        CampaignEngine.CampaignResult result = action.run();
        RangerProgression.ProgressEvent reward =
                SessionState.progression().recordWarRoomAction(label, result.success(), intensity);
        String line = (result.success() ? "[SUCCESS] " : "[BLOCKED] ")
                + result.title() + " :: " + result.detail()
                + " || " + reward.summary();

        eventLog.setValue(line + "\n\n" + eventLog.getValue());
        refreshStatus();

        if (result.milestoneReached()) {
            Notification.show("Campaign milestone reached.");
            return;
        }
        Notification.show(reward.title() + " | +" + reward.xpGained() + " XP");
    }

    private void refreshStatus() {
        status.setText(engine.statusSummary());
        RangerProgression progression = SessionState.progression();
        progressionStatus.setText("Commander: " + progression.rankTitle()
                + " | Level " + progression.level()
                + " | Credits $" + progression.credits()
                + " | Tokens " + progression.commandTokens());
        budgetTracker.setValue(buildBudgetSnapshot());

        int habitatCost = engine.habitatUpgradeCost();
        int researchCost = engine.researchFundingCost();
        int rescueCost = engine.rescueSpecimenCost();

        upgradeHabitatButton.setText("Upgrade Habitat (" + formatUsd(habitatCost) + ")");
        fundResearchButton.setText("Fund Research (" + formatUsd(researchCost) + ")");
        rescueSpecimenButton.setText("Rescue Specimen (" + formatUsd(rescueCost) + ")");

        upgradeHabitatButton.setEnabled(engine.canAffordHabitatUpgrade());
        fundResearchButton.setEnabled(engine.canAffordResearchFunding());
        rescueSpecimenButton.setEnabled(engine.canAffordRescueSpecimen());
    }

    @FunctionalInterface
    private interface Action {
        CampaignEngine.CampaignResult run();
    }

    private String buildBudgetSnapshot() {
        int budget = engine.budgetUsd();
        int habitatCost = engine.habitatUpgradeCost();
        int researchCost = engine.researchFundingCost();
        int rescueCost = engine.rescueSpecimenCost();

        return """
                Current budget: %s

                Cost checks:
                - Upgrade habitat: %s (%s)
                - Fund research: %s (%s)
                - Rescue specimen: %s (%s)

                Costs refresh after each successful operation and each new day.
                """
                .formatted(
                        formatUsd(budget),
                        formatUsd(habitatCost),
                        affordabilityLabel(budget, habitatCost),
                        formatUsd(researchCost),
                        affordabilityLabel(budget, researchCost),
                        formatUsd(rescueCost),
                        affordabilityLabel(budget, rescueCost)
                ).trim();
    }

    private String affordabilityLabel(int budget, int cost) {
        if (budget >= cost) {
            return "ready";
        }
        return "short by " + formatUsd(cost - budget);
    }

    private String formatUsd(int value) {
        return "$" + String.format(Locale.US, "%,d", value);
    }
}
