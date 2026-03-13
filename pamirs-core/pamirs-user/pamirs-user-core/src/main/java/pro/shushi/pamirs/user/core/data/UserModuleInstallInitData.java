package pro.shushi.pamirs.user.core.data;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.api.init.InstallDataInit;
import pro.shushi.pamirs.boot.common.api.init.UpgradeDataInit;
import pro.shushi.pamirs.core.common.cache.MemoryListSearchCache;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.user.api.UserModule;
import pro.shushi.pamirs.user.api.constants.SystemUser;
import pro.shushi.pamirs.user.api.model.PamirsUser;
import pro.shushi.pamirs.user.api.service.PasswordService;
import pro.shushi.pamirs.user.api.service.UserSimpleService;
import pro.shushi.pamirs.user.api.spi.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author shier
 * date 2020/4/16
 */
@Slf4j
@Component
public class UserModuleInstallInitData implements InstallDataInit, UpgradeDataInit {

    @Autowired
    private UserSimpleService userSimpleService;

    @Autowired
    private PasswordService passwordService;

    @Override
    public boolean init(AppLifecycleCommand command, String version) {
        return upgrade(command, version, null);
    }

    @Override
    public boolean upgrade(AppLifecycleCommand command, String version, String existVersion) {
        initUser();
        return true;
    }

    private void initUser() {
        PamirsUser anonymous = SystemUser.anonymous();
        PamirsUser admin = SystemUser.admin();
        List<PamirsUser> initUserList = Lists.newArrayList(anonymous, admin);

        List<PamirsUser> existUsers = Models.origin().queryListByWrapper(Pops.<PamirsUser>lambdaQuery()
                .from(PamirsUser.MODEL_MODEL)
                .select(PamirsUser::getId, PamirsUser::getPassword, PamirsUser::getInitialPassword)
                .in(PamirsUser::getId, initUserList.stream().map(PamirsUser::getId).collect(Collectors.toList())));
        MemoryListSearchCache<Long, PamirsUser> existUserCache = new MemoryListSearchCache<>(existUsers, PamirsUser::getId);

        List<PamirsUser> createUserList = new ArrayList<>();
        for (PamirsUser user : initUserList) {
            PamirsUser existUser = existUserCache.get(user.getId());
            if (existUser == null) {
                createUserList.add(user);
            } else if (!existUser.getId().equals(user.getId())) {
                log.error("Please manually update the user ID to ensure the normal operation of the built-in user permissions. oldId = {}, newId = {}", existUser.getId(), user.getId());
            }
        }

        if (!createUserList.isEmpty()) {
            PasswordEncoder encoder = Spider.getDefaultExtension(PasswordEncoder.class);
            createUserList = userSimpleService.createBatch(createUserList.stream().peek(v -> {
                String initialPassword = v.getInitialPassword();
                if (StringUtils.isNotBlank(initialPassword)) {
                    v.setInitialPassword(encoder.encode(initialPassword));
                }
                String password = v.getPassword();
                if (StringUtils.isNotBlank(password)) {
                    v.setPassword(encoder.encode(password));
                }
            }).collect(Collectors.toList()));
            initPasswords(createUserList);
        }
    }

    private void initPasswords(List<PamirsUser> users) {
        List<Long> userIds = new ArrayList<>(users.size());
        List<String> passwords = new ArrayList<>(users.size());
        for (PamirsUser user : users) {
            String password = user.getInitialPassword();
            if (StringUtils.isNotBlank(password)) {
                userIds.add(user.getId());
                passwords.add(password);
            }
        }
        if (!passwords.isEmpty()) {
            passwordService.createBatch(userIds, passwords);
        }
    }

    @Override
    public List<String> modules() {
        return Lists.newArrayList(UserModule.MODULE_MODULE);
    }

    @Override
    public int priority() {
        return 0;
    }

}
