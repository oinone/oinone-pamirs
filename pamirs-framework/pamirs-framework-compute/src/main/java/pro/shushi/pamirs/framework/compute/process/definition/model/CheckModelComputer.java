package pro.shushi.pamirs.framework.compute.process.definition.model;

import pro.shushi.pamirs.framework.compute.process.common.ComputeHelper;
import pro.shushi.pamirs.framework.compute.system.check.spi.api.CheckModelServiceApi;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.compute.context.ComputeContext;
import pro.shushi.pamirs.meta.api.core.compute.definition.ModelComputer;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.api.dto.msg.MessageHub;
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

/**
 * 模型约束函数
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/19 2:16 上午
 */
@Slf4j
@SPI.Service
public class CheckModelComputer<T> implements ModelComputer<Meta, T> {

    @Override
    public Result<Void> compute(ComputeContext context, Meta meta, String model, T data, Map<String, Object> computeContext) {
        return MessageHub.closure(() -> {
            ModelDefinition modelDefinition = meta.getModel(model);
            List<ComputeDefinition> checkList = meta.getCurrentModuleData()
                    .getComputeDefinitionList(ComputeSceneEnum.VALIDATE, ModelDefinition.MODEL_MODEL, modelDefinition.getSign());
            List<ExpressionDefinition> ruleList = meta.getCurrentModuleData()
                    .getExpressionDefinitionList(ComputeSceneEnum.VALIDATE, ModelDefinition.MODEL_MODEL, modelDefinition.getSign());
            if (null == checkList && null == ruleList) {
                return new Result<>();
            }
            boolean returnWhenError = context.returnWhenError();
            Boolean isSuccess = Spider.getDefaultExtension(CheckModelServiceApi.class)
                    .check(returnWhenError, modelDefinition, data, () -> checkList,
                            check -> {
                                FunctionDefinition function = meta.findFunction(modelDefinition.getModel(), check);
                                if (null == function) {
                                    function = meta.findFunction(NamespaceConstants.constraint, check);
                                }
                                return function;
                            }, ExpressionDefinition.constructContext(modelDefinition.getModel()), () -> ruleList);
            return ComputeHelper.generateCheckResult(isSuccess, () -> {
                String validateModel = ((ModelDefinition) data).getModel();
                return "校验签名为" + validateModel + "的模型定义，校验如下：";
            });
        });
    }

}
