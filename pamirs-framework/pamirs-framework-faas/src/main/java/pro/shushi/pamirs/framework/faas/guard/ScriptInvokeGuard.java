package pro.shushi.pamirs.framework.faas.guard;

import pro.shushi.pamirs.framework.faas.computer.LocalComputer;
import pro.shushi.pamirs.framework.faas.enmu.FaasExpEnumerate;
import pro.shushi.pamirs.framework.faas.spi.api.guard.FaasScriptAllowListApi;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.api.session.PamirsRequestSession;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spi.Spider;

import java.util.HashSet;
import java.util.Set;

/**
 * 函数执行守卫
 * <p>
 * 2021/3/5 12:09 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class ScriptInvokeGuard {

    protected static final Set<String> forbiddenNamespaceForDSL;

    static {
        forbiddenNamespaceForDSL = new HashSet<>();
        forbiddenNamespaceForDSL.add(PamirsSession.class.getName());
        forbiddenNamespaceForDSL.add(PamirsRequestSession.class.getName());
        forbiddenNamespaceForDSL.add(LocalComputer.class.getName());
    }

    public static void judgeAllow(Function function) {
        FaasScriptAllowListApi allowListApi = Spider.getDefaultExtension(FaasScriptAllowListApi.class);
        Set<String> classWhiteList = allowListApi.classWhiteList();
        Set<String> classBlackList = allowListApi.classBlackList();
        Set<String> funSignWhiteList = allowListApi.funSignWhiteList();
        Set<String> funSignBlackList = allowListApi.funSignBlackList();
        Set<String> namespaceWhiteList = allowListApi.namespaceWhiteList();
        Set<String> namespaceBlackList = allowListApi.namespaceBlackList();
        if (forbiddenNamespaceForDSL.contains(function.getClazz())
                || whiteForbidden(namespaceWhiteList, function.getNamespace())
                || blackForbidden(namespaceBlackList, function.getNamespace())
                || whiteForbidden(classWhiteList, function.getClazz())
                || blackForbidden(classBlackList, function.getClazz())
                || whiteForbidden(funSignWhiteList, function.getFunctionDefinition().getSign())
                || blackForbidden(funSignBlackList, function.getFunctionDefinition().getSign())
        ) {
            throw PamirsException.construct(FaasExpEnumerate.BASE_DEPLOY_NO_AUTH_TO_CALL_ERROR)
                    .appendMsg("function:" + function.getFunctionDefinition().getSign()).errThrow();
        }
    }

    private static boolean whiteForbidden(Set<String> set, String key) {
        return null != set && null != key && !set.contains(key);
    }

    private static boolean blackForbidden(Set<String> set, String key) {
        return null != set && null != key && set.contains(key);
    }

}
