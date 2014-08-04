package com.app.v2048;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;

import com.app.v2048.util.Logger;

/**
 * Title: Game.java Description: Copyright: 2014 Duopay, all rights reserved.
 * Duopay PROPRIETARY/CONFIDENTIAL. Use is subject to license terms. Company:
 * Duopay
 * 
 * @author: Hoswey
 * @version: 1.0 Create at: 2014年7月24日
 */
public class Game extends SimpleOnGestureListener {
    private final int width = 4;

    //Use the format of Tile[y][x] for store current tile grid
    private TileHolder[][] tiles;

    private final Activity context;

    private final TileGrid tileGrid;

    private final TextView tv_currenScore;

    private final TextView tv_bestScore;

    private int currentScore = 0;

    private final BestScode record;

    private final Random random = new Random();

    private final Stack<List<TileHolder>> historyTiles = new Stack<List<TileHolder>>();

    private final Stack<Integer> historyScore = new Stack<Integer>();

    public Game(Activity context, TileGrid tileGrid) {

        this.context = context;
        this.tileGrid = tileGrid;

        record = new BestScode(context);

        tv_currenScore = (TextView) context.findViewById(R.id.tv_currenScore);
        tv_currenScore.setText("0");

        tv_bestScore = (TextView) context.findViewById(R.id.tv_bestScore);
        tv_bestScore.setText(record.getBestScode() + "");

        newGame();
    }

    private void updateCurrentScore(int num) {

        currentScore = num;
        tv_currenScore.setText(String.valueOf(num));
    }

    private void addCurrentscore(int num) {

        int newScore = currentScore + num;
        updateCurrentScore(newScore);
    }

    public void newGame() {

        historyTiles.clear();

        tileGrid.removeAllViews();
        tiles = new TileHolder[width][width];
        updateCurrentScore(0);

        //intDead();
        random2Or4();
        random2Or4();
    }

    private void intDead() {

        for (int y = 0; y < width; y++) {

            for (int x = 0; x < width; x++) {

                if (y == 0 & x == 0)
                    continue;

                if (y % 2 == x % 2) {
                    addTile(y, x, 2);
                } else {
                    addTile(y, x, 4);
                }

            }

        }
    }

    private void updateBestScore() {

        if (currentScore > record.getBestScode()) {
            record.setBestScode(currentScore);
            tv_bestScore.setText(currentScore + "");
        }
    }

    private void addTile(int y, int x, int num) {

        Tile tile = new Tile(context);
        tile.setNumber(num);
        tileGrid.addTile(tile, y, x);
        tiles[y][x] = new TileHolder(tile);

        ObjectAnimator scaleYAnimation = ObjectAnimator.ofFloat(tile, "scaleY", 0.8f, 1f);
        ObjectAnimator scaleXAnimation = ObjectAnimator.ofFloat(tile, "scaleX", 0.8f, 1f);

        AnimatorSet scaleAnimatorSet = new AnimatorSet();
        scaleAnimatorSet.playTogether(scaleXAnimation, scaleYAnimation);
        scaleAnimatorSet.setDuration(100);
        scaleAnimatorSet.setStartDelay(10);
        scaleAnimatorSet.start();

        updateCurrentPostion();
    }

    private void add2History() {

        List<TileHolder> tileHolders = new ArrayList<TileHolder>();
        for (int y = 0; y < width; y++) {
            for (int x = 0; x < width; x++) {
                TileHolder src = tiles[y][x];
                TileHolder dest = null;;
                if (src != null) {
                    dest = new TileHolder();
                    dest.setNewNum(src.getRealNum());
                    dest.setNewY(src.getNewY());
                    dest.setNewX(src.getNewX());
                }
                tileHolders.add(dest);
            }
        }
        historyTiles.push(tileHolders);
        historyScore.push(currentScore);
    }

    private void moveTiles(Direction direction) {

        add2History();

        updateCurrentPostion();
        //logTilesStats();
        doMove(direction);
        // logTilesStats(); 
        animate(direction);
        updateCurrentPostion();

        random2Or4();
    }

    private void clearAllTiles() {

        tileGrid.removeAllViews();
        tiles = new TileHolder[width][width];

    }

    public void undo() {

        if (historyTiles.size() != 0) {
            List<TileHolder> tileHolders = historyTiles.pop();
            clearAllTiles();
            for (int y = 0; y < width; y++) {
                for (int x = 0; x < width; x++) {
                    TileHolder tileHolder = tileHolders.get(y * width + x);
                    if (tileHolder != null) {
                        addTile(y, x, tileHolder.getNewNum());
                    }
                }
            }

            updateCurrentScore(historyScore.pop());
        } else {
            showMessage("已经第一步了");
        }
    }

