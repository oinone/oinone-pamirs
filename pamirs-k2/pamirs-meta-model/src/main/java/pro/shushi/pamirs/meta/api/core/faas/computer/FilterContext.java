package pro.shushi.pamirs.meta.api.core.faas.computer;

import pro.shushi.pamirs.meta.annotation.fun.Data;
import pro.shushi.pamirs.meta.api.enmu.ScriptType;

import java.util.Set;

/**
 * 过滤上下文
 * <p>
 * 2021/1/26 5:16 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Data
public class FilterContext {

    /**
     * 直接指定类型
     */
    private ScriptType hintType;

    /**
     * 排除类型
     */
    private Set<ScriptType> excludeTypes;

}
