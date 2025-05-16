package pro.shushi.pamirs.core.common.pipeline;

import pro.shushi.pamirs.core.common.signature.PamirsSignature;

/**
 * 基础阀
 *
 * @author Adamancy Zhang on 2021-04-26 13:20
 */
public interface PamirsValve<T extends PamirsExchange> extends PamirsSignature {

    /**
     * 执行
     *
     * @param exchange 交换对象
     * @return 交换对象
     */
    T invoke(T exchange);

    /**
     * <h>阀签名</h>
     * <p>
     * 1、默认使用当前类名作为阀签名<br/>
     * 2、签名作用交由管道自行决定<br/>
     * </p>
     *
     * @return 阀签名
     */
    @Override
    default String signature() {
        return this.getClass().getName();
    }
}
