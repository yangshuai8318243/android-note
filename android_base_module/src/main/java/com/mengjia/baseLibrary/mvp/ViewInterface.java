package com.mengjia.baseLibrary.mvp;


import com.mengjia.baseLibrary.mvp.even.EvenInterface;

public interface ViewInterface<E extends EvenInterface> {
    void bindPtr();

    void unBindPtr();

    void sendEven(E even);

    <ECD> void bindViewEven(String tag, PresenterInterface.EvenChangeData<ECD> observer);

    /**
     * 卸载view的时候必须调用这个方法，根据不同的view实现，调用时机不同
     * <p>
     * 1、增加调用ptr解绑接口
     * 2、增加调用ptr绑定接口，重新绑定相关事件
     * 3、调用解绑事件之后，重置接口回调事件，同时在使用事件的地方要进行判断是否可以回调
     * 4、多语言释放资源
     * 5、view层对游戏消息的通知迁移到ptr
     * 6、动态布局是否有内存泄露
     */
    void onClean();
}
