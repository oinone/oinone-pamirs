package pro.shushi.pamirs.boot.standard.service;

import com.alibaba.fastjson.JSON;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.CreateMode;
import org.springframework.data.redis.core.StringRedisTemplate;
import pro.shushi.pamirs.boot.common.spi.api.boot.BootModulesApi;
import pro.shushi.pamirs.boot.common.spi.api.meta.MetaUpgradeCheckApi;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.session.cache.spi.SessionFillOwnSignApi;
import pro.shushi.pamirs.meta.common.constants.AppName;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.spi.HoldKeeper;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;
import pro.shushi.pamirs.meta.common.util.UUIDUtil;
import pro.shushi.pamirs.meta.util.JsonUtils;
import pro.shushi.pamirs.middleware.zookeeper.service.ZookeeperService;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 元数据升级检查
 *
 * @author Adamancy Zhang at 23:43 on 2024-07-22
 */
@Slf4j
public class MetadataUpgradeChecker {

    private static final String PAMIRS_UPGRADE_CHECKER_KEY = "pamirs:check:upgrade:metadata";

    private static final String PAMIRS_UPGRADE_SESSION_KEY_PREFIX = "pamirs:check:upgrade:session:";

    private static final String PAMIRS_UPGRADE_LOCK_PATH = "/pamirs/check/upgrade";

    private static final byte[] SUCCESS = new byte[]{1};

    private static final HoldKeeper<Boolean> isAllFlushHolder = new HoldKeeper<>();

    private static HoldKeeper<Boolean> lockHolder = new HoldKeeper<>();

    private static HoldKeeper<StringRedisTemplate> stringRedisTemplateHolder = new HoldKeeper<>();

    private static HoldKeeper<ZookeeperService> zookeeperServiceHolder = new HoldKeeper<>();

    private static HoldKeeper<MetadataFlushStatus> serverFlushStatusHolder = new HoldKeeper<>();

    private static HoldKeeper<MetadataFlushStatus> currentFlushStatusHolder = new HoldKeeper<>();

    private static Map<String, MetadataSaveStatus> METADATA_SAVE_STATUS = new ConcurrentHashMap<>();

    private static StringRedisTemplate getStringRedisTemplate() {
        return stringRedisTemplateHolder.supply(() -> BeanDefinitionUtils.getBean(StringRedisTemplate.class));
    }

    private static ZookeeperService getZookeeperService() {
        return zookeeperServiceHolder.supply(() -> BeanDefinitionUtils.getBean(ZookeeperService.class));
    }

    private static MetadataFlushStatus getCurrentFlushStatus() {
        return currentFlushStatusHolder.supply(() -> {
            MetadataFlushStatus flushStatus = new MetadataFlushStatus();
            flushStatus.setStartup(String.valueOf(System.currentTimeMillis()));
            flushStatus.setFinishedKey(getUpgradeSessionKeyPrefix() + UUIDUtil.getUUIDNumberString());
            flushStatus.setRunModules(Spider.getDefaultExtension(BootModulesApi.class).modules());
            return flushStatus;
        });
    }

    private static MetadataFlushStatus getServerFlushStatus() {
        return serverFlushStatusHolder.supply(() -> {
            StringRedisTemplate stringRedisTemplate = getStringRedisTemplate();
            String flushStatusString = stringRedisTemplate.opsForValue().get(getUpgradeCheckerKey());
            if (StringUtils.isBlank(flushStatusString)) {
                return MetadataFlushStatus.NULL_METADATA_FLUSH_STATUS;
            }
            return JSON.parseObject(flushStatusString, MetadataFlushStatus.class);
        });
    }

