public interface GameView
{
    void initalise();
    void updateBoard(GameModel model);
    void showMessage(String message);
    void gameOver (int shots);
}
