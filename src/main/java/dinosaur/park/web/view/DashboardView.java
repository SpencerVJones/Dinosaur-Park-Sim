package dinosaur.park.web.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Pre;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import dinosaur.park.DinosaurCatalog;
import dinosaur.park.ParkOperations;
import dinosaur.park.RangerProgression;
import dinosaur.park.web.ui.MainLayout;
import dinosaur.park.web.ui.SessionState;

import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.Random;

@PageTitle("Ancient Eden | Dashboard")
@Route(value = "", layout = MainLayout.class)
public class DashboardView extends VerticalLayout {
    private static final Random RANDOM = new Random();

    public DashboardView() {
        addClassName("page");
        setSizeFull();
        setPadding(false);
        setSpacing(false);

        add(buildHero());
        add(buildStatsRow());
        add(buildMissionRow());
        add(buildProgressionRow());
        add(buildQuestKeyPanel());
    }

    private Div buildHero() {
        Div hero = new Div();
        hero.addClassName("hero");

        H2 title = new H2("Ancient Eden");
        title.addClassName("hero-title");

        Paragraph subtitle = new Paragraph("Ranger command center for species intel, stewardship planning, and live campaign ops.");
        subtitle.addClassName("hero-subtitle");

        hero.add(title, subtitle);
        return hero;
    }

    private HorizontalLayout buildStatsRow() {
        List<DinosaurCatalog.DinosaurRecord> dinosaurs = ParkOperations.allDinosaurs();
        double avgLength = dinosaurs.stream().mapToDouble(DinosaurCatalog.DinosaurRecord::lengthMeters).average().orElse(0.0);
        double maxMassTons = dinosaurs.stream().mapToDouble(DinosaurCatalog.DinosaurRecord::massKg).max().orElse(0.0) / 1000.0;
        double avgDanger = dinosaurs.stream().mapToInt(DinosaurCatalog.DinosaurRecord::dangerLevel).average().orElse(0.0);

        HorizontalLayout row = new HorizontalLayout(
                metricCard("Species", String.valueOf(dinosaurs.size()), "Real paleontology dataset", "tone-blue"),
                metricCard("Avg Length", String.format(Locale.US, "%.1f m", avgLength), "Bestiary-wide body scale", "tone-teal"),
                metricCard("Heaviest", String.format(Locale.US, "%.1f tons", maxMassTons), "Prime titan benchmark", "tone-amber"),
                metricCard("Avg Danger", String.format(Locale.US, "%.1f/10", avgDanger), "Encounter threat baseline", "tone-rose")
        );
        row.setWidthFull();
        row.addClassName("stats-row");
        return row;
    }

    private Div metricCard(String label, String value, String detail, String toneClass) {
        Div card = new Div();
        card.addClassNames("metric-card", toneClass);

        Paragraph title = new Paragraph(label);
        title.addClassName("metric-label");

        H3 metric = new H3(value);
        metric.addClassName("metric-value");

        Paragraph caption = new Paragraph(detail);
        caption.addClassName("metric-caption");

        card.add(title, metric, caption);
        return card;
    }

    private HorizontalLayout buildMissionRow() {
        HorizontalLayout row = new HorizontalLayout();
        row.setWidthFull();
        row.addClassName("mission-row");

        Div briefing = new Div();
        briefing.addClassNames("panel-card", "briefing-card");
        briefing.add(new H3("Ranger Briefing"));
        Pre briefingText = new Pre("""
                Ranger Briefing:
                - Bestiary records are loaded from local curated dinosaur datasets.
                - Species intel and habitat data stream from the command archive.
                - Quest systems include random encounters, quiz trials, and mission tracks.
                - Steward tools model feeding plans, enclosure area, and revenue pressure.
                """);
        briefingText.addClassName("briefing-text");
        briefing.add(briefingText);

        Div actions = new Div();
        actions.addClassNames("panel-card", "actions-card");
        actions.add(new H3("Quick Quests"));
        actions.add(quickNavButton("Launch Encounter", ExplorerView.class, true));
        actions.add(quickNavButton("Open Quiz Arena", QuestsView.class, false));
        actions.add(quickNavButton("Open Steward Lab", LabView.class, false));
        actions.add(quickNavButton("Open War Room", WarRoomView.class, false));

        row.add(briefing, actions);
        row.setFlexGrow(2, briefing);
        row.setFlexGrow(1, actions);
        return row;
    }

    private Button quickNavButton(String label, Class<? extends Component> target, boolean primary) {
        Button button = new Button(label, event -> UI.getCurrent().navigate(target));
        button.addClassName(primary ? "btn-primary" : "btn-ghost");
        button.setWidthFull();
        return button;
    }

    private Div buildQuestKeyPanel() {
        Div panel = new Div();
        panel.addClassNames("panel-card", "quest-key-panel");

        H3 title = new H3("Quest Key");
        title.addClassName("quest-key-title");
        panel.add(title);

        String key = generateQuestKey();
        Paragraph keyValue = new Paragraph(key);
        keyValue.addClassName("quest-key-value");
        panel.add(keyValue);
        return panel;
    }

    private HorizontalLayout buildProgressionRow() {
        RangerProgression progression = SessionState.progression();

        Div commandPanel = new Div();
        commandPanel.addClassNames("panel-card", "briefing-card");
        commandPanel.add(new H3("Commander Progression"));
        commandPanel.add(new Paragraph("Rank: " + progression.rankTitle()));
        commandPanel.add(new Paragraph("Level " + progression.level() + " | XP "
                + progression.xp() + "/" + progression.xpToNextLevel()));
        commandPanel.add(new Paragraph("Credits: $" + progression.credits()
                + " | Tokens: " + progression.commandTokens()
                + " | Streak: " + progression.streak()));

        TextArea missions = new TextArea("Active Missions");
        missions.setReadOnly(true);
        missions.setHeight("240px");
        missions.addClassName("details-text");
        missions.setValue(progression.missionBoardText());
        commandPanel.add(missions);

        Div achievementsPanel = new Div();
        achievementsPanel.addClassNames("panel-card", "actions-card");
        achievementsPanel.add(new H3("Achievements"));
        TextArea achievements = new TextArea("Unlocked");
        achievements.setReadOnly(true);
        achievements.setHeight("240px");
        achievements.addClassName("details-text");
        achievements.setValue(formatAchievements(progression));
        achievementsPanel.add(achievements);

        HorizontalLayout row = new HorizontalLayout(commandPanel, achievementsPanel);
        row.setWidthFull();
        row.addClassName("mission-row");
        row.setFlexGrow(1.6, commandPanel);
        row.setFlexGrow(1.0, achievementsPanel);
        return row;
    }

    private String formatAchievements(RangerProgression progression) {
        List<String> list = progression.achievementsSnapshot();
        if (list.isEmpty()) {
            return "No achievements unlocked yet.\n\n"
                    + "Start by logging species, solving quiz prompts, and running lab operations.";
        }
        StringBuilder text = new StringBuilder();
        for (String achievement : list) {
            text.append("- ").append(achievement).append('\n');
        }
        text.append('\n')
                .append("Total unlocked: ").append(progression.achievementCount())
                .append(" | Missions completed: ").append(progression.completedMissionCount());
        return text.toString().trim();
    }

    private String generateQuestKey() {
        byte[] bytes = new byte[18];
        RANDOM.nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes).replace('+', 'x');
    }
}
