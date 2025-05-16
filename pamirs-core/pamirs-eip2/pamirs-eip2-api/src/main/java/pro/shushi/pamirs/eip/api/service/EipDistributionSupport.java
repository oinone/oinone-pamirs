package pro.shushi.pamirs.eip.api.service;

import pro.shushi.pamirs.eip.api.model.EipIntegrationInterface;
import pro.shushi.pamirs.eip.api.model.EipOpenInterface;
import pro.shushi.pamirs.eip.api.model.EipRouteDefinition;
import pro.shushi.pamirs.meta.api.dto.common.Result;

import java.util.List;
import java.util.function.BiPredicate;

public interface EipDistributionSupport {

    BiPredicate<byte[], byte[]> DEFAULT_COMPARATOR = (originData, data) -> {
        if (originData == null) {
            return Boolean.TRUE;
        }
        if (data == null) {
            return Boolean.FALSE;
        }
        if (originData.length == 1) {
            if (originData[0] == data[0]) {
                return Boolean.FALSE;
            } else {
                return Boolean.TRUE;
            }
        } else {
            return Boolean.TRUE;
        }
    };

    String ZOOKEEPER_PARENT_NODE_PATH_PREFIX = "/eip";

    byte[] ENABLED = new byte[]{1};

    byte[] DISABLED = new byte[]{0};

    /**
     * 开启分布式支持
     */
    void start() throws Exception;

    /**
     * 关闭分布式支持
     */
    void close();

    /**
     * 注册监听
     */
    void registerListener(List<String> tenantRootPathList);

    /**
     * 刷新路由定义
     */
    Result<String> refreshRouteDefinition(EipRouteDefinition eipInterface);

    /**
     * 刷新集成接口
     */
    Result<String> refreshInterface(EipIntegrationInterface eipInterface);

    /**
     * 刷新开放接口
     */
    Result<String> refreshOpenInterface(EipOpenInterface eipInterface);
}
