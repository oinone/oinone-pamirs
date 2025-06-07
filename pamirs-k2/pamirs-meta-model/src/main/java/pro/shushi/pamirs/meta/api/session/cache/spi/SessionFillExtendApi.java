package pro.shushi.pamirs.meta.api.session.cache.spi;

import pro.shushi.pamirs.meta.api.dto.meta.MetaData;
import pro.shushi.pamirs.meta.api.session.RequestContext;
import pro.shushi.pamirs.meta.base.common.MetaBaseModel;
import pro.shushi.pamirs.meta.common.spi.SPI;

import java.util.List;

/**
 * 填充Session补充操作入口
 *
 * @author wx@shushi.pro
 * @version 1.0.0
 * 2022/8/12
 */
@SPI
public interface SessionFillExtendApi {

    /**
     * 填充内存中所有元数据到二级缓存中(全量)
     *
     * @param context      元数据上下文
     * @param metaDataList 全部元数据
     * @param loadMeta     是否加载元数据
     */
    void fillAllMetaData(RequestContext context, List<MetaData> metaDataList, Boolean loadMeta);

    /**
     * 二级缓存中的元数据更新(更新)
     *
     * @param model    元数据模型
     * @param dataList 元数据
     */
    <T extends MetaBaseModel> void updateMetaData(String model, List<T> dataList);

    /**
     * 二级缓存中的元数据更新(删除)
     *
     * @param model    元数据模型
     * @param dataList 元数据
     */
    <T extends MetaBaseModel> void deleteMetaData(String model, List<T> dataList);

}
