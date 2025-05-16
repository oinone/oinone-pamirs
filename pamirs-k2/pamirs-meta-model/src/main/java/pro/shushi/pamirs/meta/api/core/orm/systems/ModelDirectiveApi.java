package pro.shushi.pamirs.meta.api.core.orm.systems;

import org.springframework.core.annotation.Order;
import pro.shushi.pamirs.meta.common.spi.SPI;

/**
 * 模型指令接口
 * <p>
 * 2020/6/30 2:55 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Order
@SPI
public interface ModelDirectiveApi {

    /**
     * 清除配置
     */
    <T> T clear(T listOrObject);

    /**
     * 递归清除配置
     */
    <T> T clearAll(T listOrObject);

    /**
     * 后置清除配置
     */
    default <T> T clearAfterAll(DirectiveApi.DirectiveConsumer<T> consumer, T listOrObject) {
        try {
            return consumer.consume();
        } finally {
            clearAll(listOrObject);
        }
    }

    /**
     * 后置清除配置
     */
    default <T> void clearAfterAllWithoutResult(DirectiveApi.DirectiveVoidConsumer consumer, T listOrObject) {
        try {
            consumer.consume();
        } finally {
            clearAll(listOrObject);
        }
    }

    /**
     * 获取所有指令
     *
     * @return 指令
     */
    <T> Long value(T obj);

    /**
     * 生效持久化ORM字段别名
     */
    @SuppressWarnings("UnusedReturnValue")
    <T> T enableColumn(T listOrObject);

    /**
     * 禁用持久化ORM字段别名，后端操作默认禁用
     */
    @SuppressWarnings("UnusedReturnValue")
    <T> T disableColumn(T listOrObject);

    /**
     * 是否转换持久化ORM字段别名
     */
    <T> boolean isDoColumn(T listOrObject);

    /**
     * 标记重入
     */
    <T> T enableReentry(T listOrObject);

    /**
     * 去掉重入标记
     */
    <T> T disableReentry(T listOrObject);

    /**
     * 是否指令重入执行
     */
    <T> boolean isReentry(T listOrObject);

    /**
     * 标记ORM重入
     */
    <T> T enableOrmReentry(T listOrObject);

    /**
     * 去掉ORM重入标记
     */
    <T> T disableOrmReentry(T listOrObject);

    /**
     * 是否ORM指令重入执行
     */
    <T> boolean isOrmReentry(T listOrObject);

    /**
     * 标记数据为待持久化数据
     */
    <T> T enableDirty(T listOrObject);

    /**
     * 去掉待持久化标记
     */
    <T> T disableDirty(T listOrObject);

    /**
     * 是否待持久化数据
     */
    <T> boolean isDirty(T listOrObject);

    /**
     * 标记数据为元数据已完成计算数据（元数据未变更）
     */
    <T> T enableMetaCompleted(T listOrObject);

    /**
     * 去掉元数据已完成计算（元数据未变更）标记
     */
    <T> T disableMetaCompleted(T listOrObject);

    /**
     * 是否元数据已完成计算数据（元数据未变更）
     */
    <T> boolean isMetaCompleted(T listOrObject);

    /**
     * 标记数据为继承元数据
     */
    <T> T enableMetaInherited(T listOrObject);

    /**
     * 去掉元数据继承标记
     */
    <T> T disableMetaInherited(T listOrObject);

    /**
     * 是否继承元数据
     */
    <T> boolean isMetaInherited(T listOrObject);

    /**
     * 标记数据为元数据差量计算中
     */
    <T> T enableMetaDiffing(T listOrObject);

    /**
     * 去掉元数据差量计算中标记
     */
    <T> T disableMetaDiffing(T listOrObject);

    /**
     * 是否元数据差量计算中
     */
    <T> boolean isMetaDiffing(T listOrObject);

    /**
     * 标记数据为元数据跨模型扩展
     */
    <T> T enableMetaCrossing(T listOrObject);

    /**
     * 去掉元数据跨模型扩展标记
     */
    <T> T disableMetaCrossing(T listOrObject);

    /**
     * 是否元数据跨模型扩展
     */
    <T> boolean isMetaCrossing(T listOrObject);

    /**
     * 标记数据为计算默认值
     */
    <T> T enableDefaultValue(T listOrObject);

    /**
     * 去掉计算默认值标记
     */
    <T> T disableDefaultValue(T listOrObject);

    /**
     * 是否计算默认值
     */
    <T> boolean isDoDefaultValue(T listOrObject);

}
