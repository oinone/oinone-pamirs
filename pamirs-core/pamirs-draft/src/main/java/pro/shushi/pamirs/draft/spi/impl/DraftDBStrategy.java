package pro.shushi.pamirs.draft.spi.impl;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.draft.constant.DraftConstants;
import pro.shushi.pamirs.draft.model.Draft;
import pro.shushi.pamirs.draft.spi.DraftStrategyApi;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.orm.json.PamirsDataUtils;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.common.spi.SPI;

/**
 * 数据库草稿存储策略API
 *
 * @author Gesi at 17:56 on 2025/9/19
 */
@Component
@SPI.Service(DraftConstants.DB_STORAGE)
public class DraftDBStrategy extends AbstractDraftStrategy implements DraftStrategyApi {

    @Override
    protected String getType() {
        return DraftConstants.DB_STORAGE;
    }

    @Override
    public Draft get(Object data) {
        Draft draft = load(data);
        if (draft == null) {
            return null;
        }
        return Models.origin().queryOneByWrapper(Pops.<Draft>lambdaQuery()
                .from(Draft.MODEL_MODEL)
                .eq(Draft::getCode, draft.getCode())
                .and((wrapper -> wrapper.isNull(Draft::getInvalidDate).or().ge(Draft::getInvalidDate, System.currentTimeMillis() / 1000))));
    }

    @Override
    public Draft getByWrapper(IWrapper<?> wrapper) {
        Object data = Models.data().queryOneByWrapper(wrapper);
        if (data == null) {
            return null;
        }
        return get(data);
    }

    @Override
    public Draft create(Draft draft) {
        draft.setInvalidDate(getInvalidDate(draft));
        draft.createOrUpdate();
        return draft;
    }

    @Override
    public Draft update(Draft draft) {
        return create(draft);
    }

    @Override
    public boolean delete(String draftCode) {
        Models.origin().deleteByWrapper(Pops.<Draft>lambdaQuery().from(Draft.MODEL_MODEL).eq(Draft::getCode, draftCode));
        return true;
    }

    @Override
    public <T> Object serialization(String model, T data) {
        return PamirsDataUtils.toJSONString(model, data);
    }

    @Override
    public <T> T deserialization(String model, Object draftData) {
        return PamirsDataUtils.parseModelObject(model, (String) draftData);
    }
}
