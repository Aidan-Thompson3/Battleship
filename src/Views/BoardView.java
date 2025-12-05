package Views;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.swing.*;

import Models.*;
import Models.BoardModel.CellState;

/**
 * Simple test harness for the board on its own.
 */
public class BoardView {
    public static void main(String[] args) {
        JFrame frame = new JFrame("BoardView Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 600);
        BoardViewPanel panel = new BoardViewPanel();
        frame.add(panel);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}

/**
 * 10x10 grid panel used for setup and battle.
 */
class BoardViewPanel extends JPanel implements ActionListener, ItemListener {

    public enum BoardMode {
        SETUP_SELECTION,
        BATTLE_TARGET,
        VIEW_ONLY
    }

    private static final String[] ROW_LABELS =
            {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};

    JButton[][] boardButtons = new JButton[10][10];
    List<CoordinatesModel> selectedCoordinates = new ArrayList<>();
    private boolean[][] hasShip = new boolean[10][10];

    private BoardMode mode = BoardMode.SETUP_SELECTION;
    private AttackListener attackListener;

    // 🔥 Hit icon
    private ImageIcon hitIcon;

    public BoardViewPanel() {
        // Layout: 10x10 board + row/col labels = 11x11
        GridLayout gridLayout = new GridLayout(11, 11);
        setLayout(gridLayout);

        // Top-left corner
        add(new JLabel(""));

        // Column headers (1–10)
        for (int col = 1; col <= 10; col++) {
            JLabel colLabel = new JLabel(String.valueOf(col), SwingConstants.CENTER);
            colLabel.setFont(new Font("Arial", Font.BOLD, 12));
            add(colLabel);
        }

        // Rows
        for (int row = 0; row < 10; row++) {
            // Row label (A–J)
            JLabel rowLabel = new JLabel(ROW_LABELS[row], SwingConstants.CENTER);
            rowLabel.setFont(new Font("Arial", Font.BOLD, 12));
            add(rowLabel);

            for (int col = 0; col < 10; col++) {
                JButton button = new JButton();
                button.setOpaque(true);
                button.setBackground(Color.WHITE);
                button.addActionListener(this);
                button.setActionCommand(row + "," + col);
                boardButtons[row][col] = button;
                add(button);
            }
        }

        loadHitIcon();
    }


    private void loadHitIcon() {
        try {
            // Adjust path/name if you call the file something else
            ImageIcon raw = new ImageIcon(
                    Objects.requireNonNull(BoardViewPanel.class.getResource("/Users/aidanthompson/Desktop/CS/COP3252/Battleship/src/resources"))
            );
            Image scaled = raw.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
            hitIcon = new ImageIcon(scaled);
        } catch (Exception ex) {
            System.err.println("Could not load /fire.png – hits will use X text instead.");
            hitIcon = null;
        }
    }

    public void setMode(BoardMode mode) {
        this.mode = mode;
    }

    public void setAttackListener(AttackListener listener) {
        this.attackListener = listener;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton clickedButton = (JButton) e.getSource();
        String[] parts = clickedButton.getActionCommand().split(",");
        int row = Integer.parseInt(parts[0]);
        int col = Integer.parseInt(parts[1]);

        if (mode == BoardMode.SETUP_SELECTION) {
            if (hasShip[row][col]) {
                // Can't re-select a permanent ship cell
                return;
            }

            // Toggle selection
            if (clickedButton.getText().isEmpty()) {
                clickedButton.setText("X");
                clickedButton.setIcon(null);
                clickedButton.setBackground(Color.CYAN);

                selectedCoordinates.add(new CoordinatesModel(row, col));
            } else {
                clickedButton.setText("");
                clickedButton.setIcon(null);
                clickedButton.setBackground(Color.WHITE);

                selectedCoordinates.removeIf(c ->
                        c.getxCor() == row && c.getyCor() == col);
            }
        } else if (mode == BoardMode.BATTLE_TARGET) {
            // In battle mode, this is an attack
            if (attackListener != null) {
                attackListener.onAttack(row, col);
            }
        } else {
            // VIEW_ONLY – ignore clicks
        }
    }


    public void clearSelection() {
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                if (!hasShip[row][col]) {
                    JButton btn = boardButtons[row][col];
                    btn.setText("");
                    btn.setIcon(null);
                    btn.setBackground(Color.WHITE);
                }
            }
        }
        selectedCoordinates.clear();
    }


    public void markPlacedShip(List<CoordinatesModel> positions) {
        for (CoordinatesModel pos : positions) {
            int row = pos.getxCor();
            int col = pos.getyCor();
            if (row < 0 || row >= 10 || col < 0 || col >= 10) continue;

            hasShip[row][col] = true;
            JButton btn = boardButtons[row][col];
            btn.setIcon(null);
            btn.setText("S");
            btn.setBackground(Color.GRAY);
        }
    }


    public void showBoardFromModel(BoardModel model, boolean hideShips) {
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                CellState state = model.getCellState(row, col);
                JButton btn = boardButtons[row][col];

                // Always clear icon/text first
                btn.setIcon(null);
                btn.setText("");

                switch (state) {
                    case EMPTY:
                        btn.setBackground(Color.WHITE);
                        break;

                    case SHIP:
                        if (hideShips) {
                            btn.setBackground(Color.WHITE);
                        } else {
                            btn.setBackground(Color.GRAY);
                            btn.setText("S");
                        }
                        break;

                    case MISS:
                        btn.setBackground(Color.BLUE);
                        btn.setText("O");
                        break;

                    case HIT:
                    case SUNK:
                        btn.setBackground(Color.WHITE); // or Color.RED if you want
                        if (hitIcon != null) {
                            btn.setIcon(hitIcon);   // 🔥 use the flame image
                        } else {
                            btn.setText("X");       // fallback if icon missing
                        }
                        break;
                }

                btn.setOpaque(true);
            }
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        // Not used, but kept for interface compatibility
    }
}


interface AttackListener {
    void onAttack(int row, int col);
}
