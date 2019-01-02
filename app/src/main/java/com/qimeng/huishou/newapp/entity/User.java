package com.qimeng.huishou.newapp.entity;

public class User {


    /**
     * msg : 1
     * xm : 林玉叶
     * success : true
     * jf : 1
     */

    private int count;
    private int weight;

    private String code;
    private int msg;
    private String xm;
    private boolean success;
    private int jf;


    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getMsg() {
        return msg;
    }

    public void setMsg(int msg) {
        this.msg = msg;
    }

    public String getXm() {
        return xm;
    }

    public void setXm(String xm) {
        this.xm = xm;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getJf() {
        return jf;
    }

    public void setJf(int jf) {
        this.jf = jf;
    }
}
