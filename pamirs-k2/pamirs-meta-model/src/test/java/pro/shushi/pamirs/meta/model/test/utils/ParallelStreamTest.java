package pro.shushi.pamirs.meta.model.test.utils;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.alibaba.ttl.threadpool.TtlExecutors;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;
import pro.shushi.pamirs.meta.util.ParallelStreamHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Adamancy Zhang at 13:53 on 2025-02-21
 */
public class ParallelStreamTest {

    @Test
    public void test1() {
        ParallelStreamHelper.parallelStream(generatorData()).forEach(System.out::println);
    }

    @Test
    public void test2() {
        System.setProperty("pamirs.parallel.enabled", "false");
        ParallelStreamHelper.parallelStream(generatorData()).forEach(System.out::println);
    }

    @Test
    public void test3() {
        List<List<String>> list = Lists.newArrayList(
                generatorData(1, 5),
                generatorData(6, 10),
                generatorData(11, 15),
                generatorData(16, 20)
        );
        ParallelStreamHelper.parallelStream(list)
                .flatMap(List::stream)
                .forEach(System.out::println);
    }

    @Test
    public void test4() {
        List<List<String>> list = Lists.newArrayList(
                generatorData(1, 5),
                generatorData(6, 10),
                generatorData(11, 15),
                generatorData(16, 20)
        );
        ParallelStreamHelper.parallelStream(list)
                .flatMap(List::stream)
                .collect(Collectors.toList())
                .forEach(System.out::println);
    }

    private static final ExecutorService executorService;

    static {
        executorService = TtlExecutors.getTtlExecutorService(new ThreadPoolExecutor(
                8, 8,
                0, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>()));
    }

    private static final ThreadLocal<Map<String, String>> threadLocal = new ThreadLocal<>();

    private static final TransmittableThreadLocal<Map<String, String>> transmittableThreadLocal = new TransmittableThreadLocal<>();

    @Test
    public void test5() {
        Map<String, String> data1 = new HashMap<>();
        data1.put("1", "1");
        threadLocal.set(data1);

        Map<String, String> data2 = new HashMap<>();
        data2.put("2", "2");
        transmittableThreadLocal.set(data2);

        printThreadData("main");

        ParallelStreamHelper.parallelStream(generatorData(1000)).forEach(v -> {
            printThreadData("parallelStream1");
        });
        ParallelStreamHelper.parallelStream(generatorData(1000)).forEach(v -> {
            printThreadData("parallelStream2");
        });

        assert executorService != null;
        executorService.execute(() -> {
            printThreadData("executorService");
        });
    }

    private void printThreadData(String name) {
        System.out.println(Thread.currentThread().getName() + " - " + name + ": " + threadLocal.get() + ", " + transmittableThreadLocal.get());
    }

    private List<String> generatorData() {
        return generatorData(1, 1000);
    }

    private List<String> generatorData(int end) {
        return generatorData(1, end);
    }

    private List<String> generatorData(int begin, int end) {
        List<String> data = new ArrayList<>();
        for (int i = begin; i <= end; i++) {
            data.add(String.valueOf(i));
        }
        return data;
    }
}
