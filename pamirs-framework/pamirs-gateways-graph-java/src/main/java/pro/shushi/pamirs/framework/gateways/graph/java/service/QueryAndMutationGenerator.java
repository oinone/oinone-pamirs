package pro.shushi.pamirs.framework.gateways.graph.java.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.framework.gateways.graph.java.constants.GraphQLSdlConstants;
import pro.shushi.pamirs.meta.api.CommonApiFactory;
import pro.shushi.pamirs.meta.api.core.definition.fun.FunctionDefinitionManager;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;

import java.util.Set;

/**
 * Query And Mutation 生成器
 * <p>
 * 2020/10/20 10:40 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class QueryAndMutationGenerator implements GraphQLSdlConstants {

    // 生成query和mutation
    public static void generate(StringBuilder queryBuilder, StringBuilder mutationBuilder,
                                Set<String> runModuleSet, ModelConfig modelConfig) {
        String modelTypeName = StringUtils.capitalize(modelConfig.getName());
        int queryCount = 0;
        int mutationCount = 0;
        for (Function function : modelConfig.getFunctionList()) {
            if (!runModuleSet.contains(function.getModule())) {
                continue;
            }
            if (!CommonApiFactory.getApi(FunctionDefinitionManager.class).canClientInvoke(modelConfig, function)) {
                continue;
            }
            if (null != function.getType() && function.getType().contains(FunctionTypeEnum.QUERY)) {
                queryCount++;
            } else {
                mutationCount++;
            }
        }
        if (!CollectionUtils.isEmpty(modelConfig.getFunctionList()) && queryCount != 0) {
            SummaryGenerator.generate(queryBuilder, modelConfig.getDisplayName() + QUERY_DISPLAY_NAME, null, modelConfig.getSummary() + QUERY_DISPLAY_NAME, null);
            queryBuilder.append(StringUtils.SPACE).append(modelConfig.getName()).append(CAPITAL_QUERY).append(":").append(modelTypeName).append(CAPITAL_QUERY).append("!").append(StringUtils.LF);
        }
        if (!CollectionUtils.isEmpty(modelConfig.getFunctionList()) && mutationCount != 0) {
            SummaryGenerator.generate(mutationBuilder, modelConfig.getDisplayName() + MUTATION_DISPLAY_NAME, null, modelConfig.getSummary() + MUTATION_DISPLAY_NAME, null);
            mutationBuilder.append(StringUtils.SPACE).append(modelConfig.getName()).append(CAPITAL_MUTATION).append(":").append(modelTypeName).append(CAPITAL_MUTATION).append("!").append(StringUtils.LF);
        }
    }

}
