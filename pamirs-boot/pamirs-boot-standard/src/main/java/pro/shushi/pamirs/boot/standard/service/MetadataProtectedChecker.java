package pro.shushi.pamirs.boot.standard.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.api.init.LifecycleBeginInit;
import pro.shushi.pamirs.boot.standard.config.MetadataProtectedConfig;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.session.cache.spi.SessionFillOwnSignApi;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;

import java.util.List;
import java.util.Set;

/**
 * 元数据保护检查
 *
 * @author Adamancy Zhang at 00:22 on 2024-07-23
 */
@Slf4j
@Order(99)
@Component
public class MetadataProtectedChecker implements LifecycleBeginInit {

    private static final String META_PROTECTED_KEY = "pamirs:check:meta-protected";

    private static final String CD_OWN_SIGN_API_CLASS_NAME = "pro.shushi.pamirs.distribution.session.cd.spi.CdDistributionSessionFillOwnSignApi";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void process(AppLifecycleCommand command, Set<String> runModules,
                        List<ModuleDefinition> installModules, List<ModuleDefinition> upgradeModules, List<ModuleDefinition> reloadModules) {
        String publicMetaProtected = stringRedisTemplate.opsForValue().get(META_PROTECTED_KEY);
        String currentMetaProtected = MetadataProtectedConfig.getMetaProtected();
        boolean hasPublicMetaProtected = StringUtils.isNotBlank(publicMetaProtected);
        boolean hasCurrentMetaProtected = StringUtils.isNotBlank(currentMetaProtected);
        if (!hasPublicMetaProtected && !hasCurrentMetaProtected) {
            return;
        }
        boolean handleOwnSign = Spider.getDefaultExtension(SessionFillOwnSignApi.class).handleOwnSign();
        if (hasCurrentMetaProtected) {
            if (handleOwnSign) {
                throw new UnsupportedOperationException("在使用元数据保护模式下，不允许设置 [pamirs.distribution.session.ownSign]");
            }
            if (!hasPublicMetaProtected || MetadataProtectedConfig.isForceProtected()) {
                writeMetaProtected(currentMetaProtected);
            } else if (!currentMetaProtected.equals(publicMetaProtected)) {
                if (StringUtils.isNotBlank(publicMetaProtected)) {
                    throw new UnsupportedOperationException("当前环境与现有环境的保护标记不一致，请修改当前环境保护标记为: " + publicMetaProtected);
                }
                throw unsupportedLocalOperation();
            }
        } else {
            if (handleOwnSign) {
                return;
            }
            throw unsupportedLocalOperation();
        }
    }

    private UnsupportedOperationException unsupportedLocalOperation() {
        boolean isDependencyCdPom = CD_OWN_SIGN_API_CLASS_NAME.equals(Spider.getDefaultExtension(SessionFillOwnSignApi.class).getClass().getName());
        if (isDependencyCdPom) {
            return new UnsupportedOperationException("公共环境开启了元数据保护模式，本地开发环境需配置 [pamirs.distribution.session.ownSign]");
        } else {
            return new UnsupportedOperationException("公共环境开启了元数据保护模式，本地开发环境需要添加POM依赖 [pro.shushi.pamirs.distribution#pamirs-distribution-session-cd] 并配置 [pamirs.distribution.session.ownSign]");
        }
    }

    private void writeMetaProtected(String metaProtected) {
        stringRedisTemplate.opsForValue().set(META_PROTECTED_KEY, metaProtected);
    }
}
