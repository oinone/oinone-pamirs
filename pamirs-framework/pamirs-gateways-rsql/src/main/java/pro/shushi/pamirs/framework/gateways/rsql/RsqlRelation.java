package pro.shushi.pamirs.framework.gateways.rsql;

import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.sql.config.Configs;
import pro.shushi.pamirs.framework.connectors.data.sql.query.QueryWrapper;
import pro.shushi.pamirs.framework.gateways.constant.RsqlCharacterConstant;
import pro.shushi.pamirs.framework.gateways.constant.RsqlConstant;
import pro.shushi.pamirs.framework.gateways.rsql.enmu.RsqlExpEnumerate;
import pro.shushi.pamirs.framework.gateways.util.BooleanHelper;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.fun.Data;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.util.PStringUtils;
import pro.shushi.pamirs.meta.constant.RSqlConstants;
import pro.shushi.pamirs.meta.constant.SqlConstants;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;
import pro.shushi.pamirs.meta.util.FieldUtils;

import java.util.*;

/**
 * rql关系型查询增强(数据库)
 */
@Data
@Slf4j
public class RsqlRelation implements SqlConstants {

    public static final List<String> OTHER = Arrays.asList((Long.MIN_VALUE) + "");

    private String property;
    private ComparisonOperator operator;
    private List arguments;

    private int propertyIndex;

    private ModelConfig model;
    private ModelFieldConfig relationField;
    private ModelFieldConfig childField;
    private ModelConfig childModel;

    public RsqlRelation(String property,
                        ComparisonOperator operator,
                        List<String> arguments,
                        ModelConfig model) {
        construct(property.split(RSqlConstants.ESCAPE_POINT), operator, arguments, model);
    }

    public RsqlRelation(String[] fields,
                        ComparisonOperator operator,
                        List<String> arguments,
                        ModelConfig model) {
        construct(fields, operator, arguments, model);
    }

    private void construct(String[] fields, ComparisonOperator operator, List<String> arguments, ModelConfig model) {
        this.propertyIndex = -1;
        String field0, field1;
        if (fields.length == 3) {
            field0 = fields[0];
            if (fields[1].charAt(0) == RsqlCharacterConstant.JSON_SEARCH_INDEX_CHARACTER && fields[1].charAt(fields[1].length() - 1) == RsqlCharacterConstant.JSON_SEARCH_INDEX_CHARACTER) {
                try {
                    this.propertyIndex = Integer.parseInt(fields[1].substring(1, fields[1].length() - 1));
                } catch (NumberFormatException e) {
                    throw PamirsException.construct(RsqlExpEnumerate.BASE_NO_SUPPORT_JSON_SEARCH_INDEX_ERROR, e).errThrow();
                }
            }
            field1 = fields[2];
        } else if (fields.length != RsqlConstant.SUPPORTED_RELATION_PROPERTY_COUNT) {
            throw PamirsException.construct(RsqlExpEnumerate.BASE_NO_SUPPORT_RELATION_RSQL_ERROR).errThrow();
        } else {
            field0 = fields[0];
            field1 = fields[1];
        }
        this.relationField = PamirsSession.getContext().getModelField(model.getModel(), field0);
        this.childModel = PamirsSession.getContext().getModelConfig(relationField.getReferences());
        this.childField = Optional.of(PamirsSession.getContext().getModelField(childModel.getModel(), field1)).orElseThrow(() -> PamirsException.construct(RsqlExpEnumerate.BASE_NO_MATCH_ARGUMENT_ERROR).errThrow());
        this.property = field0 + CharacterConstants.SEPARATOR_DOT + field1;
        this.operator = operator;
        this.arguments = arguments;
        this.model = model;
        if (TtypeEnum.O2M.value().equals(relationField.getTtype())) {
            one2many();
        } else if (TtypeEnum.O2O.value().equals(relationField.getTtype()) || TtypeEnum.M2O.value().equals(relationField.getTtype())) {
            many2one();
        } else if (TtypeEnum.M2M.value().equals(relationField.getTtype())) {
            many2many();
        }
    }

