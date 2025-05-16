package pro.shushi.pamirs.core.common.test;

import org.junit.jupiter.api.Test;
import pro.shushi.pamirs.core.common.DataShardingHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link DataShardingHelper} 测试
 *
 * @author Adamancy Zhang at 17:06 on 2023-12-02
 */
public class DataShardingHelperTest {

    @Test
    public void test1() {
        List<Integer> list = buildList(100);

        List<List<Integer>> shardingList;

        shardingList = DataShardingHelper.build(50).sharding(list);
        assert shardingList.size() == 2;
        assert shardingList.get(0).size() == 50;
        assert shardingList.get(1).size() == 50;

        shardingList = DataShardingHelper.build(30).sharding(list);
        assert shardingList.size() == 4;
        assert shardingList.get(0).size() == 25;
        assert shardingList.get(1).size() == 25;
        assert shardingList.get(2).size() == 25;
        assert shardingList.get(3).size() == 25;

        shardingList = DataShardingHelper.build(40).sharding(list);
        assert shardingList.size() == 3;
        assert shardingList.get(0).size() == 33;
        assert shardingList.get(1).size() == 33;
        assert shardingList.get(2).size() == 34;

        shardingList = DataShardingHelper.build(20).sharding(list);
        assert shardingList.size() == 5;
        assert shardingList.get(0).size() == 20;
        assert shardingList.get(1).size() == 20;
        assert shardingList.get(2).size() == 20;
        assert shardingList.get(3).size() == 20;
        assert shardingList.get(4).size() == 20;

        list = buildList(99);
        shardingList = DataShardingHelper.build(40).sharding(list);
        assert shardingList.size() == 3;
        assert shardingList.get(0).size() == 33;
        assert shardingList.get(1).size() == 33;
        assert shardingList.get(2).size() == 33;

        list = buildList(121);
        shardingList = DataShardingHelper.build(40).sharding(list);
        assert shardingList.size() == 4;
        assert shardingList.get(0).size() == 30;
        assert shardingList.get(1).size() == 30;
        assert shardingList.get(2).size() == 30;
        assert shardingList.get(3).size() == 31;

        list = buildList(39);
        shardingList = DataShardingHelper.build(40).sharding(list);
        assert shardingList.size() == 1;
        assert shardingList.get(0).size() == 39;

        list = buildList(40);
        shardingList = DataShardingHelper.build(40).sharding(list);
        assert shardingList.size() == 1;
        assert shardingList.get(0).size() == 40;

        list = buildList(41);
        shardingList = DataShardingHelper.build(40).sharding(list);
        assert shardingList.size() == 2;
        assert shardingList.get(0).size() == 20;
        assert shardingList.get(1).size() == 21;

        list = buildList(42);
        shardingList = DataShardingHelper.build(40).sharding(list);
        assert shardingList.size() == 2;
        assert shardingList.get(0).size() == 21;
        assert shardingList.get(1).size() == 21;

        list = buildList(159);
        shardingList = DataShardingHelper.build(40).sharding(list);
        assert shardingList.size() == 4;
        // 存在计算偏差
        assert shardingList.get(0).size() == 39;
        assert shardingList.get(1).size() == 39;
        assert shardingList.get(2).size() == 39;
        assert shardingList.get(3).size() == 42;

        list = buildList(160);
        shardingList = DataShardingHelper.build(40).sharding(list);
        assert shardingList.size() == 4;
        assert shardingList.get(0).size() == 40;
        assert shardingList.get(1).size() == 40;
        assert shardingList.get(2).size() == 40;
        assert shardingList.get(3).size() == 40;

        list = buildList(161);
        shardingList = DataShardingHelper.build(40).sharding(list);
        assert shardingList.size() == 5;
        assert shardingList.get(0).size() == 32;
        assert shardingList.get(1).size() == 32;
        assert shardingList.get(2).size() == 32;
        assert shardingList.get(3).size() == 32;
        assert shardingList.get(4).size() == 33;
    }

    @Test
    public void test2() {
        List<Integer> list = buildList(6);

        List<List<Integer>> shardingList;

        shardingList = DataShardingHelper.build().shardGroupMax(8).sharding(list);
        assert shardingList.size() == 6;
        assert shardingList.get(0).size() == 1;
        assert shardingList.get(1).size() == 1;

        list = buildList(100);

        shardingList = DataShardingHelper.build().shardGroupMax(5).sharding(list);
        assert shardingList.size() == 5;
        assert shardingList.get(0).size() == 20;
        assert shardingList.get(1).size() == 20;
        assert shardingList.get(2).size() == 20;
        assert shardingList.get(3).size() == 20;
        assert shardingList.get(4).size() == 20;

        shardingList = DataShardingHelper.build().shardGroupMax(8).sharding(list);
        assert shardingList.size() == 8;
        assert shardingList.get(0).size() == 12;
        assert shardingList.get(1).size() == 12;
        assert shardingList.get(2).size() == 12;
        assert shardingList.get(3).size() == 12;
        assert shardingList.get(4).size() == 12;
        assert shardingList.get(5).size() == 12;
        assert shardingList.get(6).size() == 12;
        assert shardingList.get(7).size() == 16;
    }

    @Test
    public void test3() {
        List<Integer> list = buildList(6);

        List<List<Integer>> shardingList;

        shardingList = DataShardingHelper.build().shardGroupMax(8).eachShardMin(2).sharding(list);
        assert shardingList.size() == 3;
        assert shardingList.get(0).size() == 2;
        assert shardingList.get(1).size() == 2;
        assert shardingList.get(2).size() == 2;

        list = buildList(100);

        shardingList = DataShardingHelper.build().shardGroupMax(8).sharding(list);
        assert shardingList.size() == 8;
        assert shardingList.get(0).size() == 12;
        assert shardingList.get(1).size() == 12;
        assert shardingList.get(2).size() == 12;
        assert shardingList.get(3).size() == 12;
        assert shardingList.get(4).size() == 12;
        assert shardingList.get(5).size() == 12;
        assert shardingList.get(6).size() == 12;
        assert shardingList.get(7).size() == 16;
    }

    private List<Integer> buildList(int count) {
        List<Integer> list = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            list.add(i);
        }
        return list;
    }
}
