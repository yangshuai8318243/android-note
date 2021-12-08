package com.mengjia.baseLibrary.mvp.even;


import com.mengjia.baseLibrary.data.BaseData;

public class DefMvpEven implements EvenInterface<BaseData> {
    private String mTag = "";
    private BaseData mDefultData;


    @Override
    public String getTag() {
        return mTag;
    }

    @Override
    public void setTag(String tag) {
        mTag = tag;
    }


    @Override
    public BaseData getData() {
        return mDefultData;
    }

    @Override
    public void setData(BaseData data) {
        this.mDefultData = data;
    }



}
