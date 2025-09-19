package pro.shushi.pamirs.draft.core.service.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.boot.base.enmu.ActionTypeEnum;
import pro.shushi.pamirs.boot.base.model.ServerAction;
import pro.shushi.pamirs.boot.web.loader.path.AccessResourceInfo;
import pro.shushi.pamirs.boot.web.loader.path.ResourcePath;
import pro.shushi.pamirs.boot.web.loader.path.ResourcePathMetadataType;
import pro.shushi.pamirs.boot.web.session.AccessResourceInfoSession;
import pro.shushi.pamirs.core.common.EncryptHelper;
import pro.shushi.pamirs.draft.api.enums.DraftExpEnumerate;
import pro.shushi.pamirs.draft.api.model.Draft;
import pro.shushi.pamirs.draft.api.service.DraftService;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.orm.json.PamirsDataUtils;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.base.BaseModel;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.lambda.LambdaUtil;
import pro.shushi.pamirs.meta.util.FieldUtils;
import pro.shushi.pamirs.meta.util.JsonUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Gesi at 16:34 on 2025/9/19
 */
@Service
public class DraftServiceImpl implements DraftService {

    @Override
    public <T> T queryDraft(T data) {
        Draft draft = loadDraftContext(data);
        Draft dbDraft = queryDbDraft(draft);
        if (dbDraft != null) {
            data = deserializationDraftData(dbDraft);
            FieldUtils.setFieldValue(data, LambdaUtil.fetchFieldName(BaseModel::getDraftCode), dbDraft.getCode());
            return data;
        }
        return null;
    }

    @Override
    public <T> T queryDraftByWrapper(IWrapper<T> queryWrapper) {
        T data = Models.origin().queryOneByWrapper(queryWrapper);
        return queryDraft(data);
    }

    @Override
    public <T> T createDraft(T data) {
        Draft draft = loadDraftContext(data);
        serializationDraftData(draft, data);
        draft = draft.create();
        FieldUtils.setFieldValue(data, LambdaUtil.fetchFieldName(BaseModel::getDraftCode), draft.getCode());
        return data;
    }

    @Override
    public <T> T updateDraft(T data) {
        String draftCode = (String) FieldUtils.getFieldValue(data, LambdaUtil.fetchFieldName(BaseModel::getDraftCode));
        Draft dbDraft = new Draft().queryOneByWrapper(
                Pops.<Draft>lambdaQuery().from(Draft.MODEL_MODEL)
                        .eq(Draft::getCode, draftCode)
        );
        serializationDraftData(dbDraft, data);
        dbDraft.updateById();
        FieldUtils.setFieldValue(data, LambdaUtil.fetchFieldName(BaseModel::getDraftCode), dbDraft.getCode());
        return data;
    }

    @Override
    public Boolean deleteDraft(String draftCode) {
        if (StringUtils.isBlank(draftCode)) {
            return false;
        }
        Integer deleted = new Draft().deleteByWrapper(
                Pops.<Draft>lambdaQuery().from(Draft.MODEL_MODEL)
                        .eq(Draft::getCode, draftCode)
        );
        return deleted > 0;
    }

    private <T> Draft queryDbDraft(Draft draft) {
        if (StringUtils.isBlank(draft.getCode())) {
            return null;
        }
        return new Draft().queryOneByWrapper(Pops.<Draft>lambdaQuery().from(Draft.MODEL_MODEL).eq(Draft::getCode, draft.getCode()));
    }

    private <T> Draft loadDraftContext(T data) {
        Draft draft = new Draft();
        String model = Models.api().getDataModel(data);
        draft.setUserId(PamirsSession.getUserId());
        draft.setModel(model);
        if (draft.getUserId() == null) {
            throw PamirsException.construct(DraftExpEnumerate.USER_NOT_FIND).errThrow();
        }
        if (StringUtils.isBlank(model)) {
            throw PamirsException.construct(DraftExpEnumerate.MODEL_NOT_FIND).errThrow();
        }
        draft.setPath(getPath(model));
        draft.setDataPks(getDataPks(model, data));
        draft.setCode(generatorCode(generatorDefaultDraftIdentifier(draft)));

        return draft;
    }

    private <T> void serializationDraftData(Draft draft, T data) {
        if (data == null) {
            draft.setDraftData(null);
        } else {
            draft.setDraftData(PamirsDataUtils.toJSONString(draft.getModel(), data));
        }
    }

    private <T> T deserializationDraftData(Draft draft) {
        if (StringUtils.isBlank(draft.getDraftData())) {
            return null;
        } else {
            return PamirsDataUtils.parseModelObject(draft.getModel(), draft.getDraftData());
        }
    }

    private String getPath(String model) {
        List<ResourcePath> paths = Optional.ofNullable(AccessResourceInfoSession.getInfo()).map(AccessResourceInfo::getPaths).orElse(null);
        if (CollectionUtils.isEmpty(paths)) {
            return "[]";
        }

        List<ResourcePath> pagePaths = new ArrayList<>(paths.size());
        for (int i = 0; i < paths.size(); i++) {
            ResourcePath path = paths.get(i);
            if (i == paths.size() - 1) {
                if (ResourcePathMetadataType.ACTION.equals(path.getType())) {
                    if (StringUtils.equals(path.getModel(), model)) {
                        List<ServerAction> serverActions = new ServerAction().queryList(
                                Pops.<ServerAction>lambdaQuery().from(ServerAction.MODEL_MODEL)
                                        .eq(ServerAction::getActionType, ActionTypeEnum.SERVER.value())
                                        .eq(ServerAction::getModel, model)
                                        .eq(ServerAction::getName, path.getName())
                        );
                        if (CollectionUtils.isNotEmpty(serverActions)) {
                            break;
                        }
                    }
                    if (path.getName().startsWith("uiClient")) {
                        break;
                    }
                }

            }
            pagePaths.add(path);
        }

        return "[" + pagePaths.stream().map(ResourcePath::toString).collect(Collectors.joining(ResourcePath.PATH_SPLIT)) + "]";
    }

    private <T> String getDataPks(String model, T data) {
        ModelConfig modelConfig = PamirsSession.getContext().getModelConfig(model);
        List<String> pks = new ArrayList<>(modelConfig.getPk());
        Collections.sort(pks);
        List<Object> pkValues = new ArrayList<>(pks.size());
        for (String pk : pks) {
            Object pkValue;
            if (data != null) {
                pkValue = FieldUtils.getFieldValue(data, pk);
            } else {
                pkValue = null;
            }
            pkValues.add(pkValue);
        }

        return JsonUtils.toJSONString(pkValues);
    }

    private String generatorDefaultDraftIdentifier(Draft draft) {
        String model = draft.getModel();
        Long userId = draft.getUserId();
        String dataPks = draft.getDataPks();
        String path = draft.getPath();

        StringBuilder builder = new StringBuilder(model).append(CharacterConstants.SEPARATOR_OCTOTHORPE);
        builder.append(userId).append(CharacterConstants.SEPARATOR_OCTOTHORPE).append(dataPks);
        if (StringUtils.isNotBlank(path)) {
            builder.append(CharacterConstants.SEPARATOR_OCTOTHORPE).append(path);
        }
        return builder.toString();
    }

    private String generatorCode(String draftIdentifier) {
        return EncryptHelper.shortCode(draftIdentifier);
    }

}
