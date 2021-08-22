package com.example.bps;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;

import static com.example.bps.MainActivity.TxtAddr;

//pcapanalyzer中计算导出了类文件，现在需要使用Hashmap存储每一个mac地址的每秒比特数
public class CalPacpBps {

    public HashMap<String,double[]> bps=new HashMap<>();

    private String TxtAddr=MainActivity.TxtAddr;

    private void TxtAddr(String TxtAddr){
        this.TxtAddr=TxtAddr;
    }

    public String getTxtAddr() {
        return TxtAddr;
    }

    public HashMap<String, double[]> getBps() {
        return bps;
    }

    private int pcap_record_time=0;

    public int getPcap_record_time(){
        return pcap_record_time;
    }

    private void setPcap_record_time(int pcap_record_time) {
        this.pcap_record_time = pcap_record_time;
    }

    //读取类文件
    public void readfile() throws IOException {
        ObjectInputStream ois=new ObjectInputStream(new FileInputStream(getTxtAddr()));
        while(true){
            try {
                packet pkt = (packet) ois.readObject();
                if (bps.containsKey(pkt.getMAC())){
                    if (pkt.getTime()<=59)
                    {
                        bps.get(pkt.getMAC())[pkt.getTime()]+= pkt.getLength();
                        if (pkt.getTime()>pcap_record_time){
                            setPcap_record_time(pkt.getTime());
                        }
                    }
                }
                else
                {
                    if (pkt.getTime()<=59)
                    {
                        double []temp=new double[60];
                        bps.put(pkt.getMAC(),temp);
                        bps.get(pkt.getMAC())[pkt.getTime()]+= pkt.getLength();
                        if (pkt.getTime()>pcap_record_time){
                            setPcap_record_time(pkt.getTime());
                        }
                    }
                }
            } catch (EOFException | ClassNotFoundException e) {
                break;
            }
        }
        ois.close();
    }

    //打印每秒传输比特数数组
    public void printbps()
    {
        for(String key: bps.keySet()){
            System.out.print(key+": ");
            for (int i=0;i<60;i++){
                System.out.print(bps.get(key)[i]+" ");
            }
            System.out.println("");
        }
    }
}
