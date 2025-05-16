package pro.shushi.pamirs.framework.connectors.data.elastic.rsql;

import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.framework.gateways.constant.RsqlCharacterConstant;
import pro.shushi.pamirs.framework.gateways.constant.RsqlConstant;
import pro.shushi.pamirs.framework.gateways.convert.RsqlValueConverter;
import pro.shushi.pamirs.framework.gateways.rsql.RsqlSearchOperation;
import pro.shushi.pamirs.framework.gateways.rsql.enmu.RsqlExpEnumerate;
import pro.shushi.pamirs.framework.gateways.util.BooleanHelper;
import pro.shushi.pamirs.meta.api.CommonApiFactory;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.api.session.RequestContext;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.enmu.BaseEnum;
import pro.shushi.pamirs.meta.common.enmu.IEnum;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.domain.model.DataDictionary;
import pro.shushi.pamirs.meta.enmu.SerializeEnum;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static pro.shushi.pamirs.framework.gateways.rsql.enmu.RsqlExpEnumerate.BASE_ARGUMENTS_ERROR;

/**
 * ElasticRSQLSpec
 *
 * @author yakir on 2022/09/08 11:32.
 * {@link pro.shushi.pamirs.framework.gateways.rsql.RsqlSpecification}
 */
public class ElasticRSQLSpec {

    private String             originProperty;
    private String             property;
    private ComparisonOperator operator;
    private List<String>       arguments;
    private ElasticRSQLQuery   query;
    private ModelConfig        model;

    private int propertyIndex;

    private final boolean isJsonExtractFunction;
    private final boolean isJsonContainsFunction;
    private final boolean isJsonSearchFunction;
    private final boolean isJsonFunction;
    private final boolean needFormatProperty;


    public ElasticRSQLSpec(String property, ComparisonOperator operator, List<String> arguments, ModelConfig model) {
        this.originProperty = property;
        this.property       = property;
        //rsql domain
        this.operator  = operator;
        this.arguments = arguments;
//        if (isRelatedField(property)) {
        // todo 暂不考虑关联查询

        //     RsqlRelation rsqlRelation = new RsqlRelation(property, operator, arguments, model);
        //     this.property      = rsqlRelation.getProperty();
        //     this.operator      = rsqlRelation.getOperator();
        //     this.arguments     = rsqlRelation.getArguments();
        //     this.propertyIndex = rsqlRelation.getPropertyIndex();
        // } else {
//            this.propertyIndex = -1;
//        }
        this.isJsonExtractFunction  = this.property.startsWith(RsqlConstant.JSON_EXTRACT_FUNCTION);
        this.isJsonContainsFunction = this.property.startsWith(RsqlConstant.JSON_CONTAINS_FUNCTION);
        this.isJsonSearchFunction   = this.property.startsWith(RsqlConstant.JSON_SEARCH_FUNCTION);
        this.isJsonFunction         = this.isJsonExtractFunction || this.isJsonContainsFunction || this.isJsonSearchFunction;
        this.needFormatProperty     = this.isJsonContainsFunction || this.isJsonSearchFunction;
        this.query                  = new ElasticRSQLQuery();
        this.model                  = model;
    }

