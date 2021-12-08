package com.mengjia.baseLibrary.net.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class SocketChannelClient {
    private SocketChannel socketChannel;

    public void init() {
        try {
            socketChannel = SocketChannel.open();
            socketChannel.connect(new InetSocketAddress("0000", 80));
            socketChannel.configureBlocking(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        if (socketChannel == null) return false;
        if (socketChannel.isConnectionPending()) return true;
        return socketChannel.isOpen() && socketChannel.isConnected();
    }

}
