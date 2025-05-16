package pro.shushi.pamirs.boot.standard.checker.helper;

import com.alibaba.fastjson.JSON;
import org.apache.commons.collections4.CollectionUtils;
import pro.shushi.pamirs.boot.standard.checker.constants.EnvironmentCheckConstants;
import pro.shushi.pamirs.boot.standard.checker.environment.DataSourceEnvironmentChecker;
import pro.shushi.pamirs.boot.standard.config.EnvironmentProtectedConfig;
import pro.shushi.pamirs.boot.standard.entity.EnvironmentCheckContext;
import pro.shushi.pamirs.boot.standard.entity.EnvironmentKey;
import pro.shushi.pamirs.boot.standard.utils.PlatformEnvironmentGenerator;
import pro.shushi.pamirs.framework.connectors.data.dialect.Dialects;
import pro.shushi.pamirs.framework.connectors.data.dialect.api.DsDialectComponent;
import pro.shushi.pamirs.framework.connectors.data.entity.DataSourceInfo;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.lambda.Getter;
import pro.shushi.pamirs.meta.common.lambda.LambdaUtil;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;
import pro.shushi.pamirs.meta.domain.PlatformEnvironment;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 数据源URL检查
 *
 * @author Adamancy Zhang at 09:49 on 2024-10-16
 */
@Slf4j
public class DataSourceUrlChecker implements EnvironmentKey.Checker {

    /**
     * 数据源环境检查
     */
    private final DataSourceEnvironmentChecker dataSourceEnvironmentChecker;

    /**
     * 不可变
     */
    private final boolean immutable;

    public DataSourceUrlChecker(boolean immutable) {
        this.dataSourceEnvironmentChecker = BeanDefinitionUtils.getBean(DataSourceEnvironmentChecker.class);
        this.immutable = immutable;
    }

    @Override
    public PlatformEnvironment check(EnvironmentCheckContext context, PlatformEnvironment currentEnvironment, PlatformEnvironment historyEnvironment) {
        DataSourceInfo oldInfo = JSON.parseObject(historyEnvironment.getValue(), DataSourceInfo.class);
        DataSourceInfo newInfo = JSON.parseObject(currentEnvironment.getValue(), DataSourceInfo.class);

        PredictResult result = isEqual(oldInfo, newInfo);

        if (immutable && !result.getIsConnectionEqual()) {
            if (EnvironmentProtectedConfig.isStrict()) {
                context.addError(currentEnvironment, EnvironmentCheckConstants.IMMUTABLE_TIP + oldInfo.getUrl());
            } else {
                context.addWarning(currentEnvironment, EnvironmentCheckConstants.IMMUTABLE_TIP + oldInfo.getUrl());
            }
            if (context.isSaveEnvironments()) {
                return currentEnvironment;
            }
            return null;
        }

        if (!immutable && context.isCollaborativeDevelopmentEnvironment()) {
            if (result.getIsConnectionEqual()) {
                context.addWarning(currentEnvironment, "协同开发模式下，项目业务库应使用本地数据库进行开发测试，否则有可能造成由于DDL修改冲突导致的无法预知的问题");
            }
        }

        for (Map.Entry<String, String> entry : result.parameterMessages.entrySet()) {
            String key = currentEnvironment.getKey() + CharacterConstants.SEPARATOR_DOT + entry.getKey();
            context.addWarning(PlatformEnvironmentGenerator.newInstance(dataSourceEnvironmentChecker.type(), key, null), entry.getValue());
        }

        return currentEnvironment;
    }

    @Override
    public PlatformEnvironment convert(PlatformEnvironment currentEnvironment) {
        String value = currentEnvironment.getValue();
        if (JSON.isValidObject(value)) {
            return currentEnvironment;
        }
        String dsKey = DataSourceEnvironmentHelper.getEnvironmentDsKeyNotNull(currentEnvironment.getKey());
        DataSourceInfo newInfo = Dialects.component(DsDialectComponent.class, dsKey).getDataSourceInfo(dsKey);
        currentEnvironment.setValue(JSON.toJSONString(newInfo));
        return currentEnvironment;
    }

    @Override
    public PlatformEnvironment checkDeleteEnvironment(EnvironmentCheckContext context, PlatformEnvironment historyEnvironment) {
        return null;
    }

    private PredictResult isEqual(DataSourceInfo oldInfo, DataSourceInfo newInfo) {
        boolean isEqual = true;
        if (!isEqualByGetters(oldInfo, newInfo,
                DataSourceInfo::getSchema,
                DataSourceInfo::getDatabase,
                DataSourceInfo::getProtocol,
                DataSourceInfo::getHost,
                DataSourceInfo::getPort)) {
            isEqual = false;
        }
        Map<String, List<String>> oldParameters = oldInfo.getParameters();
        Map<String, List<String>> newParameters = newInfo.getParameters();
        Map<String, String> parameterMessages = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : oldParameters.entrySet()) {
            String key = entry.getKey();
            List<String> oldValues = entry.getValue();
            List<String> newValues = newParameters.remove(key);
            if (!isListEquals(oldValues, newValues)) {
                parameterMessages.put(key, String.format("连接参数不一致，可能造成无法预知的问题; oldValue: %s, newValue: %s", oldValues, newValues));
            }
        }
        return new PredictResult(isEqual, parameterMessages);
    }

    @SafeVarargs
    private final <T> boolean isEqualByGetters(T a, T b, Getter<T, ?>... getters) {
        boolean isEqual = true;
        for (Getter<T, ?> getter : getters) {
            Object av = getter.apply(a);
            Object bv = getter.apply(b);
            if (!isObjectEquals(av, bv)) {
                isEqual = false;
                log.error("datasource url checker not equals. field: {}, oldValue: {}, newValue: {}", LambdaUtil.fetchFieldName(getter), av, bv);
            }
        }
        return isEqual;
    }

    private boolean isObjectEquals(Object a, Object b) {
        if (a == null) {
            return b == null;
        }
        return a.equals(b);
    }

    private boolean isListEquals(List<?> list1, List<?> list2) {
        if (CollectionUtils.isEmpty(list1)) {
            return CollectionUtils.isEmpty(list2);
        }
        if (CollectionUtils.isEmpty(list2)) {
            return false;
        }
        for (Object item1 : list1) {
            Object item2 = remove(list2, item1);
            if (item2 == null) {
                return false;
            }
        }
        return list2.isEmpty();
    }

    private Object remove(List<?> array, Object target) {
        Iterator<?> iterator = array.iterator();
        while (iterator.hasNext()) {
            String value = String.valueOf(iterator.next());
            if (target.equals(value)) {
                iterator.remove();
                return value;
            }
        }
        return null;
    }

    private static class PredictResult {

        private final boolean isConnectionEqual;

        private final Map<String, String> parameterMessages;

        public PredictResult(boolean isConnectionEqual, Map<String, String> parameterMessages) {
            this.isConnectionEqual = isConnectionEqual;
            this.parameterMessages = parameterMessages;
        }

        public boolean getIsConnectionEqual() {
            return isConnectionEqual;
        }

        public Map<String, String> getParameterMessages() {
            return parameterMessages;
        }
    }
}