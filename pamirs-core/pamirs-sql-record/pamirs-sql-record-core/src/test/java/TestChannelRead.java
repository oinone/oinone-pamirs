import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

/**
 * TestChannelRead
 *
 * @author yakir on 2023/06/29 14:34.
 */
public class TestChannelRead {

    public static void main(String[] args) throws IOException {
//        RandomAccessFile randomAccessFile = new RandomAccessFile("/Volumes/sm/repos/record/binlog_event.bin", "rw");
        FileInputStream  randomAccessFile = new FileInputStream("/Volumes/sm/repos/record/binlog_event.bin");
        FileOutputStream out              = new FileOutputStream("/Volumes/sm/repos/record/binlog_event.bin");
        FileChannel      channel          = randomAccessFile.getChannel();
        FileChannel      outchannel       = out.getChannel();
        ByteBuffer       buffer           = ByteBuffer.allocate(1024 * 1024);
        int              bytesRead        = channel.read(buffer);
        ByteBuffer       stringBuffer     = ByteBuffer.allocate(20);
        while (bytesRead != -1) {
            System.out.println("读取字节数：" + bytesRead);
            //之前是写buffer，现在要读buffer
            buffer.flip();// 切换模式，写->读
            while (buffer.hasRemaining()) {
                byte b = buffer.get();
                if (b == 10 || b == 13) { // 换行或回车
                    stringBuffer.flip();
                    // 这里就是一个行
                    long truncateSize = channel.size() - buffer.position();
                    outchannel.truncate(truncateSize);
                    final String line = StandardCharsets.UTF_8.decode(stringBuffer).toString();
                    System.out.println(line + "----------");// 解码已经读到的一行所对应的字节
                    stringBuffer.clear();
                } else {
                    if (stringBuffer.hasRemaining())
                        stringBuffer.put(b);
                    else { // 空间不够扩容
                        stringBuffer = reAllocate(stringBuffer);
                        stringBuffer.put(b);
                    }
                }
            }
            buffer.clear();// 清空,position位置为0，limit=capacity
            //  继续往buffer中写
            bytesRead = channel.read(buffer);
        }
        randomAccessFile.close();
    }


    private static ByteBuffer reAllocate(ByteBuffer stringBuffer) {
        final int capacity  = stringBuffer.capacity();
        byte[]    newBuffer = new byte[capacity * 2];
        System.arraycopy(stringBuffer.array(), 0, newBuffer, 0, capacity);
        return (ByteBuffer) ByteBuffer.wrap(newBuffer).position(capacity);
    }

}
