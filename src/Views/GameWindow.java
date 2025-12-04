package Views;

import Controllers.GameController;
import Models.CoordinatesModel;
import Models.ShipModel;
import Models.PlayerModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

public class GameWindow {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("BATTLESHIP - Fleet Deployment");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(900, 900);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new GameWindowPanel());
            frame.setVisible(true);
        });
    }
}

class GameWindowPanel extends JPanel implements ActionListener, ItemListener {

    private final GameController gameController;

    private BoardViewPanel boardViewPanel;

    private final JLabel phaseLabel = new JLabel();
    private final JLabel currentPlayerLabel = new JLabel();
    private final JLabel shipsPlacedLabel = new JLabel();
    private final JLabel currentShipLabel = new JLabel();
    private final JLabel instructionLabel = new JLabel();
    private final JPanel shipQueuePanel = new JPanel();

    private ShipModel.Orientation selectedOrientation = ShipModel.Orientation.HORIZONTAL;

    // Standard Battleship ship lengths: 5,4,3,3,2
    private final int[] shipLengths = {5, 4, 3, 3, 2};
    private final String[] shipNames = {"Carrier", "Battleship", "Cruiser", "Submarine", "Destroyer"};
    private int currentShipIndex = 0;

    public GameWindowPanel() {
        this.gameController = new GameController();

        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(25, 35, 45));

