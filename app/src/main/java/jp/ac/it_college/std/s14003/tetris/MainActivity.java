package jp.ac.it_college.std.s14003.tetris;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements Board.CallBack {
    private Board board;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler();
        setContentView(R.layout.activity_main);

        Bitmap srcImage = BitmapFactory.decodeResource(getResources(),
                android.R.drawable.ic_media_play);

        Matrix matrix = new Matrix();

        matrix.postRotate(90);
        Bitmap fallImage = Bitmap.createBitmap(srcImage, 0, 0,
                srcImage.getWidth(), srcImage.getHeight(), matrix, true);
        ((ImageButton) findViewById(R.id.fall)).setImageBitmap(fallImage);

        matrix.postRotate(90);
        Bitmap leftImage = Bitmap.createBitmap(srcImage, 0, 0,
                srcImage.getWidth(), srcImage.getHeight(), matrix, true);
        ((ImageButton) findViewById(R.id.left)).setImageBitmap(leftImage);

        board = (Board)findViewById(R.id.board);
        board.setCallback(this);

    }
    public void gameButtonClick(View v) {
        switch (v.getId()) {
            case R.id.left:
                board.send(Input.Left);
                break;
            case R.id.right:
                board.send(Input.Right);
                break;
            case R.id.fall:
                board.send(Input.Down);
                break;
            case R.id.rotate:
                board.send(Input.Rotate);
                break;
        }
    }


    @Override
    public void scoreAdd(final int score) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                TextView scoreView = (TextView) findViewById(R.id.score);
                int current = Integer.parseInt(scoreView.getText().toString());
                current += score;
                scoreView.setText(String.valueOf(current));
                if (current >= 5 && current <= 9) {
                    board.setSpeed(4);
                } else if (current >= 10 && current <= 19) {
                    board.setSpeed(6);
                } else if (current >= 20 && current <= 29) {
                    board.setSpeed(8);
                }
            }
        });
    }


}
