package pro.shushi.pamirs.framework.gateways.graph.longpolling;

import org.springframework.web.context.request.async.DeferredResult;
import pro.shushi.pamirs.meta.annotation.fun.Data;
import pro.shushi.pamirs.meta.api.dto.fun.Function;

/**
 * 长轮询延迟任务
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/17 1:31 上午
 */
@Data
public class DeferredTask {

    private String taskId;

    private Function function;

    private DeferredResult<String> taskResult;

}