    public ElasticRSQLQuery toQuery() {
        List<Object> args     = castArguments(property, model);
        Object       argument = args.get(0);
        query.setArg(originProperty);
        RsqlSearchOperation operation = RsqlSearchOperation.getSimpleOperator(operator);
        if (operation == null) {
            throw PamirsException.construct(RsqlExpEnumerate.BASE_NO_SUPPORT_OPERATION_RSQL_ERROR)
                    .errThrow();
        }
        switch (operation) {
            case GREATER_THAN: {
                if (isJsonFunction) {
                    if (!isJsonExtractFunction) {
                        throw PamirsException.construct(RsqlExpEnumerate.BASE_NO_SUPPORT_OPERATION_RSQL_ERROR)
                                .errThrow();
                    }
                }
                query.getParamValues().add(args);
                return query.greaterThan(makeVariable(argument));
            }
            case EQUAL: {
                if (isJsonFunction) {
                    if (isJsonContainsFunction) {
                        return query.equal(true);
                    }
                    if (!isJsonExtractFunction) {
                        throw PamirsException.construct(RsqlExpEnumerate.BASE_NO_SUPPORT_OPERATION_RSQL_ERROR)
                                .errThrow();
                    }
                }
                if (argument == null) {
                    return query.isNull();
                } else {
                    return query.equal(makeVariable(argument));
                }
            }
            case NOT_EQUAL: {
                if (isJsonFunction) {
                    if (isJsonContainsFunction) {
                        return query.equal(false);
                    }
                    if (!isJsonExtractFunction) {
                        throw PamirsException.construct(RsqlExpEnumerate.BASE_NO_SUPPORT_OPERATION_RSQL_ERROR)
                                .errThrow();
                    }
                }
                if (argument == null) {
                    return query.isNotNull();
                } else {
                    return query.notEqual(makeVariable(argument));
                }
            }
            case GREATER_THAN_OR_EQUAL: {
                if (isJsonFunction) {
                    if (!isJsonExtractFunction) {
                        throw PamirsException.construct(RsqlExpEnumerate.BASE_NO_SUPPORT_OPERATION_RSQL_ERROR)
                                .errThrow();
                    }
                }
                return query.greaterThanOrEqualTo(makeVariable(argument));
            }
            case LESS_THAN: {
                if (isJsonFunction) {
                    if (!isJsonExtractFunction) {
                        throw PamirsException.construct(RsqlExpEnumerate.BASE_NO_SUPPORT_OPERATION_RSQL_ERROR)
                                .errThrow();
                    }
                }
                return query.lessThan(makeVariable(argument));
            }
            case LESS_THAN_OR_EQUAL: {
                if (isJsonFunction) {
                    if (!isJsonExtractFunction) {
                        throw PamirsException.construct(RsqlExpEnumerate.BASE_NO_SUPPORT_OPERATION_RSQL_ERROR)
                                .errThrow();
                    }
                }
                return query.lessThanOrEqualTo(makeVariable(argument));
            }
            case IN:
                if (isJsonFunction) {
                    if (!isJsonExtractFunction) {
                        throw PamirsException.construct(RsqlExpEnumerate.BASE_NO_SUPPORT_OPERATION_RSQL_ERROR)
                                .errThrow();
                    }
                }
                query.getParamValues().add(args);
                return query.in(args);
            case NOT_IN:
                if (isJsonFunction) {
                    if (!isJsonExtractFunction) {
                        throw PamirsException.construct(RsqlExpEnumerate.BASE_NO_SUPPORT_OPERATION_RSQL_ERROR)
                                .errThrow();
                    }
                }
                query.getParamValues().add(args);
                return query.notIn(args);
            case IS_NULL:
                if (BooleanHelper.isTrue(argument)) {
                    return query.isNull();
                } else {
                    return query.isNotNull();
                }
            case IS_NOT_NULL:
                if (BooleanHelper.isTrue(argument)) {
                    return query.isNotNull();
                } else {
                    return query.isNull();
                }
            case LIKE:
                if (isJsonFunction) {
                    if (isJsonSearchFunction) {
                        return query.isNotNull();
                    }
                    if (!isJsonExtractFunction) {
                        throw PamirsException.construct(RsqlExpEnumerate.BASE_NO_SUPPORT_OPERATION_RSQL_ERROR)
                                .errThrow();
                    }
                }
                if (argument != null) {
                    return query.like((String) makeVariable(argument));
                } else {
                    return query.isNull();
                }
            case NOT_LIKE:
                if (isJsonFunction) {
                    if (isJsonSearchFunction) {
                        return query.isNull();
                    }
                    if (!isJsonExtractFunction) {
                        throw PamirsException.construct(RsqlExpEnumerate.BASE_NO_SUPPORT_OPERATION_RSQL_ERROR)
                                .errThrow();
                    }
                }
                if (argument != null) {
                    return query.notLike((String) makeVariable(argument));
                } else {
                    return query.isNull();
                }
            case HAS:
                if (isJsonFunction) {
                    if (!isJsonExtractFunction) {
                        throw PamirsException.construct(RsqlExpEnumerate.BASE_NO_SUPPORT_OPERATION_RSQL_ERROR)
                                .errThrow();
                    }
                }
                if (argument == null) {
                    return query.isNull();
                } else {
                    return query.equalTextKeyword(makeVariable(argument));
                }
            case BIT:
                if (isJsonFunction) {
                    if (!isJsonExtractFunction) {
                        throw PamirsException.construct(RsqlExpEnumerate.BASE_NO_SUPPORT_OPERATION_RSQL_ERROR)
                                .errThrow();
                    }
                }
                if (argument == null) {
                    return query.isNull();
                } else {
                    return query.equal(makeVariable(argument));
                }
            case NOT_HAS:
            case NOT_BIT:
                //BIT
                if (isJsonFunction) {
                    if (!isJsonExtractFunction) {
                        throw PamirsException.construct(RsqlExpEnumerate.BASE_NO_SUPPORT_OPERATION_RSQL_ERROR)
                                .errThrow();
                    }
                }
                if (argument == null) {
                    return query.isNotNull();
                } else {
                    return query.notEqual(makeVariable(argument));
                }
            default:
                throw PamirsException.construct(RsqlExpEnumerate.BASE_NO_SUPPORT_OPERATION_RSQL_ERROR)
                        .errThrow();
        }
    }

