package Views;

import Controllers.GameController;
import Models.CoordinatesModel;
import Models.PlayerModel;

import javax.sound.sampled.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;

public class GameWindow {
    public static void main(String args[]){
        // Set look and feel for better appearance
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Show theme selection dialog
        GameTheme selectedTheme = showThemeSelection();
        if (selectedTheme == null) {
            System.exit(0); // User closed dialog
            return;
        }

        JFrame frame = new JFrame(selectedTheme.getWindowTitle());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 750);
        GameWindowPanel gwp = new GameWindowPanel(selectedTheme);
        frame.add(gwp);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    static GameTheme showThemeSelection() {
        JDialog dialog = new JDialog((Frame)null, "Choose Your Theme", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(new Color(25, 55, 109));
        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        // Title
        JLabel titleLabel = new JLabel("SELECT YOUR BATTLESHIP THEME", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Theme buttons panel
        JPanel themesPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        themesPanel.setOpaque(false);

        final GameTheme[] selectedTheme = {null};

        // Military Theme Button
        JPanel militaryPanel = createThemeButton(
                "MILITARY",
                "Classic naval warfare",
                "Navy colors",
                "Military terminology",
                "Professional look",
                new Color(25, 55, 109),
                () -> {
                    selectedTheme[0] = GameTheme.MILITARY;
                    dialog.dispose();
                }
        );

        // Pirate Theme Button
        JPanel piratePanel = createThemeButton(
                "PIRATE",
                "Swashbuckling adventure",
                "Treasure map colors",
                "Pirate terminology",
                "Adventure style",
                new Color(139, 69, 19),
                () -> {
                    selectedTheme[0] = GameTheme.PIRATE;
                    dialog.dispose();
                }
        );

        themesPanel.add(militaryPanel);
        themesPanel.add(piratePanel);
        mainPanel.add(themesPanel, BorderLayout.CENTER);

        dialog.add(mainPanel);
        dialog.setVisible(true);

        return selectedTheme[0];
    }

    static JPanel createThemeButton(String title, String subtitle, String feature1,
                                    String feature2, String feature3, Color color,
                                    Runnable onClick) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(color);
        panel.setBorder(new CompoundBorder(
                new LineBorder(color.brighter(), 3),
                new EmptyBorder(20, 15, 20, 15)
        ));
        panel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        subtitleLabel.setForeground(new Color(255, 255, 255, 200));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(5));
        contentPanel.add(subtitleLabel);
        contentPanel.add(Box.createVerticalStrut(20));

        JLabel f1 = new JLabel(feature1);
        JLabel f2 = new JLabel(feature2);
        JLabel f3 = new JLabel(feature3);

        for (JLabel label : new JLabel[]{f1, f2, f3}) {
            label.setFont(new Font("Arial", Font.PLAIN, 11));
            label.setForeground(Color.WHITE);
            label.setAlignmentX(Component.CENTER_ALIGNMENT);
            contentPanel.add(label);
            contentPanel.add(Box.createVerticalStrut(5));
        }

        panel.add(contentPanel, BorderLayout.CENTER);

        // Hover effect
        panel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                panel.setBackground(color.brighter());
                panel.setBorder(new CompoundBorder(
                        new LineBorder(Color.YELLOW, 3),
                        new EmptyBorder(20, 15, 20, 15)
                ));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                panel.setBackground(color);
                panel.setBorder(new CompoundBorder(
                        new LineBorder(color.brighter(), 3),
                        new EmptyBorder(20, 15, 20, 15)
                ));
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                onClick.run();
            }
        });

        return panel;
    }
}

enum GameTheme {
    MILITARY("Battleship - Military", "", "", "", "Fleet", "Enemy Waters",
            "Ship", "Attack", "Mission", "Vessel", "Deploy",
            new Color(25, 55, 109), new Color(64, 145, 191),
            new Color(173, 216, 230), new Color(255, 248, 220)),

    PIRATE("Battleship - Pirate Seas", "", "", "", "Crew", "Enemy Territory",
            "Ship", "Fire Cannons", "Raid", "Vessel", "Position",
            new Color(101, 67, 33), new Color(139, 90, 43),
            new Color(70, 130, 180), new Color(245, 222, 179));

