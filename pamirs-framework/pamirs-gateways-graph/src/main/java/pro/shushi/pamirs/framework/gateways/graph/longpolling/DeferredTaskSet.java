package pro.shushi.pamirs.framework.gateways.graph.longpolling;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.annotation.fun.Data;
import pro.shushi.pamirs.meta.common.stl.ConcurrentHashSet;

import java.util.Set;

/**
 * 长轮询延迟任务set
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/17 1:31 上午
 */
@Component
@Data
public class DeferredTaskSet {

    private Set<DeferredTask> set = new ConcurrentHashSet<>();

}