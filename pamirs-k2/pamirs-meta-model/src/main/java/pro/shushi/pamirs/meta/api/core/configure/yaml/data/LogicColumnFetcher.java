package pro.shushi.pamirs.meta.api.core.configure.yaml.data;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.locale.utils.I18nUtils;
import pro.shushi.pamirs.meta.api.core.configure.yaml.data.model.ColumnInfo;
import pro.shushi.pamirs.meta.api.core.configure.yaml.data.model.PamirsTableInfo;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.Spider;

import java.util.*;

/**
 * 数据配置获取接口
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020-01-09 00:22
 */
@SPI
public interface LogicColumnFetcher {

    /**
     * 获取非模型定义非逻辑删除的字段
     *
     * @param model 模型编码
     * @return 逻辑表字段
     */
    default Set<String> fetchLogicColumnsWithoutLogicDelete(String model) {
        Set<String> logicColumns = new LinkedHashSet<>();
        // 获取配置信息
        if (StringUtils.isNotBlank(model)) {
            List<AdditionalLogicColumnFetcher> fetchers = Spider.getLoader(AdditionalLogicColumnFetcher.class).getExtensions();
            if (null != fetchers) {
                ModelConfig modelConfig = Optional.ofNullable(PamirsSession.getContext())
                        .map(v -> v.getModelConfig(model)).orElse(null);
                if (null == modelConfig) {
                    return logicColumns;
                }
                for (AdditionalLogicColumnFetcher fetcher : fetchers) {
                    fetcher.addLogicColumns(modelConfig, logicColumns);
                }
            }
        }
        return logicColumns;
    }

    /**
     * 获取非模型定义的字段
     *
     * @param model 模型编码
     * @return 逻辑表字段
     */
    default Set<String> fetchLogicColumns(String model) {
        Set<String> logicColumns = fetchLogicColumnsWithoutLogicDelete(model);
        // 获取配置信息
        if (StringUtils.isNotBlank(model)) {
            // 逻辑删除字段
            PamirsTableInfo pamirsTableInfo = PamirsTableInfo.fetchPamirsTableInfo(model);
            if (null != pamirsTableInfo) {
                if (StringUtils.isNotBlank(model)) {
                    if (pamirsTableInfo.getLogicDelete()) {
                        logicColumns.add(pamirsTableInfo.getLogicDeleteColumn());
                    }
                }
            }
        }
        return logicColumns;
    }

    /**
     * 获取非模型定义的字段定义
     *
     * @param model 模型编码
     * @return 字段列表
     */
    default List<ColumnInfo> fetchLogicColumnDefinitions(String model) {
        List<ColumnInfo> columnInfoList = new ArrayList<>();
        if (StringUtils.isNotBlank(model)) {
            List<AdditionalLogicColumnFetcher> fetchers = Spider.getLoader(AdditionalLogicColumnFetcher.class).getExtensions();
            if (null != fetchers) {
                ModelConfig modelConfig = Optional.ofNullable(PamirsSession.getContext())
                        .map(v -> v.getModelConfig(model)).orElse(null);
                if (null == modelConfig) {
                    return columnInfoList;
                }
                for (AdditionalLogicColumnFetcher fetcher : fetchers) {
                    fetcher.addLogicColumnDefinitions(modelConfig, columnInfoList);
                }
            }
            // 逻辑删除字段
            PamirsTableInfo pamirsTableInfo = PamirsTableInfo.fetchPamirsTableInfo(model);
            if (null != pamirsTableInfo) {
                if (pamirsTableInfo.getLogicDelete()) {
                    ColumnInfo columnInfo = new ColumnInfo();
                    columnInfo.setColumn(pamirsTableInfo.getLogicDeleteColumn());
                    columnInfo.setColumnDefinition("bigint default " + pamirsTableInfo.getLogicNotDeleteValue());
                    columnInfo.setSummary(I18nUtils.getMessage("LogicColumnFetcher.logicDelete"));
                    columnInfoList.add(columnInfo);
                }
            }
        }
        return columnInfoList;
    }

}
