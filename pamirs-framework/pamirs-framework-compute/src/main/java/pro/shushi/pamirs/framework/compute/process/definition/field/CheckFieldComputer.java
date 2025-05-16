package pro.shushi.pamirs.framework.compute.process.definition.field;

import com.alibaba.fastjson.serializer.SerializerFeature;
import pro.shushi.pamirs.framework.compute.process.common.ComputeHelper;
import pro.shushi.pamirs.framework.compute.system.check.spi.api.CheckModelFieldServiceApi;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.compute.context.ComputeContext;
import pro.shushi.pamirs.meta.api.core.compute.definition.FieldComputer;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.api.dto.meta.MetaData;
import pro.shushi.pamirs.meta.api.dto.msg.MessageHub;
import pro.shushi.pamirs.meta.base.common.MetaBaseModel;
import pro.shushi.pamirs.meta.common.constants.ModuleConstants;
import pro.shushi.pamirs.meta.common.constants.NamespaceConstants;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.domain.fun.ComputeDefinition;
import pro.shushi.pamirs.meta.domain.fun.ExpressionDefinition;
import pro.shushi.pamirs.meta.domain.model.ModelField;
import pro.shushi.pamirs.meta.enmu.ComputeSceneEnum;
import pro.shushi.pamirs.meta.util.FieldUtils;
import pro.shushi.pamirs.meta.util.JsonUtils;

import java.util.List;

/**
 * 字段校验
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/19 2:16 上午
 */
@Slf4j
public class CheckFieldComputer<T> implements FieldComputer<Meta, T> {

    @Override
    public Result<Void> compute(ComputeContext context, Meta meta, ModelField field, T data) {
        return MessageHub.closure(() -> {
            MetaData metaData = meta.getData().get(ModuleConstants.MODULE_BASE);
            if (null == metaData) {
                return new Result<>();
            }
            List<ComputeDefinition> checkList = metaData
                    .getComputeDefinitionList(ComputeSceneEnum.VALIDATE, ModelField.MODEL_MODEL, field.getSign());
            List<ExpressionDefinition> ruleList = metaData
                    .getExpressionDefinitionList(ComputeSceneEnum.VALIDATE, ModelField.MODEL_MODEL, field.getSign());
            if (null == checkList && null == ruleList) {
                return new Result<>();
            }
            boolean returnWhenError = context.returnWhenError();
            Boolean isSuccess = Spider.getDefaultExtension(CheckModelFieldServiceApi.class)
                    .check(returnWhenError, field, data, () -> checkList,
                            check -> meta.findFunction(NamespaceConstants.constraint, check),
                            ExpressionDefinition.constructContext(field.getModel(), field.getField()), () -> ruleList);
            return ComputeHelper.generateCheckResult(isSuccess, () -> {
                MetaBaseModel metaBaseModel = ((MetaBaseModel) data);
                String signModel = metaBaseModel.getSignModel();
                String sign = metaBaseModel.getSign();
                Object fieldValue = FieldUtils.getFieldValue(data, field.getLname());
                return "校验签名为" + sign + "的" + signModel + "的定义，字段" + field.getField()
                        + "值：" + JsonUtils.toJSONString(fieldValue, SerializerFeature.PrettyFormat)
                        + "，校验如下：";
            });
        });
    }

}
