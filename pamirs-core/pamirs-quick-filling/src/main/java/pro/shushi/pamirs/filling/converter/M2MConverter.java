package pro.shushi.pamirs.filling.converter;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.filling.enumeration.QuickFillingExpEnumerate;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.sql.query.QueryWrapper;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.api.session.RequestContext;
import pro.shushi.pamirs.meta.common.exception.PamirsException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Gesi at 9:35 on 2025/9/11
 */
public class M2MConverter extends AbstractValueConverter implements QuickFillingValueConverter {

    public static final QuickFillingValueConverter INSTANCE = new M2MConverter();

    @Override
    public Object convert(QuickFillingContext context, String value) {
        String references = context.getModelFieldConfig().getReferences();
        QueryWrapper<Object> wrapper = Pops.query().from(references);
        int queryNum = fillQueryWrapperCondition(context, references, wrapper, value);
        if (context.isFailed()) {
            return null;
        }
        List<Object> relationList = Models.origin().queryListByWrapper(wrapper);
        if (queryNum != relationList.size()) {
            context.fail("查询结果数量与传入数量不匹配");
            return null;
        }
        return relationList;
    }

    @Override
    protected Object singleValueConvert(QuickFillingContext context, String value) {
        // do nothing.
        return null;
    }

    private int fillQueryWrapperCondition(QuickFillingContext context, String model, QueryWrapper<?> queryWrapper, String value) {
        RequestContext requestContext = PamirsSession.getContext();
        List<String> labelFields = context.getLabelFields();
        if (CollectionUtils.isEmpty(labelFields)) {
            context.fail("未指定选项字段无法进行匹配");
            return 0;
        }
        List<ModelFieldConfig> labelFieldConfigs = new ArrayList<>(labelFields.size());
        for (String labelField : labelFields) {
            ModelFieldConfig modelFieldConfig = requestContext.getModelField(model, labelField);
            if (modelFieldConfig == null) {
                throw PamirsException.construct(QuickFillingExpEnumerate.FIELD_NOT_FIND).appendMsg(labelField + "字段在模型" + model + "内找不到").errThrow();
            }
            labelFieldConfigs.add(modelFieldConfig);
        }

        String[] valueList = value.split(",");
        int relationQueryNum = 0;

        for (String valueItem : valueList) {
            if (StringUtils.isBlank(valueItem)) {
                continue;
            }
            String[] valueSearchPartList = valueItem.split("-");
            if (valueSearchPartList.length == 0) {
                continue;
            }
            if (valueSearchPartList.length > labelFieldConfigs.size()) {
                continue;
            }
            queryWrapper.or(orWrapper -> {
                for (int i = 0; i < valueSearchPartList.length; i++) {
                    ModelFieldConfig relationModelFieldConfig = labelFieldConfigs.get(i);
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
            context.fail();
            return 0;
        }
        return relationQueryNum;
    }
}
