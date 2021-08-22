package com.example.bps;

/**
 * 帧头（Ethernet Header）
 */

public class FrameHeader {

    private int protocol;

    private String sourcemac;

    public void setsourcemac(String sourcemac) {this.sourcemac=sourcemac;}

    public String getSourcemac(){return sourcemac;};

    public FrameHeader() {}
}
