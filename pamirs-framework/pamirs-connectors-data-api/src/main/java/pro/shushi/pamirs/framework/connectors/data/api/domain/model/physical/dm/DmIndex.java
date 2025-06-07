package pro.shushi.pamirs.framework.connectors.data.api.domain.model.physical.dm;

import pro.shushi.pamirs.framework.connectors.data.api.domain.model.physical.oracle.OracleIndex;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;
import pro.shushi.pamirs.meta.enmu.SystemSourceEnum;

/**
 * DM索引定义
 *
 * @author Adamancy Zhang at 20:52 on 2023-06-30
 */
@Base(SystemSourceEnum.KERNEL)
@Model.Advanced(type = ModelTypeEnum.TRANSIENT, priority = 53)
@Model.model(DmIndex.MODEL_MODEL)
@Model("DM索引定义")
public class DmIndex extends OracleIndex {

    private static final long serialVersionUID = 562810968814226465L;

    public static final String MODEL_MODEL = "system.DmIndex";
}
