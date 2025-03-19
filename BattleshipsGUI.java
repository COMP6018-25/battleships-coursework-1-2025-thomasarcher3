import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

public class BattleshipsGUI implements GameView, Observer {
    private GameModel model;
    private GameController controller;
    private JFrame frame;
    private JPanel boardPanel;
    private JLabel messageLabel;

    public BattleshipsGUI(GameModel model) {
        this.model = model;
        model.addObserver(this);
    }


    public void setController(GameController controller) {
        this.controller = controller;
    }

    @Override
    public void initalise() {
        frame = new JFrame("Battleships");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 500);
        boardPanel = new JPanel(new GridLayout(GameModel.BOARD_SIZE, GameModel.BOARD_SIZE));
        messageLabel = new JLabel("click on a box to fire a shot");
        messageLabel.setHorizontalAlignment(JLabel.CENTER);
        frame.setLayout(new BorderLayout());
        frame.add(boardPanel, BorderLayout.CENTER);
        frame.add(messageLabel, BorderLayout.SOUTH);
        createBoardButtons();
        addmenu();
        frame.setVisible(true);
    }

    public void createBoardButtons() {
        boardPanel.removeAll();
        for (int i = 0; i < GameModel.BOARD_SIZE; i++) {
            for (int j = 0; j < GameModel.BOARD_SIZE; j++) {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(40, 40));
                final int row = i;
                final int col = j;
                button.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (controller != null) {
                            String input = "" + (char) ('A' + row) + (col + 1);
                            controller.handleInput(input);
                        }
                    }
                });
                boardPanel.add(button);
            }
        }
        boardPanel.revalidate();
        boardPanel.repaint();
    }

    private void addmenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("Click to load ships");
        JMenuItem loadItem = new JMenuItem("Load Ships");
        loadItem.addActionListener(checkforLoad -> {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                try {
                    model.loadShipsfromaFile(fileChooser.getSelectedFile().getPath());
                    showMessage("Ships loaded successfully");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        fileMenu.add(loadItem);
        menuBar.add(fileMenu);
        frame.setJMenuBar(menuBar);
    }

    @Override
    public void updateBoard(GameModel model) {
        Component[] components = boardPanel.getComponents();
        for (int i = 0; i < GameModel.BOARD_SIZE; i++) {
            for (int j = 0; j < GameModel.BOARD_SIZE; j++) {
                int index = i * GameModel.BOARD_SIZE + j;
                JButton button = (JButton) components[index];
                GameModel.CellStates state = model.getCellState(i, j);
                switch (state) {
                    case HIT:
                        button.setText("H");
                        button.setBackground(Color.RED);
                        break;
                    case MISS:
                        button.setText("M");
                        button.setBackground(Color.BLUE);
                        break;
                    default:
                        button.setText("");
                        button.setBackground(null);


                }
            }
        }
    }

    @Override
    public void showMessage(String message) {
        messageLabel.setText(message);
    }

    @Override
    public void gameOver(int shots) {
        JOptionPane.showMessageDialog(frame, "game over in" + shots + " shots");
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof GameModel) {
            updateBoard((GameModel) o);
            if (arg instanceof String) {
                String message = (String) arg;
                if (message.equals("SHIP_SUNK")) {
                    showMessage("Sunk");
                } else {
                    showMessage(message);
                }
            }
            if (((GameModel) o).isGameOver()) {
                gameOver(((GameModel) o).getShotsFired());
            }
        }
    }
}
