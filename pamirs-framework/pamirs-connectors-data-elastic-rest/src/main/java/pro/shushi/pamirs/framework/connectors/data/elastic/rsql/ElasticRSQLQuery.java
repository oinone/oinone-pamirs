package pro.shushi.pamirs.framework.connectors.data.elastic.rsql;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.json.JsonData;
import pro.shushi.pamirs.meta.common.enmu.BaseEnum;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

import java.util.ArrayList;
import java.util.List;


/**
 * ElasticRSQLQuery
 *
 * @author yakir on 2022/09/07 18:09.
 */
@SuppressWarnings("rawtypes")
public class ElasticRSQLQuery {

    private String arg;

    private final List paramValues;

    private Query query;

    ElasticRSQLQuery() {
        this.paramValues = new ArrayList();
    }

    public static class ElasticBoolQuery extends ElasticRSQLQuery {

        private final BoolQuery.Builder boolBuilder;

        private ElasticBoolQuery() {
            boolBuilder = QueryBuilders.bool();
        }

        public static ElasticBoolQuery bool() {
            return new ElasticBoolQuery();
        }

        public ElasticBoolQuery and(ElasticRSQLQuery query) {
            this.boolBuilder.must(query.build());
            return this;
        }

        public ElasticBoolQuery or(ElasticRSQLQuery query) {
            this.boolBuilder.should(query.build());
            return this;
        }

        public ElasticBoolQuery not(ElasticRSQLQuery query) {
            this.boolBuilder.mustNot(query.build());
            return this;
        }

        public ElasticBoolQuery and(ElasticBoolQuery query) {
            BoolQuery _query = query.builder().build();
            this.boolBuilder.must(new Query(_query));
            return this;
        }

        public ElasticBoolQuery or(ElasticBoolQuery query) {
            BoolQuery _query = query.builder().build();
            this.boolBuilder.should(new Query(_query));
            return this;
        }

        public ElasticBoolQuery not(ElasticBoolQuery query) {
            BoolQuery _query = query.builder().build();
            this.boolBuilder.mustNot(new Query(_query));
            return this;
        }

        public BoolQuery.Builder builder() {
            return this.boolBuilder;
        }
    }

    public ElasticRSQLQuery isNull() {
        ExistsQuery q = QueryBuilders.exists()
                .field(arg)
                .build();
        this.query = new Query(q);
        return this;
    }

    public ElasticRSQLQuery equal(Object query) {
        FieldValue.Kind kind = kind(query);
        FieldValue fieldValue = fieldValue(kind, query);
        TermQuery q = QueryBuilders.term()
                .field(arg)
                .queryName(arg)
                .value(fieldValue)
                .build();
        this.query = new Query(q);
        return this;
    }

    public ElasticRSQLQuery equalTextKeyword(Object query) {
        FieldValue.Kind kind = kind(query);
        FieldValue fieldValue = fieldValue(kind, query);
        TermQuery q = QueryBuilders.term()
                .field(arg + ".keyword")
                .queryName(arg + ".keyword")
                .value(fieldValue)
                .build();
        this.query = new Query(q);
        return this;
    }

    public ElasticRSQLQuery like(String query) {
        QueryStringQuery queryString = QueryBuilders.queryString()
                .fields(arg)
                .query(query)
                .build();
        this.query = new Query(queryString);
        return this;
    }

    public ElasticRSQLQuery notLike(String query) {
        QueryStringQuery queryString = QueryBuilders.queryString()
                .fields(arg)
                .query(query)
                .build();
        this.query = new Query(queryString);
        return this;
    }

    public ElasticRSQLQuery isNotNull() {
        ExistsQuery q = QueryBuilders.exists()
                .field(arg)
                .build();
        this.query = new Query(q);
        return this;
    }

    public ElasticRSQLQuery notEqual(Object query) {
        FieldValue.Kind kind = kind(query);
        FieldValue fieldValue = fieldValue(kind, query);
        TermQuery q = QueryBuilders.term()
                .field(arg)
                .queryName(arg)
                .value(fieldValue)
                .build();
        this.query = new Query(q);
        return this;
    }

