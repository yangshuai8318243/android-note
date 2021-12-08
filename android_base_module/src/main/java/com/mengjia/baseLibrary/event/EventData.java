package com.mengjia.baseLibrary.event;

/**
 * Created by Android Studio.
 * User: SnapeYang
 * Date: 2020/8/21
 * Time: 14:38
 */
public interface EventData {

    <T> T toData();

    byte[] toByte();
}
