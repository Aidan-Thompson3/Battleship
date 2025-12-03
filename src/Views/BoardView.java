package Views;

import Models.CoordinatesModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

public class BoardView {

    // Simple standalone test harness for the board panel
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("BoardView Test");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(600, 600);
            BoardViewPanel panel = new BoardViewPanel();
            frame.add(panel);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}


class BoardViewPanel extends JPanel implements ActionListener, ItemListener {

    private static final String[] ROW_LABELS =
            {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};

    private final JButton[][] boardButtons = new JButton[10][10];

    // Package-visible so GameWindowPanel (same package) can access it
    List<CoordinatesModel> selectedCoordinates = new ArrayList<>();

    // Tracks where permanent ships have been placed
    private final boolean[][] hasShip = new boolean[10][10];

    public BoardViewPanel() {
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

            // Ten buttons for the row
            for (int col = 0; col < 10; col++) {
                JButton button = new JButton();
                button.setOpaque(true);
                button.setBackground(Color.WHITE);
                button.setActionCommand(row + "," + col);
                button.addActionListener(this);

                boardButtons[row][col] = button;
                add(button);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton clickedButton = (JButton) e.getSource();
        String[] parts = clickedButton.getActionCommand().split(",");
        int row = Integer.parseInt(parts[0]);
        int col = Integer.parseInt(parts[1]);

        // Do not allow re-selecting cells that already contain ships
        if (hasShip[row][col]) {
            return;
        }

        // Toggle selection
        CoordinatesModel existing = null;
        for (CoordinatesModel c : selectedCoordinates) {
            if (c.getxCor() == row && c.getyCor() == col) {
                existing = c;
                break;
            }
        }

        if (existing == null) {
            // Select cell
            selectedCoordinates.add(new CoordinatesModel(row, col));
            clickedButton.setBackground(Color.CYAN);
            clickedButton.setText("X");
        } else {
            // Unselect cell
            selectedCoordinates.remove(existing);
            clickedButton.setBackground(Color.WHITE);
            clickedButton.setText("");
        }
    }


    public void clearSelection() {
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                if (!hasShip[row][col]) {
                    boardButtons[row][col].setBackground(Color.WHITE);
                    boardButtons[row][col].setText("");
                }
            }
        }
        selectedCoordinates.clear();
    }


    public void markPlacedShip(List<CoordinatesModel> positions) {
        for (CoordinatesModel pos : positions) {
            int row = pos.getxCor();
            int col = pos.getyCor();
            if (row < 0 || row >= 10 || col < 0 || col >= 10) {
                continue;
            }
            hasShip[row][col] = true;
            boardButtons[row][col].setBackground(Color.GRAY);
            boardButtons[row][col].setText("S");
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {

        if (e.getStateChange() == ItemEvent.SELECTED) {
            System.out.println("Item selected");
        } else {
            System.out.println("Item deselected");
        }
    }
}
