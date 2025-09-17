package pro.shushi.pamirs.business.view.action;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.business.api.entity.PamirsCompany;
import pro.shushi.pamirs.business.api.enumeration.BindingModeEnum;
import pro.shushi.pamirs.business.api.model.PamirsEmployee;
import pro.shushi.pamirs.business.api.service.DepartmentRelEmployeeService;
import pro.shushi.pamirs.business.api.service.PamirsEmployeeService;
import pro.shushi.pamirs.core.common.check.UserInfoChecker;
import pro.shushi.pamirs.core.common.function.FunctionConstant;
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
import pro.shushi.pamirs.meta.enmu.ActionContextTypeEnum;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;
import pro.shushi.pamirs.meta.enmu.ViewTypeEnum;
import pro.shushi.pamirs.user.api.model.PamirsUser;
import pro.shushi.pamirs.user.api.service.UserService;
import pro.shushi.pamirs.user.api.spi.UserPatternCheckApi;

import java.util.List;

import static pro.shushi.pamirs.business.api.enumeration.BusinessExpEnumerate.EMPLOYEE_NAME_ERROR;
import static pro.shushi.pamirs.user.api.enmu.UserExpEnumerate.*;

/**
 * {@link PamirsEmployee}动作
 *
 * @author Adamancy Zhang at 12:27 on 2021-08-31
 */
@Slf4j
@Component
@Model.model(PamirsEmployee.MODEL_MODEL)
public class PamirsEmployeeAction {

    @Autowired
    private UserService userService;

    @Autowired
    private PamirsEmployeeService pamirsEmployeeService;

    @Autowired
    private DepartmentRelEmployeeService departmentRelEmployeeService;

    @Function(openLevel = FunctionOpenEnum.API, summary = "员工构造")
    @Function.Advanced(type = FunctionTypeEnum.QUERY)
    public PamirsEmployee construct(PamirsEmployee data) {
        return data.construct();
    }

    @Function(openLevel = FunctionOpenEnum.API, summary = "员工下拉触发")
    @Function.Advanced(type = FunctionTypeEnum.QUERY)
    public PamirsEmployee constructMirror(PamirsEmployee data) {
        return data;
    }

    @Action.Advanced(name = FunctionConstants.create, managed = true, invisible = ExpConstants.idValueExist)
    @Action(displayName = "创建", summary = "添加", bindingType = ViewTypeEnum.FORM, label = "确定")
    @Function(name = FunctionConstants.create)
    @Function.fun(FunctionConstants.create)
    public PamirsEmployee create(PamirsEmployee data) {
        checkName(data);
        if (data.getBindingMode().equals(BindingModeEnum.CREATE_BINDING)) {
            checkParamPattern(data);
            data = pamirsEmployeeService.createEmployeeAndUser(data);
        } else if (data.getBindingMode().equals(BindingModeEnum.BINDING_EXISTING)) {
            checkBindingUser(data);
            data = pamirsEmployeeService.create(data);
        } else {
            throw PamirsException.construct(PARAM_CHECK_ERROR).errThrow();
        }
        return data;
    }

    private void checkBindingUser(PamirsEmployee data) {
        Long bindingUserId = data.getBindingUserId();
        if (bindingUserId == null) {
            throw PamirsException.construct(USER_MODIFY_NOT_EXISTED_ERROR).errThrow();
        }
        PamirsUser pamirsUser = new PamirsUser();
        pamirsUser.setId(bindingUserId);
        if (userService.count(pamirsUser) < 1) {
            throw PamirsException.construct(USER_MODIFY_NOT_EXISTED_ERROR).errThrow();
        }
    }

    public void checkName(PamirsEmployee data) {
        String name = data.getName();
        if (checkBlank(name, true)) return;
        UserPatternCheckApi checkApi = Spider.getLoader(UserPatternCheckApi.class).getDefaultExtension();
        if (!checkApi.checkRealName(name)) {
            log.info("{}，员工名称{}", EMPLOYEE_NAME_ERROR.msg(), name);
            throw PamirsException.construct(EMPLOYEE_NAME_ERROR).errThrow();
        }
    }

    public void checkParamPattern(PamirsEmployee data) {
        if (!UserInfoChecker.checkLogin(data.getLogin())) {
            log.info("{}，登录账号{}", USER_PARAM_LOGIN_ERROR.msg(), data.getLogin());
            throw PamirsException.construct(USER_PARAM_LOGIN_ERROR).errThrow();
        }
        if (!UserInfoChecker.checkName(data.getLogin())) {
            log.info("{}，账号用户是{}", USER_PARAM_NAME_ERROR.msg(), data.getLogin());
            throw PamirsException.construct(USER_PARAM_NAME_ERROR).appendMsg(",员工账号名称，默认使用登录账号").errThrow();
        }
    }

