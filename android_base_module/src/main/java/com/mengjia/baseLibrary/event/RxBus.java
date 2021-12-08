package com.mengjia.baseLibrary.event;

import android.text.TextUtils;
import android.util.Log;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;


import com.mengjia.baseLibrary.log.AppLog;
import com.mengjia.baseLibrary.utils.RxUtils;
import com.trello.lifecycle2.android.lifecycle.AndroidLifecycle;
import com.trello.rxlifecycle2.LifecycleProvider;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * RxBus，RxJava+Rxlifecycle消息传递
 */
public class RxBus {
    private final Subject<Object> mBus;
    private final Map<Class<?>, Object> mStickyEventMap;

    private RxBus() {
        mBus = PublishSubject.create().toSerialized();
        mStickyEventMap = new ConcurrentHashMap<>();
    }

    private static class RxBusHolder {
        private static final RxBus mDefaultInstance = new RxBus();
    }

    public static RxBus getInstance() {
        return RxBusHolder.mDefaultInstance;
    }

    /**
     * 发送事件
     */
    public void post(Object event) {
        mBus.onNext(event);
    }

    /**
     * 通过类型和标示发送事件数据
     * created at 2020/8/21 15:36
     *
     * @author SnapeYang
     */
    public void post(final String eventType, final String tag, EventData eventData) {
        DefEvent defEvent = new DefEvent.Builder().eventData(eventData).type(eventType).tag(tag).build();
        mBus.onNext(defEvent);
    }

    /**
     * 根据传递的 eventType 类型返回特定类型(eventType)的 被观察者
     */
    public <T> Observable<T> toObservable(final Class<T> eventType) {
        return mBus.ofType(eventType);
    }

    /**
     * 增加通过 type和tag的方式进行区别事件
     * created at 2020/8/21 14:57
     *
     * @author SnapeYang
     */
    public Observable<DefEvent> toObservable(final String eventType, final String tag) {
        return toObservable(DefEvent.class).filter(new Predicate<DefEvent>() {
            @Override
            public boolean test(DefEvent defEvent) throws Exception {
                if (defEvent.getType().equals(eventType) && defEvent.getTag().equals(tag))
                    return true;
                return false;
            }
        });
    }

    /**
     * 注册接受者在主线程的事件
     *
     * @param eventType
     * @param tag
     * @return
     */
    public Observable<DefEvent> toObservableUiThread(final String eventType, final String tag) {
        return toObservable(DefEvent.class).compose(RxUtils.<DefEvent>schedulersTransformer()).filter(new Predicate<DefEvent>() {
            @Override
            public boolean test(DefEvent defEvent) throws Exception {
                if (defEvent.getType().equals(eventType) && defEvent.getTag().equals(tag))
                    return true;
                return false;
            }
        });
    }

    /**
     * 接受某个类型的所有消息
     * created at 2020/8/21 15:03
     *
     * @author SnapeYang
     */
    public Observable<DefEvent> toObservableFilter(final String eventType) {
        return toObservable(DefEvent.class).filter(new Predicate<DefEvent>() {
            @Override
            public boolean test(DefEvent defEvent) throws Exception {
                if (defEvent.getType().equals(eventType))
                    return true;
                return false;
            }
        });
    }

    /**
     * 对生命周期做处理的观察者,默认事件类型
     * created at 2020/8/21 15:08
     *
     * @author SnapeYang
     */
    public Observable<DefEvent> toObservable(LifecycleOwner owner, final String eventType, final String tag) {
        return toObservable(owner, DefEvent.class, Lifecycle.Event.ON_DESTROY, eventType, tag);
    }

    /**
     * 对生命周期做处理的观察者,默认事件类型，接收某个类型的所有事件
     * created at 2020/8/21 15:08
     *
     * @author SnapeYang
     */
    public Observable<DefEvent> toObservable(LifecycleOwner owner, final String eventType) {
        return toObservable(owner, DefEvent.class, Lifecycle.Event.ON_DESTROY, eventType, null);
    }

