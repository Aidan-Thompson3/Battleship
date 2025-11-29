package Views;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import javax.swing.*;
import Models.*;

public class BoardView {
    public static void main(String args[]) {
        JFrame frame = new JFrame("title");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600,600);
        BoardViewPanel gwp = new BoardViewPanel();
        frame.add(gwp);
        frame.setVisible(true);
    }
}

class BoardViewPanel extends JPanel implements ActionListener, ItemListener{
    private GridLayout gridLayout;
    private static final String[] ROW_LABELS = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};
    private JButton[][] boardButtons = new JButton[10][10];


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

            // Add radio buttons for this row
            for(int col = 0; col < 10; col++){
                JButton button = new JButton();
                button.setOpaque(true);
                button.addActionListener(this);
                button.setActionCommand(row + "," + col); //Attatches coordinate to each button
                boardButtons[row][col] = button;
                add(button);
            }
        }



    }

    public void actionPerformed(ActionEvent e) {
        JButton clickedButton = (JButton) e.getSource();
        String coordinates = clickedButton.getActionCommand();
        System.out.println("Button Clicked at " + coordinates);

        if(clickedButton.getText().isEmpty()){
            clickedButton.setText("X");
            clickedButton.setBackground(Color.BLUE);
            clickedButton.setOpaque(true);
        }
        else {
            clickedButton.setText("");
            clickedButton.setBackground(Color.WHITE);
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