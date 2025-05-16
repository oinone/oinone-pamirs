package pro.shushi.pamirs.boot.web.service;

import pro.shushi.pamirs.boot.base.model.UeModule;

/**
 * 模块服务
 * <p>
 * 2022/9/26 9:46 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public interface ModuleService {

    UeModule loadModule(String module);

}
