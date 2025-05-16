package pro.shushi.pamirs.boot.orm.test.mock.function;

import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.common.constants.NamespaceConstants;

import static pro.shushi.pamirs.meta.enmu.FunctionSceneEnum.SEQUENCE;

/**
 * SidFunctions
 */
@Fun(NamespaceConstants.sequence)
public class SidFunctions {

    @Function(name = "DISTRIBUTION", scene = {SEQUENCE}, summary = "分布式ID")
    @Function.fun("DISTRIBUTION")
    public Long sid() {

        return System.nanoTime();

    }
}
