package com.mengjia.baseLibrary.mvp.even;

public interface EvenInterface<E> {
    String getTag();

    void setTag(String tag);

    E getData();

    void setData(E data);
}
