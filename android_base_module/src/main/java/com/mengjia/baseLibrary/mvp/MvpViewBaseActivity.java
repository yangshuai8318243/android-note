package com.mengjia.baseLibrary.mvp;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

import com.mengjia.baseLibrary.app.BaseActivity;
import com.mengjia.baseLibrary.app.BaseAppCompatActivity;
import com.mengjia.baseLibrary.language.LanguageManager;
import com.mengjia.baseLibrary.log.AppLog;
import com.mengjia.baseLibrary.mvp.even.EvenInterface;
import com.mengjia.baseLibrary.mvp.factory.PtrFactory;
import com.mengjia.baseLibrary.mvp.factory.PtrFactoryInterface;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public abstract class MvpViewBaseActivity<P extends PresenterInterface, E extends EvenInterface> extends BaseActivity implements ViewInterface<E> {
    private P mPtr;
    private Subject<E> mEven;
    private Disposable mDisposable;
    //管理RxJava，主要针对RxJava异步操作造成的内存泄漏
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindPtr();
        bindBaseEven();
    }

    public void addDisposable(Disposable disposable) {
        mCompositeDisposable.add(disposable);
    }

    /**
     * 注册基本的回调事件
     */
    private void bindBaseEven() {

    }

    @Override
    public <ECD> void bindViewEven(String tag, PresenterInterface.EvenChangeData<ECD> observer) {
        mPtr.bindViewEven(tag, observer);
    }

    /**
     * 权限失败回调
     *
     * @param permission
     */
    @Override
    public void onFailurePermissions(String permission) {
        super.onFailurePermissions(permission);
        if (mPtr != null) {
            mPtr.onFailurePermissions(permission);
        }
    }

    /**
     * 权限成功回调
     *
     * @param permission
     */
    @Override
    public void onSuccessPermissions(String permission) {
        super.onSuccessPermissions(permission);
        if (mPtr != null) {
            mPtr.onSuccessPermissions(permission);
        }
    }

    @Override
    protected void onDestroy() {
        LanguageManager.getInstance().clearForContext(this);
        unBindPtr();
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
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

    @Override
    public void unBindPtr() {

        if (mEven != null) {
            mEven.onComplete();
            mEven = null;
        }

        if (mDisposable != null) {
            mDisposable.dispose();
            mDisposable = null;
        }

        if (mPtr != null) {
            mPtr.unBindView();
            mPtr = null;
        }


        mCompositeDisposable.clear();
        AppLog.e(TAG, "----------------->", mCompositeDisposable);
    }


    protected Subject<E> newSubscriber() {
        PublishSubject<E> objectPublishSubject = PublishSubject.create();
        return objectPublishSubject;
    }

    @Override
    public void sendEven(E even) {
        if (mEven != null) {
            mEven.onNext(even);
        } else {
            //没有对应的 事件发送器 检查是否有创建ptr对象
            Log.d(TAG, "There is no corresponding event sender to check if a ptr object has been created");
        }
    }

    protected PtrFactoryInterface getPtrFactory() {
        return PtrFactory.getFactory();
    }
}
