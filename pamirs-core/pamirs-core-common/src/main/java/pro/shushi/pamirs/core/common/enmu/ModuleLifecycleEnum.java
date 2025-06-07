package pro.shushi.pamirs.core.common.enmu;

import pro.shushi.pamirs.core.common.directive.DirectiveEnumeration;

public enum ModuleLifecycleEnum implements DirectiveEnumeration<ModuleLifecycleEnum> {

    INSTALL(1),
    UPGRADE(2),
    RELOAD(4),
    UNINSTALL(8);

    private final int value;

    ModuleLifecycleEnum(int value) {
        this.value = value;
    }

    @Override
    public int intValue() {
        return value;
    }
}