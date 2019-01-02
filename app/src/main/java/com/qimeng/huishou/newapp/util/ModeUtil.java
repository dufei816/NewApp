package com.qimeng.huishou.newapp.util;

import android.util.Log;

import com.zgkxzx.modbus4And.requset.OnRequestBack;

public class ModeUtil {

    private static final long SLEEP = 300;

    private static ModeUtil myModeUtil;
    private ModBusUtil modBusUtil;
    private static final int ID = 1;
    private int weight;

    private ModeUtil() {
        modBusUtil = new ModBusUtil(Config.MODE_BUS_IP);
    }

    public static ModeUtil getInstance() {
        synchronized (ModeUtil.class) {
            if (myModeUtil == null) {
                myModeUtil = new ModeUtil();
            }
            return myModeUtil;
        }
    }

    public void openMen() {
        writeVW(670, 1, null);
    }


    public void openPingMode(Listener<Integer> listener) {
        //查询是否是开门状态
        checkPingModel(new Listener<Short>() {
            @Override
            public void onSuccess(Short data) {
                if (data == 0) {//状态为0时可使用
                    writeVW(660, 1, new Listener<Integer>() {
                        @Override
                        public void onSuccess(Integer data) {
                            //检测瓶口是否关闭
                            checkPingClose(listener);
                        }

                        @Override
                        public void onError(String msg) {
                            listener.onError(msg);
                        }
                    });
                } else {
                    listener.onError("瓶口正在使用");
                }
            }

            @Override
            public void onError(String msg) {
                listener.onError(msg);
            }
        });
    }

    /**
     * 检测是否关闭
     *
     * @param listener
     */
    private void checkPingClose(Listener<Integer> listener) {
        checkPingModel(new Listener<Short>() {
            @Override
            public void onSuccess(Short data) {
                if (data == 4) {
                    getPingCount(new Listener<Integer>() {
                        @Override
                        public void onSuccess(Integer data) {
                            listener.onSuccess(data);
                        }

                        @Override
                        public void onError(String msg) {
                            listener.onError(msg);
                        }
                    });
                } else {
                    try {
                        Thread.sleep(SLEEP);
                        checkPingClose(listener);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError(String msg) {
                listener.onError(msg);
            }
        });
    }

    /**
     * 检测瓶口状态
     *
     * @param listener
     */
    private void checkPingModel(Listener<Short> listener) {
        modBusUtil.readHoldingRegisters(new ModBusUtil.DataSuccess<short[]>() {
            @Override
            public void onSuccess(short[] shortData) {
                listener.onSuccess(shortData[0]);
            }

            @Override
            public void onError(String msg) {
                listener.onError(msg);
            }
        }, ID, 30, 1);
    }


    /**
     * 获取瓶数量
     *
     * @param listener
     */
    private void getPingCount(Listener<Integer> listener) {
        modBusUtil.readHoldingRegisters(new ModBusUtil.DataSuccess<short[]>() {
            @Override
            public void onSuccess(short[] shorts) {
                listener.onSuccess((int) shorts[0]);
            }

            @Override
            public void onError(String msg) {
                listener.onError(msg);
            }

        }, ID, 203, 1);
    }


    public void openZhiMode(Listener<Integer> listener) {
        checkZhiModel(new Listener<Short>() {
            @Override
            public void onSuccess(Short data) {
                if (data == 0) {
                    getZhiWeight(new Listener<Integer>() {
                        @Override
                        public void onSuccess(Integer data) {
                            weight = data;
                            openZhi(listener);
                        }

                        @Override
                        public void onError(String msg) {
                            listener.onError(msg);
                        }
                    });
                } else {
                    listener.onError("纸口机器正在运作");
                }
            }

            @Override
            public void onError(String msg) {
                listener.onError(msg);
            }
        });
    }

    /**
     * 获取纸业务当前运行状态
     *
     * @param listener
     */
    private void checkZhiModel(Listener<Short> listener) {
        modBusUtil.readHoldingRegisters(new ModBusUtil.DataSuccess<short[]>() {
            @Override
            public void onSuccess(short[] shorts) {
                listener.onSuccess(shorts[0]);
            }

            @Override
            public void onError(String msg) {
                listener.onError("获取纸当前运行状态异常");
            }
        }, ID, 20, 1);
    }


    /**
     * 获取纸重量
     *
     * @param listener
     */
    private void getZhiWeight(Listener<Integer> listener) {
        modBusUtil.readHoldingRegisters(new ModBusUtil.DataSuccess<short[]>() {
            @Override
            public void onSuccess(short[] shorts) {
                listener.onSuccess((int) shorts[0]);
            }

            @Override
            public void onError(String msg) {
                listener.onError("获取纸重量异常");
            }
        }, ID, 106, 1);
    }


    /**
     * 打开纸口
     *
     * @param listener
     */
    private void openZhi(Listener<Integer> listener) {
        writeVW(650, 1, new Listener<Integer>() {
            @Override
            public void onSuccess(Integer data) {
                checkWeight(listener);
            }

            @Override
            public void onError(String msg) {
                listener.onError(msg);
            }
        });
    }


    /**
     * 检测是否称重完毕
     *
     * @param listener
     */
    private void checkWeight(Listener<Integer> listener) {
        checkZhiModel(new Listener<Short>() {
            @Override
            public void onSuccess(Short data) {
                if (data == 2) {
                    getZhiWeight(new Listener<Integer>() {
                        @Override
                        public void onSuccess(Integer data) {
                            listener.onSuccess(data - weight);
                        }

                        @Override
                        public void onError(String msg) {
                            listener.onError(msg);
                        }
                    });
                } else {
                    try {
                        Thread.sleep(SLEEP);
                        checkWeight(listener);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError(String msg) {
                listener.onError(msg);
            }
        });
    }


    /**
     * 输出VW数据
     *
     * @param vw
     * @param value
     * @param listener
     */
    private void writeVW(int vw, int value, Listener<Integer> listener) {
        int index = vw / 2;
        modBusUtil.writeRegister(new ModBusUtil.DataSuccess<String>() {
            @Override
            public void onSuccess(String shortData) {
                Log.e("ModBus", "输出VW=" + vw + "   Value=" + value);
                if (listener != null) {
                    listener.onSuccess(vw);
                }
            }

            @Override
            public void onError(String msg) {
                if (listener != null) {
                    listener.onError(msg);
                }
            }
        }, 1, index, value);
    }


    public interface Listener<T> {

        void onSuccess(T data);

        void onError(String msg);

    }

}
