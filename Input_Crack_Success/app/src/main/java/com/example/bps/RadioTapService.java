package com.example.bps;

import java.util.Arrays;

public class RadioTapService {

    public RadioTapHeader parseRadioTapService(byte[] RadioTapBuffer) {
        RadioTapHeader radiotapHeader = new RadioTapHeader();

        //信号强度
        byte[] SignalStrengthBuffer = Arrays.copyOfRange(RadioTapBuffer, 22, 23);
        int signal=SignalStrengthBuffer[0]&0xff-256;
        radiotapHeader.setSignalStrength(signal);

        //RadioTap长度
        byte[] RadioTapLengthBuffer = Arrays.copyOfRange(RadioTapBuffer, 2, 4);
        DataUtils.reverseByteArray(RadioTapLengthBuffer);
        radiotapHeader.setRadioTapLength(DataUtils.byteArray2Int(RadioTapLengthBuffer,2));

        return radiotapHeader;


    }
}
