package pro.shushi.pamirs.user.core.service;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.shushi.pamirs.auth.api.enumeration.AuthorizationSourceEnum;
import pro.shushi.pamirs.auth.api.model.AuthRole;
import pro.shushi.pamirs.auth.api.model.AuthUserRoleRel;
import pro.shushi.pamirs.auth.api.service.AuthRoleService;
import pro.shushi.pamirs.auth.api.service.manager.AuthUserRoleDiffService;
import pro.shushi.pamirs.auth.api.user.AuthUser;
import pro.shushi.pamirs.core.common.FetchUtil;
import pro.shushi.pamirs.core.common.cache.MemoryListSearchCache;
import pro.shushi.pamirs.core.common.diff.DiffCollection;
import pro.shushi.pamirs.core.common.diff.DiffSet;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.base.AbstractModel;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.base.manager.data.CodeDataManager;
import pro.shushi.pamirs.meta.base.manager.data.IdDataManager;
import pro.shushi.pamirs.meta.base.manager.data.OriginDataManager;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.lambda.Getter;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.resource.api.enmu.UserSignUpType;
import pro.shushi.pamirs.user.api.cache.UserCache;
import pro.shushi.pamirs.user.api.checker.PamirsUserBehaviorChecker;
import pro.shushi.pamirs.user.api.constants.UserConstant;
import pro.shushi.pamirs.user.api.enmu.UserExpEnumerate;
import pro.shushi.pamirs.user.api.enmu.UserSourceEnum;
import pro.shushi.pamirs.user.api.enmu.UserType;
import pro.shushi.pamirs.user.api.login.UserInfoCache;
import pro.shushi.pamirs.user.api.model.PamirsUser;
import pro.shushi.pamirs.user.api.service.PasswordService;
import pro.shushi.pamirs.user.api.service.UserService;
import pro.shushi.pamirs.user.api.spi.UserPatternCheckApi;
import pro.shushi.pamirs.user.api.utils.PamirsUserDataChecker;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;


@Slf4j
@Service
@Fun(UserService.FUN_NAMESPACE)
public class UserServiceImpl implements UserService {

    @Autowired
    private PamirsUserBehaviorChecker userChecker;

    @Autowired
    private AuthUserRoleDiffService authUserRoleDiffService;

    @Autowired
    private AuthRoleService authRoleService;

    @Autowired
    private PasswordService passwordService;

    @Override
    public Integer createOrUpdate(PamirsUser user) {
        paramCheck(user);

        Long id = user.getId();
        String login = user.getLogin();
        boolean isUserExisted = false;
        PamirsUser userQueried = null;

        // 主键和唯一键都为空，直接抛出异常
        if (id == null && StringUtils.isBlank(login)) {
            throw PamirsException.construct(UserExpEnumerate.PARAM_CHECK_ERROR).errThrow();
        }

        // 判断是更新操作还是创建操作
        // 使用 login 进行查询
        if (StringUtils.isNotBlank(login)) {
            IWrapper<PamirsUser> qw = Pops.<PamirsUser>lambdaQuery().from(PamirsUser.MODEL_MODEL).eq(PamirsUser::getLogin, login);
            userQueried = Models.origin().queryOneByWrapper(qw);
            isUserExisted = (userQueried != null);
        }
        // login 未查到用户，使用 id 查询
        if (id != null && userQueried == null) {
            userQueried = (Models.origin().queryByPk(user));
            isUserExisted = userQueried != null;
        }

        // 如果是更新的情况，给 user 的 id 赋值，用于校验和租户的关联关系(防止传入的用户没有传入 id)
        if (userQueried != null) {
            user.setId(userQueried.getId());
        }

        if (isUserExisted) {
            userUpdateCommon(user);
            Models.origin().updateByPk(user);
        } else {
            userCreateCommon(user);
            String password = getUserPassword(user);
            user.unsetInitialPassword().unsetPassword();
            user = Models.origin().createOne(user);
            passwordService.encodingCreate(user.getId(), password);
        }
        //存储用户与角色的关系
        modifyRole(user);
        return 1;
    }


    @Override
    public Integer updateByPk(PamirsUser user) {
        paramCheck(user);
        userUpdateCommon(user);
        return Models.origin().updateByPk(user);
    }

    @Override
    public Integer updateByUnique(PamirsUser user) {
        paramCheck(user);
        userUpdateCommon(user);
        return Models.origin().updateByUniqueField(user);
    }


