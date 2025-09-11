package pro.shushi.pamirs.boot.web.service.impl.filling;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.boot.base.tmodel.QuickFillingFailureDetail;
import pro.shushi.pamirs.boot.base.tmodel.QuickFillingField;
import pro.shushi.pamirs.boot.web.service.QuickFillingValueConverter;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;

import java.lang.reflect.Modifier;
import java.util.*;

/**
 * @author Gesi at 16:36 on 2025/9/11
 */
public abstract class AbstractValueConverter implements QuickFillingValueConverter {

    @Override
    public Object transformObjectValue(QuickFillingField quickFillingField, String value, QuickFillingFailureDetail failureDetail) {
        if (StringUtils.isBlank(value)) {
            return null;
        }

        ModelFieldConfig modelConfigField = quickFillingField.getModelConfigField();
        if (Boolean.TRUE.equals(modelConfigField.getMulti())) {
            String[] valueList = value.split(",");
            Collection<Object> multiCollection;
            try {
                Class<?> collectionClass = Class.forName(modelConfigField.getLtype());
                if (!collectionClass.isInterface() || Modifier.isAbstract(collectionClass.getModifiers())) {
                    multiCollection = (Collection<Object>) collectionClass.newInstance();
                } else {
                    if (List.class.isAssignableFrom(collectionClass)) {
                        multiCollection = new ArrayList<>();
                    } else {
                        multiCollection = new HashSet<>();
                    }
                }
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            for (String valueItem : valueList) {
                Object transformValue = transform(quickFillingField, valueItem, failureDetail);
                multiCollection.add(transformValue);
            }

            return multiCollection;
        } else {
            return transform(quickFillingField, value, failureDetail);
        }
    }

    protected abstract Object transform(QuickFillingField quickFillingField, String value, QuickFillingFailureDetail failureDetail);

}
