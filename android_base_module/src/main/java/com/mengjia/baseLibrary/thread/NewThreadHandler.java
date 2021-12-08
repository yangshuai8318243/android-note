package com.mengjia.baseLibrary.thread;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Process;

import androidx.annotation.NonNull;

/**
 * 自定义 线程Handler
 * 多数用于常驻的子线程
 * 如socket的读写独立线程，处理时需要返回到当前线程处理
 * 提供了和 handler 相同的方法
 */
public abstract class NewThreadHandler {

    private final HandlerThread handlerThread;
    private final HandlerTool handlerTool;

    public NewThreadHandler(String name) {
        handlerThread = new HandlerThread(name);
        handlerThread.start();
        handlerThread.setPriority(Process.THREAD_PRIORITY_BACKGROUND);
        handlerTool = new HandlerTool(handlerThread.getLooper(), this);
    }

    protected abstract void working(Message message);

    public void clean() {
        handlerThread.interrupt();
    }

    public final boolean post(@NonNull Runnable r) {
        return handlerTool.post(r);
    }

    public final boolean postAtTime(@NonNull Runnable r, long time) {
        return handlerTool.postAtTime(r, time);
    }

    public final boolean sendMessage(@NonNull Message msg) {
        return handlerTool.sendMessage(msg);
    }

    public final boolean sendEmptyMessage(int what) {
        return handlerTool.sendEmptyMessage(what);
    }

    public final boolean sendMessageDelayed(int what, long time) {
        Message message = handlerTool.obtainMessage();
        message.what = what;
        return handlerTool.sendMessageDelayed(message, time);
    }

    public final boolean sendEmptyMessageAtTime(int what, long time) {
        return handlerTool.sendEmptyMessageAtTime(what, time);
    }

    private static class HandlerTool extends Handler {
        private NewThreadHandler newThreadHandler;

        public HandlerTool(NewThreadHandler threadHandler) {
            this.newThreadHandler = threadHandler;
        }

        public HandlerTool(@NonNull Looper looper, NewThreadHandler threadHandler) {
            super(looper);
            this.newThreadHandler = threadHandler;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            newThreadHandler.working(msg);
        }
    }
}
