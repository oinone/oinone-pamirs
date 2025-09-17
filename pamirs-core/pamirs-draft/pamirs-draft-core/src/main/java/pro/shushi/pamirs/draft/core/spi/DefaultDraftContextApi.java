package pro.shushi.pamirs.draft.core.spi;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.base.enmu.ActionTypeEnum;
import pro.shushi.pamirs.boot.base.model.ServerAction;
import pro.shushi.pamirs.boot.web.loader.path.AccessResourceInfo;
import pro.shushi.pamirs.boot.web.loader.path.ResourcePath;
import pro.shushi.pamirs.boot.web.loader.path.ResourcePathMetadataType;
import pro.shushi.pamirs.boot.web.session.AccessResourceInfoSession;
import pro.shushi.pamirs.core.common.EncryptHelper;
import pro.shushi.pamirs.draft.api.enums.DraftExpEnumerate;
import pro.shushi.pamirs.draft.api.model.Draft;
import pro.shushi.pamirs.draft.api.spi.DraftContextApi;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.orm.json.PamirsDataUtils;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.util.FieldUtils;
import pro.shushi.pamirs.meta.util.JsonUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 草稿上下文API默认实现
 *
 * @author Gesi at 14:13 on 2025/9/17
 */
@Component
@SPI.Service
public class DefaultDraftContextApi implements DraftContextApi {

    @Override
    public <T> Draft<T> loadDraftContext(T data) {
        Draft<T> draft = new Draft<>();
        String model = Models.api().getDataModel(data);
        draft.setUserId(PamirsSession.getUserId());
        draft.setModel(model);
        if (draft.getUserId() == null) {
            throw PamirsException.construct(DraftExpEnumerate.USER_NOT_FIND).errThrow();
        }
        if (StringUtils.isBlank(model)) {
            throw PamirsException.construct(DraftExpEnumerate.MODEL_NOT_FIND).errThrow();
        }
        draft.setPath(getPath(model, Optional.ofNullable(AccessResourceInfoSession.getInfo()).map(AccessResourceInfo::getPaths).orElse(null)));

        ModelConfig modelConfig = PamirsSession.getContext().getModelConfig(draft.getModel());
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
        draft.setDataPks(JsonUtils.toJSONString(pkValues));

        draft.setDraftIdentifier(generatorDefaultDraftIdentifier(draft.getModel(), draft.getUserId(), draft.getDataPks(), draft.getPath()));
        draft.setCode(generatorCode(draft.getDraftIdentifier()));

        return draft;
    }

    @Override
    public <T> void serializationDraftData(Draft<T> draft, T data) {
        if (data == null) {
            draft.setDraftData(null);
        } else {
            draft.setDraftData(PamirsDataUtils.toJSONString(draft.getModel(), data));
        }
    }

    @Override
    public <T> T deserializationDraftData(Draft<T> draft) {
        if (StringUtils.isBlank(draft.getDraftData())) {
            return null;
        } else {
            return PamirsDataUtils.parseModelObject(draft.getModel(), draft.getDraftData());
        }
    }

    protected String getPath(String model, List<ResourcePath> paths) {
        if (CollectionUtils.isEmpty(paths)) {
            return "[]";
        }

        List<ResourcePath> pagePaths = new ArrayList<>(paths.size());
        for (int i = 0; i < paths.size(); i++) {
            ResourcePath path = paths.get(i);
            if (i == paths.size() - 1) {
                if (ResourcePathMetadataType.ACTION.equals(path.getType()) && StringUtils.equals(path.getModel(), model)) {
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

            }
            pagePaths.add(path);
        }

        return "[" + pagePaths.stream().map(ResourcePath::toString).collect(Collectors.joining(ResourcePath.PATH_SPLIT)) + "]";
    }

    protected String generatorDefaultDraftIdentifier(String model, Long userId, String dataPks, String path) {
        StringBuilder builder = new StringBuilder(model).append(CharacterConstants.SEPARATOR_OCTOTHORPE);
        builder.append(userId).append(CharacterConstants.SEPARATOR_OCTOTHORPE).append(dataPks);
        if (StringUtils.isNotBlank(path)) {
            builder.append(CharacterConstants.SEPARATOR_OCTOTHORPE).append(path);
        }
        return builder.toString();
    }

    protected String generatorCode(String draftIdentifier) {
        return EncryptHelper.shortCode(draftIdentifier);
    }

}
