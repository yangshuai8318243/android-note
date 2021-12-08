package com.mengjia.baseLibrary.app;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.lang.ref.WeakReference;

import androidx.annotation.NonNull;

public class AppHandler<T> extends Handler {
    private WeakReference<T> obj;
    private AppHandlerListener listener;

    public AppHandler() {
    }

    public AppHandler(T objSource) {
        super();
        this.obj = new WeakReference<>(objSource);
    }

    public AppHandler(@NonNull Looper looper, T obj) {
        super(looper);
        this.obj = new WeakReference<>(obj);
    }

    public AppHandler(@NonNull Looper looper, T obj, AppHandlerListener listener) {
        super(looper);
        this.obj = new WeakReference<>(obj);
        this.listener = listener;
    }

    public void setListener(AppHandlerListener listener) {
        listener.setObj(obj);
        this.listener = listener;
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        super.handleMessage(msg);
        if (listener != null) {
            listener.handleMessage(msg);
        }
    }

    public static abstract class AppHandlerListener<O> {
        protected WeakReference<O> soc;

        public void setObj(WeakReference<O> obj) {
            this.soc = obj;
        }

        public abstract void handleMessage(Message msg);
    }

}