    /**
     * 多对一
     * 关联关系：以子模型的字段作为关联字段
     * 返回子模型的关联字段集合
     */
    private void many2one() {
        if (jsonFunctionProcess(false)) {
            return;
        }
        //case 1.搜索的字段是relationField
        int i = 0;
        for (String referenceField : relationField.getReferenceFields()) {
            if (referenceField.equals(childField.getName())) {
                this.property = relationField.getRelationFields().get(i);
                return;
            }
            i++;
        }
        RsqlQuery result = new RsqlSpecification(
                childField.getField(),
                this.operator,
                this.arguments,
                this.childModel
        ).toQuery();
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.setModel(this.childModel.getModel());
        wrapper.apply(result.getWhere().toString());
        List list = Models.data().queryListByWrapper(wrapper);
        Map<String, List<Object>> mainQueryMap = new HashMap<>();
        if (CollectionUtils.isEmpty(list)) {
            list = OTHER;
            this.arguments = list;
            this.operator = RsqlSearchOperation.IN.getOperator();
            this.property = ID;
            return;
        }
        for (Object object : list) {
            int a = 0;
            for (String f : this.relationField.getReferenceFields()) {
                Object fieldValue = FieldUtils.getReferenceFieldValue(object, relationField.getReferences(), f);
                if (fieldValue == null) {
                    continue;
                }
                String mainColm = this.relationField.getRelationFields().get(a);
                List<Object> mainColumnList = mainQueryMap.computeIfAbsent(mainColm, k -> new ArrayList<>());
                mainColumnList.add(fieldValue);
                a++;
            }
        }
        for (String field : mainQueryMap.keySet()) {
            this.arguments = mainQueryMap.get(field);
            this.operator = RsqlSearchOperation.IN.getOperator();
            this.property = field;
        }
    }

//    /**
//     * 一对多
//     * 关联关系：以主模型的字段做为关联字段
//     * 查询关联模型，转换成多对一的场景，取关联字段并集，并根据关联字段再查询一次取id并集
//     */
//    private void one2many() {
//        /**
//         * case1：模型a有【fieldA】【fieldB】2个字段，mangy2one模型b的【fieldC】字段
//         * a.fieldA ===> b.fieldC 【字段名：fieldAfieldC】
//         * a.fieldB ===> b.fieldC 【字段名：fieldBfieldC】
//         *
//         * case2：模型a有【fieldA】【fieldB】2个字段，mangy2one模型b的【fieldC】【fieldD】2个字段
//         * a.fieldA ===> b.fieldC 【字段名：fieldAfieldC】
//         * a.fieldB ===> b.fieldD 【字段名：fieldBfieldD】
//         */
//        //step 1.查询子模型的many2one字段
//        List<ModelFieldConfig> modelFields = this.childModel.getModelFieldConfigList();
//        modelFields = Optional.of(modelFields.stream()
//                .filter(_field -> (model.getModel().equals(_field.getReferences()) || model.getInherited().contains(_field.getReferences()))
//                        && TtypeEnum.M2O.value().equals(_field.getTtype()))
//                .collect(Collectors.toList()))
//                .orElseThrow(() -> PamirsException.construct(ExpEnumerate.BASE_NO_MATCH_ARGUMENT).errThrow());
//        //step 2.根据子模型的many2one字段，查询出主模型的条件
//        Map<String, List<String>> valueGroup = new HashMap<>();
//        for (ModelFieldConfig modelField : modelFields) {
//            RsqlQuery result = new RsqlSpecification(
//                    childField.getField(),
//                    this.operator,
//                    this.arguments,
//                    this.childModel
//            ).toQuery();
//            QueryWrapper wrapper = new QueryWrapper();
//            wrapper.setModel(this.childModel.getModel());
//            wrapper.apply(result.getWhere().toString());
//            List list = Models.data().queryListByWrapper(wrapper);
//            if (CollectionUtils.isEmpty(list)) {
//                list = OTHER;
//                //step 3.根据主模型的条件，查询出主模型的所有id
//                this.arguments = list;
//                this.operator = RsqlSearchOperation.IN.getOperator();
//                this.property = ID;
//                return;
//            }
//            Map<String, List<Object>> mainQueryMap = new HashMap<>();
//            for (Object object : list) {
//                int i = 0;
//                for (String f : this.relationField.getReferenceFields()) {
//                    Object fieldValue = FieldUtils.getFieldValue(object, f);
//                    if (fieldValue == null) {
//                        continue;
//                    }
//                    String mainColm = this.relationField.getRelationFields().get(i);
//                    List<Object> mainColumnList = mainQueryMap.get(mainColm);
//                    if (mainColumnList == null) {
//                        mainColumnList = new ArrayList<>();
//                        mainQueryMap.put(mainColm, mainColumnList);
//                    }
//                    mainColumnList.add(fieldValue);
//                    i++;
//                }
//            }
//            for (String field : mainQueryMap.keySet()) {
//                this.arguments = mainQueryMap.get(field);
//                this.operator = RsqlSearchOperation.IN.getOperator();
//                this.property = field;
//            }
//        }
//
//
//    }

