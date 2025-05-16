package pro.shushi.pamirs.core.common.logger.logback;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.OutputStreamAppender;
import pro.shushi.pamirs.core.common.logger.DebugLoggerTrace;
import pro.shushi.pamirs.framework.common.api.SceneAnalysisDebugTraceApi;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * 调试追踪日志适配器
 *
 * @author Adamancy Zhang at 11:36 on 2025-03-26
 */
public class DebugLoggerTraceLogbackAppender<E> extends OutputStreamAppender<E> {

    private static volatile DebugLoggerTraceLogbackAppender<?> INSTANCE = null;

    public DebugLoggerTraceLogbackAppender() {
        INSTANCE = this;
    }

    @Override
    public void start() {
        // lazy start
    }

    public void start0() {
        if (this.isStarted()) {
            return;
        }
        OutputStream targetStream = new DebugTraceOutputStream(this);
        setOutputStream(targetStream);
        super.start();
    }

    public static void init(org.slf4j.Logger logger) {
        if (logger instanceof Logger) {
            if (DebugLoggerTraceLogbackAppender.INSTANCE == null) {
                synchronized (DebugLoggerTraceLogbackAppender.class) {
                    if (DebugLoggerTraceLogbackAppender.INSTANCE == null) {
                        DebugLoggerTraceLogbackAppender.INSTANCE = initAppender((Logger) logger);
                    }
                }
            }
            DebugLoggerTraceLogbackAppender.INSTANCE.start0();
        }
    }

    private static DebugLoggerTraceLogbackAppender<ILoggingEvent> initAppender(Logger logger) {
        LoggerContext loggerContext = logger.getLoggerContext();

        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setPattern("%d %level [%thread] [%file:%line] - %msg %n");
        encoder.setCharset(StandardCharsets.UTF_8);
        encoder.setContext(loggerContext);
        encoder.start();

        DebugLoggerTraceLogbackAppender<ILoggingEvent> appender = new DebugLoggerTraceLogbackAppender<>();
        appender.setEncoder(encoder);
        appender.setContext(loggerContext);

        for (Logger item : loggerContext.getLoggerList()) {
            if (item.iteratorForAppenders().hasNext()) {
                item.addAppender(appender);
            }
        }

        loggerContext.addTurboFilter(new DebugLoggerTraceTurboFilter());

        return appender;
    }

    private static class DebugTraceOutputStream extends OutputStream {

        private final DebugLoggerTraceLogbackAppender<?> appender;

        public DebugTraceOutputStream(DebugLoggerTraceLogbackAppender<?> appender) {
            this.appender = appender;
        }

        private final ByteArrayOutputStream writer = new ByteArrayOutputStream(4096);

        @Override
        public void write(int b) throws IOException {
            if (!appender.isStarted()) {
                return;
            }
            if (!SceneAnalysisDebugTraceApi.isDebug()) {
                return;
            }
            writer.write(b);
        }

        @Override
        public void write(byte[] b) throws IOException {
            if (!appender.isStarted()) {
                return;
            }
            if (!SceneAnalysisDebugTraceApi.isDebug()) {
                return;
            }
            writer.write(b);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            if (!appender.isStarted()) {
                return;
            }
            if (!SceneAnalysisDebugTraceApi.isDebug()) {
                return;
            }
            writer.write(b, off, len);
        }

        @Override
        public void flush() throws IOException {
            if (!appender.isStarted()) {
                return;
            }
            if (!SceneAnalysisDebugTraceApi.isDebug()) {
                return;
            }
            if (writer.size() >= 1) {
                DebugLoggerTrace.out(writer.toString(StandardCharsets.UTF_8.name()));
                writer.reset();
            }
        }
    }
}