    private static boolean checkBlank(String param, Boolean allowBlank) {
        if (StringUtils.isBlank(param)) {
            if (BooleanUtils.isTrue(allowBlank)) {
                return true;
            }
            throw PamirsException.construct(USER_PARAM_EMPTY_ERROR).errThrow();
        }
        return false;
    }

    @Action.Advanced(name = FunctionConstants.update, managed = true, invisible = ExpConstants.idValueNotExist)
    @Action(displayName = "更新", summary = "修改", bindingType = ViewTypeEnum.FORM, label = "确定")
    @Function(name = FunctionConstants.update)
    @Function.fun(FunctionConstants.update)
    public PamirsEmployee update(PamirsEmployee data) {
        checkName(data);
        data = pamirsEmployeeService.update(data);
        return data;
    }

    @Action.Advanced(name = FunctionConstant.delete, managed = true)
    @Action(displayName = "删除", contextType = ActionContextTypeEnum.SINGLE_AND_BATCH)
    @Function(name = FunctionConstant.delete)
    @Function.fun(FunctionConstant.deleteWithFieldBatch)
    public List<PamirsEmployee> delete(List<PamirsEmployee> list) {
        pamirsEmployeeService.deleteByPks(list);
        return list;
    }

    @Action(displayName = "删除", contextType = ActionContextTypeEnum.SINGLE)
    public PamirsEmployee deleteOne(PamirsEmployee data) {
        pamirsEmployeeService.deleteById(data);
        return data;
    }


    @Function.Advanced(type = FunctionTypeEnum.QUERY, managed = true)
    @Function.fun(FunctionConstants.queryPage)
    @Function(openLevel = {FunctionOpenEnum.LOCAL, FunctionOpenEnum.REMOTE, FunctionOpenEnum.API})
    public Pagination<PamirsEmployee> queryPage(Pagination<PamirsEmployee> page, IWrapper<PamirsEmployee> queryWrapper) {
        return pamirsEmployeeService.queryPageAndFillSupervisor(page, queryWrapper);
    }

    @Function.Advanced(type = FunctionTypeEnum.QUERY, managed = true)
    @Function.fun(FunctionConstants.queryByEntity)
    @Function(openLevel = {FunctionOpenEnum.LOCAL, FunctionOpenEnum.REMOTE, FunctionOpenEnum.API})
    public PamirsEmployee queryOne(PamirsEmployee query) {
        PamirsEmployee pamirsEmployee = pamirsEmployeeService.queryOne(query);
        pamirsEmployee = pamirsEmployee.fieldQuery(PamirsEmployee::getCompany);
        pamirsEmployee = pamirsEmployee.fieldQuery(PamirsEmployee::getCompanyList);
        pamirsEmployee = pamirsEmployee.fieldQuery(PamirsEmployee::getDepartmentList);
        pamirsEmployee = pamirsEmployee.fieldQuery(PamirsEmployee::getRoles);
        pamirsEmployee = pamirsEmployee.fieldQuery(PamirsEmployee::getPositions);
        pamirsEmployee = pamirsEmployee.fieldQuery(PamirsEmployee::getDefaultBindingUser);
        if (null != pamirsEmployee.getDefaultBindingUser()) {
            pamirsEmployee.setLogin(pamirsEmployee.getDefaultBindingUser().getLogin());
            pamirsEmployee.setUserEmail(pamirsEmployee.getDefaultBindingUser().getEmail());
            pamirsEmployee.setBindingMode(BindingModeEnum.BINDING_EXISTING);
        }
        pamirsEmployee = departmentRelEmployeeService.fillDepartmentDataByEmployee(pamirsEmployee);
        return pamirsEmployee;
    }

    @Function.Advanced(type = FunctionTypeEnum.QUERY)
    @Function(openLevel = {FunctionOpenEnum.API})
    public Pagination<PamirsEmployee> queryPageImmediateSupervisor(Pagination<PamirsEmployee> page, IWrapper<PamirsEmployee> queryWrapper) {
        return pamirsEmployeeService.queryPageImmediateSupervisor(page, queryWrapper);
    }

    @Action(displayName = "pc端查询用户公司", summary = "临时用")
    @Action.Advanced(type = FunctionTypeEnum.QUERY)
    @Deprecated
    public String userCompany(PamirsCompany query) {
        Long userId = PamirsSession.getUserId();
        if (null == userId || userId < 1) {
            return "";
        }

        return "";
    }

    @Action(displayName = "根据部门编码查询员工")
    @Action.Advanced(type = FunctionTypeEnum.QUERY)
    public List<PamirsEmployee> queryListByDepartmentCodes(List<String> departmentCodes) {
        return pamirsEmployeeService.queryListByDepartmentCodes(departmentCodes);
    }
}
