package Views;

import Controllers.GameController;
import Models.CoordinatesModel;
import Models.PlayerModel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class GameWindow {
    public static void main(String args[]){
        JFrame frame = new JFrame("Battleship");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900,700);
        GameWindowPanel gwp = new GameWindowPanel();
        frame.add(gwp);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}

class GameWindowPanel extends JPanel implements ActionListener, ItemListener {

    private GameController gameController;

    // Setup UI
    private BoardViewPanel setupBoardPanel;
    private JPanel setupControlPanel;
    private JButton placeShipBtn;

    // Battle UI
    private JPanel battlePanel;
    private BoardViewPanel playerBoardPanel;
    private BoardViewPanel targetBoardPanel;

    // Status labels
    private JLabel phaseLabel = new JLabel();
    private JLabel currentPlayerLabel = new JLabel();
    private JLabel shipsPlacedLabel = new JLabel();
    private JLabel messageLabel =
            new JLabel("Place your ships by selecting cells and clicking 'Place Ship'.");

    // Standard Battleship ship lengths
    private final int[] shipLengths = {5, 4, 3, 3, 2};
    private int currentShipIndex = 0;

    GameWindowPanel(){
        System.out.println("Game Window Panel constructor");
        setLayout(new BorderLayout(5,5));

        gameController = new GameController();

        // ---------- TOP STATUS BAR ----------
        JPanel statusPanel = new JPanel(new GridLayout(2, 1));
        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topRow.add(new JLabel("Phase: "));
        topRow.add(phaseLabel);
        topRow.add(Box.createHorizontalStrut(20));
        topRow.add(new JLabel("Current: "));
        topRow.add(currentPlayerLabel);
        topRow.add(Box.createHorizontalStrut(20));
        topRow.add(new JLabel("Ships placed: "));
        topRow.add(shipsPlacedLabel);

        JPanel bottomRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomRow.add(messageLabel);

        statusPanel.add(topRow);
        statusPanel.add(bottomRow);
        add(statusPanel, BorderLayout.NORTH);

        // ---------- CENTER: SETUP BOARD
        setupBoardPanel = new BoardViewPanel();
        setupBoardPanel.setMode(BoardViewPanel.BoardMode.SETUP_SELECTION);
        add(setupBoardPanel, BorderLayout.CENTER);

        // ---------- BOTTOM: controls for setup ----------
        setupControlPanel = new JPanel();
        JButton horizontalBtn = new JButton("Horizontal (visual only)");
        JButton verticalBtn = new JButton("Vertical (visual only)");
        placeShipBtn = new JButton("Place Ship");

        horizontalBtn.addActionListener(e ->
                messageLabel.setText("Using horizontal-style selection (contiguous in a row)."));

        verticalBtn.addActionListener(e ->
                messageLabel.setText("Using vertical-style selection (contiguous in a column)."));

        placeShipBtn.addActionListener(e -> onPlaceShip());

        setupControlPanel.add(horizontalBtn);
        setupControlPanel.add(verticalBtn);
        setupControlPanel.add(placeShipBtn);

        add(setupControlPanel, BorderLayout.SOUTH);

        updateStatusLabels();
    }


