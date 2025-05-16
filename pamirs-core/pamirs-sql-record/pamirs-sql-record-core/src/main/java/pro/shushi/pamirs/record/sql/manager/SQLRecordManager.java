package pro.shushi.pamirs.record.sql.manager;

import com.google.common.base.Splitter;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.util.JsonUtils;
import pro.shushi.pamirs.record.sql.config.SQLRecordConfig;
import pro.shushi.pamirs.record.sql.lock.SQLRecordLockFactory;
import pro.shushi.pamirs.record.sql.pojo.ReadResult;
import pro.shushi.pamirs.record.sql.pojo.SQLRecord;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * SQLRecordManager
 *
 * @author yakir on 2023/06/29 14:58.
 */
@Slf4j
abstract
public class SQLRecordManager {

    public static final byte[] NEW_LINE = "\n".getBytes(StandardCharsets.UTF_8);

    protected String storePath;

    protected transient long writeFilePos = 0;
    protected transient long readFilePos = 0;
    protected transient long pos = 0;
    protected transient boolean hasNewFile = false;

    private static final long fileMaxSize = 10L * 1024L * 1024L * 1024L;

    protected SQLRecordManager(SQLRecordConfig sqlRecordConfig) {
        String storePath = sqlRecordConfig.getStore();
        boolean isLOCK = sqlRecordConfig.isLock();
        if (StringUtils.isBlank(storePath)) {
            this.storePath = System.getProperty("user.dir");
            log.info("未配置SQL记录存储目录 ！");
            log.info("未配置SQL记录存储目录 ！");
            log.info("未配置SQL记录存储目录 ！");
            log.info("将使用默认存储目录: [{}]", this.storePath);
        } else {
            this.storePath = storePath;
            log.info("[{}] 存储目录:[{}]", this.techName(), this.storePath);
        }

        File dir = new File(this.storePath);
        if (dir.exists()) {
            if (!dir.isDirectory() || !dir.canRead() || !dir.canWrite()) {
                throw new RuntimeException(techName() + " SQL记录存储目录[" + this.storePath + "]异常,请设置正确的存储目录");
            }
        } else {
            try {
                Files.createDirectory(Paths.get(this.storePath));
            } catch (IOException exp) {
                throw new RuntimeException(techName() + " SQL记录存储目录[" + this.storePath + "]异常,请设置正确的存储目录", exp);
            }
        }

        if (!isLOCK) {
            return;
        }
        SQLRecordLockFactory.NLock lock = SQLRecordLockFactory.INSTANCE.lock(this.storePath, ".lock");
        if (null == lock) {
            // 已有其他线程锁定
        } else {
            lock.ensureValid();
        }
    }

    protected void metaFile() {
        // 0|0|0-0
        // 当前文件|写最新文件|文件-position
        String cDataPath = this.storePath + File.separatorChar + getCurrName();
        try (RandomAccessFile raf = new RandomAccessFile(cDataPath, "rw")) {
            String line = raf.readLine();
            if (StringUtils.isBlank(line)) {
                raf.seek(0);
                raf.write("0|0|0-0".getBytes(StandardCharsets.UTF_8));
            } else {
                List<String> positions = Splitter.on("|").splitToList(line);
                long currentFile = Long.parseLong(positions.get(0));
                long newFile = Long.parseLong(positions.get(1));
                List<String> filePosList = Splitter.on("-").splitToList(positions.get(2));
                long readFile = Long.parseLong(filePosList.get(0));
                long readPos = Long.parseLong(filePosList.get(1));
                this.writeFilePos = newFile;
                this.readFilePos = currentFile;
                this.pos = readPos;
            }
        } catch (IOException exp) {
            throw new RuntimeException("Current记录存储文件[" + cDataPath + "]异常", exp);
        }

        String currPath = storePath + File.separatorChar + getBinName() + "." + writeFilePos;
        Path currFilePath = Paths.get(currPath);
        if (Files.notExists(currFilePath)) {
            try {
                Files.createFile(currFilePath);
            } catch (IOException exp) {
                throw new RuntimeException("SQL记录存储文件创建[" + currFilePath + "]异常", exp);
            }
        }
    }

    public SQLRecord appendBytes(SQLRecord data) {

        String json = JsonUtils.toJSONString(data);
        byte[] jsonBytes = json.getBytes(StandardCharsets.UTF_8);
        int capacity = jsonBytes.length + NEW_LINE.length;

        ByteBuffer byteBuffer = ByteBuffer.allocate(capacity);
        byteBuffer.put(jsonBytes);
        byteBuffer.put(NEW_LINE);
        byteBuffer.flip();

        String writeFile = this.storePath + File.separatorChar + getBinName() + "." + writeFilePos;
        Path writeFilePath = Paths.get(writeFile);
        if (!Files.exists(writeFilePath)) {
            try {
                Files.createFile(writeFilePath);
            } catch (IOException exp) {
                writeFile = this.storePath + File.separatorChar + getBinName() + "." + (writeFilePos - 1);
                log.error("新建SQL记录存储文件错误[" + writeFile + "]异常", exp);
            }
        }
        try (RandomAccessFile raf = new RandomAccessFile(writeFile, "rw")) {
            raf.seek(raf.length());
            raf.write(byteBuffer.array());
            log.info("[{}] write", techName());
            if (fileMaxSize <= raf.length()) {
                writeFilePos = writeFilePos + 1;
                hasNewFile = true;
            }
        } catch (IOException exp) {
            throw new RuntimeException("刷盘失败:" + json, exp);
        }
        return data;
    }

    public ReadResult readLine() {

        try (RandomAccessFile raf = new RandomAccessFile(this.storePath + File.separatorChar + getBinName() + "." + readFilePos, "rw")) {
            raf.seek(pos);
            String line = raf.readLine();
            if (StringUtils.isBlank(line)) {
                if (hasNewFile) {
                    readFilePos = readFilePos + 1;
                    pos = 0;
                    hasNewFile = false;
                }
                return ReadResult.empty();
            }
            line = new String(line.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
            return new ReadResult(raf.getFilePointer(), line);
        } catch (IOException exp) {
            log.error("读取行失败:", exp);
            return ReadResult.empty();
        }
    }

    public boolean commit(long position) {

        this.pos = position;
        try (RandomAccessFile raf = new RandomAccessFile(this.storePath + File.separatorChar + getCurrName(), "rw");
             FileChannel fileChannel = raf.getChannel()) {
            String write = this.writeFilePos + "|" + this.writeFilePos + "|" + this.readFilePos + "-" + position;
            fileChannel.truncate(0);
            raf.write(write.getBytes(StandardCharsets.UTF_8));
            return true;
        } catch (IOException exp) {
            throw new RuntimeException("Current记录存储文件[" + getCurrName() + "]异常", exp);
        }
    }

    public abstract String techName();

    public abstract String getBinName();

    public abstract String getCurrName();

    public abstract String topic();
}
