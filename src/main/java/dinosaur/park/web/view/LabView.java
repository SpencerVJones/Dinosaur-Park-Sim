package dinosaur.park.web.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import dinosaur.park.DinosaurCatalog;
import dinosaur.park.ParkOperations;
import dinosaur.park.RangerProgression;
import dinosaur.park.web.ui.MainLayout;
import dinosaur.park.web.ui.SessionState;

import java.util.List;

@PageTitle("Ancient Eden | Steward Lab")
@Route(value = "steward-lab", layout = MainLayout.class)
public class LabView extends VerticalLayout {
    private final List<DinosaurCatalog.DinosaurRecord> dinosaurs = ParkOperations.allDinosaurs();

    public LabView() {
        addClassName("page");
        setSizeFull();
        setPadding(false);
        setSpacing(false);

        Div heading = new Div();
        heading.addClassNames("panel-card", "section-header");
        heading.setText("Steward Lab: feeding, habitat, budget, and compatibility diagnostics.");

        Div content = new Div();
        content.setSizeFull();

        Tab feedingTab = new Tab("Feeding Planner");
        Tab habitatTab = new Tab("Habitat Planner");
        Tab revenueTab = new Tab("Revenue Simulator");
        Tab compatibilityTab = new Tab("Compatibility");

        Tabs tabs = new Tabs(feedingTab, habitatTab, revenueTab, compatibilityTab);
        tabs.addClassName("lab-tabs");
        tabs.setWidthFull();

        Div feeding = feedingPanel();
        Div habitat = habitatPanel();
        Div revenue = revenuePanel();
        Div compatibility = compatibilityPanel();

        List<Div> panels = List.of(feeding, habitat, revenue, compatibility);
        content.add(panels.toArray(Div[]::new));
        showOnly(feeding, panels);

        tabs.addSelectedChangeListener(event -> {
            if (event.getSelectedTab() == feedingTab) {
                showOnly(feeding, panels);
            } else if (event.getSelectedTab() == habitatTab) {
                showOnly(habitat, panels);
            } else if (event.getSelectedTab() == revenueTab) {
                showOnly(revenue, panels);
            } else {
                showOnly(compatibility, panels);
            }
        });

        add(heading, tabs, content);
    }

    private Div feedingPanel() {
        Div panel = basePanel();
        panel.add(new H3("Feeding Planner"));

        Select<DinosaurCatalog.DinosaurRecord> species = speciesSelect("Species");
        IntegerField population = new IntegerField("Population");
        population.setValue(1);
        population.setMin(1);
        population.setStepButtonsVisible(true);

        TextArea output = outputBox();
        Button run = new Button("Estimate Feed Cycle", event -> {
            DinosaurCatalog.DinosaurRecord dino = species.getValue();
            Integer count = population.getValue();
            if (dino == null || count == null || count < 1) {
                Notification.show("Select species and population.");
                return;
            }

            double dailyFood = ParkOperations.dailyFoodKg(dino, count);
            double enclosure = ParkOperations.habitatAreaSqMeters(dino, count);
            RangerProgression.ProgressEvent reward = SessionState.progression()
                    .recordLabOperation("Feeding Planner", 1);
            output.setValue("""
                    Species: %s
                    Population: %d
                    Estimated daily food: %.1f kg
                    Recommended enclosure area: %.0f sq m
                    
                    Reward:
                    %s
                    """.formatted(dino.commonName(), count, dailyFood, enclosure, reward.summary()));
        });
        run.addClassName("btn-primary");

        HorizontalLayout controls = new HorizontalLayout(species, population, run);
        controls.setAlignItems(Alignment.END);
        controls.setWidthFull();
        controls.addClassName("tool-row");

        panel.add(controls, output);
        return panel;
    }

    private Div habitatPanel() {
        Div panel = basePanel();
        panel.add(new H3("Habitat Planner"));

        Select<DinosaurCatalog.DinosaurRecord> species = speciesSelect("Species");
        IntegerField population = new IntegerField("Population");
        population.setValue(2);
        population.setMin(1);
        population.setStepButtonsVisible(true);

        TextArea output = outputBox();
        Button run = new Button("Plan Habitat", event -> {
            DinosaurCatalog.DinosaurRecord dino = species.getValue();
            Integer count = population.getValue();
            if (dino == null || count == null || count < 1) {
                Notification.show("Select species and population.");
                return;
            }

            String enclosureTier = ParkOperations.habitatSize(dino.commonName());
            double enclosureArea = ParkOperations.habitatAreaSqMeters(dino, count);
            RangerProgression.ProgressEvent reward = SessionState.progression()
                    .recordLabOperation("Habitat Planner", 2);
            output.setValue("""
                    Species: %s
                    Suggested enclosure type: %s
                    Population target: %d
                    Minimum area: %.0f sq m
                    Handling risk: %d/10
                    
                    Reward:
                    %s
                    """.formatted(dino.commonName(), enclosureTier, count, enclosureArea, dino.dangerLevel(), reward.summary()));
        });
        run.addClassName("btn-primary");

        HorizontalLayout controls = new HorizontalLayout(species, population, run);
        controls.setAlignItems(Alignment.END);
        controls.setWidthFull();
        controls.addClassName("tool-row");

        panel.add(controls, output);
        return panel;
    }

