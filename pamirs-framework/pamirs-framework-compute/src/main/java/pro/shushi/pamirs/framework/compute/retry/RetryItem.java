package pro.shushi.pamirs.framework.compute.retry;

import pro.shushi.pamirs.meta.annotation.fun.Data;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;

import java.util.List;

/**
 * 重试对象
 * 2021/5/24 1:31 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Data
public class RetryItem {

    // retry model definition list
    private List<ModelDefinition> retryList;

    private boolean retry;

}