    public ElasticRSQLQuery greaterThan(Object query) {
        RangeQuery gt = QueryBuilders.range()
                .field(arg)
                .gt(JsonData.of(query))
                .build();
        this.query = new Query(gt);
        return this;
    }

    public ElasticRSQLQuery greaterThanOrEqualTo(Object query) {
        RangeQuery gte = QueryBuilders.range()
                .field(arg)
                .gte(JsonData.of(query))
                .build();
        this.query = new Query(gte);
        return this;
    }

    public ElasticRSQLQuery lessThan(Object query) {
        RangeQuery lt = QueryBuilders.range()
                .field(arg)
                .lt(JsonData.of(query))
                .build();
        this.query = new Query(lt);
        return this;
    }

    public ElasticRSQLQuery lessThanOrEqualTo(Object query) {
        RangeQuery lte = QueryBuilders.range()
                .field(arg)
                .lte(JsonData.of(query))
                .build();
        this.query = new Query(lte);
        return this;
    }

    public ElasticRSQLQuery in(List<Object> args) {
        FieldValue.Kind kind = kind(args);
        List<FieldValue> fieldValues = new ArrayList<>();
        for (Object arg : args) {
            FieldValue fieldValue = fieldValue(kind, arg);
            fieldValues.add(fieldValue);
        }

        TermsQuery query = QueryBuilders.terms()
                .field(arg)
                .terms(TermsQueryField.of(_builder -> _builder.value(fieldValues)))
                .build();
        this.query = new Query(query);
        return this;
    }

    public ElasticRSQLQuery notIn(List<Object> args) {
        FieldValue.Kind kind = kind(args);
        List<FieldValue> fieldValues = new ArrayList<>();
        for (Object arg : args) {
            FieldValue fieldValue = fieldValue(kind, arg);
            fieldValues.add(fieldValue);
        }

        TermsQuery query = QueryBuilders.terms()
                .field(arg)
                .terms(TermsQueryField.of(_builder -> _builder.value(fieldValues)))
                .build();
        this.query = new Query(query);
        return this;
    }

    private FieldValue fieldValue(FieldValue.Kind kind, Object obj) {
        FieldValue.Builder fieldValueBuilder = new FieldValue.Builder();
        switch (kind) {
            case Double:
                fieldValueBuilder.doubleValue(Double.parseDouble(obj.toString()));
                break;
            case Long:
                fieldValueBuilder.longValue(Long.parseLong(obj.toString()));
                break;
            case String:
                fieldValueBuilder.stringValue(obj.toString());
                break;
            case Boolean:
                fieldValueBuilder.booleanValue(Boolean.parseBoolean(obj.toString()));
                break;
            case Null:
                fieldValueBuilder.nullValue();
                break;
        }
        return fieldValueBuilder.build();
    }

    private FieldValue.Kind kind(Object obj) {
        if (null == obj) {
            return FieldValue.Kind.String;
        }
        switch (obj.getClass().getName()) {
            case "java.lang.String":
            case "java.math.BigDecimal":
                return FieldValue.Kind.String;
            case "java.util.List":
            case "java.util.ArrayList":
                return kind(((List) obj).get(0));
            case "java.lang.Short":
            case "java.lang.Integer":
            case "java.math.BigInteger":
            case "java.lang.Long":
                return FieldValue.Kind.Long;
            case "java.lang.Float":
            case "java.lang.Double":
                return FieldValue.Kind.Double;
            case "java.lang.Boolean":
                return FieldValue.Kind.Boolean;
            case "java.util.Date":
                //fixme 目前的前后端协议没有约定传递的Date类型是什么 搜索的时候按照用户直接输入的时间字符串传递 20210103
                //return DateFormatUtils.format((Date)obj, DATE_FORMATE);
            default:
                if (obj instanceof IEnum || obj instanceof Enum) {
                    Object value = BaseEnum.getValue(obj);
                    return kind(value);
                }
                return FieldValue.Kind.String;
        }
    }


    // ---

    public Query build() {
        return this.query;
    }

    public void setArg(String arg) {
        this.arg = arg;
    }

    public List getParamValues() {
        return paramValues;
    }
}