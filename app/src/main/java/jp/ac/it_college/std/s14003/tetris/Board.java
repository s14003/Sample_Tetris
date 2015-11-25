package jp.ac.it_college.std.s14003.tetris;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;
import android.telecom.Call;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by s14003 on 15/11/11.
 */
public class Board extends SurfaceView implements SurfaceHolder.Callback {
    public static final int FPS = 30;
    private SurfaceHolder holder;
    private DrawThread thread;
    private Bitmap blocks;
    private CallBack callback;
    Tetromino test = new Tetromino(this);
    private Tetromino fallingTetromino;
    private ArrayList<Tetromino> tetrominoList = new ArrayList<>();
    private long count = 0;

    public Board(Context context) {
        super(context);
        initialize(context);
    }

    public Board(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public Board(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    private void initialize(Context context) {
        getHolder().addCallback(this);
        blocks = BitmapFactory.decodeResource(context.getResources(),R.raw.blobk2);
        spawnTetromino();
    }

    private void spawnTetromino() {
        fallingTetromino = new Tetromino(this);
        fallingTetromino.setPosition(5, 23);
    }

    public boolean fallingTetromino() {
        fallingTetromino.move(Input.Down);
        if (!isValidPosition()) {
            fallingTetromino.move(Input.Up);
            return false;
        }
        return true;
    }

    public boolean isValidPosition() {
        boolean overlapping = false;
        for (Tetromino fixedTetromino : tetrominoList) {
            if (fallingTetromino.intersect(fixedTetromino)){
                overlapping = true;
                break;
            }
        }

        return !(overlapping || fallingTetromino.isOutOfBounds());
    }

    private List<Integer> findFullRows() {
        int[] rowCounts = new int[22];
        for (Tetromino fixedTetromino : tetrominoList) {
            for (Coordinate coordinate : fixedTetromino.getCoordinates()) {
                rowCounts[coordinate.y]++;
            }
        }
        ArrayList<Integer> list = new ArrayList<>();
        for (int cy = 0; cy < rowCounts.length;cy++) {
            if (rowCounts[cy] == 10) {
                list.add(cy);
            }
        }
        return list;
    }

    private void clearRows(List<Integer> list) {
        callback.scoreAdd(list.size());
        Collections.reverse(list);
        for (int row : list) {
            clearRow(row);
        }
    }

    private void clearRow(int row) {
        ArrayList<Tetromino> deleteTetoromino = new ArrayList<>();
        for (Tetromino tetromino : tetrominoList) {
            if (tetromino.clearRowAndAdjustDown(row) == 0) {
                deleteTetoromino.add(tetromino);
            }
        }
        tetrominoList.removeAll(deleteTetoromino);
    }

    private void updateGame() {
        if (count++ / (FPS / 2) == 0) {
            return;
        }
        count = 0;
        if (!fallingTetromino()) {
            tetrominoList.add(fallingTetromino);
            clearRows(findFullRows());
            spawnTetromino();
        }

        fallingTetromino();
    }

    public void setCallback(CallBack callback) {
        this.callback = callback;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        this.holder = holder;
        startThread();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        stopThread();
    }

    public void translateCanvasCoordinate(Canvas canvas, RectF rectF, int gx,int gy) {
        float side = canvas.getWidth() / 10.0f;
        gy = 20 - gy;
        rectF.set(side * gx, side * gy, side * (gx + 1), side * (gy + 1));

    }

    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (canvas == null) {
            return;
        }

        if (!Tetromino.Type.isBitmapInitialized()) {
            Tetromino.Type.setBlockBitmap(blocks);
        }

        updateGame();

        canvas.drawColor(Color.LTGRAY);

        for (Tetromino tetromino : tetrominoList) {
            tetromino.draw(canvas);
        }
        fallingTetromino.draw(canvas);

    }

    public void send(Input input) {
        fallingTetromino.move(input);
        if (!isValidPosition()) {
            fallingTetromino.undo(input);
        } else if (input == Input.Down) {
            count = 0;
        }
    }

    private void startThread() {
        stopThread();
        thread = new DrawThread();
        thread.start();
    }

    private void stopThread() {
        if (thread != null) {
            thread.isFinished = true;
            thread = null;
        }
    }

    private class DrawThread extends Thread {
         boolean isFinished;
        @Override
        public void run() {
            long prevTime = 0;
            while(!isFinished) {
                if (holder == null ||
                        System.currentTimeMillis() - prevTime < 1000 / FPS) {
                    try {
                        sleep(1000 / FPS / 3);
                    } catch(InterruptedException e) {
                        Log.w("DrawThread", e.getMessage(),e);
                        return;
                    }
                    continue;
                }
                Canvas c = null;
                try {
                    c = holder.lockCanvas(null);
                    synchronized (holder) {
                        draw(c);
                    }
                } finally {
                    if (c != null) {
                        holder.unlockCanvasAndPost(c);
                    }
                }
                prevTime = System.currentTimeMillis();
            }
        }
    }

    public interface CallBack {
        void scoreAdd(int score);

    }
}
