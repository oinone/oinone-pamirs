package pro.shushi.pamirs.auth.api.helper;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.boot.base.constants.ClientActionConstants;
import pro.shushi.pamirs.boot.base.model.Action;
import pro.shushi.pamirs.boot.base.ux.model.view.UIAction;
import pro.shushi.pamirs.boot.web.loader.path.ResourcePath;
import pro.shushi.pamirs.core.common.TranslateUtils;
import pro.shushi.pamirs.core.common.entry.TopBarAction;
import pro.shushi.pamirs.meta.api.dto.protocol.PamirsRequestVariables;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.constants.VariableNameConstants;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;

import java.util.*;

/**
 * 权限帮助类
 *
 * @author Adamancy Zhang at 11:41 on 2024-01-15
 */
public class AuthHelper {

    /**
     * 白名单模块仅配置模块权限
     */
    private static final List<String> WHITE_MODULE = Arrays.asList(
            "model_designer",
            "ui_designer",
            "print_designer",
            "workflow_designer",
            "data_designer",
            "eip_designer",
            "ai_designer",
            "microflow_designer"
    );

    /**
     * 白名单函数，不校验任何权限
     */
    private static final Map<String, List<String>> WHITE_FUNCTION = new HashMap<>();

    /**
     * 全模型的白名单函数，不校验任何权限
     */
    private static final List<String> WHITE_ALL_FUNCTIONS = Arrays.asList(ClientActionConstants.GoBack.name, ClientActionConstants.GoBack.fun);

    /**
     * 白名单函数，仅校验是否登录
     */
    private static final Map<String, List<String>> WHITE_FUNCTION_WITH_LOGIN = new HashMap<>();

    static {
        WHITE_FUNCTION.put("base.AppConfig", Arrays.asList("queryListByWrapper"));
        WHITE_FUNCTION.put("base.WidgetDefinition", Arrays.asList("loadSDK"));
        WHITE_FUNCTION.put("user.PamirsUserTransient", Arrays.asList(
                // PC
                "login", "logout", "loginVerificationCode", "loginByVerificationCode", "fetchDingTalkRedirectUrl",
                // Mobile
                "loginPlatform", "loginMAPhone", "loginByMA",
                // Sign Up
                "signUpVerificationCode", "signUp", "signUpMobile", "forgetPassword", "sendEmailVerificationCodeForLogin",
                // Others
                "tokenLoginSimple", "sendSmsVerificationCode", "checkVerificationCode"
        ));
        WHITE_FUNCTION.put("resource.ResourceCountry", Arrays.asList("queryPhoneCodes"));
        WHITE_FUNCTION.put("resource.ResourceLang", Arrays.asList("queryLoginLanguage"));

        WHITE_FUNCTION_WITH_LOGIN.put("user.PamirsUserTransient", Arrays.asList("firstResetPassword", "modifyCurrentUserPassword", "modifyMobilePassword"));
        WHITE_FUNCTION_WITH_LOGIN.put("my.MyPamirsUserProxy", Arrays.asList("construct"));
        WHITE_FUNCTION_WITH_LOGIN.put("user.TopBarLangTransientModel", Arrays.asList("activeLang"));
        WHITE_FUNCTION_WITH_LOGIN.put("user.UserListFieldPreferStore", Arrays.asList("save"));
        WHITE_FUNCTION_WITH_LOGIN.put("user.UserQueryPreferStore", Arrays.asList("create", "update", "delete"));
        WHITE_FUNCTION_WITH_LOGIN.put("base.PamirsFile", Arrays.asList("create"));
        WHITE_FUNCTION_WITH_LOGIN.put("base.UeModel", Arrays.asList("loadModelField"));
        WHITE_FUNCTION_WITH_LOGIN.put("base.ViewAction", Arrays.asList("homepage", "load", "home"));
        WHITE_FUNCTION_WITH_LOGIN.put("base.ServerAction", Arrays.asList("load"));
        WHITE_FUNCTION_WITH_LOGIN.put("base.UrlAction", Arrays.asList("load"));
        WHITE_FUNCTION_WITH_LOGIN.put("base.ClientAction", Arrays.asList("load"));
        WHITE_FUNCTION_WITH_LOGIN.put("user.TopBarUserBlock", Arrays.asList("construct"));
        WHITE_FUNCTION_WITH_LOGIN.put("file.ExcelWorkbookDefinition", Arrays.asList("queryByWrapper"));
    }

    private AuthHelper() {
        // reject create object
    }

    public static boolean isModuleInWhite(String module) {
        return WHITE_MODULE.contains(module);
    }

    public static boolean isFunctionInWhite(String namespace, String fun) {
        if (WHITE_ALL_FUNCTIONS.contains(fun)) {
            return true;
        }
        return Optional.ofNullable(WHITE_FUNCTION.get(namespace))
                .map(v -> v.contains(fun))
                .orElse(false);
    }

    public static boolean isFunctionInWhiteOnlyLogin(String namespace, String fun) {
        return Optional.ofNullable(WHITE_FUNCTION_WITH_LOGIN.get(namespace))
                .map(v -> v.contains(fun))
                .orElse(false);
    }

    public static String fetchAuthModule() {
        String moduleName = Optional.ofNullable(PamirsSession.getRequestVariables())
                .map(PamirsRequestVariables::getHeaders)
                .map(v -> v.get(VariableNameConstants.module))
                .orElse(null);
        if (StringUtils.isNotBlank(moduleName)) {
            ModuleDefinition moduleDefinition = PamirsSession.getContext().getModuleCache().getByName(moduleName);
            if (moduleDefinition == null) {
                return null;
            }
            return moduleDefinition.getModule();
        }
        String module = PamirsSession.getServApp();
        if (StringUtils.isNotBlank(module)) {
            ModuleDefinition moduleDefinition = PamirsSession.getContext().getModule(module);
            if (moduleDefinition == null) {
                return null;
            }
            return moduleDefinition.getModule();
        }
        return null;
    }

    public static String getActionDisplayValue(Action action, UIAction actionNode) {
        return Optional.ofNullable(actionNode)
                .map(UIAction::getLabel)
                .filter(StringUtils::isNotBlank)
                .orElseGet(() -> getActionDisplayValue(action));
    }

    public static String getActionDisplayValue(Action action) {
        return Optional.ofNullable(TranslateUtils.translateValues(action.getLabel()))
                .filter(StringUtils::isNotBlank)
                .orElse(action.getDisplayName());
    }

    public static String generatorTopBarActionPath(TopBarAction action) {
        String sessionPath = action.getSessionPath();
        if (sessionPath == null) {
            sessionPath = ResourcePath.generatorPath(action.getModel(), action.getName());
        }
        return sessionPath;
    }
}
