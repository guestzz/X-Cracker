package com.wifi.xcracker.util;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicLong;

public class CounterOutputStream extends OutputStream {

    private AtomicLong mAtomicLong = new AtomicLong();

    /**
     * 拿到总长度
     * @return
     */
    public long get(){
       return mAtomicLong.get();
    }

    public void write(long b) throws IOException{
        mAtomicLong.addAndGet(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        mAtomicLong.addAndGet(b.length);

    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        mAtomicLong.addAndGet(len);

    }

    @Override
    public void flush() throws IOException {

    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public void write(int b) throws IOException {
        mAtomicLong.addAndGet(1);
    }
}
