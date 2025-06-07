package pro.shushi.pamirs.framework.compute.system.check.spi.service;

import pro.shushi.pamirs.framework.compute.system.check.spi.api.CheckModelServiceApi;
import pro.shushi.pamirs.framework.compute.system.check.util.CheckHelper;
import pro.shushi.pamirs.meta.api.core.compute.systems.constraint.CheckProcessor;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.constants.NamespaceConstants;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.domain.fun.ComputeDefinition;
import pro.shushi.pamirs.meta.domain.fun.ExpressionDefinition;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.enmu.ComputeSceneEnum;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 模型校验
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/19 2:16 上午
 */
@SPI.Service
public class CheckModelService implements CheckModelServiceApi {

    @Override
    public Boolean check(boolean returnWhenError, String model, Object data) {
        ModelDefinition modelDefinition = PamirsSession.getContext().getModelConfig(model).getModelDefinition();
        return check(returnWhenError, modelDefinition, data);
    }

    @Override
    public Boolean check(boolean returnWhenError, ModelDefinition modelDefinition, Object data) {
        return check(returnWhenError, modelDefinition, data, check -> {
            pro.shushi.pamirs.meta.api.dto.fun.Function function = PamirsSession.getContext()
                    .getFunctionAllowNull(modelDefinition.getModel(), check);
            if (null == function) {
                function = PamirsSession.getContext()
                        .findFunction(NamespaceConstants.constraint, check);
            }
            return function.getFunctionDefinition();
        }, ExpressionDefinition.constructContext(modelDefinition.getModel()));
    }

    @Override
    public Boolean check(boolean returnWhenError, ModelDefinition modelDefinition, Object data,
                         Function<String, FunctionDefinition> findCheckFunctionConsumer,
                         Map<String, Object> expressionContext) {
        // 校验
        List<ComputeDefinition> checkList = CheckHelper.fetchModelChecker(modelDefinition.getModel(),
                sign -> PamirsSession.getContext().getComputeDefinitionList(ComputeSceneEnum.VALIDATE, ModelDefinition.MODEL_MODEL, sign));
        List<ExpressionDefinition> ruleList = CheckHelper.fetchModelChecker(modelDefinition.getModel(),
                sign -> PamirsSession.getContext().getExpressionDefinitionList(ComputeSceneEnum.VALIDATE, ModelDefinition.MODEL_MODEL, sign));
        return check(returnWhenError, modelDefinition, data, () -> checkList, findCheckFunctionConsumer, expressionContext, () -> ruleList);
    }

    @Override
    public Boolean check(boolean returnWhenError, ModelDefinition modelDefinition, Object data,
                         Supplier<List<ComputeDefinition>> checksSupplier,
                         Function<String, FunctionDefinition> findCheckFunctionConsumer,
                         Map<String, Object> expressionContext,
                         Supplier<List<ExpressionDefinition>> expressionsSupplier) {
        // 校验
        return Spider.getDefaultExtension(CheckProcessor.class).check(returnWhenError, modelDefinition.getModel(), null,
                data, checksSupplier, findCheckFunctionConsumer, expressionContext, expressionsSupplier);
    }

}
