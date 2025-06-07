package pro.shushi.pamirs.boot.common.api.contants;

/**
 * 可选项配置组
 * <p>
 * 2020/8/27 5:03 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public enum ProfileEnum {
    AUTO, // 自动安装
    READONLY, // 重启（只读）
    PACKAGE, // 打包
    DDL, // 打印SQL变更
    CUSTOMIZE, // 自定义服务启动可选项
}