package com.app.v2048;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class BestScode {

    private final SharedPreferences sp;

    public BestScode(Context context) {

        sp = context.getSharedPreferences("bestscode", Context.MODE_PRIVATE);
    }

    public int getBestScode() {

        int bestscode = sp.getInt("bestscode", 0);
        return bestscode;
    }

    public void setBestScode(int bestScode) {

        Editor editor = sp.edit();
        editor.putInt("bestscode", bestScode);
        editor.commit();
    }
}
