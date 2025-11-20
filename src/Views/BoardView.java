package Views;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import javax.swing.*;

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

    public BoardViewPanel(){
        gridLayout = new GridLayout(9,9);
        setLayout( gridLayout );
        for(int i = 0; i<81; i++){
            JRadioButton rb = new JRadioButton(" " + i);
            rb.addItemListener(this);
            add(rb);
        }

    }

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



}
