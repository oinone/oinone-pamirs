package pro.shushi.pamirs.core.common.standard.service.impl;

import org.springframework.lang.Nullable;
import pro.shushi.pamirs.core.common.standard.service.StandardModelService;
import pro.shushi.pamirs.meta.base.IdModel;

/**
 * 标准Id模型服务抽象实现
 *
 * @author Adamancy Zhang at 20:30 on 2021-07-29
 */
public abstract class AbstractStandardIdModelService<T extends IdModel> extends AbstractStandardModelService<T> implements StandardModelService<T> {

    @Override
    protected T createOrUpdateBefore(@Nullable T origin, T data) {
        if (origin != null) {
            data.setId(origin.getId());
        }
        return data;
    }
}
