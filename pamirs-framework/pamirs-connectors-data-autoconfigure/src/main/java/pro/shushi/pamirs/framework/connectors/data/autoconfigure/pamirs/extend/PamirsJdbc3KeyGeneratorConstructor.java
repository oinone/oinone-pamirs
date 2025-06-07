package pro.shushi.pamirs.framework.connectors.data.autoconfigure.pamirs.extend;

import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator;
import pro.shushi.pamirs.meta.util.TypeUtils;

/**
 * Generator 构造器
 * <p>
 * 2020/7/1 4:26 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class PamirsJdbc3KeyGeneratorConstructor {

    public static final Jdbc3KeyGenerator INSTANCE = (Jdbc3KeyGenerator) TypeUtils
            .getNewInstance("pro.shushi.pamirs.framework.connectors.data.plugin.sequence.PamirsJdbc3KeyGenerator");

}
