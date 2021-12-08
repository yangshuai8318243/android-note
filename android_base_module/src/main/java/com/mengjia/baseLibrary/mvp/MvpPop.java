package com.mengjia.baseLibrary.mvp;

import android.content.Context;
import android.view.View;
import android.widget.PopupWindow;

import com.mengjia.baseLibrary.mvp.even.EvenInterface;
import com.mengjia.baseLibrary.mvp.factory.PtrFactory;
import com.mengjia.baseLibrary.mvp.factory.PtrFactoryInterface;

import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * Created by Android Studio.
 * User: SnapeYang
 * Date: 2020/9/25
 * Time: 13:51
 */
public abstract class MvpPop<P extends PresenterInterface, E extends EvenInterface> extends PopupWindow implements ViewInterface<E> {
    protected static String TAG = "";
    private P mPtr;
    private Subject<E> mEven;
    private Disposable mDisposable;

    public MvpPop(Context context) {
        super(context);
        TAG = getClass().getCanonicalName();
    }

    protected <T extends View> T findViewById(int id) {
        return getContentView().findViewById(id);
    }

    protected void init(Context context) {
        bindPtr();
    }

    protected abstract P initPtr();



    @Override
    public void bindPtr() {
        mPtr = initPtr();
        if (mPtr != null) {
//            getLifecycle().addObserver(mPtr);
            mEven = newSubscriber();
            mDisposable = mEven.subscribe(mPtr);
        }
    }

    @Override
    public void unBindPtr() {
        if (mPtr != null) {
            mPtr.onDestroy();
            mPtr = null;
        }
        if (mDisposable != null) {
            mDisposable.dispose();
        }
    }

    /**
     * 通过回调的方式绑定事件
     *
     * @param tag
     * @param observer
     */
    @Override
    public <ECD> void bindViewEven(String tag, PresenterInterface.EvenChangeData<ECD> observer) {
        mPtr.bindViewEven(tag, observer);
    }
    /**
     * 创建发送事件的对象
     *
     * @return
     */
    protected Subject<E> newSubscriber() {
        PublishSubject<E> objectPublishSubject = PublishSubject.create();
        return objectPublishSubject;
    }

    @Override
    public void sendEven(E even) {
        mEven.onNext(even);
    }


    protected PtrFactoryInterface getPtrFactory() {
        return PtrFactory.getFactory();
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    /**
     * 销毁并且清理
     */
    public void dismissClean() {
        onClean();
        dismiss();
    }

    /**
     * 清理当前数据
     */
    public void onClean() {
        unBindPtr();
    }


}
