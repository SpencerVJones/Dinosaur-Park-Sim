package dinosaur.park.web.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import dinosaur.park.DinosaurCatalog;
import dinosaur.park.RangerProgression;
import dinosaur.park.DinosaurSoundSynthesizer;
import dinosaur.park.ParkOperations;
import dinosaur.park.web.ui.MainLayout;
import dinosaur.park.web.ui.SessionState;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@PageTitle("Ancient Eden | Explorer")
@Route(value = "explorer", layout = MainLayout.class)
public class ExplorerView extends VerticalLayout {
    private final Grid<DinosaurCatalog.DinosaurRecord> grid = new Grid<>(DinosaurCatalog.DinosaurRecord.class, false);
    private final H3 detailTitle = new H3("Select a species");
    private final Paragraph detailSubtitle = new Paragraph("Choose a dinosaur record to inspect profile details.");
    private final TextArea detailArea = new TextArea();
    private final TextArea safariLog = new TextArea("Safari Log");
    private final Button wikiButton = new Button("Open Wikipedia");
    private final Button soundButton = new Button("Play Dinosaur Call");
    private DinosaurCatalog.DinosaurRecord selected;

    public ExplorerView() {
        addClassName("page");
        setSizeFull();
        setPadding(false);
        setSpacing(false);

        List<DinosaurCatalog.DinosaurRecord> allDinosaurs = ParkOperations.allDinosaurs();

        TextField search = new TextField("Search");
        search.setPlaceholder("Name, scientific name, region...");

        Select<String> period = new Select<>();
        period.setLabel("Period");
        period.setItems(withAll(ParkOperations.allPeriods()));
        period.setValue("All");

        Select<String> diet = new Select<>();
        diet.setLabel("Diet");
        diet.setItems(withAll(ParkOperations.allDiets()));
        diet.setValue("All");

        Button apply = new Button("Apply Filters", event -> grid.setItems(
                ParkOperations.filterDinosaurs(period.getValue(), diet.getValue(), search.getValue())));
        apply.addClassName("btn-primary");

        Button clear = new Button("Clear", event -> {
            search.clear();
            period.setValue("All");
            diet.setValue("All");
            grid.setItems(allDinosaurs);
        });
        clear.addClassName("btn-ghost");

        HorizontalLayout filters = new HorizontalLayout(search, period, diet, apply, clear);
        filters.setWidthFull();
        filters.setAlignItems(Alignment.END);
        filters.addClassName("filter-row");

        configureGrid(allDinosaurs);
        configureDetailPanel();
        refreshSafariLog();

        HorizontalLayout workspace = new HorizontalLayout(buildGridPanel(), buildDetailPanel());
        workspace.setWidthFull();
        workspace.setFlexGrow(2, workspace.getComponentAt(0));
        workspace.setFlexGrow(1, workspace.getComponentAt(1));
        workspace.addClassName("workspace");

        add(filters, workspace);
    }

    private void configureGrid(List<DinosaurCatalog.DinosaurRecord> allDinosaurs) {
        grid.addColumn(DinosaurCatalog.DinosaurRecord::commonName).setHeader("Species").setAutoWidth(true).setFlexGrow(1);
        grid.addColumn(DinosaurCatalog.DinosaurRecord::period).setHeader("Period").setAutoWidth(true);
        grid.addColumn(DinosaurCatalog.DinosaurRecord::diet).setHeader("Diet").setAutoWidth(true);
        grid.addColumn(d -> String.format(Locale.US, "%.1f m", d.lengthMeters())).setHeader("Length");
        grid.addColumn(d -> d.dangerLevel() + "/10").setHeader("Danger");
        grid.setItems(allDinosaurs);
        grid.setHeight("620px");
        grid.addClassName("data-grid");

        grid.addSelectionListener(event -> {
            event.getFirstSelectedItem().ifPresent(this::showDetails);
        });
    }

    private Div buildGridPanel() {
        Div panel = new Div(grid);
        panel.addClassNames("panel-card", "grid-panel");
        return panel;
    }

