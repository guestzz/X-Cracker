package com.example.bps;

import com.example.bps.FrameHeader;

import java.util.Arrays;

public class FrameService {

    public FrameHeader parseFrameHeader(byte[] frameHeaderBuffer) {
        FrameHeader frameHeader = new FrameHeader();

        byte[] MacSourceBuffer = Arrays.copyOfRange(frameHeaderBuffer, 10, 16);
        String []mac=new String[6];
        String sourcemac="";
        for (int i=0;i<6;i++)
        {
            if ((MacSourceBuffer[i] & 0xFF)<0x0F)
            {
                mac[i]="0"+Integer.toHexString(MacSourceBuffer[i] & 0xFF);
            }else{
                mac[i]=Integer.toHexString(MacSourceBuffer[i] & 0xFF);
            }
            if (i==5){
                sourcemac+=mac[i];
            }else{
                sourcemac+=mac[i]+":";
            }
        }
        frameHeader.setsourcemac(sourcemac);

        return frameHeader;
    }

}
