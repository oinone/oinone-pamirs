package pro.shushi.pamirs.framework.compute.system.check.spi.service;

import pro.shushi.pamirs.framework.compute.system.check.spi.api.CheckModelFieldServiceApi;
import pro.shushi.pamirs.framework.compute.system.check.util.CheckHelper;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.compute.systems.constraint.CheckProcessor;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.constants.NamespaceConstants;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.constant.ContextConstants;
import pro.shushi.pamirs.meta.domain.fun.ComputeDefinition;
import pro.shushi.pamirs.meta.domain.fun.ExpressionDefinition;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;
import pro.shushi.pamirs.meta.domain.model.ModelField;
import pro.shushi.pamirs.meta.enmu.ComputeSceneEnum;
import pro.shushi.pamirs.meta.util.FieldUtils;
import pro.shushi.pamirs.meta.util.JsonUtils;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 字段校验
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/19 2:16 上午
 */
@Slf4j
@SPI.Service
public class CheckModelFieldService implements CheckModelFieldServiceApi {

    @Override
    public Boolean check(boolean returnWhenError, ModelFieldConfig field, Object data) {
        return check(returnWhenError, field, data, null);
    }

    @Override
    public Boolean check(boolean returnWhenError, ModelFieldConfig field, Object data, String fun) {
        Map<String, Object> finalExpressionContext = ExpressionDefinition.constructContext(field.getModel(), field.getField());
        if (null != fun) {
            // 暂未使用fun
            finalExpressionContext.put(ContextConstants.FUN, fun);
        }
        return check(returnWhenError, field.getModelField(), data,
                (check) -> PamirsSession.getContext().findFunction(NamespaceConstants.constraint, check).getFunctionDefinition(),
                finalExpressionContext);
    }

    @Override
    public Boolean check(boolean returnWhenError, ModelField modelField, Object data, Function<String, FunctionDefinition> findCheckFunctionConsumer,
                         Map<String, Object> expressionContext) {
        // 校验
        List<ComputeDefinition> checkList = CheckHelper.fetchModelFieldChecker(modelField.getModel(), modelField.getField(),
                sign -> PamirsSession.getContext().getComputeDefinitionList(ComputeSceneEnum.VALIDATE, ModelField.MODEL_MODEL, sign));
        List<ExpressionDefinition> ruleList = CheckHelper.fetchModelFieldChecker(modelField.getModel(), modelField.getField(),
                sign -> PamirsSession.getContext().getExpressionDefinitionList(ComputeSceneEnum.VALIDATE, ModelField.MODEL_MODEL, sign));
        return check(returnWhenError, modelField, data, () -> checkList, findCheckFunctionConsumer, expressionContext, () -> ruleList);
    }

    @Override
    public Boolean check(boolean returnWhenError, ModelField modelField, Object data,
                         Supplier<List<ComputeDefinition>> checksSupplier,
                         Function<String, FunctionDefinition> findCheckFunctionConsumer,
                         Map<String, Object> expressionContext,
                         Supplier<List<ExpressionDefinition>> expressionsSupplier) {
        // 组装上下文
        Object fieldValue = null;
        if (null != data) {
            fieldValue = FieldUtils.getFieldValue(data, modelField.getLname());
        }
        // 校验
        try {
            return Spider.getDefaultExtension(CheckProcessor.class).check(returnWhenError, modelField.getModel(), modelField.getField(), fieldValue,
                    checksSupplier, findCheckFunctionConsumer, expressionContext, expressionsSupplier);
        } catch (PamirsException e) {
            log.error("Error Data: " + JsonUtils.toJSONString(data, true));
            throw e;
        }
    }

}
