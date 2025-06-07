package pro.shushi.pamirs.auth.api.runtime.spi.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.runtime.spi.CurrentRolesFetcher;
import pro.shushi.pamirs.auth.api.runtime.spi.CurrentRolesFetcherApi;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.Spider;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * <h3>默认获取当前角色列表API</h3>
 * <p>
 * Using all {@link CurrentRolesFetcher#fetch()} method collection roles.
 * </p>
 *
 * @author Adamancy Zhang at 18:23 on 2024-01-06
 */
@Order
@Component
@SPI.Service
public class DefaultCurrentRolesFetcherApi implements CurrentRolesFetcherApi {

    @Override
    public Set<Long> fetch() {
        Set<Long> roleIds = new LinkedHashSet<>();
        List<CurrentRolesFetcher> fetchers = Spider.getLoader(CurrentRolesFetcher.class).getOrderedExtensions();
        for (CurrentRolesFetcher fetcher : fetchers) {
            Set<Long> appendRoleIds = fetcher.fetch();
            if (CollectionUtils.isNotEmpty(appendRoleIds)) {
                roleIds.addAll(appendRoleIds);
            }
        }
        return roleIds;
    }
}
