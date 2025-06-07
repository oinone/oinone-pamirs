package pro.shushi.pamirs.eip.api.service.model;

import pro.shushi.pamirs.eip.api.pmodel.EipLogProxy;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;

public interface EipLogProxyService {

    <T extends EipLogProxy> Pagination<T> queryPage(Pagination<T> page, IWrapper<T> queryWrapper);

    <T extends EipLogProxy> T queryOne(T query);
}
