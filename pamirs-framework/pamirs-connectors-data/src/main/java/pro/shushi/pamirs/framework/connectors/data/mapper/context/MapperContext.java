package pro.shushi.pamirs.framework.connectors.data.mapper.context;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.binding.MapperMethod;
import org.springframework.aop.support.AopUtils;
import pro.shushi.pamirs.framework.connectors.data.plugin.util.StatementUtil;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.util.TypeUtils;

import java.util.Map;
import java.util.Optional;

/**
 * mapper上下文处理
 * <p>
 * 2020/6/30 5:06 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class MapperContext {

    public static String modelFromMapper(Class<?> mapperClass, Object... objects) {
        String model = model(objects);
        if (StringUtils.isBlank(model)) {
            return Models.api().getModel(TypeUtils.extractModelClass(mapperClass));
        }
        return model;
    }

    public static Object objFromMapper(Object obj, String key) {
        if (MapperMethod.ParamMap.class.isAssignableFrom(obj.getClass())) {
            //noinspection rawtypes
            return ((MapperMethod.ParamMap) obj).get(key);
        } else {
            return obj;
        }
    }

    /**
     * 从mapper的参数中获取当前模型编码
     *
     * @param parameters mapper map参数
     * @return 模型编码
     */
    public static String model(Map<?, ?> parameters) {
        if (MapUtils.isEmpty(parameters)) {
            return null;
        }
        return model(parameters.values().toArray());
    }

    /**
     * 从mapper的参数中获取当前模型编码
     *
     * @param objects mapper 多参数
     * @return 模型编码
     */
    public static String model(Object... objects) {
        if (null == objects || ArrayUtils.isEmpty(objects)) {
            return null;
        }
        String model;
        for (Object obj : objects) {
            if (null == obj) {
                continue;
            }
            if (obj instanceof StatementUtil || TypeUtils.isBaseType(obj.getClass())) {
                model = null;
            } else if (MapperMethod.ParamMap.class.isAssignableFrom(obj.getClass())) {
                return model(((MapperMethod.ParamMap<?>) obj).values().toArray());
            } else if (IWrapper.class.isAssignableFrom(obj.getClass())) {
                //noinspection rawtypes
                model = Optional.of((IWrapper) obj).map(IWrapper::getModel).orElse(null);
            } else if (Pagination.class.isAssignableFrom(obj.getClass())) {
                //noinspection rawtypes
                model = Optional.of((Pagination) obj).map(Pagination::getModel).orElse(null);
                if (Pagination.MODEL_MODEL.equals(model)) {
                    model = null;
                }
            } else {
                model = Optional.of(obj).map(v -> Models.api().getModel(obj)).orElse(null);
            }
            if (StringUtils.isNotBlank(model)) {
                return model;
            }
        }
        return null;
    }

    /**
     * 设置mapper的参数中的模型编码
     *
     * @param model      模型编码
     * @param parameters mapper map参数
     */
    public static void setModel(String model, Map<?, ?> parameters) {
        if (StringUtils.isBlank(model) || MapUtils.isEmpty(parameters)) {
            return;
        }
        setModel(model, parameters.values().toArray());
    }

    /**
     * 从mapper的参数中获取当前模型编码
     *
     * @param model   模型编码
     * @param objects mapper 多参数
     */
    public static void setModel(final String model, Object... objects) {
        if (StringUtils.isBlank(model) || null == objects || ArrayUtils.isEmpty(objects)) {
            return;
        }
        for (Object obj : objects) {
            if (null == obj) {
                continue;
            }
            if (MapperMethod.ParamMap.class.isAssignableFrom(obj.getClass())) {
                setModel(model, ((MapperMethod.ParamMap<?>) obj).values().toArray());
            } else if (IWrapper.class.isAssignableFrom(obj.getClass())) {
                //noinspection rawtypes
                ((IWrapper) obj).setModel(model);
            } else if (Pagination.class.isAssignableFrom(obj.getClass())) {
                if (!Pagination.MODEL_MODEL.equals(model)) {
                    //noinspection rawtypes
                    ((Pagination) obj).setModel(model);
                }
            } else {
                Models.api().setModel(obj, model);
            }
        }
    }

    public static Class<?> fetchProxyMapperClass(Object mapper) {
        if (AopUtils.isAopProxy(mapper)) {
            return mapper.getClass().getInterfaces()[0];
        }
        return mapper.getClass();
    }

}
