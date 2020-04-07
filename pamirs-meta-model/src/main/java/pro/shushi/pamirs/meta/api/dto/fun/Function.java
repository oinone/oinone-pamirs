package pro.shushi.pamirs.meta.api.dto.fun;

import pro.shushi.pamirs.meta.annotation.fun.Data;
import pro.shushi.pamirs.meta.api.enmu.ScriptType;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.enmu.FunctionUsageEnum;
import pro.shushi.pamirs.meta.enumclass.FunctionSourceEnumCls;

import java.io.Serializable;
import java.util.List;

/**
 * 函数方法
 *
 * @author d
 * @version 2019-04-26
 */
@Data
public class Function implements Serializable {

    private static final long serialVersionUID = 532251020363685453L;

    private ScriptType type;

    /** 技术名称 */
    private String name;

    /** 命名空间 */
    private String namespace;

    /** 函数编码 */
    private String fun;

    /** Java全限定类名 */
    private String clazz;

    /** Java方法名 */
    private String method;

    private List<Arg> arguments;

    private VarType returnType;

    private String codes;

    private FunctionUsageEnum usage;

    private FunctionSourceEnumCls source;

    private String group;

    private String version;

    private Integer timeout;

    private Boolean isLongPolling;

    private String longPollingKey;

    private Integer longPollingTimeout;

    public String fetchDslKey(){
        return namespace + CharacterConstants.SEPARATOR_DOT + fun;
    }

}