        // Top command panel
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(new Color(35, 45, 55));
        topPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 150, 200), 3),
                BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));

        JPanel infoPanel = new JPanel(new GridLayout(3, 1, 5, 8));
        infoPanel.setBackground(new Color(35, 45, 55));

        phaseLabel.setFont(new Font("Consolas", Font.BOLD, 18));
        phaseLabel.setForeground(new Color(100, 200, 255));
        currentPlayerLabel.setFont(new Font("Consolas", Font.BOLD, 20));
        currentPlayerLabel.setForeground(new Color(150, 255, 150));
        shipsPlacedLabel.setFont(new Font("Consolas", Font.BOLD, 16));
        shipsPlacedLabel.setForeground(new Color(255, 200, 100));

        infoPanel.add(phaseLabel);
        infoPanel.add(currentPlayerLabel);
        infoPanel.add(shipsPlacedLabel);
        topPanel.add(infoPanel, BorderLayout.NORTH);

        // Current ship info
        JPanel currentShipPanel = new JPanel(new BorderLayout(10, 10));
        currentShipPanel.setBackground(new Color(45, 60, 75));
        currentShipPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 200, 100), 2),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        JLabel currentShipTitle = new JLabel("NOW DEPLOYING:", SwingConstants.LEFT);
        currentShipTitle.setFont(new Font("Consolas", Font.BOLD, 14));
        currentShipTitle.setForeground(new Color(255, 200, 100));

        currentShipLabel.setFont(new Font("Consolas", Font.BOLD, 24));
        currentShipLabel.setForeground(new Color(255, 255, 100));
        currentShipLabel.setHorizontalAlignment(SwingConstants.CENTER);

        instructionLabel.setFont(new Font("Consolas", Font.PLAIN, 13));
        instructionLabel.setForeground(new Color(200, 220, 255));
        instructionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        instructionLabel.setText("Click cells on the grid to select ship positions, then click Place Ship");

        currentShipPanel.add(currentShipTitle, BorderLayout.NORTH);
        currentShipPanel.add(currentShipLabel, BorderLayout.CENTER);
        currentShipPanel.add(instructionLabel, BorderLayout.SOUTH);
        topPanel.add(currentShipPanel, BorderLayout.CENTER);

        // Ship queue panel
        JPanel queueContainer = new JPanel(new BorderLayout());
        queueContainer.setBackground(new Color(35, 45, 55));
        JLabel queueTitle = new JLabel("REMAINING SHIPS TO DEPLOY:", SwingConstants.LEFT);
        queueTitle.setFont(new Font("Consolas", Font.BOLD, 12));
        queueTitle.setForeground(new Color(150, 200, 255));
        queueTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        queueContainer.add(queueTitle, BorderLayout.NORTH);

        shipQueuePanel.setLayout(new GridLayout(1, 5, 10, 0));
        shipQueuePanel.setBackground(new Color(35, 45, 55));
        updateShipQueue();
        queueContainer.add(shipQueuePanel, BorderLayout.CENTER);
        topPanel.add(queueContainer, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);

        // Center board with styling
        JPanel boardContainer = new JPanel(new BorderLayout());
        boardContainer.setBackground(new Color(25, 35, 45));
        boardContainer.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));

        JPanel boardWrapper = new JPanel(new BorderLayout());
        boardWrapper.setBackground(new Color(25, 35, 45));
        boardWrapper.setBorder(BorderFactory.createLineBorder(new Color(100, 150, 200), 3));

        boardViewPanel = new BoardViewPanel();
        boardWrapper.add(boardViewPanel, BorderLayout.CENTER);
        boardContainer.add(boardWrapper, BorderLayout.CENTER);
        add(boardContainer, BorderLayout.CENTER);

        // Bottom control panel
        JPanel controlPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        controlPanel.setBackground(new Color(35, 45, 55));
        controlPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 150, 200), 3),
                BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));

        // Orientation panel
        JPanel orientationPanel = new JPanel(new GridLayout(3, 1, 5, 8));
        orientationPanel.setBackground(new Color(45, 60, 75));
        orientationPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(150, 200, 255), 2),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        JLabel orientLabel = new JLabel("SHIP ORIENTATION", SwingConstants.CENTER);
        orientLabel.setFont(new Font("Consolas", Font.BOLD, 14));
        orientLabel.setForeground(new Color(150, 200, 255));

        JButton horizontalBtn = createStyledButton("HORIZONTAL", new Color(70, 130, 180));
        JButton verticalBtn = createStyledButton("VERTICAL", new Color(70, 130, 180));

        horizontalBtn.addActionListener(e -> {
            selectedOrientation = ShipModel.Orientation.HORIZONTAL;
            horizontalBtn.setBackground(new Color(100, 200, 100));
            verticalBtn.setBackground(new Color(70, 130, 180));
        });

        verticalBtn.addActionListener(e -> {
            selectedOrientation = ShipModel.Orientation.VERTICAL;
            verticalBtn.setBackground(new Color(100, 200, 100));
            horizontalBtn.setBackground(new Color(70, 130, 180));
        });

        horizontalBtn.setBackground(new Color(100, 200, 100)); // Default selected

        orientationPanel.add(orientLabel);
        orientationPanel.add(horizontalBtn);
        orientationPanel.add(verticalBtn);

        // Action panel
        JPanel actionPanel = new JPanel(new BorderLayout(10, 10));
        actionPanel.setBackground(new Color(45, 60, 75));
        actionPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 255, 100), 2),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        JLabel actionLabel = new JLabel("DEPLOYMENT COMMAND", SwingConstants.CENTER);
        actionLabel.setFont(new Font("Consolas", Font.BOLD, 14));
        actionLabel.setForeground(new Color(100, 255, 100));

        JButton placeShipBtn = createStyledButton("PLACE SHIP", new Color(34, 139, 34));
        placeShipBtn.setFont(new Font("Consolas", Font.BOLD, 18));
        placeShipBtn.addActionListener(e -> onPlaceShip());

        JButton clearBtn = createStyledButton("CLEAR SELECTION", new Color(180, 80, 80));
        clearBtn.addActionListener(e -> boardViewPanel.clearSelection());

        actionPanel.add(actionLabel, BorderLayout.NORTH);
        actionPanel.add(placeShipBtn, BorderLayout.CENTER);
        actionPanel.add(clearBtn, BorderLayout.SOUTH);

        controlPanel.add(orientationPanel);
        controlPanel.add(actionPanel);
        add(controlPanel, BorderLayout.SOUTH);

        updateStatusLabels();
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Consolas", Font.BOLD, 14));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bgColor.brighter(), 2),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        return btn;
    }

    private void updateShipQueue() {
        shipQueuePanel.removeAll();
        for (int i = currentShipIndex; i < shipLengths.length; i++) {
            JPanel shipCard = new JPanel(new BorderLayout(5, 5));
            shipCard.setBackground(i == currentShipIndex ? new Color(80, 120, 80) : new Color(60, 70, 85));
            shipCard.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(i == currentShipIndex ? new Color(150, 255, 150) : new Color(100, 150, 200), 2),
                    BorderFactory.createEmptyBorder(8, 10, 8, 10)
            ));

            JLabel name = new JLabel(shipNames[i], SwingConstants.CENTER);
            name.setFont(new Font("Consolas", Font.BOLD, 11));
            name.setForeground(Color.WHITE);

            JLabel length = new JLabel("Length: " + shipLengths[i], SwingConstants.CENTER);
            length.setFont(new Font("Consolas", Font.PLAIN, 10));
            length.setForeground(new Color(200, 220, 255));

            shipCard.add(name, BorderLayout.CENTER);
            shipCard.add(length, BorderLayout.SOUTH);
            shipQueuePanel.add(shipCard);
        }
        shipQueuePanel.revalidate();
        shipQueuePanel.repaint();
    }

    private void onPlaceShip() {
        List<CoordinatesModel> selected = new ArrayList<>(boardViewPanel.selectedCoordinates);
        if (selected.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "NO COORDINATES SELECTED\n\nClick on grid cells to select ship positions.",
                    "Selection Required",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (currentShipIndex >= shipLengths.length) {
            JOptionPane.showMessageDialog(this,
                    "All ships for this player are already deployed!",
                    "Fleet Complete",
                    JOptionPane.INFORMATION_MESSAGE);
            boardViewPanel.clearSelection();
            return;
        }

        int requiredLength = shipLengths[currentShipIndex];
        if (selected.size() != requiredLength) {
            JOptionPane.showMessageDialog(this,
                    "INVALID SHIP LENGTH\n\n" +
                            "Selected: " + selected.size() + " cells\n" +
                            "Required: " + requiredLength + " cells for " + shipNames[currentShipIndex],
                    "Invalid Length",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        PlayerModel before = gameController.getCurrentPlayer();
        boolean success = gameController.placeShip(selected);

        if (success) {
            boardViewPanel.markPlacedShip(selected);
            boardViewPanel.clearSelection();

            currentShipIndex++;
            updateShipQueue();

            // If the current player changed, we just finished this player's setup
            if (gameController.getCurrentPlayer() != before) {
                JOptionPane.showMessageDialog(this,
                        "FLEET DEPLOYMENT COMPLETE!\n\n" +
                                getPlayerName(before) + " has positioned all ships.\n\n" +
                                "Hand the device to the next commander.",
                        "Player " + (before == gameController.getPlayer1() ? "1" : "2") + " Complete",
                        JOptionPane.INFORMATION_MESSAGE);

                // Reset for next player's fleet
                currentShipIndex = 0;
                updateShipQueue();

                // New blank board for the next player's ships
                remove(boardViewPanel.getParent().getParent());

                JPanel boardContainer = new JPanel(new BorderLayout());
                boardContainer.setBackground(new Color(25, 35, 45));
                boardContainer.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));

                JPanel boardWrapper = new JPanel(new BorderLayout());
                boardWrapper.setBackground(new Color(25, 35, 45));
                boardWrapper.setBorder(BorderFactory.createLineBorder(new Color(100, 150, 200), 3));

                boardViewPanel = new BoardViewPanel();
                boardWrapper.add(boardViewPanel, BorderLayout.CENTER);
                boardContainer.add(boardWrapper, BorderLayout.CENTER);
                add(boardContainer, BorderLayout.CENTER);
            }

            // Check if battle phase has started
            if (gameController.getCurrentPhase() == GameController.GamePhase.PLAYER1_TURN) {
                JOptionPane.showMessageDialog(this,
                        "FLEET DEPLOYMENT COMPLETE\n\n" +
                                "Both commanders have positioned their fleets.\n\n" +
                                "Initiating combat operations...",
                        "Begin Battle!",
                        JOptionPane.INFORMATION_MESSAGE);

                Window window = SwingUtilities.getWindowAncestor(this);
                if (window != null) {
                    window.dispose();
                }

                BattleWindow.launchBattle(gameController);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "INVALID PLACEMENT\n\n" +
                            "Ensure cells are:\n" +
                            "- Contiguous (touching)\n" +
                            "- In a straight line\n" +
                            "- Within grid bounds\n" +
                            "- Not overlapping existing ships",
                    "Deployment Failed",
                    JOptionPane.ERROR_MESSAGE);
        }

        updateStatusLabels();
        revalidate();
        repaint();
    }

    private String getPlayerName(PlayerModel p) {
        return (p == gameController.getPlayer1()) ? "PLAYER 1" : "PLAYER 2";
    }

    private void updateStatusLabels() {
        phaseLabel.setText("PHASE: " + gameController.getCurrentPhase());
        currentPlayerLabel.setText("COMMANDER: " + getPlayerName(gameController.getCurrentPlayer()));

        PlayerModel cp = gameController.getCurrentPlayer();
        int placed = Math.min(cp.getShips().size(), cp.getRequiredShips());
        int required = cp.getRequiredShips();
        shipsPlacedLabel.setText("SHIPS DEPLOYED: " + placed + " / " + required);

        if (currentShipIndex < shipLengths.length) {
            currentShipLabel.setText(shipNames[currentShipIndex] + " (Length: " + shipLengths[currentShipIndex] + ")");
        } else {
            currentShipLabel.setText("ALL SHIPS DEPLOYED!");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    @Override
    public void itemStateChanged(ItemEvent e) {

    }
}