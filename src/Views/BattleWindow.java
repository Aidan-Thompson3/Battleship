package Views;

import Controllers.GameController;
import Models.PlayerModel;

import javax.swing.*;
import java.awt.*;

/**
 * Standalone BattleWindow - opens a new JFrame for the battle phase
 */
public class BattleWindow extends JFrame {

    private final GameController gameController;
    private AttackViewPanel attackView;
    private DefenseViewPanel defenseView;

    private final JLabel statusLabel = new JLabel();
    private final JLabel currentPlayerLabel = new JLabel();
    private final JButton endTurnButton = new JButton("End Turn");

    public BattleWindow(GameController gameController) {
        this.gameController = gameController;

        setTitle("Battleship - Battle Phase");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);

        initializeUI();
        updateDisplay();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        // Top status panel
        JPanel statusPanel = new JPanel(new GridLayout(2, 1));
        statusPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        statusPanel.add(currentPlayerLabel);
        statusPanel.add(statusLabel);
        add(statusPanel, BorderLayout.NORTH);

        // Center panel with both boards
        JPanel boardsPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        boardsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Left: Your ships (defense view)
        JPanel defensePanel = new JPanel(new BorderLayout());
        defensePanel.setBorder(BorderFactory.createTitledBorder("Your Fleet"));
        defenseView = new DefenseViewPanel();
        defensePanel.add(defenseView, BorderLayout.CENTER);
        boardsPanel.add(defensePanel);

        // Right: Enemy waters (attack view)
        JPanel attackPanel = new JPanel(new BorderLayout());
        attackPanel.setBorder(BorderFactory.createTitledBorder("Enemy Waters"));
        attackView = new AttackViewPanel();
        attackView.setAttackListener(this::onAttack);
        attackPanel.add(attackView, BorderLayout.CENTER);
        boardsPanel.add(attackPanel);

        add(boardsPanel, BorderLayout.CENTER);

        // Bottom control panel
        JPanel controlPanel = new JPanel();
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        endTurnButton.addActionListener(e -> onEndTurn());
        endTurnButton.setEnabled(false);

        controlPanel.add(endTurnButton);
        add(controlPanel, BorderLayout.SOUTH);
    }

    private void onAttack(int row, int col) {
        if (gameController.getCurrentPhase() == GameController.GamePhase.GAME_OVER) {
            JOptionPane.showMessageDialog(this,
                    "Game is over!",
                    "Game Over",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        boolean hit = gameController.attack(row, col);

        updateDisplay();

        if (gameController.getCurrentPhase() == GameController.GamePhase.GAME_OVER) {
            PlayerModel winner = (gameController.getCurrentPlayer() == gameController.getPlayer1())
                    ? gameController.getPlayer2() : gameController.getPlayer1();

            JOptionPane.showMessageDialog(this,
                    getPlayerName(winner) + " wins!\nAll enemy ships have been sunk!",
                    "Victory!",
                    JOptionPane.INFORMATION_MESSAGE);

            int response = JOptionPane.showConfirmDialog(this,
                    "Would you like to start a new game?",
                    "New Game?",
                    JOptionPane.YES_NO_OPTION);

            if (response == JOptionPane.YES_OPTION) {
                dispose();
                SwingUtilities.invokeLater(() -> GameWindow.main(new String[]{}));
            } else {
                System.exit(0);
            }
            return;
        }

        if (hit) {
            statusLabel.setText("HIT! You get another turn.");
            endTurnButton.setEnabled(true);
        } else {
            statusLabel.setText("MISS! Turn automatically switches...");
            Timer timer = new Timer(1500, e -> switchTurn());
            timer.setRepeats(false);
            timer.start();
        }
    }

    private void onEndTurn() {
        switchTurn();
    }

    private void switchTurn() {
        endTurnButton.setEnabled(false);

        String nextPlayer = getPlayerName(gameController.getCurrentPlayer());

        int response = JOptionPane.showConfirmDialog(this,
                "Hand the computer to " + nextPlayer + ".\nClick OK when ready.",
                "Switch Players",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE);

        if (response == JOptionPane.OK_OPTION) {
            updateDisplay();
        }
    }

    private void updateDisplay() {
        PlayerModel currentPlayer = gameController.getCurrentPlayer();

        currentPlayerLabel.setText("Current Player: " + getPlayerName(currentPlayer));
        statusLabel.setText("Select a cell to attack on the Enemy Waters board.");

        defenseView.syncWithBoard(currentPlayer.playerBoard);
        attackView.syncWithBoard(currentPlayer.opponentBoard);
    }

    private String getPlayerName(PlayerModel player) {
        return (player == gameController.getPlayer1()) ? "Player 1" : "Player 2";
    }

    public static void launchBattle(GameController gameController) {
        SwingUtilities.invokeLater(() -> {
            BattleWindow window = new BattleWindow(gameController);
            window.setVisible(true);
        });
    }

    public static void main(String[] args) {
        // Test harness
        SwingUtilities.invokeLater(() -> {
            GameController gc = new GameController();
            BattleWindow window = new BattleWindow(gc);
            window.setVisible(true);
        });
    }
}