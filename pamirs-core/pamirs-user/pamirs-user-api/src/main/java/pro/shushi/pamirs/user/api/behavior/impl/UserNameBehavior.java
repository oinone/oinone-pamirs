package pro.shushi.pamirs.user.api.behavior.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.core.common.DataShardingHelper;
import pro.shushi.pamirs.core.common.behavior.IUserNameModel;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.api.CommonApiFactory;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.entity.DataMap;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.constant.FieldConstants;
import pro.shushi.pamirs.meta.util.FieldUtils;
import pro.shushi.pamirs.user.api.model.PamirsUser;
import pro.shushi.pamirs.user.api.service.UserSimpleService;

import java.util.*;

/**
 * @author Adamancy Zhang
 * @date 2021-01-15 12:56
 */
public class UserNameBehavior {

    private static final String CREATE_UID_FIELD = FieldConstants.CREATE_UID;

    private static final String WRITE_UID_FIELD = FieldConstants.WRITE_UID;

    private static final String CREATE_USER_NAME = FieldConstants.CREATE_USER_NAME;

    private static final String WRITE_USER_NAME = FieldConstants.WRITE_USER_NAME;

    private UserNameBehavior() {
        //reject create object
    }

    public static boolean isNeedSet(Object data, String model) {
        if (data instanceof IUserNameModel) {
            return true;
        }
        return data instanceof DataMap && UserNameBehavior.isNeedSet(model);
    }

    public static boolean isNeedSet(String model) {
        ModelConfig modelConfig = PamirsSession.getContext().getModelConfig(model);
        if (modelConfig == null) {
            return false;
        }
        return PamirsSession.getContext().getModelField(model, CREATE_USER_NAME) != null || PamirsSession.getContext().getModelField(model, WRITE_USER_NAME) != null;
    }

    public static void set(Collection<?> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        final int initialCapacity = computeInitialCapacity(list.size());
        Map<Long, List<Object>> createUserNameMap = new HashMap<>(initialCapacity);
        Map<Long, List<Object>> writeUserNameMap = new HashMap<>(initialCapacity);
        Set<Long> uidSet = new HashSet<>();
        for (Object item : list) {
            Object createUidValue = FieldUtils.getFieldValue(item, CREATE_UID_FIELD);
            Object writeUidValue = FieldUtils.getFieldValue(item, WRITE_UID_FIELD);

            Object createUserName = FieldUtils.getFieldValue(item, CREATE_USER_NAME);
            Object writeUserName = FieldUtils.getFieldValue(item, WRITE_USER_NAME);
            if (createUidValue instanceof Long && createUserName == null) {
                Long createUid = (Long) createUidValue;
                uidSet.add(createUid);
                createUserNameMap.computeIfAbsent(createUid, (k) -> new ArrayList<>(initialCapacity)).add(item);
            }
            if (writeUidValue instanceof Long && writeUserName == null) {
                Long writeUid = (Long) writeUidValue;
                uidSet.add(writeUid);
                writeUserNameMap.computeIfAbsent(writeUid, (k) -> new ArrayList<>(initialCapacity)).add(item);
            }
        }
        if (uidSet.isEmpty()) {
            return;
        }
        UserSimpleService userService = CommonApiFactory.getApi(UserSimpleService.class);
        List<PamirsUser> userList = DataShardingHelper.build().collectionSharding(uidSet, (sublist) -> userService.queryListByWrapper(Pops.<PamirsUser>lambdaQuery()
                .from(PamirsUser.MODEL_MODEL)
                .select(PamirsUser::getId, PamirsUser::getName)
                .setBatchSize(-1)
                .in(PamirsUser::getId, sublist)));
        if (userList.isEmpty()) {
            return;
        }
        for (PamirsUser user : userList) {
            String name = user.getName();
            if (StringUtils.isBlank(name)) {
                continue;
            }
            Long id = user.getId();
            List<Object> createUserNameList = createUserNameMap.get(id);
            if (CollectionUtils.isNotEmpty(createUserNameList)) {
                for (Object item : createUserNameList) {
                    FieldUtils.setFieldValue(item, CREATE_USER_NAME, name);
                }
            }
            List<Object> writeUserNameList = writeUserNameMap.get(id);
            if (CollectionUtils.isNotEmpty(writeUserNameList)) {
                for (Object item : writeUserNameList) {
                    FieldUtils.setFieldValue(item, WRITE_USER_NAME, name);
                }
            }
        }
    }

    private static int computeInitialCapacity(int size) {
        int initialCapacity;
        if (size <= 20) {
            initialCapacity = size;
        } else if (size <= 50) {
            initialCapacity = (int) (size * 0.8);
        } else {
            initialCapacity = 16;
        }
        return initialCapacity;
    }
}