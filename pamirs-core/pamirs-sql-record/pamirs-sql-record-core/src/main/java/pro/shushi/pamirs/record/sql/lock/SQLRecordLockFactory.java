package pro.shushi.pamirs.record.sql.lock;

import org.apache.commons.collections4.CollectionUtils;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * SQLRecordLockFactory
 *
 * @author yakir on 2024/07/29 11:07.
 * @linkplain 仿 org.apache.lucene.store.NativeFSLockFactory
 */
@Slf4j
public class SQLRecordLockFactory {

    public static final SQLRecordLockFactory INSTANCE = new SQLRecordLockFactory();

    private static final Set<String> LOCKED = Collections.synchronizedSet(new HashSet<>());

    private static final Set<NFileLock> locks = new HashSet<>();

    private SQLRecordLockFactory() {}

    public final NLock lock(String dir, String lockName) throws LockFailedError {

        Path dirPath = new File(dir).toPath();
        Path lockPath = dirPath.resolve(lockName);

        IOException exception = null;
        try {
            Files.createFile(lockPath);
        } catch (IOException e) {
            exception = e;
        }
        Path lockRealPath = null;
        try {
            lockRealPath = lockPath.toRealPath();
        } catch (IOException e) {
            if (null != exception) {
                e.addSuppressed(exception);
            }
            throw new LockFailedError("获取SQLRecord存储目录路径", e);
        }

        FileTime creationTime = null;
        try {
            creationTime = Files.readAttributes(lockRealPath, BasicFileAttributes.class)
                    .creationTime();
        } catch (IOException e) {
            throw new LockFailedError("获取SQLRecord存储目录创建时间", e);
        }

        if (LOCKED.add(lockRealPath.toString())) {
            FileChannel channel = null;
            FileLock lock = null;
            try {
                channel = FileChannel.open(lockRealPath, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
                lock = channel.tryLock();
                if (null != lock) {
                    NFileLock nFileLock = new NFileLock(lock, channel, lockRealPath, creationTime);
                    locks.add(nFileLock);
                    return nFileLock;
                } else {
                    throw new LockFailedError("SQLRecord存储目录已有其他应用锁定,请修改SQLRecord存储目录位置...");
                }
            } catch (IOException e) {
                throw new LockFailedError("打开SQLRecord存储目录锁文件", e);
            } finally {
                if (null == lock) {
                    handlePanic(channel);
                    LOCKED.remove(lockRealPath.toString());
                }
            }
        } else {
            // 已有其他线程锁定
            return null;
        }
    }

    public interface NLock extends Closeable {

        void ensureValid();

        boolean isLocked();

    }

    private static class NFileLock implements NLock {

        private final FileLock lock;
        private final FileChannel channel;
        private final Path path;
        private final FileTime creationTime;

        private volatile boolean closed;


        public NFileLock(FileLock lock, FileChannel channel, Path path, FileTime creationTime) {
            this.lock = lock;
            this.channel = channel;
            this.path = path;
            this.creationTime = creationTime;
        }

        @Override
        public void ensureValid() {

            if (closed) {
                throw new LockFailedError("SQLRecord存储目录锁已释放");
            }

            if (!LOCKED.contains(path.toString())) {
                throw new LockFailedError("SQLRecord存储目录锁已意外释放 " + this);
            }

            if (!lock.isValid()) {
                throw new LockFailedError("SQLRecord存储目录锁已意外强制释放: " + this);
            }

            long size = -1;
            try {
                size = channel.size();
            } catch (IOException e) {
                throw new LockFailedError("SQLRecord存储目录锁文件意外大小: " + size + ", (lock=" + this + ")");
            }
            if (size != 0) {
                throw new LockFailedError("SQLRecord存储目录锁文件意外大小: " + size + ", (lock=" + this + ")");
            }

            FileTime ctime = null;
            try {
                ctime = Files.readAttributes(path, BasicFileAttributes.class).creationTime();
            } catch (IOException e) {
                throw new LockFailedError("获取SQLRecord存储目录创建时间", e);
            }
            if (!creationTime.equals(ctime)) {
                throw new LockFailedError("SQLRecord存储目录锁底层文件被改变 " + ctime + ", (lock=" + this + ")");
            }
        }

        @Override
        public boolean isLocked() {
            if (null == lock) {
                return false;
            }

            return lock.isValid();
        }

        @Override
        public synchronized void close() throws IOException {
            if (closed) {
                return;
            }

            try (FileChannel _channel = this.channel;
                 FileLock _lock = this.lock) {
                if (null == _channel) {
                    throw new LockFailedError("SQLRecord存储目录锁文件通道异常");
                }
                if (null == _lock) {
                    throw new LockFailedError("SQLRecord存储目录锁文件异常");
                }
            } finally {
                LOCKED.remove(path.toString());
            }
        }
    }

    public static void shutdown() {
        if (CollectionUtils.isEmpty(locks)) {
            return;
        }

        for (NFileLock lock : locks) {
            if (null == lock) {
                continue;
            }
            try {
                lock.close();
            } catch (IOException e) {
                log.error("关闭锁资源异常", e);
            }
        }
    }

    public static class LockFailedError extends Error {

        private static final long serialVersionUID = 315174137295595676L;

        public LockFailedError(String message) {
            super(message);
        }

        public LockFailedError(String message, Throwable cause) {
            super(message, cause);
        }
    }

    private static void handlePanic(Closeable... closeables) {
        Error error = null;
        Throwable throwable = null;
        for (Closeable closeable : closeables) {
            if (null == closeable) {
                continue;
            }
            try {
                closeable.close();
            } catch (Error e) {
                if (null != error) {
                    error.addSuppressed(e);
                } else {
                    error = e;
                }
            } catch (Throwable th) {
                if (null != throwable) {
                    throwable.addSuppressed(th);
                } else {
                    throwable = th;
                }
            }
        }
        if (null != error) {
            if (null != throwable) {
                error.addSuppressed(throwable);
            }
            throw error;
        }
    }
}
