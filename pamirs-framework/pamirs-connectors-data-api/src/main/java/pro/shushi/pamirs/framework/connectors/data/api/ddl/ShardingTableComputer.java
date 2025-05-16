package pro.shushi.pamirs.framework.connectors.data.api.ddl;

import pro.shushi.pamirs.meta.api.CommonApi;

import java.util.List;

/**
 * 分表计算器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:11 下午
 */
public interface ShardingTableComputer extends CommonApi {

    /**
     * 分库分表计算
     *
     * @param module                   模块
     * @param model                    模型
     * @param shardingTableComputer    分表计算逻辑
     * @param shardingDatabaseComputer 分库计算逻辑
     * @param noShardingTableComputer  非分表计算逻辑（包含分库和非分库分表）
     */
    void computeShardingTable(String module, String model,
                              ShardingComputer shardingTableComputer,
                              ShardingComputer shardingDatabaseComputer,
                              ShardingComputer noShardingTableComputer
    );

    interface ShardingComputer {
        void compute(String dataNode, String dsSeparator, Object tableNode, String tableSeparator);
    }

    /**
     * 计算完整数据源key列表
     *
     * @param logicDsKey 逻辑数据源
     * @param separator  分隔符
     * @param dataNodes  分数据源后缀
     * @return 完整数据源key列表
     */
    List<String> completedDataNodes(String logicDsKey, String separator, List<Object> dataNodes);

    /**
     * 计算完整数据源key
     *
     * @param prefix    逻辑数据源
     * @param separator 分隔符
     * @param dataNode  分数据源后缀
     * @return 完整数据源key
     */
    String completedDataNode(String prefix, String separator, Object dataNode);

}
