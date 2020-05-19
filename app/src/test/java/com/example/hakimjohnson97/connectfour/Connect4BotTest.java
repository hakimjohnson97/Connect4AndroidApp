package com.example.hakimjohnson97.connectfour;

import org.junit.Test;

import static org.junit.Assert.*;

public class Connect4BotTest {
    @Test
    public void rateBoard() throws Exception {
        Connect4Bot bot = new Connect4Bot();

        //Simple 4 in a row
        Connect4Model model = new Connect4Model();
        model.addBall(0, Connect4Model.Color.RED);
        model.addBall(0, Connect4Model.Color.RED);
        model.addBall(0, Connect4Model.Color.RED);
        model.addBall(0, Connect4Model.Color.RED);
        assertEquals(-1000, bot.rateBoard(model.getBoard()), 0.001);

        //3 in a row with only 1 empty space
        Connect4Model model2 = new Connect4Model();
        model2.addBall(0, Connect4Model.Color.YELLOW);
        model2.addBall(0, Connect4Model.Color.YELLOW);
        model2.addBall(0, Connect4Model.Color.YELLOW);
        assertEquals(Connect4Bot.RATING_THREE_IN_A_ROW, bot.rateBoard(model2.getBoard()), 0.001);

        //3 in a row with an empty spaces on both sides
        Connect4Model model3 = new Connect4Model();
        model3.addBall(2, Connect4Model.Color.YELLOW);
        model3.addBall(3, Connect4Model.Color.YELLOW);
        model3.addBall(4, Connect4Model.Color.YELLOW);
        assertEquals(Connect4Bot.RATING_THREE_IN_A_ROW*2, bot.rateBoard(model3.getBoard()), 0.001);

        //2 in a row with 2 empty spaces on both sides
        Connect4Model model4 = new Connect4Model();
        model4.addBall(3, Connect4Model.Color.YELLOW);
        model4.addBall(4, Connect4Model.Color.YELLOW);
        assertEquals(Connect4Bot.RATING_TWO_IN_A_ROW*2, bot.rateBoard(model4.getBoard()), 0.001);

        //2 in a row with 2 empty spaces on one sides
        Connect4Model model5 = new Connect4Model();
        model5.addBall(1, Connect4Model.Color.YELLOW);
        model5.addBall(2, Connect4Model.Color.YELLOW);
        assertEquals(Connect4Bot.RATING_TWO_IN_A_ROW, bot.rateBoard(model5.getBoard()), 0.001);

        //Fake 2 in a row
        Connect4Model model6 = new Connect4Model();
        model6.addBall(1, Connect4Model.Color.YELLOW);
        model6.addBall(2, Connect4Model.Color.YELLOW);
        model6.addBall(4, Connect4Model.Color.YELLOW);
        model6.addBall(5, Connect4Model.Color.YELLOW);
        assertEquals(Connect4Bot.RATING_THREE_IN_A_ROW, bot.rateBoard(model6.getBoard()), 0.001);

        Connect4Model model7 = new Connect4Model();
        model7.addBall(1, Connect4Model.Color.YELLOW);
        model7.addBall(2, Connect4Model.Color.YELLOW);
        model7.addBall(4, Connect4Model.Color.YELLOW);
        model7.addBall(5, Connect4Model.Color.YELLOW);
        model7.addBall(6, Connect4Model.Color.YELLOW);
        assertEquals(Connect4Bot.RATING_THREE_IN_A_ROW, bot.rateBoard(model7.getBoard()), 0.001);

        //Diagonal 2 in a row
        Connect4Model model8 = new Connect4Model();
        model8.addBall(1, Connect4Model.Color.YELLOW);
        model8.addBall(2, Connect4Model.Color.RED);
        model8.addBall(2, Connect4Model.Color.YELLOW);
        assertEquals(Connect4Bot.RATING_TWO_IN_A_ROW, bot.rateBoard(model8.getBoard()), 0.001);

        //Diagonal 3 in a row
        Connect4Model model9 = new Connect4Model();
        model9.addBall(1, Connect4Model.Color.YELLOW);
        model9.addBall(2, Connect4Model.Color.RED);
        model9.addBall(2, Connect4Model.Color.YELLOW);
        model9.addBall(3, Connect4Model.Color.YELLOW);
        model9.addBall(3, Connect4Model.Color.RED);
        model9.addBall(3, Connect4Model.Color.YELLOW);
        assertEquals(Connect4Bot.RATING_THREE_IN_A_ROW, bot.rateBoard(model9.getBoard()), 0.001);

        //rating of 0
        Connect4Model model10 = new Connect4Model();
        model10.addBall(1, Connect4Model.Color.YELLOW);
        model10.addBall(1, Connect4Model.Color.RED);
        model10.addBall(1, Connect4Model.Color.YELLOW);
        model10.addBall(1, Connect4Model.Color.RED);
        model10.addBall(1, Connect4Model.Color.YELLOW);
        model10.addBall(1, Connect4Model.Color.YELLOW);
        assertEquals(0,  bot.rateBoard(model10.getBoard()), 0.001);
    }
}
