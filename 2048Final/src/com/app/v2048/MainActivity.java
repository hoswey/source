package com.app.v2048;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends Activity implements OnGestureListener {

    private LinearLayout centerlayout;

    private final Tile[][] tiles = new Tile[4][4];

    private GestureDetector gd;

    Game game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        TileGrid tileGrid = (TileGrid) findViewById(R.id.tile_grid);
        game = new Game(this, tileGrid);
        gd = new GestureDetector(this, game);

    }

    private void newGame() {

        game.newGame();
    }

    /**
     * 对话框按钮事件监听器
     */
    public class MyOnclickListener implements android.content.DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {

            switch (which) {
                case AlertDialog.BUTTON_POSITIVE:
                    //继续游戏
                    newGame();

                    break;
                //退出游戏
                case AlertDialog.BUTTON_NEGATIVE:
                    finish();
                    break;
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        return gd.onTouchEvent(event);
    }

    public void onNewGameClick(View view) {

        newGame();
    }

    public void onUndoClick(View view) {

        game.undo();
    }

    @Override
    public boolean onDown(MotionEvent e) {

        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {

        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

        float X = e2.getX() - e1.getX();
        float Y = e2.getY() - e1.getY();
        final int FLING_Min_DISTANCE = 50;

        if (X > FLING_Min_DISTANCE && Math.abs(velocityX) > Math.abs(velocityY)) {
            //  toRight();
            Toast.makeText(this, "右", 0).show();
        } else if (X < -FLING_Min_DISTANCE && Math.abs(velocityX) > Math.abs(velocityY)) {
            // toLeft();
            //          Toast.makeText(this, "左", 0).show();
        } else if (Y > FLING_Min_DISTANCE && Math.abs(velocityX) < Math.abs(velocityY)) {
            //  toDown();
            //          Toast.makeText(this, "下", 0).show();
        } else if (Y < -FLING_Min_DISTANCE && Math.abs(velocityX) < Math.abs(velocityY)) {
            // toUp();
            //          Toast.makeText(this, "上", 0).show();
        }
        return false;
    }
}