    private void onPlaceShip() {
        List<CoordinatesModel> selected =
                new ArrayList<>(setupBoardPanel.selectedCoordinates);
        if (selected.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No coordinates selected. Click on cells to select them first.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (currentShipIndex >= shipLengths.length) {
            JOptionPane.showMessageDialog(this,
                    "All ships for this player are already placed.",
                    "Fleet Complete",
                    JOptionPane.INFORMATION_MESSAGE);
            setupBoardPanel.clearSelection();
            return;
        }

        int requiredLength = shipLengths[currentShipIndex];
        if (selected.size() != requiredLength) {
            JOptionPane.showMessageDialog(this,
                    "You selected " + selected.size() + " cell(s).\n" +
                            "Current ship length is " + requiredLength + ".",
                    "Incorrect Ship Length",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        System.out.println("Attempting to place ship of length " + requiredLength);

        // Detect if the currentPlayer switches after placing (meaning we finished one player's setup)
        PlayerModel before = gameController.getCurrentPlayer();
        boolean success = gameController.placeShip(selected);

        if (success) {
            // Permanently mark ship on this setup board
            setupBoardPanel.markPlacedShip(selected);
            setupBoardPanel.clearSelection();

            currentShipIndex++;

            int shipsPlaced = before.getShips().size();
            int requiredShips = before.getRequiredShips();
            shipsPlacedLabel.setText(Math.min(shipsPlaced, requiredShips) + "/" + requiredShips);

            // Check if we just switched to the other player's setup
            if (gameController.getCurrentPhase() == GameController.GamePhase.SETUP &&
                    gameController.getCurrentPlayer() != before) {

                JOptionPane.showMessageDialog(this,
                        "Finished placing ships for " + getPlayerName(before) +
                                ". Hand the computer to the next player.",
                        "Switch Player",
                        JOptionPane.INFORMATION_MESSAGE);

                // Reset ship index for the next player
                currentShipIndex = 0;

                // Start with a fresh setup board for the next player
                remove(setupBoardPanel);
                setupBoardPanel = new BoardViewPanel();
                setupBoardPanel.setMode(BoardViewPanel.BoardMode.SETUP_SELECTION);
                add(setupBoardPanel, BorderLayout.CENTER);
            }

            // If phase changed to PLAYER1_TURN, both players are done; enter battle mode
            if (gameController.getCurrentPhase() == GameController.GamePhase.PLAYER1_TURN) {
                enterBattleMode();
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Invalid ship placement. Make sure the cells are contiguous, inside the board, " +
                            "and do not overlap existing ships.",
                    "Invalid Placement",
                    JOptionPane.ERROR_MESSAGE);
        }

        updateStatusLabels();
        revalidate();
        repaint();
    }


    private void enterBattleMode() {
        // Remove setup board from center
        remove(setupBoardPanel);

        // Create side-by-side boards for battle
        battlePanel = new JPanel(new GridLayout(1, 2, 10, 10));
        playerBoardPanel = new BoardViewPanel();
        targetBoardPanel = new BoardViewPanel();

        // Own board is view only; opponent board is clickable
        playerBoardPanel.setMode(BoardViewPanel.BoardMode.VIEW_ONLY);
        targetBoardPanel.setMode(BoardViewPanel.BoardMode.BATTLE_TARGET);

        // In battle, we don't use the selection list in these panels
        playerBoardPanel.selectedCoordinates.clear();
        targetBoardPanel.selectedCoordinates.clear();

        // Attack handler for the target board
        targetBoardPanel.setAttackListener(new AttackListener() {
            @Override
            public void onAttack(int row, int col) {
                handleAttack(row, col);
            }
        });

        battlePanel.add(playerBoardPanel);
        battlePanel.add(targetBoardPanel);

        add(battlePanel, BorderLayout.CENTER);

        // Hide setup controls
        setupControlPanel.setVisible(false);
        placeShipBtn.setVisible(false);

        messageLabel.setText("Battle started! " +
                getPlayerName(gameController.getCurrentPlayer()) +
                " attacks first. Click on the right board to fire.");

        // Initial draw
        refreshBattleBoards();

        revalidate();
        repaint();

        JOptionPane.showMessageDialog(this,
                "Both players have placed their ships.\n" +
                        "Battle phase has started!\n" +
                        "Current turn: " + getPlayerName(gameController.getCurrentPlayer()),
                "Battle Start",
                JOptionPane.INFORMATION_MESSAGE);
    }


    private void handleAttack(int row, int col) {
        if (gameController.getCurrentPhase() == GameController.GamePhase.GAME_OVER) {
            return;
        }

        boolean hit = gameController.attack(row, col);

        if (hit) {
            messageLabel.setText("HIT at (" + row + ", " + col + ")! " +
                    "Now it's " + getPlayerName(gameController.getCurrentPlayer()) + "'s turn.");
        } else {
            messageLabel.setText("Miss or already targeted at (" + row + ", " + col + "). " +
                    "Now it's " + getPlayerName(gameController.getCurrentPlayer()) + "'s turn.");
        }

        if (gameController.getCurrentPhase() == GameController.GamePhase.GAME_OVER) {
            String winner = getPlayerName(gameController.getCurrentPlayer());
            refreshBattleBoards();
            JOptionPane.showMessageDialog(this,
                    winner + " wins! All enemy ships have been sunk.",
                    "Game Over",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            refreshBattleBoards();
        }

        updateStatusLabels();
        revalidate();
        repaint();
    }


    private void refreshBattleBoards() {
        PlayerModel current = gameController.getCurrentPlayer();
        PlayerModel opponent =
                (current == gameController.getPlayer1())
                        ? gameController.getPlayer2()
                        : gameController.getPlayer1();


        playerBoardPanel.showBoardFromModel(current.playerBoard, false);


        currentPlayerLabel.setText(getPlayerName(current));
        phaseLabel.setText(gameController.getCurrentPhase().toString());
        shipsPlacedLabel.setText(current.getShips().size() + "/" + current.getRequiredShips());


        targetBoardPanel.showBoardFromModel(current.opponentBoard, true);
    }

    private String getPlayerName(PlayerModel p) {
        return (p == gameController.getPlayer1()) ? "Player 1" : "Player 2";
    }

    private void updateStatusLabels() {
        phaseLabel.setText(gameController.getCurrentPhase().toString());
        currentPlayerLabel.setText(getPlayerName(gameController.getCurrentPlayer()));
        PlayerModel cp = gameController.getCurrentPlayer();
        shipsPlacedLabel.setText(cp.getShips().size() + "/" + cp.getRequiredShips());
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    @Override
    public void itemStateChanged(ItemEvent e) {
    }
}
