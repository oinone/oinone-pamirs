package pro.shushi.pamirs.middleware.schedule.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.shushi.pamirs.middleware.schedule.constant.ScheduleConstant;
import pro.shushi.pamirs.middleware.schedule.core.manager.ScheduleTaskManager;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

/**
 * schedule.json读取帮助类
 *
 * @author Adamancy Zhang on 2021-04-29 15:40
 */
public class ScheduleFileHelper {

    private static final Logger logger = LoggerFactory.getLogger(ScheduleFileHelper.class);

    private final static ThreadLocal<byte[]> BYTES_LOCAL = new ThreadLocal<>();

    private static final int MAX_BYTE_BUFFER_LENGTH = 1024 * 64;

    private static final String DEFAULT_SCHEDULE_JSON_PATH = System.getProperty("user.dir")
            + "/" + ScheduleConstant.JSON_CONFIG_FILE_NAME;

    private ScheduleFileHelper() {
        //reject create object
    }

    public static void readJsonConfig(Consumer<String> consumer, boolean isAutoCreate) throws IOException {
        InputStream inputStream = null;
        try {
            File file = new File(DEFAULT_SCHEDULE_JSON_PATH);
            boolean isCreateFile = false;
            if (file.exists()) {
                if (file.isFile()) {
                    try {
                        logger.info("读取本地配置 path:{}", file.getPath());
                        inputStream = new FileInputStream(file);
                    } catch (FileNotFoundException e) {
                        throw new IllegalArgumentException("file does not exist. path=" + DEFAULT_SCHEDULE_JSON_PATH, e);
                    }
                } else {
                    throw new UnsupportedOperationException(DEFAULT_SCHEDULE_JSON_PATH + " is not file.");
                }
            } else {
                inputStream = ScheduleTaskManager.class.getClassLoader().getResourceAsStream(ScheduleConstant.JSON_CONFIG_FILE_PATH);
                if (inputStream == null) {
                    logger.warn("未读取到任务配置，请在resources目录下添加配置文件 {}", ScheduleConstant.JSON_CONFIG_FILE_PATH);
                    return;
                }
                logger.info("读取资源配置 path:{}", ScheduleConstant.JSON_CONFIG_FILE_PATH);
                isCreateFile = true;
            }
            FileOutputStream outputStream = null;
            try {
                if (isCreateFile && isAutoCreate) {
                    if (file.createNewFile()) {
                        logger.info("成功创建外部配置文件 path:{}", file.getPath());
                        outputStream = new FileOutputStream(file);
                    } else {
                        logger.warn("配置文件自动创建失败，如需自定义配置信息，请在 {} 目录下手动创建 schedule.json 文件", ScheduleConstant.JSON_CONFIG_FILE_PATH);
                    }
                }
                byte[] buffer = allocateBytes();
                int offset = 0, len;
                while ((len = inputStream.read(buffer, offset, buffer.length - offset)) != -1) {
                    if (outputStream != null) {
                        outputStream.write(buffer, offset, offset + len);
                    }
                    offset += len;
                    if (offset == buffer.length) {
                        byte[] newBytes = new byte[buffer.length * 3 / 2];
                        System.arraycopy(buffer, 0, newBytes, 0, buffer.length);
                        buffer = newBytes;
                    }
                }
                consumer.accept(new String(buffer, StandardCharsets.UTF_8));
            } finally {
                if (outputStream != null) {
                    outputStream.flush();
                    outputStream.close();
                }
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    private static byte[] allocateBytes() {
        return allocateBytes(MAX_BYTE_BUFFER_LENGTH);
    }

    private static byte[] allocateBytes(int length) {
        byte[] chars = BYTES_LOCAL.get();
        if (chars == null) {
            if (length <= MAX_BYTE_BUFFER_LENGTH) {
                chars = new byte[MAX_BYTE_BUFFER_LENGTH];
                BYTES_LOCAL.set(chars);
            } else {
                chars = new byte[length];
            }
        } else if (chars.length < length) {
            chars = new byte[length];
        }
        return chars;
    }
}
