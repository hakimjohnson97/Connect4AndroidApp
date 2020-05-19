package com.example.hakimjohnson97.connectfour;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * This is the activity in which players can create and join games with other players online
 * Provides a several ways to join other players
 */
public class PlayOnlineActivity extends AppCompatActivity {

    public static final String USERS = "users";
    public static final String NAME = "name";
    public static final String CHALLENGES = "challenges";

    private static final String DEFAULT_PLAYER_NAME = "Player";

    private TextView mNameTextView;
    private Button mChangeNameButton;
    private Button mQueueButton;
    private Button mChallengeButton;

    private String mPlayerName;

    private DatabaseReference mNameRef;
    private DatabaseReference mUsersRef;
    private DatabaseReference mMyUserRef;
    private DatabaseReference mChallengeRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_online);

        mNameTextView = (TextView) findViewById(R.id.nameTextView);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mUsersRef = database.getReference(USERS);

        mMyUserRef = mUsersRef.push();
        mNameRef = mMyUserRef.child(NAME);
        mChallengeRef = mMyUserRef.child(CHALLENGES);

        setUpChallengeListener();

        setUpUserName();

        //Connects name on screen to firebase.
        mNameRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.getValue(String.class);
                mNameTextView.setText(getString(R.string.NAME_PREFIX) + " " + name);
                mPlayerName = name;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Sets up each button with the corresponding dialog helper method
        mChangeNameButton = (Button) findViewById(R.id.changeNameButton);
        mChangeNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createChangeNameDialog();
            }
        });

        mQueueButton = (Button) findViewById(R.id.queueButton);
        mQueueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            createQueueDialog();
            }
        });

        mChallengeButton = (Button) findViewById(R.id.challengeButton);
        mChallengeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createChallengeDialog();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //Removes user from database
        mMyUserRef.removeValue();

    }

    /**
     * Creates a dialog where the user can enter the name of the player he wants to challenge.
     * If the ok button is pressed, then a challenge is sent through firebase
     */
    private void createChallengeDialog() {

        final Dialog dialog = new Dialog(PlayOnlineActivity.this);
        dialog.setContentView(R.layout.popup_challenge_player);
        dialog.setCancelable(true);

        final EditText nameEditText = (EditText) dialog.findViewById(R.id.editText);

        Button okButton = (Button) dialog.findViewById(R.id.okButton);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String name = nameEditText.getText().toString();

                mUsersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        //Loops through all users in firebase and checks for conflict
                        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                            if (name.equals(postSnapshot.child(NAME).getValue(String.class))) {
                                dialog.dismiss();
                                createWaitingResponseDialog(name, postSnapshot.child(CHALLENGES).getRef());
                                return;
                            }
                        }

                        makeToast(getString(R.string.PLAYER_NOT_FOUND));

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        dialog.show();
    }

    /**
     * After a challenge is sent, this method is called to wait for a response from the person being challenged
     * @param opponentName name of the opponent
     * @param challengeRef reference to the challenge directory on firebase
     */
    private void createWaitingResponseDialog(String opponentName, DatabaseReference challengeRef) {

        final MultiplayerHandler mpHandler = new MultiplayerHandler(mPlayerName);

        final Dialog dialog = new Dialog(PlayOnlineActivity.this);
        dialog.setContentView(R.layout.popup_challenging);
        dialog.setCancelable(true);

        final Button cancelButton = (Button) dialog.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mpHandler.cancelChallenge();
                dialog.dismiss();
            }
        });

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                mpHandler.cancelChallenge();
                dialog.dismiss();
            }
        });


        dialog.show();

        mpHandler.challengePlayer(challengeRef, new StringListener() {
            @Override
            public void callback(String message) {
                if (message.equals(MultiplayerHandler.DECLINE_CHALLENGE)) {
                    dialog.dismiss();
                    makeToast(getString(R.string.CHALLENGE_DECLINED));
                }
                else if (message.equals(MultiplayerHandler.SUCCESS)) {
                    dialog.dismiss();
                    runConnect4Activity(mpHandler);
                }
            }
        });
    }

    /**
     * This sets up a listener so that if any challenges are sent to the user, the user will be notified
     * by opening a dialog. The user will have a choice of either accepting or declining the challenge
     */
    private void setUpChallengeListener() {

        //Each user has their own place in the database where people can challenge them i.e mChallengeRef
        mChallengeRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(final DataSnapshot dataSnapshot, String s) {

                final Dialog dialog = new Dialog(PlayOnlineActivity.this);
                dialog.setContentView(R.layout.popup_player_challenged);

                TextView challengeTextView = (TextView) dialog.findViewById(R.id.challengeTextView);
                challengeTextView.setText(getString(R.string.CHALLENGED1)
                        + dataSnapshot.getValue(String.class).toString() + R.string.CHALLENGED2);

                Button yesButton = (Button) dialog.findViewById(R.id.yesButton);
                yesButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final MultiplayerHandler mpHandler = new MultiplayerHandler(mPlayerName);
                        //Sets up a listener to callback when the game is finished creating
                        mpHandler.createGame(dataSnapshot.getRef(), new StringListener() {
                            @Override
                            public void callback(String message) {
                                if (message.equals(MultiplayerHandler.SUCCESS)) {
                                    dialog.dismiss();
                                    runConnect4Activity(mpHandler);
                                }
                            }
                        });
                    }
                });

                Button noButton = (Button) dialog.findViewById(R.id.noButton);
                noButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Tells the opponent that the challenge was declined
                        dataSnapshot.getRef().setValue(MultiplayerHandler.DECLINE_CHALLENGE);
                        dialog.dismiss();
                    }
                });


                //Checks if the challenger cancels the challenge
                dataSnapshot.getRef().addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue(String.class).equals(MultiplayerHandler.CANCEL_CHALLENGE)) {
                            dialog.dismiss();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


                dialog.show();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Sets up the user with a a unique name by reading from the database to avoid duplicates
     */
    private void setUpUserName() {

        mUsersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ArrayList<String> users = new ArrayList<String>();

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    users.add(postSnapshot.child(NAME).getValue(String.class));
                }

                //Checks possible player numbers starting at 1 until one is not used already.
                int playerNumber = 1;
                String name;
                while (true) {
                    name = DEFAULT_PLAYER_NAME + playerNumber;
                    boolean notTaken = true;
                    for (String user : users) {
                        if (user.equals(name)) {
                            notTaken = false;
                        }
                    }

                    if (notTaken) {
                        break;
                    }

                    playerNumber++;
                }
                mNameRef.setValue(name);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    /**
     * Opens a dialog which allows the user to change his/her name. Updates firebase with the new name
     * and gives and error if the name already exists
     *
     */
    private void createChangeNameDialog() {

        final Dialog dialog = new Dialog(PlayOnlineActivity.this);
        dialog.setContentView(R.layout.popup_change_name);
        dialog.setCancelable(true);

        final EditText nameEditText = (EditText) dialog.findViewById(R.id.editText);

        int prefixLength = getString(R.string.NAME_PREFIX).length()+1;
        nameEditText.setText(mNameTextView.getText().toString().substring(prefixLength));

        Button okButton = (Button) dialog.findViewById(R.id.okButton);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            mUsersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    String newName = nameEditText.getText().toString();

                    //Checks for any duplicates with the database
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        if (newName.equals(postSnapshot.child(NAME).getValue(String.class))) {
                            makeToast(getString(R.string.NAME_TAKEN));
                            dialog.dismiss();
                            return;
                        }
                    }

                    mNameRef.setValue(newName);
                    dialog.dismiss();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


            }
        });

        dialog.show();
    }

    /**
     * Queues for a game and creates a dialog to show the status. When a game is found, the connect 4 activity
     * is called
     */
    private void createQueueDialog() {

        final MultiplayerHandler mpHandler = new MultiplayerHandler(mPlayerName);

        final Dialog dialog = new Dialog(PlayOnlineActivity.this);
        dialog.setContentView(R.layout.popup_queue_game);
        dialog.setCancelable(true);

        final Button cancelButton = (Button) dialog.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mpHandler.cancelMatchmaking();
                dialog.dismiss();
            }
        });

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                mpHandler.cancelMatchmaking();
                dialog.dismiss();
            }
        });


        dialog.show();


        mpHandler.findMatch(new StringListener() {
            @Override
            public void callback(String message) {
                dialog.dismiss();

                if (message.equals(MultiplayerHandler.SUCCESS)) {
                    runConnect4Activity(mpHandler);
                }
                else if (message.equals(MultiplayerHandler.FAILURE)) {
                    makeToast(getString(R.string.ERROR_CREATING_GAME));
                }


            }
        });
    }

    /**
     * Runs the connect 4 activity and passes the extras neceassary about the game that was created/joined.
     * @param mpHandler MultiplayerHandler where the game was created/joined
     */
    private void runConnect4Activity(MultiplayerHandler mpHandler) {
        Intent intent = new Intent(this, Connect4Activity.class);

        intent.putExtra(Connect4Activity.EXTRA_TYPE, Connect4Controller.OpponentType.ONLINE.toString());

        mpHandler.putExtrasInIntent(intent);

        this.startActivity(intent);
    }

    /**
     * Makes a toast with the given string
     * @param toastString String to be toasted
     */
    private void makeToast(String toastString) {
        Toast.makeText(getApplicationContext(), toastString, Toast.LENGTH_SHORT).show();
    }

}

