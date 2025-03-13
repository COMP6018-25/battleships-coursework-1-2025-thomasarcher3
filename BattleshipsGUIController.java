public class BattleshipsGUIController implements GameController {
    private GameModel model;
    private GameView view;

    public BattleshipsGUIController(GameModel model, GameView view) {
        this.model = model;
        this.view = view;
    }

    @Override
    public void handleInput(String input) {
        try {
            int row = input.charAt(0) - '0';
            int col = Integer.parseInt(input.substring(1)) - 1;
            if (row < 0 || row >= GameModel.BOARD_SIZE || col < 0 || col >= GameModel.BOARD_SIZE) {
                view.showMessage("Invalid row/col number");
                return;
            }

            boolean hit = model.fireShot(row, col);
            if (hit) {
                view.showMessage("Hit");
            } else {
                view.showMessage("Miss");
            }

            view.updateBoard(model);

            if(model.isGameOver()) {
                view.gameOver(model.getShotsFired());
            }
        } catch (Exception e) {
            view.showMessage("cordiates are not valid");
        }
    }

    @Override
    public void startGame() {
        view.initalise();
        view.updateBoard(model);
    }
}
