package com.hjianfei.test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Arrays;

/**
 * <pre>
 *     author : Administrator
 *     e-mail : 190766172@qq.com
 *     time   : 2017-09-14
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public class HeartThreadReceive extends Thread {


    private DatagramSocket datagramSocket = null;
    private DatagramPacket pack = null;
    private byte[] buff = new byte[5];

    public HeartThreadReceive(DatagramSocket datagramSocket) throws SocketException {
        this.datagramSocket = datagramSocket;
        pack = new DatagramPacket(buff, buff.length);
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                datagramSocket.receive(pack);
                byte[] res = Arrays.copyOf(buff, pack.getLength());
                System.out.println("收到心跳包：" + Arrays.toString(res));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