    public static boolean isAllFlush() {
        return isAllFlushHolder.supply(() -> {
            if (!lock()) {
                log.info("upgrade checker lock not obtained.");
                return false;
            }
            log.info("upgrade checker lock obtained.");
            MetadataFlushStatus currentFlushStatus = getCurrentFlushStatus();
            MetadataFlushStatus serverFlushStatus = getServerFlushStatus();
            log.info("upgrade checker currentFlushStatus: {}", JsonUtils.toJSONString(currentFlushStatus));
            StringRedisTemplate stringRedisTemplate = getStringRedisTemplate();
            if (MetadataFlushStatus.isNull(serverFlushStatus)) {
                writeFlushStatus(currentFlushStatus);
                return Boolean.FALSE.equals(Spider.getDefaultExtension(SessionFillOwnSignApi.class).handleOwnSign());
            }
            log.info("upgrade checker serverFlushStatus: {}", JsonUtils.toJSONString(serverFlushStatus));
            String finishedKey = serverFlushStatus.getFinishedKey();
            String startup = stringRedisTemplate.opsForValue().get(finishedKey);
            String serverStartup = serverFlushStatus.getStartup();
            if (!serverStartup.equals(startup)) {
                log.warn("upgrade checker startup not equal. server startup: {}, finished startup: {}", serverStartup, startup);
                writeFlushStatus(currentFlushStatus);
                return true;
            }
            Set<String> runModules = serverFlushStatus.getRunModules();
            if (runModules == null) {
                runModules = new HashSet<>();
            }
            Set<String> currentRunModules = Spider.getDefaultExtension(BootModulesApi.class).modules();
            boolean isAllFlush = !runModules.containsAll(currentRunModules);
            if (isAllFlush) {
                Set<String> allRunModules = mergeRunModules(runModules, currentRunModules);
                currentFlushStatus.setRunModules(allRunModules);
                if (!isWriteUpgradeKey()) {
                    log.info("upgrade checker rewrite server run modules.");
                    serverFlushStatus.setRunModules(allRunModules);
                    stringRedisTemplate.opsForValue().set(getUpgradeCheckerKey(), JSON.toJSONString(serverFlushStatus));
                }
            }
            log.info("upgrade checker isAllFlush: {}", isAllFlush);
            writeFlushStatus(currentFlushStatus);
            return isAllFlush;
        });
    }

    private static void writeFlushStatus(MetadataFlushStatus currentFlushStatus) {
        if (isWriteUpgradeKey()) {
            getStringRedisTemplate().opsForValue().set(getUpgradeCheckerKey(), JSON.toJSONString(currentFlushStatus));
        }
    }

    private static String getUpgradeCheckerKey() {
        String appName = AppName.get();
        if (StringUtils.isNotBlank(appName)) {
            return PAMIRS_UPGRADE_CHECKER_KEY + CharacterConstants.SEPARATOR_COLON + appName;
        }
        return PAMIRS_UPGRADE_CHECKER_KEY;
    }

    private static String getUpgradeSessionKeyPrefix() {
        String appName = AppName.get();
        if (StringUtils.isNotBlank(appName)) {
            return PAMIRS_UPGRADE_SESSION_KEY_PREFIX + appName + CharacterConstants.SEPARATOR_COLON;
        }
        return PAMIRS_UPGRADE_SESSION_KEY_PREFIX;
    }

    private static boolean lock() {
        return lockHolder.supply(() -> {
            ZookeeperService zookeeperService = getZookeeperService();
            try {
                String path = getUpgradeCheckerLockPath();
                byte[] data = zookeeperService.getData(path);
                if (data != null && data.length == 1 && data[0] == SUCCESS[0]) {
                    log.warn("upgrade checker fetch lock error. data: {}", data);
                    return false;
                }
                zookeeperService.createOrUpdateData(path, SUCCESS, v -> v.withMode(CreateMode.EPHEMERAL), null);
                return true;
            } catch (Exception e) {
                log.warn("upgrade checker fetch lock error.", e);
            }
            return false;
        });
    }

    private static void unlock() {
        if (lock()) {
            try {
                getZookeeperService().delete(getUpgradeCheckerLockPath());
            } catch (Exception e) {
                log.warn("upgrade checker release lock error.", e);
            }
        }
    }

    private static String getUpgradeCheckerLockPath() {
        String appName = AppName.get();
        if (StringUtils.isNotBlank(appName)) {
            return PAMIRS_UPGRADE_LOCK_PATH + CharacterConstants.SEPARATOR_SLASH + appName;
        }
        return PAMIRS_UPGRADE_LOCK_PATH;
    }

    public static void saveMetadataToDB(String model) {
        METADATA_SAVE_STATUS.compute(model, (k, v) -> {
            if (v == null) {
                return new MetadataSaveStatus(k);
            }
            v.saveMetadataToDB();
            return v;
        });
    }

    private static Boolean isWriteUpgradeKey() {
        return Boolean.FALSE.equals(Spider.getDefaultExtension(SessionFillOwnSignApi.class).handleOwnSign());
    }

    public static void saveMetadataToSession(String model, String operator) {
        if (MetaUpgradeCheckApi.DELETE_OPERATOR.equals(operator)) {
            return;
        }
        METADATA_SAVE_STATUS.computeIfAbsent(model, k -> new MetadataSaveStatus(k, false))
                .getSaveMetadataToSession().putIfAbsent(operator, true);
    }

