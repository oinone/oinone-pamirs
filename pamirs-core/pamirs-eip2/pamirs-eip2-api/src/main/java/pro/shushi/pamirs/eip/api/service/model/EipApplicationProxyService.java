package pro.shushi.pamirs.eip.api.service.model;

import pro.shushi.pamirs.eip.api.pmodel.EipApplicationProxy;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;


public interface EipApplicationProxyService {

    <T extends EipApplicationProxy> T create(T data);

    <T extends EipApplicationProxy> T update(T data);

    <T extends EipApplicationProxy> Pagination<T> queryPage(Pagination<T> page, IWrapper<T> queryWrapper);

    <T extends EipApplicationProxy> T queryOne(T query);

    <T extends EipApplicationProxy> T regenerateSecret(T data);

    <T extends EipApplicationProxy> T dataStatusEnable(T data);

    <T extends EipApplicationProxy> T dataStatusDisable(T data);
}
