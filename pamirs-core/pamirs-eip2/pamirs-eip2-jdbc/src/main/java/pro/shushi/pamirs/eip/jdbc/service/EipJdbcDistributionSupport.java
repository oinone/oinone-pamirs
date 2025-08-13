package pro.shushi.pamirs.eip.jdbc.service;

import pro.shushi.pamirs.eip.api.model.connector.EipConnector;
import pro.shushi.pamirs.meta.api.dto.common.Result;

import java.util.List;

/**
 * EIP Jdbc 分布式支持
 *
 * @author Adamancy Zhang at 11:19 on 2025-08-13
 */
public interface EipJdbcDistributionSupport {

    String NODE_PATH_PREFIX = "/eip/jdbc";

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
     * 刷新指定连接器
     */
    Result<String> refreshConnector(EipConnector connector);
}
