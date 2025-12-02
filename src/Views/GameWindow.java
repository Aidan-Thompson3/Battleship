package Views;

import Controllers.GameController;
import Models.CoordinatesModel;
import Models.ShipModel;

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
    private GameController gameController;

    private JButton moveButton = new JButton("Taken Pieces 1");
    private JButton moveButton2 = new JButton("Move2");
    private JButton moveButton3 = new JButton("Player 2");
    private JButton moveButton4 = new JButton("Player 1");
    private JButton moveButton5 = new JButton("Taken Piece 2");

    private ShipModel.Orientation selectedOrientation = ShipModel.Orientation.HORIZONTAL;
    private int selectedShipLength = 5;



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
        gameController = new GameController();

        add(boardViewPanel, BorderLayout.CENTER);

        add(moveButton, BorderLayout.NORTH);
        add(moveButton2, BorderLayout.SOUTH);
        add(moveButton4, BorderLayout.WEST);

        // Orientation buttons
        JPanel controlPanel = new JPanel();
        JButton horizontalBtn = new JButton("Horizontal");
        JButton verticalBtn = new JButton("Vertical");
        JButton placeShipBtn = new JButton("Place Ship");

        horizontalBtn.addActionListener(e -> {
            selectedOrientation = ShipModel.Orientation.HORIZONTAL;
            System.out.println("Orientation set to HORIZONTAL");
        });

        verticalBtn.addActionListener(e -> {
            selectedOrientation = ShipModel.Orientation.VERTICAL;
            System.out.println("Orientation set to VERTICAL");
        });

        placeShipBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (boardViewPanel.selectedCoordinates.isEmpty()) {
                    System.out.println("No coordinates selected! Click on cells to select them first.");
                    return;
                }

                // Make a copy of selected coordinates before placing
                List<CoordinatesModel> coordinatesToPlace = new ArrayList<>(boardViewPanel.selectedCoordinates);

                boolean success = gameController.placeShip(coordinatesToPlace);
                if (success) {
                    System.out.println("Ship placed successfully!");

                    // Mark the placed ship cells permanently
                    boardViewPanel.markPlacedShip(coordinatesToPlace);

                    // Clear temporary selection
                    boardViewPanel.clearSelection();

                    gameController.printPlayerShips();

                    // Show progress
                    int shipsPlaced = gameController.getCurrentPlayer().getShips().size();
                    int requiredShips = gameController.getCurrentPlayer().getRequiredShips();
                    System.out.println("Ships placed: " + shipsPlaced + "/" + requiredShips);
                } else {
                    System.out.println("Failed to place ship - invalid placement!");
                }
            }
        });

        controlPanel.add(new JLabel("Orientation:"));
        controlPanel.add(horizontalBtn);
        controlPanel.add(verticalBtn);
        add(controlPanel, BorderLayout.SOUTH);
        add(placeShipBtn,BorderLayout.EAST);

    }
}