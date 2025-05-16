package pro.shushi.pamirs.core.common.test.valve;

import pro.shushi.pamirs.core.common.pipeline.PamirsChooseValve;
import pro.shushi.pamirs.core.common.pipeline.PamirsExchange;
import pro.shushi.pamirs.core.common.pipeline.PamirsValve;

/**
 * @author Adamancy Zhang on 2021-05-09 12:16
 */
public class PamirsChooseValveTest extends PamirsValveTest implements PamirsChooseValve {

    public static final String KEY = PamirsChooseValveTest.class.getName();

    public PamirsChooseValveTest(String signature) {
        super(signature);
    }

    @Override
    public PamirsValve choose(PamirsExchange exchange) {
        return (PamirsValve) exchange.getProperty(KEY);
    }
}
