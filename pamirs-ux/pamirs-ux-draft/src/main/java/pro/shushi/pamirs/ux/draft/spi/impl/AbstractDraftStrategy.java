package pro.shushi.pamirs.ux.draft.spi.impl;

import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.boot.web.session.AccessResourceInfoSession;
import pro.shushi.pamirs.ux.draft.config.DraftConfigure;
import pro.shushi.pamirs.ux.draft.constant.DraftConstants;
import pro.shushi.pamirs.ux.draft.model.Draft;
import pro.shushi.pamirs.ux.draft.session.DraftSessionContext;
import pro.shushi.pamirs.ux.draft.spi.DraftStrategyApi;
import pro.shushi.pamirs.framework.common.utils.ShortCodeHelper;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.base.D;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;

import java.util.*;

/**
 * 草稿策略抽象基类
 *
 * @author Adamancy Zhang at 16:22 on 2025-10-20
 */
@Slf4j
public abstract class AbstractDraftStrategy implements DraftStrategyApi {

    private static final String DELETE_DRAFT_FUN = "deleteDraft";

//    private static final Map<String, List<String>> WHITE_FUNCTION = new HashMap<>();

    private static final List<String> WHITE_ALL_FUNCTIONS = Arrays.asList(
            "createDraft",
            "updateDraft",
            DELETE_DRAFT_FUN
    );

    private static final List<String> ignoredFunList = Lists.newArrayList();

    protected abstract String getType();

    @Override
    public DraftSessionContext loadSession(Function function, Object... args) {
        if (args.length != 1) {
            return null;
        }
        if (isFunctionInWhite(function.getNamespace(), function.getFun())) {
            return null;
        }
        Object validData = null;
        Map<?, ?> validMapData = null;
        for (Object arg : args) {
            if (arg instanceof D) {
                validData = arg;
                validMapData = ((D) arg).get_d();
                break;
            }
            if (arg instanceof Map) {
                validData = arg;
                validMapData = (Map<?, ?>) arg;
                break;
            }
        }
        if (validData == null) {
            return null;
        }
        String draftCode = (String) validMapData.get(DraftConstants.DRAFT_CODE_FILED);
        if (draftCode == null) {
            return null;
        }
        Draft draft = load(draftCode, validData);
        if (draft == null) {
            return null;
        }
        return new DraftSessionContext(draft, PamirsSession.getContext().getFunctionAllowNull(draft.getModel(), DELETE_DRAFT_FUN));
    }

    private boolean isFunctionInWhite(String namespace, String fun) {
        if (WHITE_ALL_FUNCTIONS.contains(fun)) {
            return true;
        }
        return false;
//        return Optional.ofNullable(WHITE_FUNCTION.get(namespace))
//                .map(v -> v.contains(fun))
//                .orElse(false);
    }

    @Override
    public Draft load(String draftCode, Object data) {
        String model = getModel(data);
        if (StringUtils.isBlank(model)) {
            return null;
        }
        Draft draft = new Draft();
        draft.setModel(model);
        draft.setCode(draftCode);
        return draft;
    }

    @Override
    public Draft load(Object data) {
        String model = getModel(data);
        if (StringUtils.isBlank(model)) {
            return null;
        }
        Draft draft = new Draft();
        draft.setModel(model);
        draft.setPath(getPath());
        draft.setUserId(getUserId());
        draft.setDataPks(getDataPks(model, data));
        draft.setCode(generatorCode(draft));
        return draft;
    }

    protected Long getInvalidDate(long now, Draft draft) {
        Long invalidDate = draft.getInvalidDate();
        if (invalidDate == null) {
            int expire = DraftConfigure.getDefaultExpire();
            if (expire >= 0) {
                invalidDate = now + expire;
            }
        }
        return invalidDate;
    }

    protected Long getInvalidDate(Draft draft) {
        return getInvalidDate(System.currentTimeMillis() / 1000, draft);
    }

    protected String generatorCode(Draft draft) {
        StringBuilder builder = new StringBuilder(draft.getModel());
        String type = getType();
        if (StringUtils.isNotBlank(type)) {
            builder.append(CharacterConstants.SEPARATOR_OCTOTHORPE).append(type);
        }
        List<String> dataPks = draft.getDataPks();
        if (CollectionUtils.isNotEmpty(dataPks)) {
            builder.append(CharacterConstants.SEPARATOR_OCTOTHORPE).append(dataPks);
        }
        String path = draft.getPath();
        if (StringUtils.isNotBlank(path)) {
            builder.append(CharacterConstants.SEPARATOR_OCTOTHORPE).append(path);
        }
        String userId = draft.getUserId();
        if (StringUtils.isNotBlank(userId)) {
            builder.append(CharacterConstants.SEPARATOR_OCTOTHORPE).append(userId);
        }
        return ShortCodeHelper.encode(builder.toString());
    }

    private String getModel(Object data) {
        if (data instanceof Pagination || data instanceof IWrapper) {
            return null;
        }
        String model = Models.api().getDataModel(data);
        if (StringUtils.isBlank(model)) {
            model = Models.api().getModel(data);
        }
        return model;
    }

    private String getPath() {
        return AccessResourceInfoSession.getInfoPath();
    }

    private String getUserId() {
        Object userId = PamirsSession.getUserId();
        if (userId == null) {
            return null;
        }
        return String.valueOf(userId);
    }

    private List<String> getDataPks(String model, Object data) {
        Map<?, ?> validData = null;
        if (data instanceof D) {
            validData = ((D) data).get_d();
        } else if (data instanceof Map) {
            validData = (Map<?, ?>) data;
        }
        if (validData == null) {
            return null;
        }
        ModelConfig modelConfig = PamirsSession.getContext().getModelConfig(model);
        List<String> pks = modelConfig.getPk();
        if (CollectionUtils.isEmpty(pks)) {
            return null;
        }
        pks = new ArrayList<>(pks);
        Collections.sort(pks);
        List<String> pkValues = new ArrayList<>(pks.size());
        for (String pk : pks) {
            Object pkValue;
            pkValue = validData.get(pk);
            if (pkValue != null) {
                pkValues.add(String.valueOf(pkValue));
            }
        }
        if (pkValues.size() == pks.size()) {
            return pkValues;
        }
        if (log.isDebugEnabled()) {
            log.debug("pks values are incomplete. pks: {}, pkValues: {}", pks, pkValues);
        }
        return null;
    }
}
