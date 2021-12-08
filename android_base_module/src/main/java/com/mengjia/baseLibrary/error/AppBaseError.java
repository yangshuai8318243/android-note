package com.mengjia.baseLibrary.error;

import android.os.Build;

/**
 * Created by Android Studio.
 * User: SnapeYang
 * Date: 2020/8/25
 * Time: 17:16
 */
public abstract class AppBaseError extends Exception {

    public AppBaseError() {
    }

    public AppBaseError(String message) {
        super(message);
    }

    public AppBaseError(String message, Throwable cause) {
        super(message, cause);
    }

    public AppBaseError(Throwable cause) {
        super(cause);
    }


    public abstract int getErrorCode();

    public abstract String getErrorMessage();

}