    private void showMessage(String message) {

        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    private void doMove(Direction direction) {

        // Toast.makeText(context, direction.toString(), Toast.LENGTH_SHORT).show();

        List<TileHolder> removedTileHolders = new ArrayList<TileHolder>();

        int addScore = 0;

        switch (direction) {

            case LEFT:
                for (int y = 0; y < width; y++) {
                    for (int x = 0; x < width - 1; x++) {
                        for (int k = x + 1; k <= width - 1; k++) {
                            TileHolder curTile = tiles[y][x];
                            TileHolder compTile = tiles[y][k];
                            if (isLess(curTile, compTile)) {
                                swapTile(y, x, y, k);
                            }
                        }
                    }

                    for (int x = 0; x < width - 1; x++) {
                        TileHolder curTile = tiles[y][x];
                        TileHolder compTile = tiles[y][x + 1];

                        if (curTile == null || compTile == null) {
                            break;
                        }

                        int curNum = curTile.getRealNum();
                        int comNum = compTile.getRealNum();
                        if (curNum == comNum) {
                            curTile.setNewNum(0);
                            removedTileHolders.add(curTile);

                            int newNum = curNum * 2;
                            compTile.setNewNum(newNum);

                            addScore += newNum;

                            for (int k = x + 1; k <= width - 1; k++) {
                                TileHolder tileNeedMove = tiles[y][k];
                                if (tileNeedMove == null) {
                                    break;
                                }
                                int newX = k - 1;
                                tileNeedMove.setNewX(newX);
                                tiles[y][newX] = tileNeedMove;
                                tiles[y][k] = null;
                            }
                            break;
                        }
                    }
                }
                break;
            case RIGHT:
                for (int y = 0; y < width; y++) {
                    for (int x = width - 1; x > 0; x--) {
                        for (int k = x - 1; k >= 0; k--) {
                            TileHolder curTile = tiles[y][x];
                            TileHolder compTile = tiles[y][k];
                            if (isLess(curTile, compTile)) {
                                swapTile(y, x, y, k);
                            }
                        }
                    }

                    for (int x = width - 1; x > 0; x--) {
                        TileHolder curTile = tiles[y][x];
                        TileHolder compTile = tiles[y][x - 1];

                        if (curTile == null || compTile == null) {
                            break;
                        }

                        int curNum = curTile.getRealNum();
                        int comNum = compTile.getRealNum();
                        if (curNum == comNum) {
                            curTile.setNewNum(0);
                            removedTileHolders.add(curTile);

                            int newNum = curNum * 2;
                            compTile.setNewNum(newNum);
                            addScore += newNum;

                            for (int k = x - 1; k >= 0; k--) {
                                TileHolder tileNeedMove = tiles[y][k];
                                if (tileNeedMove == null) {
                                    break;
                                }
                                int newX = k + 1;
                                tileNeedMove.setNewX(newX);
                                tiles[y][newX] = tileNeedMove;
                                tiles[y][k] = null;
                            }
                            break;
                        }
                    }
                }

                break;
            case UP:
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < width - 1; y++) {
                        for (int k = y + 1; k <= width - 1; k++) {
                            TileHolder curTile = tiles[y][x];
                            TileHolder compTile = tiles[k][x];
                            if (isLess(curTile, compTile)) {
                                swapTile(y, x, k, x);
                            }
                        }
                    }

                    for (int y = 0; y < width - 1; y++) {
                        TileHolder curTile = tiles[y][x];
                        TileHolder compTile = tiles[y + 1][x];

                        if (curTile == null || compTile == null) {
                            break;
                        }

                        int curNum = curTile.getRealNum();
                        int comNum = compTile.getRealNum();
                        if (curNum == comNum) {
                            curTile.setNewNum(0);
                            removedTileHolders.add(curTile);
                            int newNum = curNum * 2;
                            compTile.setNewNum(newNum);
                            addScore += newNum;

                            for (int k = y + 1; k <= width - 1; k++) {
                                TileHolder tileNeedMove = tiles[k][x];
                                if (tileNeedMove == null) {
                                    break;
                                }
                                int newY = k - 1;
                                tileNeedMove.setNewY(newY);
                                tiles[newY][x] = tileNeedMove;
                                tiles[k][x] = null;
                            }
                            break;
                        }
                    }
                }
                break;
            case DOWN:
                for (int x = 0; x < width; x++) {
                    for (int y = width - 1; y > 0; y--) {
                        for (int k = y - 1; k >= 0; k--) {
                            TileHolder curTile = tiles[y][x];
                            TileHolder compTile = tiles[k][x];
                            if (isLess(curTile, compTile)) {
                                swapTile(y, x, k, x);
                            }
                        }
                    }

                    for (int y = width - 1; y > 0; y--) {
                        TileHolder curTile = tiles[y][x];
                        TileHolder compTile = tiles[y - 1][x];

                        if (curTile == null || compTile == null) {
                            break;
                        }

                        int curNum = curTile.getRealNum();
                        int comNum = compTile.getRealNum();
                        if (curNum == comNum) {
                            curTile.setNewNum(0);
                            removedTileHolders.add(curTile);

                            int newNum = curNum * 2;
                            compTile.setNewNum(newNum);
                            addScore += newNum;

                            for (int k = y - 1; k >= 0; k--) {
                                TileHolder tileNeedMove = tiles[k][x];
                                if (tileNeedMove == null) {
                                    break;
                                }
                                int newY = k + 1;
                                tileNeedMove.setNewY(newY);
                                tiles[newY][x] = tileNeedMove;
                                tiles[k][x] = null;
                            }
                            break;
                        }
                    }
                }
                break;
        }

