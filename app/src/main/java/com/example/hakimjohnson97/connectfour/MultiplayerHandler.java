package com.example.hakimjohnson97.connectfour;

import android.content.Intent;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * This class handles all activity with other players online through firebase
 * such as creating/joining a game and communicating between players within a game
 */

public class MultiplayerHandler {

    public static final String MATCHMAKER = "matchmaker";
    public static final String GAMES = "games";

    public static final String MOVES = "moves";
    public static final String END_GAME = "endGame";
    public static final String PLAY_AGAIN = "playAgain";

    public static final String CANCEL_CHALLENGE = "Cancel";
    public static final String DECLINE_CHALLENGE = "No";

    public static final String PLAYER_1 = "player1";
    public static final String PLAYER_2 = "player2";

    public static final String SUCCESS = "success";
    public static final String FAILURE = "failure";

    public static final String MOVES_DEFAULT_VALUE = "-1";

    private static final String MATCHMAKER_NONE = "none";
    private static final String EXTRA_GAME = "GAME";
    private static final String EXTRA_NAME = "NAME";
    private static final String EXTRA_PLAYER_NUMBER = "PLAYER_NUMBER";

    private FirebaseDatabase database;
    private DatabaseReference mMatchmaker;
    private DatabaseReference mGamesReference;

    private DatabaseReference mCurrentGameReference;
    private DatabaseReference mMovesReference;
    private DatabaseReference mCurrentChallengeReference;

    private boolean mPlayerNumber;
    private boolean mGameHasEnded = false;
    private boolean mChallengeCancelled = false;

    private StringListener mEndGameListener;

    private String mPlayerName;

    public MultiplayerHandler() {

        database = FirebaseDatabase.getInstance();
        mMatchmaker = database.getReference(MATCHMAKER);
        mGamesReference = database.getReference(GAMES);

    }

    public MultiplayerHandler(String playerName) {

        this();

        mPlayerName = playerName;
    }