    private List<Object> castArguments(String property, ModelConfig model) {
        if (this.isJsonFunction) {
            int     separatorIndex = property.lastIndexOf(CharacterConstants.SEPARATOR_COLON);
            boolean isContinue     = separatorIndex >= 1;
            if (isContinue) {
                this.property = property.substring(0, separatorIndex);
                property      = property.substring(separatorIndex + 1);
                String[] properties = property.split(CharacterConstants.SEPARATOR_ESCAPE_DOT);
                isContinue = properties.length == RsqlConstant.SUPPORTED_RELATION_PROPERTY_COUNT;
                if (isContinue) {
                    RequestContext   requestContext = PamirsSession.getContext();
                    ModelFieldConfig modelField     = requestContext.getModelField(model.getModel(), properties[0]);
                    isContinue = modelField != null;
                    if (isContinue) {
                        model    = requestContext.getModelConfig(modelField.getReferences());
                        property = properties[1];
                    }
                }
            }
            if (!isContinue) {
                throw PamirsException.construct(RsqlExpEnumerate.BASE_NO_SUPPORT_RELATION_RSQL_ERROR)
                        .errThrow();
            }
        }
        final String           finalProperty = property;
        List<Object>           args          = new ArrayList<>();
        List<ModelFieldConfig> fields        = model.getModelFieldConfigList();
        List<ModelFieldConfig> matchFields   = fields.stream().filter(item -> finalProperty.equals((item.getLname()))).collect(Collectors.toList());
        ModelFieldConfig       modelField    = new ModelFieldConfig();
        if (StringUtils.isNumeric(property)) {
            for (Object argument : arguments) {
                cast(args, argument, modelField);
            }
            return args;
        }
        if (CollectionUtils.isEmpty(matchFields)) {
            return args;
        }
        modelField = matchFields.get(0);
        String  tType            = modelField.getTtype();
        boolean isNormalArgument = true;
        if (TtypeEnum.ENUM.value().equals(tType)) {
            if (modelField.getMulti() && SerializeEnum.BIT.value().equals(modelField.getStoreSerialize())) {
                if (RsqlSearchOperation.HAS.getOperator().equals(operator)) {
                    operator = RsqlSearchOperation.EQUAL.getOperator();
                } else if (RsqlSearchOperation.NOT_HAS.getOperator().equals(operator)) {
                    operator = RsqlSearchOperation.NOT_EQUAL.getOperator();
                }
                for (Object argument : arguments) {
                    cast(args, argument, modelField);
                }
                long result = 0L;
                for (Object value : args) {
                    result |= (Long) value;
                }
                args = new ArrayList<>();
                args.add(result);
                isNormalArgument = false;
            }
        }
        if (isNormalArgument) {
            for (Object argument : arguments) {
                cast(args, argument, modelField);
            }
        }
        if (CollectionUtils.isEmpty(args)) {
            throw PamirsException.construct(BASE_ARGUMENTS_ERROR)
                    .errThrow();
        }
        Object value = args.get(0);
        if (isJsonContainsFunction) {
            boolean isAppendDoubleQuotation = false;
            if (RsqlSearchOperation.IS_NULL.getOperator().equals(operator)
                    || RsqlSearchOperation.IS_NOT_NULL.getOperator().equals(operator)) {
                value = RsqlCharacterConstant.DOUBLE_QUOTATION + RsqlCharacterConstant.DOUBLE_QUOTATION;
            } else {
                if (TtypeEnum.isStringType(tType)) {
                    isAppendDoubleQuotation = true;
                } else if (TtypeEnum.ENUM.value().equals(tType)) {
                    DataDictionary dictionary = PamirsSession.getContext().getDictionary(modelField.getDictionary());
                    if (dictionary == null) {
                        throw PamirsException.construct(BASE_ARGUMENTS_ERROR)
                                .errThrow();
                    }
                    if (TtypeEnum.isStringType(dictionary.getValueType().value())) {
                        isAppendDoubleQuotation = true;
                    }
                }
            }
            if (isAppendDoubleQuotation) {
                value = RsqlCharacterConstant.DOUBLE_QUOTATION + value + RsqlCharacterConstant.DOUBLE_QUOTATION;
            }
        }
        if (needFormatProperty) {
            if (value == null) {
                value = RsqlCharacterConstant.DOUBLE_QUOTATION + RsqlCharacterConstant.DOUBLE_QUOTATION;
                if (RsqlSearchOperation.EQUAL.getOperator().equals(operator)) {
                    operator = RsqlSearchOperation.NOT_EQUAL.getOperator();
                } else if (RsqlSearchOperation.NOT_EQUAL.getOperator().equals(operator)) {
                    operator = RsqlSearchOperation.EQUAL.getOperator();
                } else {
                    throw PamirsException.construct(RsqlExpEnumerate.BASE_NO_SUPPORT_OPERATION_RSQL_ERROR)
                            .errThrow();
                }
            }
            this.property = String.format(this.property, value);
        }
        return args;
    }

