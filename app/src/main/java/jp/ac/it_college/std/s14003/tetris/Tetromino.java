package jp.ac.it_college.std.s14003.tetris;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;

/**
 * Created by s14003 on 15/11/12.
 */
public class Tetromino {
    private static Paint paint = new Paint();
    private static final HashMap<Orientation, Orientation> CLOCKWISE_ROTATIONS;
    private Board board;
    private Type type;
    private Orientation orientation;
    private Coordinate[] blockBoardCoordinates = Coordinate.asArray(0,0,0,0,0,0,0,0);
    private Coordinate base;
    private RectF dst = new RectF();

    static {
        CLOCKWISE_ROTATIONS = new HashMap<>();
        CLOCKWISE_ROTATIONS.put(Orientation.Right, Orientation.Down);
        CLOCKWISE_ROTATIONS.put(Orientation.Down, Orientation.Left);
        CLOCKWISE_ROTATIONS.put(Orientation.Left, Orientation.Up);
        CLOCKWISE_ROTATIONS.put(Orientation.Up, Orientation.Right);

    }

    public Tetromino(Board board) {
        this.board = board;
        base = new Coordinate(0, 0);
        type = Type.nextType();
        orientation = Orientation.Right;
        calcBlockBoardCoordinates();
    }

    private void calcBlockBoardCoordinates() {
        Coordinate[] localCoordinates = type.getLocalBlockCoodinates(orientation);
        for (int i = 0; i < localCoordinates.length;i++) {
            Coordinate local = localCoordinates[i];
            blockBoardCoordinates[i].x = local.x + base.x;
            blockBoardCoordinates[i].y = local.y + base.y;
        }
    }

    public void setPosition(int x, int y) {
        base.x = x;
        base.y = y;
        calcBlockBoardCoordinates();
    }

    public void move(Input input) {
        switch (input) {
            case Up:
                base.y += 1;
                break;
            case Down:
                base.y -= 1;
                break;
            case Right:
                base.x += 1;
                break;
            case Left:
                base.x -= 1;
                break;
            case Rotate:
                rotate();
                break;
        }
        calcBlockBoardCoordinates();
    }

    public void undo(Input input) {
        switch (input) {
            case Up:
                base.y -= 1;
                break;
            case Down:
                base.y += 1;
                break;
            case Right:
                base.x -= 1;
                break;
            case Left:
                base.x += 1;
                break;
            case Rotate:
                rotate(true);
                break;
        }
        calcBlockBoardCoordinates();
    }

    public void rotate() {
        rotate(false);
    }

    public void rotate(boolean isCCW) {
        orientation = CLOCKWISE_ROTATIONS.get(orientation);
        if (isCCW) {
            orientation = CLOCKWISE_ROTATIONS.get(orientation);
            orientation = CLOCKWISE_ROTATIONS.get(orientation);
        }
        calcBlockBoardCoordinates();
    }

