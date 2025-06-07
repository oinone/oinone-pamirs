package pro.shushi.pamirs.boot.orm.configure;

import pro.shushi.pamirs.meta.annotation.fun.Data;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Data
public class NoCodeModuleConfiguration implements Serializable {

    // 启动是否加载无代码模块. 默认安装. 页面新增无代码模块后,下次启动会被加载
    private Boolean init = Boolean.TRUE;

    // 启动无代码模块列表
    private Set<String> modules = new HashSet<>();
}
