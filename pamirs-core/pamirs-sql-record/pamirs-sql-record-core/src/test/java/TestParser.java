import pro.shushi.pamirs.record.sql.pojo.ReadResult;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;

/**
 * TestParser
 *
 * @author yakir on 2023/06/30 20:19.
 */
public class TestParser {

    private static final RandomAccessFile raf = null;

    public static void main(String[] args) throws IOException {
//        String    json = "{\"cT\":1688127370824,\"eventType\":\"INSERT\",\"now\":\"{\\\"_d_d_model\\\":\\\"test.TestA\\\",\\\"_d_model\\\":\\\"test.TestA\\\",\\\"id\\\":489403057998663708,\\\"createUid\\\":10001,\\\"writeUid\\\":10001}\",\"uT\":1688127370824}\n";
//        SQLRecord data = JSON.parseObject(json, SQLRecord.class);
//        List<Row> rows = RecordMessageParser.parser(data);
//        System.out.println(JSON.toJSONString(rows));

//        String path = "/Users/yakir/Developer/pamirs/pamirs-core/pamirs-sql-record/pamirs-sql-record-core/src/test/java/test.json";
//        raf = new RandomAccessFile(path, "rw");

//        for (int i = 0; i <= 9; i++) {
//            String     json       = "{\"cT\":1688183486679,\"eventType\":\"INSERT\",\"now\":\"{\\\"_d_d_model\\\":\\\"test.TestA\\\",\\\"_d_model\\\":\\\"test.TestA\\\",\\\"id\\\":489638091494001129,\\\"createUid\\\":10001,\\\"writeUid\\\":10001}\",\"uT\":168818348667" + i + "}";
//            byte[]     bytes      = json.getBytes(StandardCharsets.UTF_8);
//            ByteBuffer byteBuffer = ByteBuffer.allocate(bytes.length + 1);
//            byteBuffer.put(bytes);
//            byteBuffer.put("\n".getBytes(StandardCharsets.UTF_8));
//            byteBuffer.flip();
//            raf.seek(raf.length());
//            raf.write(byteBuffer.array());
//        }

        try (RandomAccessFile raf = new RandomAccessFile("/Volumes/sm/repos/record/binlog_event.current", "rw")) {
            raf.seek(0);
            raf.write("0|0|0-0".getBytes(StandardCharsets.UTF_8));
        }

//        ReadResult readResult = readLine();
//        truncateLine(readResult.getPosition());
    }

    public static ReadResult readLine() {

        long position = 0;
        try {
            String line = raf.readLine();
            position = raf.getFilePointer();
            return new ReadResult(position, line);
        } catch (IOException exp) {
//            log.error("读取行失败:", exp);
            exp.printStackTrace();
            return ReadResult.empty();
        }
    }

    public static boolean truncateLine(long position) {

        try {
//            truncateChannel.truncate(truncateSize);
            raf.getChannel().truncate(position);
            return true;
        } catch (IOException exp) {
            return false;
        }

    }

}
