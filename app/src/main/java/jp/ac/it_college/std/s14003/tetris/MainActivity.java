package jp.ac.it_college.std.s14003.tetris;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {
    private Board board;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
}
