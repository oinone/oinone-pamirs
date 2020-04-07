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
public interface SequenceGenerator {

    String generate(SequenceConfig config);
    
}
