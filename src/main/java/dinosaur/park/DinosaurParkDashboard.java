package dinosaur.park;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class DinosaurParkDashboard extends JFrame {
    private static final int SPACE_8 = 8;
    private static final int SPACE_16 = 16;
    private static final int SPACE_24 = 24;
    private static final int SPACE_32 = 32;
    private static final int SPACE_48 = 48;

    private static final Color ACCENT = new Color(66, 102, 188);
    private static final Color NEUTRAL_BACKGROUND = new Color(8, 13, 28);
    private static final Color SURFACE = new Color(20, 28, 52);
    private static final Color SURFACE_ELEVATED = new Color(28, 38, 67);
    private static final Color MUTED_GRAY_900 = new Color(236, 241, 255);
    private static final Color MUTED_GRAY_700 = new Color(173, 185, 214);
    private static final Color MUTED_GRAY_500 = new Color(130, 143, 174);
    private static final Color MUTED_GRAY_300 = new Color(63, 79, 118);

    private static final Font FONT_DISPLAY = new Font("SansSerif", Font.BOLD, 52);
    private static final Font FONT_H1 = new Font("SansSerif", Font.BOLD, 30);
    private static final Font FONT_H2 = new Font("SansSerif", Font.BOLD, 22);
    private static final Font FONT_BODY = new Font("SansSerif", Font.PLAIN, 15);
    private static final Font FONT_BODY_STRONG = new Font("SansSerif", Font.BOLD, 15);
    private static final Font FONT_CAPTION = new Font("SansSerif", Font.PLAIN, 13);

    private final DinosaurCatalog database = DinosaurCatalog.loadDefault();
    private final DinosaurImageClient imageService = new DinosaurImageClient();
    private final CampaignEngine campaign = new CampaignEngine();
    private final Random random = new Random();

    private final CardLayout workspaceCards = new CardLayout();
    private final JPanel workspaceContainer = new JPanel(workspaceCards);

    private final JTextArea feedArea = createOutputArea();
    private final JLabel clockLabel = new JLabel(ParkOperations.currentDateTime(), SwingConstants.RIGHT);
    private final JLabel sessionLabel = new JLabel("Session: none");
    private final JLabel safariScoreLabel = new JLabel("Scout score: 0");
    private final JLabel fossilPointsLabel = new JLabel("Fossil points: 0");
    private final JLabel campaignStatusLabel = new JLabel();
    private final JLabel campaignGoalLabel = new JLabel();
    private final JLabel campaignWarRoomStatusLabel = new JLabel();
    private final JLabel campaignWarRoomGoalLabel = new JLabel();
    private final JTextArea campaignNarrativeArea = createOutputArea();
    private final JLabel chronicleSessionLabel = new JLabel("Session: none");
    private final JLabel chronicleSafariLabel = new JLabel("Scout score: 0");
    private final JLabel chronicleFossilLabel = new JLabel("Fossil points: 0");
    private final JLabel chronicleCampaignLabel = new JLabel();

    private final JLabel explorerImageLabel = new JLabel();
    private final JTextArea explorerDetailsArea = createOutputArea();

    private final DefaultTableModel dinosaurTableModel = createDinosaurModel();
    private final JTable dinosaurTable = new JTable(dinosaurTableModel);
    private final TableRowSorter<DefaultTableModel> dinosaurSorter = new TableRowSorter<>(dinosaurTableModel);
    private final List<DinosaurCatalog.DinosaurRecord> visibleDinosaurs = new ArrayList<>();

    private JTextField sessionNameField;
    private JComboBox<String> sessionRoleCombo;
    private JTextField searchField;
    private JComboBox<String> periodFilter;
    private JComboBox<String> dietFilter;

    private int safariScore = 0;
    private int fossilPoints = 0;

    private JLabel quizPromptLabel;
    private JComboBox<String> quizChoices;
    private JTextArea quizResultArea;
    private ParkOperations.QuizQuestion currentQuiz;

    private DinosaurCatalog.DinosaurRecord selectedDinosaur;

    public DinosaurParkDashboard() {
        setTitle("Ancient Eden | Real Dinosaur Command Center");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1320, 820));
        setSize(1460, 920);
        setLocationRelativeTo(null);

        JPanel root = new GradientBackgroundPanel();
        root.setLayout(new BorderLayout(SPACE_16, SPACE_16));
        root.setBorder(new EmptyBorder(SPACE_8, SPACE_8, SPACE_8, SPACE_8));
        root.setBackground(NEUTRAL_BACKGROUND);
        setContentPane(root);

        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildBody(), BorderLayout.CENTER);

        updateCampaignDisplay();
        syncChronicleLabels();
        refreshDinosaurTable();
        startClock();
    }

    private JPanel buildHeader() {
        JPanel panel = wrapPanel(new JPanel(new BorderLayout(SPACE_16, SPACE_16)));
        panel.setBackground(SURFACE);
        panel.setBorder(new EmptyBorder(SPACE_8, SPACE_8, SPACE_8, SPACE_8));

        JPanel topBar = wrapPanel(new JPanel(new BorderLayout(SPACE_8, SPACE_8)));
        topBar.setBackground(new Color(12, 18, 35));
        topBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MUTED_GRAY_300, 1),
                new EmptyBorder(SPACE_8, SPACE_16, SPACE_8, SPACE_16)
        ));

        JPanel dots = new JPanel(new FlowLayout(FlowLayout.LEFT, SPACE_8, 0));
        dots.setOpaque(false);
        dots.add(statusDot(new Color(236, 113, 95)));
        dots.add(statusDot(new Color(239, 188, 102)));
        dots.add(statusDot(new Color(119, 200, 137)));

        JLabel bell = new JLabel("\uD83D\uDD14");
        bell.setFont(FONT_CAPTION);
        bell.setForeground(MUTED_GRAY_700);

        JPanel rightTop = new JPanel(new FlowLayout(FlowLayout.RIGHT, SPACE_16, 0));
        rightTop.setOpaque(false);
        rightTop.add(clockLabel);
        rightTop.add(bell);

        topBar.add(dots, BorderLayout.WEST);
        topBar.add(rightTop, BorderLayout.EAST);

        JLabel title = new JLabel("Ancient Eden");
        title.setForeground(MUTED_GRAY_900);
        title.setFont(FONT_DISPLAY);

        JLabel subtitle = new JLabel("Command Center");
        subtitle.setForeground(MUTED_GRAY_700);
        subtitle.setFont(FONT_BODY);

        JPanel hero = wrapPanel(new JPanel(new BorderLayout(SPACE_24, SPACE_24)));
        hero.setBackground(SURFACE);
        hero.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MUTED_GRAY_300, 1),
                new EmptyBorder(SPACE_24, SPACE_24, SPACE_24, SPACE_24)
        ));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.add(title);
        left.add(Box.createVerticalStrut(SPACE_8));
        left.add(subtitle);

        JLabel motif = new JLabel("Prime Ecosystem Online");
        motif.setFont(FONT_BODY);
        motif.setForeground(new Color(161, 201, 255));
        motif.setHorizontalAlignment(SwingConstants.RIGHT);

        hero.add(left, BorderLayout.WEST);
        hero.add(motif, BorderLayout.EAST);

        JPanel nav = new JPanel(new FlowLayout(FlowLayout.LEFT, SPACE_8, 0));
        nav.setOpaque(false);
        nav.add(headerNavButton("Command Deck", "dashboard"));
        nav.add(headerNavButton("Bestiary", "explorer"));
        nav.add(headerNavButton("Steward Lab", "lab"));
        nav.add(headerNavButton("Expeditions", "adventure"));
        nav.add(headerNavButton("Quest Board", "missions"));
        nav.add(headerNavButton("War Room", "campaign"));
        nav.add(headerNavButton("Chronicle", "chronicle"));

        sessionNameField = new JTextField(14);
        sessionRoleCombo = new JComboBox<>(new String[]{"Ranger", "Warden"});
        styleInput(sessionNameField);
        styleInput(sessionRoleCombo);

        JButton startSessionBtn = primaryButton("Enter Campaign");
        startSessionBtn.addActionListener(e -> startSession());

        JPanel sessionRow = new JPanel(new FlowLayout(FlowLayout.LEFT, SPACE_8, 0));
        sessionRow.setOpaque(false);
        sessionRow.add(labeledField("Ranger", sessionNameField, MUTED_GRAY_700));
        sessionRow.add(labeledField("Role", sessionRoleCombo, MUTED_GRAY_700));
        sessionRow.add(startSessionBtn);

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.add(hero);
        center.add(Box.createVerticalStrut(SPACE_8));
        center.add(sessionRow);
        center.add(Box.createVerticalStrut(SPACE_8));
        center.add(nav);

        panel.add(topBar, BorderLayout.NORTH);
        panel.add(center, BorderLayout.CENTER);

        return panel;
    }

    private JPanel buildBody() {
        return buildWorkspace();
    }

    private JPanel buildControlRail() {
        JPanel rail = wrapPanel(new JPanel());
        rail.setLayout(new BoxLayout(rail, BoxLayout.Y_AXIS));
        rail.setBackground(SURFACE);
        rail.setPreferredSize(new Dimension(340, 780));
        rail.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MUTED_GRAY_300, 1),
                new EmptyBorder(SPACE_24, SPACE_24, SPACE_24, SPACE_24)
        ));

        JLabel railTitle = new JLabel("Guild Ledger");
        railTitle.setFont(FONT_H2);
        railTitle.setForeground(MUTED_GRAY_900);

        sessionNameField = new JTextField();
        sessionRoleCombo = new JComboBox<>(new String[]{"Ranger", "Warden"});

        JButton startSessionBtn = primaryButton("Enter Campaign");
        startSessionBtn.addActionListener(e -> startSession());

        JButton dashboardBtn = secondaryButton("Command Deck");
        dashboardBtn.addActionListener(e -> workspaceCards.show(workspaceContainer, "dashboard"));

        JButton explorerBtn = secondaryButton("Bestiary");
        explorerBtn.addActionListener(e -> workspaceCards.show(workspaceContainer, "explorer"));

        JButton labsBtn = secondaryButton("Steward Lab");
        labsBtn.addActionListener(e -> workspaceCards.show(workspaceContainer, "lab"));

        JButton adventureBtn = secondaryButton("Expeditions");
        adventureBtn.addActionListener(e -> workspaceCards.show(workspaceContainer, "adventure"));

        JButton missionBtn = secondaryButton("Quest Board");
        missionBtn.addActionListener(e -> workspaceCards.show(workspaceContainer, "missions"));

        JButton campaignBtn = secondaryButton("War Room");
        campaignBtn.addActionListener(e -> workspaceCards.show(workspaceContainer, "campaign"));

        JButton chronicleBtn = secondaryButton("Chronicle");
        chronicleBtn.addActionListener(e -> workspaceCards.show(workspaceContainer, "chronicle"));

        JButton randomEncounterBtn = primaryButton("Trigger Encounter");
        randomEncounterBtn.addActionListener(e -> triggerRandomEncounter());

        JButton capacityBtn = secondaryButton("Check Capacity");
        capacityBtn.addActionListener(e -> {
            int visitorCount = ParkOperations.randomVisitorCount();
            appendFeed("Gate report: " + ParkOperations.visitorCapacityMessage(visitorCount));
        });

        JLabel navHeading = new JLabel("Navigation");
        navHeading.setFont(FONT_CAPTION.deriveFont(Font.BOLD));
        navHeading.setForeground(MUTED_GRAY_700);

        JLabel actionHeading = new JLabel("Field Actions");
        actionHeading.setFont(FONT_CAPTION.deriveFont(Font.BOLD));
        actionHeading.setForeground(MUTED_GRAY_700);

        JLabel statusHeading = new JLabel("Session Status");
        statusHeading.setFont(FONT_CAPTION.deriveFont(Font.BOLD));
        statusHeading.setForeground(MUTED_GRAY_700);

        rail.add(railTitle);
        rail.add(Box.createVerticalStrut(SPACE_24));
        rail.add(labeledField("Ranger Name", sessionNameField, MUTED_GRAY_700));
        rail.add(Box.createVerticalStrut(SPACE_16));
        rail.add(labeledField("Role", sessionRoleCombo, MUTED_GRAY_700));
        rail.add(Box.createVerticalStrut(SPACE_16));
        rail.add(startSessionBtn);
        rail.add(Box.createVerticalStrut(SPACE_16));
        rail.add(new JSeparator());
        rail.add(Box.createVerticalStrut(SPACE_16));
        rail.add(navHeading);
        rail.add(Box.createVerticalStrut(SPACE_8));
        rail.add(dashboardBtn);
        rail.add(Box.createVerticalStrut(SPACE_8));
        rail.add(explorerBtn);
        rail.add(Box.createVerticalStrut(SPACE_8));
        rail.add(labsBtn);
        rail.add(Box.createVerticalStrut(SPACE_8));
        rail.add(adventureBtn);
        rail.add(Box.createVerticalStrut(SPACE_8));
        rail.add(missionBtn);
        rail.add(Box.createVerticalStrut(SPACE_8));
        rail.add(campaignBtn);
        rail.add(Box.createVerticalStrut(SPACE_8));
        rail.add(chronicleBtn);
        rail.add(Box.createVerticalStrut(SPACE_16));
        rail.add(new JSeparator());
        rail.add(Box.createVerticalStrut(SPACE_16));
        rail.add(actionHeading);
        rail.add(Box.createVerticalStrut(SPACE_8));
        rail.add(randomEncounterBtn);
        rail.add(Box.createVerticalStrut(SPACE_8));
        rail.add(capacityBtn);
        rail.add(Box.createVerticalStrut(SPACE_16));
        rail.add(new JSeparator());
        rail.add(Box.createVerticalStrut(SPACE_16));
        rail.add(statusHeading);
        rail.add(Box.createVerticalStrut(SPACE_8));

        sessionLabel.setFont(FONT_BODY_STRONG);
        safariScoreLabel.setFont(FONT_BODY_STRONG);
        fossilPointsLabel.setFont(FONT_BODY_STRONG);
        campaignStatusLabel.setFont(FONT_CAPTION);
        campaignGoalLabel.setFont(FONT_CAPTION);
        sessionLabel.setForeground(MUTED_GRAY_900);
        safariScoreLabel.setForeground(MUTED_GRAY_900);
        fossilPointsLabel.setForeground(MUTED_GRAY_900);
        campaignStatusLabel.setForeground(MUTED_GRAY_700);
        campaignGoalLabel.setForeground(MUTED_GRAY_500);

        rail.add(sessionLabel);
        rail.add(Box.createVerticalStrut(SPACE_8));
        rail.add(safariScoreLabel);
        rail.add(Box.createVerticalStrut(SPACE_8));
        rail.add(fossilPointsLabel);
        rail.add(Box.createVerticalStrut(SPACE_8));
        rail.add(campaignStatusLabel);
        rail.add(Box.createVerticalStrut(SPACE_8));
        rail.add(campaignGoalLabel);
        rail.add(Box.createVerticalStrut(SPACE_16));

        return rail;
    }

    private JPanel buildWorkspace() {
        workspaceContainer.setOpaque(false);

        workspaceContainer.add(buildDashboardPage(), "dashboard");
        workspaceContainer.add(buildExplorerPage(), "explorer");
        workspaceContainer.add(buildLabPage(), "lab");
        workspaceContainer.add(buildAdventuresPage(), "adventure");
        workspaceContainer.add(buildMissionPage(), "missions");
        workspaceContainer.add(buildCampaignPage(), "campaign");
        workspaceContainer.add(buildChroniclePage(), "chronicle");

        workspaceCards.show(workspaceContainer, "dashboard");
        return workspaceContainer;
    }

    private JPanel buildDashboardPage() {
        JPanel page = wrapPanel(new JPanel(new BorderLayout(SPACE_24, SPACE_24)));

        JPanel statsRow = new JPanel(new GridLayout(1, 4, SPACE_16, SPACE_16));
        statsRow.setOpaque(false);

        int speciesCount = database.all().size();
        double avgLength = database.all().stream().mapToDouble(DinosaurCatalog.DinosaurRecord::lengthMeters).average().orElse(0);
        double maxMass = database.all().stream().mapToDouble(DinosaurCatalog.DinosaurRecord::massKg).max().orElse(0);
        double avgDanger = database.all().stream().mapToInt(DinosaurCatalog.DinosaurRecord::dangerLevel).average().orElse(0);

        statsRow.add(statCard("Species", Integer.toString(speciesCount), "Real paleontology dataset"));
        statsRow.add(statCard("Avg Length", formatOne(avgLength) + " m", "Bestiary-wide body scale"));
        statsRow.add(statCard("Heaviest", formatOne(maxMass / 1000.0) + " tons", "Prime titan benchmark"));
        statsRow.add(statCard("Avg Danger", formatOne(avgDanger) + "/10", "Encounter threat baseline"));

        JTextArea highlights = createOutputArea();
        highlights.setText("""
                Ranger briefing:
                - Bestiary records are loaded from a local curated dinosaur dataset.
                - Species portraits stream in from Wikipedia when available.
                - Encounter calls are synthesized from species profile traits.
                - Quest systems include random encounters, quiz trials, fossil digs, and mission tracks.
                - Steward Lab handles feeding, habitat, revenue, and compatibility planning.
                """);
        highlights.setCaretPosition(0);
        highlights.setColumns(68);

        JPanel quickPanel = wrapPanel(new JPanel());
        quickPanel.setLayout(new BoxLayout(quickPanel, BoxLayout.Y_AXIS));
        quickPanel.setBorder(sectionBorder("Quick Quests"));

        JButton encounterBtn = primaryButton("Launch Encounter");
        encounterBtn.addActionListener(e -> triggerRandomEncounter());
        JButton quizBtn = secondaryButton("Open Quiz Arena");
        quizBtn.addActionListener(e -> workspaceCards.show(workspaceContainer, "adventure"));
        JButton labBtn = secondaryButton("Open Steward Lab");
        labBtn.addActionListener(e -> workspaceCards.show(workspaceContainer, "lab"));
        JButton warRoomBtn = secondaryButton("Open War Room");
        warRoomBtn.addActionListener(e -> workspaceCards.show(workspaceContainer, "campaign"));

        quickPanel.add(encounterBtn);
        quickPanel.add(Box.createVerticalStrut(SPACE_16));
        quickPanel.add(quizBtn);
        quickPanel.add(Box.createVerticalStrut(SPACE_16));
        quickPanel.add(labBtn);
        quickPanel.add(Box.createVerticalStrut(SPACE_16));
        quickPanel.add(warRoomBtn);

        JScrollPane highlightsScroll = new JScrollPane(highlights);
        highlightsScroll.setPreferredSize(new Dimension(760, 320));
        highlightsScroll.setBorder(sectionBorder("Ranger Briefing"));

        JSplitPane lowerSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                highlightsScroll, quickPanel);
        lowerSplit.setResizeWeight(0.68);
        lowerSplit.setBorder(BorderFactory.createEmptyBorder());

        page.add(statsRow, BorderLayout.NORTH);
        page.add(lowerSplit, BorderLayout.CENTER);
        return page;
    }

    private JPanel buildExplorerPage() {
        JPanel page = wrapPanel(new JPanel(new BorderLayout(SPACE_16, SPACE_16)));

        searchField = new JTextField();
        periodFilter = new JComboBox<>();
        dietFilter = new JComboBox<>();

        periodFilter.addItem("All");
        for (String period : ParkOperations.allPeriods()) {
            periodFilter.addItem(period);
        }

        dietFilter.addItem("All");
        for (String diet : ParkOperations.allDiets()) {
            dietFilter.addItem(diet);
        }

        JButton applyFilterBtn = primaryButton("Apply Filters");
        applyFilterBtn.addActionListener(e -> refreshDinosaurTable());

        JButton clearFilterBtn = secondaryButton("Clear");
        clearFilterBtn.addActionListener(e -> {
            searchField.setText("");
            periodFilter.setSelectedItem("All");
            dietFilter.setSelectedItem("All");
            refreshDinosaurTable();
        });

        searchField.addActionListener(e -> refreshDinosaurTable());
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                refreshDinosaurTable();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                refreshDinosaurTable();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                refreshDinosaurTable();
            }
        });
        periodFilter.addActionListener(e -> refreshDinosaurTable());
        dietFilter.addActionListener(e -> refreshDinosaurTable());

        JPanel filterBar = new JPanel(new GridLayout(1, 5, SPACE_16, SPACE_16));
        filterBar.setOpaque(false);
        filterBar.add(labeledField("Search", searchField));
        filterBar.add(labeledField("Period", periodFilter));
        filterBar.add(labeledField("Diet", dietFilter));
        filterBar.add(applyFilterBtn);
        filterBar.add(clearFilterBtn);

        dinosaurTable.setFillsViewportHeight(true);
        dinosaurTable.setRowHeight(28);
        dinosaurTable.setFont(FONT_CAPTION);
        dinosaurTable.getTableHeader().setFont(FONT_CAPTION.deriveFont(Font.BOLD));
        dinosaurTable.getTableHeader().setBackground(new Color(31, 43, 74));
        dinosaurTable.getTableHeader().setForeground(MUTED_GRAY_900);
        dinosaurTable.setGridColor(MUTED_GRAY_300);
        dinosaurTable.setBackground(new Color(18, 25, 46));
        dinosaurTable.setForeground(MUTED_GRAY_900);
        dinosaurTable.setSelectionBackground(new Color(55, 84, 142));
        dinosaurTable.setSelectionForeground(Color.WHITE);
        dinosaurTable.setAutoCreateRowSorter(false);
        dinosaurTable.setRowSorter(dinosaurSorter);
        dinosaurSorter.setSortsOnUpdates(true);
        dinosaurSorter.toggleSortOrder(0);

        dinosaurTable.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) {
                return;
            }
            int viewRow = dinosaurTable.getSelectedRow();
            if (viewRow < 0) {
                return;
            }

            int modelRow = dinosaurTable.convertRowIndexToModel(viewRow);
            if (modelRow < 0 || modelRow >= visibleDinosaurs.size()) {
                return;
            }
            selectedDinosaur = visibleDinosaurs.get(modelRow);
            updateExplorerDetails(selectedDinosaur);
        });

        JScrollPane tableScroll = new JScrollPane(dinosaurTable);
        tableScroll.setBorder(sectionBorder("Bestiary Registry"));

        explorerImageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        explorerImageLabel.setPreferredSize(new Dimension(320, 220));
        explorerImageLabel.setBorder(BorderFactory.createLineBorder(MUTED_GRAY_300, 1));

        explorerDetailsArea.setText("Select a bestiary entry to load dossier details, portrait, and species call.");

        JButton playCallBtn = primaryButton("Play Dinosaur Call");
        playCallBtn.addActionListener(e -> {
            if (selectedDinosaur == null) {
                showInputError("Select a dinosaur first.");
                return;
            }
            DinosaurSoundSynthesizer.playSpeciesCall(selectedDinosaur);
            appendFeed("Audio: played call for " + selectedDinosaur.commonName() + ".");
        });

        JButton safariLogBtn = secondaryButton("Add to Scout Log");
        safariLogBtn.addActionListener(e -> {
            if (selectedDinosaur == null) {
                showInputError("Select a dinosaur first.");
                return;
            }
            safariScore += Math.max(3, selectedDinosaur.dangerLevel());
            safariScoreLabel.setText("Scout score: " + safariScore);
            syncChronicleLabels();
            appendFeed("Scout log: observed " + selectedDinosaur.commonName() + ". +"
                    + Math.max(3, selectedDinosaur.dangerLevel()) + " points.");
            DinosaurSoundSynthesizer.playSpeciesCall(selectedDinosaur);
        });

        JButton wikiBtn = secondaryButton("Open Wikipedia");
        wikiBtn.addActionListener(e -> openWikipediaForSelection());

        JPanel detailActions = new JPanel(new GridLayout(3, 1, SPACE_8, SPACE_8));
        detailActions.setOpaque(false);
        detailActions.add(playCallBtn);
        detailActions.add(safariLogBtn);
        detailActions.add(wikiBtn);

        JPanel detailPanel = wrapPanel(new JPanel(new BorderLayout(SPACE_16, SPACE_16)));
        detailPanel.setBorder(sectionBorder("Dossier Viewer"));
        detailPanel.add(explorerImageLabel, BorderLayout.NORTH);
        detailPanel.add(new JScrollPane(explorerDetailsArea), BorderLayout.CENTER);
        detailPanel.add(detailActions, BorderLayout.SOUTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tableScroll, detailPanel);
        splitPane.setResizeWeight(0.62);
        splitPane.setBorder(BorderFactory.createEmptyBorder());

        page.add(filterBar, BorderLayout.NORTH);
        page.add(splitPane, BorderLayout.CENTER);
        return page;
    }

    private JPanel buildLabPage() {
        JPanel page = wrapPanel(new JPanel(new BorderLayout(SPACE_16, SPACE_16)));
        JTabbedPane tabs = new JTabbedPane();
        styleTabs(tabs);
        tabs.addTab("Feeding", buildFeedingPlannerCard());
        tabs.addTab("Habitat", buildHabitatPlannerCard());
        tabs.addTab("Revenue", buildRevenueSimulatorCard());
        tabs.addTab("Compatibility", buildCompatibilityCard());
        page.add(tabs, BorderLayout.CENTER);
        return page;
    }

    private JPanel buildAdventuresPage() {
        JPanel page = wrapPanel(new JPanel(new BorderLayout(SPACE_16, SPACE_16)));
        JTabbedPane tabs = new JTabbedPane();
        styleTabs(tabs);
        tabs.addTab("Quiz Arena", buildQuizPanel());
        tabs.addTab("Fossil Dig", buildFossilDigPanel());
        tabs.addTab("Sound Shrine", buildSoundboardPanel());
        page.add(tabs, BorderLayout.CENTER);
        return page;
    }

    private JPanel buildMissionPage() {
        JPanel page = wrapPanel(new JPanel(new BorderLayout(SPACE_24, SPACE_24)));

        JPanel checklistPanel = wrapPanel(new JPanel());
        checklistPanel.setLayout(new BoxLayout(checklistPanel, BoxLayout.Y_AXIS));
        checklistPanel.setBorder(sectionBorder("Daily Quests"));

        JCheckBox mission1 = new JCheckBox("Log 3 field encounters");
        JCheckBox mission2 = new JCheckBox("Complete one quiz trial");
        JCheckBox mission3 = new JCheckBox("Run a feeding protocol");
        JCheckBox mission4 = new JCheckBox("Recover one rare fossil");
        JCheckBox[] missionChecks = {mission1, mission2, mission3, mission4};
        for (JCheckBox check : missionChecks) {
            check.setOpaque(false);
            check.setFont(FONT_BODY);
            check.setForeground(MUTED_GRAY_900);
        }

        JLabel missionProgress = new JLabel("Progress: 0/4 complete");
        missionProgress.setFont(FONT_BODY_STRONG);

        Runnable refresh = () -> {
            int done = 0;
            if (mission1.isSelected()) {
                done++;
            }
            if (mission2.isSelected()) {
                done++;
            }
            if (mission3.isSelected()) {
                done++;
            }
            if (mission4.isSelected()) {
                done++;
            }

            missionProgress.setText("Progress: " + done + "/4 complete");
            if (done == 4) {
                appendFeed("Quest board complete. Guild morale boosted.");
            }
        };

        mission1.addActionListener(e -> refresh.run());
        mission2.addActionListener(e -> refresh.run());
        mission3.addActionListener(e -> refresh.run());
        mission4.addActionListener(e -> refresh.run());

        checklistPanel.add(mission1);
        checklistPanel.add(Box.createVerticalStrut(SPACE_8));
        checklistPanel.add(mission2);
        checklistPanel.add(Box.createVerticalStrut(SPACE_8));
        checklistPanel.add(mission3);
        checklistPanel.add(Box.createVerticalStrut(SPACE_8));
        checklistPanel.add(mission4);
        checklistPanel.add(Box.createVerticalStrut(SPACE_16));
        checklistPanel.add(missionProgress);

        JTextArea notes = createOutputArea();
        notes.setText("""
                Quest board tips:
                - Bestiary is best for encounter logs and species scouting.
                - Expeditions hosts quiz and fossil sessions.
                - Steward Lab supports planning tasks tied to roleplay progression.
                """);
        notes.setColumns(68);

        JScrollPane notesScroll = new JScrollPane(notes);
        notesScroll.setPreferredSize(new Dimension(760, 320));
        notesScroll.setBorder(sectionBorder("Quest Notes"));

        page.add(checklistPanel, BorderLayout.WEST);
        page.add(notesScroll, BorderLayout.CENTER);
        return page;
    }

    private JPanel buildCampaignPage() {
        JPanel page = wrapPanel(new JPanel(new BorderLayout(SPACE_24, SPACE_24)));

        JPanel statusPanel = card("War Room Status");
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Campaign Command");
        title.setFont(FONT_H1);
        title.setForeground(MUTED_GRAY_900);

        campaignWarRoomStatusLabel.setFont(FONT_BODY_STRONG);
        campaignWarRoomStatusLabel.setForeground(MUTED_GRAY_900);
        campaignWarRoomGoalLabel.setFont(FONT_CAPTION);
        campaignWarRoomGoalLabel.setForeground(MUTED_GRAY_700);

        statusPanel.add(title);
        statusPanel.add(Box.createVerticalStrut(SPACE_8));
        statusPanel.add(campaignWarRoomStatusLabel);
        statusPanel.add(Box.createVerticalStrut(SPACE_8));
        statusPanel.add(campaignWarRoomGoalLabel);

        JPanel actionsPanel = card("Council Actions");
        actionsPanel.setLayout(new GridLayout(5, 1, SPACE_8, SPACE_8));

        JButton tourBtn = primaryButton("Run Guided Tour");
        tourBtn.addActionListener(e -> handleCampaignResult(campaign.runGuidedTour()));
        JButton habitatBtn = secondaryButton("Upgrade Habitat");
        habitatBtn.addActionListener(e -> handleCampaignResult(campaign.upgradeHabitat()));
        JButton researchBtn = secondaryButton("Fund Research");
        researchBtn.addActionListener(e -> handleCampaignResult(campaign.fundResearch()));
        JButton rescueBtn = secondaryButton("Rescue Specimen");
        rescueBtn.addActionListener(e -> handleCampaignResult(campaign.rescueSpecimen()));
        JButton nextDayBtn = secondaryButton("Advance Day");
        nextDayBtn.addActionListener(e -> handleCampaignResult(campaign.nextDay()));

        actionsPanel.add(tourBtn);
        actionsPanel.add(habitatBtn);
        actionsPanel.add(researchBtn);
        actionsPanel.add(rescueBtn);
        actionsPanel.add(nextDayBtn);

        campaignNarrativeArea.setRows(12);
        if (campaignNarrativeArea.getText().isBlank()) {
            campaignNarrativeArea.setText("War room briefing initialized. Execute council actions to build reputation and stability.");
        }
        JScrollPane narrativeScroll = new JScrollPane(campaignNarrativeArea);
        narrativeScroll.setBorder(sectionBorder("Commander Chronicle"));

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, narrativeScroll, actionsPanel);
        split.setResizeWeight(0.72);
        split.setBorder(BorderFactory.createEmptyBorder());

        page.add(statusPanel, BorderLayout.NORTH);
        page.add(split, BorderLayout.CENTER);
        return page;
    }

    private JPanel buildChroniclePage() {
        JPanel page = wrapPanel(new JPanel(new BorderLayout(SPACE_24, SPACE_24)));

        JPanel ledger = card("Ranger Ledger");
        ledger.setLayout(new BoxLayout(ledger, BoxLayout.Y_AXIS));
        ledger.setPreferredSize(new Dimension(320, 420));

        JLabel ledgerTitle = new JLabel("Session Snapshot");
        ledgerTitle.setFont(FONT_H2);
        ledgerTitle.setForeground(MUTED_GRAY_900);

        chronicleSessionLabel.setFont(FONT_BODY_STRONG);
        chronicleSafariLabel.setFont(FONT_BODY_STRONG);
        chronicleFossilLabel.setFont(FONT_BODY_STRONG);
        chronicleCampaignLabel.setFont(FONT_CAPTION);
        chronicleCampaignLabel.setForeground(MUTED_GRAY_700);

        JButton clearBtn = secondaryButton("Clear Chronicle");
        clearBtn.addActionListener(e -> {
            feedArea.setText("");
            appendFeed("Chronicle reset by current ranger.");
        });

        JButton warRoomBtn = secondaryButton("Open War Room");
        warRoomBtn.addActionListener(e -> workspaceCards.show(workspaceContainer, "campaign"));

        ledger.add(ledgerTitle);
        ledger.add(Box.createVerticalStrut(SPACE_16));
        ledger.add(chronicleSessionLabel);
        ledger.add(Box.createVerticalStrut(SPACE_8));
        ledger.add(chronicleSafariLabel);
        ledger.add(Box.createVerticalStrut(SPACE_8));
        ledger.add(chronicleFossilLabel);
        ledger.add(Box.createVerticalStrut(SPACE_8));
        ledger.add(chronicleCampaignLabel);
        ledger.add(Box.createVerticalStrut(SPACE_16));
        ledger.add(clearBtn);
        ledger.add(Box.createVerticalStrut(SPACE_8));
        ledger.add(warRoomBtn);

        JScrollPane feedScroll = new JScrollPane(feedArea);
        feedScroll.setBorder(sectionBorder("Chronicle Feed"));
        feedScroll.getViewport().setBackground(SURFACE);

        page.add(ledger, BorderLayout.WEST);
        page.add(feedScroll, BorderLayout.CENTER);
        return page;
    }

    private JPanel buildFeedingPlannerCard() {
        JPanel card = card("Feeding Planner");
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JComboBox<DinosaurCatalog.DinosaurRecord> species = dinosaurCombo();
        JSpinner population = new JSpinner(new SpinnerNumberModel(2, 1, 200, 1));

        JLabel output = new JLabel("Estimated food: -");
        output.setFont(FONT_BODY_STRONG);

        JButton calculateBtn = primaryButton("Calculate Daily Food");
        calculateBtn.addActionListener(e -> {
            DinosaurCatalog.DinosaurRecord dinosaur = (DinosaurCatalog.DinosaurRecord) species.getSelectedItem();
            if (dinosaur == null) {
                return;
            }
            int count = (Integer) population.getValue();
            double foodKg = ParkOperations.dailyFoodKg(dinosaur, count);
            output.setText("Estimated food: " + formatOne(foodKg) + " kg/day");
            appendFeed("Feeding plan generated for " + count + " " + dinosaur.commonName() + ".");
        });

        card.add(labeledField("Species", species));
        card.add(Box.createVerticalStrut(SPACE_8));
        card.add(labeledField("Population", population));
        card.add(Box.createVerticalStrut(SPACE_16));
        card.add(calculateBtn);
        card.add(Box.createVerticalStrut(SPACE_16));
        card.add(output);

        return card;
    }

    private JPanel buildHabitatPlannerCard() {
        JPanel card = card("Habitat Planner");
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JComboBox<DinosaurCatalog.DinosaurRecord> species = dinosaurCombo();
        JSpinner population = new JSpinner(new SpinnerNumberModel(3, 1, 100, 1));

        JLabel output = new JLabel("Required habitat: -");
        output.setFont(FONT_BODY_STRONG);

        JButton calculateBtn = primaryButton("Compute Habitat Area");
        calculateBtn.addActionListener(e -> {
            DinosaurCatalog.DinosaurRecord dinosaur = (DinosaurCatalog.DinosaurRecord) species.getSelectedItem();
            if (dinosaur == null) {
                return;
            }
            int count = (Integer) population.getValue();
            double area = ParkOperations.habitatAreaSqMeters(dinosaur, count);
            output.setText("Required habitat: " + formatOne(area) + " m2");
            appendFeed("Habitat sizing completed for " + dinosaur.commonName() + " group of " + count + ".");
        });

        card.add(labeledField("Species", species));
        card.add(Box.createVerticalStrut(SPACE_8));
        card.add(labeledField("Population", population));
        card.add(Box.createVerticalStrut(SPACE_16));
        card.add(calculateBtn);
        card.add(Box.createVerticalStrut(SPACE_16));
        card.add(output);

        return card;
    }

    private JPanel buildRevenueSimulatorCard() {
        JPanel card = card("Revenue Simulator");
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JSpinner visitorSpinner = new JSpinner(new SpinnerNumberModel(1200, 0, 50000, 50));
        JTextField ticketField = new JTextField("59");
        JTextField fixedCostField = new JTextField("25000");
        JTextField variableCostField = new JTextField("12");

        JTextArea output = createOutputArea();
        output.setRows(4);

        JButton simulateBtn = primaryButton("Run Projection");
        simulateBtn.addActionListener(e -> {
            try {
                int visitors = (Integer) visitorSpinner.getValue();
                double ticket = parseDouble(ticketField, "Ticket price");
                double fixed = parseDouble(fixedCostField, "Fixed costs");
                double variable = parseDouble(variableCostField, "Variable cost per visitor");

                ParkOperations.RevenueResult result = ParkOperations.revenueProjection(visitors, ticket, fixed, variable);
                NumberFormat money = NumberFormat.getCurrencyInstance(Locale.US);

                output.setText("""
                        Gross Revenue: %s
                        Total Costs: %s
                        Net Revenue: %s
                        Margin: %s%%
                        """.formatted(
                        money.format(result.grossRevenue()),
                        money.format(result.totalCosts()),
                        money.format(result.netRevenue()),
                        formatOne(result.marginPercent())
                ));

                appendFeed("Revenue projection generated for " + visitors + " projected visitors.");
            } catch (IllegalArgumentException ex) {
                showInputError(ex.getMessage());
            }
        });

        card.add(labeledField("Visitors/day", visitorSpinner));
        card.add(Box.createVerticalStrut(SPACE_8));
        card.add(labeledField("Ticket price (USD)", ticketField));
        card.add(Box.createVerticalStrut(SPACE_8));
        card.add(labeledField("Fixed costs (USD)", fixedCostField));
        card.add(Box.createVerticalStrut(SPACE_8));
        card.add(labeledField("Variable per visitor", variableCostField));
        card.add(Box.createVerticalStrut(SPACE_16));
        card.add(simulateBtn);
        card.add(Box.createVerticalStrut(SPACE_16));
        card.add(new JScrollPane(output));

        return card;
    }

    private JPanel buildCompatibilityCard() {
        JPanel card = card("Species Compatibility");
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JComboBox<DinosaurCatalog.DinosaurRecord> first = dinosaurCombo();
        JComboBox<DinosaurCatalog.DinosaurRecord> second = dinosaurCombo();

        JTextArea output = createOutputArea();
        output.setRows(4);

        JButton compareBtn = primaryButton("Analyze Pair");
        compareBtn.addActionListener(e -> {
            DinosaurCatalog.DinosaurRecord a = (DinosaurCatalog.DinosaurRecord) first.getSelectedItem();
            DinosaurCatalog.DinosaurRecord b = (DinosaurCatalog.DinosaurRecord) second.getSelectedItem();
            if (a == null || b == null) {
                return;
            }

            ParkOperations.CompatibilityResult result = ParkOperations.compareSpecies(a, b);
            output.setText(result.summary());
            appendFeed("Compatibility analysis completed for " + a.commonName() + " and " + b.commonName() + ".");
            DinosaurCatalog.DinosaurRecord louder = a.dangerLevel() >= b.dangerLevel() ? a : b;
            DinosaurSoundSynthesizer.playSpeciesCall(louder);
        });

        card.add(labeledField("Species A", first));
        card.add(Box.createVerticalStrut(SPACE_8));
        card.add(labeledField("Species B", second));
        card.add(Box.createVerticalStrut(SPACE_16));
        card.add(compareBtn);
        card.add(Box.createVerticalStrut(SPACE_16));
        card.add(new JScrollPane(output));

        return card;
    }

    private JPanel buildQuizPanel() {
        JPanel panel = card("Dino Quiz Challenge");
        panel.setLayout(new BorderLayout(SPACE_16, SPACE_16));

        quizPromptLabel = new JLabel();
        quizPromptLabel.setFont(FONT_H1);

        quizChoices = new JComboBox<>();
        quizResultArea = createOutputArea();
        quizResultArea.setRows(5);

        JButton submitBtn = primaryButton("Submit Answer");
        submitBtn.addActionListener(e -> submitQuizAnswer());

        JButton nextBtn = secondaryButton("Next Question");
        nextBtn.addActionListener(e -> nextQuizQuestion());

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.add(quizPromptLabel);
        top.add(Box.createVerticalStrut(SPACE_8));
        top.add(quizChoices);

        JPanel actions = new JPanel(new GridLayout(1, 2, SPACE_16, SPACE_16));
        actions.setOpaque(false);
        actions.add(submitBtn);
        actions.add(nextBtn);

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(quizResultArea), BorderLayout.CENTER);
        panel.add(actions, BorderLayout.SOUTH);

        nextQuizQuestion();
        return panel;
    }

    private JPanel buildFossilDigPanel() {
        JPanel panel = card("Fossil Dig Simulator");
        panel.setLayout(new BorderLayout(SPACE_16, SPACE_16));

        JTextArea output = createOutputArea();
        output.setText("Start excavating to discover fossil quality and earn points.");

        JButton digBtn = primaryButton("Excavate Site");
        digBtn.addActionListener(e -> {
            int rarityRoll = random.nextInt(100);
            int points;
            String find;

            if (rarityRoll >= 95) {
                find = "Legendary find: nearly complete articulated skeleton";
                points = 30;
            } else if (rarityRoll >= 75) {
                find = "Rare find: partial skull with preserved teeth";
                points = 15;
            } else if (rarityRoll >= 45) {
                find = "Uncommon find: vertebrae segment";
                points = 8;
            } else {
                find = "Common find: isolated fragment";
                points = 3;
            }

            fossilPoints += points;
            fossilPointsLabel.setText("Fossil points: " + fossilPoints);
            syncChronicleLabels();
            output.setText(find + "\nPoints earned: +" + points + "\nTotal points: " + fossilPoints);
            appendFeed("Excavation update: " + find + " (+" + points + ")");
        });

        panel.add(new JScrollPane(output), BorderLayout.CENTER);
        panel.add(digBtn, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildSoundboardPanel() {
        JPanel panel = card("Dinosaur Soundboard");
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JComboBox<DinosaurCatalog.DinosaurRecord> species = dinosaurCombo();

        JButton playBtn = primaryButton("Play Species Call");
        playBtn.addActionListener(e -> {
            DinosaurCatalog.DinosaurRecord dino = (DinosaurCatalog.DinosaurRecord) species.getSelectedItem();
            if (dino == null) {
                return;
            }
            DinosaurSoundSynthesizer.playSpeciesCall(dino);
            appendFeed("Soundboard: " + dino.commonName() + " call triggered.");
        });

        panel.add(labeledField("Species", species));
        panel.add(Box.createVerticalStrut(SPACE_16));
        panel.add(playBtn);

        return panel;
    }

    private void refreshDinosaurTable() {
        if (searchField == null || periodFilter == null || dietFilter == null) {
            return;
        }

        String search = searchField.getText();
        String period = (String) periodFilter.getSelectedItem();
        String diet = (String) dietFilter.getSelectedItem();

        visibleDinosaurs.clear();
        visibleDinosaurs.addAll(ParkOperations.filterDinosaurs(period, diet, search));

        dinosaurTableModel.setRowCount(0);
        for (DinosaurCatalog.DinosaurRecord dino : visibleDinosaurs) {
            dinosaurTableModel.addRow(new Object[]{
                    dino.commonName(),
                    dino.scientificName(),
                    dino.period(),
                    dino.diet(),
                    formatOne(dino.lengthMeters()),
                    formatOne(dino.massKg()),
                    formatOne(dino.topSpeedKph()),
                    dino.region(),
                    dino.dangerLevel()
            });
        }
        dinosaurSorter.sort();

        if (!visibleDinosaurs.isEmpty()) {
            dinosaurTable.setRowSelectionInterval(0, 0);
            int modelIndex = dinosaurTable.convertRowIndexToModel(0);
            selectedDinosaur = visibleDinosaurs.get(modelIndex);
            updateExplorerDetails(selectedDinosaur);
        } else {
            selectedDinosaur = null;
            explorerImageLabel.setIcon(null);
            explorerDetailsArea.setText("No bestiary entries match the current filters.");
        }
    }

    private void updateExplorerDetails(DinosaurCatalog.DinosaurRecord dinosaur) {
        String details = """
                Common Name: %s
                Scientific Name: %s
                Period: %s
                Approx. Time: %s million years ago
                Diet: %s
                Length: %s m
                Mass: %s kg
                Top Speed: %s kph
                Region: %s
                First Described: %s
                Danger Level: %d / 10

                Fact:
                %s
                """.formatted(
                dinosaur.commonName(),
                dinosaur.scientificName(),
                dinosaur.period(),
                formatOne(dinosaur.timeframeMya()),
                dinosaur.diet(),
                formatOne(dinosaur.lengthMeters()),
                formatOne(dinosaur.massKg()),
                formatOne(dinosaur.topSpeedKph()),
                dinosaur.region(),
                dinosaur.firstDescribed(),
                dinosaur.dangerLevel(),
                dinosaur.fact()
        );

        explorerDetailsArea.setText(details);
        explorerDetailsArea.setCaretPosition(0);
        imageService.loadImageAsync(dinosaur.wikiTitle(), 320, explorerImageLabel::setIcon);
    }

    private void startSession() {
        String name = sessionNameField.getText().trim();
        if (name.isBlank()) {
            name = "Wanderer";
        }

        String role = (String) sessionRoleCombo.getSelectedItem();
        if (role == null) {
            role = "Ranger";
        }

        sessionLabel.setText("Session: " + name + " (" + role + ")");
        appendFeed("Session opened for " + name + " as " + role + ".");
        syncChronicleLabels();

        if ("Warden".equals(role)) {
            workspaceCards.show(workspaceContainer, "campaign");
        } else {
            workspaceCards.show(workspaceContainer, "explorer");
        }
    }

    private void triggerRandomEncounter() {
        DinosaurCatalog.DinosaurRecord randomDino = ParkOperations.randomDinosaur();
        selectedDinosaur = randomDino;
        safariScore += 5 + randomDino.dangerLevel();
        safariScoreLabel.setText("Scout score: " + safariScore);
        syncChronicleLabels();

        appendFeed("Encounter: " + randomDino.commonName() + " sighted in the " + randomDino.period()
                + " habitat corridor. +" + (5 + randomDino.dangerLevel()) + " scout points.");

        workspaceCards.show(workspaceContainer, "explorer");
        updateExplorerDetails(randomDino);
        DinosaurSoundSynthesizer.playSpeciesCall(randomDino);
    }

    private void nextQuizQuestion() {
        currentQuiz = ParkOperations.generateQuizQuestion(random);
        quizPromptLabel.setText(currentQuiz.prompt());
        quizChoices.setModel(new DefaultComboBoxModel<>(currentQuiz.options().toArray(new String[0])));
        quizResultArea.setText("Select an answer and submit.");
    }

    private void submitQuizAnswer() {
        if (currentQuiz == null) {
            return;
        }

        int selectedIndex = quizChoices.getSelectedIndex();
        boolean correct = selectedIndex == currentQuiz.correctIndex();

        if (correct) {
            safariScore += 10;
            safariScoreLabel.setText("Scout score: " + safariScore);
            quizResultArea.setText("Correct.\n" + currentQuiz.explanation() + "\n+10 scout points.");
            appendFeed("Quiz success: " + currentQuiz.prompt());
            syncChronicleLabels();
        } else {
            String correctAnswer = currentQuiz.options().get(currentQuiz.correctIndex());
            quizResultArea.setText("Not quite. Correct answer: " + correctAnswer + "\n" + currentQuiz.explanation());
            appendFeed("Quiz miss: " + currentQuiz.prompt());
        }
    }

    private void handleCampaignResult(CampaignEngine.CampaignResult result) {
        if (result == null) {
            return;
        }

        appendFeed("Campaign: " + result.title() + " | " + result.detail());
        appendCampaignNarrative(result.title() + " -> " + result.detail());
        if (!result.success()) {
            showInputError(result.detail());
        }

        if (result.milestoneReached()) {
            appendFeed("Campaign goal achieved. Operations are in a stable winning state.");
        }

        updateCampaignDisplay();
    }

    private void updateCampaignDisplay() {
        String status = campaign.statusSummary();
        String goals = campaign.goalsSummary();

        campaignStatusLabel.setText(status);
        campaignGoalLabel.setText(goals);
        campaignWarRoomStatusLabel.setText(status);
        campaignWarRoomGoalLabel.setText(goals);
        chronicleCampaignLabel.setText(status);
        syncChronicleLabels();
    }

    private void appendCampaignNarrative(String line) {
        campaignNarrativeArea.append("[" + LocalDateTime.now().toLocalTime().withNano(0) + "] " + line + "\n\n");
        campaignNarrativeArea.setCaretPosition(campaignNarrativeArea.getDocument().getLength());
    }

    private void syncChronicleLabels() {
        chronicleSessionLabel.setText(sessionLabel.getText());
        chronicleSafariLabel.setText(safariScoreLabel.getText());
        chronicleFossilLabel.setText(fossilPointsLabel.getText());
    }

    private void openWikipediaForSelection() {
        if (selectedDinosaur == null) {
            showInputError("Select a dinosaur first.");
            return;
        }

        try {
            String page = URLEncoder.encode(selectedDinosaur.wikiTitle(), StandardCharsets.UTF_8)
                    .replace("+", "_");
            URI uri = URI.create("https://en.wikipedia.org/wiki/" + page);
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(uri);
            }
        } catch (Exception ex) {
            showInputError("Could not open browser for this species.");
        }
    }

    private JPanel statCard(String title, String value, String subtitle) {
        JPanel card = card(title);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        Color accent = statAccent(title);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(FONT_H1);
        valueLabel.setForeground(accent);

        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(FONT_CAPTION);
        subtitleLabel.setForeground(MUTED_GRAY_700);

        card.add(valueLabel);
        card.add(Box.createVerticalStrut(SPACE_8));
        card.add(subtitleLabel);

        return card;
    }

    private Color statAccent(String title) {
        String key = title.toLowerCase(Locale.US);
        if (key.contains("length")) {
            return new Color(118, 221, 190);
        }
        if (key.contains("heaviest")) {
            return new Color(243, 186, 94);
        }
        if (key.contains("danger")) {
            return new Color(236, 134, 116);
        }
        return new Color(110, 174, 255);
    }

    private JPanel card(String title) {
        JPanel panel = new JPanel();
        panel.setBackground(SURFACE_ELEVATED);
        panel.setBorder(BorderFactory.createCompoundBorder(
                sectionBorder(title),
                new EmptyBorder(SPACE_16, SPACE_16, SPACE_16, SPACE_16)
        ));
        return panel;
    }

    private TitledBorder sectionBorder(String title) {
        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(73, 93, 141), 1),
                title
        );
        border.setTitleFont(FONT_CAPTION.deriveFont(Font.BOLD));
        border.setTitleColor(new Color(218, 228, 255));
        return border;
    }

    private JLabel statusDot(Color color) {
        JLabel dot = new JLabel("\u25CF");
        dot.setForeground(color);
        dot.setFont(FONT_CAPTION.deriveFont(Font.BOLD));
        return dot;
    }

    private JButton headerNavButton(String text, String pageKey) {
        JButton button = secondaryButton(text);
        button.setFont(FONT_CAPTION.deriveFont(Font.BOLD));
        button.addActionListener(e -> workspaceCards.show(workspaceContainer, pageKey));
        return button;
    }

    private JPanel wrapPanel(Component component) {
        if (component instanceof JPanel panel) {
            panel.setBackground(NEUTRAL_BACKGROUND);
            return panel;
        }

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(NEUTRAL_BACKGROUND);
        if (component != null) {
            wrapper.add(component, BorderLayout.CENTER);
        }
        return wrapper;
    }

    private void styleTabs(JTabbedPane tabs) {
        tabs.setFont(FONT_BODY_STRONG);
        tabs.setBackground(SURFACE);
        tabs.setForeground(MUTED_GRAY_900);
        tabs.setBorder(BorderFactory.createLineBorder(MUTED_GRAY_300, 1));
    }

    private JComboBox<DinosaurCatalog.DinosaurRecord> dinosaurCombo() {
        JComboBox<DinosaurCatalog.DinosaurRecord> combo = new JComboBox<>(
                new DefaultComboBoxModel<>(database.all().toArray(new DinosaurCatalog.DinosaurRecord[0]))
        );
        combo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        return combo;
    }

    private JPanel labeledField(String label, JComponent field) {
        return labeledField(label, field, MUTED_GRAY_900);
    }

    private JPanel labeledField(String label, JComponent field, Color labelColor) {
        styleInput(field);
        JPanel panel = new JPanel(new BorderLayout(SPACE_8, SPACE_8));
        panel.setOpaque(false);
        JLabel name = new JLabel(label);
        name.setFont(FONT_CAPTION.deriveFont(Font.BOLD));
        name.setForeground(labelColor);
        panel.add(name, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private void styleInput(JComponent field) {
        field.setFont(FONT_BODY);
        if (field instanceof JTextField textField) {
            textField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(MUTED_GRAY_300, 1),
                    new EmptyBorder(SPACE_8, SPACE_8, SPACE_8, SPACE_8)
            ));
            textField.setCaretColor(MUTED_GRAY_900);
            textField.setBackground(new Color(15, 23, 43));
            textField.setForeground(MUTED_GRAY_900);
        } else if (field instanceof JComboBox<?> comboBox) {
            comboBox.setBackground(new Color(15, 23, 43));
            comboBox.setForeground(MUTED_GRAY_900);
            comboBox.setBorder(BorderFactory.createLineBorder(MUTED_GRAY_300, 1));
        } else if (field instanceof JSpinner spinner) {
            spinner.setBackground(new Color(15, 23, 43));
            spinner.setForeground(MUTED_GRAY_900);
            spinner.setBorder(BorderFactory.createLineBorder(MUTED_GRAY_300, 1));
        }
    }

    private DefaultTableModel createDinosaurModel() {
        return new DefaultTableModel(new Object[]{
                "Common", "Scientific", "Period", "Diet", "Length (m)", "Mass (kg)", "Speed (kph)", "Region", "Danger"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private JButton primaryButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(ACCENT);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(FONT_BODY_STRONG);
        button.setOpaque(true);
        button.setBorderPainted(true);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(119, 153, 230), 1),
                new EmptyBorder(SPACE_8, SPACE_16, SPACE_8, SPACE_16)
        ));
        button.setContentAreaFilled(true);
        button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        return button;
    }

    private JButton secondaryButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(44, 57, 92));
        button.setForeground(MUTED_GRAY_900);
        button.setFocusPainted(false);
        button.setFont(FONT_BODY_STRONG);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(83, 102, 148), 1),
                new EmptyBorder(SPACE_8, SPACE_16, SPACE_8, SPACE_16)
        ));
        button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        return button;
    }

    private static JTextArea createOutputArea() {
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(FONT_BODY);
        area.setMargin(new Insets(SPACE_16, SPACE_16, SPACE_16, SPACE_16));
        area.setColumns(68);
        area.setBackground(new Color(18, 25, 46));
        area.setForeground(MUTED_GRAY_900);
        area.setCaretColor(MUTED_GRAY_900);
        area.setBorder(BorderFactory.createEmptyBorder());
        return area;
    }

    private void appendFeed(String message) {
        feedArea.append("[" + LocalDateTime.now().toLocalTime().withNano(0) + "] " + message + "\n\n");
        feedArea.setCaretPosition(feedArea.getDocument().getLength());
    }

    private void showInputError(String message) {
        JOptionPane.showMessageDialog(this, message, "Input Error", JOptionPane.ERROR_MESSAGE);
    }

    private double parseDouble(JTextField field, String label) {
        String value = field.getText().trim();
        if (value.isBlank()) {
            throw new IllegalArgumentException(label + " is required.");
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(label + " must be numeric.");
        }
    }

    private void startClock() {
        Timer timer = new Timer(1000, e -> clockLabel.setText(ParkOperations.currentDateTime()));
        timer.setRepeats(true);
        timer.start();
    }

    private String formatOne(double value) {
        return String.format(Locale.US, "%.1f", value);
    }

    private static final class GradientBackgroundPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            GradientPaint bg = new GradientPaint(
                    0,
                    0,
                    new Color(7, 12, 25),
                    getWidth(),
                    getHeight(),
                    new Color(16, 24, 46)
            );
            g2.setPaint(bg);
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.setColor(new Color(78, 108, 182, 40));
            g2.fillOval((int) (getWidth() * 0.45), (int) (getHeight() * 0.18), 420, 260);
            g2.setColor(new Color(212, 130, 87, 30));
            g2.fillOval((int) (getWidth() * 0.66), (int) (getHeight() * 0.48), 380, 220);
            g2.dispose();
        }
    }
}
