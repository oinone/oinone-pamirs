package pro.shushi.pamirs.framework.gateways.rsql.connector;

import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import pro.shushi.pamirs.framework.common.entry.TreeNode;
import pro.shushi.pamirs.framework.connectors.data.sql.config.ModelFieldConfigWrapper;
import pro.shushi.pamirs.framework.gateways.constant.RsqlCharacterConstant;
import pro.shushi.pamirs.framework.gateways.constant.RsqlConstant;
import pro.shushi.pamirs.framework.gateways.convert.RsqlValueConverter;
import pro.shushi.pamirs.framework.gateways.rsql.RSQLNodeInfo;
import pro.shushi.pamirs.framework.gateways.rsql.RsqlRelation;
import pro.shushi.pamirs.framework.gateways.rsql.RsqlSearchOperation;
import pro.shushi.pamirs.framework.gateways.rsql.enmu.RsqlExpEnumerate;
import pro.shushi.pamirs.framework.gateways.util.BooleanHelper;
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
 * RSQL转SQL节点连接器
 *
 * @author Adamancy Zhang at 10:34 on 2025-04-07
 */
public class RSQLToSQLNodeConnector extends SQLNodeConnector {

    public static final NodeConnector INSTANCE = new RSQLToSQLNodeConnector();

    @Override
    protected String connect(TreeNode<RSQLNodeInfo> node, List<String> arguments) {
        RSQLNodeInfo nodeInfo = node.getValue();
        ComparisonOperator operator = nodeInfo.getOperator();
        ModelFieldConfig modelFieldConfig = nodeInfo.getModelFieldConfig();
        if (modelFieldConfig == null) {
            return connectByNormal(nodeInfo, operator, arguments);
        }
        if (modelFieldConfig.isVirtual()) {
            return connectByVirtual(nodeInfo, operator, arguments);
        }
        return connectByNormal(nodeInfo, operator, arguments);
    }

    // region 此片段完全保留原有RsqlSpecification中对普通字段RSQL转SQL逻辑，请勿追加或修改任何逻辑

    private String connectByNormal(RSQLNodeInfo nodeInfo, ComparisonOperator operator, List<String> arguments) {
        String field = nodeInfo.getField();
        String[] propertyKeys = field.split("\\.");
        if (propertyKeys.length >= 2) {
            RsqlRelation rsqlRelation = new RsqlRelation(propertyKeys, operator, arguments, nodeInfo.getModelConfig());
            field = rsqlRelation.getProperty();
            operator = rsqlRelation.getOperator();
            arguments = rsqlRelation.getArguments();
        }

        Context context = new Context();
        context.isJsonExtractFunction = field.startsWith(RsqlConstant.JSON_EXTRACT_FUNCTION);
        context.isJsonContainsFunction = field.startsWith(RsqlConstant.JSON_CONTAINS_FUNCTION);
        context.isJsonSearchFunction = field.startsWith(RsqlConstant.JSON_SEARCH_FUNCTION);
        context.isJsonFunction = context.isJsonExtractFunction || context.isJsonContainsFunction || context.isJsonSearchFunction;
        context.needFormatProperty = context.isJsonContainsFunction || context.isJsonSearchFunction;
        context.property = field;
        context.operator = operator;
        context.arguments = arguments;

        String column = getColumn(context, field, nodeInfo.getModelConfig());
        String argument = getArgumentString(context, field, nodeInfo.getModelConfig());
        String sqlOperator = getSQLOperator(context.operator);
        if (StringUtils.isBlank(column)) {
            column = context.property;
        }
        return concat(CharacterConstants.SEPARATOR_BLANK,
                column,
                sqlOperator,
                argument);
    }

