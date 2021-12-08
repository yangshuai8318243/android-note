package com.mengjia.baseLibrary.mvp.factory;

import android.util.Log;

import com.mengjia.baseLibrary.mvp.PresenterInterface;
import com.mengjia.baseLibrary.mvp.ViewInterface;

import java.util.HashMap;
import java.util.Set;

public class PtrFactory implements PtrFactoryInterface {

    public static PtrFactory getFactory() {
        return BuildePtrFactory.sPtrFactory;
    }

    private HashMap<String, PresenterInterface> ptrCache = new HashMap<>();

    /**
     * 通过类名对ptr进行缓存，一个ptr只能缓存一次
     *
     * @param ptr
     * @param viewInterface
     * @param <P>
     * @param <V>
     * @return
     */
    @Override
    public <P extends PresenterInterface, V extends ViewInterface> P newPtr(Class<P> ptr, V viewInterface) {
        Log.e(getClass().getName(), "-------------------->" + ptr.getName());
        PresenterInterface presenterInterface = ptrCache.get(ptr.getName());
        if (presenterInterface != null) {
            return (P) presenterInterface;
        }
        try {
            P newInstance = ptr.newInstance();
            ptrCache.put(ptr.getName(), newInstance);
            return newInstance;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            Log.e("PtrFactory", e.getMessage());

        } catch (InstantiationException e) {
            e.printStackTrace();
            Log.e("PtrFactory", e.getMessage());

        }
        throw new RuntimeException("please inherit PresenterInterface");
    }

    @Override
    public <P extends PresenterInterface> void removePtr(Class<P> ptr) {
        PresenterInterface remove = ptrCache.remove(ptr.getName());
        if (remove != null) {
            remove.onDestroy();
        }
    }

    @Override
    public <P extends PresenterInterface> P getPtr(Class<P> ptr) {
        PresenterInterface presenterInterface = ptrCache.get(ptr.getName());
        if (presenterInterface != null) {
            return (P) presenterInterface;
        }
        return null;
    }

    @Override
    public void clearAll() {
        Set<String> strings = ptrCache.keySet();
        for (String key : strings) {
            PresenterInterface remove = ptrCache.remove(key);
            if (remove != null) {
                remove.onDestroy();
            }
        }
    }

    private static class BuildePtrFactory {
        private static PtrFactory sPtrFactory = new PtrFactory();
    }

    private PtrFactory() {
    }


}
