package pro.shushi.pamirs.framework.gateways.graph.util;

import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.constant.FunctionConstants;

import java.util.HashSet;
import java.util.Set;

/**
 * 动作工具类
 * <p>
 * 2021/3/24 11:50 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class ActionUtils implements FunctionConstants {

    private static final Set<String> builtActionSet = new HashSet<>(8);

    static {
        builtActionSet.add(create);
        builtActionSet.add(update);
        builtActionSet.add(delete);
    }

    public static boolean isBuiltAction(String namespace, String fun) {
        ModelConfig modelConfig = PamirsSession.getContext().getModelConfig(namespace);
        return null != modelConfig && builtActionSet.contains(fun);
    }

}
