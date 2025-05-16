package pro.shushi.pamirs.core.common.behavior.impl;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.core.common.behavior.ITreeCodeModel;

import java.util.function.Function;

public class TreeCodeBehavior {

    public static final String EMPTY_CODE = "";

    public static final String SEPARATION_CHARACTER = "#";

    private TreeCodeBehavior() {
        //reject create object
    }

    public static <T extends ITreeCodeModel> String computeTreeCode(T data, T parent) {
        return computeTreeCode(data, parent, ITreeCodeModel::getCode);
    }

    public static <T extends ITreeCodeModel> String computeTreeCode(T data, T parent, Function<T, String> defaultCodeGetter) {
        String treeCode = fetchTreeCode(data, defaultCodeGetter);
        if (EMPTY_CODE.equals(treeCode))
            return EMPTY_CODE;
        return concat(fetchTreeCode(parent, defaultCodeGetter), data.getCode());
    }

    public static <T extends ITreeCodeModel> String fetchTreeCode(T data, Function<T, String> defaultCodeGetter) {
        if (data == null)
            return EMPTY_CODE;
        String treeCode = data.getTreeCode();
        if (StringUtils.isNotBlank(treeCode))
            return treeCode;
        if (defaultCodeGetter != null)
            return defaultCodeGetter.apply(data);
        return EMPTY_CODE;
    }

    public static String concat(String prefix, String suffix) {
        if (StringUtils.isBlank(prefix))
            return suffix;
        return prefix + SEPARATION_CHARACTER + suffix;
    }
}
