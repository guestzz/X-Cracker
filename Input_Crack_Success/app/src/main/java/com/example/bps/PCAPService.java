package com.example.bps;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

import static com.example.bps.MainActivity.TxtAddr;

public class PCAPService {

    private FrameService frameService = new FrameService();
    private RadioTapService radioTapService =new RadioTapService();
    packet pkt;
    public int FirstPacketTime;
    public int PacketNumber=0;

    public void parsePcap(File pcapFile) throws IOException {
        String fileaddr = TxtAddr;
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileaddr));

        // pcap global header: 24 bytes
        byte[] globalHeaderBuffer = new byte[24];
        // pcap packet header: 16 bytes
        byte[] packetHeaderBuffer = new byte[16];
        byte[] packetDataBuffer;

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(pcapFile);
            if(fis.read(globalHeaderBuffer) != 24) {
                System.out.println("The Pcap file is broken!");
                return;
            }

            // 解析 Global Header
            GlobalHeader globalHeader = parseGlobalHeader(globalHeaderBuffer);
            if (globalHeader.getLinkType() != GlobalHeader.LINK_TYPE_80211) {
                System.out.println("Link type is not 802.11!");
                return;
            }
            int t=0;
            while (fis.read(packetHeaderBuffer) > 0) {
                t++;
                pkt = new packet();
                // 解析 Packet Header, 16位
                PacketHeader packetHeader = parsePacketHeader(packetHeaderBuffer);
                packetDataBuffer = new byte[packetHeader.getCapLen()];
                if (fis.read(packetDataBuffer) != packetHeader.getCapLen()) {
                    System.out.println("The Pcap file is broken!");
                    return;
                }
                // 解析Packet Data, Packet Data里面首先是Radiotap头
                parsePacketData(packetDataBuffer);
                if (PacketNumber == 1) {
                    FirstPacketTime = packetHeader.getTimeS();
                }

                pkt.setLength(packetHeader.getLen());
                pkt.setTime(packetHeader.getTimeS() - FirstPacketTime);

                //写入文件
                if (pkt.getMAC() != null) {
                    try {
                        oos.writeObject(pkt);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            oos.close();
            System.out.println("All PacketNumber:"+t);
            System.out.println("PacketNumber We Need :"+PacketNumber);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     *  解析Global Header
     */
    private GlobalHeader parseGlobalHeader(byte[] globalHeaderBuffer) {
        GlobalHeader globalHeader = new GlobalHeader();

        byte[] magicBuffer = Arrays.copyOfRange(globalHeaderBuffer, 0, 4);
        byte[] linkTypeBuffer = Arrays.copyOfRange(globalHeaderBuffer, 20, 24);

        int magic = DataUtils.byteArray2Int(magicBuffer, 4);

        DataUtils.reverseByteArray(linkTypeBuffer);
        int linkType = DataUtils.byteArray2Int(linkTypeBuffer, 4);
        globalHeader.setMagic(magic);
        globalHeader.setLinkType(linkType);

        return globalHeader;
    }

    /**
     *  解析Packet Header
     */
    private PacketHeader parsePacketHeader(byte[] dataHeaderBuffer){

        byte[] timeSBuffer = Arrays.copyOfRange(dataHeaderBuffer, 0, 4);
        byte[] timeMsBuffer = Arrays.copyOfRange(dataHeaderBuffer, 4, 8);
        byte[] capLenBuffer = Arrays.copyOfRange(dataHeaderBuffer, 8, 12);
        byte[] lenBuffer = Arrays.copyOfRange(dataHeaderBuffer, 12, 16);

        PacketHeader packetHeader = new PacketHeader();

        DataUtils.reverseByteArray(timeSBuffer);
        DataUtils.reverseByteArray(timeMsBuffer);
        DataUtils.reverseByteArray(capLenBuffer);
        DataUtils.reverseByteArray(lenBuffer);

        int timeS = DataUtils.byteArray2Int(timeSBuffer, 4);
        int timeMs = DataUtils.byteArray2Int(timeMsBuffer, 4);
        int capLen = DataUtils.byteArray2Int(capLenBuffer, 4);
        int len = DataUtils.byteArray2Int(lenBuffer, 4);

        packetHeader.setTimeS(timeS);
        packetHeader.setTimeMs(timeMs);
        packetHeader.setCapLen(capLen);
        packetHeader.setLen(len);
        return packetHeader;
    }

    /**
     *  解析PacketData
     */
    private void parsePacketData(byte[] packetDataBuffer) {
        //先处理Radiotap
        byte[] RadioTapBuffer = Arrays.copyOfRange(packetDataBuffer, 0, 24);
        RadioTapHeader radiotapHeader=radioTapService.parseRadioTapService(RadioTapBuffer);
        if (radiotapHeader.getRadioTapLength()>24)
        {
            return;
        }
        PacketNumber++;

        // 处理802.11的帧
        byte[] frameHeaderBuffer = Arrays.copyOfRange(packetDataBuffer, 24, packetDataBuffer.length);
        FrameHeader frameHeader = frameService.parseFrameHeader(frameHeaderBuffer);

        pkt.setMAC(frameHeader.getSourcemac());

    }

}
