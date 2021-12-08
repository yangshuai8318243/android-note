package com.mengjia.baseLibrary.data;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class BaseData implements Parcelable {
    private static final byte Integer_TAG = 'I';
    private static final byte String_TAG = 'S';
    private static final byte Long_TAG = 'L';
    private static final byte Double_TAG = 'D';
    private static final byte Boolean_TAG = 'B';
    private static final byte BaseDataList_TAG = 'X';
    private static final byte BaseData_TAG = 'Y';
    private static final byte OBJ_TAG = 'O';
    private static final int CURRENT_PARCEL_VERSION = 1; // 当前序列化格式的版本号

    private static final String TAG = "BaseData";
    private Integer errorCode;
    private Long mLong;
    private Double mDouble;
    private Boolean isOk;
    private String message;
    private Map<String, String> mVarClassName;
    private Map<String, Object> mSimpleMapData;

    private BaseData(Builder builder) {
        setErrorCode(builder.errorCode);
        setmLong(builder.mLong);
        setmDouble(builder.mDouble);
        setIsOk(builder.isOk);
        setMessage(builder.message);
        mVarClassName = builder.mVarClassName;
        mSimpleMapData = builder.mSimpleMapData;
    }


    private boolean objType(Object o) {

        if (o instanceof String) {
            return true;
        }

        if (o instanceof Double) {
            return true;
        }

        if (o instanceof Integer) {
            return true;
        }

        if (o instanceof Boolean) {
            return true;
        }

        if (o instanceof Long) {
            return true;
        }

        if (o instanceof Parcelable) {
            return true;
        }

        return false;
    }

    public boolean putData(String key, Object o) {
        if (key == null || o == null) {
            return false;
        }

        if (!objType(o)) {
            return false;
        }

        String canonicalName = o.getClass().getCanonicalName();
        mVarClassName.put(key, canonicalName);
        mSimpleMapData.put(key, o);
        return true;
    }

    public <T> T getData(String key) {
        if (mSimpleMapData.size() > 0) {
            Object o = mSimpleMapData.get(key);
            return (T) o;
        }
        return null;
    }

    protected BaseData(Parcel in) {
        try {
            int parcelVersion = in.readInt();
            Log.e(TAG, "-----parcelVersion---->" + parcelVersion);
            fromParcelV1(in);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    public final boolean fromParcel(Parcel in) {
        try {
            int parcelVersion = in.readInt();
            if (parcelVersion == 1) { // 目前只支持第一版
                fromParcelV1(in);
            } else {
                throw new Exception("BaseData.fromParcel(in): unkown parcel version: " + parcelVersion);
            }

            return true;
        } catch (Throwable e) {
            Log.e(TAG, e.getMessage());
            return false;
        }
    }

    private void fromParcelV1(Parcel in) throws Throwable {
        byte b = in.readByte();
        checkVariable(b, Integer_TAG);
        errorCode = in.readInt();

        b = in.readByte();
        checkVariable(b, Long_TAG);
        mLong = in.readLong();

        b = in.readByte();
        checkVariable(b, Double_TAG);
        mDouble = in.readDouble();

        b = in.readByte();
        checkVariable(b, Boolean_TAG);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            isOk = in.readBoolean();
        } else {
            String s = in.readString();
            if (s == "0") {
                isOk = true;
            } else {
                isOk = false;
            }
        }

        b = in.readByte();
        checkVariable(b, String_TAG);
        message = in.readString();


        mSimpleMapData = new HashMap<>();
        mVarClassName = new HashMap<>();
        int itemCount = in.readInt();

        for (int i = 0; i < itemCount; i++) {
            String key = in.readString();
            byte type = in.readByte();

            if (type == BaseData_TAG) {
                BaseData baseData = new BaseData(in);
                mSimpleMapData.put(key, baseData);
                mVarClassName.put(key, BaseData.class.getCanonicalName());

            } else if (type == BaseDataList_TAG) {
                BaseDataList baseDataList = new BaseDataList(in);
                mSimpleMapData.put(key, baseDataList);
                mVarClassName.put(key, BaseDataList.class.getCanonicalName());

            } else if (type == String_TAG) {
                String data = in.readString();
                mSimpleMapData.put(key, data);
                mVarClassName.put(key, String.class.getCanonicalName());

            } else if (type == Long_TAG) {
                Long data = in.readLong();
                mSimpleMapData.put(key, data);
                mVarClassName.put(key, Long.class.getCanonicalName());

            } else if (type == Integer_TAG) {
                Integer data = in.readInt();
                mSimpleMapData.put(key, data);
                mVarClassName.put(key, Integer.class.getCanonicalName());

            } else if (type == Double_TAG) {
                Double data = in.readDouble();
                mSimpleMapData.put(key, data);
                mVarClassName.put(key, Double.class.getCanonicalName());

            } else if (type == Boolean_TAG) {
                Boolean data = (Boolean) in.readSerializable();
                mSimpleMapData.put(key, data);
                mVarClassName.put(key, Boolean.class.getCanonicalName());

            } else if (type == OBJ_TAG) {
                String className = in.readString();
                Class<?> aClass = Class.forName(className);
                Parcelable parcelable = in.readParcelable(aClass.getClassLoader());

                mSimpleMapData.put(key, parcelable);
                mVarClassName.put(key, aClass.getCanonicalName());

            }


        }

    }

    private void checkVariable(byte b, byte tag) {
        if (b != tag) {
            throw new RuntimeException("Current variable " + b + "is not " + tag);
        }
    }

    public static final Creator<BaseData> CREATOR = new Creator<BaseData>() {
        @Override
        public BaseData createFromParcel(Parcel in) {
            return new BaseData(in);
        }

        @Override
        public BaseData[] newArray(int size) {
            return new BaseData[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // 序列化对象的格式版本号
        dest.writeInt(CURRENT_PARCEL_VERSION);

        dest.writeByte(Integer_TAG);
        dest.writeInt(errorCode);

        dest.writeByte(Long_TAG);
        dest.writeLong(mLong);

        dest.writeByte(Double_TAG);
        dest.writeDouble(mDouble);

        dest.writeByte(Boolean_TAG);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            dest.writeBoolean(isOk);
        } else {
            dest.writeString(isOk ? "0" : "1");
        }

        dest.writeByte(String_TAG);
        dest.writeString(message);


        // 写入序列化对象的键值对个数
        dest.writeInt(mSimpleMapData.size());

        for (String key : mSimpleMapData.keySet()) {
            Object jo = mSimpleMapData.get(key);
            dest.writeString(key);

            if (jo instanceof BaseData) { // BaseData
                dest.writeByte(BaseData_TAG);
                ((BaseData) jo).writeToParcel(dest, flags);

            } else if (jo instanceof BaseDataList) { // BaseDataList
                dest.writeByte(BaseDataList_TAG);
                ((BaseDataList) jo).writeToParcel(dest, flags);

            } else if (jo instanceof String) { // String
                dest.writeByte(String_TAG);
                dest.writeString((String) jo);

            } else if (jo instanceof Long) { // Long
                dest.writeByte(Long_TAG);
                dest.writeLong((Long) jo);

            } else if (jo instanceof Integer) { // Integer
                dest.writeByte(Integer_TAG);
                dest.writeInt((Integer) jo);

            } else if (jo instanceof Double) { // Double
                dest.writeByte(Double_TAG);
                dest.writeDouble((Double) jo);

            } else if (jo instanceof Boolean) { // Boolean
                dest.writeByte(Boolean_TAG);
                dest.writeSerializable((Boolean) jo);

            } else { // 其他类型
                String className = mVarClassName.get(key);

                dest.writeByte(OBJ_TAG);
                dest.writeString(className);
                dest.writeParcelable((Parcelable) jo, flags);
            }

        }
    }


    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public Long getmLong() {
        return mLong;
    }

    public void setmLong(Long mLong) {
        this.mLong = mLong;
    }

    public Double getmDouble() {
        return mDouble;
    }

    public void setmDouble(Double mDouble) {
        this.mDouble = mDouble;
    }

    public Boolean getIsOk() {
        return isOk;
    }

    public void setIsOk(Boolean isOk) {
        this.isOk = isOk;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String print() {
        return print(true, false);
    }

    public String print(boolean isSimple, boolean isShowTag) {
        StringBuilder stringBuilder = new StringBuilder();
        String s = stringBuilder.append("BaseData_TAG = " + BaseData_TAG)
                .append("\n")
                .append("BaseDataList_TAG = " + BaseDataList_TAG)
                .append("\n")
                .append("Integer_TAG = " + Integer_TAG)
                .append("\n")
                .append("Long_TAG = " + Long_TAG)
                .append("\n")
                .append("Double_TAG = " + Double_TAG)
                .append("\n")
                .append("Boolean_TAG = " + Boolean_TAG)
                .append("\n")
                .append("String_TAG = " + String_TAG)
                .append("\n").toString();
        String s1 = "BaseData{" +
                "mInt=" + errorCode +
                "\n" +
                ", mLong=" + mLong +
                 "\n" +
                ", mDouble=" + mDouble +
                "\n" +
                ", mBoolean=" + isOk +
                "\n" +
                ", mSimpleMapData=" + mSimpleMapData +
                "\n" +
                ", mStr='" + message + '\'';
        if (isSimple) {
            s1 = s1 + "}";
        } else {
            s1 = s1 +
                    ", mVarClassName=" + mVarClassName +
                    "\n" +
                    ", mSimpleMapData=" + mSimpleMapData +
                    "\n" +
                    '}';
        }

        if (isShowTag) {
            s1 = s1 + "\n" + s;
        }
        return s1;
    }

    public static final class Builder {
        private Integer errorCode = -1;
        private Long mLong = -1L;
        private Double mDouble = -1.0;
        private Boolean isOk = true;
        private String message = "";
        private Map<String, String> mVarClassName = new HashMap<>();
        private Map<String, Object> mSimpleMapData = new HashMap<>();

        public Builder() {
        }

        public Builder errorCode(Integer val) {
            errorCode = val;
            return this;
        }

        public Builder mLong(Long val) {
            mLong = val;
            return this;
        }

        public Builder mDouble(Double val) {
            mDouble = val;
            return this;
        }

        public Builder isOk(Boolean val) {
            isOk = val;
            return this;
        }

        public Builder message(String val) {
            this.message = val;
            return this;
        }

        public Builder mVarClassName(Map<String, String> val) {
            mVarClassName = val;
            return this;
        }

        public Builder mSimpleMapData(Map<String, Object> val) {
            mSimpleMapData = val;
            return this;
        }

        public BaseData build() {
            return new BaseData(this);
        }
    }
}