    private final String windowTitle;
    private final String fleetIcon;
    private final String targetIcon;
    private final String hitIcon;
    private final String fleetWord;
    private final String enemyWatersWord;
    private final String shipWord;
    private final String attackWord;
    private final String missionWord;
    private final String vesselWord;
    private final String deployWord;
    private final Color navyBlue;
    private final Color oceanBlue;
    private final Color lightBlue;
    private final Color sand;

    GameTheme(String windowTitle, String fleetIcon, String targetIcon, String hitIcon,
              String fleetWord, String enemyWatersWord, String shipWord, String attackWord,
              String missionWord, String vesselWord, String deployWord,
              Color navyBlue, Color oceanBlue, Color lightBlue, Color sand) {
        this.windowTitle = windowTitle;
        this.fleetIcon = fleetIcon;
        this.targetIcon = targetIcon;
        this.hitIcon = hitIcon;
        this.fleetWord = fleetWord;
        this.enemyWatersWord = enemyWatersWord;
        this.shipWord = shipWord;
        this.attackWord = attackWord;
        this.missionWord = missionWord;
        this.vesselWord = vesselWord;
        this.deployWord = deployWord;
        this.navyBlue = navyBlue;
        this.oceanBlue = oceanBlue;
        this.lightBlue = lightBlue;
        this.sand = sand;
    }

    public String getWindowTitle() { return windowTitle; }
    public String getFleetIcon() { return fleetIcon; }
    public String getTargetIcon() { return targetIcon; }
    public String getHitIcon() { return hitIcon; }
    public String getFleetWord() { return fleetWord; }
    public String getEnemyWatersWord() { return enemyWatersWord; }
    public String getShipWord() { return shipWord; }
    public String getAttackWord() { return attackWord; }
    public String getMissionWord() { return missionWord; }
    public String getVesselWord() { return vesselWord; }
    public String getDeployWord() { return deployWord; }
    public Color getNavyBlue() { return navyBlue; }
    public Color getOceanBlue() { return oceanBlue; }
    public Color getLightBlue() { return lightBlue; }
    public Color getSand() { return sand; }
}

class GameWindowPanel extends JPanel implements ActionListener, ItemListener {

    private GameController gameController;
    private GameTheme theme;

    private BoardViewPanel setupBoardPanel;
    private JPanel setupControlPanel;
    private JButton placeShipBtn;

    // Ship indicator labels
    private JLabel shipNameLabel;
    private JLabel shipLengthLabel;

    private JPanel battlePanel;
    private BoardViewPanel playerBoardPanel;
    private BoardViewPanel targetBoardPanel;

    private JLabel phaseLabel = new JLabel();
    private JLabel currentPlayerLabel = new JLabel();
    private JLabel shipsPlacedLabel = new JLabel();
    private JLabel messageLabel;

    private final int[] shipLengths = {5, 4, 3, 3, 2};
    private final String[] shipNames = {"Carrier (5)", "Battleship (4)", "Cruiser (3)", "Submarine (3)", "Destroyer (2)"};
    private int currentShipIndex = 0;

    private boolean attackAnimating = false;

    // Color scheme - now from theme
    private Color NAVY_BLUE;
    private Color OCEAN_BLUE;
    private Color LIGHT_BLUE;
    private Color SAND;
    private static final Color DARK_RED = new Color(139, 0, 0);
    private static final Color SUCCESS_GREEN = new Color(46, 125, 50);

    GameWindowPanel(GameTheme theme){
        this.theme = theme;

        // Set colors from theme
        this.NAVY_BLUE = theme.getNavyBlue();
        this.OCEAN_BLUE = theme.getOceanBlue();
        this.LIGHT_BLUE = theme.getLightBlue();
        this.SAND = theme.getSand();

        this.messageLabel = new JLabel("Select cells on the board and click 'Place " + theme.getShipWord() + "' to position your fleet.");

        setLayout(new BorderLayout(10, 10));
        setBackground(SAND);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        gameController = new GameController();

        // Create status panel with modern styling
        JPanel statusPanel = createStatusPanel();
        add(statusPanel, BorderLayout.NORTH);

        // Setup board panel
        setupBoardPanel = new BoardViewPanel();
        setupBoardPanel.setMode(BoardViewPanel.BoardMode.SETUP_SELECTION);
        setupBoardPanel.setBorder(createTitledBorder("Your Fleet Placement"));
        add(setupBoardPanel, BorderLayout.CENTER);

        // Setup control panel
        setupControlPanel = createSetupControlPanel();
        add(setupControlPanel, BorderLayout.SOUTH);

        updateStatusLabels();
        updateShipInstructions();
    }

