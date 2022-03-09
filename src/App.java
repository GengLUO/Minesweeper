package src;

import model.Difficulty;
//import model.Minesweeper;
import model.PlayableMinesweeper;
import view.MinesweeperView;
import model.Minesweeper;

public class App {
    public static void main(String[] args) throws Exception {
//        Uncomment the lines below once your game model code is ready; don't forget to import your game model
        PlayableMinesweeper model = new Minesweeper();
        MinesweeperView view = new MinesweeperView(model);
        model.startNewGame(Difficulty.EASY);
        /**
            Your code to bind your game model to the game user interface
        */

    }
}
