package pro.shushi.pamirs.core.common.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pro.shushi.pamirs.core.common.SuperMap;

/**
 * @author Adamancy Zhang at 15:58 on 2022-06-08
 */
@DisplayName("SuperMap测试")
public class SuperMapTest {

    @DisplayName("基础功能测试")
    @Test
    public void base() {
        SuperMap map = new SuperMap();
        map.putIteration("a.a.a", 1);
        map.putIteration("a.a.b", 1);
        map.putIteration("a.b.a", 1);
        map.putIteration("a.b.b", 1);
        map.putIteration("a.c[0].a", 1);
        map.putIteration("a.c[1].b", 2);
        map.putIteration("b.d[0]", 1);
        map.putIteration("b.d[1]", 2);

        assert (Integer) map.getIteration("a.a.a") == 1;
        assert (Integer) map.getIteration("a.a.b") == 1;
        assert (Integer) map.getIteration("a.b.a") == 1;
        assert (Integer) map.getIteration("a.b.b") == 1;
        assert (Integer) map.getIteration("a.c[0].a") == 1;
        assert (Integer) map.getIteration("a.c[1].b") == 2;
        assert (Integer) map.getIteration("b.d[0]") == 1;
        assert (Integer) map.getIteration("b.d[1]") == 2;

        assert (Integer) map.removeIteration("a.a.a") == 1;
        assert (Integer) map.getIteration("a.a.b") == 1;
        SuperMap target = (SuperMap) map.removeIteration("a.c[0]");
        assert target != null && target.size() == 1 && (Integer) target.get("a") == 1;
        target = (SuperMap) map.getIteration("a.c[0]");
        assert target != null && target.size() == 1 && (Integer) target.get("b") == 2;
        assert (Integer) map.removeIteration("b.d[0]") == 1;
        assert (Integer) map.getIteration("b.d[0]") == 2;
    }
}
