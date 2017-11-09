package com.example.bluetooth.le.model;

/**
 * Created by zhdk on 2017/8/9.
 */

public class DataInfo {
    private byte[] data;
    private int dataType;

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public int getDataType() {
        return dataType;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    public static class DataType{
        public static int SEND=0x01;
        public static int RECEIVE=0x02;
    }
}
