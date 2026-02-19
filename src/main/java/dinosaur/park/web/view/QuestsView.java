package dinosaur.park.web.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import dinosaur.park.DinosaurCatalog;
import dinosaur.park.ParkOperations;
import dinosaur.park.RangerProgression;
import dinosaur.park.web.ui.MainLayout;
import dinosaur.park.web.ui.SessionState;

import java.util.Random;

@PageTitle("Ancient Eden | Quick Quests")
@Route(value = "quests", layout = MainLayout.class)
public class QuestsView extends VerticalLayout {
    private final Random random = new Random();
    private final Paragraph prompt = new Paragraph("Press Generate to begin.");
    private final Paragraph playerStatus = new Paragraph();
    private final RadioButtonGroup<String> options = new RadioButtonGroup<>();
    private final TextArea quizOutput = new TextArea("Quiz Feedback");
    private final TextArea encounterLog = new TextArea("Encounter Feed");
    private final TextArea missionProgress = new TextArea("Mission Progress");
    private ParkOperations.QuizQuestion currentQuestion;

    public QuestsView() {
        addClassName("page");
        setSizeFull();
        setPadding(false);
        setSpacing(false);

        HorizontalLayout row = new HorizontalLayout(encounterPanel(), quizPanel(), missionPanel());
        row.setWidthFull();
        row.addClassName("quest-row");
        row.setFlexGrow(1.1, row.getComponentAt(0));
        row.setFlexGrow(1.2, row.getComponentAt(1));
        row.setFlexGrow(1.0, row.getComponentAt(2));

        add(row);
        refreshProgressPanels();
    }

    private Div encounterPanel() {
        Div panel = panel("Encounter Terminal");
        Paragraph feed = new Paragraph("Deploy an encounter to discover a random species event.");
        feed.addClassName("muted-text");

        encounterLog.setReadOnly(true);
        encounterLog.setHeight("360px");
        encounterLog.addClassName("details-text");
        encounterLog.setValue("No encounters launched yet.");

        Button run = new Button("Launch Encounter", event -> {
            DinosaurCatalog.DinosaurRecord dino = ParkOperations.randomDinosaur();
            RangerProgression.ProgressEvent reward = SessionState.progression().recordEncounter(dino.dangerLevel());
            String report = """
                    Encounter: %s
                    Era: %s
                    Region: %s
                    Threat: %d/10
                    Intel: %s

                    Reward:
                    %s
                    """.formatted(
                    dino.commonName(),
                    dino.period(),
                    dino.region(),
                    dino.dangerLevel(),
                    dino.fact(),
                    reward.summary()
            );
            encounterLog.setValue(report);
            SessionState.addSafariEntry("[Encounter] " + dino.commonName() + " in " + dino.region());
            Notification.show(reward.title() + " | +" + reward.xpGained() + " XP");
            refreshProgressPanels();
        });
        run.addClassName("btn-primary");

        panel.add(feed, run, encounterLog);
        return panel;
    }

    private Div quizPanel() {
        Div panel = panel("Quiz Arena");

        prompt.addClassName("quiz-prompt");
        playerStatus.addClassName("muted-text");
        options.setLabel("Select your answer");

        quizOutput.setReadOnly(true);
        quizOutput.setHeight("220px");
        quizOutput.addClassName("details-text");
        quizOutput.setValue("Awaiting question.");

        Button generate = new Button("Generate Question", event -> generateQuestion());
        generate.addClassName("btn-primary");

        Button submit = new Button("Submit Answer", event -> submitAnswer());
        submit.addClassName("btn-ghost");

        HorizontalLayout actions = new HorizontalLayout(generate, submit);
        panel.add(playerStatus, prompt, options, actions, quizOutput);
        return panel;
    }

    private Div missionPanel() {
        Div panel = panel("Mission Board");
        TextArea board = new TextArea("Active Roles");
        board.setReadOnly(true);
        board.setHeight("420px");
        board.addClassName("details-text");

        StringBuilder lines = new StringBuilder();
        for (ParkOperations.StaffMember staff : ParkOperations.staffRoster()) {
            lines.append(staff.summary()).append('\n');
            lines.append(ParkOperations.tasksByPosition(staff.position())).append("\n\n");
        }
        board.setValue(lines.toString().trim());

        missionProgress.setReadOnly(true);
        missionProgress.setHeight("220px");
        missionProgress.addClassName("details-text");

        panel.add(new Paragraph("Crew objectives and role assignments for current operations."), board, missionProgress);
        return panel;
    }

    private void generateQuestion() {
        currentQuestion = ParkOperations.generateQuizQuestion(random);
        prompt.setText(currentQuestion.prompt());
        options.setItems(currentQuestion.options());
        options.clear();
        quizOutput.setValue("Question loaded.");
    }

    private void submitAnswer() {
        if (currentQuestion == null) {
            Notification.show("Generate a question first.");
            return;
        }
        String selected = options.getValue();
        if (selected == null) {
            Notification.show("Pick an answer.");
            return;
        }
        String expected = currentQuestion.options().get(currentQuestion.correctIndex());
        boolean correct = selected.equals(expected);
        RangerProgression.ProgressEvent reward = SessionState.progression().recordQuizAnswer(correct);
        quizOutput.setValue((correct ? "Correct.\n\n" : "Not this time.\n\n")
                + "Answer: " + expected + "\n"
                + "Explanation: " + currentQuestion.explanation() + "\n\n"
                + "Reward: " + reward.summary());
        refreshProgressPanels();
    }

    private Div panel(String title) {
        Div panel = new Div();
        panel.addClassNames("panel-card", "quest-panel");
        panel.add(new H3(title));
        return panel;
    }

    private void refreshProgressPanels() {
        RangerProgression progression = SessionState.progression();
        missionProgress.setValue(progression.missionBoardText());
        playerStatus.setText("Commander: " + progression.rankTitle()
                + " | Level " + progression.level()
                + " | Credits $" + progression.credits()
                + " | Missions completed " + progression.completedMissionCount());
    }
}
