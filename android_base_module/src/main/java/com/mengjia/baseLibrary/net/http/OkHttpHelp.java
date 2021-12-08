package com.mengjia.baseLibrary.net.http;

import com.google.gson.Gson;
import com.mengjia.baseLibrary.log.AppLog;

import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class OkHttpHelp {
    protected OkHttpClient okHttpClient;

    public void setOkHttpClient(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
    }

    public Request buildFileUploadRequest() {
        Request.Builder builder = new Request.Builder();
        return builder.build();
    }

    public synchronized Request buildReques(String requestUrl, Map<String, String> param) {
        Request.Builder builder = new Request.Builder();

        if (param != null) {
            FormBody.Builder formBder = new FormBody.Builder();
            for (String key : param.keySet()) {
                formBder.add(key, param.get(key));
            }
            builder.post(formBder.build());
        }

        Request build = builder.url(requestUrl).build();
        return build;
    }

    public synchronized <R> R getRequest(Class<R> rClass, String requestUrl) throws Throwable {
        return request(rClass, requestUrl, null);
    }

    public synchronized <R> R postRequest(Class<R> rClass, String requestUrl, Map<String, String> param) throws Throwable {
        return request(rClass, requestUrl, param);
    }

    public synchronized <R> R request(Class<R> rClass, String requestUrl, Map<String, String> param) throws Throwable {
        ResponseBody responseBody = requestBase(requestUrl, param);
        String string = responseBody.string();
        R fromJson = new Gson().<R>fromJson(string, rClass);
        return fromJson;
    }

    public synchronized byte[] getRequest(String requestUrl) throws Throwable {
        return request(requestUrl, null);
    }

    public synchronized byte[] postRequest(String requestUrl, Map<String, String> param) throws Throwable {
        return request(requestUrl, param);
    }

    public synchronized byte[] request(String requestUrl, Map<String, String> param) throws Throwable {
        ResponseBody responseBody = requestBase(requestUrl, param);
        return responseBody.bytes();
    }

    public synchronized ResponseBody requestBase(String requestUrl, Map<String, String> param) throws Throwable {
        if (okHttpClient == null) {
            AppLog.e("未初始化OkHttpHelp");
            return null;
        }

        Request request = buildReques(requestUrl, param);
        Call call = okHttpClient.newCall(request);

        Response execute = call.execute();

        if (execute.code() != 200) {
            throw new NetworkData.Builder().mesage(execute.message()).code(execute.code()).build();
        }
        ResponseBody body = execute.body();
        return body;
    }

    public synchronized void requestSyn(String requestUrl, Callback callback) {
        if (okHttpClient == null) {
            AppLog.e("未初始化OkHttpHelp");
            return;
        }

        Request request = buildReques(requestUrl, null);
        Call call = okHttpClient.newCall(request);
        call.enqueue(callback);
    }

    public synchronized void requestSyn(String requestUrl, Map<String, String> param, Callback callback) {
        if (okHttpClient == null) {
            AppLog.e("未初始化OkHttpHelp");
            return;
        }

        Request request = buildReques(requestUrl, param);
        Call call = okHttpClient.newCall(request);
        call.enqueue(callback);
    }


}
