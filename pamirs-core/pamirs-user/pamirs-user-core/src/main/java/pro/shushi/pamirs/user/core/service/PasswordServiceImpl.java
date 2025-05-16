package pro.shushi.pamirs.user.core.service;

import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.core.common.DataShardingHelper;
import pro.shushi.pamirs.core.common.ObjectHelper;
import pro.shushi.pamirs.core.common.entry.Holder;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.user.api.enmu.UserExpEnumerate;
import pro.shushi.pamirs.user.api.model.PamirsPassword;
import pro.shushi.pamirs.user.api.service.PasswordService;
import pro.shushi.pamirs.user.api.spi.PasswordEncoder;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户密码服务实现
 *
 * @author Adamancy Zhang at 18:37 on 2024-01-12
 */
@Service
@Fun(PasswordService.FUN_NAMESPACE)
public class PasswordServiceImpl implements PasswordService {

    @Function
    @Override
    public Boolean create(Long userId, String password) {
        return create0(userId, password, false);
    }

    @Function
    @Override
    public Boolean encodingCreate(Long userId, String rawPassword) {
        return create0(userId, rawPassword, true);
    }

    @Function
    @Override
    public Boolean createBatch(Collection<Long> userIds, Collection<String> passwords) {
        return createBatch0(userIds, passwords, false);
    }

    @Function
    @Override
    public Boolean encodingCreateBatch(Collection<Long> userIds, Collection<String> rawPasswords) {
        return createBatch0(userIds, rawPasswords, true);
    }

