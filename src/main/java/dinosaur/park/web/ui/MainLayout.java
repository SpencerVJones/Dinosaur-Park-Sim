package dinosaur.park.web.ui;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLink;
import dinosaur.park.ParkOperations;
import dinosaur.park.RangerProgression;
import dinosaur.park.web.view.DashboardView;
import dinosaur.park.web.view.ExplorerView;
import dinosaur.park.web.view.LabView;
import dinosaur.park.web.view.QuestsView;
import dinosaur.park.web.view.WarRoomView;

public class MainLayout extends AppLayout {
    private final Span clock = new Span(ParkOperations.currentDateTime());
    private final Span rank = new Span();
    private final Span level = new Span();
    private final Span credits = new Span();
    private final Span codex = new Span();

    public MainLayout() {
        addClassName("app-shell");
        setPrimarySection(Section.DRAWER);
        addToNavbar(buildTopBar());
        addToDrawer(buildDrawer());
        refreshHud();
        startClockRefresh();
    }

    private HorizontalLayout buildTopBar() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.addClassName("nav-toggle");

        H1 title = new H1("Ancient Eden");
        title.addClassName("top-title");

        Span subtitle = new Span("Command Center");
        subtitle.addClassName("top-subtitle");

        Div titleBlock = new Div(title, subtitle);
        titleBlock.addClassName("top-title-block");

        Div hud = new Div(
                hudItem("Rank", rank),
                hudItem("Level", level),
                hudItem("Credits", credits),
                hudItem("Codex", codex)
        );
        hud.addClassName("top-hud");

        clock.addClassName("top-clock");

        HorizontalLayout topBar = new HorizontalLayout(toggle, titleBlock, hud, clock);
        topBar.expand(titleBlock);
        topBar.setWidthFull();
        topBar.setAlignItems(Alignment.CENTER);
        topBar.addClassName("top-bar");
        return topBar;
    }

    private VerticalLayout buildDrawer() {
        VerticalLayout drawer = new VerticalLayout();
        drawer.setPadding(false);
        drawer.setSpacing(false);
        drawer.addClassName("side-nav");

        drawer.add(navLink("Dashboard", DashboardView.class));
        drawer.add(navLink("Explorer", ExplorerView.class));
        drawer.add(navLink("Steward Lab", LabView.class));
        drawer.add(navLink("Quick Quests", QuestsView.class));
        drawer.add(navLink("War Room", WarRoomView.class));
        return drawer;
    }

    private RouterLink navLink(String text, Class<? extends Component> target) {
        RouterLink link = new RouterLink(text, target);
        link.addClassName("side-link");
        return link;
    }

    private void startClockRefresh() {
        UI ui = UI.getCurrent();
        if (ui == null) {
            return;
        }
        ui.setPollInterval(1000);
        ui.addPollListener(event -> {
            clock.setText(ParkOperations.currentDateTime());
            refreshHud();
        });
    }

    private Div hudItem(String label, Span value) {
        Span key = new Span(label);
        key.addClassName("top-hud-key");
        value.addClassName("top-hud-value");
        Div item = new Div(key, value);
        item.addClassName("top-hud-item");
        return item;
    }

    private void refreshHud() {
        RangerProgression progression = SessionState.progression();
        rank.setText(progression.rankTitle());
        level.setText(progression.level() + " (" + progression.xp() + "/" + progression.xpToNextLevel() + " XP)");
        credits.setText("$" + progression.credits());
        codex.setText(progression.discoveredSpeciesCount() + " species");
    }
}
