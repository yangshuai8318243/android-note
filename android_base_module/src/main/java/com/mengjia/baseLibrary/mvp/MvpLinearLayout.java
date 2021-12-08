package com.mengjia.baseLibrary.mvp;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.mengjia.baseLibrary.log.AppLog;
import com.mengjia.baseLibrary.mvp.even.EvenInterface;
import com.mengjia.baseLibrary.mvp.factory.PtrFactory;
import com.mengjia.baseLibrary.mvp.factory.PtrFactoryInterface;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public abstract class MvpLinearLayout<P extends PresenterInterface, E extends EvenInterface> extends LinearLayout implements ViewInterface<E> {
    protected static String TAG = "";
    private P mPtr;
    private Subject<E> mEven;
    private Disposable mDisposable;
    //管理RxJava，主要针对RxJava异步操作造成的内存泄漏
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    public MvpLinearLayout(Context context) {
        super(context);
    }

    public MvpLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MvpLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    protected void init(Context context) {
        bindPtr();
    }

    protected abstract P initPtr();

    @Override
    public void bindPtr() {
        mPtr = initPtr();
        if (mPtr != null) {
            mEven = newSubscriber();
            mDisposable = mEven.subscribe(mPtr);
        }
    }

    public void addDisposable(Disposable disposable) {
        mCompositeDisposable.add(disposable);
    }

    @Override
    public void unBindPtr() {
        if (mPtr != null) {
            mPtr.unBindView();
            mPtr = null;
        }
        if (mDisposable != null) {
            mDisposable.dispose();
            mDisposable = null;
        }

        mCompositeDisposable.clear();
    }

    @Override
    public <ECD> void bindViewEven(String tag, PresenterInterface.EvenChangeData<ECD> observer) {
        mPtr.bindViewEven(tag, observer);
    }

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
    protected void onDetachedFromWindow() {
        AppLog.e(TAG, "-----------onDetachedFromWindow------------->");
        super.onDetachedFromWindow();
    }


    /**
     * 清理当前数据
     */
    @Override
    public void onClean() {
        unBindPtr();
    }
}
