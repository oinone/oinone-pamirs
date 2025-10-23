package pro.shushi.pamirs.boot.web.service.impl.filling;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.boot.base.tmodel.QuickFillingFailureDetail;
import pro.shushi.pamirs.boot.base.tmodel.QuickFillingField;
import pro.shushi.pamirs.boot.web.service.QuickFillingValueConverter;
import pro.shushi.pamirs.framework.connectors.data.sql.query.QueryWrapper;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author Gesi at 9:35 on 2025/9/11
 */
public class M2MConverter extends AbstractValueConverter implements QuickFillingValueConverter {

    public static final QuickFillingValueConverter INSTANCE = new M2MConverter();

    @Override
    public Object transformObjectValue(QuickFillingField quickFillingField, String value, QuickFillingFailureDetail failureDetail) {
        ModelFieldConfig modelFieldConfig = quickFillingField.getModelConfigField();
        if (StringUtils.isBlank(value)) {
            return getFieldCollection(modelFieldConfig);
        }
        QueryWrapper<Object> relationQueryWrapper = getRelationQueryWrapper(quickFillingField, true);
        String relationModel = relationQueryWrapper.getModel();
        AtomicInteger relationQueryNum = new AtomicInteger(0);

        relationQueryWrapper.and(andWrapper -> {
            relationQueryNum.set(fillQueryWrapperCondition(relationModel, andWrapper, quickFillingField, value, failureDetail));
        });

        if (failureDetail.isFailed()) {
            return null;
        }

        List<Object> relationList = Models.origin().queryListByWrapper(relationQueryWrapper);
        if (relationQueryNum.get() != relationList.size()) {
            failureDetail.fail("查询结果数量与传入数量不匹配");
            return null;
        }
        Collection<Object> relationCollection = getFieldCollection(modelFieldConfig);
        relationCollection.addAll(relationList);
        return relationCollection;
    }

    private int fillQueryWrapperCondition(String relationModel, QueryWrapper<Object> queryWrapper, QuickFillingField quickFillingField, String value, QuickFillingFailureDetail failureDetail) {
        ModelConfig relationModelConfig = PamirsSession.getContext().getModelConfig(relationModel);

        List<String> relationSelectFields = quickFillingField.getRelationSelectFields();
        List<ModelFieldConfig> relationSelectFieldConfigs = new ArrayList<>(relationSelectFields.size());
        for (String relationSelectField : relationSelectFields) {
            ModelFieldConfig relationModelFieldConfig =
                    relationModelConfig.getModelFieldConfigList().stream().filter(i -> StringUtils.equals(i.getField(), relationSelectField)).collect(Collectors.toList()).get(0);
            relationSelectFieldConfigs.add(relationModelFieldConfig);
        }

        String[] valueList = value.split(",");
        int relationQueryNum = 0;

        for (String valueItem : valueList) {
            String[] valueSearchPartList = valueItem.split("-");
            if (StringUtils.isBlank(valueItem) || valueSearchPartList.length == 0) {
                continue;
            }

            if (valueSearchPartList.length > relationSelectFieldConfigs.size()) {
                failureDetail.fail();
                return 0;
            }
            queryWrapper.or(orWrapper -> {
                for (int i = 0; i < valueSearchPartList.length; i++) {
                    ModelFieldConfig relationModelFieldConfig = relationSelectFieldConfigs.get(i);
                    String searchPart = valueSearchPartList[i];
                    if (StringUtils.isNotBlank(searchPart)) {
                        orWrapper.eq(relationModelFieldConfig.getColumn(), searchPart);
                    } else {
                        orWrapper.isNull(relationModelFieldConfig.getColumn());
                    }
                }
            });
            relationQueryNum++;
        }

        if (relationQueryNum == 0) {
            failureDetail.fail();
            return 0;
        }

        return relationQueryNum;
    }
}
