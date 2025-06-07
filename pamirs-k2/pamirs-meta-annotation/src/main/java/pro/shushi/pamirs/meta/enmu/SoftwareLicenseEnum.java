package pro.shushi.pamirs.meta.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * 软件版权协议枚举
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Base
@Dict(dictionary = "base.SoftwareLicense", displayName = "软件版权协议类型")
public enum SoftwareLicenseEnum implements IEnum<String> {

    GPL2("GPL2", "GPL-2", "GPL Version 2"),
    GPL2ORLATER("GPL2_OR_LATER", "GPL-2 or any later version", "GPL-2 or any later version"),
    GPL3("GPL3", "GPL-3", "GPL Version 3"),
    GPL3ORLATER("GPL3_OR_LATER", "GPL-3 or any later version", "GPL-3 or any later version"),
    AGPL3("AGPL3", "Affero GPL-3", "Affero GPL-3"),
    LGPL3("LGPL3", "LGPL Version 3", "LGPL Version 3"),
    ORTHEROSI("ORTHEROSI", "Other OSI approved licence", "Other OSI approved licence"),
    PEEL1("PEEL1", "Pamirs Enterprise Edition License v1.0", "Pamirs Enterprise Edition License v1.0"),
    PPL1("PPL1", "Pamirs Proprietary License v1.0", "Pamirs Proprietary License v1.0"),
    ORTHERPROPRIETARY("ORTHERPROPRIETARY", "Other Proprietary", "Other Proprietary");

    private final String value;

    private final String displayName;

    private final String help;

    SoftwareLicenseEnum(String value, String displayName, String help) {
        this.value = value;
        this.displayName = displayName;
        this.help = help;
    }

    @Override
    public String value() {
        return value;
    }

    @Override
    public String displayName() {
        return displayName;
    }

    @Override
    public String help() {
        return help;
    }

}
