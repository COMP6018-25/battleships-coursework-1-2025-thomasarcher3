import javax.swing.SwingUtilities;

public class Battleships {
    public static void main(String[] args) {
        GameModel model = new GameModel();
        boolean cliMode = args.length > 0 && args[0].equalsIgnoreCase("cli");

        if (cliMode) {
            BattleshipsCLI cli = new BattleshipsCLI(model);
            BattleshipsCLI.start();
        } else {
            SwingUtilities.invokeLater((() -> {
                GameView view = new BattleshipsGUI(model,null);
                GameController controller = new BattleshipsGUI(model,view);
                ((BattleshipsGUI)view).setController(controller);
                controller.startGame();
            }));
        }
    }
}
