package com.nikkorejz.gameoflife;

import android.annotation.SuppressLint;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final int WIDTH_GRID = 25;
    private final int HEIGHT_GRID = 10;

    private boolean[][] mArray = new boolean[HEIGHT_GRID][];

    private CountDownTimer mTimer;

    private boolean isGameStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout mButtonsContainer = new FrameLayout(this);
        int WIDTH_BUTTON = 75;

        LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(WIDTH_BUTTON, WIDTH_BUTTON);
        for (int i = 0; i < HEIGHT_GRID; i++) {
            mArray[i] = new boolean[WIDTH_GRID];
            for (int j = 0; j < WIDTH_GRID; j++) {
                ImageButton mButton = new ImageButton(this);
                mButton.setOnClickListener(MainActivity.this);
                mParams.leftMargin = WIDTH_BUTTON * j;
                mParams.topMargin = WIDTH_BUTTON * i;
                mButton.setLayoutParams(mParams);
                mButton.setId(100 * i + j);
                mButton.setBackground(ContextCompat.getDrawable(this, R.drawable.background));
                mButtonsContainer.addView(mButton);
            }
        }

        FrameLayout.LayoutParams mParams2 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mParams2.gravity = Gravity.CENTER;
        mButtonsContainer.setLayoutParams(mParams2);

        setContentView(mButtonsContainer);

        onCreateTimer();
    }

    private void onCreateTimer() {
        if (mTimer != null) {
            mTimer.cancel();
        }
        mTimer = new CountDownTimer(10000, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (isGameStarted)
                    doStep();
            }

            @Override
            public void onFinish() {
                mTimer.start();
            }
        };
        mTimer.start();
    }

    @SuppressLint("ResourceType")
    @Override
    public void onClick(View v) {
        v.setSelected(!v.isSelected());
        mArray[v.getId() / 100][v.getId() % 100] = v.isSelected();
    }

    boolean[][] duplicate() {
        boolean[][] mDuplicate = new boolean[HEIGHT_GRID][];
        for (int i = 0; i < HEIGHT_GRID; i++) {
            mDuplicate[i] = new boolean[WIDTH_GRID];
            System.arraycopy(mArray[i], 0, mDuplicate[i], 0, WIDTH_GRID);
        }
        return mDuplicate;
    }

    void doStep() {
        boolean[][] mDuplicate = duplicate();
        for (int i = 0; i < HEIGHT_GRID; i++) {
            for (int j = 0; j < WIDTH_GRID; j++) {
                int mCount = 0;
                if (i > 0 && mDuplicate[i - 1][j])
                    mCount++;
                if (i > 0 && j > 0 && mDuplicate[i - 1][j - 1])
                    mCount++;
                if (i > 0 && j < WIDTH_GRID - 1 && mDuplicate[i - 1][j + 1])
                    mCount++;
                if (j > 0 && mDuplicate[i][j - 1])
                    mCount++;
                if (j < WIDTH_GRID - 1 && mDuplicate[i][j + 1])
                    mCount++;
                if (i < HEIGHT_GRID - 1 && mDuplicate[i + 1][j])
                    mCount++;
                if (i < HEIGHT_GRID - 1 && j > 0 && mDuplicate[i + 1][j - 1])
                    mCount++;
                if (i < HEIGHT_GRID - 1 && j < WIDTH_GRID - 1 && mDuplicate[i + 1][j + 1])
                    mCount++;

                if (mDuplicate[i][j]) {
                    mArray[i][j] = mCount == 2 || mCount == 3;
                } else {
                    mArray[i][j] = mCount == 3;
                }
                findViewById(100 * i + j).setSelected(mArray[i][j]);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ActionPlay:
                isGameStarted = true;
                return true;
            case R.id.ActionStep:
                doStep();
                return true;
            case R.id.ActionStop:
                isGameStarted = false;
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mTimer != null)
            mTimer.cancel();
    }
}
