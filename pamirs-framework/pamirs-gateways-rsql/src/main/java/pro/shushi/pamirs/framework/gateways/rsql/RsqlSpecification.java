package pro.shushi.pamirs.framework.gateways.rsql;

import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import pro.shushi.pamirs.framework.connectors.data.sql.config.ModelFieldConfigWrapper;
import pro.shushi.pamirs.framework.gateways.constant.RsqlCharacterConstant;
import pro.shushi.pamirs.framework.gateways.constant.RsqlConstant;
import pro.shushi.pamirs.framework.gateways.convert.RsqlValueConverter;
import pro.shushi.pamirs.framework.gateways.rsql.enmu.RsqlExpEnumerate;
import pro.shushi.pamirs.framework.gateways.util.BooleanHelper;
import pro.shushi.pamirs.meta.annotation.fun.Data;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
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
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static pro.shushi.pamirs.framework.gateways.rsql.enmu.RsqlExpEnumerate.*;

/**
 * TypeHelper供Rsql专用，所以使用继承
 *
 * @author deng
 */
@Slf4j
@Data
public class RsqlSpecification {

    private String originProperty;
    private String property;
    private ComparisonOperator operator;
    private List<String> arguments;
    private Object[] requestArguments;
    private RsqlQuery query;
    private ModelConfig model;

    private int propertyIndex;

    private final boolean isJsonExtractFunction;
    private final boolean isJsonContainsFunction;
    private final boolean isJsonSearchFunction;
    private final boolean isJsonFunction;
    private final boolean needFormatProperty;

    public RsqlSpecification(
            String property, ComparisonOperator operator, List<String> arguments, ModelConfig model) {
        this.originProperty = property;
        this.property = property;
        //rsql domain
        this.operator = operator;
        this.arguments = arguments;
        if (isRelatedField(property)) {
            RsqlRelation rsqlRelation = new RsqlRelation(property, operator, arguments, model);
            this.property = rsqlRelation.getProperty();
            this.operator = rsqlRelation.getOperator();
            this.arguments = rsqlRelation.getArguments();
            this.propertyIndex = rsqlRelation.getPropertyIndex();
        } else {
            this.propertyIndex = -1;
        }
        this.isJsonExtractFunction = this.property.startsWith(RsqlConstant.JSON_EXTRACT_FUNCTION);
        this.isJsonContainsFunction = this.property.startsWith(RsqlConstant.JSON_CONTAINS_FUNCTION);
        this.isJsonSearchFunction = this.property.startsWith(RsqlConstant.JSON_SEARCH_FUNCTION);
        this.isJsonFunction = this.isJsonExtractFunction || this.isJsonContainsFunction || this.isJsonSearchFunction;
        this.needFormatProperty = this.isJsonContainsFunction || this.isJsonSearchFunction;
        this.query = new RsqlQuery(castProperty(this.property, model));
        this.model = model;
    }

