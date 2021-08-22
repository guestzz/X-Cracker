package com.example.bps;

import com.example.bps.PCAPService;
import java.io.File;
import java.io.IOException;
import static com.example.bps.MainActivity.PcapAddr;

public class FirstTest {

    public static void parse() throws IOException {
        PCAPService pcapService = new PCAPService();
        File pcapFile = new File(PcapAddr);
        pcapService.parsePcap(pcapFile);
    }
}
