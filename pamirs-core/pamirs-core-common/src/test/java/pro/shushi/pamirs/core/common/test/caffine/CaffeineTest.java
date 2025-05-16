package pro.shushi.pamirs.core.common.test.caffine;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.junit.jupiter.api.Test;
import pro.shushi.pamirs.meta.common.util.UUIDUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author Adamancy Zhang at 11:09 on 2024-07-17
 */
public class CaffeineTest {

    private static final Cache<String, Object> cache1 = Caffeine.newBuilder().maximumSize(10_000).build();

    private static final Map<String, Object> cache1T = new ConcurrentHashMap<>();

    private static final Cache<String, Object> cache2 = Caffeine.newBuilder().maximumSize(100).expireAfterWrite(3, TimeUnit.SECONDS).build();

    @Test
    public void test1() {
        long start = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            cache1.get(UUIDUtil.getUUIDNumberString(), (k) -> new HashMap<>());
        }
        System.out.println("cost time: " + (System.currentTimeMillis() - start) + "ms");
    }

    @Test
    public void test2() {
        Map<Integer, String> keyMap = new HashMap<>();
        long start = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            String key = keyMap.computeIfAbsent(i % 100, (k) -> UUIDUtil.getUUIDNumberString());
            cache1.get(key, (k) -> new HashMap<>());
        }
        System.out.println("cost time: " + (System.currentTimeMillis() - start) + "ms");
    }

    @Test
    public void test3() {
        String key = UUIDUtil.getUUIDNumberString();
        long start = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            cache1.get(key, (k) -> new HashMap<>());
        }
        System.out.println("cost time: " + (System.currentTimeMillis() - start) + "ms");
    }

    @Test
    public void test4() {
        String key = UUIDUtil.getUUIDNumberString();
        long start = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            cache1T.computeIfAbsent(key, (k) -> new HashMap<>());
        }
        System.out.println("cost time: " + (System.currentTimeMillis() - start) + "ms");
    }
}
