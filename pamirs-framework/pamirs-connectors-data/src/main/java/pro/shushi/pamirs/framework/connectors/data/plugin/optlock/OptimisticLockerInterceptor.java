package pro.shushi.pamirs.framework.connectors.data.plugin.optlock;

import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import pro.shushi.pamirs.framework.connectors.data.constant.StatementConstants;
import pro.shushi.pamirs.framework.connectors.data.mapper.context.MapperContext;
import pro.shushi.pamirs.framework.connectors.data.plugin.util.FieldUtil;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.base.D;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.util.FieldUtils;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static pro.shushi.pamirs.framework.connectors.data.enmu.DataExpEnumerate.BASE_NO_OPTIMISTIC_LOCKER_VALUE2_ERROR;
import static pro.shushi.pamirs.framework.connectors.data.enmu.DataExpEnumerate.BASE_NO_OPTIMISTIC_LOCKER_VALUE_ERROR;

/**
 * 乐观锁支持
 * <p>
 * Optimistic Lock Light version
 * <p>Intercept on {@link Executor}.update;</p>
 * <p>Support version types: int/Integer, long/Long, java.util.Date, java.sql.Timestamp</p>
 * <p>For extra types, please define a subclass and override {@code getUpdatedVersionVal}() method.</p>
 * <br>
 * <p>How to use?</p>
 * <p>(1) Define an Entity and add {@link Version} annotation on one entity field.</p>
 * <p>(2) Add {@link pro.shushi.pamirs.framework.connectors.data.plugin.optlock.OptimisticLockerInterceptor} into mybatis plugin.</p>
 * <br>
 * <p>How to work?</p>
 * <p>if update entity with version column=1:</p>
 * <p>(1) no {@link pro.shushi.pamirs.framework.connectors.data.plugin.optlock.OptimisticLockerInterceptor}:</p>
 * <p>SQL: update tbl_test set name='abc' where id=100001;</p>
 * <p>(2) add {@link pro.shushi.pamirs.framework.connectors.data.plugin.optlock.OptimisticLockerInterceptor}:</p>
 * <p>SQL: update tbl_test set name='abc',version=2 where id=100001 and version=1;</p>
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/14 2:11 上午
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Intercepts({@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})})
public class OptimisticLockerInterceptor implements Interceptor {

    private static final String PARAM_UPDATE_METHOD_NAME = "update";

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        MappedStatement ms = (MappedStatement) args[0];
        boolean optimisticLocker = PamirsSession.directive().isOptimisticLocker();
        if (SqlCommandType.UPDATE != ms.getSqlCommandType() || !optimisticLocker) {
            return invocation.proceed();
        }
        Object param = args[1];
        if (param instanceof Map) {
            Map map = (Map) param;
            //updateById(et), update(et, wrapper);
            Object coll = map.getOrDefault(Constants.COLLECTION, null);
            Object cc = map.getOrDefault(StatementConstants.CONDITION_COLLECTION, null);
            map.put(FieldUtil.NAME, FieldUtil.INSTANCE);
            if (null != coll) {
                lowCodeInvoke(map, (List) coll);
            } else if (null != cc) {
                lowCodeInvoke(map, (List) cc);
            } else {
                Object et = map.getOrDefault(Constants.ENTITY, null);
                if (et != null) {
                    // entity
                    String methodId = ms.getId();
                    String methodName = methodId.substring(methodId.lastIndexOf(StringPool.DOT) + 1);
                    String model = MapperContext.model(map);
                    if (StringUtils.isNotBlank(model)) {
                        lowCodeInvoke(methodName, map, et);
                    } else {
                        originInvoke(methodName, map, et);
                    }
                }
            }
        }
        return invocation.proceed();
    }

    private void originInvoke(String methodName, Map map, Object et) throws IllegalAccessException {
        TableInfo tableInfo = TableInfoHelper.getTableInfo(et.getClass());
        if (tableInfo == null || !tableInfo.isWithVersion()) {
            return;
        }
        TableFieldInfo fieldInfo = tableInfo.getVersionFieldInfo();
        Field versionField = fieldInfo.getField();
        // 旧的 version 值
        Object originalVersionVal = versionField.get(et);
        if (originalVersionVal == null) {
            return;
        }
        String versionColumn = fieldInfo.getColumn();
        // 新的 version 值
        Object updatedVersionVal = this.getUpdatedVersionVal(fieldInfo.getPropertyType(), originalVersionVal);
        if (PARAM_UPDATE_METHOD_NAME.equals(methodName)) {
            AbstractWrapper<?, ?, ?> aw = (AbstractWrapper<?, ?, ?>) map.getOrDefault(Constants.WRAPPER, null);
            if (aw == null) {
                UpdateWrapper<?> uw = new UpdateWrapper<>();
                uw.eq(versionColumn, originalVersionVal);
                map.put(Constants.WRAPPER, uw);
            } else {
                aw.apply(versionColumn + " = {0}", originalVersionVal);
            }
        } else {
            map.put(Constants.MP_OPTLOCK_VERSION_COLUMN, versionColumn);
            map.put(Constants.MP_OPTLOCK_VERSION_ORIGINAL, originalVersionVal);
        }
        versionField.set(et, updatedVersionVal);
    }

    private void lowCodeInvoke(String methodName, Map map, Object et) {
        Map etm;
        if (et instanceof Map) {
            etm = (Map) et;
        } else {
            etm = ((D) et).get_d();
            map.put(Constants.ENTITY, etm);
        }
        String model = MapperContext.model(map);
        if (null == model) {
            return;
        }
        ModelFieldConfig optimisticLocker = Optional.ofNullable(PamirsSession.getContext()).map(v -> v.getModelConfig(model))
                .map(ModelConfig::getOptimisticLockerField)
                .flatMap(v -> Optional.ofNullable(PamirsSession.getContext()).map(f -> f.getModelField(model, v)))
                .orElse(null);
        if (null == optimisticLocker) {
            return;
        }
        String optimisticLockerProperty = optimisticLocker.getLname();
        String optimisticLockerColumn = optimisticLocker.getColumn();
        // 旧的 version 值
        Object originalVersionVal = FieldUtils.getFieldValue(etm, optimisticLockerProperty);
        if (null == originalVersionVal) {
            throw PamirsException.construct(BASE_NO_OPTIMISTIC_LOCKER_VALUE_ERROR).errThrow();
        }
        // 新的 version 值
        Object updatedVersionVal = this.getUpdatedVersionVal(originalVersionVal.getClass(), originalVersionVal);
        if (PARAM_UPDATE_METHOD_NAME.equals(methodName)) {
            pro.shushi.pamirs.framework.connectors.data.sql.AbstractWrapper<?, ?, ?> aw
                    = (pro.shushi.pamirs.framework.connectors.data.sql.AbstractWrapper<?, ?, ?>) map.getOrDefault(Constants.WRAPPER, null);
            if (aw == null) {
                pro.shushi.pamirs.framework.connectors.data.sql.update.UpdateWrapper<?> uw
                        = new pro.shushi.pamirs.framework.connectors.data.sql.update.UpdateWrapper<>();
                uw.eq(optimisticLockerColumn, originalVersionVal);
                map.put(Constants.WRAPPER, uw);
            } else {
                aw.apply(optimisticLockerColumn + " = {0}", originalVersionVal);
            }
        }
        map.put(Constants.MP_OPTLOCK_VERSION_COLUMN, optimisticLockerColumn);
        etm.put(optimisticLockerProperty, updatedVersionVal);
        etm.put(Constants.MP_OPTLOCK_VERSION_ORIGINAL, originalVersionVal);
    }

    private void lowCodeInvoke(Map map, List list) {
        String model = MapperContext.model(list);
        if (null == model) {
            return;
        }
        ModelFieldConfig optimisticLocker = Optional.ofNullable(PamirsSession.getContext()).map(v -> v.getModelConfig(model))
                .map(ModelConfig::getOptimisticLockerField)
                .flatMap(v -> Optional.ofNullable(PamirsSession.getContext()).map(f -> f.getModelField(model, v)))
                .orElse(null);
        if (null == optimisticLocker) {
            return;
        }
        String optimisticLockerProperty = optimisticLocker.getLname();
        String optimisticLockerColumn = optimisticLocker.getColumn();
        for (Object item : list) {
            // 旧的 version 值
            Object originalVersionVal = FieldUtils.getFieldValue(item, optimisticLockerProperty);
            if (null == originalVersionVal) {
                throw PamirsException.construct(BASE_NO_OPTIMISTIC_LOCKER_VALUE2_ERROR).errThrow();
            }
            // 新的 version 值
            Object updatedVersionVal = this.getUpdatedVersionVal(originalVersionVal.getClass(), originalVersionVal);
            FieldUtils.setFieldValue(item, optimisticLockerProperty, updatedVersionVal);
            FieldUtils.setFieldValue(item, Constants.MP_OPTLOCK_VERSION_ORIGINAL, originalVersionVal);
        }
        map.put(Constants.MP_OPTLOCK_VERSION_COLUMN, optimisticLockerColumn);
    }

    /**
     * This method provides the control for version value.<BR>
     * Returned value type must be the same as original one.
     *
     * @param originalVersionVal ignore
     * @return updated version val
     */
    protected Object getUpdatedVersionVal(Class<?> clazz, Object originalVersionVal) {
        if (long.class.equals(clazz) || Long.class.equals(clazz)) {
            return ((long) originalVersionVal) + 1;
        } else if (int.class.equals(clazz) || Integer.class.equals(clazz)) {
            return ((int) originalVersionVal) + 1;
        } else if (Date.class.equals(clazz)) {
            return new Date();
        } else if (Timestamp.class.equals(clazz)) {
            return new Timestamp(System.currentTimeMillis());
        } else if (LocalDateTime.class.equals(clazz)) {
            return LocalDateTime.now();
        }
        //not supported type, return original val.
        return originalVersionVal;
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof Executor) {
            return Plugin.wrap(target, this);
        }
        return target;
    }
}
