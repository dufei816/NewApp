package com.qimeng.huishou.newapp.util;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Log;

import com.zgkxzx.modbus4And.ModbusFactory;
import com.zgkxzx.modbus4And.exception.ModbusInitException;
import com.zgkxzx.modbus4And.ip.IpParameters;
import com.zgkxzx.modbus4And.ip.tcp.TcpMaster;
import com.zgkxzx.modbus4And.msg.ReadCoilsRequest;
import com.zgkxzx.modbus4And.msg.ReadCoilsResponse;
import com.zgkxzx.modbus4And.msg.ReadDiscreteInputsRequest;
import com.zgkxzx.modbus4And.msg.ReadDiscreteInputsResponse;
import com.zgkxzx.modbus4And.msg.ReadHoldingRegistersRequest;
import com.zgkxzx.modbus4And.msg.ReadHoldingRegistersResponse;
import com.zgkxzx.modbus4And.msg.WriteRegisterRequest;
import com.zgkxzx.modbus4And.msg.WriteRegisterResponse;
import com.zgkxzx.modbus4And.msg.WriteRegistersRequest;
import com.zgkxzx.modbus4And.msg.WriteRegistersResponse;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

@SuppressLint("CheckResult")
public class ModBusUtil {

    private TcpMaster masterIn;
    private TcpMaster masterOut;
    private boolean init = false;
    private String ip;


    public ModBusUtil(String ip) {
        if (TextUtils.isEmpty(ip)) {
            return;
        }
        this.ip = ip;
        init();
    }

    private void init() {
        IpParameters prams = new IpParameters();
        prams.setHost(ip);
        prams.setPort(502);
        prams.setEncapsulated(false);
        masterIn = (TcpMaster) new ModbusFactory().createTcpMaster(prams, true);
        masterIn.setTimeout(2000);
        masterIn.setRetries(0);
        masterOut = (TcpMaster) new ModbusFactory().createTcpMaster(prams, true);
        masterOut.setTimeout(2000);
        masterOut.setRetries(0);
        new Thread(() -> {
            try {
                masterIn.init();
                masterOut.init();
                init = true;
            } catch (ModbusInitException e) {
                e.printStackTrace();
                init = false;
            }
        }).start();
    }

    public void readDiscreteInput(DataSuccess<boolean[]> success, int slaveId, int start, int len) {
        Observable.just(1).observeOn(Schedulers.newThread())
                .subscribe(l -> {
                    ReadDiscreteInputsRequest request = new ReadDiscreteInputsRequest(slaveId, start, len);
                    ReadDiscreteInputsResponse response = (ReadDiscreteInputsResponse) masterIn.send(request);
                    if (response.isException()) {
                        Log.e("ModBus", response.getExceptionMessage());
                        success.onError(response.getExceptionMessage());
                    } else {
                        boolean[] booleanData = response.getBooleanData();
                        boolean[] resultByte = new boolean[len];
                        System.arraycopy(booleanData, 0, resultByte, 0, len);
                        success.onSuccess(resultByte);
                    }
                }, error -> {
                    success.onError(error.getMessage());
                    error.printStackTrace();
                });
    }



    public void readHoldingRegisters(DataSuccess<short[]> success, int slaveId, int start, int len) {
        Observable.just(1).observeOn(Schedulers.newThread())
                .subscribe(l -> {
                    ReadHoldingRegistersRequest request = new ReadHoldingRegistersRequest(slaveId, start, len);
                    ReadHoldingRegistersResponse response = (ReadHoldingRegistersResponse) masterIn.send(request);
                    if (response.isException()) {
                        Log.e("ModBus", response.getExceptionMessage());
                        success.onError(response.getExceptionMessage());
                    } else {
                        success.onSuccess(response.getShortData());
                    }
                }, error -> {
                    error.printStackTrace();
                    success.onError(error.getMessage());
                });
    }


    public void writeRegisters(DataSuccess<String> success, int slaveId, int start, short[] values) {
        Observable.just(1).observeOn(Schedulers.newThread())
                .subscribe(l -> {
                    WriteRegistersRequest request = new WriteRegistersRequest(slaveId, start, values);
                    WriteRegistersResponse response = (WriteRegistersResponse) masterOut.send(request);
                    if (response.isException()) {
                        Log.e("ModBus", response.getExceptionMessage());
                        success.onError(response.getExceptionMessage());
                    } else {
                        success.onSuccess("Success");
                    }
                }, error -> {
                    success.onError(error.getMessage());
                    error.printStackTrace();
                });
    }


    public void readCoil(DataSuccess<boolean[]> success, int slaveId, int start, int len) {
        Observable.just(1).observeOn(Schedulers.newThread())
                .subscribe(l -> {
                    ReadCoilsRequest request = new ReadCoilsRequest(slaveId, start, len);
                    ReadCoilsResponse response = (ReadCoilsResponse) masterIn.send(request);
                    if (response.isException()) {
                        Log.e("ModBus", response.getExceptionMessage());
                        success.onError(response.getExceptionMessage());
                    } else {
                        boolean[] booleanData = response.getBooleanData();
                        boolean[] resultByte = new boolean[len];
                        System.arraycopy(booleanData, 0, resultByte, 0, len);
                        success.onSuccess(resultByte);
                    }
                }, error -> {
                    success.onError(error.getMessage());
                    error.printStackTrace();
                });
    }


    public void writeRegister(DataSuccess<String> success, int slaveId, int offset, int value) {
        Observable.just(1).observeOn(Schedulers.newThread())
                .subscribe(l -> {
                    WriteRegisterRequest request = new WriteRegisterRequest(slaveId, offset, value);
                    WriteRegisterResponse response = (WriteRegisterResponse) masterOut.send(request);
                    if (response.isException()) {
                        Log.e("ModBus", response.getExceptionMessage());
                        success.onError(response.getExceptionMessage());
                    } else {
                        success.onSuccess("Success");
                    }
                }, error -> {
                    success.onError(error.getMessage());
                    error.printStackTrace();
                });
    }


    public interface DataSuccess<T> {

        void onSuccess(T shortData);

        void onError(String msg);

    }

}