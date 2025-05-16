package pro.shushi.pamirs.framework.connectors.data.sql.spi;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.serializers.JavaSerializer;
import org.springframework.core.annotation.Order;
import pro.shushi.pamirs.framework.common.spi.KryoRegisterApi;
import pro.shushi.pamirs.framework.connectors.data.sql.AbstractWrapper;
import pro.shushi.pamirs.framework.connectors.data.sql.SharedString;
import pro.shushi.pamirs.framework.connectors.data.sql.Wrapper;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.framework.connectors.data.sql.query.QueryWrapper;
import pro.shushi.pamirs.framework.connectors.data.sql.segments.*;
import pro.shushi.pamirs.framework.connectors.data.sql.update.LambdaUpdateWrapper;
import pro.shushi.pamirs.meta.common.spi.SPI;

/**
 * SQL KRYO register API实现
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Order
@SPI.Service("SQL")
public class SqlKryoRegisterApi implements KryoRegisterApi {

    /**
     * kryo.register
     * @param kryo kryo对象
     *  https://www.thinbug.com/q/32861369
     *
     * 【重要】不能随意增加注册类，会因注册顺序问题导致原有的数据反序列化失败
     * 【重要】不能随意增加注册类，会因注册顺序问题导致原有的数据反序列化失败
     * 【重要】不能随意增加注册类，会因注册顺序问题导致原有的数据反序列化失败
     */
    @Override
    public void register(Kryo kryo) {
        //【重要】不能随意增加注册类，后会因注册顺序问题导致原有的数据反序列化失败
        kryo.register(QueryWrapper.class, new JavaSerializer());
        kryo.register(LambdaQueryWrapper.class, new JavaSerializer());
        kryo.register(LambdaUpdateWrapper.class, new JavaSerializer());
        kryo.register(NormalSegmentList.class, new JavaSerializer());
        kryo.register(OrderBySegmentList.class, new JavaSerializer());
        kryo.register(GroupBySegmentList.class, new JavaSerializer());
        kryo.register(HavingSegmentList.class, new JavaSerializer());
    }

}
