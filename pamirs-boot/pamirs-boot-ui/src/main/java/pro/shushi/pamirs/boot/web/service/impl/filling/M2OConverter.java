package pro.shushi.pamirs.boot.web.service.impl.filling;

import com.alibaba.fastjson.TypeReference;
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
import pro.shushi.pamirs.meta.util.JsonUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Gesi at 9:35 on 2025/9/11
 */
@Service
public class M2OConverter extends AbstractValueConverter implements QuickFillingValueConverter {

    private static final TypeReference<List<Map<String, String>>> ADDRESS_VALUE_TYPE_REFERENCE = new TypeReference<List<Map<String, String>>>() {
    };

    private static final String ADDRESS_MODEL = "resource.ResourceAddress";

    @Override
    public boolean canTransform(TtypeEnum ttype) {
        return TtypeEnum.M2O.equals(ttype) || TtypeEnum.O2O.equals(ttype);
    }

    @Override
    public Object transformObjectValue(QuickFillingField quickFillingField, String value, QuickFillingFailureDetail failureDetail) {
        ModelFieldConfig modelFieldConfig = quickFillingField.getModelConfigField();
        if (StringUtils.isBlank(value)) {
            return getFieldCollection(modelFieldConfig);
        }
        QueryWrapper<Object> relationQueryWrapper = getRelationQueryWrapper(quickFillingField, false);
        String relationModel = relationQueryWrapper.getModel();

        if (ADDRESS_MODEL.equals(relationModel)) {
            relationQueryWrapper.and(andWrapper -> {
                fillAddressQueryWrapperCondition(andWrapper, quickFillingField, value, failureDetail);
            });
        } else {
            relationQueryWrapper.and(andWrapper -> {
                fillQueryWrapperCondition(relationModel, andWrapper, quickFillingField, value, failureDetail);
            });
        }

        if (failureDetail.isFailed()) {
            return null;
        }

        List<Object> relationList = Models.origin().queryListByWrapper(relationQueryWrapper);
        if (CollectionUtils.isEmpty(relationList)) {
            failureDetail.fail(QuickFillingFailCodeEnum.QUERY_NUMBER_NOT_MATCH, "查询结果数量与传入数量不匹配");
            return null;
        } else if (relationList.size() != 1) {
            failureDetail.fail(QuickFillingFailCodeEnum.QUERY_TOO_MANY_NUMBER, "查询到多条数据");
            return null;
        }
        return relationList.get(0);
    }

    private void fillQueryWrapperCondition(String relationModel, QueryWrapper<Object> queryWrapper, QuickFillingField quickFillingField, String value, QuickFillingFailureDetail failureDetail) {
        ModelConfig relationModelConfig = PamirsSession.getContext().getModelConfig(relationModel);

        List<String> relationSelectFields = quickFillingField.getRelationSelectFields();
        List<ModelFieldConfig> relationSelectFieldConfigs = new ArrayList<>(relationSelectFields.size());
        for (String relationSelectField : relationSelectFields) {
            ModelFieldConfig relationModelFieldConfig =
                    relationModelConfig.getModelFieldConfigList().stream().filter(i -> StringUtils.equals(i.getField(), relationSelectField)).collect(Collectors.toList()).get(0);
            relationSelectFieldConfigs.add(relationModelFieldConfig);
        }

        String[] valueSearchPartList = value.split("-");
        if (StringUtils.isBlank(value) || valueSearchPartList.length == 0) {
            failureDetail.fail(QuickFillingFailCodeEnum.TYPE_INCOMPATIBLE);
            return;
        }

        if (valueSearchPartList.length > relationSelectFieldConfigs.size()) {
            failureDetail.fail(QuickFillingFailCodeEnum.TYPE_INCOMPATIBLE);
            return;
        }
        for (int i = 0; i < valueSearchPartList.length; i++) {
            ModelFieldConfig relationModelFieldConfig = relationSelectFieldConfigs.get(i);
            String searchPart = valueSearchPartList[i];
            if (StringUtils.isNotBlank(searchPart)) {
                queryWrapper.eq(relationModelFieldConfig.getColumn(), searchPart);
            } else {
                queryWrapper.isNull(relationModelFieldConfig.getColumn());
            }
        }
    }

    private void fillAddressQueryWrapperCondition(QueryWrapper<Object> queryWrapper, QuickFillingField quickFillingField, String value, QuickFillingFailureDetail failureDetail) {
        List<Map<String, String>> addressValueList = JsonUtils.parseObject(value, ADDRESS_VALUE_TYPE_REFERENCE);
        ModelConfig modelConfig = PamirsSession.getContext().getModelConfig(ADDRESS_MODEL);
        boolean isAllAddressValueNull = true;
        for (Map<String, String> addressValueMap : addressValueList) {
            String addressField = addressValueMap.get("field");
            String addressValue = addressValueMap.get("value");
            ModelFieldConfig modelFieldConfig =
                    modelConfig.getModelFieldConfigList().stream().filter(i -> StringUtils.equals(i.getField(), addressField)).collect(Collectors.toList()).get(0);
            if (StringUtils.isNotBlank(addressValue)) {
                queryWrapper.eq(modelFieldConfig.getColumn(), addressValue);
                isAllAddressValueNull = false;
            }
        }
        if (isAllAddressValueNull) {
            failureDetail.fail(QuickFillingFailCodeEnum.TYPE_INCOMPATIBLE);
        }
    }
}
