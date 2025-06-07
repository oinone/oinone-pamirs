package pro.shushi.pamirs.framework.gateways.graph.util;

import graphql.Scalars;
import graphql.execution.ExecutionPath;
import graphql.schema.*;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.framework.gateways.graph.PamirsScalars;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.CommonApiFactory;
import pro.shushi.pamirs.meta.api.core.compute.systems.type.TypeProcessor;
import pro.shushi.pamirs.meta.api.core.orm.path.ClientExecutionPath;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.session.RequestContext;
import pro.shushi.pamirs.meta.base.BaseModel;
import pro.shushi.pamirs.meta.common.enmu.BaseEnum;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.domain.model.DataDictionary;

import static pro.shushi.pamirs.framework.gateways.graph.containts.GraphQLSdlConstants.CAPITAL_MUTATION;
import static pro.shushi.pamirs.framework.gateways.graph.containts.GraphQLSdlConstants.CAPITAL_QUERY;
import static pro.shushi.pamirs.framework.gateways.graph.enmu.GqlExpEnumerate.BASE_DICTIONARY_CONFIG_ERROR;
import static pro.shushi.pamirs.framework.gateways.graph.enmu.GqlExpEnumerate.BASE_REFERENCE_MODEL_ERROR;
import static pro.shushi.pamirs.meta.annotation.Field.serialize.NON;
import static pro.shushi.pamirs.meta.api.core.compute.systems.type.TypeProcessor.*;
import static pro.shushi.pamirs.meta.enmu.TtypeEnum.*;

/**
 * GraphQL工具类
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/17 1:31 上午
 */
@Slf4j
public class GraphQLUtils {

    public static ClientExecutionPath convertPath(ExecutionPath path) {
        return ClientExecutionPath.fromList(path.toList());
    }

    public static GraphQLScalarType fetchBasicGraphql(String ttype, Integer size) {
        return BaseEnum.<String, GraphQLScalarType>switchGet(ttype, caseValue(),
                cases(BINARY).to(() -> PamirsScalars.GraphQLByte),
                cases(FLOAT, INTEGER, UID).to(() -> fetchNumberGraph(ttype, size)),
                cases(BOOLEAN).to(() -> PamirsScalars.GraphQLBoolean),
                cases(STRING, TEXT, PHONE, EMAIL).to(() -> PamirsScalars.GraphQLString),
                cases(HTML).to(() -> PamirsScalars.GraphQLHtml),
                cases(MONEY).to(() -> PamirsScalars.GraphQLMoney),
                cases(DATETIME, YEAR, DATE, TIME).to(() -> PamirsScalars.GraphQLDate),
                cases(VOID).to(() -> PamirsScalars.GraphQLVoid),
                cases(OBJ).to(() -> PamirsScalars.GraphQLObject),
                cases(MAP).to(() -> PamirsScalars.GraphQLMap),
                cases(RELATED, ENUM, O2O, O2M, M2O, M2M),
                defaults()
        );
    }

    public static String fetchBasicGraphqlType(String ttype, Integer size) {
        GraphQLScalarType scalarType = fetchBasicGraphql(ttype, size);
        if (scalarType == null) {
            return null;
        }
        return scalarType.getName();
    }

    public static GraphQLScalarType fetchNumberGraph(String ttype, final Integer size) {
        return BaseEnum.<String, GraphQLScalarType>switchGet(ttype, caseValue(),
                cases(INTEGER).to(() -> {
                    int computeSize = isNullOrNegative(size) ? DEFAULT_BIGINT : size;
                    if (computeSize <= DEFAULT_SHORT) {
                        return PamirsScalars.GraphQLShort;
                    } else if (computeSize <= DEFAULT_INTEGER) {
                        return PamirsScalars.GraphQLInt;
//                } else if (size <= GRAPH_BIGINT_LIMIT) {
//                    return Scalars.GraphQLLong.getName();
                    } else if (computeSize <= DEFAULT_BIGINT_LIMIT) {
                        return PamirsScalars.GraphQLLong;
                    } else {
                        return PamirsScalars.GraphQLBigDecimal;
                    }
                }),
                cases(FLOAT).to(() -> {
                    int computeSize = isNullOrNegative(size) ? DEFAULT_DOUBLE : size;
                    if (computeSize <= DEFAULT_FLOAT) {
                        return PamirsScalars.GraphQLFloat;
                    } else if (computeSize <= DEFAULT_DOUBLE) {
                        return PamirsScalars.GraphQLFloat;
                    } else {
                        return PamirsScalars.GraphQLBigDecimal;
                    }
                }),
                cases(UID).to(() -> PamirsScalars.GraphQLLong),
                defaults()
        );
    }

