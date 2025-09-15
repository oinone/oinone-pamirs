package pro.shushi.pamirs.boot.web.service.impl.filling;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.boot.base.enmu.QuickFillingFailCodeEnum;
import pro.shushi.pamirs.boot.base.tmodel.QuickFillingFailureDetail;
import pro.shushi.pamirs.boot.base.tmodel.QuickFillingField;
import pro.shushi.pamirs.boot.web.service.QuickFillingValueConverter;
import pro.shushi.pamirs.framework.connectors.data.sql.query.QueryWrapper;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Gesi at 9:35 on 2025/9/11
 */
@Service
public class M2MConverter extends AbstractValueConverter implements QuickFillingValueConverter {

    @Override
    public boolean canTransform(TtypeEnum ttype) {
        return TtypeEnum.M2M.equals(ttype) || TtypeEnum.O2M.equals(ttype);
    }

    @Override
    public Object transformObjectValue(QuickFillingField quickFillingField, String value, QuickFillingFailureDetail failureDetail) {
        ModelFieldConfig modelFieldConfig = quickFillingField.getModelConfigField();
        if (StringUtils.isBlank(value)) {
            return getFieldCollection(modelFieldConfig);
        }
        QueryWrapper<Object> relationQueryWrapper = getRelationQueryWrapper(quickFillingField, true);
        fillQueryWrapperCondition(relationQueryWrapper, quickFillingField, value, failureDetail);

        List<Object> relationList = Models.origin().queryListByWrapper(relationQueryWrapper);
        if (CollectionUtils.isEmpty(relationList)) {
            return null;
        }
        Collection<Object> relationCollection = getFieldCollection(modelFieldConfig);
        relationCollection.addAll(relationList);
        return relationCollection;
    }

    private void fillQueryWrapperCondition(QueryWrapper<Object> queryWrapper, QuickFillingField quickFillingField, String value, QuickFillingFailureDetail failureDetail) {
        String relationModel = queryWrapper.getModel();
        ModelConfig relationModelConfig = PamirsSession.getContext().getModelConfig(relationModel);
        List<String> relationSelectFields = quickFillingField.getRelationSelectFields();
        List<ModelFieldConfig> relationSelectFieldConfigs = new ArrayList<>(relationSelectFields.size());
        for (String relationSelectField : relationSelectFields) {
            ModelFieldConfig relationModelFieldConfig =
                    relationModelConfig.getModelFieldConfigList().stream().filter(i -> StringUtils.equals(i.getField(), relationSelectField)).collect(Collectors.toList()).get(0);
            relationSelectFieldConfigs.add(relationModelFieldConfig);
        }

        String[] valueList = value.split(",");

        for (String valueItem : valueList) {
            String[] valueSearchPartList = valueItem.split("-");
            if (StringUtils.isBlank(valueItem) || valueSearchPartList.length == 0) {
                queryWrapper.or(orWrapper -> {
                    orWrapper.eq("1", "0");
                });
                continue;
            }

            if (valueSearchPartList.length > relationSelectFieldConfigs.size()) {
                failureDetail.fail(QuickFillingFailCodeEnum.TYPE_INCOMPATIBLE);
                return;
            }
            queryWrapper.or(orWrapper -> {
                for (int i = 0; i < valueSearchPartList.length; i++) {
                    ModelFieldConfig relationModelFieldConfig = relationSelectFieldConfigs.get(i);
                    String searchPart = valueSearchPartList[i];
                    orWrapper.eq(relationModelFieldConfig.getColumn(), searchPart);
                }
            });
        }
    }
}
