package pro.shushi.pamirs.core.common.behavior.impl;

import org.apache.commons.collections4.CollectionUtils;
import pro.shushi.pamirs.core.common.behavior.IDataStatus;
import pro.shushi.pamirs.core.common.enmu.CommonExpEnumerate;
import pro.shushi.pamirs.core.common.enmu.DataStatusEnum;
import pro.shushi.pamirs.meta.common.exception.PamirsException;

import java.util.Collections;
import java.util.List;

public abstract class DataStatusBehavior<T extends IDataStatus> {

    protected abstract T fetchData(T data);

    public T dataStatusEnable(T data) {
        data = fetchData(data);
        if (DataStatusEnum.ENABLED.equals(data.getDataStatus())) {
            throw PamirsException.construct(CommonExpEnumerate.DATA_STATUS_ENABLED_TO_ENABLED_ERROR).errThrow();
        }
        if (DataStatusEnum.DRAFT.equals(data.getDataStatus())) {
            throw PamirsException.construct(CommonExpEnumerate.DATA_STATUS_DRAFT_TO_ENABLED_ERROR).errThrow();
        }
        data.setDataStatus(DataStatusEnum.ENABLED);
        return data;
    }

    public T dataStatusDisable(T data) {
        data = fetchData(data);
        if (DataStatusEnum.DISABLED.equals(data.getDataStatus())) {
            throw PamirsException.construct(CommonExpEnumerate.DATA_STATUS_DISABLED_TO_DISABLED_ERROR).errThrow();
        }
        if (DataStatusEnum.DRAFT.equals(data.getDataStatus())) {
            throw PamirsException.construct(CommonExpEnumerate.DATA_STATUS_DRAFT_TO_DISABLED_ERROR).errThrow();
        }
        data.setDataStatus(DataStatusEnum.DISABLED);
        return data;
    }

    public List<T> dataStatusBatchEnable(List<T> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            return Collections.emptyList();
        }
        for (T item : dataList) {
            item.setDataStatus(DataStatusEnum.ENABLED);
        }
        return dataList;
    }

    public List<T> dataStatusBatchDisable(List<T> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            return Collections.emptyList();
        }
        for (T item : dataList) {
            item.setDataStatus(DataStatusEnum.DISABLED);
        }
        return dataList;
    }
}