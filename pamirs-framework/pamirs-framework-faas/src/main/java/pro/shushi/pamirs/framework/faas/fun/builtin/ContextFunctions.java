package pro.shushi.pamirs.framework.faas.fun.builtin;

import pro.shushi.pamirs.framework.faas.spi.api.fun.ContextFunctionBizApi;
import pro.shushi.pamirs.framework.faas.spi.api.fun.ContextFunctionRoleApi;
import pro.shushi.pamirs.framework.faas.spi.api.fun.ContextFunctionsUserApi;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.constants.NamespaceConstants;
import pro.shushi.pamirs.meta.common.spi.Spider;

import java.util.List;

import static pro.shushi.pamirs.meta.enmu.FunctionCategoryEnum.CONTEXT;
import static pro.shushi.pamirs.meta.enmu.FunctionLanguageEnum.JAVA;
import static pro.shushi.pamirs.meta.enmu.FunctionOpenEnum.LOCAL;
import static pro.shushi.pamirs.meta.enmu.FunctionSceneEnum.EXPRESSION;

/**
 * 上下文函数
 *
 * @version 1.0.0
 */
@SuppressWarnings("rawtypes")
@Fun(NamespaceConstants.expression)
public class ContextFunctions {

    @Function.Advanced(
            displayName = "获取当前用户id", language = JAVA,
            builtin = true, category = CONTEXT
    )
    @Function.fun("CURRENT_UID")
    @Function(name = "CURRENT_UID", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: CURRENT_UID()\n函数说明: 获取当前用户id"
    )
    public static Long currentUserId() {
        return PamirsSession.getUserId();
    }

    @Function.Advanced(
            displayName = "获取当前用户名", language = JAVA,
            builtin = true, category = CONTEXT
    )
    @Function.fun("CURRENT_USER_NAME")
    @Function(name = "CURRENT_USER_NAME", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: CURRENT_USER_NAME()\n函数说明: 获取当前用户的用户名"
    )
    public static String currentUserName() {
        return PamirsSession.getUserName();
    }

    @Function.Advanced(
            displayName = "获取当前用户", language = JAVA,
            builtin = true, category = CONTEXT
    )
    @Function.fun("CURRENT_USER")
    @Function(name = "CURRENT_USER", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: CURRENT_USER()\n函数说明: 获取当前用户"
    )
    public static Object currentUser() {
        return Spider.getDefaultExtension(ContextFunctionsUserApi.class).currentUser();
    }

    @Function.Advanced(
            displayName = "获取当前用户的角色id列表", language = JAVA,
            builtin = true, category = CONTEXT
    )
    @Function.fun("CURRENT_ROLE_IDS")
    @Function(name = "CURRENT_ROLE_IDS", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: CURRENT_ROLE_IDS()\n函数说明: 获取当前用户的角色id列表"
    )
    public static List<Long> currentRoleIds() {
        return Spider.getDefaultExtension(ContextFunctionRoleApi.class).currentRoleIds();
    }

    @Function.Advanced(
            displayName = "获取当前用户的角色列表", language = JAVA,
            builtin = true, category = CONTEXT
    )
    @Function.fun("CURRENT_ROLES")
    @Function(name = "CURRENT_ROLES", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: CURRENT_ROLES()\n函数说明: 获取当前用户的角色列表"
    )
    public static List currentRoles() {
        return Spider.getDefaultExtension(ContextFunctionRoleApi.class).currentRoles();
    }

    @Function.Advanced(
            displayName = "获取当前用户的合作伙伴id", language = JAVA,
            builtin = true, category = CONTEXT
    )
    @Function.fun("CURRENT_PARTNER_ID")
    @Function(name = "CURRENT_PARTNER_ID", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: CURRENT_PARTNER_ID()\n函数说明: 获取当前用户的合作伙伴id"
    )
    public static Long currentPartnerId() {
        return Spider.getDefaultExtension(ContextFunctionBizApi.class).currentPartnerId();
    }

    @Function.Advanced(
            displayName = "获取当前用户的合作伙伴", language = JAVA,
            builtin = true, category = CONTEXT
    )
    @Function.fun("CURRENT_PARTNER")
    @Function(name = "CURRENT_PARTNER", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: CURRENT_PARTNER()\n函数说明: 获取当前用户的合作伙伴"
    )
    public static Object currentPartner() {
        return Spider.getDefaultExtension(ContextFunctionBizApi.class).currentPartner();
    }

    @Function.Advanced(
            displayName = "获取当前用户的部门", language = JAVA,
            builtin = true, category = CONTEXT
    )
    @Function.fun("CURRENT_DEPARTMENT")
    @Function(name = "CURRENT_DEPARTMENT", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: CURRENT_DEPARTMENT()\n函数说明: 获取当前用户的部门"
    )
    public static Object currentUserDepart() {
        return Spider.getDefaultExtension(ContextFunctionBizApi.class).currentUserDepart();
    }

    @Function.Advanced(
            displayName = "获取当前用户部门编码", language = JAVA,
            builtin = true, category = CONTEXT
    )
    @Function.fun("CURRENT_DEPARTMENT_CODE")
    @Function(name = "CURRENT_DEPARTMENT_CODE", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: CURRENT_DEPARTMENT_CODE()\n函数说明: 获取当前用户部门编码"
    )
    public static String currentUserDepartCode() {
        return Spider.getDefaultExtension(ContextFunctionBizApi.class).currentUserDepartCode();
    }

}