        addCurrentscore(addScore);
        for (TileHolder th: removedTileHolders) {
            tileGrid.removeTile(th.getTile());
        }
    }

    private void logTilesStats() {

        for (int y = 0; y < width; y++) {

            for (int x = 0; x < width; x++) {

                if (tiles[y][x] == null)
                    continue;

                Logger.i(tiles[y][x].toString());
            }
        }
    }

    private void animate(Direction direction) {

        for (int y = 0; y < width; y++) {

            for (int x = 0; x < width; x++) {

                TileHolder tile = tiles[y][x];

                if (tile == null) {
                    continue;
                }

                int orgX = tile.getOrgX();
                int orgY = tile.getOrgY();

                int newX = tile.getNewX();
                int newY = tile.getNewY();

                final int orgNum = tile.getRealNum();
                final int newNum = tile.getNewNum();

                if (newNum == 0) {

                    tiles[y][x] = null;
                    tileGrid.removeTile(tile.getTile());

                } else if (orgX != newX || orgY != newY) {

                    Rect start = tileGrid.getTileRect(orgY, orgX);
                    Rect end = tileGrid.getTileRect(newY, newX);

                    int deltaX = end.left - start.left;
                    int deltaY = end.top - start.top;

                    if (deltaX != 0 && deltaY != 0 || deltaX == 0 && deltaY == 0) {
                        throw new RuntimeException("deltaX " + deltaX + " and deltaY " + deltaY
                                + " cannot 0 or have value in the same time");
                    }

                    final Tile cell = tile.getTile();
                    float translationX = cell.getTranslationX();
                    ObjectAnimator translateXAnimator = ObjectAnimator.ofFloat(cell, "translationX", translationX,
                            translationX + deltaX);

                    float translationY = cell.getTranslationY();
                    ObjectAnimator translateYAnimator = ObjectAnimator.ofFloat(cell, "translationY", translationY,
                            translationY + deltaY);

                    ObjectAnimator scaleXAnimation = ObjectAnimator.ofFloat(cell, "scaleX", 0.8f, 1.2f, 1f);
                    ObjectAnimator scaleYAnimation = ObjectAnimator.ofFloat(cell, "scaleY", 0.8f, 1.2f, 1f);

                    AnimatorSet translateAnimatorSet = new AnimatorSet();
                    translateAnimatorSet.playTogether(translateXAnimator, translateYAnimator);
                    translateAnimatorSet.setDuration(100);

                    final AnimatorSet scaleAnimatorSet = new AnimatorSet();
                    scaleAnimatorSet.playTogether(scaleXAnimation, scaleYAnimation);
                    scaleAnimatorSet.setDuration(50);

                    translateAnimatorSet.addListener(new AnimatorListener() {

                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {

                            if (orgNum != newNum) {
                                cell.setNumber(newNum);
                                scaleAnimatorSet.start();
                            }
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }

                    });
                    translateAnimatorSet.start();
                }
            }
        }
    }

    private void updateCurrentPostion() {

        for (int y = 0; y < width; y++) {
            for (int x = 0; x < width; x++) {
                TileHolder tileHolder = tiles[y][x];

                if (tileHolder == null)
                    continue;

                tileHolder.setOrgY(y);
                tileHolder.setOrgX(x);

                tileHolder.setNewY(y);
                tileHolder.setNewX(x);
                tileHolder.setNewNum(tileHolder.getRealNum());
            }
        }
    }

    private void swapTile(int y1, int x1, int y2, int x2) {

        TileHolder temp = tiles[y1][x1];
        tiles[y1][x1] = tiles[y2][x2];

        if (tiles[y1][x1] != null) {
            tiles[y1][x1].setNewX(x1);
            tiles[y1][x1].setNewY(y1);
        }
        tiles[y2][x2] = temp;
        if (tiles[y2][x2] != null) {
            tiles[y2][x2].setNewX(x2);
            tiles[y2][x2].setNewY(y2);
        }
    }

    private boolean isLess(TileHolder a, TileHolder b) {

        boolean isLess = false;
        if (a == null && b != null) {
            isLess = true;
        }
        return isLess;
    }

    private static class TileHolder {

        private Tile tile;

        private int orgX;

        private int newX;

        private int orgY;

        private int newY;

        private int newNum;

        public TileHolder() {

        }

        public TileHolder(Tile tile) {

            super();
            this.tile = tile;
        }

        public Tile getTile() {

            return tile;
        }

        public int getRealNum() {

            return tile.getNumber();
        }

        public int getOrgX() {

            return orgX;
        }

        public void setOrgX(int orgX) {

            this.orgX = orgX;
        }

        public int getNewX() {

            return newX;
        }

        public void setNewX(int newX) {

            this.newX = newX;
        }

        public int getOrgY() {

            return orgY;
        }

        public void setOrgY(int orgY) {

            this.orgY = orgY;
        }

        public int getNewY() {

            return newY;
        }

        public void setNewY(int newY) {

            this.newY = newY;
        }

        public int getNewNum() {

            return newNum;
        }

        public void setNewNum(int newNum) {

            this.newNum = newNum;
        }

        @Override
        public String toString() {

            return "Tile[tile=" + tile + ", orgY=" + orgY + ", orgX=" + orgX + ", newY=" + newY + ", newX=" + newX
                    + ", orgNum=" + getRealNum() + ", newNum=" + newNum + "]";
        }
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

        float X = e2.getX() - e1.getX();
        float Y = e2.getY() - e1.getY();
        final int FLING_Min_DISTANCE = 50;

        Direction direction = null;
        if (X > FLING_Min_DISTANCE && Math.abs(velocityX) > Math.abs(velocityY)) {
            direction = Direction.RIGHT;

        } else if (X < -FLING_Min_DISTANCE && Math.abs(velocityX) > Math.abs(velocityY)) {
            direction = Direction.LEFT;
        } else if (Y > FLING_Min_DISTANCE && Math.abs(velocityX) < Math.abs(velocityY)) {
            direction = Direction.DOWN;
        } else if (Y < -FLING_Min_DISTANCE && Math.abs(velocityX) < Math.abs(velocityY)) {
            direction = Direction.UP;
        }
        if (direction != null)
            moveTiles(direction);

        return false;
    }

    //随机产生2或4
    private void random2Or4() {

        Logger.i("isAllFilled()=%s，isCanMove()=%s", isAllFilled(), isCanMove());

        if (!isAllFilled()) {//没充满非0数字

            int y = random.nextInt(width);
            int x = random.nextInt(width);
            if (tiles[y][x] == null) {//
                int n2Or4 = (random.nextInt(2) + 1) * 2;
                addTile(y, x, n2Or4);
                if (!isCanMove()) {
                    showGameOverDiallog();
                }
            } else {
                random2Or4();
            }
        }
    }

    private void showGameOverDiallog() {

        AlertDialog.Builder builder = new Builder(context);
        builder.setTitle("游戏结束！");
        builder.setMessage("是否继续游戏？");
        android.content.DialogInterface.OnClickListener listener = new MyOnclickListener();
        builder.setPositiveButton("继续游戏", listener);
        builder.setNegativeButton("退出游戏", listener);
        builder.setCancelable(false);//不能按退回键关闭
        builder.create().show();

    }

    /**
     * 对话框按钮事件监听器
     */
    public class MyOnclickListener implements android.content.DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {

            updateBestScore();
            switch (which) {
                case AlertDialog.BUTTON_POSITIVE:
                    //继续游戏
                    newGame();

                    break;
                //退出游戏
                case AlertDialog.BUTTON_NEGATIVE:
                    //context.finish();
                    break;
            }
        }
    }

    /**
     * 判断是否有相邻数字是否相等；
     * 
     * @return true ：有（非0）相邻数字相等
     */
    private boolean isCanMove() {

        for (int y = 0; y < width; y++) {
            for (int x = 0; x < width - 1; x++) {
                if (tiles[y][x] == null || tiles[y][x + 1] == null
                        || tiles[y][x].getRealNum() == tiles[y][x + 1].getRealNum()) {
                    return true;
                } else if (tiles[x][y] == null || tiles[x + 1][y] == null
                        || tiles[x][y].getRealNum() == tiles[x + 1][y].getRealNum()) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isAllFilled() {

        for (int y = 0; y < width; y++) {
            for (int x = 0; x < width; x++) {
                if (tiles[y][x] == null) {
                    return false;
                }
            }
        }
        return true;
    }

    private static enum Direction {
        LEFT,
        UP,
        RIGHT,
        DOWN;
    }
}
