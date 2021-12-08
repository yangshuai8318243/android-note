package com.mengjia.baseLibrary.net.socket;

import android.os.Message;

import com.mengjia.baseLibrary.log.AppLog;
import com.mengjia.baseLibrary.thread.NewThreadHandler;
import com.mengjia.baseLibrary.utils.PrintUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class ReadSocket extends NewThreadHandler {
    private static final String TAG = "ReadScoket";
    private static final int STRAT_RECEIVE = 0x11;
    private boolean isReceive = false;
    private int readTimes;
    private static final int READ_TIMES_MAX = 20;
    //    Socket输入流
    private InputStream inputStream;
    private ChatSocketClient chatSocketClient;

    private ReadDataCallback readDataCallback;

    public ReadSocket(ChatSocketClient chatSocketClient) {
        super("ReadScoket_thread");
        this.chatSocketClient = chatSocketClient;
    }

    void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public void setReadDataCallback(ReadDataCallback readDataCallback) {
        this.readDataCallback = readDataCallback;
    }


    /**
     * 接收数据
     * created at 2020/8/21 10:20
     *
     * @author SnapeYang
     */
    private synchronized void receive() {
        if (inputStream != null && chatSocketClient != null && chatSocketClient.isConnected() && isReceive) {
            try {
                //得到的是16进制数，需要进行解析
                byte[] bt = new byte[1024 * 1024];
                int read = inputStream.read(bt);
//                AppLog.i(TAG, "receive--->", read, readTimes);
                if (read > 0) {
                    readTimes = 0;
                    if (readDataCallback != null) {
                        byte[] bytes = Arrays.copyOf(bt, read);
                        readDataCallback.processData(bytes);
                        AppLog.i(TAG, "接收成功", read, readTimes);
                    } else {
                        AppLog.i(TAG, "没有设置数据接收回调");
                    }
                } else {
                    //当数据为-1的情况下，其实scoket已经断开了，但是本地对象没有断开，这里主动断开连接,
                    //todo 这种判断也是不安全的，最安全的还是通过心跳包进行判断
//                    if (readTimes > READ_TIMES_MAX) {
//                        readTimes = 0;
//                        AppLog.i(TAG, "主动断开连接", readTimes);
//
//                    } else {
//                        readTimes++;
//                    }
                    try {
                        chatSocketClient.serviceScoketIsConnected();
                    } catch (Throwable throwable) {
                        if (chatSocketClient != null)
                            chatSocketClient.disconnect();
                    }
//                    AppLog.i(TAG, "接收失败", read, readTimes);
                }
                // 捕获解析或者其他情况出现的异常
            } catch (Throwable e) {
                String errInfo = PrintUtils.errInfo(e);
                AppLog.i(TAG, "接收失败", Thread.currentThread().getName());
//                AppLog.i(TAG, errInfo);
            } finally {
                sendEmptyMessage(STRAT_RECEIVE);
            }
        } else {
            AppLog.e(TAG, "这种情况标示scoket已经关闭了", chatSocketClient, isReceive);
        }
    }


    /**
     * 开始读取数据
     * created at 2020/8/21 16:43
     *
     * @author SnapeYang
     */
    public void receiveStart() {
        if (!isReceive) {
            this.isReceive = true;
            AppLog.e(TAG, "====receiveStart======>");
            sendEmptyMessage(STRAT_RECEIVE);
        }
    }

    @Override
    protected void working(Message message) {
        switch (message.what) {
            case STRAT_RECEIVE:
                receive();
                break;
        }
    }


    public interface ReadDataCallback {
        /**
         * 异步处理
         * created at 2020/8/21 10:20
         *
         * @param data scoket中的数据
         * @author SnapeYang
         */
        boolean processData(byte[] data);
    }

    public void close() {
        try {
            isReceive = false;
            clean();
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            inputStream = null;
            readDataCallback = null;
            chatSocketClient = null;
        }
    }
}
