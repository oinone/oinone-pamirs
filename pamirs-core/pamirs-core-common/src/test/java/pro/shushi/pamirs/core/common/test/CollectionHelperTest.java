package pro.shushi.pamirs.core.common.test;

import org.junit.jupiter.api.Test;
import org.testng.collections.Lists;
import pro.shushi.pamirs.core.common.CollectionHelper;

import java.util.List;

/**
 * @author Adamancy Zhang at 16:15 on 2024-06-11
 */
public class CollectionHelperTest {

    @Test
    public void testConnect() {
        List<Integer> list1 = Lists.newArrayList(1, 2, 3);
        List<Integer> list2 = Lists.newArrayList(4, 5);
        System.out.println(CollectionHelper.connect(list1, list2));
    }

    @Test
    public void testSwap() {
        List<Integer> list = Lists.newArrayList(1, 2, 3, 4, 5);
        System.out.println(list);
        CollectionHelper.swap(list, 0, 3);
        System.out.println(list);
    }
}