    @Override
    @Function
    @Transactional
    public PamirsUser create(PamirsUser user) {
        paramCheck(user);

        Spider.getLoader(UserPatternCheckApi.class).getDefaultExtension().userPatternCheck(user);
        //参数的校验
        validateCreateOrUpdate(user, Boolean.FALSE);
        //参数的默认值与重新计算
        computeCreateData(user);
        String password = getUserPassword(user);
        user.unsetInitialPassword().unsetPassword();
        user = Models.origin().createOne(user);
        passwordService.encodingCreate(user.getId(), password);
        modifyRole(user);
        return user;
    }

    @Function
    @Override
    @Transactional
    public PamirsUser update(PamirsUser user) {
        paramCheck(user);
        userUpdateCommon(user);

        Integer modCount = 0;
        user.unsetInitialPassword().unsetPassword();
        if (null != user.getId()) {
            modCount = IdDataManager.getInstance().updateById(user);
        } else {
            modCount = Models.origin().updateByUniqueField(user);
        }

        if (modCount == 0) {
            log.error("更新失败，当前的用户名为: {}", user.getName());
        }

        return user;
    }

    @Override
    public Integer updateById(PamirsUser user) {
        paramCheck(user);
        paramCheck(user.getId());

        userUpdateCommon(user);
        Integer modCount = IdDataManager.getInstance().updateById(user);
        if (modCount == 0) {
            log.error("更新失败，当前的用户名为: {}", user.getName());
        }

        return modCount;
    }

    @Override
    @Function
    public PamirsUser queryById(Long id) {
        if (id == null) {
            return null;
        }
        return new PamirsUser().setId(id).queryById();
    }

    @Override
    @Function
    public List<PamirsUser> queryListByIds(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return null;
        }

