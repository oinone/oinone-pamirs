package pro.shushi.pamirs.eip.api.strategy.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.springframework.beans.factory.annotation.Autowired;
import pro.shushi.pamirs.eip.api.enmu.EipExpEnumerate;
import pro.shushi.pamirs.framework.session.tenant.component.PamirsTenantSession;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.middleware.zookeeper.service.ZookeeperService;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiPredicate;

/**
 * 基于zk实现的分布式状态同步
 *
 * @author yeshenyue on 2025/4/24 11:19.
 */
@Slf4j
public abstract class AbstractEipDistributedConfigSync {

    @Autowired
    protected ZookeeperService zookeeperService;

    /**
     * 是否已注册过，一个实例仅注册一次
     */
    private final AtomicBoolean registered = new AtomicBoolean(false);

    /**
     * 跟节点路径
     */
    protected abstract String getRootPath();

    /**
     * 服务显示名称，用于日志输出
     */
    protected abstract String getDisplayName();

    /**
     * 注册监听
     */
    public void startListener(TreeCacheListener listener) {
        if (registered.compareAndSet(false, true)) {
            String path = getFullRootPath();
            log.info("{} Register ZK listener: {}", getDisplayName(), path);
            zookeeperService.registerTreeCache(path, listener);
        }
    }

    /**
     * 修改节点数据
     *
     * @param childPath 子节点路径
     * @param data 节点内容
     * @param isRefresh 是否强制刷新
     */
    protected void syncConfig(String childPath, byte[] data, Boolean isRefresh) {
        String path = buildPath(childPath);
        try {
            if (Boolean.TRUE.equals(isRefresh)) {
                zookeeperService.createOrUpdateData(path, data);
            } else {
                zookeeperService.createOrUpdateData(path, data, DEFAULT_COMPARATOR);
            }
        } catch (Exception e) {
            log.error("{} Status refresh failed [childPath:{}]", getDisplayName(), childPath, e);
            throw PamirsException.construct(EipExpEnumerate.EIP_CB_REFRESH_ERROR).errThrow();
        }
    }

    /**
     * 删除节点
     */
    protected void removeConfig(String childPath) {
        String path = buildPath(childPath);
        try {
            if (zookeeperService.checkExists(path)) {
                zookeeperService.delete(path);
            }
        } catch (Exception e) {
            log.error("{} Config deletion failed, childPath={}", getDisplayName(), childPath, e);
        }
    }

    private String buildPath(String childPath) {
        if (childPath == null || childPath.isEmpty()) {
            throw new IllegalArgumentException("childPath must not be null or empty");
        }
        String fullRootPath = getFullRootPath();
        if (childPath.startsWith(CharacterConstants.SEPARATOR_SLASH)) {
            return fullRootPath + childPath;
        } else {
            return fullRootPath + CharacterConstants.SEPARATOR_SLASH + childPath;
        }
    }

    private String getFullRootPath() {
        String tenant = PamirsTenantSession.getTenant();
        String tenantPath = StringUtils.isBlank(tenant) ? "" : ("#" + tenant);
        return fetchProcessorRootPath() + tenantPath;
    }

    private String fetchProcessorRootPath() {
        String path = StringUtils.trim(getRootPath());
        if (StringUtils.isBlank(path)) {
            throw new IllegalArgumentException("Zookeeper root path must not be blank");
        }
        return path.startsWith("/") ? path : "/" + path;
    }

    protected BiPredicate<byte[], byte[]> DEFAULT_COMPARATOR = (originData, data) -> {
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
}
