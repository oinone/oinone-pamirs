package pro.shushi.pamirs.auth.core.init;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.AuthModule;
import pro.shushi.pamirs.auth.api.constants.SystemRole;
import pro.shushi.pamirs.auth.api.constants.SystemRoleType;
import pro.shushi.pamirs.auth.api.enumeration.AuthorizationSourceEnum;
import pro.shushi.pamirs.auth.api.model.AuthRole;
import pro.shushi.pamirs.auth.api.model.AuthRoleType;
import pro.shushi.pamirs.auth.api.model.AuthUserRoleRel;
import pro.shushi.pamirs.auth.api.service.AuthRoleService;
import pro.shushi.pamirs.auth.api.service.AuthRoleTypeService;
import pro.shushi.pamirs.auth.api.service.authorize.AuthUserAuthorizeService;
import pro.shushi.pamirs.auth.api.user.AuthUserService;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.api.init.InstallDataInit;
import pro.shushi.pamirs.boot.common.api.init.UpgradeDataInit;
import pro.shushi.pamirs.core.common.cache.MemoryListSearchCache;
import pro.shushi.pamirs.core.common.version.Version;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 权限模块数据初始化
 *
 * @author Adamancy Zhang at 09:45 on 2024-01-05
 */
@Slf4j
@Component
public class AuthModuleDataInit implements InstallDataInit, UpgradeDataInit {

    @Autowired
    private AuthRoleTypeService authRoleTypeService;

    @Autowired
    private AuthRoleService authRoleService;

    @Autowired
    private AuthUserAuthorizeService authUserAuthorizeService;

    @Autowired
    private AuthUserService userInfoService;

    @Override
    public boolean init(AppLifecycleCommand command, String version) {
        return upgrade(command, version, null);
    }

    @Override
    public boolean upgrade(AppLifecycleCommand command, String version, String existVersion) {
        Version existModuleVersion = null;
        if (Version.check(existVersion)) {
            existModuleVersion = Version.parse(existVersion);
        }
        initRoleType();
        initRole(existModuleVersion);
        AuthCacheChecker.check();
        return true;
    }

    private void initRoleType() {
        AuthRoleType systemRoleType = SystemRoleType.system();
        if (authRoleTypeService.count(Pops.<AuthRoleType>lambdaQuery()
                .from(AuthRoleType.MODEL_MODEL)
                .eq(AuthRoleType::getCode, systemRoleType.getCode())) >= 1) {
            return;
        }
        authRoleTypeService.create(systemRoleType);
    }

    private void initRole(Version oldVersion) {
        AuthRole adminRole = SystemRole.admin();
        List<AuthRole> initRoleList = Lists.newArrayList(adminRole, SystemRole.base(), SystemRole.business());

        List<AuthRole> existRoles = authRoleService.queryListByWrapper(Pops.<AuthRole>lambdaQuery()
                .from(AuthRole.MODEL_MODEL)
                .select(AuthRole::getId, AuthRole::getCode)
                .in(AuthRole::getCode, initRoleList.stream().map(AuthRole::getCode).collect(Collectors.toList())));
        MemoryListSearchCache<String, AuthRole> existRoleCache = new MemoryListSearchCache<>(existRoles, AuthRole::getCode);

        List<AuthRole> createRoleList = new ArrayList<>();
        List<AuthRole> forceUpdateRoleList = new ArrayList<>();
        for (AuthRole role : initRoleList) {
            AuthRole existRole = existRoleCache.get(role.getCode());
            if (existRole == null) {
                createRoleList.add(role);
            } else if (!existRole.getId().equals(role.getId())) {
                log.error("请手动更新角色ID以保证内置角色权限正常运行。oldId = {}, newId = {}", existRole.getId(), role.getId());
            }
//            else if (isForceUpdate(oldVersion)) {
//                AuthRole forceUpdateRole = new AuthRole();
//                forceUpdateRole.setCode(role.getCode())
//                        .setSource(role.getSource())
//                        .setRoleTypeCode(role.getRoleTypeCode())
//                        .setId(role.getId());
//                forceUpdateRoleList.add(forceUpdateRole);
//            }
        }

        if (!createRoleList.isEmpty()) {
            authRoleService.createBatch(createRoleList);
            if (createRoleList.stream().anyMatch(v -> v.getCode().equals(adminRole.getCode()))) {
                authUserAuthorizeService.authorize(userInfoService.getAdminUser().getId(), adminRole.getId(), AuthorizationSourceEnum.BUILD_IN);
            }
        }

        if (!forceUpdateRoleList.isEmpty()) {
            forceUpdateRoleList.forEach(role -> {
                authRoleService.updateByWrapper(role, Pops.<AuthRole>lambdaUpdate()
                        .from(AuthRole.MODEL_MODEL)
                        .eq(AuthRole::getCode, role.getCode()));
                AuthRole existRole = existRoleCache.get(role.getCode());
                if (existRole != null) {
                    Models.origin().updateByWrapper(new AuthUserRoleRel().setRoleId(role.getId()), Pops.<AuthUserRoleRel>lambdaUpdate()
                            .from(AuthUserRoleRel.MODEL_MODEL)
                            .eq(AuthUserRoleRel::getRoleId, existRole.getId()));
                }
            });
        }
    }

    private boolean isForceUpdate(Version oldVersion) {
        return oldVersion == null || AuthModule.version.compareTo(oldVersion) > 0;
    }

    @Override
    public List<String> modules() {
        return Lists.newArrayList(AuthModule.MODULE_MODULE);
    }

    @Override
    public int priority() {
        return 0;
    }
}
