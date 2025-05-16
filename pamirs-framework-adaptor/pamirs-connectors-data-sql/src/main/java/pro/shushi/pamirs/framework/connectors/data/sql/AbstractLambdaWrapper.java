package pro.shushi.pamirs.framework.connectors.data.sql;

import org.apache.commons.collections4.CollectionUtils;
import pro.shushi.pamirs.framework.configure.staticloader.TableInfoFetcher;
import pro.shushi.pamirs.framework.connectors.data.sql.config.Configs;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.api.session.RequestContext;
import pro.shushi.pamirs.meta.base.D;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.lambda.Getter;
import pro.shushi.pamirs.meta.common.lambda.LambdaUtil;
import pro.shushi.pamirs.meta.common.util.BitUtil;
import pro.shushi.pamirs.meta.enmu.SerializeEnum;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;
import pro.shushi.pamirs.meta.util.FieldUtils;

import java.lang.invoke.SerializedLambda;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static java.util.stream.Collectors.joining;
import static pro.shushi.pamirs.framework.connectors.data.sql.SqlKeyword.*;

/**
 * Lambda 语法使用 Wrapper
 * <p>统一处理解析 lambda 获取 column</p>
 */
@SuppressWarnings("serial")
public abstract class AbstractLambdaWrapper<T, Children extends AbstractLambdaWrapper<T, Children>>
        extends AbstractWrapper<T, Getter<T, ?>, Children> {

    @Override
    protected void initEntityClass() {
        super.initEntityClass();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected ISqlSegment columnsToString(Getter<T, ?>... columns) {
        return columnsToString(true, columns);
    }

    @SuppressWarnings("unchecked")
    protected ISqlSegment columnsToString(boolean onlyColumn, Getter<T, ?>... columns) {
        return () -> Arrays.stream(columns).map(i -> columnToString(i, onlyColumn)).map(ISqlSegment::getSqlSegment)
                .collect(joining(CharacterConstants.SEPARATOR_COMMA));
    }

    @Override
    protected ISqlSegment columnToString(Getter<T, ?> column) {
        return columnToString(column, true);
    }

    protected ISqlSegment columnToString(Getter<T, ?> column, boolean onlyColumn) {
        if (null == column) {
            return () -> CharacterConstants.SEPARATOR_EMPTY;
        }
        String columnString = getColumn(column, onlyColumn);
        return () -> columnString;
    }

    @Override
    protected ISqlSegment columnsToString(Collection<Getter<T, ?>> columns) {
        return columnsToString(columns, true);
    }

    protected ISqlSegment columnsToString(Collection<Getter<T, ?>> columns, boolean onlyColumn) {
        if (CollectionUtils.isEmpty(columns)) {
            return () -> CharacterConstants.SEPARATOR_EMPTY;
        }
        String column = columns.stream().map(v -> getColumn(v, onlyColumn))
                .collect(joining(CharacterConstants.SEPARATOR_COMMA, CharacterConstants.LEFT_BRACKET, CharacterConstants.RIGHT_BRACKET));
        return () -> column;
    }

    /**
     * 获取 SerializedLambda 对应的列信息，从 lambda 表达式中推测实体类
     * <p>
     * 如果获取不到列信息，那么本次条件组装将会失败
     *
     * @param getter     lambda 表达式
     * @param onlyColumn 如果是，结果: "name", 如果否： "name" as "name"
     * @return 列
     * @see SerializedLambda#getImplClass()
     * @see SerializedLambda#getImplMethodName()
     */
    private String getColumn(Getter<T, ?> getter, boolean onlyColumn) {
        return Configs.wrap(fetchModelFieldConfig(getter)).getSqlSelect(onlyColumn);
    }

    public Children rel(Getter<T, ?> column, Object val) {
        return rel(true, column, val);
    }

    public Children rel(boolean condition, Getter<T, ?> column, Object val) {
        return rel0(condition, column, val, false);
    }

    public Children nrel(Getter<T, ?> column, Object val) {
        return nrel(true, column, val);
    }

    public Children nrel(boolean condition, Getter<T, ?> column, Object val) {
        return rel0(condition, column, val, true);
    }

    public Children has(Getter<T, ?> column, Object val) {
        return has(true, column, val);
    }

    public Children has(boolean condition, Getter<T, ?> column, Object val) {
        return has0(condition, column, val, false);
    }

    public Children hasnt(Getter<T, ?> column, Object val) {
        return hasnt(true, column, val);
    }

    public Children hasnt(boolean condition, Getter<T, ?> column, Object val) {
        return has0(condition, column, val, true);
    }

    public Children hasor(Getter<T, ?> column, Object val) {
        return hasor(true, column, val);
    }

    public Children hasor(boolean condition, Getter<T, ?> column, Object val) {
        return hasor0(condition, column, val, true);
    }

    public Children hasntor(Getter<T, ?> column, Object val) {
        return hasntor(true, column, val);
    }

    public Children hasntor(boolean condition, Getter<T, ?> column, Object val) {
        return hasor0(condition, column, val, false);
    }

    public Children multi(Getter<T, ?> column, Collection<?> coll) {
        return multi(true, column, coll);
    }

    public Children multi(boolean condition, Getter<T, ?> column, Collection<?> coll) {
        return multi0(condition, column, coll, false);
    }

    public Children mulnot(Getter<T, ?> column, Collection<?> coll) {
        return mulnot(true, column, coll);
    }

    public Children mulnot(boolean condition, Getter<T, ?> column, Collection<?> coll) {
        return multi0(condition, column, coll, true);
    }

    private Children rel0(boolean condition, Getter<T, ?> column, Object val, boolean isNot) {
        if (val instanceof D) {
            if (isNot) {
                return addCondition0(condition, column, NE, val);
            } else {
                return addCondition0(condition, column, EQ, val);
            }
        } else if (val instanceof Collection) {
            if (isNot) {
                this.not(condition);
            }
            return in0(condition, column, (Collection<?>) val);
        }
        return typedThis;
    }

    protected Children addCondition0(boolean condition, Getter<T, ?> column, SqlKeyword sqlKeyword, Object val) {
        return addSingleRelationCondition(condition, column, val, modelFieldConfig -> {
            RequestContext requestContext = PamirsSession.getContext();
            String model = modelFieldConfig.getModel();
            List<String> relationFields = modelFieldConfig.getRelationFields();
            List<String> referenceFields = modelFieldConfig.getReferenceFields();
            AtomicInteger aci = new AtomicInteger(0);
            for (String relationField : relationFields) {
                expression.add(() -> Configs.wrap(requestContext.getModelField(model, relationField)).getSqlSelect(true), sqlKeyword,
                        () -> formatSql("{0}", FieldUtils.getReferenceFieldValue(val, modelFieldConfig.getReferences(), referenceFields.get(aci.getAndIncrement()))));
            }
        }, modelFieldConfig -> expression.add(columnToString(column), sqlKeyword, () -> formatSql("{0}", val)));
    }

    protected Children in0(boolean condition, Getter<T, ?> column, Collection<?> coll) {
        return addCollectionRelationCondition(condition, column, coll, (modelFieldConfig, entries) -> {
            RequestContext requestContext = PamirsSession.getContext();
            String model = modelFieldConfig.getModel();
            for (Map.Entry<String, List<Object>> entry : entries) {
                expression.add(() -> Configs.wrap(requestContext.getModelField(model, entry.getKey())).getSqlSelect(true), IN, inExpression(entry.getValue()));
            }
        }, modelFieldConfig -> expression.add(columnToString(column), IN, inExpression(coll)));
    }

    protected Children has0(boolean condition, Getter<T, ?> column, Object val, boolean isNot) {
        return doIt0(condition, column, modelFieldConfig -> {
            throw new UnsupportedOperationException("Invalid operation");
        }, modelFieldConfig -> {
            if (TtypeEnum.ENUM.value().equals(modelFieldConfig.getTtype())) {
                if (modelFieldConfig.getMulti() && SerializeEnum.BIT.value().equals(modelFieldConfig.getStoreSerialize())) {
                    Collection<?> coll;
                    if (!(val instanceof Collection)) {
                        coll = Collections.singletonList(val);
                    } else {
                        coll = (Collection<?>) val;
                    }
                    long value = computeBitEnumCollectionValue(coll, modelFieldConfig.getField());

                    if (isNot) {
                        expression.add(() -> Configs.wrap(modelFieldConfig).getSqlSelect(true) + " & " + value, NE, () -> formatSql("{0}", value));
                    } else {
                        expression.add(() -> Configs.wrap(modelFieldConfig).getSqlSelect(true) + " & " + value, EQ, () -> formatSql("{0}", value));
                    }
                    return;
                }
            } else if (TtypeEnum.INTEGER.value().equals(modelFieldConfig.getTtype())) {
                if (isNot) {
                    expression.add(() -> Configs.wrap(modelFieldConfig).getSqlSelect(true) + " & " + val, NE, () -> formatSql("{0}", val));
                } else {
                    expression.add(() -> Configs.wrap(modelFieldConfig).getSqlSelect(true) + " & " + val, EQ, () -> formatSql("{0}", val));
                }
                return;
            }

            throw new UnsupportedOperationException("Invalid operation");
        });
    }

    protected Children hasor0(boolean condition, Getter<T, ?> column, Object val, boolean isNot) {
        return doIt0(condition, column, modelFieldConfig -> {
            throw new UnsupportedOperationException("Invalid operation");
        }, modelFieldConfig -> {
            if (TtypeEnum.ENUM.value().equals(modelFieldConfig.getTtype())) {
                if (modelFieldConfig.getMulti() && SerializeEnum.BIT.value().equals(modelFieldConfig.getStoreSerialize())) {
                    Collection<?> coll;
                    if (!(val instanceof Collection)) {
                        coll = Collections.singletonList(val);
                    } else {
                        coll = (Collection<?>) val;
                    }
                    long value = computeBitEnumCollectionValue(coll, modelFieldConfig.getField());

                    if (isNot) {
                        expression.add(() -> Configs.wrap(modelFieldConfig).getSqlSelect(true) + " & " + value, GT, () -> "0");
                    } else {
                        expression.add(() -> Configs.wrap(modelFieldConfig).getSqlSelect(true) + " & " + value, EQ, () -> "0");
                    }
                    return;
                }
            } else if (TtypeEnum.INTEGER.value().equals(modelFieldConfig.getTtype())) {
                if (isNot) {
                    expression.add(() -> Configs.wrap(modelFieldConfig).getSqlSelect(true) + " & " + val, GT, () -> "0");
                } else {
                    expression.add(() -> Configs.wrap(modelFieldConfig).getSqlSelect(true) + " & " + val, EQ, () -> "0");
                }
                return;
            }

            throw new UnsupportedOperationException("Invalid operation");
        });
    }


    protected Children multi0(boolean condition, Getter<T, ?> column, Collection<?> coll, boolean isNot) {
        return doIt0(condition, column, modelFieldConfig -> {
            throw new UnsupportedOperationException("Invalid operation");
        }, modelFieldConfig -> {
            if (TtypeEnum.ENUM.value().equals(modelFieldConfig.getTtype())) {
                if (modelFieldConfig.getMulti() && SerializeEnum.BIT.value().equals(modelFieldConfig.getStoreSerialize())) {
                    long value = computeBitEnumCollectionValue(coll, modelFieldConfig.getField());
                    if (isNot) {
                        expression.add(() -> Configs.wrap(modelFieldConfig).getSqlSelect(true), NE, () -> formatSql("{0}", value));
                    } else {
                        expression.add(() -> Configs.wrap(modelFieldConfig).getSqlSelect(true), EQ, () -> formatSql("{0}", value));
                    }
                    return;
                }
            }

            throw new UnsupportedOperationException("Invalid operation");
        });
    }

    private Children addCollectionRelationCondition(boolean condition, Getter<T, ?> column, Object data,
                                                    BiConsumer<ModelFieldConfig, Set<Map.Entry<String, List<Object>>>> consumer,
                                                    Consumer<ModelFieldConfig> notRelationConsumer) {
        return doIt0(condition, column, modelFieldConfig -> {
            List<String> relationFields = modelFieldConfig.getRelationFields();
            List<String> referenceFields = modelFieldConfig.getReferenceFields();
            Map<String, List<Object>> resultMap = new HashMap<>();
            int i;
            if (data instanceof Collection) {
                Collection<?> collection = (Collection<?>) data;
                boolean isInterrupt = false;
                for (Object item : collection) {
                    if (item instanceof D) {
                        i = 0;
                        for (String relationField : relationFields) {
                            resultMap.computeIfAbsent(relationField, k -> new ArrayList<>())
                                    .add(FieldUtils.getReferenceFieldValue(item, modelFieldConfig.getReferences(), referenceFields.get(i++)));
                        }
                    } else {
                        isInterrupt = true;
                        break;
                    }
                }
                if (isInterrupt) {
                    throw new RuntimeException(String.format("约定检查，多对一和一对一字段必须传入对象或对象集合 [field %s]", modelFieldConfig.getField()));
                }
            } else if (data instanceof D) {
                i = 0;
                for (String relationField : relationFields) {
                    resultMap.computeIfAbsent(relationField, k -> new ArrayList<>())
                            .add(FieldUtils.getReferenceFieldValue(data, modelFieldConfig.getReferences(), referenceFields.get(i++)));
                }
            } else {
                throw new RuntimeException(String.format("约定检查，多对一和一对一字段必须传入对象或对象集合 [field %s]", modelFieldConfig.getField()));
            }
            consumer.accept(modelFieldConfig, resultMap.entrySet());
        }, notRelationConsumer);
    }

    private Children addSingleRelationCondition(boolean condition, Getter<T, ?> column, Object data,
                                                Consumer<ModelFieldConfig> consumer,
                                                Consumer<ModelFieldConfig> notRelationConsumer) {
        return doIt0(condition, column, modelFieldConfig -> {
            if (data instanceof D) {
                consumer.accept(modelFieldConfig);
            } else {
                throw new RuntimeException(String.format("约定检查，多对一和一对一字段必须传入对象 [field %s]", modelFieldConfig.getField()));
            }
        }, notRelationConsumer);
    }

    private Children doIt0(boolean condition, Getter<T, ?> column,
                           Consumer<ModelFieldConfig> consumer,
                           Consumer<ModelFieldConfig> notRelationConsumer) {
        if (!condition) {
            return typedThis;
        }
        ModelFieldConfig modelFieldConfig = fetchModelFieldConfig(column);
        String ttype = modelFieldConfig.getTtype();
        if (TtypeEnum.M2O.value().equals(ttype) || TtypeEnum.O2O.value().equals(ttype)) {
            consumer.accept(modelFieldConfig);
            return typedThis;
        }
        notRelationConsumer.accept(modelFieldConfig);
        return typedThis;
    }

    protected ModelFieldConfig fetchModelFieldConfig(Getter<T, ?> getter) {
        String fieldName = LambdaUtil.fetchFieldName(getter);
        Class<?> clazz = LambdaUtil.fetchClazz(getter);
        return TableInfoFetcher.fetchModelFieldConfig(clazz, fieldName);
    }

    private long computeBitEnumCollectionValue(Collection<?> collection, String field) {
        long result = 0L;
        for (Object item : collection) {
            if (item == null) {
                continue;
            }
            Long itemValue = BitUtil.longValue(item);
            if (itemValue == null) {
                throw new RuntimeException(String.format("无法识别的枚举定义 [field %s]", field));
            }
            result |= itemValue;
        }
        return result;
    }
}