    private Div buildDetailPanel() {
        Div panel = new Div();
        panel.addClassNames("panel-card", "detail-panel");

        Div hero = new Div(detailTitle, detailSubtitle);
        hero.addClassName("detail-hero");

        HorizontalLayout actions = new HorizontalLayout();
        actions.addClassName("detail-actions");

        Button logButton = new Button("Add to Safari Log", event -> addToSafariLog());
        logButton.addClassName("btn-ghost");

        soundButton.addClassName("btn-primary");
        soundButton.addClickListener(event -> playCall());

        wikiButton.addClassName("btn-ghost");
        wikiButton.addClickListener(event -> openWikipedia());
        wikiButton.setEnabled(false);
        soundButton.setEnabled(false);

        actions.add(soundButton, logButton, wikiButton);

        detailArea.setLabel("Profile");
        detailArea.setReadOnly(true);
        detailArea.addClassName("details-text");
        detailArea.setHeight("320px");
        detailArea.setWidthFull();

        safariLog.setReadOnly(true);
        safariLog.setHeight("220px");
        safariLog.addClassName("details-text");
        safariLog.setWidthFull();

        panel.add(hero, detailArea, actions, safariLog);
        return panel;
    }

    private void configureDetailPanel() {
        detailArea.setValue("No dinosaur selected.");
    }

    private void showDetails(DinosaurCatalog.DinosaurRecord dino) {
        selected = dino;
        detailTitle.setText(dino.commonName());
        detailSubtitle.setText(dino.scientificName() + " | " + dino.period() + " | " + dino.diet());
        detailArea.setValue(formatDetails(dino));
        wikiButton.setEnabled(true);
        soundButton.setEnabled(true);
    }

    private String formatDetails(DinosaurCatalog.DinosaurRecord dino) {
        return """
                Scientific Name: %s
                Period: %s
                Approx. Time: %.1f million years ago
                Diet: %s
                Length: %.1f m
                Mass: %.1f kg
                Top Speed: %.1f kph
                Region: %s
                First Described: %s
                Source: %s

                Field Notes:
                %s
                """.formatted(
                dino.scientificName(),
                dino.period(),
                dino.timeframeMya(),
                dino.diet(),
                dino.lengthMeters(),
                dino.massKg(),
                dino.topSpeedKph(),
                dino.region(),
                dino.firstDescribed(),
                dino.profileSource(),
                dino.fact()
        );
    }

    private void addToSafariLog() {
        if (selected == null) {
            Notification.show("Select a dinosaur first.");
            return;
        }
        SessionState.addSafariEntry(selected.commonName() + " | " + selected.period() + " | danger " + selected.dangerLevel() + "/10");
        RangerProgression.ProgressEvent reward = SessionState.progression()
                .recordDiscovery(selected.commonName(), selected.dangerLevel());
        refreshSafariLog();
        Notification.show(reward.title() + " | +" + reward.xpGained() + " XP | +$" + reward.creditsGained());
    }

    private void refreshSafariLog() {
        List<String> entries = SessionState.safariLogSnapshot();
        if (entries.isEmpty()) {
            safariLog.setValue("No entries yet.");
            return;
        }
        safariLog.setValue(String.join("\n", entries));
    }

    private void openWikipedia() {
        if (selected == null) {
            return;
        }
        String title = URLEncoder.encode(selected.wikiTitle(), StandardCharsets.UTF_8);
        getUI().ifPresent(ui -> ui.getPage().open("https://en.wikipedia.org/wiki/" + title, "_blank"));
    }

    private void playCall() {
        if (selected == null) {
            return;
        }
        DinosaurSoundSynthesizer.playSpeciesCall(selected);
        Notification.show("Synthesizing " + selected.commonName() + " call.");
    }

    private List<String> withAll(List<String> values) {
        List<String> options = new ArrayList<>();
        options.add("All");
        options.addAll(values);
        return options;
    }
}
