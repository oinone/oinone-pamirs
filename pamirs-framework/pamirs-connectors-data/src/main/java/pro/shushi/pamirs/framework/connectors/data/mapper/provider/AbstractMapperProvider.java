package pro.shushi.pamirs.framework.connectors.data.mapper.provider;

import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.framework.connectors.data.constant.StatementConstants;
import pro.shushi.pamirs.framework.connectors.data.dialect.Dialects;
import pro.shushi.pamirs.framework.connectors.data.dialect.api.SQLMethodDialectService;
import pro.shushi.pamirs.framework.connectors.data.mapper.method.api.DeleteMethod;
import pro.shushi.pamirs.framework.connectors.data.mapper.method.api.InsertMethod;
import pro.shushi.pamirs.framework.connectors.data.mapper.method.api.SelectMethod;
import pro.shushi.pamirs.framework.connectors.data.mapper.method.api.UpdateMethod;
import pro.shushi.pamirs.framework.connectors.data.mapper.template.ScriptTemplate;
import pro.shushi.pamirs.framework.connectors.data.mapper.template.SqlTemplate;
import pro.shushi.pamirs.meta.api.cache.CacheProxy;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.prefix.DataPrefixManager;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * Mapper 提供器抽象基类
 * <p>
 * 2020/6/29 1:55 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class AbstractMapperProvider {

    private static final Cache<String, String> scriptCache = CacheProxy.getInstance(Caffeine.newBuilder()
            .maximumSize(100_000).expireAfterWrite(1, TimeUnit.MINUTES).build());

    private static String cacheKey(String module, String model, String method) {
        return DataPrefixManager.keyPrefix(module, model, model + CharacterConstants.SEPARATOR_UNDERLINE + method);
    }

    protected static String fetch(ModelConfig modelConfig, String method, Function<String, String> fetchFunction, String... appendix) {
        if (!ArrayUtils.isEmpty(appendix)) {
            method = method + CharacterConstants.SEPARATOR_COLON + StringUtils.join(appendix, CharacterConstants.SEPARATOR_COMMA);
        }
        return scriptCache.get(cacheKey(modelConfig.getModule(), modelConfig.getModel(), method), fetchFunction);
    }

    protected static String script(String sql) {
        return String.format(ScriptTemplate.SCRIPT, sql);
    }

    protected static String insert(final ModelConfig modelConfig, final boolean duplicateKeyUpdate) {
        InsertMethod insertMethod = getInsertMethod(modelConfig)
                .setDuplicateKeyUpdate(duplicateKeyUpdate).setValuePrefix(Constants.ENTITY_DOT);
        return insertMethod.insert();
    }

    protected static String insertBatch(final ModelConfig modelConfig, final boolean duplicateKeyUpdate) {
        InsertMethod insertMethod = getInsertMethod(modelConfig)
                .setDuplicateKeyUpdate(duplicateKeyUpdate).setBatch(true).setValuePrefix(StatementConstants.ITEM_DOT);
        return insertMethod.insertBatch();
    }

    protected static String update(final ModelConfig modelConfig,
                                   final boolean judgeEntityNull, final boolean wrapperSet,
                                   final boolean queryWrapper, final boolean withPk,
                                   final boolean useOptimisticLocker) {
        UpdateMethod updateMethod = getUpdateMethod(modelConfig)
                .setJudgeEntityNull(judgeEntityNull).setWrapperSet(wrapperSet)
                .setValuePrefix(Constants.ENTITY_DOT);
        updateMethod.setQueryWrapper(queryWrapper).setWithPk(withPk)
                .setUseOptimisticLocker(useOptimisticLocker).setOptimisticLockerPrefix(Constants.ENTITY_DOT);
        return updateMethod.update();
    }

    protected static String updateByUniqueKey(final ModelConfig modelConfig,
                                              final boolean judgeEntityNull, final boolean batch,
                                              final boolean useOptimisticLocker,
                                              final String[] nonEmptyUniqueKey) {
        UpdateMethod updateMethod = getUpdateMethod(modelConfig)
                .setJudgeEntityNull(judgeEntityNull).setWrapperSet(false)
                .setValuePrefix(Constants.ENTITY_DOT)
                .setBatch(batch);
        updateMethod.setUseOptimisticLocker(useOptimisticLocker).setOptimisticLockerPrefix(Constants.ENTITY_DOT)
                .setNonEmptyUniqueKey(nonEmptyUniqueKey);
        return updateMethod.updateByUniqueKey();
    }

    protected static String updateByPk(final ModelConfig modelConfig,
                                       final boolean judgeEntityNull, final boolean wrapperSet,
                                       final boolean useOptimisticLocker) {
        UpdateMethod updateMethod = getUpdateMethod(modelConfig)
                .setJudgeEntityNull(judgeEntityNull).setWrapperSet(wrapperSet)
                .setValuePrefix(Constants.ENTITY_DOT);
        updateMethod.setPkPrefix(Constants.ENTITY_DOT)
                .setUseOptimisticLocker(useOptimisticLocker).setOptimisticLockerPrefix(Constants.ENTITY_DOT);
        return updateMethod.updateByPk();
    }

    protected static String updateByPks(final ModelConfig modelConfig,
                                        final boolean judgeEntityNull, final boolean wrapperSet,
                                        final boolean withPk,
                                        final boolean useOptimisticLocker) {
        UpdateMethod updateMethod = getUpdateMethod(modelConfig)
                .setJudgeEntityNull(judgeEntityNull).setWrapperSet(wrapperSet).setValuePrefix(Constants.ENTITY_DOT);
        updateMethod.setQueryWrapper(false).setWithPk(withPk).setPkPrefix(StatementConstants.ITEM_DOT)
                .setUseOptimisticLocker(useOptimisticLocker).setOptimisticLockerPrefix(StatementConstants.ITEM_DOT);
        return updateMethod.updateByPks();
    }

    protected static String delete(final ModelConfig modelConfig, final boolean queryWrapper) {
        DeleteMethod deleteMethod = getDeleteMethod(modelConfig);
        deleteMethod.setQueryWrapper(queryWrapper).setWithPk(false);
        return script(String.format(deleteMethod.sqlDelete(), deleteMethod.table(), deleteMethod.sqlSegment()));
    }

    protected static String deleteByPk(final ModelConfig modelConfig) {
        DeleteMethod deleteMethod = getDeleteMethod(modelConfig);
        deleteMethod.setQueryWrapper(false).setWithPk(false).setPkPrefix(Constants.ENTITY_DOT);
        return script(String.format(deleteMethod.sqlDelete(), deleteMethod.table(), deleteMethod.sqlPk()));
    }

    protected static String deleteByPks(final ModelConfig modelConfig) {
        DeleteMethod deleteMethod = getDeleteMethod(modelConfig);
        deleteMethod.setQueryWrapper(false).setWithPk(false).setOptimisticLockerPrefix(Constants.ENTITY_DOT);
        return script(String.format(deleteMethod.sqlDelete(), deleteMethod.table(), deleteMethod.sqlPks()));
    }

    protected static String deleteByUniqueKey(final ModelConfig modelConfig, final String[] nonEmptyUniqueKey) {
        DeleteMethod deleteMethod = getDeleteMethod(modelConfig);
        deleteMethod.setQueryWrapper(false).setWithPk(false)
                .setOptimisticLockerPrefix(Constants.ENTITY_DOT).setNonEmptyUniqueKey(nonEmptyUniqueKey);
        return script(String.format(deleteMethod.sqlDelete(), deleteMethod.table(), deleteMethod.sqlUnique()));
    }

    protected static String deleteByUniqueKeys(final ModelConfig modelConfig, final String[] nonEmptyUniqueKey) {
        DeleteMethod deleteMethod = getDeleteMethod(modelConfig);
        deleteMethod.setQueryWrapper(false).setWithPk(false)
                .setOptimisticLockerPrefix(Constants.ENTITY_DOT).setNonEmptyUniqueKey(nonEmptyUniqueKey);
        return script(String.format(deleteMethod.sqlDelete(), deleteMethod.table(), deleteMethod.sqlUniques()));
    }

    protected static String select(final ModelConfig modelConfig, final boolean onlyColumn,
                                   final boolean queryWrapper, final boolean withPk, final boolean keyConflict) {
        SelectMethod selectMethod = getSelectMethod(modelConfig)
                .setOnlyColumn(onlyColumn).setQueryWrapper(queryWrapper).setWithPk(withPk).setKeyConflict(keyConflict);
        return script(String.format(SqlTemplate.SELECT, selectMethod.sqlSelect(), selectMethod.table(), selectMethod.sqlSegment()));
    }

    @SuppressWarnings("SameParameterValue")
    protected static String selectByUnique(final ModelConfig modelConfig, final boolean onlyColumn,
                                           final boolean queryWrapper, final boolean withPk, final String[] nonEmptyUniqueKey) {
        SelectMethod selectMethod = getSelectMethod(modelConfig)
                .setOnlyColumn(onlyColumn).setQueryWrapper(queryWrapper).setWithPk(withPk).setOptimisticLockerPrefix(Constants.ENTITY_DOT)
                .setNonEmptyUniqueKey(nonEmptyUniqueKey);
        return script(String.format(SqlTemplate.SELECT, selectMethod.sqlSelect(), selectMethod.table(), selectMethod.sqlUnique()));
    }

    @SuppressWarnings("SameParameterValue")
    protected static String selectByPk(final ModelConfig modelConfig, final boolean onlyColumn,
                                       final boolean queryWrapper, final boolean withPk) {
        SelectMethod selectMethod = getSelectMethod(modelConfig)
                .setOnlyColumn(onlyColumn).setQueryWrapper(queryWrapper).setWithPk(withPk).setPkPrefix(Constants.ENTITY_DOT)
                .setOptimisticLockerPrefix(Constants.ENTITY);
        return script(String.format(SqlTemplate.SELECT, selectMethod.sqlSelect(), selectMethod.table(), selectMethod.sqlPk()));
    }

    @SuppressWarnings("SameParameterValue")
    protected static String selectByPks(final ModelConfig modelConfig, final boolean onlyColumn,
                                        final boolean queryWrapper, final boolean withPk) {
        SelectMethod selectMethod = getSelectMethod(modelConfig)
                .setOnlyColumn(onlyColumn).setQueryWrapper(queryWrapper).setWithPk(withPk).setOptimisticLockerPrefix(Constants.ENTITY_DOT);
        return script(String.format(SqlTemplate.SELECT, selectMethod.sqlSelect(), selectMethod.table(), selectMethod.sqlPks()));
    }

    @SuppressWarnings("SameParameterValue")
    protected static String selectByUniques(final ModelConfig modelConfig, final boolean onlyColumn,
                                            final boolean queryWrapper, final boolean withPk, final String[] nonEmptyUniqueKey) {
        SelectMethod selectMethod = getSelectMethod(modelConfig)
                .setOnlyColumn(onlyColumn).setQueryWrapper(queryWrapper).setWithPk(withPk).setOptimisticLockerPrefix(Constants.ENTITY_DOT)
                .setNonEmptyUniqueKey(nonEmptyUniqueKey);
        return script(String.format(SqlTemplate.SELECT, selectMethod.sqlSelect(), selectMethod.table(), selectMethod.sqlUniques()));
    }

    protected static String selectCount(final ModelConfig modelConfig,
                                        final boolean queryWrapper, final boolean withPk, final boolean keyConflict) {
        SelectMethod selectMethod = getSelectMethod(modelConfig).setQueryWrapper(queryWrapper).setWithPk(withPk).setKeyConflict(keyConflict);
        return script(String.format(SqlTemplate.SELECT_COUNT, selectMethod.sqlCount(), selectMethod.table(), selectMethod.sqlSegment()));
    }

    protected static SQLMethodDialectService getSqlScriptTemplateDialectService(ModelConfig modelConfig) {
        return Dialects.component(SQLMethodDialectService.class, modelConfig.getDsKey());
    }

    protected static SelectMethod getSelectMethod(ModelConfig modelConfig) {
        return getSqlScriptTemplateDialectService(modelConfig).getSelectMethod(modelConfig);
    }

    protected static InsertMethod getInsertMethod(ModelConfig modelConfig) {
        return getSqlScriptTemplateDialectService(modelConfig).getInsertMethod(modelConfig);
    }

    protected static UpdateMethod getUpdateMethod(ModelConfig modelConfig) {
        return getSqlScriptTemplateDialectService(modelConfig).getUpdateMethod(modelConfig);
    }

    protected static DeleteMethod getDeleteMethod(ModelConfig modelConfig) {
        return getSqlScriptTemplateDialectService(modelConfig).getDeleteMethod(modelConfig);
    }
}
