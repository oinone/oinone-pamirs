package pro.shushi.pamirs.framework.compare.model;

import pro.shushi.pamirs.meta.annotation.fun.Data;
import pro.shushi.pamirs.meta.base.common.MetaBaseModel;

/**
 * 差量模型
 * <p>
 * 2020/11/20 4:41 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Data
public class DiffModel {

    private MetaBaseModel modelObject;

    private String module;

    private String loadModule;

    private String sign;
}