        ids = ids.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList());
        return FetchUtil.fetchListByIds(PamirsUser.MODEL_MODEL, ids);
    }

    @Override
    @Function
    public Map<Long, PamirsUser> queryMapByIds(List<Long> ids) {
        List<PamirsUser> list = queryListByIds(ids);
        return list.stream().collect(Collectors.toMap(PamirsUser::getId, a -> a, (a, b) -> a));
    }

    @Override
    @Function
    public PamirsUser queryByLogin(String login) {
        if (StringUtils.isBlank(login)) {
            return null;
        }
        return Models.origin().queryOne(new PamirsUser().setLogin(login));
    }

    @Override
    @Function
    public PamirsUser queryByEmail(String email) {
        if (StringUtils.isBlank(email)) {
            return null;
        }
        return Models.origin().queryOne(new PamirsUser().setEmail(email));
    }

    @Override
    @Function
    public PamirsUser queryByPhone(String phone) {
        if (StringUtils.isBlank(phone)) {
            return null;
        }
        return Models.origin().queryOne(new PamirsUser().setPhone(phone));
    }

    @Override
    @Function
    public PamirsUser queryFullById(Long id) {
        PamirsUser user = queryById(id);
        if (!Objects.isNull(user)) {
            OriginDataManager origin = Models.origin();
            origin.fieldQuery(user, PamirsUser::getAvatarBig);
            origin.fieldQuery(user, PamirsUser::getAvatarMedium);
            origin.fieldQuery(user, PamirsUser::getCountry);
            origin.fieldQuery(user, PamirsUser::getCurrency);
            origin.fieldQuery(user, PamirsUser::getRoles);
            origin.fieldQuery(user, PamirsUser::getThirdParties);
            origin.fieldQuery(user, PamirsUser::getTheme);
        }
        return user;
    }

    @Override
    @Function
    public Boolean enable(Long id) {
        PamirsUser user = new PamirsUser().queryById(id);
        if (null == user) {
            throw PamirsException.construct(UserExpEnumerate.USER_MODIFY_USER_INFO_NO_USER_ERROR).errThrow();
        }

        Integer modCount = IdDataManager.getInstance().updateById((PamirsUser) new PamirsUser().setActive(true).setId(id));
        UserInfoCache.clearUserById(user.getId());
        return modCount > 0;
    }

    @Override
    @Function
    public Boolean disable(Long id) {
        PamirsUser user = new PamirsUser().queryById(id);
        if (null == user) {
            throw PamirsException.construct(UserExpEnumerate.USER_MODIFY_USER_INFO_NO_USER_ERROR).errThrow();
        }

        Integer modCount = IdDataManager.getInstance().updateById((PamirsUser) new PamirsUser().setActive(false).setId(id));
        UserCache.clearSessionByUid(id);
        UserInfoCache.clearUserById(user.getId());
        return modCount > 0;
    }

    @Override
    @Function
    public Boolean delete(Long id) {
        paramCheck(id);
        PamirsUser user = new PamirsUser().queryById(id);
        if (Objects.isNull(user)) {
            throw PamirsException.construct(UserExpEnumerate.USER_MODIFY_USER_INFO_NO_USER_ERROR).errThrow();
        }

        return user.deleteById();
    }


    @Override
    @Function
    public Boolean updateUserInfo(Long id, String name, String email, String avatar) {
        PamirsUser user = new PamirsUser().queryById(id);
        if (Objects.isNull(user)) {
            throw PamirsException.construct(UserExpEnumerate.USER_MODIFY_USER_INFO_NO_USER_ERROR).errThrow();
        }

        if (StringUtils.isNotBlank(name)) {
            user.setName(name);
        }
        if (StringUtils.isNotBlank(email)) {
            user.setEmail(email);
        }
        if (StringUtils.isNotBlank(avatar)) {
            user.setAvatarUrl(avatar);
        }

        update(user);

        return Boolean.TRUE;
    }

    @Override
    @Function
    public PamirsUser modifyUserInfo(PamirsUser user) {
        return getPamirsUser(user, this::userUpdateCommon);
    }

    @Function
    @Override
    public PamirsUser modifyUserInfoAndRoles(PamirsUser user) {
        return getPamirsUser(user, v -> this.userUpdateCommon(v, false));
    }

    private PamirsUser getPamirsUser(PamirsUser user, Consumer<PamirsUser> consumer) {
        paramCheck(user);

        UserPatternCheckApi checkApi = Spider.getLoader(UserPatternCheckApi.class).getDefaultExtension();
        checkApi.checkPhone(user.getPhone(), user.getPhoneCode());
        checkApi.checkEmail(user.getContactEmail());
        checkApi.checkIdCard(user.getIdCard());
        checkApi.checkName(user.getName());
        consumer.accept(user);
        user.unsetInitialPassword().unsetPassword();
        user.fieldSave(PamirsUser::getThirdParties);
        Models.origin().updateByPk(user);
        if (Boolean.FALSE.equals(user.getActive())) {
            UserCache.clearSessionByUid(user.getId());
            UserInfoCache.clearUserById(user.getId());
        }
        return user;
    }

    @Override
    public PamirsUser modifyUserInfoExcel(PamirsUser user) {
        return getPamirsUser(user, pamirsUser -> userUpdateCommon(pamirsUser, false));
    }

    @Function
    @Override
    public Boolean bindUserRole(List<PamirsUser> userList, List<AuthRole> roleList) {
        if (CollectionUtils.isEmpty(userList) || CollectionUtils.isEmpty(roleList)) {
            return Boolean.FALSE;
        }
        Set<Long> userIdList = userList.stream().map(IdModel::getId).collect(Collectors.toSet());
        Set<Long> roleIdList = roleList.stream().map(IdModel::getId).collect(Collectors.toSet());
        if (CollectionUtils.isEmpty(userIdList) || CollectionUtils.isEmpty(roleIdList)) {
            return Boolean.FALSE;
        }

        List<AuthUserRoleRel> userRoleRels = Models.origin().queryListByWrapper(
                Pops.<AuthUserRoleRel>lambdaQuery().from(AuthUserRoleRel.MODEL_MODEL)
                        .in(AuthUserRoleRel::getUserId, userIdList)
                        .in(AuthUserRoleRel::getRoleId, roleIdList)
        );

        MemoryListSearchCache<String, AuthUserRoleRel> userRoleRelCache = new MemoryListSearchCache<>(userRoleRels, this::generatorAuthUserRoleKey);
        List<AuthUserRoleRel> insertEntities = new ArrayList<>();
        for (Long userId : userIdList) {
            for (Long roleId : roleIdList) {
                AuthUserRoleRel userRoleRel = userRoleRelCache.get(generatorAuthUserRoleKey(userId, roleId));
                if (userRoleRel != null) {
                    continue;
                }
                insertEntities.add(new AuthUserRoleRel().setUserId(userId).setRoleId(roleId).setSource(AuthorizationSourceEnum.MANUAL).setActive(Boolean.TRUE));
            }
        }

        if (CollectionUtils.isEmpty(insertEntities)) {
            return Boolean.FALSE;
        }

        //插入绑定关系,刷新缓存
        Models.origin().createBatch(insertEntities);
        List<AuthRole> createRoles = authRoleService.fetchActiveRoles(insertEntities.stream().map(AuthUserRoleRel::getRoleId).collect(Collectors.toSet()));
        if (CollectionUtils.isNotEmpty(createRoles)) {
            Set<Long> createRoleIds = createRoles.stream().map(AuthRole::getId).collect(Collectors.toSet());
            authUserRoleDiffService.refreshUserRoles(userList.stream().map(AuthUser::getId).collect(Collectors.toSet()), DiffCollection.set(null, createRoleIds, null, null));
        }
        return Boolean.TRUE;
    }

    private String generatorAuthUserRoleKey(AuthUserRoleRel data) {
        return generatorAuthUserRoleKey(data.getUserId(), data.getRoleId());
    }

    private String generatorAuthUserRoleKey(Long userId, Long roleId) {
        return userId + CharacterConstants.SEPARATOR_OCTOTHORPE + roleId;
    }

    @Override
    @Function
    public PamirsUser initialPassword(Long id, String initPassword) {
        paramCheck(id, initPassword);

        PamirsUser user = userChecker.checkUserExist(id);
        userChecker.checkInitialPasswordFormat(initPassword);
        passwordService.unsafeChangePassword(id, initPassword);
        UserCache.clearSessionByUid(user.getId());
        return user;
    }

    @Override
    @Function
    public void modifyPassword(Long id, String newPassword) {
        PamirsUser existUserData = userChecker.checkUserExist(id);
        PamirsUserDataChecker.checkPasswordFormat(newPassword, existUserData.getLogin());
        passwordService.unsafeChangePassword(id, newPassword);
        UserCache.clearSessionByUid(id);
    }

    @Override
    public Boolean deleteByPk(Long id) {
        paramCheck(id);
        Boolean result = Models.origin().deleteByPk(new PamirsUser().setId(id));
        passwordService.delete(id);
        return result;
    }

    @Override
    public PamirsUser queryByPk(PamirsUser user) {
        if (user == null || user.getId() == null) {
            return null;
        }
        return Models.origin().queryByPk(user);
    }

    @Override
    public PamirsUser queryOne(PamirsUser user) {
        if (user == null || (user.getId() == null
                && StringUtils.isBlank(user.getLogin())
                && StringUtils.isBlank(user.getEmail())
                && StringUtils.isBlank(user.getPhone()))) {
            return null;
        }
        return Models.origin().queryOne(user);
    }

    @Override
    public List<PamirsUser> deleteWithFieldBatch(List<PamirsUser> pamirsUsers) {
        paramCheck(pamirsUsers);

        return Models.origin().deleteWithFieldBatch(pamirsUsers);
    }

    @Override
    public <T extends AbstractModel> T queryOneByWrapper(IWrapper<T> queryWrapper) {
        if (null == queryWrapper) {
            return null;
        }

        return Models.origin().queryOneByWrapper(queryWrapper);
    }

    @Override
    public Long count(PamirsUser user) {
        if (null == user) {
            return 0L;
        }

        return Models.origin().count(user);
    }

    @Override
    @Function
    public Pagination<PamirsUser> queryPage(Pagination<PamirsUser> page, IWrapper<PamirsUser> queryWrapper) {
        if (page == null || queryWrapper == null) {
            return null;
        }
        return Models.origin().queryPage(page, queryWrapper);
    }

    @Override
    @Function
    public PamirsUser queryByCode(PamirsUser user) {
        if (user == null || StringUtils.isBlank(user.getCode())) {
            return null;
        }

        return CodeDataManager.getInstance().queryByCode(user);
    }

    @Override
    @Function
    public Integer updateByCode(PamirsUser user) {
        if (user == null || StringUtils.isBlank(user.getCode())) {
            return 0;
        }

        userUpdateCommon(user);
        return CodeDataManager.getInstance().updateByCode(user);
    }

    @Override
    @Function
    public Boolean deleteByCode(PamirsUser user) {
        paramCheck(user);
        paramCheck(user.getCode());

        return CodeDataManager.getInstance().deleteByCode(user);
    }

    @Override
    public PamirsUser queryByCode(String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }

        return (PamirsUser) CodeDataManager.getInstance().queryByCode(new PamirsUser().setCode(code));
    }

    @Override
    public Boolean deleteByCode(String code) {
        paramCheck(code);
        return CodeDataManager.getInstance().deleteByCode(new PamirsUser().setCode(code));
    }

    @Override
    public Boolean deleteById(Long id) {
        paramCheck(id);
        return IdDataManager.getInstance().deleteById(new PamirsUser().setId(id));
    }

    @Override
    public Boolean deleteByPk(PamirsUser user) {
        paramCheck(user);
        paramCheck(user.getId());

        return Models.origin().deleteByPk(user);
    }

    /**
     * 校验创建或者更新用户的用户的参数
     *
     * @param user     全量的用户数据
     * @param isModify 是否是更新
     */
    private void validateCreateOrUpdate(PamirsUser user, Boolean isModify) {
        PamirsUser existUser;

        String login = user.getLogin();
        if (isModify) {
            existUser = this.queryById(user.getId());

            // 修改密码的情况，页面上没有传入login情况，无需校验
            if (StringUtils.isNotBlank(user.getLogin())) {
                String originLogin = existUser.getLogin();
                if (!originLogin.equals(user.getLogin())) {
                    throw PamirsException.construct(UserExpEnumerate.USER_UPDATE_INIT_LOGIN_CANT_CHANGE_ERROR).setExtendObject(UserConstant.FIELD_LOGIN).errThrow();
                }
            }
        } else {
            if (StringUtils.isBlank(login)) {
                throw PamirsException.construct(UserExpEnumerate.USER_CREATE_INIT_LOGIN_IS_NOT_NULL_ERROR).setExtendObject(UserConstant.FIELD_LOGIN).errThrow();
            }
            // 过滤掉系统用户（即系统用户的密码修改不受扩展点影响） 8848:eip_system.; 10088L:workflow_system; 10086L:trigger_system
            if (!(user.getId() != null && (8848L == user.getId() || 10086L == user.getId() || 10088L == user.getId()))) {
                if (!PamirsUserDataChecker.checkLogin(login)) {
                    log.error("用户创建校验失败,账号校验失败:{}", login);
                    throw PamirsException.construct(UserExpEnumerate.USER_CREATE_LOGIN_FORMAT_ERROR).setExtendObject(UserConstant.FIELD_LOGIN).errThrow();
                }
            }
        }

        UserPatternCheckApi checkApi = Spider.getLoader(UserPatternCheckApi.class).getDefaultExtension();
        String phone = user.getPhone();
        String phoneCode = user.getPhoneCode();
        if (StringUtils.isNotBlank(phone)) {
            if (!checkApi.checkPhone(phone, phoneCode)) {
                log.error("用户创建校验失败,手机号校验失败:{},用户的账号为{}", phone, login);
                throw PamirsException.construct(UserExpEnumerate.USER_CREATE_PHONE_NOT_FORMAT_ERROR).setExtendObject("phone").errThrow();
            }
            if (isModify) {
                if (existUserInfo(new PamirsUser().setPhone(phone)) && (!queryByPhone(phone).getId().equals(user.getId()))) {
                    log.error("用户更新失败,手机号已经被注册:{},用户的账号为{}", phone, login);
                    throw PamirsException.construct(UserExpEnumerate.USER_CREATE_PHONE_EXISTED_ERROR).setExtendObject("phone").errThrow();
                }
            } else if (existUserInfo(new PamirsUser().setPhone(phone))) {
                log.error("用户创建校验失败,手机号已经被注册:{},用户的账号为{}", phone, login);
                throw PamirsException.construct(UserExpEnumerate.USER_CREATE_PHONE_EXISTED_ERROR).setExtendObject("phone").errThrow();
            }
        }

        String email = user.getEmail();
        if (StringUtils.isNotBlank(email)) {
            if (!checkApi.checkEmail(email)) {
                log.error("用户创建失败，邮箱校验失败{}，用户的账户为{}", email, login);
                throw PamirsException.construct(UserExpEnumerate.USER_CREATE_EMAIL_NOT_FORMAT_ERROR).setExtendObject("email").errThrow();
            }
            if (isModify) {
                if (existUserInfo(new PamirsUser().setEmail(email)) && (!queryByEmail(email).getId().equals(user.getId()))) {
                    log.error("用户更新失败,邮箱已经被注册:{},用户的账号为{}", email, login);
                    throw PamirsException.construct(UserExpEnumerate.USER_CREATE_EMAIL_EXISTED_ERROR).setExtendObject("email").errThrow();
                }
            } else if (existUserInfo(new PamirsUser().setEmail(email))) {
                log.error("用户创建校验失败,邮箱已经被注册:{},用户的账号为{}", email, login);
                throw PamirsException.construct(UserExpEnumerate.USER_CREATE_EMAIL_EXISTED_ERROR).setExtendObject("email").errThrow();
            }
        }

        if (!isModify && existUserInfo(new PamirsUser().setLogin(login))) {
            log.error("用户创建校验失败,账号已经被注册:{},用户的账号为{}", login, login);
            throw PamirsException.construct(UserExpEnumerate.USER_CREATE_LOGIN_EXISTED_ERROR).setExtendObject(UserConstant.FIELD_LOGIN).errThrow();
        }
    }

    /**
     * 使用实体查询用户是否存在
     *
     * @param user 查询实体
     */
    private Boolean existUserInfo(PamirsUser user) {
        return Models.origin().queryListByEntity(user).size() > 0;
    }

    /**
     * 创建用户的时候的默认值（如果没有填写）
     * 默认设置将会把用户的账号作为用户名（name）
     * 默认设置当前时间作为注册时间
     * 默认设置账户为激活状态
     * 默认设置注册方式为后台注册
     *
     * @param user
     */
    private void computeCreateData(PamirsUser user) {
        defaultValue(user, PamirsUser::getName, PamirsUser::setName, user.getLogin());
        defaultValue(user, PamirsUser::getNickname, PamirsUser::setName, user.getName());
        defaultValue(user, PamirsUser::getSignUpType, PamirsUser::setSignUpType, UserSignUpType.BACKSTAGE);
        defaultValue(user, PamirsUser::getActive, PamirsUser::setActive, Boolean.TRUE);
        defaultValue(user, PamirsUser::getRegDate, PamirsUser::setRegDate, new Date());
        defaultValue(user, PamirsUser::getUserType, PamirsUser::setUserType, UserType.MANUAL.name());
        defaultValue(user, PamirsUser::getSource, PamirsUser::setSource, UserSourceEnum.MANUAL);
    }

    private <U, V> void defaultValue(U user, Getter<U, V> getter, BiConsumer<U, V> setter, V defaultValue) {
        V value = getter.apply(user);
        if (null == value) {
            setter.accept(user, defaultValue);
            return;
        }
        if (value instanceof String && StringUtils.isBlank((String) value)) {
            setter.accept(user, defaultValue);
        }
    }

    private void updateUserRole(List<AuthRole> roles, PamirsUser user) {
        Set<Long> roleIds;
        if (CollectionUtils.isEmpty(roles)) {
            roleIds = Collections.emptySet();
        } else {
            roleIds = roles.stream().map(AuthRole::getId).collect(Collectors.toSet());
        }
        List<AuthRole> originRoles;
        if (CollectionUtils.isEmpty(roleIds)) {
            originRoles = Collections.emptyList();
        } else {
            originRoles = authRoleService.fetchRoles(roleIds);
        }
        updateRolesAndRefresh(user, originRoles);
    }

    private void updateRolesAndRefresh(PamirsUser user, List<AuthRole> roles) {
        Long userId = user.getId();
        DiffSet<Long> diffRoles = authUserRoleDiffService.saveRoles(userId, roles);
        if (diffRoles != null) {
            authUserRoleDiffService.refreshUserRoles(userId, diffRoles);
        }
    }

    private String getUserPassword(PamirsUser user) {
        String password = user.getPassword();
        if (StringUtils.isBlank(password)) {
            password = user.getInitialPassword();
        }
        return password;
    }

    /**
     * 参数校验，为空抛出异常
     *
     * @param params 校验参数数组
     */
    private void paramCheck(Object... params) {
        PamirsException exception = PamirsException.construct(UserExpEnumerate.PARAM_CHECK_ERROR).errThrow();
        for (Object param : params) {
            if (param == null) {
                throw exception;
            } else if (param instanceof String) {
                if (StringUtils.isBlank((String) param)) {
                    throw exception;
                }
            } else if (param instanceof Collection) {
                if (CollectionUtils.isEmpty((Collection) param)) {
                    throw exception;
                }
            }
        }
    }

    /**
     * 用户创建的通用逻辑
     */
    private void userCreateCommon(PamirsUser user) {
        validateCreateOrUpdate(user, Boolean.FALSE);
        computeCreateData(user);
    }

    /**
     * 用户更新的通用逻辑
     */
    private void userUpdateCommon(PamirsUser user) {
        userUpdateCommon(user, true);
    }

    /**
     * 用户更新的通用逻辑
     *
     * @param user           用户数据化
     * @param creationMethod 创建用户方式：true 页面创建，false：Excel 导入方式
     */
    private void userUpdateCommon(PamirsUser user, Boolean creationMethod) {
        // 传入的用户无法被唯一标识，直接抛出异常，特殊情况是当用户使用 IWrapper 进行查询，此时 IWrapper 视为可能的唯一标识
        if (user.getId() == null
                && StringUtils.isBlank(user.getLogin())
                && StringUtils.isBlank(user.getPhone())
                && StringUtils.isBlank(user.getEmail())) {
            throw PamirsException.construct(UserExpEnumerate.PARAM_CHECK_ERROR).errThrow();
        }

        // 全量查询用户数据，后续需要使用 id、唯一键 进行校验操作
        PamirsUser userQueried;
        userQueried = this.queryByUniqueField(user);
        if (userQueried == null && user.getId() != null) {
            userQueried = this.queryById(user.getId());
        }
        // 如果通过所有的标识都不能查询到数据库中的用户，说明该用户不存在，无法被更新
        if (userQueried == null) {
            throw PamirsException.construct(UserExpEnumerate.USER_MODIFY_NOT_EXISTED_ERROR).errThrow();
        }
        if (user.getId() == null) {
            user.setId(userQueried.getId());
        }

        // 参数校验
        validateCreateOrUpdate(user, Boolean.TRUE);

        if (creationMethod) {
            if (CollectionUtils.isNotEmpty(user.getRoles())) {
                updateUserRole(user.getRoles(), user);
            }
        } else {
            updateUserRole(user.getRoles(), user);
        }

    }

    // 创建用户的角色信息
    private void modifyRole(PamirsUser user) {
        if (user == null || user.getId() == null || CollectionUtils.isEmpty(user.getRoles())) {
            return;
        }
        Set<Long> roleIds = user.getRoles().stream().map(AuthRole::getId).filter(Objects::nonNull).collect(Collectors.toSet());
        List<AuthRole> originRoles;
        if (CollectionUtils.isEmpty(roleIds)) {
            originRoles = Collections.emptyList();
        } else {
            originRoles = authRoleService.fetchRoles(roleIds);
        }
        updateRolesAndRefresh(user, originRoles);
    }

    /**
     * 通过 唯一键 查询用户
     *
     * @param user 需要查询的用户
     * @return 数据库中存储的用户
     */
    private PamirsUser queryByUniqueField(PamirsUser user) {
        if (user == null) return null;

        IWrapper<PamirsUser> qw;
        String login = user.getLogin();
        String phone = user.getPhone();
        String email = user.getEmail();

        if (StringUtils.isNotBlank(login)) {
            qw = Pops.<PamirsUser>lambdaQuery().from(PamirsUser.MODEL_MODEL).eq(PamirsUser::getLogin, login);
            PamirsUser userQueried = Models.origin().queryOneByWrapper(qw);
            if (userQueried != null) {
                return userQueried;
            }
        }
        if (StringUtils.isNotBlank(phone)) {
            qw = Pops.<PamirsUser>lambdaQuery().from(PamirsUser.MODEL_MODEL).eq(PamirsUser::getPhone, phone);
            PamirsUser userQueried = Models.origin().queryOneByWrapper(qw);
            if (userQueried != null) {
                return userQueried;
            }
        }
        if (StringUtils.isNotBlank(email)) {
            qw = Pops.<PamirsUser>lambdaQuery().from(PamirsUser.MODEL_MODEL).eq(PamirsUser::getEmail, email);
            PamirsUser userQueried = Models.origin().queryOneByWrapper(qw);
            if (userQueried != null) {
                return userQueried;
            }
        }
        return null;
    }
}
