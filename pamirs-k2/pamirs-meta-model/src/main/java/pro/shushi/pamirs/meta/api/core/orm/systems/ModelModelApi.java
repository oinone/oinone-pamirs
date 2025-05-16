package pro.shushi.pamirs.meta.api.core.orm.systems;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.meta.api.core.orm.systems.model.DefaultModelModelApi;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.base.D;
import pro.shushi.pamirs.meta.common.constants.VariableNameConstants;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.util.FieldUtils;
import pro.shushi.pamirs.meta.util.FunctionUtils;
import pro.shushi.pamirs.meta.util.TypeUtils;

import java.util.List;
import java.util.Map;

/**
 * 模型类接口
 * <p>
 * 2020/6/30 2:55 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@SPI
public interface ModelModelApi {

    /**
     * 通过模型类获取模型编码
     *
     * @param modelClazz 模型类
     * @param <T>        模型类泛型
     * @return 模型编码
     */
    default <T> String getModel(Class<T> modelClazz) {
        if (null == modelClazz) {
            return null;
        }
        if (TypeUtils.isMap(modelClazz)) {
            return null;
        } else {
            return DefaultModelModelApi.sign(modelClazz);
        }
    }

    /**
     * 通过对象获取模型编码
     *
     * @param modelObject 模型对象
     * @param <T>         模型类泛型
     * @return 模型编码
     */
    default <T> String getModel(T modelObject) {
        if (null == modelObject) {
            return null;
        }
        if (Map.class.isAssignableFrom(modelObject.getClass())) {
            return (String) ((Map<?, ?>) modelObject).get(VariableNameConstants.entityModel);
        } else if (IWrapper.class.isAssignableFrom(modelObject.getClass())) {
            return ((IWrapper<?>) modelObject).getModel();
        } else if (D.class.isAssignableFrom(modelObject.getClass())) {
            String model = (String) FieldUtils.getFieldValue(modelObject, VariableNameConstants.entityModel);
            if (StringUtils.isNotBlank(model)) {
                return model;
            }
            return DefaultModelModelApi.sign(modelObject.getClass());
        } else if (Iterable.class.isAssignableFrom(modelObject.getClass())) {
            Iterable<?> iterable = (Iterable<?>) modelObject;
            for (Object item : iterable) {
                String model = getModel(item);
                if (model != null) {
                    return model;
                }
            }
        } else if (modelObject.getClass().isArray()) {
            Object[] objects = (Object[]) modelObject;
            for (Object item : objects) {
                String model = getModel(item);
                if (model != null) {
                    return model;
                }
            }
        }
        return null;
    }

    /**
     * 通过对象获取命名空间
     *
     * @param modelObject 模型对象
     * @param <T>         模型类泛型
     * @return 模型编码，如果模型对象是Wrapper或者Condition等封装类，返回被封装的数据实体的模型编码
     */
    default <T> String getDataModel(T modelObject) {
        if (null == modelObject) {
            return null;
        }
        if (Pagination.class.isAssignableFrom(modelObject.getClass())) {
            return (String) FieldUtils.getFieldValue(modelObject, VariableNameConstants.dataModel);
        }
        String model = null;
        if (Map.class.isAssignableFrom(modelObject.getClass())) {
            model = (String) ((Map<?, ?>) modelObject).get(VariableNameConstants.dataModel);
        } else if (IWrapper.class.isAssignableFrom(modelObject.getClass())) {
            model = ((IWrapper<?>) modelObject).getModel();
        } else if (D.class.isAssignableFrom(modelObject.getClass())) {
            model = (String) FieldUtils.getFieldValue(modelObject, VariableNameConstants.dataModel);
        } else if (Iterable.class.isAssignableFrom(modelObject.getClass())) {
            Iterable<?> iterable = (Iterable<?>) modelObject;
            for (Object item : iterable) {
                model = getDataModel(item);
                if (model != null) {
                    return model;
                }
            }
        } else if (modelObject.getClass().isArray()) {
            Object[] objects = (Object[]) modelObject;
            for (Object item : objects) {
                model = getDataModel(item);
                if (model != null) {
                    return model;
                }
            }
        }
        if (model != null) {
            return model;
        }
        return getModel(modelObject);
    }

    /**
     * 设置对象模型编码
     *
     * @param model       模型编码
     * @param modelObject 模型对象
     * @param <T>         模型类泛型
     */
    default <T> void setModel(T modelObject, String model) {
        if (null == modelObject) {
            return;
        }
        if (Map.class.isAssignableFrom(modelObject.getClass())) {
            //noinspection unchecked
            ((Map<String, Object>) modelObject).put(VariableNameConstants.entityModel, model);
        } else if (D.class.isAssignableFrom(modelObject.getClass())) {
            ((D) modelObject).get_d().put(VariableNameConstants.entityModel, model);
        } else if (List.class.isAssignableFrom(modelObject.getClass())) {
            @SuppressWarnings("unchecked") List<Object> list = (List<Object>) modelObject;
            if (CollectionUtils.isEmpty(list)) {
                return;
            }
            for (Object item : list) {
                setModel(item, model);
            }
        } else if (modelObject.getClass().isArray()) {
            Object[] objects = (Object[]) modelObject;
            if (0 == objects.length) {
                return;
            }
            for (Object item : objects) {
                setModel(item, model);
            }
        }
    }

    /**
     * 设置对象命名空间
     *
     * @param dataModel   数据实体模型编码，如果当前模型对象是Wrapper或者Condition等封装类，则表示被封装的数据实体的模型编码
     * @param modelObject 模型对象
     * @param <T>         模型类泛型
     */
    default <T> void setDataModel(String dataModel, T modelObject) {
        if (null == modelObject) {
            return;
        }
        Object obj = FunctionUtils.fetchDynamicParameter(modelObject, 1);
        if (Map.class.isAssignableFrom(obj.getClass())) {
            //noinspection unchecked
            ((Map<String, Object>) obj).put(VariableNameConstants.dataModel, dataModel);
        } else if (D.class.isAssignableFrom(obj.getClass())) {
            FieldUtils.setFieldValue(obj, VariableNameConstants.dataModel, dataModel);
        } else if (List.class.isAssignableFrom(obj.getClass())) {
            @SuppressWarnings("unchecked") List<Object> list = (List<Object>) obj;
            if (CollectionUtils.isEmpty(list)) {
                return;
            }
            for (Object item : list) {
                setDataModel(dataModel, item);
            }
        } else if (obj.getClass().isArray()) {
            Object[] objects = (Object[]) obj;
            if (0 == objects.length) {
                return;
            }
            for (Object item : objects) {
                setDataModel(dataModel, item);
            }
        }
    }

}
