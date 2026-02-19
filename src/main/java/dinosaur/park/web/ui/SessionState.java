package dinosaur.park.web.ui;

import com.vaadin.flow.server.VaadinSession;
import dinosaur.park.CampaignEngine;
import dinosaur.park.RangerProgression;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class SessionState {
    private static final String CAMPAIGN_KEY = SessionState.class.getName() + ".campaign";
    private static final String LOG_KEY = SessionState.class.getName() + ".safari-log";
    private static final String PROGRESSION_KEY = SessionState.class.getName() + ".progression";

    private SessionState() {
    }

    public static CampaignEngine campaign() {
        VaadinSession session = VaadinSession.getCurrent();
        if (session == null) {
            return new CampaignEngine();
        }
        CampaignEngine engine = (CampaignEngine) session.getAttribute(CAMPAIGN_KEY);
        if (engine == null) {
            engine = new CampaignEngine();
            session.setAttribute(CAMPAIGN_KEY, engine);
        }
        return engine;
    }

    public static List<String> safariLog() {
        VaadinSession session = VaadinSession.getCurrent();
        if (session == null) {
            return new ArrayList<>();
        }
        @SuppressWarnings("unchecked")
        List<String> log = (List<String>) session.getAttribute(LOG_KEY);
        if (log == null) {
            log = new ArrayList<>();
            session.setAttribute(LOG_KEY, log);
        }
        return log;
    }

    public static void addSafariEntry(String entry) {
        if (entry == null || entry.isBlank()) {
            return;
        }
        safariLog().add(entry);
    }

    public static List<String> safariLogSnapshot() {
        return Collections.unmodifiableList(new ArrayList<>(safariLog()));
    }

    public static RangerProgression progression() {
        VaadinSession session = VaadinSession.getCurrent();
        if (session == null) {
            return new RangerProgression();
        }
        RangerProgression progression = (RangerProgression) session.getAttribute(PROGRESSION_KEY);
        if (progression == null) {
            progression = new RangerProgression();
            session.setAttribute(PROGRESSION_KEY, progression);
        }
        return progression;
    }
}
