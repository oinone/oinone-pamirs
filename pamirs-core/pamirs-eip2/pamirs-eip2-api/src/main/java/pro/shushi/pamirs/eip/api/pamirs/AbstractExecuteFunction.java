package pro.shushi.pamirs.eip.api.pamirs;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.api.Fun;
import pro.shushi.pamirs.meta.api.Models;

public abstract class AbstractExecuteFunction {

    private final String namespace;

    private final String fun;

    public AbstractExecuteFunction(String namespace, String fun) {
        if (StringUtils.isNotBlank(namespace)) {
            this.namespace = namespace;
        } else {
            this.namespace = null;
        }
        if (StringUtils.isNotBlank(fun)) {
            this.fun = fun;
        } else {
            this.fun = null;
        }
    }

    public String getNamespace() {
        return namespace;
    }

    public String getFun() {
        return fun;
    }

    protected Object ignoreHookCall(String namespace, String fun, Object... args) {
        if (namespace != null && fun != null) {
            if (args == null || args.length == 0) {
                return Models.directive().run(() -> Fun.run(Fun.fetch(namespace, fun)));
            } else {
                return Models.directive().run(() -> Fun.run(namespace, fun, args));
            }
        }
        return null;
    }

    protected Object ignoreHookCall(Object... args) {
        return ignoreHookCall(namespace, fun, args);
    }

    protected Object call(String namespace, String fun, Object... args) {
        if (namespace != null && fun != null) {
            if (args == null || args.length == 0) {
                return Fun.run(namespace, fun);
            } else {
                return Fun.run(namespace, fun, args);
            }
        }
        return null;
    }

    protected Object call(Object... args) {
        return call(namespace, fun, args);
    }
}