    @Function
    @Override
    public Boolean changePassword(Long userId, String oldRawPassword, String newRawPassword) {
        PamirsPassword password = fetchPassword(userId);
        String encodedPassword = getEncodedPassword(password);
        PasswordEncoder passwordEncoder = Spider.getDefaultExtension(PasswordEncoder.class);
        if (!passwordEncoder.matches(oldRawPassword, encodedPassword)) {
            throw PamirsException.construct(UserExpEnumerate.USER_PASSWORD_NOT_MATCHED_ERROR).errThrow();
        }
        Integer effectRow = Models.origin().updateByWrapper(new PamirsPassword().setPassword(passwordEncoder.encode(newRawPassword)), Pops.<PamirsPassword>lambdaUpdate()
                .from(PamirsPassword.MODEL_MODEL)
                .eq(PamirsPassword::getUserId, userId));
        if (effectRow == 1) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    @Function
    @Override
    public Boolean unsafeChangePassword(Long userId, String newRawPassword) {
        Integer effectRow = Models.origin().updateByWrapper(new PamirsPassword().setPassword(Spider.getDefaultExtension(PasswordEncoder.class).encode(newRawPassword)),
                Pops.<PamirsPassword>lambdaUpdate()
                        .from(PamirsPassword.MODEL_MODEL)
                        .eq(PamirsPassword::getUserId, userId));
        if (effectRow == 1) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    @Function
    @Override
    public Boolean resetPassword(Long userId) {
        return resetPasswords(Collections.singletonList(userId));
    }

    @Function
    @Override
    public Boolean resetPasswords(Collection<Long> userIds) {
        Integer effectRow = Models.origin().updateByWrapper(new PamirsPassword().setPassword(null),
                Pops.<PamirsPassword>lambdaUpdate()
                        .from(PamirsPassword.MODEL_MODEL)
                        .in(PamirsPassword::getUserId, userIds));
        if (effectRow >= 1) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    @Function
    @Override
    public Boolean resetSpecifiedPassword(Long userId, String initialRawPassword) {
        PamirsPassword data = new PamirsPassword()
                .setPassword(null)
                .setInitialPassword(Spider.getDefaultExtension(PasswordEncoder.class).encode(initialRawPassword));
        Integer effectRow = Models.origin().updateByWrapper(data, Pops.<PamirsPassword>lambdaUpdate()
                .from(PamirsPassword.MODEL_MODEL)
                .eq(PamirsPassword::getUserId, userId));
        if (effectRow == 1) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    @Function
    @Override
    public Boolean resetSpecifiedPasswords(Collection<Long> userIds, Collection<String> initialRawPasswords) {
        List<PamirsPassword> passwords = new ArrayList<>();
        Set<Long> repeatSet = new HashSet<>(userIds.size());
        Iterator<Long> userIdIterator = userIds.iterator();
        Iterator<String> initialPasswordIterator = initialRawPasswords.iterator();
        PasswordEncoder passwordEncoder = Spider.getDefaultExtension(PasswordEncoder.class);
        while (userIdIterator.hasNext() && initialPasswordIterator.hasNext()) {
            Long userId = userIdIterator.next();
            String initialPassword = initialPasswordIterator.next();
            if (ObjectHelper.isNotRepeat(repeatSet, userId)) {
                passwords.add(new PamirsPassword().setUserId(userId)
                        .setPassword(null)
                        .setInitialPassword(passwordEncoder.encode(initialPassword)));
            }
        }
        if (userIdIterator.hasNext() || initialPasswordIterator.hasNext()) {
            throw new IllegalArgumentException("User ids and passwords list do not match.");
        }
        Integer effectRow = Models.origin().updateBatch(passwords);
        if (effectRow >= 1) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    @Function
    @Override
    public Boolean checkUserPassword(Long userId, String rawPassword) {
        PamirsPassword userPassword = fetchPassword(userId);
        String encodedPassword = getEncodedPassword(userPassword);
        PasswordEncoder passwordEncoder = Spider.getDefaultExtension(PasswordEncoder.class);
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    @Function
    @Override
    public Boolean isNeedModifyInitialPassword(Long userId) {
        return StringUtils.isBlank(fetchPassword(userId).getPassword());
    }

    @Override
    public Boolean delete(Long userId) {
        Integer effectRow = Models.origin().deleteByWrapper(Pops.<PamirsPassword>lambdaUpdate()
                .from(PamirsPassword.MODEL_MODEL)
                .eq(PamirsPassword::getUserId, userId));
        if (effectRow == 1) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    @Override
    public Boolean deleteBatch(Collection<Long> userIds) {
        Holder<Integer> holder = new Holder<>(0);
        DataShardingHelper.build().collectionSharding(userIds, sublist -> {
            Integer effectRow = Models.origin().deleteByWrapper(Pops.<PamirsPassword>lambdaUpdate()
                    .from(PamirsPassword.MODEL_MODEL)
                    .in(PamirsPassword::getUserId, sublist));
            holder.set(holder.get() + effectRow);
            return Collections.emptyList();
        });
        if (holder.get() >= 1) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    private PamirsPassword fetchPassword(Long userId) {
        PamirsPassword password = Models.origin().queryOneByWrapper(Pops.<PamirsPassword>lambdaQuery()
                .from(PamirsPassword.MODEL_MODEL)
                .eq(PamirsPassword::getUserId, userId));
        if (password == null) {
            throw PamirsException.construct(UserExpEnumerate.USER_PASSWORD_NOT_FOUND_ERROR).errThrow();
        }
        return password;
    }

    private String getEncodedPassword(PamirsPassword password) {
        String encodedPassword = password.getPassword();
        if (StringUtils.isBlank(encodedPassword)) {
            encodedPassword = password.getInitialPassword();
            if (StringUtils.isBlank(encodedPassword)) {
                throw PamirsException.construct(UserExpEnumerate.USER_PASSWORD_NOT_FOUND_ERROR).errThrow();
            }
        }
        return encodedPassword;
    }

    private Boolean create0(Long userId, String password, boolean encoding) {
        if (Models.origin().count(Pops.<PamirsPassword>lambdaQuery()
                .from(PamirsPassword.MODEL_MODEL)
                .eq(PamirsPassword::getUserId, userId)).compareTo(1L) >= 0) {
            return Boolean.FALSE;
        }
        if (StringUtils.isBlank(password)) {
            password = null;
        }
        if (encoding && password != null) {
            password = Spider.getDefaultExtension(PasswordEncoder.class).encode(password);
        }
        PamirsPassword data = new PamirsPassword();
        data.setUserId(userId)
                .setInitialPassword(password);
        Models.origin().createOne(data);
        return Boolean.TRUE;
    }

    private Boolean createBatch0(Collection<Long> userIds, Collection<String> passwords, boolean encoding) {
        Set<Long> existUserIds = DataShardingHelper.build().collectionSharding(userIds, sublist -> Models.origin().queryListByWrapper(Pops.<PamirsPassword>lambdaQuery()
                .from(PamirsPassword.MODEL_MODEL)
                .select(PamirsPassword::getUserId)
                .in(PamirsPassword::getUserId, sublist)))
                .stream()
                .map(PamirsPassword::getUserId)
                .collect(Collectors.toSet());
        Set<Long> createUserIds = Sets.difference(new HashSet<>(userIds), existUserIds);
        if (createUserIds.isEmpty()) {
            return Boolean.FALSE;
        }
        Map<Long, String> createFinderCache = new HashMap<>(userIds.size());
        Iterator<Long> userIdIterator = userIds.iterator();
        Iterator<String> passwordIterator = passwords.iterator();
        List<PamirsPassword> createPasswords = new ArrayList<>();
        PasswordEncoder passwordEncoder = null;
        if (encoding) {
            passwordEncoder = Spider.getDefaultExtension(PasswordEncoder.class);
        }
        for (Long createUserId : createUserIds) {
            String password = findPassword(createFinderCache, userIdIterator, passwordIterator, createUserId);
            if (StringUtils.isNotBlank(password)) {
                if (passwordEncoder != null) {
                    password = passwordEncoder.encode(password);
                }
                PamirsPassword data = new PamirsPassword();
                data.setUserId(createUserId)
                        .setInitialPassword(password);
                createPasswords.add(data);
            }
        }
        if (createPasswords.isEmpty()) {
            return Boolean.FALSE;
        }
        Models.origin().createBatch(createPasswords);
        return Boolean.TRUE;
    }

    private String findPassword(Map<Long, String> createFinderCache, Iterator<Long> userIdIterator, Iterator<String> passwordIterator, Long userId) {
        String password = createFinderCache.get(userId);
        if (password != null) {
            return password;
        }
        while (userIdIterator.hasNext() && passwordIterator.hasNext()) {
            Long targetUserId = userIdIterator.next();
            String targetPassword = passwordIterator.next();
            createFinderCache.put(targetUserId, targetPassword);
            if (userId.equals(targetUserId)) {
                return targetPassword;
            }
        }
        if (userIdIterator.hasNext() || passwordIterator.hasNext()) {
            throw new IllegalArgumentException("User ids and passwords list do not match.");
        }
        return null;
    }
}
