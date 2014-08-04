package com.app.v2048;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

@SuppressLint("UseSparseArrays")
public class Tile extends View {

    private int number;

    public Tile(Context context) {

        super(context);
        setNumber(0);
    }

    public Tile(Context context, AttributeSet attrs) {

        super(context, attrs);
        setNumber(1024);
    }

    public void setNumber(int number) {

        this.number = number;
        invalidate();
    }

    public int getNumber() {

        return number;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        onDrawNum(canvas, number);
    }

    public void onDrawNum(Canvas canvas, int number) {

        ViewStyle viewStyle = VIEW_STYLES.get(number);

        Rect r = new Rect();
        r.set(0, 0, super.getLayoutParams().width, super.getLayoutParams().height);

        int bgcolor = viewStyle.getBgColor();

        Paint paint = new Paint();

        paint.setColor(bgcolor);
        canvas.drawRect(r, paint);

        paint.setColor(viewStyle.getFontColor());
        paint.setTextSize(50);
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setFakeBoldText(true);
        String snumber = number == 0 ? "" : String.valueOf(number);

        Rect bounds = new Rect();
        paint.getTextBounds(snumber, 0, snumber.length(), bounds);

        int x = canvas.getWidth() / 2 - bounds.width() / 2;
        int y = canvas.getHeight() / 2 + bounds.height() / 2;
        canvas.drawText(snumber, x, y, paint);

    }

    private static Map<Integer, ViewStyle> VIEW_STYLES = null;

    static {

        VIEW_STYLES = new HashMap<Integer, ViewStyle>();
        VIEW_STYLES.put(0, new ViewStyle("#CCC0B3", "#CCC0B3"));
        VIEW_STYLES.put(2, new ViewStyle("#EEE4DA", "#776E65"));
        VIEW_STYLES.put(4, new ViewStyle("#EDE0C8", "#776E65"));
        VIEW_STYLES.put(8, new ViewStyle("#F2B179", "#f9f6f2"));
        VIEW_STYLES.put(16, new ViewStyle("#F49563", "#f9f6f2"));
        VIEW_STYLES.put(32, new ViewStyle("#F5794D", "#f9f6f2"));
        VIEW_STYLES.put(64, new ViewStyle("#F55D37", "#f9f6f2"));
        VIEW_STYLES.put(128, new ViewStyle("#EEE863", "#f9f6f2"));
        VIEW_STYLES.put(256, new ViewStyle("#EDB04D", "#f9f6f2"));
        VIEW_STYLES.put(512, new ViewStyle("#ECB04D", "#f9f6f2"));
        VIEW_STYLES.put(1024, new ViewStyle("#EB9437", "#f9f6f2"));
        VIEW_STYLES.put(2048, new ViewStyle("#EA7821", "#f9f6f2"));
    }

    private static class ViewStyle {

        private final int bgColor;

        private final int fontColor;

        public ViewStyle(String bgColor, String fontColor) {

            super();
            this.bgColor = Color.parseColor(bgColor);
            this.fontColor = Color.parseColor(fontColor);
        }

        public int getBgColor() {

            return bgColor;
        }

        public int getFontColor() {

            return fontColor;
        }

    }

    @Override
    public int hashCode() {

        final int prime = 31;
        int result = 1;
        result = prime * result + number;
        return result;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Tile other = (Tile) obj;
        if (number != other.number)
            return false;
        return true;
    } 
}
