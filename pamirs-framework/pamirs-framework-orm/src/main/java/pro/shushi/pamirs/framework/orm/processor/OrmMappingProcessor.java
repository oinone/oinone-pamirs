package pro.shushi.pamirs.framework.orm.processor;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.orm.template.function.ModelAfterComputeApi;
import pro.shushi.pamirs.meta.api.core.orm.template.function.ModelBeforeComputeApi;
import pro.shushi.pamirs.meta.api.core.orm.template.function.FieldRecursionComputeApi;
import pro.shushi.pamirs.meta.base.D;

import java.util.Map;

/**
 * ORM map化 处理器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/18 6:35 下午
 */
@SuppressWarnings("rawtypes")
@Component
public class OrmMappingProcessor implements ModelBeforeComputeApi, ModelAfterComputeApi,
        FieldRecursionComputeApi {

    @Override
    public Object before(String model, Object obj) {
        Models.api().setModel(obj, model);
        return obj;
    }

    @Override
    public Object after(String model, Object obj) {
        Map<String, Object> dMap;
        if (Map.class.isAssignableFrom(obj.getClass())) {
            //noinspection unchecked
            dMap = (Map<String, Object>) obj;
        } else {
            dMap = ((D) obj).get_d();
        }
        return dMap;
    }

}
