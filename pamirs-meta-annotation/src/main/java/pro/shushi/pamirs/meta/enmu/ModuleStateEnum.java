package pro.shushi.pamirs.meta.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * 模块状态枚举
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Base
@Dict(dictionary = "base.ModuleState", displayName = "模块枚举")
public enum ModuleStateEnum implements IEnum<String> {

    UNINSTALLABLE("uninstallable", "不可安装", "不可安装"),
    UNINSTALLED("uninstalled", "未安装", "未安装"),
    TOINSTALL("toinstall", "安装中", "安装中"),
    TOUPGRADE("toupgrade", "升级中", "升级中"),
    TOPREVIEW("topreview", "预览中", "预览中"),
    TOPUBLISH("topublish", "发布中", "发布中"),
    INSTALLED("installed", "已安装", "已安装"),
    TOREMOVE("toremove", "卸载中", "卸载中"),
    TORELOAD("toreload", "重启中", "重启中");

    private String value;

    private String displayName;

    private String help;

    ModuleStateEnum(String  value, String displayName, String help) {
        this.value = value;
        this.displayName = displayName;
        this.help = help;
    }

}
