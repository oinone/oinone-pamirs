package pro.shushi.pamirs.core.common.pipeline;

/**
 * 传输交换对象（双交换体）
 *
 * @author Adamancy Zhang on 2021-05-23 18:57
 */
public interface PamirsTransferExchange extends PamirsExchange {

    /**
     * 获取源交换体
     *
     * @return 交换体
     */
    Object getOriginBody();

    /**
     * 设置新的源交换体
     *
     * @param body 交换体
     */
    void setOriginBody(Object body);

    /**
     * <h>获取目标交换体</h>
     * <p>
     * 同{@link PamirsExchange#getBody()}
     * </p>
     *
     * @return 交换体
     */
    default Object getTargetBody() {
        return getBody();
    }

    /**
     * <h>设置新的目标交换体</h>
     * <p>
     * 同{@link PamirsExchange#setBody(Object)}
     * </p>
     *
     * @param body 交换体
     */
    default void setTargetBody(Object body) {
        setBody(body);
    }
}
