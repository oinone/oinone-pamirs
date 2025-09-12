package pro.shushi.pamirs.boot.web.service.impl.filling;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.boot.base.enmu.QuickFillingFailCodeEnum;
import pro.shushi.pamirs.boot.base.tmodel.QuickFillingFailureDetail;
import pro.shushi.pamirs.boot.base.tmodel.QuickFillingField;
import pro.shushi.pamirs.boot.web.service.QuickFillingValueConverter;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;

import java.lang.reflect.Modifier;
import java.util.*;

/**
 * @author Gesi at 16:36 on 2025/9/11
 */
@Slf4j
public abstract class AbstractValueConverter implements QuickFillingValueConverter {

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Object transformObjectValue(QuickFillingField quickFillingField, String value, QuickFillingFailureDetail failureDetail) {
        if (StringUtils.isBlank(value)) {
            return null;
        }

        ModelFieldConfig modelFieldConfig = quickFillingField.getModelConfigField();
        if (Boolean.TRUE.equals(modelFieldConfig.getMulti())) {
            Collection multiCollection = null;
            try {
                Class<?> collectionClass = Class.forName(modelFieldConfig.getLtype());
                if (Collection.class.isAssignableFrom(collectionClass)) {
                    if (!collectionClass.isInterface() && Modifier.isAbstract(collectionClass.getModifiers())) {
                        multiCollection = (Collection) collectionClass.newInstance();
                    } else {
                        if (List.class.isAssignableFrom(collectionClass)) {
                            multiCollection = new ArrayList<>();
                        } else if (Set.class.isAssignableFrom(collectionClass)) {
                            multiCollection = new HashSet<>();
                        }
                    }
                }
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            if (multiCollection != null) {
                String[] valueList = value.split(",");
                for (String valueItem : valueList) {
                    Object transformValue;
                    try {
                        transformValue = transform(quickFillingField, valueItem, failureDetail);
                    } catch (Exception e) {
                        log.error("自动填报类型转换失败", e);
                        if (!failureDetail.isFailed()) {
                            failureDetail.fail(QuickFillingFailCodeEnum.TYPE_INCOMPATIBLE);
                        }
                        return null;
                    }
                    if (failureDetail.isFailed()) {
                        return null;
                    }
                    multiCollection.add(transformValue);
                }
                return multiCollection;
            }
        }
        try {
            return transform(quickFillingField, value, failureDetail);
        } catch (Exception e) {
            log.error("自动填报类型转换失败", e);
            if (!failureDetail.isFailed()) {
                failureDetail.fail(QuickFillingFailCodeEnum.TYPE_INCOMPATIBLE);
            }
            return null;
        }
    }

    protected Object transform(QuickFillingField quickFillingField, String value, QuickFillingFailureDetail failureDetail) {
        return null;
    }

}
