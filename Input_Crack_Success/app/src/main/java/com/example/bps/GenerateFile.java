package com.example.bps;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;

//将生成的特征矩阵保存为文件
public class GenerateFile {
    public static void generate_file(HashMap<String,double[]> Similarity) throws IOException {
        File file = new File("/sdcard/temp/Data.txt");
        //路径不存在，新建文件
        if( !file.exists() && !file.isDirectory()) {
            file.createNewFile();
        }
        Writer out = new FileWriter(file);
        BufferedWriter bufferedWriter = new BufferedWriter(out);
        for(String key: Similarity.keySet()) {
            String str1 = String.valueOf(key);
            String str2 = String.valueOf(Similarity.get(key)[0]);
            String str3 = String.valueOf(Similarity.get(key)[1]);
            String str4=String.valueOf(Similarity.get(key)[2]);
            bufferedWriter.write(str1);
            bufferedWriter.write(" ");
            bufferedWriter.write(str2);
            bufferedWriter.write(" ");
            bufferedWriter.write(str3);
            bufferedWriter.write(" ");
            bufferedWriter.write(str4);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        }
    }
}
