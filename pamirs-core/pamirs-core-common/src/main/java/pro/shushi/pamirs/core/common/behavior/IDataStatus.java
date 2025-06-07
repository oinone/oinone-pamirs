package pro.shushi.pamirs.core.common.behavior;

import pro.shushi.pamirs.core.common.enmu.DataStatusEnum;

public interface IDataStatus {

    DataStatusEnum getDataStatus();

    <T> T setDataStatus(DataStatusEnum dataStatus);
}