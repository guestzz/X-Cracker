package com.example.bps;

import java.io.Serializable;

//packet对象，只关心我们需要的内容，并用以存入文件
public class packet implements java.io.Serializable {

    private static final long serialVersionUID = 1L;
    private String MAC;
    private int time;
    private int length;

    public void setLength(int length) {
        this.length = length;
    }

    public int getLength() {
        return length;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public void setMAC(String MAC) {
        this.MAC = MAC;
    }

    public String getMAC() {
        return MAC;
    }

    @Override
    public String toString() {
        return "Packet [time=" + time + ", Mac=" + MAC + ", Length=" + length+ "]";
    }
}