    public RsqlQuery toQuery() {
        List<Object> args = castArguments(property, model);
        if (isJsonFunction) {
            this.query.getWhere().append(this.property);
        }
        Object argument = args.get(0);
        query.setCondition(new StringBuilder(originProperty + StringUtils.SPACE + operator.getSymbol() + StringUtils.SPACE + argument));
        RsqlSearchOperation operation = RsqlSearchOperation.getSimpleOperator(operator);
        if (operation == null) {
            throw PamirsException.construct(RsqlExpEnumerate.BASE_NO_SUPPORT_OPERATION_RSQL_ERROR).errThrow();
        }
        switch (operation) {
            case GREATER_THAN: {
                if (isJsonFunction) {
                    if (!isJsonExtractFunction) {
                        throw PamirsException.construct(RsqlExpEnumerate.BASE_NO_SUPPORT_OPERATION_RSQL_ERROR).errThrow();
                    }
                }
                query.getParamValues().add(argument);
                return query.greaterThan(makeVariable(argument));
            }
            case EQUAL: {
                if (isJsonFunction) {
                    if (isJsonContainsFunction) {
                        return query.equal(true);
                    }
                    if (!isJsonExtractFunction) {
                        throw PamirsException.construct(RsqlExpEnumerate.BASE_NO_SUPPORT_OPERATION_RSQL_ERROR).errThrow();
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
                        throw PamirsException.construct(RsqlExpEnumerate.BASE_NO_SUPPORT_OPERATION_RSQL_ERROR).errThrow();
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
                        throw PamirsException.construct(RsqlExpEnumerate.BASE_NO_SUPPORT_OPERATION_RSQL_ERROR).errThrow();
                    }
                }
                return query.greaterThanOrEqualTo(makeVariable(argument));
            }
            case LESS_THAN: {
                if (isJsonFunction) {
                    if (!isJsonExtractFunction) {
                        throw PamirsException.construct(RsqlExpEnumerate.BASE_NO_SUPPORT_OPERATION_RSQL_ERROR).errThrow();
                    }
                }
                return query.lessThan(makeVariable(argument));
            }
            case LESS_THAN_OR_EQUAL: {
                if (isJsonFunction) {
                    if (!isJsonExtractFunction) {
                        throw PamirsException.construct(RsqlExpEnumerate.BASE_NO_SUPPORT_OPERATION_RSQL_ERROR).errThrow();
                    }
                }
                return query.lessThanOrEqualTo(makeVariable(argument));
            }
            case IN:
                if (isJsonFunction) {
                    if (!isJsonExtractFunction) {
                        throw PamirsException.construct(RsqlExpEnumerate.BASE_NO_SUPPORT_OPERATION_RSQL_ERROR).errThrow();
                    }
                }
                query.getParamValues().add(args);
                return query.in(makeQuestionMarks(args));
            case NOT_IN:
                if (isJsonFunction) {
                    if (!isJsonExtractFunction) {
                        throw PamirsException.construct(RsqlExpEnumerate.BASE_NO_SUPPORT_OPERATION_RSQL_ERROR).errThrow();
                    }
                }
                query.getParamValues().add(args);
                return query.notIn(makeQuestionMarks(args));
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
                        throw PamirsException.construct(RsqlExpEnumerate.BASE_NO_SUPPORT_OPERATION_RSQL_ERROR).errThrow();
                    }
                }
                if (argument != null) {
                    return query.like((String) makeVariable(CharacterConstants.PERCENT + argument + CharacterConstants.PERCENT));
                } else {
                    return query.isNull();
                }
            case NOT_LIKE:
                if (isJsonFunction) {
                    if (isJsonSearchFunction) {
                        return query.isNull();
                    }
                    if (!isJsonExtractFunction) {
                        throw PamirsException.construct(RsqlExpEnumerate.BASE_NO_SUPPORT_OPERATION_RSQL_ERROR).errThrow();
                    }
                }
                if (argument != null) {
                    return query.notLike((String) makeVariable(CharacterConstants.PERCENT + argument + CharacterConstants.PERCENT));
                } else {
                    return query.isNull();
                }
            case HAS:
            case BIT:
                if (isJsonFunction) {
                    if (!isJsonExtractFunction) {
                        throw PamirsException.construct(RsqlExpEnumerate.BASE_NO_SUPPORT_OPERATION_RSQL_ERROR).errThrow();
                    }
                }
                if (argument == null) {
                    return query.isNull();
                } else {
                    return query.equal(makeVariable(argument));
                }
            case NOT_HAS:
            case NOT_BIT:
                if (isJsonFunction) {
                    if (!isJsonExtractFunction) {
                        throw PamirsException.construct(RsqlExpEnumerate.BASE_NO_SUPPORT_OPERATION_RSQL_ERROR).errThrow();
                    }
                }
                if (argument == null) {
                    return query.isNotNull();
                } else {
                    return query.notEqual(makeVariable(argument));
                }
            case STARTS:
                if (isJsonFunction) {
                    if (isJsonSearchFunction) {
                        return query.isNotNull();
                    }
                    if (!isJsonExtractFunction) {
                        throw PamirsException.construct(RsqlExpEnumerate.BASE_NO_SUPPORT_OPERATION_RSQL_ERROR).errThrow();
                    }
                }
                if (argument != null) {
                    return query.like((String) makeVariable(argument + CharacterConstants.PERCENT));
                } else {
                    return query.isNull();
                }
            case NOT_STARTS:
                if (isJsonFunction) {
                    if (isJsonSearchFunction) {
                        return query.isNull();
                    }
                    if (!isJsonExtractFunction) {
                        throw PamirsException.construct(RsqlExpEnumerate.BASE_NO_SUPPORT_OPERATION_RSQL_ERROR).errThrow();
                    }
                }
                if (argument != null) {
                    return query.notLike((String) makeVariable(argument + CharacterConstants.PERCENT));
                } else {
                    return query.isNull();
                }
            case ENDS:
                if (isJsonFunction) {
                    if (isJsonSearchFunction) {
                        return query.isNotNull();
                    }
                    if (!isJsonExtractFunction) {
                        throw PamirsException.construct(RsqlExpEnumerate.BASE_NO_SUPPORT_OPERATION_RSQL_ERROR).errThrow();
                    }
                }
                if (argument != null) {
                    return query.like((String) makeVariable(CharacterConstants.PERCENT + argument));
                } else {
                    return query.isNull();
                }
            case NOT_ENDS:
                if (isJsonFunction) {
                    if (isJsonSearchFunction) {
                        return query.isNull();
                    }
                    if (!isJsonExtractFunction) {
                        throw PamirsException.construct(RsqlExpEnumerate.BASE_NO_SUPPORT_OPERATION_RSQL_ERROR).errThrow();
                    }
                }
                if (argument != null) {
                    return query.notLike((String) makeVariable(CharacterConstants.PERCENT + argument));
                } else {
                    return query.isNull();
                }
            case COLUMN_EQUAL:
                if (isJsonFunction) {
                    if (isJsonContainsFunction) {
                        return query.equal(true);
                    }
                    if (!isJsonExtractFunction) {
                        throw PamirsException.construct(RsqlExpEnumerate.BASE_NO_SUPPORT_OPERATION_RSQL_ERROR).errThrow();
                    }
                }
                if (argument == null) {
                    return query.isNull();
                } else {
                    return query.equal(makeFiled(argument));
                }
            case COLUMN_NOT_EQUAL:
                if (isJsonFunction) {
                    if (isJsonContainsFunction) {
                        return query.equal(false);
                    }
                    if (!isJsonExtractFunction) {
                        throw PamirsException.construct(RsqlExpEnumerate.BASE_NO_SUPPORT_OPERATION_RSQL_ERROR).errThrow();
                    }
                }
                if (argument == null) {
                    return query.isNull();
                } else {
                    return query.notEqual(makeFiled(argument));
                }
            default:
                throw PamirsException.construct(RsqlExpEnumerate.BASE_NO_SUPPORT_OPERATION_RSQL_ERROR).errThrow();
        }
    }

    private String castProperty(String property, ModelConfig model) {
        //判断是否是数字 例如1==1 -> 应当支持为1=1
        if (StringUtils.isNumeric(property)) {
            return property;
        }
        if (isJsonFunction) {
            int separatorIndex = property.lastIndexOf(CharacterConstants.SEPARATOR_COLON);
            if (separatorIndex >= 1) {
                return CharacterConstants.SEPARATOR_EMPTY;
            }
            throw PamirsException.construct(RsqlExpEnumerate.BASE_NO_SUPPORT_RELATION_RSQL_ERROR).errThrow();
        }
        final String finalProperty = property;
        List<ModelFieldConfig> fields = model.getModelFieldConfigList();
        List<ModelFieldConfig> matchFields = fields.stream().filter(item -> finalProperty.equals((item.getLname()))).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(matchFields)) {
            throw PamirsException.construct(BASE_NO_MATCH_FIELD_ERROR).appendMsg("field：" + property).errThrow();
        }
        ModelFieldConfig modelField = matchFields.get(0);
        String ttype = modelField.getTtype();
        String column = ModelFieldConfigWrapper.wrap(modelField).getSqlSelect(true);
        if (StringUtils.isBlank(column)) {
            throw PamirsException.construct(BASE_NO_MATCH_COLUMN_ERROR).errThrow();
        }
        if (TtypeEnum.ENUM.value().equals(ttype)) {
            if (modelField.getMulti() && SerializeEnum.BIT.value().equals(modelField.getStoreSerialize())) {
                if (RsqlSearchOperation.HAS.getOperator().equals(operator)
                        || RsqlSearchOperation.NOT_HAS.getOperator().equals(operator)
                        || RsqlSearchOperation.HAS_OR.getOperator().equals(operator)
                        || RsqlSearchOperation.HAS_NOT_OR.getOperator().equals(operator)
                ) {
                    List<Object> values = new ArrayList<>();
                    for (Object argument : arguments) {
                        cast(values, argument, modelField);
                    }
                    long result = 0L;
                    for (Object value : values) {
                        result |= (Long) value;
                    }
                    return column + " & " + result;
                }
            }
        }
        return column;
    }

    private List<Object> castArguments(String property, ModelConfig model) {
        if (this.isJsonFunction) {
            int separatorIndex = property.lastIndexOf(CharacterConstants.SEPARATOR_COLON);
            boolean isContinue = separatorIndex >= 1;
            if (isContinue) {
                this.property = property.substring(0, separatorIndex);
                property = property.substring(separatorIndex + 1);
                String[] properties = property.split(CharacterConstants.SEPARATOR_ESCAPE_DOT);
                isContinue = properties.length == RsqlConstant.SUPPORTED_RELATION_PROPERTY_COUNT;
                if (isContinue) {
                    RequestContext requestContext = PamirsSession.getContext();
                    ModelFieldConfig modelField = requestContext.getModelField(model.getModel(), properties[0]);
                    isContinue = modelField != null;
                    if (isContinue) {
                        model = requestContext.getModelConfig(modelField.getReferences());
                        property = properties[1];
                    }
                }
            }
            if (!isContinue) {
                throw PamirsException.construct(RsqlExpEnumerate.BASE_NO_SUPPORT_RELATION_RSQL_ERROR).errThrow();
            }
        }
        final String finalProperty = property;
        List<Object> args = new ArrayList<>();
        List<ModelFieldConfig> fields = model.getModelFieldConfigList();
        List<ModelFieldConfig> matchFields = fields.stream().filter(item -> finalProperty.equals((item.getLname()))).collect(Collectors.toList());
        ModelFieldConfig modelField = new ModelFieldConfig();
        if (StringUtils.isNumeric(property)) {
            for (Object argument : arguments) {
                if (StringUtils.isNumeric((String) argument)) {
                    argument = NumberUtils.toLong((String) argument);
                }
                cast(args, argument, modelField);
            }
            return args;
        }
        if (CollectionUtils.isEmpty(matchFields)) {
            return args;
        }
        modelField = matchFields.get(0);
        String ttype = modelField.getTtype();
        boolean isNormalArgument = true;
        if (TtypeEnum.ENUM.value().equals(ttype)) {
            if (modelField.getMulti() && SerializeEnum.BIT.value().equals(modelField.getStoreSerialize())) {
                if (!RsqlSearchOperation.IS_NULL.getOperator().equals(operator)
                        && !RsqlSearchOperation.IS_NOT_NULL.getOperator().equals(operator)) {
                    boolean appendProperty = false;
                    boolean isZero = false;
                    if (RsqlSearchOperation.HAS.getOperator().equals(operator)) {
                        operator = RsqlSearchOperation.EQUAL.getOperator();
                        appendProperty = true;
                    } else if (RsqlSearchOperation.NOT_HAS.getOperator().equals(operator)) {
                        operator = RsqlSearchOperation.NOT_EQUAL.getOperator();
                        appendProperty = true;
                    } else if (RsqlSearchOperation.HAS_OR.getOperator().equals(operator)) {
                        operator = RsqlSearchOperation.GREATER_THAN.getOperator();
                        appendProperty = true;
                        isZero = true;
                    } else if (RsqlSearchOperation.HAS_NOT_OR.getOperator().equals(operator)) {
                        operator = RsqlSearchOperation.EQUAL.getOperator();
                        appendProperty = true;
                        isZero = true;
                    }
                    for (Object argument : arguments) {
                        cast(args, argument, modelField);
                    }
                    long result = 0L;
                    for (Object value : args) {
                        result |= (Long) value;
                    }
                    if (this.isJsonFunction && appendProperty) {
                        this.property = this.property + " & " + result;
                    }
                    args = new ArrayList<>();
                    if (isZero) {
                        args.add(0L);
                    } else {
                        args.add(result);
                    }
                    isNormalArgument = false;
                }
            }
        }
        if (isNormalArgument) {
            for (Object argument : arguments) {
                cast(args, argument, modelField);
            }
        }
        if (CollectionUtils.isEmpty(args)) {
            throw PamirsException.construct(BASE_ARGUMENTS_ERROR).errThrow();
        }
        Object value = args.get(0);
        if (isJsonContainsFunction) {
            boolean isAppendDoubleQuotation = false;
            if (RsqlSearchOperation.IS_NULL.getOperator().equals(operator)
                    || RsqlSearchOperation.IS_NOT_NULL.getOperator().equals(operator)) {
                value = RsqlCharacterConstant.DOUBLE_QUOTATION + RsqlCharacterConstant.DOUBLE_QUOTATION;
            } else {
                if (TtypeEnum.isStringType(ttype)) {
                    isAppendDoubleQuotation = true;
                } else if (TtypeEnum.ENUM.value().equals(ttype)) {
                    DataDictionary dictionary = PamirsSession.getContext().getDictionary(modelField.getDictionary());
                    if (dictionary == null) {
                        throw PamirsException.construct(BASE_ARGUMENTS_ERROR).errThrow();
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
                    throw PamirsException.construct(RsqlExpEnumerate.BASE_NO_SUPPORT_OPERATION_RSQL_ERROR).errThrow();
                }
            }
            this.property = String.format(this.property, value);
        }
        return args;
    }

    private void cast(List<Object> parent, Object argument, ModelFieldConfig modelField) {
        List<RsqlValueConverter> converters = Spider.getLoader(RsqlValueConverter.class).getOrderedExtensions();
        for (RsqlValueConverter converter : converters) {
            if (converter.match(modelField)) {
                argument = converter.convert(modelField, argument);
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

    protected Object makeVariable(Object obj) {
        switch (obj.getClass().getName()) {
            case "java.lang.String":
                return "'" + obj + "'";
            case "java.util.List":
            case "java.util.ArrayList":
                return makeVariables((List) obj);
            case "java.lang.Integer":
            case "java.lang.Float":
            case "java.lang.Long":
            case "java.lang.Short":
            case "java.math.BigInteger":
            case "java.math.BigDecimal":
                return String.valueOf(obj);
            case "java.lang.Boolean":
                return ((Boolean) obj) ? 1 : 0;
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

    protected String makeFiled(Object fieldLName) {
        final String finalFieldLName = (String) fieldLName;
        List<ModelFieldConfig> fields = model.getModelFieldConfigList();
        List<ModelFieldConfig> matchFields = fields.stream().filter(item -> finalFieldLName.equals((item.getLname()))).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(matchFields)) {
            throw PamirsException.construct(BASE_NO_MATCH_FIELD_ERROR).appendMsg("field：" + fieldLName).errThrow();
        }
        ModelFieldConfig modelField = matchFields.get(0);
        String column = ModelFieldConfigWrapper.wrap(modelField).getSqlSelect(true);
        if (StringUtils.isBlank(column)) {
            throw PamirsException.construct(BASE_NO_MATCH_COLUMN_ERROR).errThrow();
        }
        return column;
    }

    protected String makeVariables(List list) {
        return "(" + String.join(",", (Collection) list.stream().map(v -> makeVariable(v)).collect(Collectors.toList())) + ")";
    }

    protected String makeQuestionMarks(List list) {
        return "(" + String.join(",", (Collection) list.stream().map(v -> makeVariable(v)).collect(Collectors.toList())) + ")";
    }

    public static Boolean isRelatedField(String clomn) {
        return StringUtils.isBlank(clomn) ? false : clomn.contains(".");
    }
}
