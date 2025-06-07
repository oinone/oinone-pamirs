package pro.shushi.pamirs.core.common.test;

import com.google.common.collect.Sets;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testng.collections.Lists;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Adamancy Zhang at 15:29 on 2024-01-08
 */
@DisplayName("集合操作测试")
public class SetTest {

    @Test
    public void test() {
        Set<Long> set1 = new HashSet<>(Lists.newArrayList(1L, 2L, 3L));
        Set<Long> set2 = new HashSet<>(Lists.newArrayList(2L, 3L, 4L));

        System.out.println(Sets.difference(set1, set2));
        System.out.println(Sets.difference(set2, set1));
    }

    @Test
    public void test2() {
        Map<String, Integer> map = new LinkedHashMap<>();
        map.put("1", 1);
        map.put("4", 4);
        map.put("2", 2);
        map.put("3", 3);
        map.put("5", 5);

//        map.put("4", 3);
//        map.put("3", 4);

        System.out.println(map.keySet());
        System.out.println(map.values());
    }
}
