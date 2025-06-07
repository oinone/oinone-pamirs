package pro.shushi.pamirs.boot.common.api.contants;

/**
 * 升级指令
 * <p>
 * 2020/8/27 5:03 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public enum UpgradeEnum {
    AUTO, // 自动，模块版本号升高则升级模块
    FORCE, // 强制升级
    READONLY, // 只读
}