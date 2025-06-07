package pro.shushi.pamirs.user.api.service;

import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.user.api.spi.PasswordEncoder;

import java.util.Collection;

/**
 * 用户密码服务
 *
 * @author Adamancy Zhang at 18:33 on 2024-01-12
 */
@Fun(PasswordService.FUN_NAMESPACE)
public interface PasswordService {

    String FUN_NAMESPACE = "user.PamirsPasswordService";

    /**
     * 创建用户密码（需手动加密）
     *
     * @param userId   用户ID
     * @param password 加密后的密码
     * @return 创建是否成功
     */
    @Function
    Boolean create(Long userId, String password);

    /**
     * 创建用户密码（使用{@link PasswordEncoder}加密）
     *
     * @param userId      用户ID
     * @param rawPassword 原始密码
     * @return 创建是否成功
     */
    @Function
    Boolean encodingCreate(Long userId, String rawPassword);

    /**
     * 创建用户密码（需手动加密）
     *
     * @param userIds   用户ID集合
     * @param passwords 加密后的密码集合，与用户ID对应
     * @return 是否创建成功
     */
    @Function
    Boolean createBatch(Collection<Long> userIds, Collection<String> passwords);

    /**
     * 批量创建用户密码（使用{@link PasswordEncoder}加密）
     *
     * @param userIds      用户ID集合
     * @param rawPasswords 原始密码集合，与用户ID对应
     * @return 是否创建成功
     */
    @Function
    Boolean encodingCreateBatch(Collection<Long> userIds, Collection<String> rawPasswords);

    /**
     * 修改密码
     *
     * @param userId         用户ID
     * @param oldRawPassword 旧的原始密码
     * @param newRawPassword 新的原始密码
     * @return 是否修改成功
     */
    @Function
    Boolean changePassword(Long userId, String oldRawPassword, String newRawPassword);

    /**
     * 修改密码（非安全修改）
     *
     * @param userId         用户ID
     * @param newRawPassword 新的原始密码
     * @return 是否修改成功
     */
    @Function
    Boolean unsafeChangePassword(Long userId, String newRawPassword);

    /**
     * 重置用户密码为初始密码
     *
     * @param userId 用户ID
     * @return 是否重置成功
     */
    @Function
    Boolean resetPassword(Long userId);

    /**
     * 批量重置用户密码为初始密码
     *
     * @param userIds 用户ID集合
     * @return 是否重置成功
     */
    @Function
    Boolean resetPasswords(Collection<Long> userIds);

    /**
     * 重置用户密码为指定密码
     *
     * @param userId             用户ID
     * @param initialRawPassword 指定的初始密码（非加密）
     * @return 是否重置成功
     */
    @Function
    Boolean resetSpecifiedPassword(Long userId, String initialRawPassword);

    /**
     * 批量重置用户密码为指定密码
     *
     * @param userIds             用户ID集合
     * @param initialRawPasswords 原始密码集合，与用户ID对应
     * @return 是否重置成功
     */
    @Function
    Boolean resetSpecifiedPasswords(Collection<Long> userIds, Collection<String> initialRawPasswords);

    /**
     * 校验用户密码是否匹配
     *
     * @param userId      用户ID
     * @param rawPassword 原始密码
     * @return 是否匹配
     */
    @Function
    Boolean checkUserPassword(Long userId, String rawPassword);

    /**
     * 是否需要修改初始密码（用于首次修改密码判断）
     *
     * @param userId 用户ID
     * @return 是否需要修改
     */
    @Function
    Boolean isNeedModifyInitialPassword(Long userId);

    /**
     * 删除用户密码
     *
     * @param userId 用户ID
     * @return 是否删除成功
     */
    @Function
    Boolean delete(Long userId);

    /**
     * 批量删除用户密码
     *
     * @param userIds 用户ID集合
     * @return 是否删除成功
     */
    @Function
    Boolean deleteBatch(Collection<Long> userIds);
}