    private String getColumn(Context context, String property, ModelConfig model) {
        //判断是否是数字 例如1==1 -> 应当支持为1=1
        if (StringUtils.isNumeric(property)) {
            return property;
        }
        if (context.isJsonFunction) {
            int separatorIndex = property.lastIndexOf(CharacterConstants.SEPARATOR_COLON);
            if (separatorIndex >= 1) {
                return CharacterConstants.SEPARATOR_EMPTY;
            }
            throw PamirsException.construct(RsqlExpEnumerate.BASE_NO_SUPPORT_RELATION_RSQL_ERROR).errThrow();
        }
        List<ModelFieldConfig> fields = model.getModelFieldConfigList();
        List<ModelFieldConfig> matchFields = fields.stream().filter(item -> property.equals((item.getLname()))).collect(Collectors.toList());
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
                ComparisonOperator operator = context.operator;
                if (RsqlSearchOperation.HAS.getOperator().equals(operator)
                        || RsqlSearchOperation.NOT_HAS.getOperator().equals(operator)
                        || RsqlSearchOperation.HAS_OR.getOperator().equals(operator)
                        || RsqlSearchOperation.HAS_NOT_OR.getOperator().equals(operator)
                ) {
                    List<Object> values = new ArrayList<>();
                    for (Object argument : context.arguments) {
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

    private String getArgumentString(Context context, String field, ModelConfig model) {
        List<Object> args = castArguments(context, field, model);
        Object argument = args.get(0);
        RsqlSearchOperation operation = RsqlSearchOperation.getSimpleOperator(context.operator);
        if (operation == null) {
            throw PamirsException.construct(RsqlExpEnumerate.BASE_NO_SUPPORT_OPERATION_RSQL_ERROR).errThrow();
        }
        boolean isJsonExtractFunction = context.isJsonExtractFunction;
        boolean isJsonContainsFunction = context.isJsonContainsFunction;
        boolean isJsonSearchFunction = context.isJsonSearchFunction;
        boolean isJsonFunction = context.isJsonFunction;
        switch (operation) {
            case EQUAL:
            case NOT_EQUAL:
                if (isJsonFunction) {
                    if (isJsonContainsFunction) {
                        context.operator = RsqlSearchOperation.EQUAL.getOperator();
                        return operation.isNot() ? Boolean.FALSE.toString() : Boolean.TRUE.toString();
                    }
                    if (!isJsonExtractFunction) {
                        throw PamirsException.construct(RsqlExpEnumerate.BASE_NO_SUPPORT_OPERATION_RSQL_ERROR).errThrow();
                    }
                }
                break;
            case IS_NULL:
                if (BooleanHelper.isTrue(argument)) {
                    context.operator = RsqlSearchOperation.IS_NULL.getOperator();
                } else {
                    context.operator = RsqlSearchOperation.IS_NOT_NULL.getOperator();
                }
                return null;
            case IS_NOT_NULL:
                if (BooleanHelper.isTrue(argument)) {
                    context.operator = RsqlSearchOperation.IS_NOT_NULL.getOperator();
                } else {
                    context.operator = RsqlSearchOperation.IS_NULL.getOperator();
                }
                return null;
            case GREATER_THAN:
            case GREATER_THAN_OR_EQUAL:
            case LESS_THAN:
            case LESS_THAN_OR_EQUAL:
            case IN:
            case NOT_IN:
                if (isJsonFunction) {
                    if (!isJsonExtractFunction) {
                        throw PamirsException.construct(RsqlExpEnumerate.BASE_NO_SUPPORT_OPERATION_RSQL_ERROR).errThrow();
                    }
                }
                break;
            case LIKE:
            case NOT_LIKE:
                if (isJsonFunction) {
                    if (isJsonSearchFunction) {
                        if (operation.isNot()) {
                            context.operator = RsqlSearchOperation.IS_NULL.getOperator();
                        } else {
                            context.operator = RsqlSearchOperation.IS_NOT_NULL.getOperator();
                        }
                        return null;
                    }
                    if (!isJsonExtractFunction) {
                        throw PamirsException.construct(RsqlExpEnumerate.BASE_NO_SUPPORT_OPERATION_RSQL_ERROR).errThrow();
                    }
                }
                if (argument == null) {
                    break;
                }
                return makeVariable(CharacterConstants.PERCENT + argument + CharacterConstants.PERCENT).toString();
            case STARTS:
            case NOT_STARTS:
                if (isJsonFunction) {
                    if (isJsonSearchFunction) {
                        if (operation.isNot()) {
                            context.operator = RsqlSearchOperation.IS_NULL.getOperator();
                        } else {
                            context.operator = RsqlSearchOperation.IS_NOT_NULL.getOperator();
                        }
                        return null;
                    }
                    if (!isJsonExtractFunction) {
                        throw PamirsException.construct(RsqlExpEnumerate.BASE_NO_SUPPORT_OPERATION_RSQL_ERROR).errThrow();
                    }
                }
                if (argument == null) {
                    break;
                }
                return makeVariable(argument + CharacterConstants.PERCENT).toString();
            case ENDS:
            case NOT_ENDS:
                if (isJsonFunction) {
                    if (isJsonSearchFunction) {
                        if (operation.isNot()) {
                            context.operator = RsqlSearchOperation.IS_NULL.getOperator();
                        } else {
                            context.operator = RsqlSearchOperation.IS_NOT_NULL.getOperator();
                        }
                        return null;
                    }
                    if (!isJsonExtractFunction) {
                        throw PamirsException.construct(RsqlExpEnumerate.BASE_NO_SUPPORT_OPERATION_RSQL_ERROR).errThrow();
                    }
                }
                if (argument == null) {
                    break;
                }
                return makeVariable(CharacterConstants.PERCENT + argument).toString();
            case HAS:
            case BIT:
            case NOT_HAS:
            case NOT_BIT:
                if (isJsonFunction) {
                    if (!isJsonExtractFunction) {
                        throw PamirsException.construct(RsqlExpEnumerate.BASE_NO_SUPPORT_OPERATION_RSQL_ERROR).errThrow();
                    }
                }
                if (argument == null) {
                    break;
                }
                return makeVariable(argument).toString();
            case COLUMN_EQUAL:
            case COLUMN_NOT_EQUAL:
                if (isJsonFunction) {
                    if (isJsonContainsFunction) {
                        context.operator = RsqlSearchOperation.EQUAL.getOperator();
                        return operation.isNot() ? Boolean.FALSE.toString() : Boolean.TRUE.toString();
                    }
                    if (!isJsonExtractFunction) {
                        throw PamirsException.construct(RsqlExpEnumerate.BASE_NO_SUPPORT_OPERATION_RSQL_ERROR).errThrow();
                    }
                }
                if (argument == null) {
                    break;
                }
                return makeColumn(model, argument);
        }
        if (argument == null) {
            if (operation.isNot()) {
                context.operator = RsqlSearchOperation.IS_NOT_NULL.getOperator();
            } else {
                context.operator = RsqlSearchOperation.IS_NULL.getOperator();
            }
            return null;
        }
        if (context.operator.isMultiValue()) {
            return makeVariable(args).toString();
        }
        return makeVariable(argument).toString();
    }

    private List<Object> castArguments(Context context, String property, ModelConfig model) {
        if (context.isJsonFunction) {
            int separatorIndex = property.lastIndexOf(CharacterConstants.SEPARATOR_COLON);
            boolean isContinue = separatorIndex >= 1;
            if (isContinue) {
                context.property = property.substring(0, separatorIndex);
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
            for (Object argument : context.arguments) {
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
                ComparisonOperator operator = context.operator;
                if (!RsqlSearchOperation.IS_NULL.getOperator().equals(operator)
                        && !RsqlSearchOperation.IS_NOT_NULL.getOperator().equals(operator)) {
                    boolean appendProperty = false;
                    boolean isZero = false;
                    if (RsqlSearchOperation.HAS.getOperator().equals(operator)) {
                        context.operator = RsqlSearchOperation.EQUAL.getOperator();
                        appendProperty = true;
                    } else if (RsqlSearchOperation.NOT_HAS.getOperator().equals(operator)) {
                        context.operator = RsqlSearchOperation.NOT_EQUAL.getOperator();
                        appendProperty = true;
                    } else if (RsqlSearchOperation.HAS_OR.getOperator().equals(operator)) {
                        context.operator = RsqlSearchOperation.GREATER_THAN.getOperator();
                        appendProperty = true;
                        isZero = true;
                    } else if (RsqlSearchOperation.HAS_NOT_OR.getOperator().equals(operator)) {
                        context.operator = RsqlSearchOperation.EQUAL.getOperator();
                        appendProperty = true;
                        isZero = true;
                    }
                    for (Object argument : context.arguments) {
                        cast(args, argument, modelField);
                    }
                    long result = 0L;
                    for (Object value : args) {
                        result |= (Long) value;
                    }
                    if (context.isJsonFunction && appendProperty) {
                        context.property = context.property + " & " + result;
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
            for (Object argument : context.arguments) {
                cast(args, argument, modelField);
            }
        }
        if (CollectionUtils.isEmpty(args)) {
            throw PamirsException.construct(BASE_ARGUMENTS_ERROR).errThrow();
        }
        Object value = args.get(0);
        if (context.isJsonContainsFunction) {
            boolean isAppendDoubleQuotation = false;
            ComparisonOperator operator = context.operator;
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
        if (context.needFormatProperty) {
            if (value == null) {
                value = RsqlCharacterConstant.DOUBLE_QUOTATION + RsqlCharacterConstant.DOUBLE_QUOTATION;
                ComparisonOperator operator = context.operator;
                if (RsqlSearchOperation.EQUAL.getOperator().equals(operator)) {
                    context.operator = RsqlSearchOperation.NOT_EQUAL.getOperator();
                } else if (RsqlSearchOperation.NOT_EQUAL.getOperator().equals(operator)) {
                    context.operator = RsqlSearchOperation.EQUAL.getOperator();
                } else {
                    throw PamirsException.construct(RsqlExpEnumerate.BASE_NO_SUPPORT_OPERATION_RSQL_ERROR).errThrow();
                }
            }
            context.property = String.format(context.property, value);
        }
        return args;
    }

    private void cast(List<Object> values, Object argument, ModelFieldConfig modelField) {
        List<RsqlValueConverter> converters = Spider.getLoader(RsqlValueConverter.class).getOrderedExtensions();
        for (RsqlValueConverter converter : converters) {
            if (converter.match(modelField)) {
                argument = converter.convert(modelField, argument);
            }
        }
        values.add(argument);
    }

    protected String makeVariables(List list) {
        return "(" + String.join(",", (Collection) list.stream().map(v -> makeVariable(v)).collect(Collectors.toList())) + ")";
    }

    private Object makeVariable(Object obj) {
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

    protected String makeColumn(ModelConfig model, Object fieldLName) {
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

    private static class Context {

        private boolean isJsonExtractFunction;

        private boolean isJsonContainsFunction;

        private boolean isJsonSearchFunction;

        private boolean isJsonFunction;

        private boolean needFormatProperty;

        private String property;

        private ComparisonOperator operator;

        private List<String> arguments;
    }

    // endregion

    private String connectByVirtual(RSQLNodeInfo nodeInfo, ComparisonOperator operator, List<String> arguments) {
        ModelFieldConfig modelFieldConfig = nodeInfo.getModelFieldConfig();
        String ttype = modelFieldConfig.getTtype();
        if (TtypeEnum.RELATED.value().equals(ttype)) {
            List<String> relatedFields = modelFieldConfig.getRelated();
            if (CollectionUtils.isEmpty(relatedFields)) {
                throw PamirsException.construct(BASE_NO_MATCH_FIELD_ERROR).errThrow();
            }
            ModelFieldConfig relatedFieldConfig = PamirsSession.getContext().getModelField(nodeInfo.getModelConfig().getModel(), relatedFields.get(0));
            if (relatedFieldConfig == null) {
                throw PamirsException.construct(BASE_NO_MATCH_FIELD_ERROR).appendMsg("field：" + relatedFields.get(0)).errThrow();
            }
            RSQLNodeInfo relatedNodeInfo = nodeInfo.clone();
            relatedNodeInfo.setField(String.join(".", relatedFields));
            relatedNodeInfo.setModelFieldConfig(relatedFieldConfig);
            return connectByNormal(relatedNodeInfo, operator, arguments);
        }
        return null;
    }
}
