package pro.shushi.pamirs.middleware.schedule.core.serialize;

import com.esotericsoftware.kryo.Kryo;
import pro.shushi.pamirs.meta.common.spi.SPI;

/**
 * KRYO register API
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@SPI
public interface KryoRegisterApi {

    /**
     * 扩展注册方法
     *
     * @param kryo kryo对象
     */
    default void register(Kryo kryo) {

    }

}
