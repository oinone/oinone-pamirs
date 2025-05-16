package pro.shushi.pamirs.sid.functions;

import pro.shushi.pamirs.framework.common.id.UidGeneratorFactory;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.constants.NamespaceConstants;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;

import static pro.shushi.pamirs.meta.enmu.FunctionSceneEnum.SEQUENCE;

/**
 * SidFunctions
 *
 * @author yakir on 2020/04/22 11:30.
 */
@Base
@Fun(NamespaceConstants.sequence)
public class SidFunctions {

    @Function.Advanced(displayName = "获取分布式ID", type = FunctionTypeEnum.QUERY)
    @Function(name = "DISTRIBUTION", scene = {SEQUENCE}, summary = "分布式ID")
    @Function.fun("DISTRIBUTION")
    public Long sid() {
        return UidGeneratorFactory.getCachedUidGenerator().getUID();

    }
}
