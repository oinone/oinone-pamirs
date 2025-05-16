package pro.shushi.pamirs.core.common.pipeline;

import pro.shushi.pamirs.core.common.directive.Directive;

import java.util.Comparator;
import java.util.List;

/**
 * 管道
 *
 * @param <T> 任意交换对象类型
 * @author Adamancy Zhang on 2021-04-26 13:20
 */
public interface PamirsPipeline<T extends PamirsExchange> extends PamirsValve<T> {

    /**
     * <h>管道签名</h>
     *
     * @return 管道签名
     */
    @Override
    String signature();

    /**
     * 添加阀
     *
     * @param valve 阀
     */
    void addValve(PamirsValve<T> valve);

    /**
     * 包含其他管道
     *
     * @param pipeline 其他管道
     */
    void include(PamirsPipeline<T> pipeline);

    /**
     * 阀排序
     *
     * @param comparable 比较器
     */
    void sort(Comparator<PamirsValve<T>> comparable);

    /**
     * 获取当前管道中的所有阀，不包含其他管道
     *
     * @return 不可变阀列表 {@link java.util.Collections#unmodifiableList(List)}
     */
    List<PamirsValve<T>> getValves();

    /**
     * 获取所有阀，包含其他管道
     *
     * @return 不可变阀列表 {@link java.util.Collections#unmodifiableList(List)}
     */
    List<PamirsValve<T>> getAllValves();

    /**
     * 管道特性
     */
    enum Feature implements Directive {

        /**
         * 串行
         */
        SERIAL(2 >> 1),

        /**
         * 重复阀校验
         */
        NON_REPEAT_VALVE(2),
        ;

        private final int value;

        Feature(int value) {
            this.value = value;
        }

        @Override
        public int intValue() {
            return value;
        }
    }
}