    private void one2many() {
        if (jsonFunctionProcess(true)) {
            return;
        }
        String[] properties = property.split(CharacterConstants.SEPARATOR_ESCAPE_DOT);
        if (properties.length == RsqlConstant.SUPPORTED_RELATION_PROPERTY_COUNT) {
            String referenceField = properties[1];
            List<String> relationFields = relationField.getRelationFields();
            List<String> referenceFields = relationField.getReferenceFields();
            List<Object> referenceList = queryReferenceList(PStringUtils.fieldName2Column(referenceField));
            referenceList = queryRelationList(referenceList, relationField.getModel(), relationFields, relationField.getReferences(), referenceFields);
            if (referenceList == null) {
                returnEmptyResult();
                return;
            }
            finalQueryProcessor(referenceList);
            return;
        }
        throw PamirsException.construct(RsqlExpEnumerate.BASE_NO_SUPPORT_RELATION_RSQL_ERROR).errThrow();
    }

    /**
     * 多对多
     * 关联关系：子模型的字段作为关联字段，并以主模型的id关联该关联字段
     * 查询关联表，查询关联字段的数据，并根据关联自担查询关联表，返回主模型的id并集
     */
    private void many2many() {
        if (jsonFunctionProcess(true)) {
            return;
        }
        String[] properties = property.split(CharacterConstants.SEPARATOR_ESCAPE_DOT);
        if (properties.length == RsqlConstant.SUPPORTED_RELATION_PROPERTY_COUNT) {
            String referenceField = properties[1];
            String model = relationField.getModel();
            String through = relationField.getThrough();
            String reference = relationField.getReferences();
            List<String> relationFields = relationField.getRelationFields();
            List<String> referenceFields = relationField.getReferenceFields();
            List<String> throughRelationFields = relationField.getThroughRelationFields();
            List<String> throughReferenceFields = relationField.getThroughReferenceFields();
            List<Object> relationList = queryReferenceList(PStringUtils.fieldName2Column(referenceField));
            relationList = queryRelationList(relationList, through, throughReferenceFields, reference, referenceFields);
            if (relationList == null) {
                returnEmptyResult();
                return;
            }
            relationList = queryRelationList(relationList, model, relationFields, through, throughRelationFields);
            if (relationList == null) {
                returnEmptyResult();
                return;
            }
            finalQueryProcessor(relationList);
            return;
        }
        throw PamirsException.construct(RsqlExpEnumerate.BASE_NO_SUPPORT_RELATION_RSQL_ERROR).errThrow();
    }

