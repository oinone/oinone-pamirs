package pro.shushi.pamirs.core.common.test.valve;

import pro.shushi.pamirs.core.common.pipeline.PamirsExchange;
import pro.shushi.pamirs.core.common.pipeline.PamirsValve;
import pro.shushi.pamirs.core.common.signature.extension.AbstractPamirsSignature;

/**
 * 测试基础阀
 *
 * @author Adamancy Zhang on 2021-05-09 10:25
 */
public class PamirsValveTest extends AbstractPamirsSignature implements PamirsValve {

    public PamirsValveTest(String signature) {
        super(signature);
    }

    @Override
    public PamirsExchange invoke(PamirsExchange exchange) {
        System.out.println(this.getClass().getName());
        return exchange;
    }
}
