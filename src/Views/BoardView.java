package Views;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.*;
import java.util.List;
import Models.*;
import Models.BoardModel.CellState;

public class BoardView {
    public static void main(String args[]) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        JFrame frame = new JFrame("⚓ Battleship Board");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(650, 650);
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

    // Color scheme
    private static final Color OCEAN_BLUE = new Color(64, 145, 191);
    private static final Color DEEP_OCEAN = new Color(25, 55, 109);
    private static final Color LIGHT_OCEAN = new Color(173, 216, 230);
    private static final Color SHIP_GRAY = new Color(96, 96, 96);
    private static final Color SELECTION_CYAN = new Color(0, 188, 212);
    private static final Color HIT_RED = new Color(244, 67, 54);
    private static final Color MISS_BLUE = new Color(33, 150, 243);
    private static final Color SUNK_DARK_RED = new Color(139, 0, 0);
    private static final Color HOVER_HIGHLIGHT = new Color(255, 235, 59, 80);

    public enum BoardMode {
        SETUP_SELECTION,
        BATTLE_TARGET,
        VIEW_ONLY
    }

    private BoardMode mode = BoardMode.SETUP_SELECTION;
    private AttackListener attackListener;

    public BoardViewPanel(){
        // 11x11 grid to include row and column labels
        gridLayout = new GridLayout(11, 11, 2, 2);
        setLayout(gridLayout);
        setBackground(DEEP_OCEAN);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Top-left corner (empty space)
        JLabel corner = new JLabel("");
        corner.setOpaque(true);
        corner.setBackground(DEEP_OCEAN);
        add(corner);

        // Add column numbers (1-10)
        for(int col = 1; col <= 10; col++){
            JLabel colLabel = new JLabel(String.valueOf(col), SwingConstants.CENTER);
            colLabel.setFont(new Font("Arial", Font.BOLD, 14));
            colLabel.setForeground(Color.WHITE);
            colLabel.setOpaque(true);
            colLabel.setBackground(DEEP_OCEAN);
            add(colLabel);
        }

        // Add rows with letter labels and buttons
        for(int row = 0; row < 10; row++){
            // Add row letter (A-J)
            JLabel rowLabel = new JLabel(ROW_LABELS[row], SwingConstants.CENTER);
            rowLabel.setFont(new Font("Arial", Font.BOLD, 14));
            rowLabel.setForeground(Color.WHITE);
            rowLabel.setOpaque(true);
            rowLabel.setBackground(DEEP_OCEAN);
            add(rowLabel);

            // Add JButtons for this row
            for(int col = 0; col < 10; col++){
                JButton button = createStyledButton(row, col);
                boardButtons[row][col] = button;
                add(button);
            }
        }
    }

    private JButton createStyledButton(int row, int col) {
        JButton button = new JButton();
        button.setOpaque(true);
        button.setBackground(LIGHT_OCEAN);
        button.setForeground(DEEP_OCEAN);
        button.setFont(new Font("Arial", Font.BOLD, 20));
        button.setBorder(new LineBorder(OCEAN_BLUE, 1));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(this);
        button.setActionCommand(row + "," + col);

        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            Color originalColor = button.getBackground();

            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (mode == BoardMode.BATTLE_TARGET || mode == BoardMode.SETUP_SELECTION) {
                    if (button.getText().isEmpty() || mode == BoardMode.SETUP_SELECTION) {
                        button.setBorder(new LineBorder(Color.YELLOW, 2));
                    }
                }
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBorder(new LineBorder(OCEAN_BLUE, 1));
            }
        });

        return button;
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
                Toolkit.getDefaultToolkit().beep();
                return;
            }

            System.out.println("Button Clicked at " + coordinates);

            if(clickedButton.getText().isEmpty()){
                clickedButton.setText("●");
                clickedButton.setForeground(Color.WHITE);
                clickedButton.setBackground(SELECTION_CYAN);
                clickedButton.setFont(new Font("Arial", Font.BOLD, 24));
                clickedButton.setOpaque(true);

                CoordinatesModel coord = new CoordinatesModel(row, col);
                selectedCoordinates.add(coord);
                System.out.println("Added coordinate: (" + row + ", " + col + ")");
            }
            else {
                clickedButton.setText("");
                clickedButton.setBackground(LIGHT_OCEAN);
                clickedButton.setForeground(DEEP_OCEAN);

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
                    boardButtons[row][col].setBackground(LIGHT_OCEAN);
                    boardButtons[row][col].setForeground(DEEP_OCEAN);
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
            btn.setText("■");
            btn.setFont(new Font("Arial", Font.BOLD, 22));
            btn.setForeground(Color.WHITE);
            btn.setBackground(SHIP_GRAY);
            btn.setBorder(new LineBorder(SHIP_GRAY.darker(), 2));
            btn.setOpaque(true);
        }
    }


    public void showBoardFromModel(BoardModel model, boolean hideShips) {
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                CellState state = model.getCellState(row, col);
                JButton btn = boardButtons[row][col];

                btn.setText("");
                btn.setIcon(null);
                btn.setForeground(Color.BLACK);
                btn.setFont(new Font("Arial", Font.BOLD, 24));
                btn.setBorder(new LineBorder(OCEAN_BLUE, 1));

                switch (state) {
                    case EMPTY:
                        btn.setBackground(LIGHT_OCEAN);
                        btn.setText("");
                        break;

                    case SHIP:
                        if (hideShips) {
                            btn.setBackground(LIGHT_OCEAN);
                            btn.setText("");
                        } else {
                            btn.setBackground(SHIP_GRAY);
                            btn.setText("■");
                            btn.setFont(new Font("Arial", Font.BOLD, 22));
                            btn.setForeground(Color.WHITE);
                            btn.setBorder(new LineBorder(SHIP_GRAY.darker(), 2));
                        }
                        break;

                    case MISS:
                        btn.setBackground(LIGHT_OCEAN);
                        btn.setText("●");
                        btn.setForeground(MISS_BLUE);
                        break;

                    case HIT:
                        btn.setBackground(Color.WHITE);
                        btn.setText("✖");
                        btn.setFont(new Font("Arial", Font.BOLD, 28));
                        btn.setForeground(HIT_RED);
                        btn.setBorder(new LineBorder(HIT_RED, 2));
                        break;

                    case SUNK:
                        btn.setBackground(SUNK_DARK_RED);
                        btn.setText("✖");
                        btn.setFont(new Font("Arial", Font.BOLD, 28));
                        btn.setForeground(Color.WHITE);
                        btn.setBorder(new LineBorder(Color.BLACK, 2));
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


interface AttackListener {
    void onAttack(int row, int col);
}