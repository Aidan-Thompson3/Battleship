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
    private final JLabel legendLabel = new JLabel();
    private final JPanel statsPanel = new JPanel();

    private PlayerModel lastDisplayedPlayer = null;

    public BattleWindow(GameController gameController) {
        this.gameController = gameController;

        setTitle("BATTLESHIP - Combat Operations");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 800);
        setLocationRelativeTo(null);

        initializeUI();
        updateDisplay();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(25, 35, 45));

        // Top command center panel
        JPanel commandPanel = new JPanel(new BorderLayout());
        commandPanel.setBackground(new Color(35, 45, 55));
        commandPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 150, 200), 3),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        JPanel topInfoPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        topInfoPanel.setBackground(new Color(35, 45, 55));

        currentPlayerLabel.setFont(new Font("Consolas", Font.BOLD, 22));
        currentPlayerLabel.setForeground(new Color(100, 200, 255));
        statusLabel.setFont(new Font("Consolas", Font.PLAIN, 16));
        statusLabel.setForeground(new Color(200, 220, 255));

        topInfoPanel.add(currentPlayerLabel);
        topInfoPanel.add(statusLabel);
        commandPanel.add(topInfoPanel, BorderLayout.NORTH);

        // Statistics panel
        statsPanel.setBackground(new Color(35, 45, 55));
        statsPanel.setLayout(new GridLayout(1, 4, 15, 0));
        updateStatsPanel();
        commandPanel.add(statsPanel, BorderLayout.CENTER);

        // Legend
        legendLabel.setText("MISS = White Circle  |  HIT = Red X  |  YOUR SHIP = Gray Square  |  SUNK = Black");
        legendLabel.setFont(new Font("Consolas", Font.BOLD, 13));
        legendLabel.setForeground(new Color(150, 170, 200));
        legendLabel.setHorizontalAlignment(SwingConstants.CENTER);
        legendLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        commandPanel.add(legendLabel, BorderLayout.SOUTH);

        add(commandPanel, BorderLayout.NORTH);

        // Center panel with both boards
        JPanel boardsPanel = new JPanel(new GridLayout(1, 2, 25, 0));
        boardsPanel.setBackground(new Color(25, 35, 45));
        boardsPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // Left: Your ships (defense view)
        JPanel defensePanel = new JPanel(new BorderLayout(0, 10));
        defensePanel.setBackground(new Color(25, 35, 45));

        JPanel defenseTitlePanel = new JPanel(new BorderLayout());
        defenseTitlePanel.setBackground(new Color(40, 80, 60));
        defenseTitlePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 200, 120), 2),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        JLabel defenseTitle = new JLabel("YOUR FLEET - DEFENSIVE RADAR", SwingConstants.CENTER);
        defenseTitle.setFont(new Font("Consolas", Font.BOLD, 16));
        defenseTitle.setForeground(new Color(150, 255, 150));
        defenseTitlePanel.add(defenseTitle);

        defensePanel.add(defenseTitlePanel, BorderLayout.NORTH);
        defenseView = new DefenseViewPanel();
        defenseView.setBorder(BorderFactory.createLineBorder(new Color(80, 200, 120), 3));
        defensePanel.add(defenseView, BorderLayout.CENTER);
        boardsPanel.add(defensePanel);

        // Right: Enemy waters (attack view)
        JPanel attackPanel = new JPanel(new BorderLayout(0, 10));
        attackPanel.setBackground(new Color(25, 35, 45));

        JPanel attackTitlePanel = new JPanel(new BorderLayout());
        attackTitlePanel.setBackground(new Color(80, 40, 40));
        attackTitlePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 100, 100), 2),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        JLabel attackTitle = new JLabel("ENEMY WATERS - TARGET AND FIRE", SwingConstants.CENTER);
        attackTitle.setFont(new Font("Consolas", Font.BOLD, 16));
        attackTitle.setForeground(new Color(255, 150, 150));
        attackTitlePanel.add(attackTitle);

        attackPanel.add(attackTitlePanel, BorderLayout.NORTH);
        attackView = new AttackViewPanel();
        attackView.setAttackListener(this::onAttack);
        attackView.setBorder(BorderFactory.createLineBorder(new Color(255, 100, 100), 3));
        attackPanel.add(attackView, BorderLayout.CENTER);
        boardsPanel.add(attackPanel);

        add(boardsPanel, BorderLayout.CENTER);

        // Bottom control panel
        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(new Color(35, 45, 55));
        controlPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 150, 200), 3),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        endTurnButton.setFont(new Font("Consolas", Font.BOLD, 16));
        endTurnButton.setBackground(new Color(70, 130, 180));
        endTurnButton.setForeground(Color.WHITE);
        endTurnButton.setFocusPainted(false);
        endTurnButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 170, 220), 3),
                BorderFactory.createEmptyBorder(12, 30, 12, 30)
        ));
        endTurnButton.addActionListener(e -> onEndTurn());
        endTurnButton.setEnabled(false);

        controlPanel.add(endTurnButton);
        add(controlPanel, BorderLayout.SOUTH);
    }

    private void updateStatsPanel() {
        statsPanel.removeAll();
        PlayerModel currentPlayer = gameController.getCurrentPlayer();
        PlayerModel opponent = (currentPlayer == gameController.getPlayer1())
                ? gameController.getPlayer2() : gameController.getPlayer1();

        // Count hits and misses
        int yourHits = countHits(currentPlayer.opponentBoard);
        int yourMisses = countMisses(currentPlayer.opponentBoard);
        int enemyHits = countHits(opponent.opponentBoard);
        int yourShipsRemaining = countShipsRemaining(currentPlayer);

        statsPanel.add(createStatLabel("YOUR HITS", String.valueOf(yourHits), new Color(255, 100, 100)));
        statsPanel.add(createStatLabel("YOUR MISSES", String.valueOf(yourMisses), new Color(150, 200, 255)));
        statsPanel.add(createStatLabel("ENEMY HITS ON YOU", String.valueOf(enemyHits), new Color(255, 150, 50)));
        statsPanel.add(createStatLabel("YOUR SHIPS LEFT", String.valueOf(yourShipsRemaining), new Color(100, 255, 100)));

        statsPanel.revalidate();
        statsPanel.repaint();
    }

    private JPanel createStatLabel(String title, String value, Color color) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(45, 55, 65));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 2),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Consolas", Font.BOLD, 11));
        titleLabel.setForeground(new Color(180, 200, 220));

        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("Consolas", Font.BOLD, 24));
        valueLabel.setForeground(color);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(valueLabel, BorderLayout.CENTER);
        return panel;
    }

    private int countHits(Models.BoardModel board) {
        int count = 0;
        for (int r = 0; r < 10; r++) {
            for (int c = 0; c < 10; c++) {
                Models.BoardModel.CellState state = board.getCellState(r, c);
                if (state == Models.BoardModel.CellState.HIT || state == Models.BoardModel.CellState.SUNK) {
                    count++;
                }
            }
        }
        return count;
    }

    private int countMisses(Models.BoardModel board) {
        int count = 0;
        for (int r = 0; r < 10; r++) {
            for (int c = 0; c < 10; c++) {
                if (board.getCellState(r, c) == Models.BoardModel.CellState.MISS) {
                    count++;
                }
            }
        }
        return count;
    }

    private int countShipsRemaining(PlayerModel player) {
        int remaining = 0;
        for (Models.ShipModel ship : player.getShips()) {
            boolean isAfloat = false;
            for (Models.CoordinatesModel pos : ship.getPositions()) {
                Models.BoardModel.CellState state = player.playerBoard.getCellState(pos.getxCor(), pos.getyCor());
                if (state == Models.BoardModel.CellState.SHIP) {
                    isAfloat = true;
                    break;
                }
            }
            if (isAfloat) remaining++;
        }
        return remaining;
    }

    private void onAttack(int row, int col) {
        if (gameController.getCurrentPhase() == GameController.GamePhase.GAME_OVER) {
            JOptionPane.showMessageDialog(this,
                    "Game is already over!",
                    "Game Over",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Execute the attack
        boolean hit = gameController.attack(row, col);

        // Get the current player whose turn it is
        PlayerModel currentPlayer = gameController.getCurrentPlayer();

        // IMMEDIATELY update the attack view to show hit/miss
        attackView.syncWithBoard(currentPlayer.opponentBoard);
        updateStatsPanel();

        // Force Swing to process all pending events and actually paint the changes
        SwingUtilities.invokeLater(() -> {
            attackView.revalidate();
            attackView.repaint();
            statsPanel.revalidate();
            statsPanel.repaint();
        });

        // Let the UI actually render before continuing
        try {
            Thread.sleep(300); // Longer pause to ensure rendering completes
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

        // Check for game over
        if (gameController.getCurrentPhase() == GameController.GamePhase.GAME_OVER) {
            currentPlayer = gameController.getCurrentPlayer();
            PlayerModel winner = (currentPlayer == gameController.getPlayer1())
                    ? gameController.getPlayer1() : gameController.getPlayer2();

            JOptionPane.showMessageDialog(this,
                    "VICTORY!\n\n" + getPlayerName(winner) + " has destroyed the enemy fleet!\n\nAll enemy ships have been sunk!",
                    "GAME OVER - " + getPlayerName(winner) + " WINS!",
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
            // Show popup for direct hit AFTER board is updated
            JOptionPane.showMessageDialog(this,
                    "DIRECT HIT!\n\nYou have struck an enemy ship!\nYou get another turn.",
                    "HIT!",
                    JOptionPane.INFORMATION_MESSAGE);

            // After dismissing popup, ensure the hit cell is still showing
            attackView.syncWithBoard(gameController.getCurrentPlayer().opponentBoard);
            attackView.revalidate();
            attackView.repaint();

            statusLabel.setText("DIRECT HIT! Take another shot or end your turn.");
            statusLabel.setForeground(new Color(255, 100, 100));
            endTurnButton.setEnabled(true);
        } else {
            // Show popup for miss AFTER board is updated
            JOptionPane.showMessageDialog(this,
                    "MISS!\n\nYour shot missed the target.\nTurn will now switch.",
                    "MISS",
                    JOptionPane.INFORMATION_MESSAGE);

            statusLabel.setText("MISS! Turn switching...");
            statusLabel.setForeground(new Color(150, 200, 255));

            // Switch turn immediately after they dismiss the dialog
            switchTurn();
        }
    }

    private void onEndTurn() {
        switchTurn();
    }

    private void switchTurn() {
        endTurnButton.setEnabled(false);

        // Current player has just finished their turn
        // After the GameController switches internally, getCurrentPlayer returns the NEW player
        // But we need to tell the NEXT player (who hasn't taken their turn yet) to get ready

        // Since attack() already switched the turn in GameController,
        // getCurrentPlayer() now returns the player who is ABOUT to play
        PlayerModel nextPlayer = gameController.getCurrentPlayer();
        String nextPlayerName = getPlayerName(nextPlayer);

        JOptionPane.showMessageDialog(this,
                "SWITCHING PLAYERS\n\n" +
                        "Hand the device to " + nextPlayerName + ".\n\n" +
                        "Make sure " + nextPlayerName + " cannot see the current screen!",
                "Player Switch",
                JOptionPane.INFORMATION_MESSAGE);

        // NOW update display for the new player (after dialog dismissal)
        updateDisplay();
    }

    private void updateDisplay() {
        PlayerModel currentPlayer = gameController.getCurrentPlayer();

        // Update defense view to show new player's board
        defenseView.syncWithBoard(currentPlayer.playerBoard);
        lastDisplayedPlayer = currentPlayer;

        currentPlayerLabel.setText("COMMANDER: " + getPlayerName(currentPlayer));
        statusLabel.setText("Select target coordinates on ENEMY WATERS grid and fire!");
        statusLabel.setForeground(new Color(200, 220, 255));

        // Update attack view for current player's knowledge
        attackView.syncWithBoard(currentPlayer.opponentBoard);
        updateStatsPanel();
    }

    private String getPlayerName(PlayerModel player) {
        return (player == gameController.getPlayer1()) ? "PLAYER 1" : "PLAYER 2";
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