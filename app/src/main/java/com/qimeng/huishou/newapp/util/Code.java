package com.qimeng.huishou.newapp.util;

import android.os.SystemClock;
import android.serialport.SerialPort;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.LinkedList;

public class Code {
    private String div;
    private SerialPort port;
    private InputStream input;
    private OutputStream output;

    private boolean isRead = true;
    private boolean isData = true;
    private DataListener listener;

    private LinkedList<byte[]> linkedList = new LinkedList<>();

    public interface DataListener {
        void onData(String string);
    }

    public Code(String div, DataListener listener) {
        this.div = div;
        this.listener = listener;
        openDiv();
    }

    private void openDiv() {
        try {
            port = new SerialPort(new File(div), 9600,0);
            input = port.getInputStream();
            output = port.getOutputStream();
            startRead();
            startData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startData() {
        new Thread(() -> {
            while (isData) {
                if (linkedList.size() > 0) {
                    byte [] data = linkedList.removeFirst();
                    String hexStr = new String(data);
                    if (listener != null) {
                        Log.e("Code", hexStr);
                        listener.onData(hexStr);
                    }
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void startRead() {
        new Thread(() -> {
            while (isRead) {
                byte[] received = new byte[1024];
                int size;
                while (isRead) {
                    if (Thread.currentThread().isInterrupted()) {
                        break;
                    }
                    try {

                        int available = input.available();

                        if (available > 0) {
                            size = input.read(received);
                            if (size > 0) {
                                onDataReceive(received, size);
                            }
                        } else {
                            // 暂停一点时间，免得一直循环造成CPU占用率过高
                            SystemClock.sleep(1);
                        }
                    } catch (IOException e) {
                        Log.i("Read", "读取数据失败" + e.getMessage());
                    }
                }
            }
        }).start();
    }

    private byte[] end = new byte[]{0x0D, 0x0A};
    private byte[] data = new byte[0];

    private void onDataReceive(byte[] received, int size) {
        byte[] newData = new byte[data.length + size];
        System.arraycopy(data, 0, newData, 0, data.length);
        System.arraycopy(received, 0, newData, data.length, size);
        data = newData;
        Log.e("Read232", Arrays.toString(data) + "   " + new String(data));
        int index = ByteUtil.findBytes(newData, end);
        if (index != -1) {
            byte[] code = new byte[index];
            System.arraycopy(data, 0, code, 0, index);
            linkedList.add(code);
            newData = new byte[data.length - (index + 2)];
            System.arraycopy(data, index + 2, newData, 0, newData.length);
            data = newData;
        }
    }
}