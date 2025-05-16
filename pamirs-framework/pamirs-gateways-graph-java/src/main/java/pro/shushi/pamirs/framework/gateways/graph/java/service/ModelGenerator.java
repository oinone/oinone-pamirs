package pro.shushi.pamirs.framework.gateways.graph.java.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.framework.gateways.graph.java.constants.GraphQLSdlConstants;
import pro.shushi.pamirs.framework.gateways.graph.util.GraphQLUtils;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.api.session.RequestContext;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;

import java.util.Objects;

/**
 * 模型生成器
 * <p>
 * 2020/10/20 10:48 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class ModelGenerator implements GraphQLSdlConstants {

    // 生成模型
    public static void generate(StringBuilder graphQLTypeSb, RequestContext requestContext, ModelConfig modelConfig, String wrapperModel, boolean input) {
        if (CollectionUtils.isEmpty(modelConfig.getModelFieldConfigList())) {
            return;
        }
        String type = "模型";
        String suffix = "";
        if (Pagination.MODEL_MODEL.equals(wrapperModel)) {
            suffix = CAPITAL_PAGE;
            type = "分页模型";
        } else if (IWrapper.MODEL_MODEL.equals(wrapperModel)) {
            suffix = COND_INPUT;
            type = "条件模型";
        } else if (input) {
            suffix = CAPITAL_INPUT;
            type = "请求模型";
        }
        SummaryGenerator.generate(graphQLTypeSb, modelConfig.getDisplayName(), type, modelConfig.getSummary(), null);
        String capitalizeName = StringUtils.capitalize(modelConfig.getName());
        graphQLTypeSb.append(input ? INPUT : TYPE).append(StringUtils.SPACE).append(capitalizeName).append(suffix);
        graphQLTypeSb.append(StringUtils.SPACE).append("{").append(StringUtils.LF);
        if (null != wrapperModel) {
            ModelConfig wrapperModelConfig = Objects.requireNonNull(PamirsSession.getContext()).getModelConfig(wrapperModel);
            if (Pagination.MODEL_MODEL.equals(wrapperModel)) {
                graphQLTypeSb.append(StringUtils.SPACE);
                SummaryGenerator.generate(graphQLTypeSb, "内容", "字段", "查询结果列表", null);
                graphQLTypeSb.append(StringUtils.SPACE).append(CONTENT).append(":").append("[").append(capitalizeName).append("]").append(StringUtils.LF);
            } else if (IWrapper.MODEL_MODEL.equals(wrapperModel)) {
                graphQLTypeSb.append(StringUtils.SPACE);
                SummaryGenerator.generate(graphQLTypeSb, "条件", "字段", "条件", null);
                graphQLTypeSb.append(StringUtils.SPACE).append(UPDATE_ENTITY).append(":").append(capitalizeName).append(CAPITAL_INPUT).append(StringUtils.LF);
            }
            generateFields(graphQLTypeSb, requestContext, wrapperModelConfig, input);
        } else {
            generateFields(graphQLTypeSb, requestContext, modelConfig, input);
        }
        graphQLTypeSb.append("}").append(StringUtils.LF);
    }

    // 生成字段
    private static void generateFields(StringBuilder modelGraphqlTypeSb, RequestContext requestContext, ModelConfig modelConfig, boolean input) {
        for (ModelFieldConfig fieldConfig : modelConfig.getModelFieldConfigList()) {
            modelGraphqlTypeSb.append(StringUtils.SPACE);
            SummaryGenerator.generate(modelGraphqlTypeSb, fieldConfig.getDisplayName(), "字段", fieldConfig.getSummary(), fieldConfig.getInvisible());
            StringBuilder fieldString = new StringBuilder(StringUtils.SPACE).append(fieldConfig.getName()).append(":").append(StringUtils.SPACE);
            String ttype = fieldConfig.getTtype();
            if (TtypeEnum.isRelatedType(ttype)) {
                ttype = fieldConfig.getRelatedTtype();
            }
            fieldString.append(GraphQLUtils.fetchGraphqlType(requestContext, modelConfig.getName(),
                    ttype, fieldConfig.getLtype(), fieldConfig.getSize(), fieldConfig.getMulti(),
                    fieldConfig.getReferences(), fieldConfig.getDictionary(), fieldConfig.getRequestSerialize(), input));
            fieldString.append(StringUtils.LF);
            modelGraphqlTypeSb.append(fieldString);
        }
    }

}
