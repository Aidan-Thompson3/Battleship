package Views;

import Models.BoardModel;

import javax.swing.*;
import java.awt.*;

public class DefenseView {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("DefenseView Test");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(600, 600);

            DefenseViewPanel panel = new DefenseViewPanel();

            // For testing - simulate some ships and hits
            panel.updateCell(0, 0, BoardModel.CellState.SHIP);
            panel.updateCell(0, 1, BoardModel.CellState.SHIP);
            panel.updateCell(0, 2, BoardModel.CellState.HIT);
            panel.updateCell(1, 0, BoardModel.CellState.MISS);

            frame.add(panel);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}

class DefenseViewPanel extends JPanel {

    private static final String[] ROW_LABELS =
            {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};

    private final JLabel[][] boardLabels = new JLabel[10][10];

    public DefenseViewPanel() {
        GridLayout gridLayout = new GridLayout(11, 11);
        setLayout(gridLayout);

        // Top-left empty corner
        add(new JLabel(""));

        // Column headers (0–9)
        for (int col = 0; col < 10; col++) {
            JLabel colLabel = new JLabel(String.valueOf(col), SwingConstants.CENTER);
            colLabel.setFont(new Font("Arial", Font.BOLD, 12));
            add(colLabel);
        }

        // Rows
        for (int row = 0; row < 10; row++) {
            // Row label on the left
            JLabel rowLabel = new JLabel(ROW_LABELS[row], SwingConstants.CENTER);
            rowLabel.setFont(new Font("Arial", Font.BOLD, 12));
            add(rowLabel);

            // Ten labels for the row
            for (int col = 0; col < 10; col++) {
                JLabel label = new JLabel("", SwingConstants.CENTER);
                label.setOpaque(true);
                label.setBackground(Color.WHITE);
                label.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                label.setPreferredSize(new Dimension(40, 40));

                boardLabels[row][col] = label;
                add(label);
            }
        }
    }

    public void updateCell(int row, int col, BoardModel.CellState state) {
        if (row < 0 || row >= 10 || col < 0 || col >= 10) {
            return;
        }

        JLabel label = boardLabels[row][col];

        switch (state) {
            case EMPTY:
                label.setBackground(Color.WHITE);
                label.setText("");
                break;
            case SHIP:
                label.setBackground(Color.GRAY);
                label.setText("S");
                break;
            case MISS:
                label.setBackground(Color.BLUE);
                label.setText("O");
                break;
            case HIT:
                label.setBackground(Color.RED);
                label.setText("X");
                break;
            case SUNK:
                label.setBackground(Color.DARK_GRAY);
                label.setText("X");
                break;
            default:
                break;
        }
    }

    public void syncWithBoard(BoardModel playerBoard) {
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                BoardModel.CellState state = playerBoard.getCellState(row, col);
                updateCell(row, col, state);
            }
        }
    }

    public void reset() {
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                updateCell(row, col, BoardModel.CellState.EMPTY);
            }
        }
    }
}