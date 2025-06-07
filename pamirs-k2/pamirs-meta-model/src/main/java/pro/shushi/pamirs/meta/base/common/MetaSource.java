package pro.shushi.pamirs.meta.base.common;

import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.constant.FieldConstants;
import pro.shushi.pamirs.meta.enmu.SystemSourceEnum;

/**
 * 元数据来源
 * <p>
 * 2020/9/26 11:50 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public interface MetaSource {

    default SystemSourceEnum source() {
        return (SystemSourceEnum) Models.d(this).get(FieldConstants.SOURCE);
    }

}
