package com.mengjia.baseLibrary.net.socket;


import android.os.Message;

import com.mengjia.baseLibrary.log.AppLog;
import com.mengjia.baseLibrary.thread.NewThreadHandler;
import com.mengjia.baseLibrary.utils.TimeUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;

public class ChatSocketClient extends NewThreadHandler {

    private static final String TAG = "ChatScoket";
    private static final int STRAT_CONNECT = 0x01;

    //    Socket
    private Socket socket;
    //    IP地址
    private String s_ipAddress;
    //    端口号
    private int s_port;
    //    线程
    private Thread thread;

    private ReadSocket readSocket;
    private WriteSocket writeSocket;
    private boolean isConnect = false;


    @Override
    protected void working(Message message) {
        switch (message.what) {
            case STRAT_CONNECT:
                connect();
                break;
        }
    }


    public ChatSocketClient(String ipAddress, int port) {
        super("ChatScoketClient_thread");
        this.readSocket = new ReadSocket(this);
        this.writeSocket = new WriteSocket(this);
        s_ipAddress = ipAddress;
        s_port = port;
    }


    public String getS_ipAddress() {
        return s_ipAddress;
    }

    public int getS_port() {
        return s_port;
    }

    public synchronized ReadSocket getReadSocket() {
        return readSocket;
    }

    public synchronized WriteSocket getWriteSocket() {
        return writeSocket;
    }


    public synchronized void connectStart() {
        sendEmptyMessage(STRAT_CONNECT);
    }

    /**
     * 初始化连接设置
     */
    public synchronized void initSocket() {
        try {
            socket = new Socket();
            // 发送数据包，默认为 false，即客户端发送数据采用 Nagle 算法；
            // 但是对于实时交互性高的程序，建议其改为 true，即关闭 Nagle 算法，客户端每发送一次数据，无论数据包大小都会将这些数据发送出去
            socket.setTcpNoDelay(true);
            // 设置输出流的发送缓冲区大小，默认是4KB，即4096字节
            socket.setSendBufferSize(4096);
            // 设置输入流的接收缓冲区大小，默认是4KB，即4096字节
            socket.setReceiveBufferSize(4096);
            // 作用：每隔一段时间检查服务器是否处于活动状态，如果服务器端长时间没响应，自动关闭客户端socket
            // 防止服务器端无效时，客户端长时间处于连接状态
            socket.setKeepAlive(true);
            //代表可以立即向服务器端发送单字节数据
            socket.setOOBInline(true);
            socket.setSoTimeout(TimeUtil.TimeConstants.SEC * 10);//设置超时时间
        } catch (SocketException e) {
            e.printStackTrace();
            AppLog.e(TAG, "连接异常");
            if (disconnectedCallback != null) {
                disconnectedCallback.callback(e);
            }
        }
    }

    /**
     * 通过IP地址(域名)和端口进行连接
     */
    private synchronized void connect() {
        try {
            if (!isConnected()) {
                SocketAddress remoteAddr = new InetSocketAddress(s_ipAddress, s_port);
                socket.connect(remoteAddr, 10000); //等待建立连接的超时时间为5秒

                OutputStream outputStream = socket.getOutputStream();
                writeSocket.setOutputStream(outputStream);
                InputStream inputStream = socket.getInputStream();
                readSocket.setInputStream(inputStream);
                isConnect = true;
                AppLog.i(TAG, "连接成功");

                if (connectedCallback != null) {
                    connectedCallback.callback();
                }

            } else {
                AppLog.i(TAG, "当前scoket已经连接");
            }
        } catch (IOException e) {
            e.printStackTrace();
            AppLog.e(TAG, "连接异常");
            if (disconnectedCallback != null) {
                disconnectedCallback.callback(e);
            }
        }
    }

    void serviceScoketIsConnected() throws IOException {
        socket.sendUrgentData(0xFF);
    }

    /**
     * 判断是否连接
     */
    public synchronized boolean isConnected() {
        if (socket == null) return false;
        return isConnect;
    }

    /**
     * 连接回调
     * 非主线程回调
     */
    private OnServerConnectedCallbackBlock connectedCallback;
    /**
     * 断开连接回调(连接失败)
     * 非主线程回调
     */
    private OnServerDisconnectedCallbackBlock disconnectedCallback;


    /**
     * 回调声明
     */
    public interface OnServerConnectedCallbackBlock {
        void callback();
    }

    public interface OnServerDisconnectedCallbackBlock {
        void callback(IOException e);
    }

    public interface OnReceiveCallbackBlock {
        void callback(String receicedMessage);
    }

    public synchronized void setConnectedCallback(OnServerConnectedCallbackBlock connectedCallback) {
        this.connectedCallback = connectedCallback;
    }

    public synchronized void setDisconnectedCallback(OnServerDisconnectedCallbackBlock disconnectedCallback) {
        this.disconnectedCallback = disconnectedCallback;
    }


    /**
     * 移除回调
     */
    private synchronized void removeCallback() {
        connectedCallback = null;
        disconnectedCallback = null;
    }

    /**
     * 断开连接
     */
    public synchronized void disconnect() {
        if (isConnected()) {
            try {
                clean();
                if (readSocket != null) {
                    readSocket.close();
                    readSocket = null;
                }
                if (writeSocket != null) {
                    writeSocket.close();
                    writeSocket = null;
                }


                isConnect = false;
                if (socket != null) {
                    if (socket.isClosed()) {
                        if (disconnectedCallback != null) {
                            disconnectedCallback.callback(new IOException("断开连接"));
                        }
                    }

                    socket.close();
                    socket = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
