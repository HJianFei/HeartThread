package com.hjianfei.test;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.Arrays;

/**
 * <pre>
 *     author : Administrator
 *     e-mail : 190766172@qq.com
 *     time   : 2017-09-15
 *     desc   : 心跳监听线程 每隔一段时间向服务器端发送一个数据包
 *     version: 1.0
 * </pre>
 */

public class HeartThread {

    private String ip_address;
    private int port;
    /**
     * 线程是否运行
     */
    private boolean running = false;
    /**
     * 心跳间隔时间
     */
    private long receiveTimeDelay = 3000;
    /**
     * 最后一次发送数据的时间
     */

    private DatagramSocket datagramSocket = null;
    private DatagramPacket datagramPacket = null;
    private byte[] buffer = new byte[]{1, 2, 3, 4, 5};

    public HeartThread(String ip_address, int port) {
        this.ip_address = ip_address;
        this.port = port;
    }

    public void start() throws Exception {
        if (running) {
            return;
        }
        datagramSocket = new DatagramSocket(port);
        datagramSocket.setSoTimeout(3000);//设置阻塞线程的等待超时时间
        datagramPacket = new DatagramPacket(buffer, buffer.length, new InetSocketAddress(ip_address, port));
        running = true;
        /**
         * 保持长连接的线程，每隔2秒项服务器发一个一个保持连接的心跳消息
         */
        new Thread(new KeepAliveWatchDog()).start();
        /**
         * 接受消息的线程，处理消息
         */
        new Thread(new ReceiveWatchDog()).start();
    }

    /**
     * 保持长连接的线程，每隔2秒项服务器发一个一个保持连接的心跳消息
     */
    private class KeepAliveWatchDog implements Runnable {

        long lastSendTime = System.currentTimeMillis();//最后一次发送的时间
        long checkDelay = 10;
        long keepAliveDelay = 3000;//发送时间间隔

        @Override
        public void run() {
            while (running) {
                if (System.currentTimeMillis() - lastSendTime > keepAliveDelay) {//3秒钟发送一次心跳包
                    try {
                        //向服务器发送心跳包
                        datagramSocket.send(datagramPacket);
                        //修改最后一次发送时间
                        lastSendTime = System.currentTimeMillis();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        //休眠10毫秒
                        Thread.sleep(checkDelay);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }

    /**
     * 接受消息的线程，处理消息
     */
    private class ReceiveWatchDog implements Runnable {

        boolean run = true;
        long lastReceiveTime = System.currentTimeMillis();
        private DatagramPacket pack = null;
        private byte[] buff = new byte[5];

        @Override
        public void run() {
            while (running && run) {
                if (System.currentTimeMillis() - lastReceiveTime > receiveTimeDelay) {//超时没有收到回复,断开重连
                    overThis();
                } else {
                    try {
                        pack = new DatagramPacket(buff, buff.length);
                        datagramSocket.receive(pack);
                        byte[] res = Arrays.copyOf(buff, pack.getLength());
                        System.out.println("收到心跳包：" + Arrays.toString(res));
                        lastReceiveTime = System.currentTimeMillis();
                    } catch (Exception e) {
                        e.printStackTrace();
                        overThis();
                    }
                }
            }


        }
    }

    private void overThis() {
        running = false;
        Log.d("onResponse", "接收超时");

    }
}
