package pro.shushi.pamirs.meta.api.core.orm.systems.orm;

import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.orm.template.function.FieldRecursionComputeApi;
import pro.shushi.pamirs.meta.api.core.orm.template.function.ModelAfterComputeApi;
import pro.shushi.pamirs.meta.api.core.orm.template.function.ModelBeforeComputeApi;
import pro.shushi.pamirs.meta.api.core.orm.template.function.PersistenceModelAfterComputeApi;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.meta.FuseMeta;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.base.D;
import pro.shushi.pamirs.meta.common.constants.VariableNameConstants;
import pro.shushi.pamirs.meta.constant.FieldConstants;
import pro.shushi.pamirs.meta.util.ClassUtils;
import pro.shushi.pamirs.meta.util.TypeUtils;

import java.util.Map;
import java.util.Objects;

/**
 * ORM对象化处理器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/18 6:35 下午
 */
@SuppressWarnings("rawtypes")
public class DefaultOrmObjectingProcessor implements ModelBeforeComputeApi, ModelAfterComputeApi, PersistenceModelAfterComputeApi, FieldRecursionComputeApi {

    @Override
    public Object before(String model, Object obj) {
        if (TypeUtils.isMap(obj.getClass())) {
            Models.api().setModel(obj, model);
        }
        return obj;
    }

    @Override
    public Object after(String model, Object obj) {
        if (null == obj) {
            return null;
        }
        ModelConfig modelConfig = Objects.requireNonNull(PamirsSession.getContext()).getSimpleModelConfig(model);
        return after(modelConfig, obj);
    }

    @Override
    public Object after(ModelConfig modelConfig, Object obj) {
        if (null == obj) {
            return null;
        }
        String model = modelConfig.getModel();
        if (IWrapper.MODEL_MODEL.equals(model)) {
            return obj;
        }
        Object result = null;
        Map<String, Object> dMap;
        if (TypeUtils.isMap(obj.getClass())) {
            //noinspection unchecked
            dMap = (Map<String, Object>) obj;
            if (null == dMap.get(FieldConstants._d_modelFieldName)) {
                return obj;
            }
            if (Pagination.MODEL_MODEL.equals(model)) {
                result = new Pagination().setModel((String) dMap.get(VariableNameConstants.model));
            } else {
                String lname = FuseMeta.lname(modelConfig);
                if (!ClassUtils.isNoClass(lname)) {
                    result = TypeUtils.getNewInstance(lname);
                }
            }
            if (result != null && D.class.isAssignableFrom(result.getClass())) {
                ((D) result).set_d(dMap);
            } else {
                result = dMap;
            }
        } else {
            return obj;
        }
        return result;
    }
}
