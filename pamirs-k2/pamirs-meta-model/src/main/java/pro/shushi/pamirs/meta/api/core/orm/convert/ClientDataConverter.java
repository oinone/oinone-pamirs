package pro.shushi.pamirs.meta.api.core.orm.convert;

import pro.shushi.pamirs.meta.api.core.orm.template.context.ModelComputeContext;
import pro.shushi.pamirs.meta.common.spi.HoldKeeper;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

/**
 * 数据转换API
 *
 * @author d@shushi.pro
 * @author cpc@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:11 下午
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface ClientDataConverter extends ReentryApi {

    //前端请求
    String CLIENT_TYPE_FRONTEND = "FRONTEND";
    //RPC请求
    String CLIENT_TYPE_RPC = "RPC";

    /**
     * 入转换
     *
     * @param context 上下文
     * @param model   模型编码
     * @param obj     数据对象
     * @return 转换结果
     */
    default <T> T in(ModelComputeContext context, String model, Object obj) {
        return in(context, model, obj, CLIENT_TYPE_FRONTEND);
    }

    /**
     * 出转换
     *
     * @param model 模型编码
     * @param obj   数据对象
     * @return 转换结果
     */
    default <T> T out(String model, Object obj) {
        return out(model, obj, CLIENT_TYPE_FRONTEND);
    }

    /**
     * 入转换
     *
     * @param context    上下文
     * @param model      模型编码
     * @param obj        数据对象
     * @param clientType 请求端的类型
     * @return 转换结果
     */
    default <T> T in(ModelComputeContext context, String model, Object obj, String clientType) {
        return null;
    }

    /**
     * 出转换
     *
     * @param model      模型编码
     * @param obj        数据对象
     * @param clientType 请求端的类型
     * @return 转换结果
     */
    default <T> T out(String model, Object obj, String clientType) {
        return null;
    }

    HoldKeeper<ClientDataConverter> holder = new HoldKeeper<>();

    static ClientDataConverter get() {
        return holder.supply(() -> Spider.getDefaultExtension(ClientDataConverter.class));
    }
}
