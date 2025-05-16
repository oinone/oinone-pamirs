package pro.shushi.pamirs.boot.standard.checker;

import pro.shushi.pamirs.boot.standard.entity.EnvironmentCheckContext;
import pro.shushi.pamirs.meta.domain.PlatformEnvironment;
import pro.shushi.pamirs.meta.domain.PlatformEnvironmentHistoryRecord;

import java.util.List;
import java.util.Map;

/**
 * @author Gesi at 11:14 on 2024/11/29
 */
public interface EnvironmentRecordFinder {

    /**
     * 收集修改环境记录
     *
     * @return 当前要保存的修改环境记录信息
     */
    List<PlatformEnvironmentHistoryRecord> collectionUpdate(EnvironmentCheckContext context, List<PlatformEnvironment> environments, Map<String, List<PlatformEnvironment>> recordHistoryEnvironmentMap);

}