    private JPanel createStatusPanel() {
        JPanel statusPanel = new JPanel(new BorderLayout(10, 10));
        statusPanel.setBackground(NAVY_BLUE);
        statusPanel.setBorder(new CompoundBorder(
                new LineBorder(OCEAN_BLUE, 2),
                new EmptyBorder(15, 20, 15, 20)
        ));

        // Top info row
        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        topRow.setBackground(NAVY_BLUE);

        addStatusLabel(topRow, "Phase:", phaseLabel);
        addStatusLabel(topRow, "Current Player:", currentPlayerLabel);
        addStatusLabel(topRow, "Ships Placed:", shipsPlacedLabel);

        // Add restart button to top row
        JButton restartBtn = new JButton("Restart Game");
        restartBtn.setFont(new Font("Arial", Font.BOLD, 11));
        restartBtn.setBackground(new Color(211, 47, 47));
        restartBtn.setForeground(Color.WHITE);
        restartBtn.setFocusPainted(false);
        restartBtn.setBorder(new CompoundBorder(
                new LineBorder(new Color(211, 47, 47).darker(), 2),
                new EmptyBorder(5, 15, 5, 15)
        ));
        restartBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        restartBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to restart?\nCurrent game progress will be lost.",
                    "Confirm Restart",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );
            if (confirm == JOptionPane.YES_OPTION) {
                restartGame();
            }
        });

        topRow.add(Box.createHorizontalStrut(20));
        topRow.add(restartBtn);

        // Message row
        JPanel bottomRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        bottomRow.setBackground(NAVY_BLUE);

        messageLabel.setFont(new Font("Arial", Font.BOLD, 14));
        messageLabel.setForeground(Color.WHITE);
        bottomRow.add(messageLabel);

        statusPanel.add(topRow, BorderLayout.NORTH);
        statusPanel.add(bottomRow, BorderLayout.CENTER);

        return statusPanel;
    }

    private void addStatusLabel(JPanel panel, String labelText, JLabel valueLabel) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.BOLD, 13));
        label.setForeground(LIGHT_BLUE);

        valueLabel.setFont(new Font("Arial", Font.BOLD, 13));
        valueLabel.setForeground(Color.WHITE);

        panel.add(label);
        panel.add(valueLabel);
    }

    private JPanel createSetupControlPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(SAND);
        panel.setBorder(new EmptyBorder(10, 0, 0, 0));

        // Ship indicator panel - shows current ship being placed
        JPanel shipIndicatorPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        shipIndicatorPanel.setBackground(SAND);
        shipIndicatorPanel.setBorder(new CompoundBorder(
                new LineBorder(OCEAN_BLUE, 3),
                new EmptyBorder(15, 20, 15, 20)
        ));

        JLabel currentShipLabel = new JLabel("NOW PLACING:");
        currentShipLabel.setFont(new Font("Arial", Font.BOLD, 14));
        currentShipLabel.setForeground(NAVY_BLUE);

        shipNameLabel = new JLabel(shipNames[0].split(" \\(")[0]);
        shipNameLabel.setFont(new Font("Arial", Font.BOLD, 20));
        shipNameLabel.setForeground(new Color(211, 47, 47));

        shipLengthLabel = new JLabel("(Select " + shipLengths[0] + " cells)");
        shipLengthLabel.setFont(new Font("Arial", Font.BOLD, 16));
        shipLengthLabel.setForeground(NAVY_BLUE);

        shipIndicatorPanel.add(currentShipLabel);
        shipIndicatorPanel.add(shipNameLabel);
        shipIndicatorPanel.add(shipLengthLabel);

        // Instructions panel
        JPanel instructionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        instructionsPanel.setBackground(SAND);

        JLabel instructionLabel = new JLabel("Click cells to select, then place your ship →");
        instructionLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        instructionLabel.setForeground(NAVY_BLUE);
        instructionsPanel.add(instructionLabel);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttonPanel.setBackground(SAND);

        placeShipBtn = new JButton("Place " + theme.getShipWord());
        styleButton(placeShipBtn, SUCCESS_GREEN);
        placeShipBtn.addActionListener(e -> onPlaceShip());

        JButton clearBtn = new JButton("Clear Selection");
        styleButton(clearBtn, new Color(211, 47, 47));
        clearBtn.addActionListener(e -> {
            setupBoardPanel.clearSelection();
            messageLabel.setText("Selection cleared. Select cells for your next " + theme.getShipWord().toLowerCase() + ".");
        });

        JButton randomBtn = new JButton("Random " + theme.getShipWord() + " Placement");
        styleButton(randomBtn, new Color(103, 58, 183)); // Purple color
        randomBtn.addActionListener(e -> onRandomPlacement());

        buttonPanel.add(placeShipBtn);
        buttonPanel.add(clearBtn);
        buttonPanel.add(randomBtn);

        // Combine all panels
        JPanel topSection = new JPanel(new BorderLayout(5, 5));
        topSection.setBackground(SAND);
        topSection.add(shipIndicatorPanel, BorderLayout.NORTH);
        topSection.add(instructionsPanel, BorderLayout.CENTER);

        panel.add(topSection, BorderLayout.NORTH);
        panel.add(buttonPanel, BorderLayout.CENTER);

        return panel;
    }

    private void styleButton(JButton button, Color bgColor) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(new CompoundBorder(
                new LineBorder(bgColor.darker(), 2),
                new EmptyBorder(10, 20, 10, 20)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
    }

    private TitledBorder createTitledBorder(String title) {
        TitledBorder border = BorderFactory.createTitledBorder(
                new LineBorder(OCEAN_BLUE, 2),
                title,
                TitledBorder.CENTER,
                TitledBorder.TOP
        );
        border.setTitleFont(new Font("Arial", Font.BOLD, 16));
        border.setTitleColor(NAVY_BLUE);
        return border;
    }

    private void updateShipInstructions() {
        if (currentShipIndex < shipNames.length) {
            String shipName = shipNames[currentShipIndex];
            messageLabel.setText("Place your " + shipName + " - select " +
                    shipLengths[currentShipIndex] + " cells in a row or column.");

            // Update the ship indicator panel
            if (shipNameLabel != null) {
                shipNameLabel.setText(shipName.split(" \\(")[0]); // Just the name without length
                shipLengthLabel.setText("(Select " + shipLengths[currentShipIndex] + " cells)");
            }
        }
    }

    private void onPlaceShip() {
        List<CoordinatesModel> selected = new ArrayList<>(setupBoardPanel.selectedCoordinates);
        if (selected.isEmpty()) {
            showStyledDialog(
                    "No cells selected. Click on cells to select them first.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (currentShipIndex >= shipLengths.length) {
            showStyledDialog(
                    "All ships for this player are already placed.",
                    "Fleet Complete",
                    JOptionPane.INFORMATION_MESSAGE
            );
            setupBoardPanel.clearSelection();
            return;
        }

        int requiredLength = shipLengths[currentShipIndex];
        if (selected.size() != requiredLength) {
            showStyledDialog(
                    "You selected " + selected.size() + " cell(s).\n" +
                            "The " + shipNames[currentShipIndex] + " requires exactly " + requiredLength + " cells.",
                    "Incorrect Ship Length",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        PlayerModel before = gameController.getCurrentPlayer();
        boolean success = gameController.placeShip(selected);

        if (success) {
            setupBoardPanel.markPlacedShip(selected);
            setupBoardPanel.clearSelection();

            currentShipIndex++;

            int shipsPlaced = before.getShips().size();
            int requiredShips = before.getRequiredShips();
            shipsPlacedLabel.setText(Math.min(shipsPlaced, requiredShips) + "/" + requiredShips);

            if (gameController.getCurrentPhase() == GameController.GamePhase.SETUP &&
                    gameController.getCurrentPlayer() != before) {

                showStyledDialog(
                        "Fleet complete for " + getPlayerName(before) + "!\n\n" +
                                "Pass the device to " + getPlayerName(gameController.getCurrentPlayer()) + " to place their ships.",
                        "Switch Players",
                        JOptionPane.INFORMATION_MESSAGE
                );

                currentShipIndex = 0;

                remove(setupBoardPanel);
                setupBoardPanel = new BoardViewPanel();
                setupBoardPanel.setMode(BoardViewPanel.BoardMode.SETUP_SELECTION);
                setupBoardPanel.setBorder(createTitledBorder("Your Fleet Placement"));
                add(setupBoardPanel, BorderLayout.CENTER);

                // Recreate setup control panel for new player
                remove(setupControlPanel);
                setupControlPanel = createSetupControlPanel();
                add(setupControlPanel, BorderLayout.SOUTH);
            }

            if (gameController.getCurrentPhase() == GameController.GamePhase.PLAYER1_TURN) {
                enterBattleMode();
            } else {
                updateShipInstructions();
            }
        } else {
            showStyledDialog(
                    "Invalid ship placement!\n\n" +
                            "- Cells must be in a straight line (horizontal or vertical)\n" +
                            "- Cells must be adjacent (no gaps)\n" +
                            "- Cannot overlap with existing ships\n" +
                            "- Must be within the board boundaries",
                    "Invalid Placement",
                    JOptionPane.ERROR_MESSAGE
            );
        }

        updateStatusLabels();
        revalidate();
        repaint();
    }

    private void onRandomPlacement() {
        setupBoardPanel.clearSelection();

        PlayerModel currentPlayer = gameController.getCurrentPlayer();
        int shipsToPlace = 5 - currentPlayer.getShips().size();

        if (shipsToPlace == 0) {
            showStyledDialog(
                    "All ships are already placed!",
                    "Fleet Complete",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        messageLabel.setText("Randomly placing " + shipsToPlace + " " + theme.getShipWord().toLowerCase() + "(s)...");

        // Use SwingWorker to show progress without freezing UI
        SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() throws Exception {
                java.util.Random random = new java.util.Random();

                while (currentShipIndex < shipLengths.length) {
                    int shipLength = shipLengths[currentShipIndex];
                    boolean placed = false;
                    int attempts = 0;
                    int maxAttempts = 1000;

                    while (!placed && attempts < maxAttempts) {
                        attempts++;

                        // Random orientation (0 = horizontal, 1 = vertical)
                        boolean horizontal = random.nextBoolean();

                        // Random start position
                        int startRow, startCol;
                        if (horizontal) {
                            startRow = random.nextInt(10);
                            startCol = random.nextInt(10 - shipLength + 1);
                        } else {
                            startRow = random.nextInt(10 - shipLength + 1);
                            startCol = random.nextInt(10);
                        }

                        // Generate positions
                        List<CoordinatesModel> positions = new ArrayList<>();
                        for (int i = 0; i < shipLength; i++) {
                            if (horizontal) {
                                positions.add(new CoordinatesModel(startRow, startCol + i));
                            } else {
                                positions.add(new CoordinatesModel(startRow + i, startCol));
                            }
                        }

                        // Try to place the ship
                        placed = gameController.placeShip(positions);

                        if (placed) {
                            // Update UI on EDT
                            final List<CoordinatesModel> finalPositions = new ArrayList<>(positions);
                            javax.swing.SwingUtilities.invokeLater(() -> {
                                setupBoardPanel.markPlacedShip(finalPositions);
                                currentShipIndex++;
                                updateShipInstructions();
                                int shipsPlaced = currentPlayer.getShips().size();
                                shipsPlacedLabel.setText(shipsPlaced + "/5");
                            });

                            // Small delay to show placement animation
                            Thread.sleep(300);
                        }
                    }

                    if (!placed) {
                        publish("Failed to place ship after " + maxAttempts + " attempts. Try clearing the board.");
                        return null;
                    }
                }

                return null;
            }

            @Override
            protected void process(java.util.List<String> chunks) {
                for (String msg : chunks) {
                    messageLabel.setText(msg);
                }
            }

            @Override
            protected void done() {
                try {
                    get(); // Check for exceptions

                    if (currentPlayer.allShipsPlaced()) {
                        messageLabel.setText("All " + theme.getShipWord().toLowerCase() + "s randomly placed!");

                        // Check if we need to switch players or start battle
                        if (gameController.getCurrentPhase() == GameController.GamePhase.SETUP &&
                                gameController.getCurrentPlayer() != currentPlayer) {

                            Timer switchTimer = new Timer(1000, e -> {
                                showStyledDialog(
                                        "Fleet complete for " + getPlayerName(currentPlayer) + "!\n\n" +
                                                "Pass the device to " + getPlayerName(gameController.getCurrentPlayer()) + " to place their ships.",
                                        "Switch Players",
                                        JOptionPane.INFORMATION_MESSAGE
                                );

                                currentShipIndex = 0;

                                remove(setupBoardPanel);
                                setupBoardPanel = new BoardViewPanel();
                                setupBoardPanel.setMode(BoardViewPanel.BoardMode.SETUP_SELECTION);
                                setupBoardPanel.setBorder(createTitledBorder("Your Fleet Placement"));
                                add(setupBoardPanel, BorderLayout.CENTER);

                                remove(setupControlPanel);
                                setupControlPanel = createSetupControlPanel();
                                add(setupControlPanel, BorderLayout.SOUTH);

                                updateStatusLabels();
                                revalidate();
                                repaint();
                            });
                            switchTimer.setRepeats(false);
                            switchTimer.start();

                        } else if (gameController.getCurrentPhase() == GameController.GamePhase.PLAYER1_TURN) {
                            Timer battleTimer = new Timer(1000, e -> {
                                enterBattleMode();
                            });
                            battleTimer.setRepeats(false);
                            battleTimer.start();
                        }
                    }
                } catch (Exception e) {
                    messageLabel.setText(" Error during random placement: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };

        worker.execute();
    }

    private void enterBattleMode() {
        remove(setupBoardPanel);

        battlePanel = new JPanel(new GridLayout(1, 2, 20, 10));
        battlePanel.setBackground(SAND);

        // Create panels with labels for each board
        JPanel playerSection = new JPanel(new BorderLayout(0, 10));
        playerSection.setBackground(SAND);

        JPanel opponentSection = new JPanel(new BorderLayout(0, 10));
        opponentSection.setBackground(SAND);

        // Create clear labels
        JLabel playerLabel = new JLabel("YOUR SHIPS", SwingConstants.CENTER);
        playerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        playerLabel.setForeground(NAVY_BLUE);
        playerLabel.setBackground(SAND);
        playerLabel.setOpaque(true);
        playerLabel.setBorder(new CompoundBorder(
                new LineBorder(SUCCESS_GREEN, 2),
                new EmptyBorder(8, 10, 8, 10)
        ));

        JLabel opponentLabel = new JLabel("ENEMY TARGETS - CLICK TO ATTACK", SwingConstants.CENTER);
        opponentLabel.setFont(new Font("Arial", Font.BOLD, 18));
        opponentLabel.setForeground(DARK_RED);
        opponentLabel.setBackground(SAND);
        opponentLabel.setOpaque(true);
        opponentLabel.setBorder(new CompoundBorder(
                new LineBorder(DARK_RED, 2),
                new EmptyBorder(8, 10, 8, 10)
        ));

        playerBoardPanel = new BoardViewPanel();
        targetBoardPanel = new BoardViewPanel();

        playerBoardPanel.setMode(BoardViewPanel.BoardMode.VIEW_ONLY);
        targetBoardPanel.setMode(BoardViewPanel.BoardMode.BATTLE_TARGET);

        playerBoardPanel.selectedCoordinates.clear();
        targetBoardPanel.selectedCoordinates.clear();

        targetBoardPanel.setAttackListener(new AttackListener() {
            @Override
            public void onAttack(int row, int col) {
                handleAttack(row, col);
            }
        });

        playerBoardPanel.setBorder(createTitledBorder("Your " + theme.getFleetWord()));
        targetBoardPanel.setBorder(createTitledBorder(theme.getEnemyWatersWord()));

        // Add labels and boards to sections
        playerSection.add(playerLabel, BorderLayout.NORTH);
        playerSection.add(playerBoardPanel, BorderLayout.CENTER);

        opponentSection.add(opponentLabel, BorderLayout.NORTH);
        opponentSection.add(targetBoardPanel, BorderLayout.CENTER);

        battlePanel.add(playerSection);
        battlePanel.add(opponentSection);

        add(battlePanel, BorderLayout.CENTER);

        setupControlPanel.setVisible(false);

        messageLabel.setText("Battle started! " + getPlayerName(gameController.getCurrentPlayer()) +
                " fires first. Click the enemy board to " + theme.getAttackWord().toLowerCase() + "!");

        refreshBattleBoards();

        revalidate();
        repaint();

        showStyledDialog(
                "Both fleets are deployed!\n\n" +
                        "The " + theme.getMissionWord().toLowerCase() + " begins now.\n" +
                        "Current turn: " + getPlayerName(gameController.getCurrentPlayer()) + "\n\n" +
                        "Click on the right board (" + theme.getEnemyWatersWord() + ") to fire!",
                "Battle Stations!",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void handleAttack(int row, int col) {
        if (attackAnimating) {
            return;
        }
        if (gameController.getCurrentPhase() == GameController.GamePhase.GAME_OVER) {
            return;
        }

        attackAnimating = true;

        boolean hit = gameController.attack(row, col);

        // Check if a ship was sunk
        PlayerModel defender = (gameController.getCurrentPlayer() == gameController.getPlayer1())
                ? gameController.getPlayer2()
                : gameController.getPlayer1();
        boolean shipSunk = checkIfShipSunk(defender, row, col);

        if (hit) {
            playExplosionSound();

            if (shipSunk) {
                playShipSunkSound();
                messageLabel.setText(theme.getVesselWord().toUpperCase() + " SUNK at (" +
                        (char)('A' + row) + ", " + (col + 1) + ")! Keep firing!");

                // Show dramatic sunk notification after a short delay
                Timer sunkTimer = new Timer(800, e -> {
                    showStyledDialog(
                            "ENEMY " + theme.getVesselWord().toUpperCase() + " DESTROYED!\n\n" +
                                    "You've sunk an enemy " + theme.getVesselWord().toLowerCase() + "!\n" +
                                    "Continue the " + theme.getAttackWord().toLowerCase() + "!",
                            theme.getVesselWord().toUpperCase() + " SUNK!",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                });
                sunkTimer.setRepeats(false);
                sunkTimer.start();
            } else {
                messageLabel.setText("DIRECT HIT at (" + (char)('A' + row) + ", " + (col + 1) + ")! Fire again!");
            }
        } else {
            messageLabel.setText("Miss at (" + (char)('A' + row) + ", " + (col + 1) + "). Turn ending...");
        }

        refreshBattleBoards();

        if (gameController.getCurrentPhase() == GameController.GamePhase.GAME_OVER) {
            String winner = getPlayerName(gameController.getCurrentPlayer());

            // Show game over dialog with restart option
            Object[] options = {"Restart Game", "Exit"};
            int choice = JOptionPane.showOptionDialog(
                    this,
                    winner + " WINS!\n\n" +
                            "All enemy ships have been destroyed!\n" +
                            "Victory is yours!\n\n" +
                            "Would you like to play again?",
                    "Game Over",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            if (choice == 0) { // Restart
                restartGame();
            } else { // Exit
                System.exit(0);
            }

            attackAnimating = false;
            updateStatusLabels();
            return;
        }

        if (hit) {
            attackAnimating = false;
            updateStatusLabels();
            return;
        }

        Timer timer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ((Timer)e.getSource()).stop();

                gameController.endTurn();
                refreshBattleBoards();
                updateStatusLabels();

                messageLabel.setText(getPlayerName(gameController.getCurrentPlayer()) +
                        "'s turn to attack!");

                attackAnimating = false;
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    /**
     * Checks if a ship was sunk at the given coordinates.
     * Returns true if the ship at this position is now completely destroyed.
     */
    private boolean checkIfShipSunk(PlayerModel defender, int hitRow, int hitCol) {
        // Find the ship at this position
        for (var ship : defender.getShips()) {
            boolean containsHitPosition = false;

            // Check if this ship contains the hit position
            for (var pos : ship.getPositions()) {
                if (pos.getxCor() == hitRow && pos.getyCor() == hitCol) {
                    containsHitPosition = true;
                    break;
                }
            }

            if (!containsHitPosition) {
                continue; // This ship doesn't contain the hit position
            }

            // Check if ALL positions of this ship are hit/sunk
            boolean allHit = true;
            for (var pos : ship.getPositions()) {
                var state = defender.playerBoard.getCellState(pos.getxCor(), pos.getyCor());
                if (state != Models.BoardModel.CellState.HIT &&
                        state != Models.BoardModel.CellState.SUNK) {
                    allHit = false;
                    break;
                }
            }

            return allHit; // Ship is sunk if all positions are hit
        }

        return false; // No ship found at this position
    }

    private void refreshBattleBoards() {
        PlayerModel current = gameController.getCurrentPlayer();
        PlayerModel opponent = (current == gameController.getPlayer1())
                ? gameController.getPlayer2()
                : gameController.getPlayer1();

        playerBoardPanel.showBoardFromModel(current.playerBoard, false);
        targetBoardPanel.showBoardFromModel(current.opponentBoard, true);

        currentPlayerLabel.setText(getPlayerName(current));
        phaseLabel.setText(gameController.getCurrentPhase().toString());
        shipsPlacedLabel.setText(current.getShips().size() + "/" + current.getRequiredShips());
    }

    private String getPlayerName(PlayerModel p) {
        return (p == gameController.getPlayer1()) ? "Player 1" : "Player 2";
    }

    /**
     * Restarts the game by showing theme selection and resetting everything
     */
    private void restartGame() {
        // Get the parent frame
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);

        // Show theme selection
        GameTheme selectedTheme = GameWindow.showThemeSelection();
        if (selectedTheme == null) {
            System.exit(0); // User closed dialog
            return;
        }

        // Update frame title
        frame.setTitle(selectedTheme.getWindowTitle());

        // Remove this panel
        frame.getContentPane().removeAll();

        // Create new game panel with selected theme
        GameWindowPanel newPanel = new GameWindowPanel(selectedTheme);
        frame.add(newPanel);

        // Refresh the frame
        frame.revalidate();
        frame.repaint();
    }

    private void updateStatusLabels() {
        phaseLabel.setText(gameController.getCurrentPhase().toString());
        currentPlayerLabel.setText(getPlayerName(gameController.getCurrentPlayer()));
        PlayerModel cp = gameController.getCurrentPlayer();
        shipsPlacedLabel.setText(cp.getShips().size() + "/" + cp.getRequiredShips());
    }

    private void showStyledDialog(String message, String title, int messageType) {
        UIManager.put("OptionPane.messageFont", new Font("Arial", Font.PLAIN, 13));
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }

    private void playExplosionSound() {
        new Thread(() -> {
            tryPlaySoundFile("/sounds/explosion.wav");
        }).start();
    }

    private void playShipSunkSound() {
        new Thread(() -> {
            // Try to play a special sunk sound, or use explosion as fallback
            if (!tryPlaySoundFile("/sounds/sunk.wav")) {
                tryPlaySoundFile("/sounds/shipsunk.wav");
            }
        }).start();
    }

    /**
     * Attempts to play a sound file from resources.
     * @param resourcePath Path to the sound file in resources (e.g., "/sounds/explosion.wav")
     * @return true if sound was played successfully, false otherwise
     */
    private boolean tryPlaySoundFile(String resourcePath) {
        try {
            java.net.URL soundURL = getClass().getResource(resourcePath);
            if (soundURL == null) {
                return false;
            }

            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundURL);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);

            // Add listener to close clip when it finishes playing
            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    clip.close();
                    try {
                        audioIn.close();
                    } catch (Exception e) {
                        // Ignore
                    }
                }
            });

            clip.start();
            return true;
        } catch (Exception e) {
            // File not found or couldn't play - return false to try next option
            return false;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
    }
}