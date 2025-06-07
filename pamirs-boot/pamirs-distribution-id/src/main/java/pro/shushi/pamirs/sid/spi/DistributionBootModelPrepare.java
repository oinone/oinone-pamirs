package pro.shushi.pamirs.sid.spi;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.common.spi.api.boot.BootModelPrepareApi;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.sid.model.WorkerNode;
import pro.shushi.pamirs.sid.model.WorkerNodeStatic;

import java.util.Map;

/**
 * 启动预加载模型接口默认实现
 * <p>
 * 2020/8/27 5:03 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Order(55)
@Component
@SPI.Service
public class DistributionBootModelPrepare implements BootModelPrepareApi {

    public void prepare(Map<String/*model*/, String/*simulate model*/> modelMap) {
        modelMap.put(WorkerNode.MODEL_MODEL, WorkerNodeStatic.MODEL_MODEL);
    }

}
