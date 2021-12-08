package com.mengjia.baseLibrary.mvp.factory;


import com.mengjia.baseLibrary.mvp.PresenterInterface;
import com.mengjia.baseLibrary.mvp.ViewInterface;

public interface PtrFactoryInterface {
    <P extends PresenterInterface, V extends ViewInterface> P newPtr(Class<P> ptr, V viewInterface);

    <P extends PresenterInterface> void removePtr(Class<P> ptr);

    <P extends PresenterInterface> P getPtr(Class<P> ptr);

    void clearAll();
}
