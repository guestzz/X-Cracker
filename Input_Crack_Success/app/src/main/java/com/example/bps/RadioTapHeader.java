package com.example.bps;

public class RadioTapHeader {
    private int SignalStrength;
    private int RadioTapLength;

    public int getSignalStrength(){
        return SignalStrength;
    }

    public void setSignalStrength(int SignalStrength)
    {
        this.SignalStrength=SignalStrength;
    }

    public int getRadioTapLength(){
        return RadioTapLength;
    }

    public void setRadioTapLength(int RadioTapLength)
    {
        this.RadioTapLength=RadioTapLength;
    }

    public RadioTapHeader() {}
}
