package pro.shushi.pamirs.meta.api.core.session;

import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.meta.api.CommonApiFactory;
import pro.shushi.pamirs.meta.api.Fun;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.definition.fun.FunctionDefinitionManager;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.dto.config.TxConfig;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.common.util.ListUtils;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;
import pro.shushi.pamirs.meta.domain.fun.TransactionConfig;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.domain.model.ModelField;

import java.util.ArrayList;
import java.util.List;

/**
 * session元数据计算器-辅助工具
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:11 下午
 */
public class SessionsHelper {

    private final static FunctionDefinitionManager FUNCTION_DEFINITION_MANAGER = new FunctionDefinitionManager() {
    };

    public static FunctionDefinitionManager fetchFunctionDefinitionManager() {
        return CommonApiFactory.getApi(FunctionDefinitionManager.class, FUNCTION_DEFINITION_MANAGER);
    }

    /**
     * 构造模型配置
     *
     * @param modelDefinition 模型定义
     * @return 模型配置
     */
    public static ModelConfig fetchModelConfig(ModelDefinition modelDefinition) {
        ModelConfig modelConfig = Models.compute().convert(modelDefinition);
        List<ModelFieldConfig> modelFieldConfigList = new ArrayList<>();
        List<Function> functionList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(modelDefinition.getModelFields())) {
            ModelFieldConfig modelFieldConfig;
            for (ModelField modelField : modelDefinition.getModelFields()) {
                modelFieldConfig = convert(modelField);
                modelFieldConfigList.add(modelFieldConfig);
            }
        }
        if (!CollectionUtils.isEmpty(modelDefinition.getFunctions())) {
            FunctionDefinitionManager functionDefinitionManager = fetchFunctionDefinitionManager();
            Function function;
            for (FunctionDefinition functionDefinition : modelDefinition.getFunctions()) {
                function = Fun.generate(functionDefinition);
                functionDefinitionManager.metaAdd(modelConfig, functionList, function);
            }
        }
        modelConfig.setModelFieldConfigList(modelFieldConfigList).setFunctionList(functionList);
        return modelConfig;
    }

    /**
     * 字段配置转换
     *
     * @param modelField 字段定义
     * @return 字段配置
     */
    public static ModelFieldConfig convert(ModelField modelField) {
        return Models.compute().convert(modelField);
    }

    public static TxConfig convert(TransactionConfig transactionConfig) {
        if (null == transactionConfig) {
            return null;
        }
        return new TxConfig().setEnable(transactionConfig.getActive()).setEnableXa(transactionConfig.getEnableXa())
                .setTransactionManager(transactionConfig.getTransactionManager())
                .setNamespace(transactionConfig.getNamespace()).setFun(transactionConfig.getFun())
                .setPropagation(transactionConfig.getPropagation()).setIsolation(transactionConfig.getIsolation())
                .setTimeout(transactionConfig.getTimeout()).setReadOnly(transactionConfig.getReadOnly())
                .setRollbackFor(transactionConfig.getRollbackFor())
                .setRollbackForClassName(transactionConfig.getRollbackForClassName())
                .setNoRollbackFor(transactionConfig.getNoRollbackFor())
                .setNoRollbackForClassName(transactionConfig.getNoRollbackForClassName())
                .setNoRollbackForExpCode(ListUtils.toArray(transactionConfig.getNoRollbackForExpCode()))
                .setRollbackForExpCode(ListUtils.toArray(transactionConfig.getRollbackForExpCode()));
    }

}
