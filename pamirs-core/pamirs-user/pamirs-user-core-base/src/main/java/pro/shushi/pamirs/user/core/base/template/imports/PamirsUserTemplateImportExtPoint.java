package pro.shushi.pamirs.user.core.base.template.imports;

import com.alibaba.excel.exception.ExcelAnalysisException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.model.AuthRole;
import pro.shushi.pamirs.core.common.check.UserInfoChecker;
import pro.shushi.pamirs.file.api.context.ExcelImportContext;
import pro.shushi.pamirs.file.api.extpoint.AbstractExcelImportDataExtPointImpl;
import pro.shushi.pamirs.file.api.model.ExcelImportTask;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.annotation.Ext;
import pro.shushi.pamirs.meta.annotation.ExtPoint;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.common.util.PStringUtils;
import pro.shushi.pamirs.user.api.enmu.UserExpEnumerate;
import pro.shushi.pamirs.user.api.model.PamirsUser;
import pro.shushi.pamirs.user.api.service.UserService;
import pro.shushi.pamirs.user.core.base.pmodel.PamirsUserProxy;
import pro.shushi.pamirs.user.core.base.pojo.UserPojo;
import pro.shushi.pamirs.user.core.base.template.PamirsUserTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author: Wuxin
 * @createTime: 2024/06/11 17:49
 */
@Slf4j
@Component
@Ext(ExcelImportTask.class)
public class PamirsUserTemplateImportExtPoint extends AbstractExcelImportDataExtPointImpl<PamirsUserProxy> {
    private static final String SEMICOLON_STRING = ";";
    private static final String INITIAL_PASSWORD = "Abcd@1234";

    @Autowired
    private UserService userService;

    @Override
    @ExtPoint.Implement(expression = "importContext.definitionContext.name==\"" + PamirsUserTemplate.TEMPLATE_NAME + "\"")
    public Boolean importData(ExcelImportContext importContext, PamirsUserProxy userProxy) {
        List<Object> dataBufferList = importContext.getDataBufferList();
        if (CollectionUtils.isEmpty(dataBufferList)) {
            initPamirsUser(dataBufferList);
        }
        UserPojo userPojo = (UserPojo) dataBufferList.get(0);

        //校验用户导入信息
        verifyPamirsUser(userProxy, userPojo);

        calcExcel(userProxy);
        return true;
    }

    private void calcExcel(PamirsUserProxy userProxy) {
        IWrapper<PamirsUser> qw = Pops.<PamirsUser>lambdaQuery().from(PamirsUser.MODEL_MODEL).eq(PamirsUser::getLogin, userProxy.getLogin());
        Long count = new PamirsUser().count(qw);
        if (count > 0) {
            PamirsUser pamirsUser = new PamirsUser().queryOneByWrapper(qw);
            pamirsUser.setName(userProxy.getName());
            pamirsUser.setGender(userProxy.getGender());
            pamirsUser.setLogin(userProxy.getLogin());
            pamirsUser.setActive(userProxy.getActive());
            pamirsUser.setContactPhone(userProxy.getContactPhone());
            pamirsUser.setContactEmail(userProxy.getContactEmail());
            pamirsUser.setRoles(userProxy.getRoles());
            userService.modifyUserInfoExcel(pamirsUser);
        } else {
            if (StringUtils.isEmpty(userProxy.getInitialPassword())) {
                userProxy.setInitialPassword(INITIAL_PASSWORD);
            }
            userService.create(userProxy);
        }
    }


    /**
     * 校验用户导入信息
     *
     * @param userProxy userProxy
     * @param userPojo  userPojo
     */
    private void verifyPamirsUser(PamirsUserProxy userProxy, UserPojo userPojo) {
        Map<String, AuthRole> roleMap = userPojo.getAuthRoleMap();
        if (userProxy == null) {
            return;
        }
        if (userProxy.getLogin() == null || Boolean.FALSE.equals(UserInfoChecker.checkLogin(userProxy.getLogin()))) {
            throw new ExcelAnalysisException(UserExpEnumerate.USER_PARAM_LOGIN_ERROR.msg());
        }
        Boolean active = userProxy.getActive();
        if (active == null) {
            userProxy.setActive(Boolean.TRUE);
        }

        String roleCode = userProxy.getRoleCode();
        if (StringUtils.isNotEmpty(roleCode)) {
            if (roleMap.isEmpty()) {
                throw new ExcelAnalysisException(UserExpEnumerate.INVALID_ROLE_CODE.msg());
            }
            String[] roleCodes = roleCode.split(SEMICOLON_STRING);
            StringBuilder errorCode = new StringBuilder();
            List<AuthRole> authRoles = new ArrayList<>();
            for (String code : roleCodes) {
                AuthRole authRole = roleMap.get(code);
                if (authRole == null) {
                    errorCode.append(code).append(" ");
                } else {
                    authRoles.add(authRole);
                }
            }
            if (StringUtils.isNotEmpty(errorCode)) {
                throw new ExcelAnalysisException(PStringUtils.parse1(UserExpEnumerate.INVALID_ROLE_CODE_EXCEPTION.msg(), errorCode));
            }
            userProxy.setRoles(authRoles);
        }

    }


    /**
     * 参数的默认值与重新计算
     *
     * @param dataBufferList dataBufferList
     */
    private void initPamirsUser(List<Object> dataBufferList) {
        List<AuthRole> authRoles = new AuthRole().queryList();
        Map<String, AuthRole> authRoleMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(authRoles)) {
            authRoleMap = authRoles.stream().collect(Collectors.toMap(AuthRole::getCode, authRole -> authRole));
        }

        UserPojo userPojo = UserPojo.of(authRoleMap);
        dataBufferList.add(0, userPojo);
    }


}