package Views;

import Controllers.GameController;
import Models.CoordinatesModel;
import Models.PlayerModel;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
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

    private BoardViewPanel setupBoardPanel;
    private JPanel setupControlPanel;
    private JButton placeShipBtn;

    private JPanel battlePanel;
    private BoardViewPanel playerBoardPanel;
    private BoardViewPanel targetBoardPanel;

    private JLabel phaseLabel = new JLabel();
    private JLabel currentPlayerLabel = new JLabel();
    private JLabel shipsPlacedLabel = new JLabel();
    private JLabel messageLabel =
            new JLabel("Place your ships by selecting cells and clicking 'Place Ship'.");

    private final int[] shipLengths = {5, 4, 3, 3, 2};
    private int currentShipIndex = 0;

    private boolean attackAnimating = false;

    GameWindowPanel(){
        setLayout(new BorderLayout(5,5));

        gameController = new GameController();

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

        setupBoardPanel = new BoardViewPanel();
        setupBoardPanel.setMode(BoardViewPanel.BoardMode.SETUP_SELECTION);
        add(setupBoardPanel, BorderLayout.CENTER);

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

                JOptionPane.showMessageDialog(this,
                        "Finished placing ships for " + getPlayerName(before) +
                                ". Hand the computer to the next player.",
                        "Switch Player",
                        JOptionPane.INFORMATION_MESSAGE);

                currentShipIndex = 0;

                remove(setupBoardPanel);
                setupBoardPanel = new BoardViewPanel();
                setupBoardPanel.setMode(BoardViewPanel.BoardMode.SETUP_SELECTION);
                add(setupBoardPanel, BorderLayout.CENTER);
            }

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
        remove(setupBoardPanel);

        battlePanel = new JPanel(new GridLayout(1, 2, 10, 10));
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

        battlePanel.add(playerBoardPanel);
        battlePanel.add(targetBoardPanel);

        add(battlePanel, BorderLayout.CENTER);

        setupControlPanel.setVisible(false);
        placeShipBtn.setVisible(false);

        messageLabel.setText("Battle started! " +
                getPlayerName(gameController.getCurrentPlayer()) +
                " attacks first. Click on the right board to fire.");

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
        if (attackAnimating) {
            return;
        }
        if (gameController.getCurrentPhase() == GameController.GamePhase.GAME_OVER) {
            return;
        }

        attackAnimating = true;

        boolean hit = gameController.attack(row, col);

        if (hit) {
            playExplosionSound();
            messageLabel.setText("HIT at (" + row + ", " + col + ")! You go again.");
        } else {
            messageLabel.setText("Miss or already targeted at (" + row + ", " + col + ").");
        }

        refreshBattleBoards();

        if (gameController.getCurrentPhase() == GameController.GamePhase.GAME_OVER) {
            String winner = getPlayerName(gameController.getCurrentPlayer());
            JOptionPane.showMessageDialog(this,
                    winner + " wins! All enemy ships have been sunk.",
                    "Game Over",
                    JOptionPane.INFORMATION_MESSAGE);
            attackAnimating = false;
            updateStatusLabels();
            return;
        }

        if (hit) {
            attackAnimating = false;
            updateStatusLabels();
            return;
        }

        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ((Timer)e.getSource()).stop();

                gameController.endTurn();
                refreshBattleBoards();
                updateStatusLabels();

                messageLabel.setText("Now it's " +
                        getPlayerName(gameController.getCurrentPlayer()) + "'s turn.");

                attackAnimating = false;
            }
        });
        timer.setRepeats(false);
        timer.start();
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

    private void playExplosionSound() {
        try {
            java.net.URL url = getClass().getResource("/sounds/loud-explosion-425457.mp3");
            if (url == null) {
                System.err.println("Explosion sound not found on classpath.");
                return;
            }
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
    }
}