    private List<Object> queryReferenceList(String argument) {
        QueryWrapper wrapper = Pops.query().from(relationField.getReferences());
        computeNewComparisonOperator(wrapper, argument, this.getArguments().get(0));
        return Models.data().queryListByWrapper(wrapper);
    }

    private void returnEmptyResult() {
        this.property = this.model.getPk().get(0);
        this.operator = RsqlSearchOperation.IS_NULL.getOperator();
        this.arguments = Collections.singletonList(true);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private ComparisonOperator computeNewComparisonOperator(QueryWrapper wrapper, String column, Object argument) {
        switch (operator.getSymbol()) {
            case "==":
            case "=eq=":
                wrapper.eq(column, argument);
                return RsqlSearchOperation.IN.getOperator();
            case ">":
            case "=gt=":
                wrapper.gt(column, argument);
                return RsqlSearchOperation.IN.getOperator();
            case ">=":
            case "=ge=":
                wrapper.ge(column, argument);
                return RsqlSearchOperation.IN.getOperator();
            case "<":
            case "=lt=":
                wrapper.lt(column, argument);
                return RsqlSearchOperation.IN.getOperator();
            case "<=":
            case "=le=":
                wrapper.le(column, argument);
                return RsqlSearchOperation.IN.getOperator();
            case "=like=":
                wrapper.like(column, argument);
                return RsqlSearchOperation.IN.getOperator();
            case "=notlike=":
                wrapper.notLike(column, argument);
                return RsqlSearchOperation.IN.getOperator();
            case "!=":
            case "=ne=":
                wrapper.ne(column, argument);
                return RsqlSearchOperation.IN.getOperator();
            case "=isnull=":
                if (BooleanHelper.isTrue(argument)) {
                    wrapper.isNull(column);
                } else {
                    wrapper.isNotNull(column);
                }
                return RsqlSearchOperation.IN.getOperator();
            case "=notnull=":
                if (BooleanHelper.isTrue(argument)) {
                    wrapper.isNotNull(column);
                } else {
                    wrapper.isNull(column);
                }
                return RsqlSearchOperation.IN.getOperator();
            default:
                throw PamirsException.construct(RsqlExpEnumerate.BASE_NO_SUPPORT_RELATION_RSQL_ERROR).errThrow();
        }
    }


    private List<Object> queryRelationList(List<Object> referenceList,
                                           String queryModel,
                                           List<String> relationFields,
                                           String referenceModel,
                                           List<String> referenceFields) {
        Map<String, List<Object>> relationFieldValueMap = new HashMap<>(8);
        for (Object item : referenceList) {
            Map<String, Object> relationFieldValueItem = new HashMap<>();
            boolean isAddValue = true;
            for (int i = 0; i < referenceFields.size(); i++) {
                String referenceField = referenceFields.get(i);
                Object value;
                if (FieldUtils.isConstantRelationFieldValue(referenceField)) {
                    value = referenceField.substring(1, referenceField.length() - 1);
                } else {
                    value = FieldUtils.getReferenceFieldValue(item, referenceModel, referenceField);
                }
                if (value != null) {
                    String relationFieldItem = relationFields.get(i);
                    if (FieldUtils.isConstantRelationFieldValue(relationFieldItem)) {
                        if (!relationFieldItem.substring(1, relationFieldItem.length() - 1).equals(value)) {
                            isAddValue = false;
                        }
                    } else {
                        relationFieldValueItem.put(relationFields.get(i), value);
                    }
                } else {
                    isAddValue = false;
                }
            }
            if (isAddValue) {
                for (Map.Entry<String, Object> entry : relationFieldValueItem.entrySet()) {
                    String fieldKey = entry.getKey();
                    List<Object> relationFieldValueList = relationFieldValueMap.computeIfAbsent(fieldKey, k -> new ArrayList<>());
                    relationFieldValueList.add(entry.getValue());
                }
            }
        }
        if (relationFieldValueMap.isEmpty()) {
            return null;
        }
        QueryWrapper<Object> wrapper = Pops.query().from(queryModel);
        List<String> columnList = new ArrayList<>();
        List<Object>[] valuesList = new ArrayList[relationFieldValueMap.values().size()];
        int index = 0;
        for (Map.Entry<String, List<Object>> entry : relationFieldValueMap.entrySet()) {
            columnList.add(PStringUtils.fieldName2Column(entry.getKey()));
            valuesList[index++] = entry.getValue();
        }
        wrapper.in(columnList, valuesList);
        List<Object> resultList = Models.data().queryListByWrapper(wrapper);
        if (CollectionUtils.isEmpty(resultList)) {
            return null;
        }
        return resultList;
    }

    private void finalQueryProcessor(List<Object> relationList) {
        List<Object> ids = new ArrayList<>();
        for (Object item : relationList) {
            ids.add(FieldUtils.getFieldValue(item, SqlConstants.ID));
        }
        this.property = SqlConstants.ID;
        this.operator = RsqlSearchOperation.IN.getOperator();
        this.arguments = ids;
    }

    private boolean jsonFunctionProcess(boolean isArray) {
        if (relationField.getStore()) {
            String serialize = relationField.getStoreSerialize();
            if (StringUtils.isBlank(serialize)
                    || Field.serialize.NON.equals(serialize)
                    || Field.serialize.JSON.equals(serialize)) {
                RsqlSearchOperation operation = RsqlSearchOperation.getSimpleOperator(operator);
                if (operation == null) {
                    throw PamirsException.construct(RsqlExpEnumerate.BASE_NO_SUPPORT_OPERATION_RSQL_ERROR).errThrow();
                }
                if (isArray) {
                    String jsonArrayPrefix = this.propertyIndex == -1 ? RsqlConstant.JSON_ARRAY_PREFIX : RsqlConstant.getJsonArrayIndex(this.propertyIndex);
                    switch (operation) {
                        case EQUAL:
                        case NOT_EQUAL:
                        case IS_NULL:
                        case IS_NOT_NULL:
                            this.property = String.format(RsqlConstant.JSON_CONTAINS_FUNCTION_FORMAT,
                                    String.format(RsqlConstant.JSON_EXTRACT_FUNCTION_FORMAT,
                                            Configs.wrap(relationField).getSqlSelect(true),
                                            jsonArrayPrefix + childField.getName()
                                    ),
                                    RsqlCharacterConstant.STRING_FORMAT_PLACEHOLDER
                            ).concat(CharacterConstants.SEPARATOR_COLON).concat(this.property);
                            break;
                        case LIKE:
                        case NOT_LIKE:
                            this.property = String.format(RsqlConstant.JSON_SEARCH_FUNCTION_FORMAT,
                                    String.format(RsqlConstant.JSON_EXTRACT_FUNCTION_FORMAT,
                                            Configs.wrap(relationField).getSqlSelect(true),
                                            jsonArrayPrefix + childField.getName()
                                    ),
                                    RsqlCharacterConstant.STRING_FORMAT_PLACEHOLDER
                            ).concat(CharacterConstants.SEPARATOR_COLON).concat(this.property);
                            break;
                        default:
                            throw PamirsException.construct(RsqlExpEnumerate.BASE_NO_SUPPORT_OPERATION_RSQL_ERROR).errThrow();
                    }
                } else {
                    this.property = String.format(RsqlConstant.JSON_EXTRACT_FUNCTION_FORMAT,
                            Configs.wrap(relationField).getSqlSelect(true),
                            RsqlConstant.JSON_OBJECT_PREFIX + childField.getName()
                    ).concat(CharacterConstants.SEPARATOR_COLON).concat(this.property);
                }
                return true;
            }
            throw PamirsException.construct(RsqlExpEnumerate.BASE_NO_SUPPORT_RELATION_RSQL_ERROR).errThrow();
        }
        return false;
    }
}
