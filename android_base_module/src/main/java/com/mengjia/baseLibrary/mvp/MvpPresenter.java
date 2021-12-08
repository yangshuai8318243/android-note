package com.mengjia.baseLibrary.mvp;


import android.os.Bundle;
import android.util.Log;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import com.mengjia.baseLibrary.log.AppLog;
import com.mengjia.baseLibrary.mvp.data.MvpData;
import com.mengjia.baseLibrary.mvp.even.EvenConstants;
import com.mengjia.baseLibrary.mvp.even.EvenInterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public abstract class MvpPresenter<E extends EvenInterface, M extends ModelInterface> implements PresenterInterface<E> {

    protected static String TAG = "";
    protected M mModel;
    private Map<String, LiveData> mLiveDataMap;
    private Map<String, EvenChangeData> mEvenChangeDataMap;

    //管理RxJava，主要针对RxJava异步操作造成的内存泄漏
    private CompositeDisposable mCompositeDisposable;
    private MediatorLiveData<MvpData> mStartActivityLive;
    private MediatorLiveData<MvpData> mStartCanonicalActivityLive;
    private MediatorLiveData<MvpData> mRequestPermission;
    private MediatorLiveData<MvpData> mFinishActivity;

    public MvpPresenter() {
        TAG = getClass().getName();
        this.mModel = bindModel();
        this.mLiveDataMap = addLiveData();
        this.mEvenChangeDataMap = new HashMap<>();
        bindStarActivityEven();
        mCompositeDisposable = new CompositeDisposable();
        init();
    }

    protected void init() {
    }


    @Override
    public void onSuccessPermissions(String permission) {

    }

    @Override
    public void onFailurePermissions(String permission) {

    }

    /**
     * 创建需要view响应的livedata
     *
     * @return
     */
    protected Map<String, LiveData> addLiveData() {
        return new HashMap<>();
    }

    /**
     * 将RxJava的事件管理起来统一处理
     *
     * @param disposable
     */
    protected void addSubscribe(Disposable disposable) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = new CompositeDisposable();
        }
        mCompositeDisposable.add(disposable);
    }

    /**
     * 通过LiveData的方式
     * 绑定ViewInterface对应的livedata对象
     *
     * @param owner
     * @param tag
     * @param observer
     */
    @Override
    public void bindViewLiveData(LifecycleOwner owner, String tag, Observer observer) {
//        Log.e(TAG, "----------bindViewLiveData------>" + tag);

        checkLiveDataMap();
        LiveData liveData = mLiveDataMap.get(tag);

        if (liveData == null) {
            throw new RuntimeException("Please confirm if LiveData was created");
        } else {
            liveData.observe(owner, observer);
        }
    }

    /**
     * 检查mLiveDataMap
     */
    private void checkLiveDataMap() {
        if (mLiveDataMap == null) {
            throw new RuntimeException("Please confirm if LiveData was created");
        }
    }

    /**
     * 绑定基础事件
     */
    private void bindStarActivityEven() {
        checkLiveDataMap();
        mStartActivityLive = new MediatorLiveData<>();
        mLiveDataMap.put(EvenConstants.START_ACTIVIT_PTR_TAG, mStartActivityLive);

        mStartCanonicalActivityLive = new MediatorLiveData<>();
        mLiveDataMap.put(EvenConstants.START_CANONICAL_PTR_TAG, mStartCanonicalActivityLive);

        mRequestPermission = new MediatorLiveData<>();
        mLiveDataMap.put(EvenConstants.REQUEST_PERMISSIONS, mRequestPermission);

        mFinishActivity = new MediatorLiveData<>();
        mLiveDataMap.put(EvenConstants.FINISH_ACTIVITY_PTR_TAG, mFinishActivity);
    }

    /**
     * 关闭当前Activity
     */
    protected void finishActivity() {
        mFinishActivity.setValue(new MvpData());
    }

    /**
     * 单个权限申请
     *
     * @param permission
     */
    protected void requestPermissions(String permission) {
        String[] strings = new String[]{permission};
        requestPermissions(strings);
    }

    /**
     * 多个权限申请
     *
     * @param permissions
     */
    @Override
    public void requestPermissions(String[] permissions) {
        MvpData mvpData = new MvpData();
        ArrayList<String> strings = new ArrayList<>();
        Collections.addAll(strings, permissions);
        mvpData.add(strings);
        mRequestPermission.setValue(mvpData);
    }


    /**
     * 跳转页面
     *
     * @param className 所跳转的目的Activity类名
     */
    protected void startActivity(String className) {
        try {
            Class<?> aClass = Class.forName(className);
            startActivity(aClass, null);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    /**
     * 跳转页面
     *
     * @param clz 所跳转的目的Activity类
     */
    protected void startActivity(Class<?> clz) {
        startActivity(clz, null);
    }

    /**
     * 跳转页面
     *
     * @param clz    所跳转的目的Activity类
     * @param bundle 跳转所携带的信息
     */
    protected void startActivity(Class<?> clz, Bundle bundle) {
        MvpData mvpData = new MvpData();
        mvpData.setmClassName(clz);
        mvpData.setmData(bundle);
        if (mStartActivityLive != null) {
            mStartActivityLive.setValue(mvpData);
        } else {
            Log.e(TAG, "mStartActivityLive was not created");
        }
    }

    /**
     * 跳转容器页面
     *
     * @param canonicalName 规范名 : Fragment.class.getCanonicalName()
     */
    public void startContainerActivity(String canonicalName) {
        startContainerActivity(canonicalName, null);
    }


    /**
     * 跳转容器页面
     *
     * @param canonicalName 规范名 : Fragment.class.getCanonicalName()
     * @param bundle        跳转所携带的信息
     */
    public void startContainerActivity(String canonicalName, Bundle bundle) {
        MvpData mvpData = new MvpData();
        mvpData.setmFragmentName(canonicalName);
        mvpData.setmData(bundle);
        if (mStartCanonicalActivityLive != null) {
            mStartCanonicalActivityLive.setValue(mvpData);
        } else {
            Log.e(TAG, "mStartCanonicalActivityLive was not created");
        }

    }

    /**
     * Lifecycle基础接口
     *
     * @param owner
     * @param event
     */
    @Override
    public void onAny(LifecycleOwner owner, Lifecycle.Event event) {

    }

    /**
     * 生命周期回调
     */
    @Override
    public void onCreate() {
    }

    /**
     * 生命周期回调
     */
    @Override
    public void onDestroy() {
        if (mModel != null) {
            mModel.onCleared();
        }
        //ViewModel销毁时会执行，同时取消所有异步任务
        if (mCompositeDisposable != null) {
            mCompositeDisposable.clear();
        }

        mLiveDataMap.clear();

        unBindRxEven();

        unBindViewEvenAll();
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }


    /**
     * 绑定事件
     */
    @Override
    public void bindRxEven() {

    }

    /**
     * 解绑事件
     */
    @Override
    public void unBindRxEven() {

    }

    @Override
    public void unBindView() {
        unBindRxEven();
        unBindViewEvenAll();
    }

    /**
     * 通过回调的方式ptr获取绑定的事件对象
     *
     * @param tag
     * @param <T>
     * @return
     */
    @Override
    public <T> EvenChangeData<T> getBindViewEven(String tag) {
        return mEvenChangeDataMap.get(tag);
    }

    /**
     * 通过回调的方式注册事件
     *
     * @param tag
     * @param evenChangeData
     * @param <T>
     */
    @Override
    public <T> void bindViewEven(String tag, EvenChangeData<T> evenChangeData) {
        mEvenChangeDataMap.put(tag, evenChangeData);
    }

    /**
     * 清除所有绑定的事件
     */
    @Override
    public void unBindViewEvenAll() {
        mEvenChangeDataMap.clear();
        int size = mEvenChangeDataMap.size();
        AppLog.e(TAG, "------unBindViewEvenAll---->", size);
    }

    /**
     * 绑定model
     *
     * @return
     */
    protected abstract M bindModel();

    /**
     * 创建model
     *
     * @param mClass
     * @return
     */
    protected M coreateModel(Class<M> mClass) {
        try {
            return mClass.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException("Cannot create an instance of " + mClass, e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Cannot create an instance of " + mClass, e);
        }
    }

}
