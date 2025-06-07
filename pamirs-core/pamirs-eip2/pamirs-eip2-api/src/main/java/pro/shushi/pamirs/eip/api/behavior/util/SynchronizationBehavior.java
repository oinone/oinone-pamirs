package pro.shushi.pamirs.eip.api.behavior.util;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import pro.shushi.pamirs.eip.api.behavior.ISynchronizationModel;
import pro.shushi.pamirs.eip.api.behavior.ISynchronizationService;
import pro.shushi.pamirs.eip.api.behavior.ISynchronizationServiceFactory;
import pro.shushi.pamirs.eip.api.behavior.enmu.SynchronizationStatusEnum;
import pro.shushi.pamirs.eip.api.behavior.model.SynchronizationInfo;
import pro.shushi.pamirs.meta.api.Models;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class SynchronizationBehavior {

    private static final String EMPTY = "";
    private static final String MIDDLE_CHARACTER = ": ";
    private static final String SPLIT_CHARACTER = ", ";

    public static final Function<SynchronizationInfo, String> COMPUTE_BY_STATUS = v -> v.getDisplayName() + MIDDLE_CHARACTER + v.getSynchronizationStatus().getDisplayName();

    public static final Function<SynchronizationInfo, String> COMPUTE_BY_MESSAGE = v -> v.getDisplayName() + MIDDLE_CHARACTER + v.getMessage();

    public static final Function<SynchronizationInfo, String> COMPUTE_SWITCH_STATUS = v -> {
        SynchronizationStatusEnum synchronizationStatus = v.getSynchronizationStatus();
        switch (synchronizationStatus) {
            case EXCEPTION:
                return COMPUTE_BY_MESSAGE.apply(v);
            case PENDING:
            case SUCCESS:
            case NONE:
            default:
                return COMPUTE_BY_STATUS.apply(v);
        }
    };

    public static String computeSynchronizationInfo(List<SynchronizationInfo> synchronizationInfoList, Function<SynchronizationInfo, String> process) {
        if (CollectionUtils.isEmpty(synchronizationInfoList)) {
            return EMPTY;
        }
        List<String> synchronizationStatusInfoList = new ArrayList<>();
        for (SynchronizationInfo item : synchronizationInfoList) {
            synchronizationStatusInfoList.add(process.apply(item));
        }
        return String.join(SPLIT_CHARACTER, synchronizationStatusInfoList);
    }

    public static <T extends ISynchronizationModel> T execute(T data) {
        String model = Models.api().getModel(data);
        List<ISynchronizationService> services = ISynchronizationServiceFactory.getSyncService(model);
        if (CollectionUtils.isNotEmpty(services)) {
            for (ISynchronizationService info : services) {
                data = execute(data, info);
            }
        }
        return data;
    }

    public static <T extends ISynchronizationModel> T execute(T data, String interfaceName) {
        String model = Models.api().getModel(data);
        ISynchronizationService service = ISynchronizationServiceFactory.getSyncService(model, interfaceName);
        return execute(data, service);
    }

    private static <T extends ISynchronizationModel> T execute(T data, ISynchronizationService service) {
        if (service == null) {
            return data;
        }
        SynchronizationInfo info = service.synchronizationInfo();
        try {
            info = service.push(data);
        } catch (Exception e) {
            info.setSynchronizationStatus(SynchronizationStatusEnum.EXCEPTION);
            info.setMessage(ExceptionUtils.getStackTrace(e));
        }
        return setSyncInfo(data, info);
    }

    public static <T extends ISynchronizationModel> T setSyncInfo(T data, SynchronizationInfo info) {
        List<SynchronizationInfo> infos = data.getSynchronizationInfoList();
        if (CollectionUtils.isNotEmpty(infos)) {
            Boolean contains = false;
            for (SynchronizationInfo synchronizationInfo : infos) {
                if (info.getInterfaceName().equals(synchronizationInfo.getInterfaceName())) {
                    contains = true;
                    synchronizationInfo.setDisplayName(info.getDisplayName());
                    synchronizationInfo.setMessage(info.getMessage());
                    synchronizationInfo.setSynchronizationStatus(info.getSynchronizationStatus());
                }
            }
            if (!contains) {
                infos.add(info);
            }
        } else {
            infos = new ArrayList<SynchronizationInfo>() {{
                this.add(info);
            }};
        }
        data.setSynchronizationInfoList(infos);
        return data;
    }

}
