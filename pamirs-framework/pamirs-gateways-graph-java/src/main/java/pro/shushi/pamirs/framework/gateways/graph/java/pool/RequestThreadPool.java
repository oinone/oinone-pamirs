package pro.shushi.pamirs.framework.gateways.graph.java.pool;

import com.alibaba.fastjson.serializer.SerializerFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.async.DeferredResult;
import pro.shushi.pamirs.framework.gateways.graph.error.ClientGraphQLError;
import pro.shushi.pamirs.framework.gateways.graph.spi.TranslateErrorService;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.dto.common.Message;
import pro.shushi.pamirs.meta.api.dto.protocol.PamirsRequestResult;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.enmu.InformationLevelEnum;
import pro.shushi.pamirs.meta.util.JsonUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * RequestThreadPool
 *
 * @author yakir on 2025/03/19 09:55.
 */
@Slf4j
@Configuration
public class RequestThreadPool {

    private static final AtomicInteger deferredThreadIncr = new AtomicInteger();

    private final ExecutorService executorService;

    public RequestThreadPool(@Autowired RequestThreadPoolConfig requestThreadPoolConfig) {
        executorService = new ThreadPoolExecutor(requestThreadPoolConfig.getCoreSize(), requestThreadPoolConfig.getMaxSize(),
                requestThreadPoolConfig.getKeepAliveTime(),
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(),
                new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r, "Deferred" + deferredThreadIncr.getAndIncrement());
                    }
                });
    }

    public void submit(Runnable run, DeferredResult<String> deferredResult) {
        if (null == executorService) {
            PamirsRequestResult result = new PamirsRequestResult();
            result.setData(new HashMap<>());
            log.warn("系统启动中,稍后再试...");
            Map<Object, Object> extMap = new HashMap<>();
            extMap.put("success", true);
            extMap.put(ClientGraphQLError.MESSAGES, Collections.singletonList(Message.init().setMessage("系统启动中,稍后再试...")
                    .setLevel(InformationLevelEnum.WARN).setErrorType(null)));
            result.setExtensions(extMap);
            TranslateErrorService translateErrorService = Spider.getDefaultExtension(TranslateErrorService.class);
            translateErrorService.translateError(result);
            deferredResult.setResult(JsonUtils.toJSONString(result, SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.WriteDateUseDateFormat));
            return;
        }

        executorService.submit(run);
    }
}
