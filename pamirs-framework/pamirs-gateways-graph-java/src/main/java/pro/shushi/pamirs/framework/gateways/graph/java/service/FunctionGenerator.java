package pro.shushi.pamirs.framework.gateways.graph.java.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.framework.gateways.graph.java.constants.GraphQLSdlConstants;
import pro.shushi.pamirs.framework.gateways.graph.util.GraphQLUtils;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.fun.Arg;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.api.dto.fun.VarType;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.api.session.RequestContext;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.constants.NamespaceConstants;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 函数生成器
 * <p>
 * 2020/10/20 10:46 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class FunctionGenerator implements GraphQLSdlConstants {

    // 生成函数
    public static void generate(StringBuilder modelGraphqlTypeSb, RequestContext requestContext,
                                Set<String> runModuleSet, ModelConfig modelConfig) {
        if (CollectionUtils.isEmpty(modelConfig.getFunctionList())
                || ModelTypeEnum.ABSTRACT.value().equals(modelConfig.getType().value()) && !modelConfig.getModel().equals(NamespaceConstants.pamirs)
        ) {
            return;
        }
        generateQueryOrMutation(modelGraphqlTypeSb, requestContext, runModuleSet, modelConfig, Boolean.TRUE);
        generateQueryOrMutation(modelGraphqlTypeSb, requestContext, runModuleSet, modelConfig, Boolean.FALSE);
    }

    private static void generateQueryOrMutation(StringBuilder modelGraphqlTypeSb, RequestContext requestContext,
                                                Set<String> runModuleSet, ModelConfig modelConfig, boolean query) {
        List<Function> functionList = new ArrayList<>();
        for (Function function : modelConfig.getFunctionList()) {
            if (!runModuleSet.contains(function.getModule())) {
                continue;
            }
            if ((FunctionTypeEnum.QUERY.in(function.getType()) != query) || !FunctionOpenEnum.API.in(function.getOpen())) {
                continue;
            }
            functionList.add(function);
        }
        if (CollectionUtils.isEmpty(functionList)) {
            return;
        }
        String type = (query ? QUERY_DISPLAY_NAME : MUTATION_DISPLAY_NAME);
        SummaryGenerator.generate(modelGraphqlTypeSb, modelConfig.getDisplayName(), type, modelConfig.getSummary() + type, null);
        modelGraphqlTypeSb.append(TYPE).append(StringUtils.SPACE).append(StringUtils.capitalize(modelConfig.getName()))
                .append(query ? CAPITAL_QUERY : CAPITAL_MUTATION)
                .append(StringUtils.SPACE).append("{").append(StringUtils.LF);
        for (Function function : functionList) {
            // 处理方法名和入参
            List<String> argStrings = new ArrayList<>();
            modelGraphqlTypeSb.append(StringUtils.SPACE);
            SummaryGenerator.generate(modelGraphqlTypeSb, function.getDisplayName(), null, function.getSummary(), null);
            modelGraphqlTypeSb.append(StringUtils.SPACE).append(function.getName());
            if (!CollectionUtils.isEmpty(function.getArguments())) {
                for (Arg arg : function.getArguments()) {
                    StringBuilder argString = new StringBuilder().append(arg.getName()).append(":");
                    if (IWrapper.MODEL_MODEL.equals(arg.getModel())) {
                        argString.append(StringUtils.capitalize(modelConfig.getName())).append(COND_INPUT);
                    } else {
                        argString.append(GraphQLUtils.fetchGraphqlType(requestContext, modelConfig.getName(),
                                arg.getTtype(), arg.getLtype(), arg.getSize(), arg.getMulti(),
                                arg.getModel(), arg.getDictionary(), null, Boolean.TRUE));
                    }
                    argStrings.add(argString.toString());
                }
                String argsString = StringUtils.join(argStrings, CharacterConstants.SEPARATOR_COMMA);
                modelGraphqlTypeSb.append("(").append(argsString).append(")");
            }

            modelGraphqlTypeSb.append(":");
            // 处理返回值类型
            VarType returnType = function.getReturnType();
            if (StringUtils.isNotBlank(returnType.getModel()) && Pagination.MODEL_MODEL.equals(returnType.getModel())) {
                modelGraphqlTypeSb.append(StringUtils.capitalize(modelConfig.getName())).append(CAPITAL_PAGE);
            } else {
                modelGraphqlTypeSb.append(GraphQLUtils.fetchGraphqlType(requestContext, modelConfig.getName(),
                        returnType.getTtype(), returnType.getLtype(), returnType.getSize(), returnType.getMulti(),
                        returnType.getModel(), returnType.getDictionary(), null, Boolean.FALSE));
            }
            modelGraphqlTypeSb.append(StringUtils.LF);
        }
        modelGraphqlTypeSb.append("}").append(StringUtils.LF);
    }

}
