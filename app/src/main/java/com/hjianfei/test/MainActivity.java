package com.hjianfei.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn_connect, btn_send;
    private EditText ed_text;
    private Socket socket = null;
    private OutputStream os;
    private InputStream is;
    private DatagramSocket datagramSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_connect = (Button) findViewById(R.id.btn_connect);
        btn_send = (Button) findViewById(R.id.btn_send);
        ed_text = (EditText) findViewById(R.id.text);
        btn_send.setOnClickListener(this);
        btn_connect.setOnClickListener(this);
        try {
            datagramSocket = new DatagramSocket(8989);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        new HeartThreadSend(datagramSocket).start();
        try {
            new HeartThreadReceive(datagramSocket).start();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_send:
                if (socket != null && socket.isConnected()) {
                    Log.d("onResponse", "未断开");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                byte[] buffer = new byte[6];
                                os = socket.getOutputStream();
                                is = socket.getInputStream();
                                String string = ed_text.getText().toString();
                                List<byte[]> sendData = ProtocolUtils.getSendData(031101, false, string);
                                for (byte[] bs : sendData) {
                                    os.write(bs);
                                    is.read(buffer, 0, buffer.length);
                                    Log.d("onResponse", Arrays.toString(buffer));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();

                } else {
                    Log.d("onResponse", "断开连接");
                }
                break;
            case R.id.btn_connect:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            socket = new Socket("192.168.1.60", 9090);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;
        }
    }
}
