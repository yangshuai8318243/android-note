package com.mengjia.baseLibrary.mvp;


import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public abstract class MvpModel implements ModelInterface {
    protected static String TAG = "";
    //管理RxJava，主要针对RxJava异步操作造成的内存泄漏
    private CompositeDisposable mCompositeDisposable;


    public MvpModel() {
        TAG = getClass().getName();
//        WorkManager.initialize(BaseApp.getInstance(), new Configuration.Builder().build());
//        WorkManager instance = WorkManager.getInstance(BaseApp.getInstance());
//        instance.enqueue(new WorkRequest() {
//            @NonNull
//            @Override
//            public UUID getId() {
//                return super.getId();
//            }
//        });
        mCompositeDisposable = new CompositeDisposable();
    }

    protected void addDisposable(Disposable disposable) {
        mCompositeDisposable.add(disposable);
    }

    protected void deleteDisposable(Disposable disposable) {
        if (disposable == null) return;
        mCompositeDisposable.delete(disposable);
    }

    @Override
    public void onCleared() {
        mCompositeDisposable.clear();
    }
}
