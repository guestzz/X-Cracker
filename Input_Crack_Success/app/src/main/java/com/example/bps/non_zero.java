package com.example.bps;

public class non_zero{

    public static double non_zero(double[] x,int size) {
        double non_zero_num=0;
        for(int t=0;t<size;t++){
            if (x[t]!=0){
                non_zero_num++;
            }
        }
        return non_zero_num/(double)size;
    }
}