    private Div revenuePanel() {
        Div panel = basePanel();
        panel.add(new H3("Revenue Simulator"));

        IntegerField visitors = new IntegerField("Visitors");
        visitors.setValue(2200);
        visitors.setMin(0);

        NumberField ticket = new NumberField("Ticket Price ($)");
        ticket.setValue(54.0);
        ticket.setMin(0);

        NumberField fixed = new NumberField("Fixed Cost ($)");
        fixed.setValue(35000.0);
        fixed.setMin(0);

        NumberField variable = new NumberField("Variable Cost / Visitor ($)");
        variable.setValue(14.0);
        variable.setMin(0);

        TextArea output = outputBox();
        Button run = new Button("Run Forecast", event -> {
            try {
                ParkOperations.RevenueResult result = ParkOperations.revenueProjection(
                        visitors.getValue() == null ? 0 : visitors.getValue(),
                        ticket.getValue() == null ? 0 : ticket.getValue(),
                        fixed.getValue() == null ? 0 : fixed.getValue(),
                        variable.getValue() == null ? 0 : variable.getValue()
                );
                RangerProgression.ProgressEvent reward = SessionState.progression()
                        .recordLabOperation("Revenue Simulator", 2);

                output.setValue("""
                        Gross Revenue: $%,.0f
                        Total Costs: $%,.0f
                        Net Revenue: $%,.0f
                        Margin: %.1f%%
                        
                        Reward:
                        %s
                        """.formatted(
                        result.grossRevenue(),
                        result.totalCosts(),
                        result.netRevenue(),
                        result.marginPercent(),
                        reward.summary()
                ));
            } catch (IllegalArgumentException ex) {
                Notification.show(ex.getMessage());
            }
        });
        run.addClassName("btn-primary");

        HorizontalLayout rowOne = new HorizontalLayout(visitors, ticket, fixed, variable, run);
        rowOne.setAlignItems(Alignment.END);
        rowOne.setWidthFull();
        rowOne.addClassName("tool-row");

        panel.add(rowOne, output);
        return panel;
    }

    private Div compatibilityPanel() {
        Div panel = basePanel();
        panel.add(new H3("Compatibility Check"));

        Select<DinosaurCatalog.DinosaurRecord> first = speciesSelect("Species A");
        Select<DinosaurCatalog.DinosaurRecord> second = speciesSelect("Species B");

        TextArea output = outputBox();
        Button run = new Button("Analyze Pair", event -> {
            DinosaurCatalog.DinosaurRecord one = first.getValue();
            DinosaurCatalog.DinosaurRecord two = second.getValue();
            if (one == null || two == null) {
                Notification.show("Select both species.");
                return;
            }
            if (one.equals(two)) {
                Notification.show("Choose two different species.");
                return;
            }

            ParkOperations.CompatibilityResult result = ParkOperations.compareSpecies(one, two);
            RangerProgression.ProgressEvent reward = SessionState.progression()
                    .recordLabOperation("Compatibility Check", 3);
            output.setValue("""
                    Pair: %s + %s
                    Compatibility Score: %d/100
                    Tier: %s
                    Summary: %s
                    
                    Reward:
                    %s
                    """.formatted(
                    one.commonName(),
                    two.commonName(),
                    result.score(),
                    result.tier(),
                    result.summary(),
                    reward.summary()
            ));
        });
        run.addClassName("btn-primary");

        HorizontalLayout controls = new HorizontalLayout(first, second, run);
        controls.setAlignItems(Alignment.END);
        controls.setWidthFull();
        controls.addClassName("tool-row");

        panel.add(controls, output);
        return panel;
    }

    private Select<DinosaurCatalog.DinosaurRecord> speciesSelect(String label) {
        Select<DinosaurCatalog.DinosaurRecord> select = new Select<>();
        select.setLabel(label);
        select.setItems(dinosaurs);
        select.setItemLabelGenerator(d -> d.commonName() + " (" + d.period() + ")");
        return select;
    }

    private TextArea outputBox() {
        TextArea output = new TextArea("Output");
        output.setReadOnly(true);
        output.setHeight("280px");
        output.addClassName("details-text");
        return output;
    }

    private Div basePanel() {
        Div panel = new Div();
        panel.addClassNames("panel-card", "lab-panel");
        panel.setWidthFull();
        return panel;
    }

    private void showOnly(Div target, List<Div> allPanels) {
        for (Div panel : allPanels) {
            panel.setVisible(panel == target);
        }
    }
}
