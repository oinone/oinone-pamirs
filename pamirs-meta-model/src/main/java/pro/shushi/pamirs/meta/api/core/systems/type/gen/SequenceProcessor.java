package pro.shushi.pamirs.meta.api.core.systems.type.gen;

import pro.shushi.pamirs.meta.domain.model.SequenceConfig;

/**
 *
 * 序列 生成器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/23 5:53 下午
 */
public interface SequenceProcessor<T> {

    /**
     * 序列生成
     *
     * @param sequence 序列生成器
     * @param config 序列生成配置
     * @return
     */
    T generate(String sequence, SequenceConfig config);
    
}
