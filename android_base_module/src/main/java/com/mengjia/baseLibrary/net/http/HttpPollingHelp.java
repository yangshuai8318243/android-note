package com.mengjia.baseLibrary.net.http;

import com.mengjia.baseLibrary.log.AppLog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpPollingHelp extends OkHttpHelp {
    private static final String TAG = "HttpPollingHelp";
    private PollingInterceptor pollingInterceptor;

    private static final class HttpPollingHelpHolder {
        private static final HttpPollingHelp HTTP_POLLING_HELP = new HttpPollingHelp();
    }

    public static HttpPollingHelp getInstance() {
        return HttpPollingHelpHolder.HTTP_POLLING_HELP;
    }

    private HttpPollingHelp() {
    }

    public void init(OkHttpClient okHttpClient) {
        pollingInterceptor = new PollingInterceptor();
        okHttpClient = okHttpClient.newBuilder()
                .addInterceptor(pollingInterceptor)
                .build();
        setOkHttpClient(okHttpClient);
    }

    public synchronized void requestSynPollingUrl(List<String> urls, Callback callback) {
        if (urls.size() <= 0) return;
        pollingInterceptor.addUrls(urls);
        requestSyn(urls.get(0), callback);
    }

    public synchronized void requestSynPollingSize(String requestUrl, int size, Callback callback) {
        pollingInterceptor.setPollingSize(size);
        requestSyn(requestUrl, callback);
    }


    public static final class PollingInterceptor implements Interceptor {
        private List<String> urlList = new ArrayList<>();
        private int pollingSize = -1;


        public void addUrls(List<String> list) {
            urlList.addAll(list);
        }

        public void addUrl(String url) {
            urlList.add(url);
        }


        public void setPollingSize(int pollingSize) {
            this.pollingSize = pollingSize;
        }

        private void clean() {
            urlList.clear();
            pollingSize = -1;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Response proceed = chain.proceed(request);
            AppLog.e(TAG, "--PollingInterceptor--->", pollingSize, urlList);
            if (pollingSize > 0) {
                Response response = null;
                try {
                    response = pollingFrequency(request, chain);
                    if (response != null) {
                        proceed = response;
                    }
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }

            }

            if (urlList.size() > 0) {
                Response response = null;
                try {
                    response = pollingUrl(request, chain);
                    if (response != null) {
                        proceed = response;
                    }
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }

            }
            clean();
            return proceed;
        }

        private Response pollingFrequency(Request request, Chain chain) throws Throwable {
            if (pollingSize < 0) {
                return chain.proceed(request);
            } else {
                Response proceed = null;
                AppLog.e(TAG, "--pollingFrequency--->", pollingSize);

                for (int i = 0; i < pollingSize; i++) {
                    proceed = chain.proceed(request);
                    if (proceed.code() == 200) {
                        break;
                    }
                }
                return proceed;
            }
        }

        private Response pollingUrl(Request request, Chain chain) throws Throwable {
            if (urlList.size() <= 0) {
                return chain.proceed(request);
            } else {
                Response proceed = null;
                AppLog.e(TAG, "--pollingUrl--->", urlList);

                for (int i = 0; i < urlList.size(); i++) {
                    String url = urlList.get(i);
                    Request build = request.newBuilder().url(url).build();
                    proceed = chain.proceed(build);
                    Response build1 = proceed.newBuilder().build();
                    if (proceed.code() == 200) {
                        proceed = build1;
                        break;
                    }
                }
                return proceed;
            }
        }
    }

    public static interface PollingCall {
        boolean isPolling(Response response) throws Throwable;
    }
}
