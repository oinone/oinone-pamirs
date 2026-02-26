package pro.shushi.pamirs.user.view.action;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.constant.ExpConstants;
import pro.shushi.pamirs.meta.constant.FunctionConstants;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;
import pro.shushi.pamirs.meta.enmu.ViewTypeEnum;
import pro.shushi.pamirs.user.api.enmu.UserExpEnumerate;
import pro.shushi.pamirs.user.api.enmu.UserSourceEnum;
import pro.shushi.pamirs.user.api.model.PamirsUser;
import pro.shushi.pamirs.user.api.service.PamirsUserConstructor;
import pro.shushi.pamirs.user.api.service.UserService;
import pro.shushi.pamirs.user.api.service.UserSimpleService;
import pro.shushi.pamirs.ux.common.utils.WrapperHelper;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * @author xzf 2023/1/9 14:57
 */
@Slf4j
@Component
@Model.model(PamirsUser.MODEL_MODEL)
public class PamirsUserAction {

    @Autowired
    private UserService userService;

    @Autowired
    private UserSimpleService userSimpleService;

    @Function(openLevel = FunctionOpenEnum.API)
    @Function.Advanced(type = FunctionTypeEnum.QUERY)
    public PamirsUser construct(PamirsUser user) {
        List<PamirsUserConstructor> constructors = Spider.getLoader(PamirsUserConstructor.class).getOrderedExtensions();
        if (CollectionUtils.isEmpty(constructors)) {
            user = user.construct();
        } else {
            user = constructors.get(0).construct(user);
        }
        return user;
    }

    @Action(displayName = "确定", summary = "创建用户", bindingType = ViewTypeEnum.FORM)
    @Action.Advanced(invisible = ExpConstants.idValueExist)
    public PamirsUser create(PamirsUser data) {
        if (data.getEmail() != null && data.getEmail().isEmpty()) {
            data.unsetEmail();
        }
        if (data.getPhone() != null && data.getPhone().isEmpty()) {
            data.unsetPhone();
        }
        if (data.getRegDate() != null) {
            if (data.getRegDate().after(new Date())) {
                throw PamirsException.construct(UserExpEnumerate.USER_PARAM_REG_DATA_ERROR).errThrow();
            }
        }
        if (data.getName() == null) {
            //用户名跟login一致
            data.setName(data.getLogin());
        }
        data = userService.create(data);
        return success(data);
    }

    @Action(displayName = "确定", summary = "修改用户", bindingType = ViewTypeEnum.FORM)
    @Action.Advanced(invisible = ExpConstants.idValueNotExist)
    public PamirsUser update(PamirsUser data) {
        if (data.getRegDate() != null) {
            if (data.getRegDate().after(new Date())) {
                throw PamirsException.construct(UserExpEnumerate.USER_PARAM_REG_DATA_ERROR).errThrow();
            }
        }

        data = userService.update(data);
        return success(data);
    }

    @Action(displayName = "确定", summary = "修改用户", bindingType = ViewTypeEnum.FORM)
    // 默认生成页面屏蔽掉按钮的展示
    @Action.Advanced(invisible = "ture")
    public PamirsUser modifyUserInfo(PamirsUser data) {
        if (data.getRegDate() != null) {
            if (data.getRegDate().after(new Date())) {
                throw PamirsException.construct(UserExpEnumerate.USER_PARAM_REG_DATA_ERROR).errThrow();
            }
        }
        data = userService.modifyUserInfoAndRoles(data);
        return success(data);
    }

    @Action(displayName = "查询用户信息", summary = "查询用户信息")
    @Action.Advanced(invisible = "ture")
    public PamirsUser userInfo() {
        Long userId = PamirsSession.getUserId();
        if (userId == null) return success(new PamirsUser());
        return queryOne((PamirsUser) new PamirsUser().setId(userId));
    }

    @Action(displayName = "激活")
    @Action.Advanced(invisible = "context.activeRecord.active == 'true'")
    public PamirsUser active(PamirsUser user) {
        userService.enable(Optional.ofNullable(user)
                .map(PamirsUser::getId)
                .orElseThrow(() -> PamirsException.construct(UserExpEnumerate.USER_ID_ISNULL).errThrow()));
        return success(user);
    }

    @Action(displayName = "冻结")
    @Action.Advanced(invisible = "context.activeRecord.active == 'false'")
    public PamirsUser unActive(PamirsUser user) {
        userService.disable(Optional.ofNullable(user)
                .map(PamirsUser::getId)
                .orElseThrow(() -> PamirsException.construct(UserExpEnumerate.USER_ID_ISNULL).errThrow()));
        return success(user);
    }

    @Function.Advanced(type = FunctionTypeEnum.QUERY)
    @Function.fun(FunctionConstants.queryByEntity)
    @Function(openLevel = FunctionOpenEnum.API)
    public PamirsUser queryOne(PamirsUser query) {
        PamirsUser user = userService.queryById(query.getId());
        if (user == null) {
            log.error("根据ID：{} 查询用户信息为空", query.getId());
            return null;
        }
        return success(user);
    }

    @Function.Advanced(type = FunctionTypeEnum.QUERY)
    @Function.fun(FunctionConstants.queryPage)
    @Function(openLevel = FunctionOpenEnum.API)
    public Pagination<PamirsUser> queryPage(Pagination<PamirsUser> page, IWrapper<PamirsUser> queryWrapper) {
        LambdaQueryWrapper<PamirsUser> wrapper = WrapperHelper.lambda(queryWrapper)
                .ne(PamirsUser::getSource, UserSourceEnum.BUILD_IN.value());
        Pagination<PamirsUser> pamirsUserPagination = userService.queryPage(page, wrapper);
        pamirsUserPagination.getContent().forEach(this::success);
        return pamirsUserPagination;
    }

    @Function.Advanced(type = FunctionTypeEnum.DELETE)
    @Function.fun(FunctionConstants.deleteWithFieldBatch)
    @Function(openLevel = FunctionOpenEnum.API)
    public List<PamirsUser> deleteWithFieldBatch(List<PamirsUser> dataList) {
        List<PamirsUser> pamirsUsers = userService.deleteWithFieldBatch(dataList);
        pamirsUsers = pamirsUsers.stream().map(this::success).collect(Collectors.toList());
        return pamirsUsers;
    }

    @Action(displayName = "确定", summary = "修改当前用户信息", bindingType = ViewTypeEnum.FORM)
    // 默认生成页面屏蔽掉按钮的展示
    @Action.Advanced(invisible = "ture")
    public PamirsUser updateBasicInfo(PamirsUser user) {
        Long userId = PamirsSession.getUserId();
        user.setId(userId);
        user = userService.update(user);
        return success(user);
    }

    private PamirsUser success(PamirsUser user) {
        return user.setPassword(null).setInitialPassword(null);
    }

}

