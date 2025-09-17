package pro.shushi.pamirs.draft.api.spi;

import pro.shushi.pamirs.draft.api.model.Draft;
import pro.shushi.pamirs.meta.common.spi.SPI;

/**
 * 草稿上下文API
 *
 * @author Gesi at 13:54 on 2025/9/17
 */
@SPI
public interface DraftContextApi {

    /**
     * 加载草稿上下文
     * @param data 页面数据
     * @return 添加能查询到数据库唯一草稿数据的草稿对象
     * @param <T> 页面模型类型
     */
    <T> Draft<T> loadDraftContext(T data);

    /**
     * 序列化草稿数据
     * @param draft 加载好的草稿对象
     * @param data 页面数据
     * @param <T> 页面模型类型
     */
    <T> void serializationDraftData(Draft<T> draft, T data);

    /**
     * 反序列化草稿数据
     * @param draft 加载好的草稿对象
     * @return 页面数据
     * @param <T> 页面模型类型
     */
    <T> T deserializationDraftData(Draft<T> draft);

}
