package com.example.bps;

//皮尔逊相关系数
public class Correlation_Coefficient {

    public static double getPearsonCorrelationScore(double[] xData, double[] yData,int size) {
        double []x=new double[size];
        double []y=new double[size];
        for (int i=0;i<size;i++){
            x[i]=xData[i];
            y[i]=yData[i];
        }

        double xMeans;
        double yMeans;
        double numerator = 0;// 求解皮尔逊的分子
        double denominator = 0;// 求解皮尔逊系数的分母

        double result = 0;
        // 拿到两个数据的平均值
        xMeans = getMeans(x);
        yMeans = getMeans(y);
        // 计算皮尔逊系数的分子
        numerator = generateNumerator(x, xMeans, y, yMeans);
        // 计算皮尔逊系数的分母
        denominator = generateDenomiator(x, xMeans, y, yMeans);
        // 计算皮尔逊系数
        result = numerator / denominator;
        return result;
    }

    //计算分子
    private static double generateNumerator(double[] xData, double xMeans, double[] yData, double yMeans) {
        double numerator = 0.0;
        for (int i = 0; i < xData.length; i++) {
            numerator += (xData[i] - xMeans) * (yData[i] - yMeans);
        }
        return numerator;
    }

    //计算分母
    private static double generateDenomiator(double[] xData, double xMeans, double[] yData, double yMeans) {
        double xSum = 0.0;
        for (int i = 0; i < xData.length; i++) {
            xSum += (xData[i] - xMeans) * (xData[i] - xMeans);
        }
        double ySum = 0.0;
        for (int i = 0; i < yData.length; i++) {
            ySum += (yData[i] - yMeans) * (yData[i] - yMeans);
        }
        return Math.sqrt(xSum) * Math.sqrt(ySum);
    }

    //返回平均值
    private static double getMeans(double[] datas) {
        double sum = 0.0;
        for (int i = 0; i < datas.length; i++) {
            sum += datas[i];
        }
        return sum / datas.length;
    }
}
