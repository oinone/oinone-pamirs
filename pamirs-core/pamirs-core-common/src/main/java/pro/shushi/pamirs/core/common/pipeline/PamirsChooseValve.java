package pro.shushi.pamirs.core.common.pipeline;

/**
 * 选择阀
 *
 * @param <T> 任意交换对象类
 * @author Adamancy Zhang on 2021-04-26 13:20
 */
public interface PamirsChooseValve<T extends PamirsExchange> extends PamirsValve<T> {

    /**
     * 选择下一个执行的阀
     *
     * @param exchange 交换对象
     * @return 下一个阀
     */
    PamirsValve<T> choose(T exchange);
}
