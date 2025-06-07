package pro.shushi.pamirs.boot.web.service;

import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;

/**
 * 函数服务
 *
 * @author Adamancy Zhang at 14:02 on 2024-08-19
 */
public interface FunctionService {

    FunctionDefinition load(String namespace, String fun);

    FunctionDefinition loadByName(String namespace, String name);

}
