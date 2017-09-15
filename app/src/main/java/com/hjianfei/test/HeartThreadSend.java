package com.hjianfei.test;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

/**
 * <pre>
 *     author : Administrator
 *     e-mail : 190766172@qq.com
 *     time   : 2017-09-14
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public class HeartThreadSend extends Thread {

    private DatagramSocket datagramSocket = null;
    private DatagramPacket datagramPacket = null;
    private byte[] buffer = new byte[]{1,2,3,4,5};

    public HeartThreadSend(DatagramSocket datagramSocket) {
        this.datagramSocket = datagramSocket;
        datagramPacket  = new DatagramPacket(buffer, buffer.length, new InetSocketAddress("192.168.1.60", 8989));
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                Thread.sleep(2000);
                System.out.println("发送心跳包");
                datagramSocket.send(datagramPacket);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
