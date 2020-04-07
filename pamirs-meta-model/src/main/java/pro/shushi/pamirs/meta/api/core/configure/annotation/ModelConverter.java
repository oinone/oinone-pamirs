package pro.shushi.pamirs.meta.api.core.configure.annotation;

import pro.shushi.pamirs.meta.api.CommonApi;
import pro.shushi.pamirs.meta.api.core.compute.Prioritized;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.meta.MetaNames;
import pro.shushi.pamirs.meta.api.dto.meta.ExecuteContext;

/**
 * 模型注解配置转换器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:11 下午
 *
 * @param <T> T为元模型类
 * @param <D> D为Java Class、Method等Java meta类型
 *
 */
public interface ModelConverter<T, D> extends Prioritized, CommonApi {

    /**
     * 元模型注解配置校验
     *
     * @param context 校验上下文
     * @param names 命名空间
     * @param source 配置源
     */
    Result validate(ExecuteContext context, MetaNames names, D source);

    /**
     * 转化方法
     *
     * @param names 命名空间
     * @param source 配置源
     * @param metaModelObject 元模型类对象
     * @return
     */
    T convert(MetaNames names, D source, T metaModelObject);

    /**
     * 转换器自定义模型签名器
     *
     * 如果实现类没有实现该接口，则使用元模型对应默认的ModelSigner接口
     *
     * @param names
     * @param source
     * @return
     */
    default String sign(MetaNames names, D source) {
        return null;
    }

}
