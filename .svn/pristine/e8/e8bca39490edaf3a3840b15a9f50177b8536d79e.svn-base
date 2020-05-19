package com.example.hakimjohnson97.connectfour;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * This is the view class which draws the board, score and player names.
 * It receives touch events and notifies the controller
 */

public class Connect4View extends View {

    private static final float BALL_PADDING_RATIO = 0.1f;
    private static final int TEXT_PADDING = 30;
    private static final int TURN_LINE_LENGTH = 100;
    private static final int TURN_LINE_HEIGHT = 23;

    private static final int PLAY_AGAIN_BUTTON_WIDTH = 212;
    private static final int PLAY_AGAIN_BUTTON_HEIGHT = 58;

    private static final int BACKGROUND_COLOR = 0xFF3399FF;
    private static final int LAST_BALL_WIDTH = 2;

    private static final int NAME_TEXT_SIZE = 60;
    private static final int SCORE_TEXT_SIZE = 125;
    private static final int WIN_STROKE_WIDTH = 7;

    private Connect4Model mModel;
    private Connect4Controller mController;

    private Paint mBoardPaint;
    private Paint mNameTextPaint;
    private Paint mScoreTextPaint;
    private Paint mTurnLinePaint;
    private Paint mWinPaint;

    private float mGridWidth;
    private float mGridHeight;

    private float mPlayer1PosX;
    private float mPlayer2PosX;
    private float mCenterPos;

    private Rect mBoardRect;
    private Rect mPlayAgainRect;


    public Connect4View(Context context, @Nullable AttributeSet attrs) {

        super(context, attrs);

        setBackgroundColor(BACKGROUND_COLOR);

        //Sets up the paint specs
        mBoardPaint = new Paint();
        mBoardPaint.setAntiAlias(true);
        mBoardPaint.setColor(Color.RED);
        mBoardPaint.setStyle(Paint.Style.FILL);
        mBoardPaint.setStrokeJoin(Paint.Join.ROUND);
        mBoardPaint.setStrokeWidth(LAST_BALL_WIDTH);

        mNameTextPaint = new Paint();
        mNameTextPaint.setColor(Color.WHITE);
        mNameTextPaint.setTextSize(NAME_TEXT_SIZE);
        mNameTextPaint.setTextAlign(Paint.Align.CENTER);

        mScoreTextPaint = new Paint();
        mScoreTextPaint.setColor(Color.WHITE);
        mScoreTextPaint.setTextSize(SCORE_TEXT_SIZE);
        mScoreTextPaint.setTextAlign(Paint.Align.CENTER);

        mTurnLinePaint = new Paint();
        mBoardPaint.setColor(Color.RED);

        mWinPaint = new Paint();
        mWinPaint.setColor(Color.BLACK);
        mWinPaint.setStrokeJoin(Paint.Join.ROUND);
        mWinPaint.setStrokeWidth(WIN_STROKE_WIDTH);

    }

    public void setModel(Connect4Model model) {
        mModel = model;
    }

    public void setController(Connect4Controller controller) {
        mController = controller;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        super.onSizeChanged(w, h, oldw, oldh);

        mPlayer1PosX = w/4;
        mCenterPos = w/2;
        mPlayer2PosX = (3*w)/4;

        mGridWidth = w/ Connect4Model.BOARD_WIDTH;
        mGridHeight = mGridWidth;

        mBoardRect = new Rect(0, (int)(h - mGridHeight *Connect4Model.BOARD_HEIGHT), w, h);

        mPlayAgainRect = new Rect( (int) mCenterPos - PLAY_AGAIN_BUTTON_WIDTH/2,
                (int)( mBoardRect.bottom - mGridHeight *Connect4Model.BOARD_HEIGHT - PLAY_AGAIN_BUTTON_HEIGHT - TEXT_PADDING ),
                (int) mCenterPos + PLAY_AGAIN_BUTTON_WIDTH/2,
                (int)( mBoardRect.bottom - mGridHeight *Connect4Model.BOARD_HEIGHT) - TEXT_PADDING);
        

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mModel == null) {
            return;
        }

