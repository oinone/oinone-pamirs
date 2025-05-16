package pro.shushi.pamirs.core.common.test.valve;

import pro.shushi.pamirs.core.common.pipeline.PamirsExchange;
import pro.shushi.pamirs.core.common.pipeline.PamirsLoopValve;

/**
 * @author Adamancy Zhang on 2021-05-09 12:19
 */
public class PamirsLoopValveTest extends PamirsValveTest implements PamirsLoopValve {

    public static final String KEY = PamirsLoopValveTest.class.getName();

    public PamirsLoopValveTest(String signature) {
        super(signature);
    }

    @Override
    public boolean loop(PamirsExchange exchange) {
        boolean isLoop = (boolean) exchange.getProperty(KEY);
        if (isLoop) {
            Integer current = (Integer) exchange.getProperty("current");
            if (current == null) {
                current = 1;
            }
            if (current > 3) {
                return false;
            }
            System.out.println("loop valve current=" + current);
            exchange.setProperty("current", current + 1);
        }
        return isLoop;
    }
}
