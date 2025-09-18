package pro.shushi.pamirs.draft.api.utils;

import pro.shushi.pamirs.meta.api.dto.fun.Function;

/**
 * 草稿删除调用函数
 *
 * @author Gesi at 11:32 on 2025/9/18
 */
public class DraftDeleteFunction implements AutoCloseable {

    private static final ThreadLocal<Function> FUNCTION = new ThreadLocal<>();

    public DraftDeleteFunction(Function function) {
        FUNCTION.set(function);
    }

    public static Function get() {
        return FUNCTION.get();
    }

    @Override
    public void close() {
        FUNCTION.remove();
    }
}
