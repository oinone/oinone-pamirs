package pro.shushi.pamirs.auth.api.spi.service;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.entity.AuthResult;
import pro.shushi.pamirs.auth.api.holder.AuthApiHolder;
import pro.shushi.pamirs.auth.api.runtime.cache.DataPermissionCacheApi;
import pro.shushi.pamirs.auth.api.spi.AuthFilterService;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.constant.RSqlConstants;

import java.util.Iterator;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * 默认过滤服务
 *
 * @author Adamancy Zhang at 11:17 on 2024-04-23
 */
@Order
@Component
@SPI.Service
public class DefaultAuthFilterService implements AuthFilterService {

    @Override
    public AuthResult<String> fetchModelFilterForRead(String model) {
        return fetchModelFilter(model, (api) -> api::fetchRowPermissionsForRead).transfer(this::fetchModelFilterAfterProperties);
    }

    @Override
    public AuthResult<String> fetchModelFilterForWrite(String model) {
        return fetchModelFilter(model, (api) -> api::fetchRowPermissionsForWrite).transfer(this::fetchModelFilterAfterProperties);
    }

    @Override
    public AuthResult<String> fetchModelFilterForDelete(String model) {
        return fetchModelFilter(model, (api) -> api::fetchRowPermissionsForDelete).transfer(this::fetchModelFilterAfterProperties);
    }

    protected AuthResult<Set<String>> fetchModelFilter(String model, Function<DataPermissionCacheApi, BiFunction<Set<Long>, String, AuthResult<Set<String>>>> fetcher) {
        return AuthApiHolder.getFetchPermissionApi().fetch((accessInfo, roleIds) -> fetcher.apply(AuthApiHolder.getDataPermissionCacheApi()).apply(roleIds, model));
    }

    protected String fetchModelFilterAfterProperties(Set<String> filters) {
        int filterSize = filters.size();
        if (filterSize == 0) {
            return emptyFilter();
        }
        if (filterSize == 1) {
            return filters.iterator().next();
        }
        StringBuilder filterBuilder = new StringBuilder();
        Iterator<String> filterIterator = filters.iterator();
        filterBuilder.append(CharacterConstants.LEFT_BRACKET).append(filterIterator.next()).append(CharacterConstants.RIGHT_BRACKET);
        while (filterIterator.hasNext()) {
            filterBuilder.append(RSqlConstants.SPACE_OR).append(CharacterConstants.LEFT_BRACKET).append(filterIterator.next()).append(CharacterConstants.RIGHT_BRACKET);
        }
        return filterBuilder.toString();
    }

    protected String emptyFilter() {
        return null;
    }
}
