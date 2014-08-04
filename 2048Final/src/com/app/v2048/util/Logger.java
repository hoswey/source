package com.app.v2048.util;

import android.util.Log;

/**
 * Title: Logger.java Description: Copyright: 2014 Duopay, all rights reserved.
 * Duopay PROPRIETARY/CONFIDENTIAL. Use is subject to license terms. Company:
 * Duopay
 * 
 * @author: Hoswey
 * @version: 1.0 Create at: 2014年6月19日
 */
public class Logger {

    public static void i(String message, Object... params) {

        Log.i("2048Final", String.format(message, params));
    }

 
}