    /**
     * <h>保存元数据全部完成后进行完整保存判断</h>
     * <p>
     * 如果没有完整保存，则下一次启动强制使用全量刷新进行矫正
     * </p>
     */
    public static void saveMetadataFinished() {
        if (isWriteUpgradeKey()) {
            MetadataFlushStatus flushStatus = getCurrentFlushStatus();
            boolean isFullSuccess = true;
            for (MetadataSaveStatus saveStatus : METADATA_SAVE_STATUS.values()) {
                int saveDBOperatorCount = saveStatus.getSaveMetadataToDB();
                Map<String, Boolean> saveMetadataSessionStatus = saveStatus.getSaveMetadataToSession();
                int saveMetadataSessionCount = saveMetadataSessionStatus.size();
                if (!saveMetadataSessionStatus.isEmpty() &&
                        saveDBOperatorCount != saveMetadataSessionCount &&
                        !saveMetadataSessionStatus.values().stream().allMatch(v -> v)) {
                    isFullSuccess = false;
                    log.warn("upgrade checker is not full success. saveDBOperatorCount: {}, saveMetadataSessionCount: {}", saveDBOperatorCount, saveMetadataSessionCount);
                    break;
                }
            }
            if (isFullSuccess) {
                String finishedKey = flushStatus.getFinishedKey();
                String startup = flushStatus.getStartup();
                log.info("upgrade checker write upgrade session key. finishedKey: {}, startup: {}", finishedKey, startup);
                StringRedisTemplate stringRedisTemplate = getStringRedisTemplate();
                Set<String> oldKeys = stringRedisTemplate.keys(getUpgradeSessionKeyPrefix() + CharacterConstants.SEPARATOR_ASTERISK);
                if (CollectionUtils.isNotEmpty(oldKeys)) {
                    stringRedisTemplate.delete(oldKeys);
                }
                stringRedisTemplate.opsForValue().set(finishedKey, startup);
            }
        }
        unlock();
        clear();
    }

    private static Set<String> mergeRunModules(Set<String> serverRunModules, Set<String> currentRunModules) {
        if (serverRunModules == null) {
            serverRunModules = Collections.emptySet();
        }
        if (currentRunModules == null) {
            currentRunModules = Collections.emptySet();
        }
        Set<String> allRunModules = new HashSet<>();
        allRunModules.addAll(serverRunModules);
        allRunModules.addAll(currentRunModules);
        return allRunModules;
    }

    private static void clear() {
        lockHolder = null;
        stringRedisTemplateHolder = null;
        zookeeperServiceHolder = null;
        serverFlushStatusHolder = null;
        currentFlushStatusHolder = null;
        METADATA_SAVE_STATUS = null;
    }

    private static class MetadataSaveStatus {

        private final String model;

        private final AtomicInteger saveMetadataToDB;

        private final Map<String, Boolean> saveMetadataToSession;

        public MetadataSaveStatus(String model) {
            this(model, true);
        }

        public MetadataSaveStatus(String model, boolean saveMetadataToDB) {
            this.model = model;
            if (saveMetadataToDB) {
                this.saveMetadataToDB = new AtomicInteger(1);
            } else {
                this.saveMetadataToDB = new AtomicInteger(0);
            }
            this.saveMetadataToSession = new ConcurrentHashMap<>();
        }

        public String getModel() {
            return model;
        }

        public int getSaveMetadataToDB() {
            return saveMetadataToDB.get();
        }

        public void saveMetadataToDB() {
            this.saveMetadataToDB.incrementAndGet();
        }

        public Map<String, Boolean> getSaveMetadataToSession() {
            return saveMetadataToSession;
        }
    }

    private static class MetadataFlushStatus {

        public static final MetadataFlushStatus NULL_METADATA_FLUSH_STATUS;

        static {
            NULL_METADATA_FLUSH_STATUS = new MetadataFlushStatus();
            NULL_METADATA_FLUSH_STATUS.setIsNull(true);
        }

        private boolean isNull;

        private String startup;

        private String finishedKey;

        private Set<String> runModules;

        public boolean getIsNull() {
            return isNull;
        }

        public void setIsNull(boolean isNull) {
            this.isNull = isNull;
        }

        public String getStartup() {
            return startup;
        }

        public void setStartup(String startup) {
            this.startup = startup;
        }

        public String getFinishedKey() {
            return finishedKey;
        }

        public void setFinishedKey(String finishedKey) {
            this.finishedKey = finishedKey;
        }

        public Set<String> getRunModules() {
            return runModules;
        }

        public void setRunModules(Set<String> runModules) {
            this.runModules = runModules;
        }

        public static boolean isNull(MetadataFlushStatus flushStatus) {
            return NULL_METADATA_FLUSH_STATUS.equals(flushStatus);
        }

        public static boolean isNotNull(MetadataFlushStatus flushStatus) {
            return !isNull(flushStatus);
        }
    }
}
