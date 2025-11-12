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
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.exception.PamirsException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

        List<String> valueList = Arrays.stream(value.split(CharacterConstants.SEPARATOR_COMMA)).map(String::trim).filter(StringUtils::isNotBlank).collect(Collectors.toList());
        if (valueList.isEmpty()) {
            context.fail("没有有效值可以进行匹配");
            return 0;
        }
        // FIXME: zbh 20251112 此处不允许使用 or 拼接，后续需要改成多次匹配查询
        ModelFieldConfig labelFieldConfig = labelFieldConfigs.get(0);
        queryWrapper.in(labelFieldConfig.getColumn(), valueList);
        return valueList.size();
    }
}