    public boolean intersect(Tetromino target) {
        for (Coordinate blockCoordinate : blockBoardCoordinates) {
            for (Coordinate targetBlockCoordinate: target.blockBoardCoordinates) {
                if (blockCoordinate.x == targetBlockCoordinate.x &&
                        blockCoordinate.y == targetBlockCoordinate.y) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isOutOfBounds() {
        for (Coordinate blockCoodinate : blockBoardCoordinates) {
            if (blockCoodinate.x < 0 || blockCoodinate.x >= 10
                    || blockCoodinate.y <= 0) {
                return true;
            }
        }
        return false;
    }

    public Coordinate[] getCoordinates() {
        return blockBoardCoordinates;
    }

    public enum  Orientation {
        Right, Down, Left, Up,;
    }

    public void draw(Canvas canvas) {
        for(Coordinate point : blockBoardCoordinates) {
            board.translateCanvasCoordinate(canvas,dst, point.x, point.y);
            canvas.drawBitmap(Type.blockBitmap,type.getRect(), dst, paint);
        }
    }

    public enum Type {
        I(1), O(2), S(3), Z(4), J(5), L(6), T(7),;

        private static LinkedList<Type> queue = new LinkedList<>();
        private static Random random = new Random();
        private static final int SHUFFLE_COUNT = 100;
        private static Bitmap blockBitmap = null;
        private static final Map<Type,Map<Orientation,Coordinate[]>> LOCAL_BLOCK_COORDINATES;
        private static Map<Type, Rect> blockRect  = new HashMap<>();


        static {
            LOCAL_BLOCK_COORDINATES = new HashMap<>();
            HashMap<Orientation, Coordinate[]> coordinates;


            coordinates = new HashMap<>();
            coordinates.put(Orientation.Right,Coordinate.asArray(-1,0,0,0,1,0,2,0));
            coordinates.put(Orientation.Down,Coordinate.asArray(0,-1,0,0,0,1,0,2));
            coordinates.put(Orientation.Left,Coordinate.asArray(-1,0,0,0,1,0,2,0));
            coordinates.put(Orientation.Up,Coordinate.asArray(0,-1,0,0,0,1,0,2));
            LOCAL_BLOCK_COORDINATES.put(I, coordinates);

            coordinates = new HashMap<>();
            coordinates.put(Orientation.Right, Coordinate.asArray(-1, -1, -1, 0, 0, -1, 0, 0));
            coordinates.put(Orientation.Down, Coordinate.asArray(-1, -1, -1, 0, 0, -1, 0, 0));
            coordinates.put(Orientation.Left, Coordinate.asArray(-1, -1, -1, 0, 0, -1, 0, 0));
            coordinates.put(Orientation.Up, Coordinate.asArray(-1, -1, -1, 0, 0, -1, 0, 0));
            LOCAL_BLOCK_COORDINATES.put(O, coordinates);

            coordinates = new HashMap<>();
            coordinates.put(Orientation.Right, Coordinate.asArray(0, -1, 0, 0, 1, 0, 1, 1));
            coordinates.put(Orientation.Down, Coordinate.asArray(1, -1, 0, -1, 0, 0, -1, 0));
            coordinates.put(Orientation.Left, Coordinate.asArray(0, -1, 0, 0, 1, 0, 1, 1));
            coordinates.put(Orientation.Up, Coordinate.asArray(1, -1, 0, -1, 0, 0, -1, 0));
            LOCAL_BLOCK_COORDINATES.put(S, coordinates);

            coordinates = new HashMap<>();
            coordinates.put(Orientation.Right, Coordinate.asArray(1, -1, 1, 0, 0, 0, 0, 1));
            coordinates.put(Orientation.Down, Coordinate.asArray(1, 1, 0, 1, 0, 0, -1, 0));
            coordinates.put(Orientation.Left, Coordinate.asArray(1, -1, 1, 0, 0, 0, 0, 1));
            coordinates.put(Orientation.Up, Coordinate.asArray(1, 1, 0, 1, 0, 0, -1, 0));
            LOCAL_BLOCK_COORDINATES.put(Z, coordinates);

            coordinates = new HashMap<>();
            coordinates.put(Orientation.Down, Coordinate.asArray(1, -1, 0, -1, 0, 0, 0, 1));
            coordinates.put(Orientation.Right, Coordinate.asArray(1, 1, 1, 0, 0, 0, -1, 0));
            coordinates.put(Orientation.Left, Coordinate.asArray(0, -1, 0, 0, 0, 1, -1, 1));
            coordinates.put(Orientation.Up, Coordinate.asArray(-1, -1, -1, 0, 0, 0, 1, 0));
            LOCAL_BLOCK_COORDINATES.put(J, coordinates);

            coordinates = new HashMap<>();
            coordinates.put(Orientation.Down, Coordinate.asArray(-1, -1, 0, -1, 0, 0, 0, 1));
            coordinates.put(Orientation.Right, Coordinate.asArray(1, -1, 1, 0, 0, 0, -1, 0));
            coordinates.put(Orientation.Up, Coordinate.asArray(0, -1, 0, 0, 0, 1, 1, 1));
            coordinates.put(Orientation.Left, Coordinate.asArray(1, 0, 0, 0, -1, 0, -1, 1));
            LOCAL_BLOCK_COORDINATES.put(L, coordinates);

            coordinates = new HashMap<>();
            coordinates.put(Orientation.Down, Coordinate.asArray(1, 0, 0, 0, -1, 0, 0, 1));
            coordinates.put(Orientation.Right, Coordinate.asArray(0, -1, 0, 0, 0, 1, -1, 0));
            coordinates.put(Orientation.Up, Coordinate.asArray(1, 0, 0, 0, -1, 0, 0, -1));
            coordinates.put(Orientation.Left, Coordinate.asArray(0, -1, 0, 0, 0, 1, 1, 0));
            LOCAL_BLOCK_COORDINATES.put(T, coordinates);
        }

        private final int id;

        Type(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public Rect getRect() {
            return blockRect.get(this);
        }

        public static Type nextType() {
            if (queue.size() == 0) {
                generateQueue();
            }
            return queue.remove();
        }

        public static void generateQueue() {
            queue.addAll(Arrays.asList(I,O,S,Z,J,L,T));
            for (int i = 0;i < SHUFFLE_COUNT;i++) {
                int src = random.nextInt(queue.size());
                int dst = random.nextInt(queue.size());
                Type tmp = queue.get(src);
                queue.set(src,queue.get(dst));
                queue.set(dst, tmp);
            }
        }

        public Coordinate[] getLocalBlockCoodinates(Orientation orientation) {
            return LOCAL_BLOCK_COORDINATES.get(this).get(orientation);
        }

        public static boolean isBitmapInitialized() {
            return blockBitmap != null;
        }


        public static void setBlockBitmap(Bitmap bitmap) {
            blockBitmap = bitmap;
            int side = blockBitmap.getWidth();
            blockRect.put(I, new Rect(0, 0 * side, side, (0+1) * side));
            blockRect.put(O, new Rect(0, 1 * side, side, (1 + 1) * side));
            blockRect.put(S, new Rect(0, 2 * side, side, (2 + 1) * side));
            blockRect.put(Z, new Rect(0, 3 * side, side, (3 + 1) * side));
            blockRect.put(J, new Rect(0, 4 * side, side, (4 + 1) * side));
            blockRect.put(L, new Rect(0, 5 * side, side, (5 + 1) * side));
            blockRect.put(T, new Rect(0, 6 * side, side, (6 + 1) * side));
        }
    }
}
