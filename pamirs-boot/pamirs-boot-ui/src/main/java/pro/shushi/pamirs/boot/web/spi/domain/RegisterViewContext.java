package pro.shushi.pamirs.boot.web.spi.domain;

import pro.shushi.pamirs.boot.base.model.Action;
import pro.shushi.pamirs.meta.annotation.fun.Data;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.util.FieldUtils;
import pro.shushi.pamirs.meta.util.ModelUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * 注册视图上下文
 * <p>
 * 2022/5/18 3:47 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Data
public class RegisterViewContext {

    private Meta meta;

    private ModelDefinition modelDefinition;

    private Map<String, List<Action>> actionMap;

    private Class<?> clazz;

    private List<Field> fieldList;

    public RegisterViewContext(Meta meta, ModelDefinition modelDefinition, Map<String, List<Action>> actionMap) {
        setMeta(meta);
        setModelDefinition(modelDefinition);
        setActionMap(actionMap);
        Class<?> clazz = ModelUtils.fetchClazz(getModelDefinition());
        if (null == clazz) {
            return;
        }
        setClazz(clazz);
    }

    public List<Field> fetchFieldList() {
        List<Field> fieldList = getFieldList();
        if (null == fieldList) {
            fieldList = FieldUtils.getDeclaredFieldsByClassForMultipleInheritance(getClazz(), getMeta()::getModel);
            setFieldList(fieldList);
        }
        return fieldList;
    }

}
