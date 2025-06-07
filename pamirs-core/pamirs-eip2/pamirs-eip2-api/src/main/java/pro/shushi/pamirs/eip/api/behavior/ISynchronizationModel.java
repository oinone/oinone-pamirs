package pro.shushi.pamirs.eip.api.behavior;

import pro.shushi.pamirs.eip.api.behavior.model.SynchronizationInfo;

import java.util.List;

/**
 * 同步模型
 */
public interface ISynchronizationModel {

    List<SynchronizationInfo> getSynchronizationInfoList();

    void setSynchronizationInfoList(List<SynchronizationInfo> synchronizationInfos);

}
