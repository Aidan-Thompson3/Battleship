package Views;

import Models.BoardModel;

import javax.swing.*;
import java.awt.*;

public class AttackView {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("AttackView Test");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(600, 600);
            AttackViewPanel panel = new AttackViewPanel();
            frame.add(panel);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}

class AttackViewPanel extends JPanel {

    private static final String[] ROW_LABELS =
            {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};

    private final JButton[][] boardButtons = new JButton[10][10];
    private AttackListener attackListener;

    private final BoardModel.CellState[][] cellStates = new BoardModel.CellState[10][10];

    public interface AttackListener {
        void onAttack(int row, int col);
    }

    public AttackViewPanel() {
        GridLayout gridLayout = new GridLayout(11, 11);
        setLayout(gridLayout);

        // Initialize all cells to EMPTY
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                cellStates[row][col] = BoardModel.CellState.EMPTY;
            }
        }

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

            // Ten buttons for the row
            for (int col = 0; col < 10; col++) {
                JButton button = new JButton();
                button.setOpaque(true);
                button.setBackground(Color.LIGHT_GRAY);
                button.setActionCommand(row + "," + col);

                final int r = row;
                final int c = col;
                button.addActionListener(e -> onCellClicked(r, c));

                boardButtons[row][col] = button;
                add(button);
            }
        }
    }

    private void onCellClicked(int row, int col) {
        if (attackListener != null) {
            attackListener.onAttack(row, col);
        }
    }

    public void setAttackListener(AttackListener listener) {
        this.attackListener = listener;
    }

    public void updateCell(int row, int col, BoardModel.CellState state) {
        if (row < 0 || row >= 10 || col < 0 || col >= 10) {
            return;
        }

        cellStates[row][col] = state;
        JButton button = boardButtons[row][col];

        switch (state) {
            case EMPTY:
                button.setBackground(Color.LIGHT_GRAY);
                button.setText("");
                button.setEnabled(true);
                break;
            case MISS:
                button.setBackground(Color.BLUE);
                button.setText("O");
                button.setEnabled(false);
                break;
            case HIT:
                button.setBackground(Color.RED);
                button.setText("X");
                button.setEnabled(false);
                break;
            case SUNK:
                button.setBackground(Color.DARK_GRAY);
                button.setText("X");
                button.setEnabled(false);
                break;
            default:
                break;
        }
    }

    public void syncWithBoard(BoardModel opponentBoard) {
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                BoardModel.CellState state = opponentBoard.getCellState(row, col);
                updateCell(row, col, state);
            }
        }
    }
}