    /**
     * 对生命周期做处理的观察者
     * created at 2020/8/21 15:05
     *
     * @author SnapeYang
     */
    public <T> Observable<T> toObservable(LifecycleOwner owner, final Class<T> eventType) {
        return toObservable(owner, eventType, Lifecycle.Event.ON_DESTROY, null, null);
    }

    /**
     * 使用Rxlifecycle解决RxJava引起的内存泄漏
     */
    public <T> Observable<T> toObservable(LifecycleOwner owner, final Class<T> eventType, Lifecycle.Event event, final String type, final String tag) {
        LifecycleProvider<Lifecycle.Event> provider = AndroidLifecycle.createLifecycleProvider(owner);
        return mBus.ofType(eventType)
                .filter(new Predicate<T>() {
                    @Override
                    public boolean test(T t) throws Exception {
                        AppLog.e("RXBUS", "---111-->");

                        if (t instanceof DefEvent) {
                            AppLog.e("RXBUS", "---222-->", type, tag);
                            DefEvent defEvent = (DefEvent) t;
                            AppLog.e("RXBUS", "---333-->", defEvent.getType(), defEvent.getTag());

                            if (!TextUtils.isEmpty(type)) {
                                if (TextUtils.isEmpty(tag)) {
                                    return defEvent.getType().equals(type);
                                } else {
                                    return defEvent.getType().equals(type) && defEvent.getTag().equals(tag);
                                }
                            }
                            return false;
                        }
                        return true;
                    }
                })
                .doOnDispose(new Action() {
                    @Override
                    public void run() throws Exception {
                        Log.i("RxBus", "RxBus取消订阅");
                    }
                })
                .compose(provider.<T>bindUntilEvent(event))
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 判断是否有订阅者
     */
    public boolean hasObservers() {
        return mBus.hasObservers();
    }

    /**
     * Stciky 相关
     */

    /**
     * 发送一个新Sticky事件
     */
    public void postSticky(Object event) {
        synchronized (mStickyEventMap) {
            mStickyEventMap.put(event.getClass(), event);
        }
        post(event);
    }

    public <T> Observable<T> toObservableSticky(LifecycleOwner owner, final Class<T> eventType) {
        return toObservableSticky(owner, eventType, Lifecycle.Event.ON_DESTROY);
    }

    /**
     * 根据传递的 eventType 类型返回特定类型(eventType)的 被观察者
     * 使用Rxlifecycle解决RxJava引起的内存泄漏
     */
    public <T> Observable<T> toObservableSticky(LifecycleOwner owner, final Class<T> eventType, Lifecycle.Event e) {
        synchronized (mStickyEventMap) {
            LifecycleProvider<Lifecycle.Event> provider = AndroidLifecycle.createLifecycleProvider(owner);
            Observable<T> observable = mBus.ofType(eventType)
                    .doOnDispose(new Action() {
                        @Override
                        public void run() throws Exception {
                            Log.i("RxBus", "RxBus取消订阅");
                        }
                    })
                    .compose(provider.<T>bindUntilEvent(e))
                    .subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
            final Object event = mStickyEventMap.get(eventType);

            if (event != null) {
                return observable.mergeWith(Observable.create(new ObservableOnSubscribe<T>() {
                    @Override
                    public void subscribe(ObservableEmitter<T> subscriber) throws Exception {
                        subscriber.onNext(eventType.cast(event));
                    }
                }));
            } else {
                return observable;
            }
        }
    }

    /**
     * 根据eventType获取Sticky事件
     */
    public <T> T getStickyEvent(Class<T> eventType) {
        synchronized (mStickyEventMap) {
            return eventType.cast(mStickyEventMap.get(eventType));
        }
    }

    /**
     * 移除指定eventType的Sticky事件
     */
    public <T> T removeStickyEvent(Class<T> eventType) {
        synchronized (mStickyEventMap) {
            return eventType.cast(mStickyEventMap.remove(eventType));
        }
    }

    /**
     * 移除所有的Sticky事件
     */
    public void removeAllStickyEvents() {
        synchronized (mStickyEventMap) {
            mStickyEventMap.clear();
        }
    }

}
