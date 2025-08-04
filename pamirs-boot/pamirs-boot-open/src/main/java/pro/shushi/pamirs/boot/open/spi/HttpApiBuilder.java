package pro.shushi.pamirs.boot.open.spi;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.spi.api.infrastructure.HttpApiBuilderApi;
import pro.shushi.pamirs.framework.gateways.graph.configuration.PamirsFrameworkGatewayConfiguration;
import pro.shushi.pamirs.framework.gateways.graph.java.build.GraphQLBuilder;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.common.spi.SPI;

import jakarta.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 启动构建http协议接口
 * <p>
 * 2020/8/27 5:05 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Order(88)
@Component
@SPI.Service
public class HttpApiBuilder implements HttpApiBuilderApi {

    @Resource
    private PamirsFrameworkGatewayConfiguration pamirsFrameworkGatewayConfiguration;

    @Override
    public void build(AppLifecycleCommand command, Set<String> runModuleSet, List<Meta> metaList) {
        if (!command.getOptions().isRebuildHttpApi()) {
            return;
        }
        if (CollectionUtils.isEmpty(metaList)) {
            return;
        }

        // 构建GraphQL
        Set<String> buildHttpApiModules;
        if (pamirsFrameworkGatewayConfiguration.isBuildAll()) {
            buildHttpApiModules = new HashSet<>(runModuleSet);
            buildHttpApiModules.addAll(metaList.stream().map(Meta::getData).map(Map::keySet).flatMap(Collection::stream).collect(Collectors.toSet()));
        } else {
            buildHttpApiModules = runModuleSet;
        }
        GraphQLBuilder.build(buildHttpApiModules, pamirsFrameworkGatewayConfiguration.isShowDoc());
    }

}
