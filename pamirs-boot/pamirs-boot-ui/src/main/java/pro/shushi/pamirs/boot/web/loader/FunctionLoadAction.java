package pro.shushi.pamirs.boot.web.loader;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.web.enmu.BootUxdExpEnumerate;
import pro.shushi.pamirs.boot.web.service.FunctionService;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;

/**
 * @author Adamancy Zhang at 11:58 on 2024-08-19
 */
@Component
@Fun(FunctionDefinition.MODEL_MODEL)
public class FunctionLoadAction {

    @Autowired
    private FunctionService functionService;

    @Base
    @Function(openLevel = FunctionOpenEnum.API)
    @Function.Advanced(type = FunctionTypeEnum.QUERY, displayName = "加载函数")
    public FunctionDefinition load(FunctionDefinition query) {
        String namespace = query.getNamespace();
        if (StringUtils.isBlank(namespace)) {
            throw PamirsException.construct(BootUxdExpEnumerate.FUNCTION_NAMESPACE_IS_NULL_ERROR).errThrow();
        }
        String fun = query.getFun();
        String name = query.getName();
        if (StringUtils.isNotBlank(fun)) {
            return functionService.load(namespace, fun);
        } else if (StringUtils.isNotBlank(name)) {
            return functionService.loadByName(namespace, name);
        }
        return null;
    }
}
