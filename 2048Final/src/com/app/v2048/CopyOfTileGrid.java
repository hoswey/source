package com.app.v2048;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.GridLayout;

/**
 * Title: TileGrid.java Description: Copyright: 2014 Duopay, all rights
 * reserved. Duopay PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * Company: Duopay
 * 
 * @author: Hoswey
 * @version: 1.0 Create at: 2014年7月22日
 */
public class CopyOfTileGrid extends GridLayout {

    private Paint paintBackground;

    private Context context;

    private final List<Tile> tiles = new ArrayList<Tile>();

    public CopyOfTileGrid(Context context) {

        super(context);
        init(context);
    }

    public CopyOfTileGrid(Context context, AttributeSet attrs) {

        super(context, attrs);
        init(context);
    }

    public CopyOfTileGrid(Context context, AttributeSet attrs, int defStyle) {

        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {

        this.context = context;

        paintBackground = new Paint();
        paintBackground.setStyle(Paint.Style.FILL);
        paintBackground.setColor(Color.parseColor("#CCC0B3"));

    }

    public void addTile(Tile tile, int x, int y) {

        Rect rect = getTileRect(x, y);
        int width = Math.abs(rect.top - rect.bottom);

        ViewGroup.LayoutParams vparams = new ViewGroup.LayoutParams(width, width);
        GridLayout.LayoutParams params = new GridLayout.LayoutParams(vparams);
        params.setMargins(rect.left, rect.top, 0, 0);

        tile.setLayoutParams(params);
        addView(tile);
    }

    public void removeTile(Tile tile) {

        removeView(tile);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        //        int w = MeasureSpec.getSize(widthMeasureSpec);
        //        int h = MeasureSpec.getSize(heightMeasureSpec);
        //        setMeasuredDimension(w, h);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        //Write the 4 * 4 tiles
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                Rect rect = getTileRect(j, i);
                canvas.drawRect(rect, paintBackground);
            }
        }
        super.onDraw(canvas);
    }

    private Rect getTileRect(int x, int y) {

        int width = getLayoutParams().height;
        //For the length 8/9 used for the 4 tile, and 1/9 used for the border
        int tileWidth = width * 9 / 10 / 4;
        int borderWidth = width * 1 / 10 / 5;

        int left = (x + 1) * borderWidth + x * tileWidth;
        int top = (y + 1) * borderWidth + y * tileWidth;
        int right = left + tileWidth;
        int bottom = top + tileWidth;

        Rect rect = new Rect(left, top, right, bottom);

        return rect;

    }

    private int getBorderWidth() {

        int width = getLayoutParams().height;
        //For the length 8/9 used for the 4 tile, and 1/9 used for the border
        int tileWidth = width * 9 / 10 / 4;
        int borderWidth = width * 1 / 10 / 5;

        return borderWidth;
    }
}
