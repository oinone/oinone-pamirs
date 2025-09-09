package pro.shushi.pamirs.boot.orm.configure;

import pro.shushi.pamirs.meta.annotation.fun.Data;

import java.util.LinkedHashSet;
import java.util.Set;

@Data
public class NoCodeModuleConfiguration {

    /**
     * 启动是否加载无代码模块. 默认安装. 页面新增无代码模块后,下次启动会被加载
     */
    private Boolean init = true;

    /**
     * 启动无代码模块列表
     */
    private Set<String> modules = new LinkedHashSet<>();

    /**
     * 指定启动无代码模块时是否自动解析无代码模块依赖加入到启动模块列表
     */
    private Boolean resolveDependency = true;
}
