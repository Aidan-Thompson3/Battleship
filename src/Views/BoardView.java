package Views;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import javax.swing.*;
import java.util.List;
import Models.*;
import Models.BoardModel.CellState;

public class BoardView {
    public static void main(String args[]) {
        JFrame frame = new JFrame("title");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600,600);
        BoardViewPanel gwp = new BoardViewPanel();
        frame.add(gwp);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}

class BoardViewPanel extends JPanel implements ActionListener, ItemListener{
    private GridLayout gridLayout;
    private static final String[] ROW_LABELS = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};
    JButton[][] boardButtons = new JButton[10][10];

    // used during setup
    List<CoordinatesModel> selectedCoordinates = new ArrayList<>();
    private boolean[][] hasShip = new boolean[10][10];

    public enum BoardMode {
        SETUP_SELECTION,
        BATTLE_TARGET,
        VIEW_ONLY
    }

    private BoardMode mode = BoardMode.SETUP_SELECTION;
    private AttackListener attackListener;

    public BoardViewPanel(){
        // 10x10 grid to include row and column labels
        gridLayout = new GridLayout(11, 11);
        setLayout(gridLayout);

        // Top-left corner (empty space)
        JLabel corner = new JLabel("");
        add(corner);

        // Add column numbers (1-10)
        for(int col = 1; col <= 10; col++){
            JLabel colLabel = new JLabel(String.valueOf(col), SwingConstants.CENTER);
            colLabel.setFont(new Font("Arial", Font.BOLD, 12));
            add(colLabel);
        }

        // Add rows with letter labels and buttons
        for(int row = 0; row < 10; row++){
            // Add row letter (A-J)
            JLabel rowLabel = new JLabel(ROW_LABELS[row], SwingConstants.CENTER);
            rowLabel.setFont(new Font("Arial", Font.BOLD, 12));
            add(rowLabel);

            // Add JButtons for this row
            for(int col = 0; col < 10; col++){
                JButton button = new JButton();
                button.setOpaque(true);
                button.addActionListener(this);
                button.setActionCommand(row + "," + col); // attach coordinate
                boardButtons[row][col] = button;
                add(button);
            }
        }
    }

    public void setMode(BoardMode mode) {
        this.mode = mode;
    }

    public void setAttackListener(AttackListener listener) {
        this.attackListener = listener;
    }

    public void actionPerformed(ActionEvent e) {
        JButton clickedButton = (JButton) e.getSource();
        String coordinates = clickedButton.getActionCommand();

        String[] parts = coordinates.split(",");
        int row = Integer.parseInt(parts[0]);
        int col = Integer.parseInt(parts[1]);

        if (mode == BoardMode.SETUP_SELECTION) {
            // Don't allow selecting cells that already have ships
            if (hasShip[row][col]) {
                System.out.println("Cannot select (" + row + ", " + col + ") - ship already placed here!");
                return;
            }

            System.out.println("Button Clicked at " + coordinates);

            if(clickedButton.getText().isEmpty()){
                clickedButton.setText("O");
                clickedButton.setForeground(Color.BLACK);
                clickedButton.setBackground(Color.CYAN); // temp selection
                clickedButton.setOpaque(true);

                CoordinatesModel coord = new CoordinatesModel(row, col);
                selectedCoordinates.add(coord);
                System.out.println("Added coordinate: (" + row + ", " + col + ")");
            }
            else {
                clickedButton.setText("");
                clickedButton.setBackground(Color.WHITE);
                clickedButton.setForeground(Color.BLACK);

                selectedCoordinates.removeIf(c -> c.getxCor() == row && c.getyCor() == col);
                System.out.println("Removed coordinate: (" + row + ", " + col + ")");
            }
        } else if (mode == BoardMode.BATTLE_TARGET) {
            // in battle mode, this is an attack
            if (attackListener != null) {
                attackListener.onAttack(row, col);
            }
        } else {
            // VIEW_ONLY – ignore clicks
        }
    }

    public void clearSelection() {
        // Only clear cells that don't have ships placed on them
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                if (!hasShip[row][col]) {
                    boardButtons[row][col].setText("");
                    boardButtons[row][col].setBackground(Color.WHITE);
                    boardButtons[row][col].setForeground(Color.BLACK);
                }
            }
        }
        selectedCoordinates.clear();
        System.out.println("Selection cleared (ships remain marked)");
    }

    public void markPlacedShip(List<CoordinatesModel> positions) {
        // Mark these cells as having ships and make them permanently colored
        for (CoordinatesModel pos : positions) {
            int row = pos.getxCor();
            int col = pos.getyCor();
            hasShip[row][col] = true;
            JButton btn = boardButtons[row][col];
            btn.setText("O");                      // ship circle
            btn.setForeground(Color.DARK_GRAY);
            btn.setBackground(Color.GRAY);         // Permanent gray for placed ships
            btn.setOpaque(true);
        }
    }

    /**
     * Used in battle to render the board.
     * MISS: blue circle
     * HIT:  red circle
     * SUNK: darker red circle
     */
    public void showBoardFromModel(BoardModel model, boolean hideShips) {
        Color sunkColor = new Color(139, 0, 0); // dark red

        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                CellState state = model.getCellState(row, col);
                JButton btn = boardButtons[row][col];

                btn.setText("");
                btn.setIcon(null);
                btn.setForeground(Color.BLACK);

                switch (state) {
                    case EMPTY:
                        btn.setBackground(Color.WHITE);
                        break;

                    case SHIP:
                        if (hideShips) {
                            btn.setBackground(Color.WHITE);
                        } else {
                            btn.setBackground(Color.GRAY);
                            btn.setText("O");
                            btn.setForeground(Color.DARK_GRAY);
                        }
                        break;

                    case MISS:
                        btn.setBackground(Color.WHITE);
                        btn.setText("O");
                        btn.setForeground(Color.BLUE);   // blue circle = miss
                        break;

                    case HIT:
                        btn.setBackground(Color.WHITE);
                        btn.setText("O");
                        btn.setForeground(Color.RED);    // red circle = hit
                        break;

                    case SUNK:
                        btn.setBackground(Color.WHITE);
                        btn.setText("O");
                        btn.setForeground(sunkColor);    // darker red = sunk
                        break;
                }

                btn.setOpaque(true);
            }
        }
    }

    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            System.out.println("Checked!");

        } else {
            System.out.println("Unchecked!");
        }
    }
}

/**
 * Used by GameWindow when the board is in BATTLE_TARGET mode.
 */
interface AttackListener {
    void onAttack(int row, int col);
}
