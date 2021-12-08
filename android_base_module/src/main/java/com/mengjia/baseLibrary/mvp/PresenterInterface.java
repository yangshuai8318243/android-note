package com.mengjia.baseLibrary.mvp;


import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.OnLifecycleEvent;

import com.mengjia.baseLibrary.app.PermissionsListener;
import com.mengjia.baseLibrary.mvp.even.EvenInterface;

import io.reactivex.functions.Consumer;

public interface PresenterInterface<E extends EvenInterface> extends LifecycleObserver, Consumer<E>, PermissionsListener {
    @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
    void onAny(LifecycleOwner owner, Lifecycle.Event event);

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    void onCreate();

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    void onDestroy();

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    void onStart();

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    void onStop();

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    void onResume();

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    void onPause();

    void bindRxEven();

    void unBindRxEven();


    /**
     * 普通方式的v和p的通讯方式
     *
     * @param tag
     * @param evenChangeData
     * @param <T>
     */
    <T> void bindViewEven(String tag, EvenChangeData<T> evenChangeData);

    /**
     * 获取绑定的事件
     *
     * @param tag
     * @param <T>
     * @return
     */
    <T> EvenChangeData<T> getBindViewEven(String tag);

    /**
     * 普通通讯方式解绑
     */
    void unBindViewEvenAll();

    void bindViewLiveData(LifecycleOwner owner, String tag, Observer observer);

    interface EvenChangeData<T> {
        void setValue(T value);
    }

    /**
     * 解绑view层
     */
    void unBindView();
}
