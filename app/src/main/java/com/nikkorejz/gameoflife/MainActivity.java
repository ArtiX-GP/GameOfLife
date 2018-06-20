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

    // Ширина сетки.
    private final int WIDTH_GRID = 25;
    
    // Высота сетки.
    private final int HEIGHT_GRID = 10;

    // Двумерный массив (сетка, таблица) для рассчета хода.
    private boolean[][] mArray = new boolean[HEIGHT_GRID][];

    // Таймер
    private CountDownTimer mTimer;

    private boolean isGameStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Контейнер с кнопками (ячейками).
        FrameLayout mButtonsContainer = new FrameLayout(this);
        
        // Ширина и высота кнопки (Кнопки квадратные)
        int WIDTH_BUTTON = 75;

        // Параметры для ячеек.
        LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(WIDTH_BUTTON, WIDTH_BUTTON);
        for (int i = 0; i < HEIGHT_GRID; i++) {
            // На каждую строку создаем массив из *WIDTH_GRID* элементов.
            mArray[i] = new boolean[WIDTH_GRID];
            for (int j = 0; j < WIDTH_GRID; j++) {
                // Создаем ячейку.
                ImageButton mButton = new ImageButton(this);
                // Подключаем слушатель событий.
                mButton.setOnClickListener(MainActivity.this);
                
                // Настройка позиционирования ячейки (добавляем отступы).
                mParams.leftMargin = WIDTH_BUTTON * j;
                mParams.topMargin = WIDTH_BUTTON * i;
                
                // Применяем изменения.
                mButton.setLayoutParams(mParams);
                
                // Устанавливаем уникальный идентификатор.
                mButton.setId(100 * i + j);
                
                // Устанавливаем задний фон для ячейки.
                mButton.setBackground(ContextCompat.getDrawable(this, R.drawable.background));
                
                // Добавляем ячейку в ранее созданный контейнер.
                mButtonsContainer.addView(mButton);
            }
        }

        // Параметры для контейнера.
        FrameLayout.LayoutParams mParams2 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        
        // Центрируем все кнопки по центру.
        mParams2.gravity = Gravity.CENTER;
        
        // Применяем изменения.
        mButtonsContainer.setLayoutParams(mParams2);

        // Показываем то, что получилось. 
        setContentView(mButtonsContainer);

        // Запускаем таймер.
        onCreateTimer();
    }

    private void onCreateTimer() {
        // Если таймер живой, то останавливаем его.
        if (mTimer != null) {
            mTimer.cancel();
        }
        
        // Создаем новый таймер.
        mTimer = new CountDownTimer(10000, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Если игра запущена, то выполняем один шаг каждые 100 миллисекунд.
                if (isGameStarted)
                    doStep();
            }

            @Override
            public void onFinish() {
                // По окончании таймера запускаем его заново (создаем эффект бесконечной анимации).
                mTimer.start();
            }
        };
        mTimer.start();
    }

    @SuppressLint("ResourceType")
    @Override
    public void onClick(View v) {
        // При нажатии на любую ячейку изменяем ее статус.
        v.setSelected(!v.isSelected());
        // Также меняем состояние в массиве.
        mArray[v.getId() / 100][v.getId() % 100] = v.isSelected();
    }

    // Функция дублирования двумерного массива.
    boolean[][] duplicate() {
        boolean[][] mDuplicate = new boolean[HEIGHT_GRID][];
        for (int i = 0; i < HEIGHT_GRID; i++) {
            mDuplicate[i] = new boolean[WIDTH_GRID];
            System.arraycopy(mArray[i], 0, mDuplicate[i], 0, WIDTH_GRID);
        }
        return mDuplicate;
    }

    void doStep() {
        // Создаем дубликат, для корректной работы программы.
        // Мы не можем проверять одну матрицу и сразу же делать в ней изменения, это повлечет за собой ошибки.
        boolean[][] mDuplicate = duplicate();
        for (int i = 0; i < HEIGHT_GRID; i++) {
            for (int j = 0; j < WIDTH_GRID; j++) {
                // Кол-во соседей вокруг данной клеточки.
                int mCount = 0;
                
                // Считаем соседей.
                
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
                
                // Посчитали соседей.

                // Если данная клетка "жива", то если кол-во соседей равно 2 или 3, то оставляем эту клетку в покое.
                if (mDuplicate[i][j]) {
                    mArray[i][j] = mCount == 2 || mCount == 3;
                } else { // Иначе, клетка оживает в том случае, если кол-во соседей равно 3.
                    mArray[i][j] = mCount == 3;
                }
                // Находим нашу ячейку (кнопку) и обновляем ее статус (Жива она или нет).
                findViewById(100 * i + j).setSelected(mArray[i][j]);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Мы это не проходили, это создание контекстного меню.
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Слушатель нажатий на элементы меню.
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
        // Когда мы закрываем приложение, то мы выключаем таймер.
        if (mTimer != null)
            mTimer.cancel();
    }
}
