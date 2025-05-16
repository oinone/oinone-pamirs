package pro.shushi.pamirs.core.common.test.valve;

import pro.shushi.pamirs.core.common.pipeline.PamirsExchange;
import pro.shushi.pamirs.core.common.pipeline.PamirsFilterValve;

/**
 * @author Adamancy Zhang on 2021-05-09 12:19
 */
public class PamirsFilterValveTest extends PamirsValveTest implements PamirsFilterValve {

    public static final String KEY = PamirsFilterValveTest.class.getName();

    public PamirsFilterValveTest(String signature) {
        super(signature);
    }

    @Override
    public boolean filter(PamirsExchange exchange) {
        return (boolean) exchange.getProperty(KEY);
    }
}
