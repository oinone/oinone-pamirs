package pro.shushi.pamirs.meta.api.core.configure.annotation;

import pro.shushi.pamirs.meta.api.CommonApi;
import pro.shushi.pamirs.meta.api.core.compute.Prioritized;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.meta.ExecuteContext;
import pro.shushi.pamirs.meta.api.dto.meta.MetaNames;

import java.util.List;

/**
 * 模型注解配置转换器
 *
 * @param <T> T为元模型类或者元模型类泛型集合类
 * @param <D> D为Java Class、Method等Java meta类型
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:11 下午
 */
public interface ModelConverter<T, D> extends Prioritized, CommonApi {

    /**
     * 元模型注解配置校验
     *
     * @param context 校验上下文
     * @param names   命名空间
     * @param source  配置源
     */
    @SuppressWarnings("rawtypes")
    Result validate(ExecuteContext context, MetaNames names, D source);

    /**
     * 转化方法
     *
     * @param names           命名空间
     * @param source          配置源
     * @param metaModelObject 元模型类对象
     * @return 返回值
     */
    T convert(MetaNames names, D source, T metaModelObject);

    /**
     * 转换器自定义模型签名器
     * <p>
     * 如果实现类没有实现该接口，则使用元模型对应默认的ModelSigner接口
     *
     * @param names  命名空间
     * @param source 配置源
     * @return 返回值
     */
    default String sign(MetaNames names, D source) {
        return null;
    }

    /**
     * 获取元模型
     *
     * @return 元模型全限定类名
     */
    default Class<?> metaModelClazz() {
        return null;
    }

    /**
     * 分组
     *
     * @return 分组
     */
    String group();

    /**
     * 是否是集合转换器
     *
     * @return 是否是集合转换器
     */
    default ConverterType type() {
        return null;
    }

    /**
     * 如果是集合转换器，可以提供指定签名列表获取已存在元数据
     *
     * @param names  命名空间
     * @param source 配置源
     * @return 返回配置源包含的元数据签名列表
     */
    default List<String> signs(MetaNames names, D source) {
        return null;
    }

}
