package com.mengjia.baseLibrary.mvp.even;

public interface EvenConstants {
    /**
     * 启动Activity统一事件
     */
    String START_ACTIVIT_PTR_TAG = "START_ACTIVIT_PTR_TAG";
    /**
     * 启动容器Activity统一事件
     */
    String START_CANONICAL_PTR_TAG = "START_CANONICAL_PTR_TAG";


    /**
     * 关闭Activity统一事件
     */
    String FINISH_ACTIVITY_PTR_TAG = "FINISH_ACTIVITY_PTR_TAG";


    /**
     * 权限申请统一事件
     */
    String REQUEST_PERMISSIONS = "REQUEST_PERMISSIONS";
    /**
     * 权限申请成功
     */
    String REQUEST_PERMISSIONS_SUCCESS = "REQUEST_PERMISSIONS_SUCCESS";
    /**
     * 权限申请失败
     */
    String REQUEST_PERMISSIONS_FAILURE = "REQUEST_PERMISSIONS_FAILURE";
}
