package pro.shushi.pamirs.meta.api.core.configure.yaml.data;

import pro.shushi.pamirs.meta.api.core.configure.yaml.data.model.ColumnInfo;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.common.spi.SPI;

import java.util.List;
import java.util.Set;

/**
 * 扩展数据配置获取接口
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020-01-09 00:22
 */
@SPI
public interface AdditionalLogicColumnFetcher {

    /**
     * 添加额外的逻辑字段
     *
     * @param modelConfig  模型配置
     * @param logicColumns 字段列表
     * @return 逻辑字段
     */
    default Set<String> addLogicColumns(ModelConfig modelConfig, Set<String> logicColumns) {
        return logicColumns;
    }

    /**
     * 添加额外的逻辑字段定义
     *
     * @param modelConfig 模型配置
     * @param columnInfos 字段定义列表
     * @return 逻辑字段
     */
    default List<ColumnInfo> addLogicColumnDefinitions(ModelConfig modelConfig, List<ColumnInfo> columnInfos) {
        return columnInfos;
    }

}
