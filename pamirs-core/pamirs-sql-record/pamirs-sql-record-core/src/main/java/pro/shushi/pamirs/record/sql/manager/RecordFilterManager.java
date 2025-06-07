package pro.shushi.pamirs.record.sql.manager;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.record.sql.enmu.FilterType;
import pro.shushi.pamirs.record.sql.model.RecordFilter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * RecordFilterManager
 *
 * @author yakir on 2023/06/28 20:31.
 */
@Slf4j
@Component
public class RecordFilterManager {

    public static final String BEAN_NAME = "recordFilterManager";

    private static final Map<String, FilterType> FILTER_SET = new ConcurrentHashMap<>();

    private Boolean inited = Boolean.FALSE;

    public void initFilterCache() {
        FILTER_SET.clear();
        List<RecordFilter> filterList = selectByType(null);
        for (RecordFilter filter : filterList) {
            String _filter = filter.getFilter();

            String[] dbTable = StringUtils.split(_filter, ".");
            if (dbTable.length < 2) {
                log.error("过滤表达式异常{}", _filter);
                continue;
            }

            String table = dbTable[1];
            List<String> models = PamirsSession.getContext().getModelsByTable(table);
            if (CollectionUtils.isEmpty(models)) {
                log.error("未通过表名获取到模型{}", table);
                FILTER_SET.put(_filter, filter.getFilterType());
                continue;
            }
            for (String model : models) {
                ModelConfig modelCfg = PamirsSession.getContext().getModelConfig(model);
                if (null == modelCfg) {
                    log.error("未获取到模型{}", model);
                    continue;
                }

                String dsKey = modelCfg.getDsKey();

                String innerFilter = dsKey + "." + table;

                FilterType filterType = FILTER_SET.get(innerFilter);
                if (null == filterType) {
                    FILTER_SET.put(innerFilter, filter.getFilterType());
                } else {
                    if (!filterType.equals(filter.getFilterType())) {
                        FILTER_SET.put(innerFilter, FilterType.ALL);
                    }
                }
            }
        }

        log.info("record filter set: [{}]", FILTER_SET);
        inited = Boolean.TRUE;
    }

    public void refresh() {
        if (inited) {
            initFilterCache();
        }
    }

    public FilterType filter(String dsKey, String table) {
        String filter = dsKey + "." + table;
        return FILTER_SET.get(filter);
    }

    public List<RecordFilter> selectByType(FilterType type) {

        LambdaQueryWrapper<RecordFilter> qw = Pops.<RecordFilter>lambdaQuery()
                .from(RecordFilter.MODEL_MODEL)
                .setBatchSize(-1);

        if (null != type) {
            qw.eq(RecordFilter::getFilterType, type);
        }

        return new RecordFilter().queryList(qw);
    }

    public List<RecordFilter> createOrUpdateBatch(List<RecordFilter> data) {
        Models.data().createOrUpdateBatch(data);
        return data;
    }

    public RecordFilter delete(RecordFilter data) {
        data.deleteByEntity();
        return data;
    }

}