    protected Object makeVariable(Object obj) {
        switch (obj.getClass().getName()) {
            case "java.lang.String":
                return obj.toString();
            case "java.util.List":
            case "java.util.ArrayList":
                return obj;
            case "java.lang.Integer":
            case "java.lang.Float":
            case "java.lang.Long":
            case "java.lang.Short":
            case "java.math.BigInteger":
            case "java.math.BigDecimal":
                return String.valueOf(obj);
            case "java.lang.Boolean":
                return ((Boolean) obj) ? Boolean.TRUE : Boolean.FALSE;
            case "java.util.Date":
                //fixme 目前的前后端协议没有约定传递的Date类型是什么 搜索的时候按照用户直接输入的时间字符串传递 20210103
                //return DateFormatUtils.format((Date)obj, DATE_FORMATE);
            default:
                /**
                 * 前后端RSQL协议：
                 * 前端传递枚举的NAME，后端存储的是枚举的VALUE
                 */
                if (obj instanceof IEnum || obj instanceof Enum) {
                    Object value = BaseEnum.getValue(obj);
                    return makeVariable(value);
                }
                return obj;
        }
    }

    private void cast(List<Object> parent, Object argument, ModelFieldConfig modelField) {
        List<RsqlValueConverter> converters = Spider.getLoader(RsqlValueConverter.class).getOrderedExtensions();
        EsRsqlDateConverter esDateConverter = CommonApiFactory.getApi(EsRsqlDateConverter.class);
        for (RsqlValueConverter converter : converters) {
            if (converter.match(modelField)) {
                argument = converter.convert(modelField, argument);
                if (esDateConverter.match(modelField)) {
                    argument = esDateConverter.convert(modelField, argument);
                }
            }
        }
        switch (RsqlSearchOperation.getSimpleOperator(operator)) {
            case GREATER_THAN: {
                query.getParamValues().add(argument);
            }
            case IS_NULL:
            case IS_NOT_NULL:
            case COLUMN_EQUAL:
            case COLUMN_NOT_EQUAL:
            case LIKE:
            case NOT_LIKE:
                parent.add(argument);
                return;
        }
        parent.add(argument);
    }

    public static Boolean isRelatedField(String clomn) {
        return StringUtils.isNotBlank(clomn) && clomn.contains(".");
    }
}
