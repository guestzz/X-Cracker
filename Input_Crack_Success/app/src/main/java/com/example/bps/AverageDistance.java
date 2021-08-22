package com.example.bps;

public class AverageDistance {

    public static double AverageDistance(double[] xData, double[] yData,int size) {
        double max=0,average_distance=0;
        for (int i=0;i<size;i++)
        {
            double temp=Math.max(xData[i],yData[i]);
            if (temp>max){
                max=temp;
            }
        }
        double[] x_standard = new double[size];
        double[] y_standard = new double[size];
        for (int i=0;i<size;i++)
        {
            x_standard[i]=xData[i]/max;
            y_standard[i]=yData[i]/max;
            average_distance+=Math.abs(x_standard[i]-y_standard[i]);
        }
        return (double)average_distance/size;
    }
}
