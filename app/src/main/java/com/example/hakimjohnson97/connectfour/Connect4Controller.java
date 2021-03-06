package com.example.hakimjohnson97.connectfour;

import android.os.AsyncTask;
import android.util.Log;

/**
 * This class is the controller of the view and the model. It is the brain of the game.
 * It talks over the network through the MultiplayerHandler class and updates the view whenever the model changes
 */

public class Connect4Controller {

    public enum OpponentType {HOTSEAT, BOT, ONLINE};
    private static final String PLAYER_1_NAME_AGAINST_BOT = "You";

    private Connect4Model mModel;
    private Connect4View mView;

    private boolean mPlayerNumber;
    private OpponentType mOpponentType;
    private MultiplayerHandler mMpHandler;
    private Connect4Bot mBot;

    private boolean mUserPlayAgainPressed = false;
    private boolean mOpponentPlayAgainPressed = false;



    public Connect4Controller(Connect4Model model, Connect4View view, OpponentType opponentType) {

        mModel = model;
        mView = view;
        mOpponentType = opponentType;
        mPlayerNumber = Connect4Model.PLAYER_1;

        if (mOpponentType == OpponentType.BOT) {

            mBot = new Connect4Bot();
            mModel.setPlayer1Name(PLAYER_1_NAME_AGAINST_BOT);
            mModel.setPlayer2Name(Connect4Bot.NAME);

        }
    }


    /**
     * Starts a multiplayer game with the given mpHandler that has already set up a game with an opponent
     * @param mpHandler instance of MultiplayerHandler with a game set up already
     */
    public void startMultiplayerGame(MultiplayerHandler mpHandler) {

        mMpHandler = mpHandler;
        mPlayerNumber = mpHandler.getPlayerNumber();

        //Sets up listeners for the multiplayer game
        final StringListener movesListener = new StringListener() {
            @Override
            public void callback(String message) {

                if (mModel.getPlayerTurn() != mPlayerNumber) {
                    int column = Integer.parseInt(message);
                    mModel.addBall(column, Connect4Model.Color.YELLOW);
                    mModel.checkForWinner();
                    mView.invalidate();

                }
                nextPlayerTurn();

            }
        };

        final StringListener playAgainListener = new StringListener() {
            @Override
            public void callback(String message) {

                if (mModel.hasWinner()) {
                    if (message.equals(mModel.getPlayer2Name())) {
                        mOpponentPlayAgainPressed = true;

                    }
                    else if (message.equals(mModel.getPlayer1Name())) {
                        mUserPlayAgainPressed = true;
                    }

                    if (mUserPlayAgainPressed && mOpponentPlayAgainPressed) {
                        mModel.reset();
                        mView.invalidate();
                        mOpponentPlayAgainPressed = false;
                        mUserPlayAgainPressed = false;
                    }

                }
            }
        };

        mModel.setPlayer1Name(mMpHandler.getUserName());

        mMpHandler.getOpponentName(new StringListener() {
            @Override
            public void callback(String message) {
                mModel.setPlayer2Name(message);
                mView.invalidate();
            }
        });

        mpHandler.startGame(movesListener, playAgainListener);
    }

    /**
     * This function is called whenever the view has detected a touch event on the screen.
     * The controller decides what to do with the event, the view only reports it to the controller
     * @param column column number of the touch event
     */
    public void userTouchedScreen(int column) {

        if (mModel.hasWinner()) {
            return;
        }

        if (mModel.getPlayerTurn() == mPlayerNumber) {

            int row = mModel.addBall(column, Connect4Model.Color.RED);

            //Column is full
            if (row == -1) {
                return;
            }

            mModel.checkForWinner();

            if (mOpponentType == OpponentType.ONLINE) {
                mMpHandler.sendMove(Integer.toString(column));
            }
            else if (mOpponentType == OpponentType.BOT && !mModel.hasWinner()) {
                new BotAsyncTask().execute(mModel.getBoard());
                nextPlayerTurn();
            }
            else {
                nextPlayerTurn();
            }
        }
        else  {

            if (mOpponentType == OpponentType.HOTSEAT) {
                mModel.addBall(column, Connect4Model.Color.YELLOW);
                nextPlayerTurn();
            }

            mModel.checkForWinner();
        }

        mView.invalidate();
    }

    /**
     * This is called when the user clicks the Play Again button after a game is complete.
     * Starts a new game or tells the opponent if its online
     */
    public void userClickedPlayAgain() {

        if (mOpponentType == OpponentType.ONLINE) {

            mMpHandler.playAgainPressed();

        }
        else {

            mModel.reset();
            //Must start the bot if its his turn next game
            if (mOpponentType == OpponentType.BOT && mModel.getPlayerTurn() != mPlayerNumber) {
                new BotAsyncTask().execute(mModel.getBoard());
            }

        }
        mView.invalidate();
    }

    /**
     * Ends the game. Communicates this to opponent if playing online
     */
    public void endGame() {

        if (mOpponentType == OpponentType.ONLINE) {
            mMpHandler.endGame();
        }
    }

    /**
     * Helper function which goes to next player turn and updates the view
     */
    private void nextPlayerTurn() {

        mModel.nextPlayerTurn();
        mView.invalidate();
    }


    /**
     * This class runs the bot on a separate thread so the main thread is not blocked
     * After the bot is run it updates the board
     */
    private class BotAsyncTask extends AsyncTask<Connect4Model.Color[][], Void, Integer> {

        @Override
        protected Integer doInBackground(Connect4Model.Color[][] ... params) {

            return mBot.getNextMove(params[0]);

        }


        @Override
        protected void onPostExecute(Integer move) {

            mModel.addBall(move, Connect4Model.Color.YELLOW);

            nextPlayerTurn();
            mModel.checkForWinner();
            mView.invalidate();

        }

    }


}
