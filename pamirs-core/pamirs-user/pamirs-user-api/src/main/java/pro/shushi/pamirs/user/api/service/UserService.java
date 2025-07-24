package pro.shushi.pamirs.user.api.service;

import pro.shushi.pamirs.auth.api.model.AuthRole;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.base.AbstractModel;
import pro.shushi.pamirs.user.api.model.PamirsUser;

import java.util.List;
import java.util.Map;

/**
 * @author Nation
 * @cdate 2020-12-21 11:27
 */
@Fun(UserService.FUN_NAMESPACE)
public interface UserService {

    String FUN_NAMESPACE = "pamirs.user.api.UserService";

    /**
     * 新增
     *
     * @param user 用户
     * @return 用户
     */
    @Function
    PamirsUser create(PamirsUser user);

    /**
     * 新增或者更新用户
     *
     * @param user 用户
     * @return 被修改的记录数
     */
    Integer createOrUpdate(PamirsUser user);

    /**
     * 更新
     *
     * @param user 用户
     * @return 用户
     */
    @Function
    PamirsUser update(PamirsUser user);

    /**
     * 通过主键更新用户
     *
     * @param user 用户
     * @return 被修改的记录数
     */
    Integer updateByPk(PamirsUser user);

    /**
     * 通过唯一键更新用户
     *
     * @param user 用户
     * @return 被修改的记录数
     */
    Integer updateByUnique(PamirsUser user);

    Integer updateById(PamirsUser user);

    /**
     * 根据id查询
     *
     * @param id 账号id
     */
    @Function
    PamirsUser queryById(Long id);

    @Function
    List<PamirsUser> queryListByIds(List<Long> ids);

    @Function
    Map<Long, PamirsUser> queryMapByIds(List<Long> ids);

    @Function
    PamirsUser queryByLogin(String login);

    @Function
    PamirsUser queryByEmail(String email);

    @Function
    PamirsUser queryByPhone(String phone);

    /**
     * 根据id查询详细信息，包含关联对象
     *
     * @param id 账号id
     */
    @Function
    PamirsUser queryFullById(Long id);

    /**
     * 启用账号
     *
     * @param id 账号id
     * @return java.lang.Boolean
     */
    @Function
    Boolean enable(Long id);

    /**
     * 停用账号
     *
     * @param id 账号id
     * @return java.lang.Boolean
     */
    @Function
    Boolean disable(Long id);

    /**
     * 删除
     *
     * @param id 账号id
     * @return java.lang.Boolean
     */
    @Function
    Boolean delete(Long id);

    /**
     * 修改密码
     *
     * @param id          账号id
     * @param newPassword 新密码
     * @return java.lang.Boolean
     */
    @Function
    void modifyPassword(Long id, String newPassword);

    /**
     * 修改个人账号信息
     *
     * @param id     账号id
     * @param name   姓名
     * @param email  邮箱
     * @param avatar 头像
     * @return java.lang.Boolean
     */
    @Function
    Boolean updateUserInfo(Long id, String name, String email, String avatar);

    /**
     * 修改用户基础信息（除了密码以外的数据）
     *
     * @param user
     * @return
     */
    @Function
    PamirsUser modifyUserInfo(PamirsUser user);

    @Function
    PamirsUser modifyUserInfoAndRoles(PamirsUser user);

    /**
     * Excel 批量修改用户基础信息（除了密码以外的数据）
     */
    @Function
    PamirsUser modifyUserInfoExcel(PamirsUser user);

    /**
     * 批量为用户添加角色
     */
    @Function
    Boolean bindUserRole(List<PamirsUser> userList, List<AuthRole> roleList);

    /**
     * 重置用户初始密码
     *
     * @param id
     * @return
     */
    @Function
    PamirsUser initialPassword(Long id, String initPassword);

    /**
     * 分页查询
     */
    @Function
    Pagination<PamirsUser> queryPage(Pagination<PamirsUser> page, IWrapper<PamirsUser> queryWrapper);

    /**
     * 通过 code 查询用户
     */
    @Function
    PamirsUser queryByCode(PamirsUser user);

    PamirsUser queryByCode(String code);

    /**
     * 通过 code 更新用户
     */
    @Function
    Integer updateByCode(PamirsUser user);

    /**
     * 通过 code 删除用户
     */
    @Function
    Boolean deleteByCode(PamirsUser user);

    Boolean deleteByCode(String code);

    @Function
    /**
     * 通过 id 删除用户
     * @param id
     */
    Boolean deleteById(Long id);


    /**
     * 通过主键删除用户
     */
    Boolean deleteByPk(PamirsUser user);

    Boolean deleteByPk(Long pk);

    /**
     * 查询当前用户在数据库中的记录数
     *
     * @return
     */
    Long count(PamirsUser user);

    /**
     * 通过主键查询用户
     */
    PamirsUser queryByPk(PamirsUser user);

    /**
     * 通过对象查询用户
     */
    PamirsUser queryOne(PamirsUser data);

    List<PamirsUser> deleteWithFieldBatch(List<PamirsUser> pamirsUsers);

    <T extends AbstractModel> T queryOneByWrapper(IWrapper<T> queryWrapper);

    @Function
    void checkWorkflowTaskHandover(PamirsUser user);

}
