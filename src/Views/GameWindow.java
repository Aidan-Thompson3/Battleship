package Views;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import javax.swing.*;

public class GameWindow {
    public static void main(String args[]){
        JFrame frame = new JFrame("title");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600,600);
        GameWindowPanel gwp = new GameWindowPanel();
        frame.add(gwp);
        frame.setVisible(true);
    }
}

class GameWindowPanel extends JPanel implements ActionListener, ItemListener {

    private BorderLayout layout;
    private GridLayout gridLayout;
    private BoardViewPanel boardViewPanel;

    private JButton moveButton = new JButton("Taken Pieces 1");
    private JButton moveButton2 = new JButton("Move2");
    private JButton moveButton3 = new JButton("Player 2");
    private JButton moveButton4 = new JButton("Player 1");
    private JButton moveButton5 = new JButton("Taken Piece 2");
    //private JCheckBox checkBox = new JCheckBox("Taken Piece 2");



    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("Action Performed run");
        return;
    }

    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            System.out.println("Checked!");
        } else {
            System.out.println("Unchecked!");
        }
    }

    GameWindowPanel(){
        System.out.println("Game Window Panel constructor");
        layout = new BorderLayout( 5, 5 );
        setLayout( layout );
        boardViewPanel = new BoardViewPanel();

        add(boardViewPanel, BorderLayout.CENTER);

        add(moveButton, BorderLayout.NORTH);
        add(moveButton2, BorderLayout.SOUTH);
        add(moveButton3, BorderLayout.EAST);
        add(moveButton4, BorderLayout.WEST);

    }
}
