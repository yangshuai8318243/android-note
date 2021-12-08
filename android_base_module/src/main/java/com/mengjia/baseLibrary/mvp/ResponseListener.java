package com.mengjia.baseLibrary.mvp;

public interface ResponseListener<T> {
    void onError(int code, String message);

    void onComplete(T data);
}