        drawBoard(canvas);
        drawScoreText(canvas);
        drawWinningMove(canvas);
        drawPlayAgainButton(canvas);
    }

    /**
     * Draws the board with balls on the canvas
     * @param canvas
     */
    private void drawBoard(Canvas canvas) {
        Connect4Model.Color board[][] = mModel.getBoard();

        for (int i = 0; i < Connect4Model.BOARD_WIDTH; i++) {
            for (int j = 0; j < Connect4Model.BOARD_HEIGHT; j++) {

                if (board[i][j] == Connect4Model.Color.EMPTY) {
                    mBoardPaint.setColor(Color.WHITE);
                }
                else if (board[i][j] == Connect4Model.Color.RED) {
                    mBoardPaint.setColor(Color.RED);
                }
                else if (board[i][j] == Connect4Model.Color.YELLOW) {
                    mBoardPaint.setColor(Color.YELLOW);
                }

                canvas.drawOval(mBoardRect.left + (mGridWidth *(i+ BALL_PADDING_RATIO)), mBoardRect.bottom - (mGridHeight *(j+1- BALL_PADDING_RATIO)),
                        mBoardRect.left + (mGridWidth *(i+1- BALL_PADDING_RATIO)), mBoardRect.bottom - (mGridHeight *(j+ BALL_PADDING_RATIO)), mBoardPaint);

                //Draws a green outline on the last placed ball
                if (mModel.getLastPlacedBall() != null && mModel.getLastPlacedBall().equals(i, j)) {

                    mBoardPaint.setStyle(Paint.Style.STROKE);
                    mBoardPaint.setColor(Color.GREEN);
                    canvas.drawOval(mBoardRect.left + (mGridWidth *(i+ BALL_PADDING_RATIO)), mBoardRect.bottom - (mGridHeight *(j+1- BALL_PADDING_RATIO)),
                            mBoardRect.left + (mGridWidth *(i+1- BALL_PADDING_RATIO)), mBoardRect.bottom - (mGridHeight *(j+ BALL_PADDING_RATIO)), mBoardPaint);
                    mBoardPaint.setStyle(Paint.Style.FILL);

                }
            }
        }

    }

    /**
     * Draws the score on the canvas
     * @param canvas
     */
    private void drawScoreText(Canvas canvas) {

        //Calculates the height of the text being drawn so the items can be organized
        int nameHeight = getTextRect(mModel.getPlayer1Name(), mNameTextPaint).height() + TEXT_PADDING;
        canvas.drawText(mModel.getPlayer1Name(), mPlayer1PosX, nameHeight, mNameTextPaint);
        canvas.drawText(mModel.getPlayer2Name(), mPlayer2PosX, nameHeight, mNameTextPaint);

        int scoreHeight = nameHeight + getTextRect("0", mScoreTextPaint).height() + TEXT_PADDING;
        canvas.drawText(Integer.toString(mModel.getPlayer1Score()), mPlayer1PosX, scoreHeight, mScoreTextPaint);
        canvas.drawText(Integer.toString(mModel.getPlayer2Score()), mPlayer2PosX, scoreHeight, mScoreTextPaint);
        canvas.drawText("-", mCenterPos, scoreHeight, mScoreTextPaint);

        //Draws a line under the player whose turn it is
        if (mModel.getPlayerTurn() == Connect4Model.PLAYER_1) {
            mTurnLinePaint.setColor(Color.RED);
            canvas.drawRect(mPlayer1PosX - TURN_LINE_LENGTH/2, scoreHeight + TEXT_PADDING, mPlayer1PosX + TURN_LINE_LENGTH/2,
                    scoreHeight + TEXT_PADDING + TURN_LINE_HEIGHT, mTurnLinePaint);
        }
        else {
            mTurnLinePaint.setColor(Color.YELLOW);
            canvas.drawRect(mPlayer2PosX - TURN_LINE_LENGTH/2, scoreHeight + TEXT_PADDING, mPlayer2PosX + TURN_LINE_LENGTH/2,
                    scoreHeight + TEXT_PADDING + TURN_LINE_HEIGHT, mTurnLinePaint);
        }
    }

    /**
     * Draws the winning move on the canvas if the game is over
     * @param canvas
     */
    private void drawWinningMove(Canvas canvas) {

        if (mModel.hasWinner() == true) {
            Connect4Model.WinningMove winningMove = mModel.getWinningMove();

            if (winningMove != null) {
                //Draws a line through the balls of the winning move
                canvas.drawLine(mBoardRect.left + mGridWidth * (winningMove.startPos.x + 0.5f),
                        mBoardRect.bottom - mGridWidth * (winningMove.startPos.y + 0.5f),
                        mBoardRect.left + mGridWidth * (winningMove.endPos.x + 0.5f),
                        mBoardRect.bottom - mGridWidth * (winningMove.endPos.y + 0.5f),
                        mWinPaint);
            }

        }
    }

    /**
     * Draws the Play Again button if the game is over
     * @param canvas
     */
    private void drawPlayAgainButton(Canvas canvas) {

        if (mModel.hasWinner()) {
            Drawable buttonImage = getResources().getDrawable(R.drawable.play_again_button, null);
            buttonImage.setBounds(mPlayAgainRect);
            buttonImage.draw(canvas);
        }
    }

    /**
     * Helper function which gets the bounding rect around a given text
     * @param text text that will be drawn
     * @param painter painter to draw the text
     * @return
     */
    private Rect getTextRect(String text, Paint painter) {

        Rect rect = new Rect();
        painter.getTextBounds(text, 0, text.length(), rect);
        return rect;

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float x = event.getX();
        float y = event.getY();
        Log.d("BLAH" , "FDSFSDF");

        if (event.getAction() == MotionEvent.ACTION_DOWN) {

        }
        else if (event.getAction() == MotionEvent.ACTION_MOVE) {

        }
        else if (event.getAction() == MotionEvent.ACTION_UP) {

            //Checks if any of the presses were on the board or button
            if (mBoardRect.contains((int)x, (int)y)) {

                int column = (int) (x / mGridWidth);
                if (column == Connect4Model.BOARD_WIDTH) {
                    column--;
                }
                //Notifies the controller of a click
                mController.userTouchedScreen(column);
            }

            else if (mModel.hasWinner() && mPlayAgainRect.contains((int)x, (int)y)) {

                mController.userClickedPlayAgain();
            }

            invalidate();


        }
        else {
            return false;
        }

        return true;
    }
}
