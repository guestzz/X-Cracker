package com.example.bps;

import android.media.MediaExtractor;
import android.media.MediaFormat;

import org.opencv.video.Video;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import static com.example.bps.MainActivity.VideoAddr;

public class Videobps {
    private FileSizeUtil fileSizeUtil=new FileSizeUtil();//实例化计算文件大小对象
    private int frame_rate;//视频帧率
    private int duration;//视频时长
    private double[] bps=new double[60];//录制视频的每秒字节数
    public int getDuration() {
        return duration;
    }


    public double[] getBps() {
        return bps;
    }

    public void displaybps()
    {
        for (int i=0;i<duration;i++)
        {
            System.out.println(" "+bps[i]);
        }
    }

    public void CalVideoBps(String AppAddress){
        //MediaExtractor对象获取多媒体源
        MediaExtractor mVideoExtractor = new MediaExtractor();
        try {
            System.out.println(VideoAddr);
            mVideoExtractor.setDataSource(VideoAddr);
            System.out.println("Access Success!");
        } catch (IOException e) {
            e.printStackTrace();
        }

        //获取视频轨道位置videoTrackIndex与视频格式MediaFormat
        int count = mVideoExtractor.getTrackCount();//获取轨道数量
        System.out.println("轨道数量 = "+count);
        int videoTrackIndex=-1;
        MediaFormat videoMediaFormat = null;//视频格式
        for (int i = 0; i < count; i++)
        {
            MediaFormat itemMediaFormat = mVideoExtractor.getTrackFormat(i);
            String itemMime = itemMediaFormat.getString(MediaFormat.KEY_MIME);
            if (itemMime.startsWith("video")) { //获取视频轨道位置
                videoTrackIndex = i;
                videoMediaFormat = itemMediaFormat;
                continue;
            }
        }

        //输出视频相关信息
        System.out.println("帧率"+videoMediaFormat.getInteger(MediaFormat.KEY_FRAME_RATE));
        frame_rate=videoMediaFormat.getInteger(MediaFormat.KEY_FRAME_RATE);
        System.out.println("宽度"+videoMediaFormat.getInteger(MediaFormat.KEY_WIDTH));
        System.out.println("高度"+videoMediaFormat.getInteger(MediaFormat.KEY_HEIGHT));
        System.out.println("播放时长"+videoMediaFormat.getLong(MediaFormat.KEY_DURATION)/1000000+"s");
        duration= (int) Math.floor((double)(videoMediaFormat.getLong(MediaFormat.KEY_DURATION)/1000000));
        System.out.println("视频大小"+fileSizeUtil.getFileOrFilesSize(VideoAddr,3)+"MB");


        //在运行程序的存储空间中生成临时文件
        for (int i=0;i<duration;i++)
        {
            File videoFile = new File(AppAddress, "video.h264");

            if (videoFile.exists()) {
                videoFile.delete();
            }

            try {
                //新建h.264编码文件
                FileOutputStream videoOutputStream = new FileOutputStream(AppAddress+"/video.h264");

                //分离视频
                int maxVideoBufferCount = videoMediaFormat.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE);//获取视频的输出缓存的最大大小
                ByteBuffer videoByteBuffer = ByteBuffer.allocate(maxVideoBufferCount);
                mVideoExtractor.selectTrack(videoTrackIndex);//选择到视频轨道
                int len = 0;
                int startindex=0;
                for (int j=0;j<frame_rate*i;j++)
                {
                    mVideoExtractor.advance();
                }
                while ((len = mVideoExtractor.readSampleData(videoByteBuffer, 0)) != -1)
                {
                    byte[] bytes = new byte[len];
                    videoByteBuffer.get(bytes);//获取字节
                    videoOutputStream.write(bytes);//写入字节
                    videoByteBuffer.clear();
                    mVideoExtractor.advance();//预先加载后面的数据
                    startindex++;
                    if (startindex==videoMediaFormat.getInteger(MediaFormat.KEY_FRAME_RATE))
                    {
                        break;
                    }
                }
                videoOutputStream.flush();
                videoOutputStream.close();

            } catch (FileNotFoundException e) {
                System.out.println("separate: 错误原因=" + e);
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (videoFile.exists())
            {
                System.out.println("视频保存成功！");
            }

            System.out.println("第"+i+"秒"+"生成视频大小"+(double)videoFile.length()/(1024*1024)+"MB");
            bps[i]=videoFile.length();
            mVideoExtractor.unselectTrack(videoTrackIndex);//取消选择视频轨道
        }
        mVideoExtractor.release();//释放资源
    }
}
