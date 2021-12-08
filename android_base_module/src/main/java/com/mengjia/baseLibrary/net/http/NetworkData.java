package com.mengjia.baseLibrary.net.http;

/**
 * Created by Android Studio.
 * User: SnapeYang
 * Date: 2020/8/24
 * Time: 16:00
 */
public class NetworkData extends Exception{
    public static final int DEF_CODE = -1;
    private int code;
    private String mesage;
    private Object data;

    public <D> void setData(D data) {
        this.data = data;
    }

    public <D> D getData() {
        return (D) data;
    }

    private NetworkData(Builder builder) {
        code = builder.code;
        mesage = builder.mesage;
        data = builder.data;
    }

    public int getCode() {
        return code;
    }

    public String getMesage() {
        return mesage;
    }

    public boolean isOk() {
        return this.code == DEF_CODE;
    }

    public static final class Builder {
        private int code = DEF_CODE;
        private String mesage = "";
        private Object data;

        public Builder() {
        }

        public Builder code(int val) {
            code = val;
            return this;
        }

        public Builder mesage(String val) {
            mesage = val;
            return this;
        }

        public Builder data(Object val) {
            data = val;
            return this;
        }

        public NetworkData build() {
            return new NetworkData(this);
        }
    }

    @Override
    public String toString() {
        return "DecorationData{" +
                "code=" + code +
                ", mesage='" + mesage + '\'' +
                ", data=" + data +
                '}';
    }
}
