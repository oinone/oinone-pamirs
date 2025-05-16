package pro.shushi.pamirs.boot.standard.interceptor;

import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.springframework.beans.BeanUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import pro.shushi.pamirs.framework.connectors.data.util.DataConfigurationHelper;
import pro.shushi.pamirs.framework.connectors.data.dialect.Dialects;
import pro.shushi.pamirs.framework.connectors.data.dialect.api.SQLParamDialectService;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.base.D;
import pro.shushi.pamirs.meta.constant.FieldConstants;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

@Slf4j
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})
}
)
@Component
@Order(999)
public class UserDataInterceptor implements Interceptor {

    private static final String CREATE_UID = FieldConstants.CREATE_UID;

    private static final String WRITE_UID = FieldConstants.WRITE_UID;

    private static final String WRITE_DATE = FieldConstants.WRITE_DATE;

    private static final String DONT_RESET = "DONT_RESET";//


    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // 拦截 Executor 的 update 方法 生成sql前将 tenantId 设置到实体中
        if (invocation.getTarget() instanceof Executor) {
            return invokeUpdate(invocation);
        }
        return invocation.proceed();
    }

    private Object invokeUpdate(Invocation invocation) throws Exception {
        // 获取第一个参数
        MappedStatement ms = (MappedStatement) invocation.getArgs()[0];
        // 非 insert 语句 不处理
        if (ms.getSqlCommandType() != SqlCommandType.INSERT && ms.getSqlCommandType() != SqlCommandType.UPDATE) {
            return invocation.proceed();
        }
        // mybatis的参数对象
        Object paramObj = invocation.getArgs()[1];
        if (paramObj == null) {
            return invocation.proceed();
        }

        // 插入语句只传一个基本类型参数, 不做处理
        if (ClassUtils.isPrimitiveOrWrapper(paramObj.getClass())
                || String.class.isAssignableFrom(paramObj.getClass())
                || Number.class.isAssignableFrom(paramObj.getClass())) {
            return invocation.proceed();
        }

        Object originData = processParam(paramObj, ms.getSqlCommandType());
        Object result = invocation.proceed();
        resetParam(originData, paramObj);
        return result;
    }

    private void resetParam(Object originData, Object parameterObject) {
        if (DONT_RESET.equals(originData)) {
            return;
        }
        if (parameterObject instanceof Map) {
            Map<String, Object> parameterMap = (Map<String, Object>) parameterObject;
            if (parameterMap.containsKey(Constants.ENTITY)) {
                Object parameter = parameterMap.get(Constants.ENTITY);
                resetUserIdAndDate(parameter, originData);
            } else if (parameterMap.containsKey(Constants.COLLECTION)) {
                Object parameterList = parameterMap.get(Constants.COLLECTION);
                List<?> originDataList = (List<?>) originData;
                List<?> parameterObjectList = (List<?>) parameterList;
                for (int i = 0; i < originDataList.size(); i++) {
                    Object originDataMap = originDataList.get(i);
                    Object parameterObjectMap = parameterObjectList.get(i);
                    resetUserIdAndDate(parameterObjectMap, originDataMap);
                }
            }
        }
    }

    private void resetUserIdAndDate(Object data, Object originData) {
        if (DONT_RESET.equals(originData)) {
            return;
        }
        if (data instanceof Map) {
            if (originData != null) {
                ((Map<String, Object>) data).put(WRITE_DATE, originData);
            }
        } else if (data instanceof D) {
            Map<String, Object> d = ((D) data).get_d();
            if (originData != null) {
                d.put(WRITE_DATE, originData);
            }
        }
    }

    private Object processParam(Object parameterObject, SqlCommandType sqlCommandType) throws IllegalAccessException, InvocationTargetException {
        // 处理参数对象  如果是 map 且map的key 中没有 tenantId，添加到参数map中
        // 如果参数是bean，反射设置值
        if (parameterObject instanceof Map) {
            Serializable currentUserId = PamirsSession.getUserId();
            Map<String, Object> parameterMap = (Map<String, Object>) parameterObject;
            if (parameterMap.containsKey(Constants.ENTITY)) {
                return setUserIdAndDate(parameterMap.get(Constants.ENTITY), sqlCommandType, currentUserId);
            } else if (parameterMap.containsKey(Constants.COLLECTION)) {
                Object coll = parameterMap.get(Constants.COLLECTION);
                if (coll instanceof Collection) {
                    Collection<?> collection = (Collection<?>) coll;
                    List result = new ArrayList();
                    for (Object item : collection) {
                        result.add(setUserIdAndDate(item, sqlCommandType, currentUserId));
                    }
                    return result;
                }
            }
        } else {
            PropertyDescriptor ps = BeanUtils.getPropertyDescriptor(parameterObject.getClass(), WRITE_UID);
            if (ps != null && ps.getReadMethod() != null && ps.getWriteMethod() != null) {
                Object value = ps.getReadMethod().invoke(parameterObject);
                if (value == null) {
                    ps.getWriteMethod().invoke(parameterObject, 1L);
                }
            }
        }
        return DONT_RESET;
    }

    private Object setUserIdAndDate(Object data, SqlCommandType sqlCommandType, Serializable userId) {
        Map<String, Object> map = null;
        if (data == null) {
            return DONT_RESET;
        }
        if (data instanceof Map) {
            map = (Map<String, Object>) data;
        } else if (data instanceof D) {
            map = ((D) data).get_d();
        } else {
            log.warn("创建人和更新人设置失败，尚未支持的入参类型 class: {} sqlCommandType: {}", data.getClass(), sqlCommandType);
        }
        if (map == null) {
            return DONT_RESET;
        }

        String dsKey = DataConfigurationHelper.getDsKey();

        if (SqlCommandType.INSERT.equals(sqlCommandType)) {
            if (null != userId) {
                map.putIfAbsent(CREATE_UID, userId);
                map.putIfAbsent(WRITE_UID, userId); //insert时有值就不塞，无值就塞writeUid
            }
            //map.remove(WRITE_DATE, null);
            Dialects.component(SQLParamDialectService.class, dsKey).resolveIfWriteDate(map);
        } else if (SqlCommandType.UPDATE.equals(sqlCommandType)) {
            // map.remove(WRITE_DATE, null);
            Dialects.component(SQLParamDialectService.class, dsKey).resolveIfWriteDate(map);

            //update的时候没有key（_disableRefreshWriteUid）就不塞，有key就塞
            if (!map.containsKey(FieldConstants._disableRefreshWriteUid) && null != userId) {
                map.put(WRITE_UID, userId);
            }
            //update的时候map中无_disableRefreshWriteDate移除writeDate
            if (!map.containsKey(FieldConstants._disableRefreshWriteDate)) {
                Object originWriteDate = map.get(WRITE_DATE);
                // map.remove(WRITE_DATE);
                Dialects.component(SQLParamDialectService.class, dsKey).resolveWriteDate(map);
                if (null != originWriteDate) {
                    return originWriteDate;
                }
            }
        }
        return DONT_RESET;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }
}