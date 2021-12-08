package com.mengjia.baseLibrary.mvp.factory;


import com.mengjia.baseLibrary.mvp.ModelInterface;

public interface ModelFactoryInterface {
    <M extends ModelInterface>  M  newModel(Class<M> model);
}
