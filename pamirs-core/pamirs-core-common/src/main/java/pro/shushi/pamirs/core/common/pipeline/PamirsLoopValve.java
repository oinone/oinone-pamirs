package pro.shushi.pamirs.core.common.pipeline;

/**
 * 循环阀
 *
 * @param <T> 任意交换对象类型
 * @author Adamancy Zhang on 2021-04-26 13:20
 */
public interface PamirsLoopValve<T extends PamirsExchange> extends PamirsValve<T> {

    /**
     * 是否需要循环执行
     *
     * @param exchange 交换对象
     * @return 是否循环
     */
    boolean loop(T exchange);
}