    /**
     * Sends a challenge to a player through firebase and listens for a response
     * @param challengesRef a reference to the challenges directory of the firebase database
     * @param listener listener to callback to after the opponent responds
     *                 sends DECLINE_CHALLENGE if challenge is declined
     *                 or else joins the game
     */
    public void challengePlayer(DatabaseReference challengesRef, final StringListener listener) {

        mChallengeCancelled = false;

        mCurrentChallengeReference = challengesRef.push();
        mCurrentChallengeReference.setValue(mPlayerName);

        //Listens for a response after setting the value above
        mCurrentChallengeReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                if (value.equals(mPlayerName)) {
                    return;
                }

                if (value.equals(DECLINE_CHALLENGE)) {
                    listener.callback(DECLINE_CHALLENGE);
                }
                else if (mChallengeCancelled == false) {

                    joinGame(value, listener);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    /**
     * Cancels a previously sent challenge
     */
    public void cancelChallenge() {

        mCurrentChallengeReference.setValue(CANCEL_CHALLENGE);
        mChallengeCancelled = true;

    }

    /**
     * Finds a game through matchmaker, creates a game if is first arriver and joins a game if is second arriver
     */
    public void findMatch(final StringListener listener) {

        mMatchmaker.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                final String matchmaker = dataSnapshot.getValue(String.class);

                if (matchmaker.equals(MATCHMAKER_NONE)) {
                    createGame(mMatchmaker, listener);
                }
                else {
                    mMatchmaker.setValue(MATCHMAKER_NONE);
                    joinGame(matchmaker, listener);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Cancels a previously made matchmaking process
     */
    public void cancelMatchmaking() {

        mCurrentGameReference.removeValue();
        mMatchmaker.setValue(MATCHMAKER_NONE);

    }

    /**
     * Creates a game through firebase and waits for another user to join it, then it calls back through the listener
     * @param gameStored stores the key of the game in this location
     * @param listener calls back to this listener when another user joins the game
     */
    public void createGame(DatabaseReference gameStored, final StringListener listener) {

        String matchmaker;
        final DatabaseReference dbReference = mGamesReference.push();
        mCurrentGameReference = dbReference;

        dbReference.child(PLAYER_1).setValue(mPlayerName);
        matchmaker = dbReference.getKey();

        gameStored.setValue(matchmaker);

        //Listens for a player to join the game, and then calls back to the listener
        mCurrentGameReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                String name = dataSnapshot.getValue(String.class);

                if (!name.equals(mPlayerName)) {
                    mPlayerNumber = Connect4Model.PLAYER_1;

                    listener.callback(SUCCESS);
                    mCurrentGameReference.removeEventListener(this);
                }
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
     * Joins an already existing game and callbacks to the listener
     * @param matchmaker String storing the key of hte game
     * @param listener listener to be called back to
     */
    private void joinGame(final String matchmaker, final StringListener listener) {

        //Sets up child subtrees needed for the game
        mCurrentGameReference = mGamesReference.child(matchmaker);
        mCurrentGameReference.child(MOVES).setValue(MOVES_DEFAULT_VALUE);
        mCurrentGameReference.child(END_GAME).setValue(Boolean.FALSE.toString());
        mCurrentGameReference.child(PLAY_AGAIN).setValue("");
        mCurrentGameReference.child(PLAYER_2).setValue(mPlayerName);

        mPlayerNumber = Connect4Model.PLAYER_2;
        listener.callback(SUCCESS);

    }

    /**
     * Saves extras relevant to matchmaking in an intent
     * @param intent intent to store the data
     */
    public void putExtrasInIntent(Intent intent) {

        intent.putExtra(EXTRA_GAME, mCurrentGameReference.getKey());
        intent.putExtra(EXTRA_NAME, mPlayerName);
        intent.putExtra(EXTRA_PLAYER_NUMBER, mPlayerNumber);
    }

    /**
     * retrives data from an intent
     * @param intent
     */
    public void takeExtrasInIntent(Intent intent) {

        mCurrentGameReference = mGamesReference.child(intent.getStringExtra(EXTRA_GAME));
        mPlayerName = intent.getStringExtra(EXTRA_NAME);
        mPlayerNumber = intent.getBooleanExtra(EXTRA_PLAYER_NUMBER, false);
    }

    public boolean getPlayerNumber() {
        return mPlayerNumber;
    }

    /**
     * Sets up the listener needed to end the game when the other player leaves
     * @param endGameListener
     */
    public void setEndGameListener(StringListener endGameListener) {

        mEndGameListener = endGameListener;
    }

    /**
     * Starts a game after creating/joining one. Sets up all the necessary communication with the opponent
     * @param movesListener
     * @param playAgainListener
     */
    public void startGame(final StringListener movesListener, final StringListener playAgainListener) {

        mMovesReference = mCurrentGameReference.child(MOVES);

        //Sets up the listeners for the values on the database where the user can talk to the game

        mMovesReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String message =  dataSnapshot.getValue(String.class);
                movesListener.callback(message);
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

        mCurrentGameReference.child(PLAY_AGAIN).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                playAgainListener.callback(dataSnapshot.getValue(String.class));
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

        mCurrentGameReference.child(END_GAME).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (!mGameHasEnded) {
                    if (dataSnapshot.getValue(String.class).equals("true")) {

                        mCurrentGameReference.child(END_GAME).removeEventListener(this);

                        mCurrentGameReference.removeValue();

                        mEndGameListener.callback("");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    /**
     * Sends a move to the opponent
     * @param move column number of board in form of a String
     */
    public void sendMove(String move) {
        mMovesReference.push().setValue(move);
    }

    /**
     * Tells the opponent that the Play Again button was pressed
     */
    public void playAgainPressed() {
        mCurrentGameReference.child(PLAY_AGAIN).push().setValue(mPlayerName);
    }

    /**
     * Ends the game and notifies the opponent
     */
    public void endGame() {

        mGameHasEnded = true;
        mCurrentGameReference.child(END_GAME).setValue("true");

    }

    public String getUserName() {
        return mPlayerName;
    }

    /**
     * Retrieves the opponent's name through the database and gives it back through the callback listener
     * @param callback listener to callback to
     */
    public void getOpponentName(final StringListener callback) {

        String opponentString;
        if (mPlayerNumber == Connect4Model.PLAYER_1) {
            opponentString = PLAYER_2;
        }
        else {
            opponentString = PLAYER_1;
        }

        //Gets the name from the database
        mCurrentGameReference.child(opponentString).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                callback.callback(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
