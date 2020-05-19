package com.example.hakimjohnson97.connectfour;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * The Main menu of the app. Provides options to play bot, online or hotseat.
 */
public class MainActivity extends AppCompatActivity {

    private Button mPlayHotseatButton;
    private Button mPlayOnlineButton;
    private Button mPlayBotButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPlayHotseatButton = (Button) findViewById(R.id.playHotseatButton);
        mPlayHotseatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runConnect4Activity(Connect4Controller.OpponentType.HOTSEAT.toString());
            }
        });

        mPlayOnlineButton = (Button) findViewById(R.id.playOnlineButton);
        mPlayOnlineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runPlayOnlineActivity();
            }
        });

        mPlayBotButton = (Button) findViewById(R.id.playBotButton);
        mPlayBotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runConnect4Activity(Connect4Controller.OpponentType.BOT.toString());
            }
        });

    }

    /**
     * Runs the connect 4 activity
     * @param opponentType the opponent type
     */
    public void runConnect4Activity(String opponentType) {

        Intent intent = new Intent(this, Connect4Activity.class);
        intent.putExtra(Connect4Activity.EXTRA_TYPE, opponentType);
        this.startActivity(intent);

    }

    /**
     * Runs the play online activity.
     */
    public void runPlayOnlineActivity() {

        Intent intent = new Intent(this, PlayOnlineActivity.class);
        this.startActivity(intent);

    }
}
