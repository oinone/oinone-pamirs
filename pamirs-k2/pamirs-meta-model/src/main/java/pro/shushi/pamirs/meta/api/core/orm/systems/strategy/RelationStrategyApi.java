package pro.shushi.pamirs.meta.api.core.orm.systems.strategy;

import java.util.List;
import java.util.function.Consumer;

/**
 * 关系数据提交策略API
 * <p>
 * 2020/7/21 1:47 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public interface RelationStrategyApi {

    /**
     * 判断关联关系数据的提交策略
     *
     * @param references             关联模型
     * @param referenceFields        关联字段
     * @param entity                 数据实体
     * @param insertConsumer         新增逻辑的消费者
     * @param updateConsumer         更新逻辑的消费者
     * @param insertOrUpdateConsumer 新增或更新逻辑的消费者，根据唯一索引判断
     */
    void submit(String references, List<String> referenceFields, Object entity,
                Consumer<Object> insertConsumer, Consumer<Object> updateConsumer,
                Consumer<Object> insertOrUpdateConsumer);

}
