package com.example.hakimjohnson97.connectfour;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


/**
 * This is the activity class where the connect 4 game is played
 * Sets up the model, view and controller and starts the game
 */
public class Connect4Activity extends AppCompatActivity {

    public static final String EXTRA_TYPE = "TYPE";

    private Connect4Model mModel;
    private Connect4View mView;
    private Connect4Controller mController;

    private boolean mGameAlreadyEnded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect4);

        Intent intent = getIntent();
        String type = intent.getStringExtra(EXTRA_TYPE);

        Connect4Controller.OpponentType opponentType;
        opponentType = Connect4Controller.OpponentType.valueOf(type);

        mModel = new Connect4Model();

        mView = (Connect4View) findViewById( R.id.connectFourView);
        mView.setModel(mModel);

        mController = new Connect4Controller(mModel, mView, opponentType);

        //Starts game if ONLINE
        if (opponentType == Connect4Controller.OpponentType.ONLINE) {

            MultiplayerHandler mpHandler = new MultiplayerHandler();
            mpHandler.takeExtrasInIntent(intent);

            mpHandler.setEndGameListener(new StringListener() {
                @Override
                public void callback(String message) {
                    mGameAlreadyEnded = true;
                    finish();
                }
            });

            mController.startMultiplayerGame(mpHandler);

        }

        mView.setController(mController);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (!mGameAlreadyEnded) {
            mController.endGame();
        }
    }
}
