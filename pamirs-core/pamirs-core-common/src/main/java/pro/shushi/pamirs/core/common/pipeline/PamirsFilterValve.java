package pro.shushi.pamirs.core.common.pipeline;

/**
 * 过滤阀
 *
 * @param <T> 任意交换对象类型
 * @author Adamancy Zhang on 2021-04-26 13:20
 */
public interface PamirsFilterValve<T extends PamirsExchange> extends PamirsValve<T> {

    /**
     * 检查是否需要执行
     *
     * @param exchange 交换对象
     * @return 是否过滤
     */
    boolean filter(T exchange);
}
