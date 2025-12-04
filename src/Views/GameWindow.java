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
            JFrame frame = new JFrame("Battleship");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 800);
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

    private ShipModel.Orientation selectedOrientation = ShipModel.Orientation.HORIZONTAL;

    // Standard Battleship ship lengths: 5,4,3,3,2
    private final int[] shipLengths = {5, 4, 3, 3, 2};
    private int currentShipIndex = 0;

    public GameWindowPanel() {
        this.gameController = new GameController();

        setLayout(new BorderLayout());

        // Center board
        boardViewPanel = new BoardViewPanel();
        add(boardViewPanel, BorderLayout.CENTER);

        // Top info panel
        JPanel infoPanel = new JPanel(new GridLayout(1, 3));
        infoPanel.add(phaseLabel);
        infoPanel.add(currentPlayerLabel);
        infoPanel.add(shipsPlacedLabel);
        add(infoPanel, BorderLayout.NORTH);

        // Bottom control panel – orientation + place button
        JPanel controlPanel = new JPanel();
        JButton horizontalBtn = new JButton("Horizontal");
        JButton verticalBtn = new JButton("Vertical");
        JButton placeShipBtn = new JButton("Place Ship");

        horizontalBtn.addActionListener(e -> {
            selectedOrientation = ShipModel.Orientation.HORIZONTAL;
            System.out.println("Orientation set to HORIZONTAL");
        });

        verticalBtn.addActionListener(e -> {
            selectedOrientation = ShipModel.Orientation.VERTICAL;
            System.out.println("Orientation set to VERTICAL");
        });

        placeShipBtn.addActionListener(e -> onPlaceShip());

        controlPanel.add(new JLabel("Orientation:"));
        controlPanel.add(horizontalBtn);
        controlPanel.add(verticalBtn);
        controlPanel.add(placeShipBtn);

        add(controlPanel, BorderLayout.SOUTH);

        updateStatusLabels();
    }

    private void onPlaceShip() {
        List<CoordinatesModel> selected = new ArrayList<>(boardViewPanel.selectedCoordinates);
        if (selected.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No coordinates selected.\nClick on the board to select cells.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (currentShipIndex >= shipLengths.length) {
            JOptionPane.showMessageDialog(this,
                    "All ships for this player are already placed.",
                    "Fleet Complete",
                    JOptionPane.INFORMATION_MESSAGE);
            boardViewPanel.clearSelection();
            return;
        }

        int requiredLength = shipLengths[currentShipIndex];
        if (selected.size() != requiredLength) {
            JOptionPane.showMessageDialog(this,
                    "You selected " + selected.size() + " cell(s).\n"
                            + "Current ship length is " + requiredLength + ".",
                    "Incorrect Ship Length",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        System.out.println("Attempting to place ship of length "
                + requiredLength + " with selected orientation " + selectedOrientation);

        PlayerModel before = gameController.getCurrentPlayer();
        boolean success = gameController.placeShip(selected);

        if (success) {
            boardViewPanel.markPlacedShip(selected);
            boardViewPanel.clearSelection();

            currentShipIndex++;
            int placedCountForPlayer = before.getShips().size();
            int requiredShipsForPlayer = before.getRequiredShips();
            shipsPlacedLabel.setText("Ships placed: "
                    + Math.min(placedCountForPlayer, requiredShipsForPlayer)
                    + "/" + requiredShipsForPlayer);

            // If the current player changed, we just finished this player's setup
            if (gameController.getCurrentPlayer() != before) {
                JOptionPane.showMessageDialog(this,
                        "Finished placing ships for " + getPlayerName(before)
                                + ".\nHand the computer to the next player.",
                        "Switch Player",
                        JOptionPane.INFORMATION_MESSAGE);

                // Reset for next player's fleet
                currentShipIndex = 0;
                shipsPlacedLabel.setText("Ships placed: 0/" + requiredShipsForPlayer);

                // New blank board for the next player's ships
                remove(boardViewPanel);
                boardViewPanel = new BoardViewPanel();
                add(boardViewPanel, BorderLayout.CENTER);
            }

            // Check if battle phase has started
            if (gameController.getCurrentPhase() == GameController.GamePhase.PLAYER1_TURN) {
                JOptionPane.showMessageDialog(this,
                        "Both players have placed their ships.\n"
                                + "Starting battle phase!",
                        "Setup Complete",
                        JOptionPane.INFORMATION_MESSAGE);

                // Close setup window and launch battle window
                Window window = SwingUtilities.getWindowAncestor(this);
                if (window != null) {
                    window.dispose();
                }

                BattleWindow.launchBattle(gameController);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Invalid ship placement. Make sure the cells are contiguous, in-bounds, "
                            + "and do not overlap existing ships.",
                    "Invalid Placement",
                    JOptionPane.ERROR_MESSAGE);
        }

        updateStatusLabels();
        revalidate();
        repaint();
    }

    private String getPlayerName(PlayerModel p) {
        return (p == gameController.getPlayer1()) ? "Player 1" : "Player 2";
    }

    private void updateStatusLabels() {
        phaseLabel.setText("Phase: " + gameController.getCurrentPhase());
        currentPlayerLabel.setText("Current: " + getPlayerName(gameController.getCurrentPlayer()));

        PlayerModel cp = gameController.getCurrentPlayer();
        shipsPlacedLabel.setText("Ships placed: "
                + Math.min(cp.getShips().size(), cp.getRequiredShips())
                + "/" + cp.getRequiredShips());
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    @Override
    public void itemStateChanged(ItemEvent e) {

    }
}
