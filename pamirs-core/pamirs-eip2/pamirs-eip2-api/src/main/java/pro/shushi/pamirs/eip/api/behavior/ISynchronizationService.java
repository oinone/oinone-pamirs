package pro.shushi.pamirs.eip.api.behavior;

import pro.shushi.pamirs.eip.api.behavior.model.SynchronizationInfo;
import pro.shushi.pamirs.eip.api.behavior.model.SynchronizationInfoDefinition;

/**
 * 同步状态
 */
public interface ISynchronizationService {

    SynchronizationInfoDefinition initSync();

    SynchronizationInfo synchronizationInfo();

    SynchronizationInfo push(Object data);

}