    public static String fetchNumberGraphType(String ttype, final Integer size) {
        GraphQLScalarType scalarType = fetchNumberGraph(ttype, size);
        if (scalarType == null) {
            return null;
        }
        return scalarType.getName();
    }

    @SuppressWarnings("unused")
    public static String fetchGraphqlType(RequestContext requestContext, String modelName, String ttype, String ltype, Integer size, Boolean multi,
                                          String referenceModel, String dictionary, String serialize, boolean isInput) {
        TypeProcessor typeProcessor = CommonApiFactory.getTypeProcessor();
        size = typeProcessor.fetchDefaultSizeForInteger(ltype, size);
        size = typeProcessor.fetchDefaultSizeForFloat(ltype, size);
        String basicGraphQL = fetchBasicGraphqlType(ttype, size);
        if (StringUtils.isBlank(basicGraphQL)) {
            String referenceName = "";
            if (typeProcessor.isRelationField(ttype) || typeProcessor.isRelatedField(ttype)) {
                if (StringUtils.isBlank(referenceModel)) {
                    referenceName = modelName;
                } else {
                    ModelConfig modelConfig = requestContext.getModelConfig(referenceModel);
                    if (null == modelConfig) {
                        log.error("模型[字段关联的模型]未配置或被删除, 模型modelName:" + modelName + ", 字段关联模型referenceModel:" + referenceModel);
                        throw PamirsException.construct(BASE_REFERENCE_MODEL_ERROR)
                                .appendMsg("modelName:" + modelName + ", referenceModel:" + referenceModel).errThrow();
                    }
                    referenceName = modelConfig.getName();
                }
                if (isInput) {
                    referenceName = referenceName + "Input";
                }
            } else if (typeProcessor.isEnumField(ttype)) {
                DataDictionary dataDictionary = requestContext.getDictionary(dictionary);
                if (null == dataDictionary) {
                    log.error("模型[字段关联的字典]未配置或被删除, 模型modelName:" + modelName + ", 字段关联字典dictionary:" + dictionary);
                    throw PamirsException.construct(BASE_DICTIONARY_CONFIG_ERROR)
                            .appendMsg("modelName:" + modelName + ", dictionary:" + dictionary).errThrow();
                }
                referenceName = dataDictionary.getName();
            }
            basicGraphQL = StringUtils.capitalize(referenceName);
        }
        if ((BaseModel.class.getSimpleName() + (isInput ? "Input" : "")).equals(basicGraphQL)) {
            basicGraphQL = StringUtils.capitalize(modelName);
            if (isInput) {
                basicGraphQL = basicGraphQL + "Input";
            }
        }
        String result;
        if (StringUtils.isNotBlank(serialize) && !NON.equals(serialize) && !Long.class.getName().equals(ltype)) {
            return Scalars.GraphQLString.getName();
        }
        if (null != multi && multi) {
            result = "[" + basicGraphQL + "]";
        } else {
            result = basicGraphQL;
        }
        return result;
    }

    private static boolean isNullOrNegative(Integer value) {
        return null == value || value <= 0;
    }

    public static String toModelName(String queryOrMutationType) {
        if (queryOrMutationType.endsWith(CAPITAL_QUERY)) {
            return queryOrMutationType.substring(0, queryOrMutationType.lastIndexOf(CAPITAL_QUERY));
        } else if (queryOrMutationType.endsWith(CAPITAL_MUTATION)) {
            return queryOrMutationType.substring(0, queryOrMutationType.lastIndexOf(CAPITAL_MUTATION));
        }
        return queryOrMutationType;
    }

    public static GraphQLFieldDefinition fetchFieldDefinition(GraphQLFieldDefinition typeDefinition, String fieldName) {
        GraphQLUnmodifiedType unmodifiedType = GraphQLTypeUtil.unwrapAll(typeDefinition.getType());
        if (!(unmodifiedType instanceof GraphQLOutputType)) {
            return null;
        }
        GraphQLOutputType unwrappedType = (GraphQLOutputType) unmodifiedType;
        if (unwrappedType instanceof GraphQLObjectType) {
            return ((GraphQLObjectType) unwrappedType).getFieldDefinition(fieldName);
        }
        return null;
    }
}
