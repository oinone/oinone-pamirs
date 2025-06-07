package pro.shushi.pamirs.meta.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * 中间件枚举
 *
 * @author shier
 */
@Dict(dictionary = ModuleMiddlewareTypeEnum.DICTIONARY, displayName = "中间件类型枚举", summary = "中间件类型枚举")
public enum ModuleMiddlewareTypeEnum implements IEnum<String> {

    CACHE("CACHE", "缓存"),
    CONFIGURATION_CENTER("CONFIGURATION_CENTER", "配置中心"),
    MESSAGE_QUEUE("MESSAGE_QUEUE", "消息队列"),
    SEARCH("SEARCH", "搜索"),
    SERVICE_CENTER("SERVICE_CENTER", "服务中心"),
    DATABASE("DATABASE", "关系型数据库"),
    OBJECT_STORAGE("OBJECT_STORAGE", "对象存储"),
    ;

    public static final String DICTIONARY = "base.ModuleMiddlewareTypeEnum";

    private final String value;
    private final String displayName;

    ModuleMiddlewareTypeEnum(String value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }


    @Override
    public String value() {
        return value;
    }

    @Override
    public String displayName() {
        return displayName;
    }

}
