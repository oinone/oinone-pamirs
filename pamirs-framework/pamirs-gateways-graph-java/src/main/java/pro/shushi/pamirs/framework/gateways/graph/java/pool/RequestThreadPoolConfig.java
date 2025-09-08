package pro.shushi.pamirs.framework.gateways.graph.java.pool;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import pro.shushi.pamirs.meta.annotation.fun.Data;

/**
 * RequestThreadPoolConfig
 *
 * @author yakir on 2025/03/19 10:12.
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "pamirs.request.thread")
public class RequestThreadPoolConfig {

    private boolean deferred = true;

    private int coreSize = Math.max(Runtime.getRuntime().availableProcessors(), 10);

    private int maxSize = Math.max(Runtime.getRuntime().availableProcessors(), 16);

    private long keepAliveTime = 60 * 1000 * 1000L;

}
