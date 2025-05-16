package pro.shushi.pamirs.meta.api.core.orm.systems.directive;

import pro.shushi.pamirs.meta.api.core.orm.systems.ModelDirectiveBatchApi;
import pro.shushi.pamirs.meta.common.spi.SPI;

import static pro.shushi.pamirs.meta.api.core.orm.systems.directive.SystemDirectiveEnum.*;

/**
 * 批量指令
 * <p>
 * 全部接口化是因为指令系统是最底层逻辑，与枚举解耦
 * <p>
 * 2020/7/13 3:17 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@SPI.Service
public class DefaultModelDirectiveBatchApi extends AbstractModelDirectiveApi implements ModelDirectiveBatchApi {

    @Override
    public <T> T clear(T listOrObject) {
        return setMetaBit(listOrObject, 0L);
    }

    @Override
    public <T> T init(T listOrObject, SystemDirectiveEnum... directiveEnums) {
        clear(listOrObject);
        if (null == directiveEnums || 0 == directiveEnums.length) {
            return listOrObject;
        }
        Long initValue = 0L;
        for (SystemDirectiveEnum v : directiveEnums) {
            initValue += v.getValue();
        }
        setMetaBit(listOrObject, initValue);
        return listOrObject;
    }

    @Override
    public <T> T init(T listOrObject, Long directive) {
        clear(listOrObject);
        return setMetaBit(listOrObject, directive);
    }

    @Override
    public <T> Long value(T obj) {
        return getMetaBit(obj);
    }

    @Override
    public <T> T sudo(T listOrObject) {
        return disable(listOrObject, AUTHENTICATE, this::sudo);
    }

    @Override
    public <T> T disableSudo(T listOrObject) {
        return enable(listOrObject, AUTHENTICATE, this::disableSudo);
    }

    @Override
    public <T> boolean isSudo(T listOrObject) {
        return !hasBit(listOrObject, AUTHENTICATE);
    }

    @Override
    public <T> T enableOptimisticLocker(T listOrObject) {
        return disable(listOrObject, UNLOCK, this::enableOptimisticLocker);
    }

    @Override
    public <T> T disableOptimisticLocker(T listOrObject) {
        return enable(listOrObject, UNLOCK, this::disableOptimisticLocker);
    }

    @Override
    public <T> boolean isOptimisticLocker(T listOrObject) {
        return !hasBit(listOrObject, UNLOCK);
    }

    @Override
    public <T> T enableCheck(T listOrObject) {
        return enable(listOrObject, CHECK, this::enableCheck);
    }

    @Override
    public <T> T disableCheck(T listOrObject) {
        return disable(listOrObject, CHECK, this::disableCheck);
    }

    @Override
    public <T> boolean isDoCheck(T listOrObject) {
        return hasBit(listOrObject, CHECK);
    }

    @Override
    public <T> T enableDefaultValue(T listOrObject) {
        return enable(listOrObject, DEFAULT_VALUE, this::enableDefaultValue);
    }

    @Override
    public <T> T disableDefaultValue(T listOrObject) {
        return disable(listOrObject, DEFAULT_VALUE, this::disableDefaultValue);
    }

    @Override
    public <T> boolean isDoDefaultValue(T listOrObject) {
        return hasBit(listOrObject, DEFAULT_VALUE);
    }

    @Override
    public <T> T enableHook(T listOrObject) {
        return enable(listOrObject, HOOK, this::enableHook);
    }

    @Override
    public <T> T disableHook(T listOrObject) {
        return disable(listOrObject, HOOK, this::disableHook);
    }

    @Override
    public <T> boolean isHook(T listOrObject) {
        return hasBit(listOrObject, HOOK);
    }

    @Override
    public <T> T enableExtPoint(T listOrObject) {
        return enable(listOrObject, EXT_POINT, this::enableExtPoint);
    }

    @Override
    public <T> T disableExtPoint(T listOrObject) {
        return disable(listOrObject, EXT_POINT, this::disableExtPoint);
    }

    @Override
    public <T> boolean isDoExtPoint(T listOrObject) {
        return hasBit(listOrObject, EXT_POINT);
    }

    @Override
    public <T> T enableColumn(T listOrObject) {
        return enable(listOrObject, ORM_COLUMN, this::enableColumn);
    }

    @Override
    public <T> T disableColumn(T listOrObject) {
        return disable(listOrObject, ORM_COLUMN, this::disableColumn);
    }

    @Override
    public <T> boolean isDoColumn(T listOrObject) {
        return hasBit(listOrObject, ORM_COLUMN);
    }

    @Override
    public <T> T enableReentry(T listOrObject) {
        return enable(listOrObject, REENTRY, this::enableReentry);
    }

    @Override
    public <T> T disableReentry(T listOrObject) {
        return disable(listOrObject, REENTRY, this::disableReentry);
    }

    @Override
    public <T> boolean isReentry(T listOrObject) {
        return hasBit(listOrObject, REENTRY);
    }

    @Override
    public <T> T enableOrmReentry(T listOrObject) {
        return enable(listOrObject, ORM_REENTRY, this::enableOrmReentry);
    }

    @Override
    public <T> T disableOrmReentry(T listOrObject) {
        return disable(listOrObject, ORM_REENTRY, this::disableOrmReentry);
    }

    @Override
    public <T> boolean isOrmReentry(T listOrObject) {
        return hasBit(listOrObject, ORM_REENTRY);
    }

    @Override
    public <T> T enableUsePkStrategy(T listOrObject) {
        return enable(listOrObject, USE_PK_STRATEGY, this::enableUsePkStrategy);
    }

    @Override
    public <T> T disableUsePkStrategy(T listOrObject) {
        return disable(listOrObject, USE_PK_STRATEGY, this::disableUsePkStrategy);
    }

    @Override
    public <T> boolean isUsePkStrategy(T listOrObject) {
        return hasBit(listOrObject, USE_PK_STRATEGY);
    }

    @Override
    public <T> T enableBuiltAction(T listOrObject) {
        return enable(listOrObject, BUILT_ACTION, this::enableBuiltAction);
    }

    @Override
    public <T> T disableBuiltAction(T listOrObject) {
        return disable(listOrObject, BUILT_ACTION, this::disableBuiltAction);
    }

    @Override
    public <T> boolean isBuiltAction(T listOrObject) {
        return hasBit(listOrObject, BUILT_ACTION);
    }

    @Override
    public <T> T enableFromClient(T listOrObject) {
        return enable(listOrObject, FROM_CLIENT, this::enableFromClient);
    }

    @Override
    public <T> T disableFromClient(T listOrObject) {
        return disable(listOrObject, FROM_CLIENT, this::disableFromClient);
    }

    @Override
    public <T> boolean isFromClient(T listOrObject) {
        return hasBit(listOrObject, FROM_CLIENT);
    }

    @Override
    public <T> T enableIgnoreFunManagement(T listOrObject) {
        return enable(listOrObject, IGNORE_FUN_MANAGEMENT, this::enableIgnoreFunManagement);
    }

    @Override
    public <T> T disableIgnoreFunManagement(T listOrObject) {
        return disable(listOrObject, IGNORE_FUN_MANAGEMENT, this::disableIgnoreFunManagement);
    }

    @Override
    public <T> boolean isIgnoreFunManagement(T listOrObject) {
        return hasBit(listOrObject, IGNORE_FUN_MANAGEMENT);
    }

    @Override
    public <T> T enableDirty(T listOrObject) {
        return disable(listOrObject, CLEAN, this::disableDirty);
    }

    @Override
    public <T> T disableDirty(T listOrObject) {
        return enable(listOrObject, CLEAN, this::enableDirty);
    }

    @Override
    public <T> boolean isDirty(T listOrObject) {
        return !hasBit(listOrObject, CLEAN);
    }

    @Override
    public <T> T enableMetaCompleted(T listOrObject) {
        return enable(listOrObject, META_COMPLETED, this::enableMetaCompleted);
    }

    @Override
    public <T> T disableMetaCompleted(T listOrObject) {
        return disable(listOrObject, META_COMPLETED, this::disableMetaCompleted);
    }

    @Override
    public <T> boolean isMetaCompleted(T listOrObject) {
        return hasBit(listOrObject, META_COMPLETED);
    }

    @Override
    public <T> T enableMetaInherited(T listOrObject) {
        return enable(listOrObject, META_REFRESH, this::enableMetaInherited);
    }

    @Override
    public <T> T disableMetaInherited(T listOrObject) {
        return disable(listOrObject, META_REFRESH, this::disableMetaInherited);
    }

    @Override
    public <T> boolean isMetaInherited(T listOrObject) {
        return hasBit(listOrObject, META_REFRESH);
    }

    @Override
    public <T> T enableMetaDiffing(T listOrObject) {
        return enable(listOrObject, META_DIFFING, this::enableMetaDiffing);
    }

    @Override
    public <T> T disableMetaDiffing(T listOrObject) {
        return disable(listOrObject, META_DIFFING, this::disableMetaDiffing);
    }

    @Override
    public <T> boolean isMetaDiffing(T listOrObject) {
        return hasBit(listOrObject, META_DIFFING);
    }

    @Override
    public <T> T enableMetaCrossing(T listOrObject) {
        return enable(listOrObject, META_CROSSING, this::enableMetaCrossing);
    }

    @Override
    public <T> T disableMetaCrossing(T listOrObject) {
        return disable(listOrObject, META_CROSSING, this::disableMetaCrossing);
    }

    @Override
    public <T> boolean isMetaCrossing(T listOrObject) {
        return hasBit(listOrObject, META_CROSSING);
    }

    @Override
    public <T> T enableRemoteMeta(T listOrObject) {
        return enable(listOrObject, REMOTE_META, this::enableRemoteMeta);
    }

    @Override
    public <T> T disableRemoteMeta(T listOrObject) {
        return disable(listOrObject, REMOTE_META, this::enableRemoteMeta);
    }

    @Override
    public <T> boolean isRemoteMeta(T listOrObject) {
        return hasBit(listOrObject, REMOTE_META);
    }
}
