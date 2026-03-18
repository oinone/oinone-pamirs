package pro.shushi.pamirs.boot.standard.checker.environment;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.standard.checker.PlatformEnvironmentChecker;
import pro.shushi.pamirs.boot.standard.entity.EnvironmentCheckContext;
import pro.shushi.pamirs.boot.standard.entity.EnvironmentKey;
import pro.shushi.pamirs.locale.utils.I18nUtils;
import pro.shushi.pamirs.meta.common.spi.Holder;
import pro.shushi.pamirs.meta.common.util.UUIDUtil;
import pro.shushi.pamirs.meta.domain.PlatformEnvironment;

import java.util.List;

/**
 * 实例环境检查
 *
 * @author Adamancy Zhang at 13:16 on 2024-10-19
 */
@Component
public class InstanceEnvironmentChecker extends AbstractPlatformEnvironmentChecker implements PlatformEnvironmentChecker {

    private static final String ENV_PROTECTED_KEY = "pamirs:check:env-protected";

    private static final String INSTANCE_ID = "instance-id";

    private static Holder<String> instanceIdHolder = new Holder<>();

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean checkBefore(EnvironmentCheckContext context, List<PlatformEnvironment> currentEnvironments, List<PlatformEnvironment> historyEnvironments) {
        String instanceId = stringRedisTemplate.opsForValue().get(ENV_PROTECTED_KEY);
        PlatformEnvironment historyEnvironment = findEnvironmentByKey(historyEnvironments, INSTANCE_ID);
        if (StringUtils.isBlank(instanceId)) {
            if (historyEnvironment == null) {
                // 新的数据库，新的Redis
                instanceId = UUIDUtil.getUUIDNumberString();
                EnvironmentKey.add(INSTANCE_ID);
                PlatformEnvironment newEnvironment = generatorEnvironmentProperty(INSTANCE_ID, instanceId);
                currentEnvironments.add(newEnvironment);
                context.addCreate(newEnvironment);
            } else {
                // 旧的数据库，新的Redis
                instanceId = historyEnvironment.getValue();
            }
            instanceIdHolder.set(instanceId);
            return true;
        }
        if (historyEnvironment == null) {
            // 新的数据库，旧的Redis
            context.addError(generatorImmutableEnvironmentProperty(INSTANCE_ID, instanceId), I18nUtils.getMessage("InstanceEnvironmentChecker.redis.base.db.error") + tip());
            return false;
        } else {
            // 旧的数据库，旧的Redis
            String dbInstanceId = historyEnvironment.getValue();
            if (instanceId.equals(dbInstanceId)) {
                return true;
            }
            context.addError(generatorImmutableEnvironmentProperty(INSTANCE_ID, instanceId), I18nUtils.getMessage("InstanceEnvironmentChecker.redis.mismatch.error") + tip());
            return false;
        }
    }

    @Override
    public void save(EnvironmentCheckContext context, List<PlatformEnvironment> allEnvironments, List<PlatformEnvironment> currentEnvironments, List<PlatformEnvironment> historyEnvironments) {
        String instanceId = instanceIdHolder.get();
        if (StringUtils.isNotBlank(instanceId)) {
            stringRedisTemplate.opsForValue().set(ENV_PROTECTED_KEY, instanceId);
            instanceIdHolder.set(null);
        }
        instanceIdHolder = null;
    }

    private String tip() {
        return I18nUtils.getMessage("InstanceEnvironmentChecker.redis.solution.tip");
    }
}
