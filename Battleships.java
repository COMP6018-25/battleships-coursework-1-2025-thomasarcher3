import javax.swing.SwingUtilities;

public class Battleships {
    public static void main(String[] args) {
        GameModel model = new GameModel();
        boolean cliMode = args.length > 0 && args[0].equalsIgnoreCase("cli");

        if (cliMode) {
            BattleshipsCLI cli = new BattleshipsCLI(model);
            cli.start();
        } else {
            SwingUtilities.invokeLater((() -> {
                BattleshipsGUI view = new BattleshipsGUI(model);
                GameController controller = new BattleshipsGUIController(model, view);
                view.setController(controller);
                controller.startGame();
            }));
        }
    }
}
