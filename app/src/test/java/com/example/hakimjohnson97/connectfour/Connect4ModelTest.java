package com.example.hakimjohnson97.connectfour;

import org.junit.Test;

import static org.junit.Assert.*;

public class Connect4ModelTest {
    @Test
    public void addBall() throws Exception {
        Connect4Model model = new Connect4Model();
        model.addBall(0, Connect4Model.Color.RED);
        model.addBall(0, Connect4Model.Color.RED);
        model.addBall(4, Connect4Model.Color.RED);
        model.addBall(0, Connect4Model.Color.YELLOW);

        Connect4Model.Color board[][] = model.getBoard();
        assertEquals(Connect4Model.Color.RED, board[0][0]);
        assertEquals(Connect4Model.Color.RED, board[0][1]);
        assertEquals(Connect4Model.Color.RED, board[4][0]);
        assertEquals(Connect4Model.Color.YELLOW, board[0][2]);
        assertEquals(Connect4Model.Color.EMPTY, board[0][3]);
    }

    @Test
    public void checkForWinner() {

        Connect4Model model = new Connect4Model();
        model.addBall(0, Connect4Model.Color.RED);
        model.addBall(0, Connect4Model.Color.RED);
        model.addBall(0, Connect4Model.Color.RED);
        model.addBall(0, Connect4Model.Color.RED);

        assertEquals(true, model.checkForWinner());

        Connect4Model model2 = new Connect4Model();
        model2.addBall(0, Connect4Model.Color.RED);
        model2.addBall(1, Connect4Model.Color.YELLOW);
        model2.addBall(1, Connect4Model.Color.RED);
        model2.addBall(2, Connect4Model.Color.RED);
        model2.addBall(2, Connect4Model.Color.YELLOW);
        model2.addBall(2, Connect4Model.Color.RED);

        model2.addBall(3, Connect4Model.Color.RED);
        model2.addBall(3, Connect4Model.Color.YELLOW);
        model2.addBall(3, Connect4Model.Color.YELLOW);
        model2.addBall(3, Connect4Model.Color.RED);

        assertEquals(true, model2.checkForWinner());

        Connect4Model model3 = new Connect4Model();
        model3.addBall(0, Connect4Model.Color.RED);
        model3.addBall(0, Connect4Model.Color.RED);
        model3.addBall(0, Connect4Model.Color.RED);
        model3.addBall(0, Connect4Model.Color.YELLOW);

        assertEquals(false, model3.checkForWinner());

        Connect4Model model4 = new Connect4Model();
        model4.addBall(0, Connect4Model.Color.YELLOW);
        model4.addBall(1, Connect4Model.Color.YELLOW);
        model4.addBall(2, Connect4Model.Color.YELLOW);
        model4.addBall(3, Connect4Model.Color.YELLOW);

        assertEquals(true, model4.checkForWinner());

        Connect4Model model5 = new Connect4Model();
        model5.addBall(5, Connect4Model.Color.RED);
        model5.addBall(4, Connect4Model.Color.YELLOW);
        model5.addBall(4, Connect4Model.Color.RED);
        model5.addBall(3, Connect4Model.Color.RED);
        model5.addBall(3, Connect4Model.Color.YELLOW);
        model5.addBall(3, Connect4Model.Color.RED);

        model5.addBall(2, Connect4Model.Color.RED);
        model5.addBall(2, Connect4Model.Color.YELLOW);
        model5.addBall(2, Connect4Model.Color.YELLOW);
        model5.addBall(2, Connect4Model.Color.RED);

        assertEquals(true, model5.checkForWinner());

        Connect4Model model6 = new Connect4Model();
        model6.addBall(0, Connect4Model.Color.RED);
        model6.addBall(1, Connect4Model.Color.RED);
        model6.addBall(2, Connect4Model.Color.RED);
        model6.addBall(3, Connect4Model.Color.YELLOW);

        assertEquals(false, model6.checkForWinner());
    }
}
