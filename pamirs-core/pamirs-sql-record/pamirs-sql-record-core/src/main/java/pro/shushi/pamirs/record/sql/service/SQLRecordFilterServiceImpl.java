package pro.shushi.pamirs.record.sql.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.middleware.canal.domain.Destination;
import pro.shushi.pamirs.middleware.canal.entity.RefreshFilterEntity;
import pro.shushi.pamirs.middleware.canal.entity.SimpleResult;
import pro.shushi.pamirs.middleware.canal.service.CanalService;
import pro.shushi.pamirs.record.sql.enmu.FilterType;
import pro.shushi.pamirs.record.sql.manager.FilterWatcherManagerFactory;
import pro.shushi.pamirs.record.sql.manager.RecordFilterManager;
import pro.shushi.pamirs.record.sql.model.RecordFilter;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static pro.shushi.pamirs.meta.common.constants.CharacterConstants.*;

/**
 * SQLRecordFilterServiceImpl
 *
 * @author yakir on 2023/06/30 14:25.
 */
@Slf4j
@Component
public class SQLRecordFilterServiceImpl implements CanalService {

    @Autowired
    private RecordFilterManager recordFilterManager;

    @Override
    public String getCurrentFilter(String destination) {
        throw new UnsupportedOperationException("getCurrentFilter");
    }

    @Override
    @Transactional
    public SimpleResult<RefreshFilterEntity> appendFilter(RefreshFilterEntity data) {

        try {
            String destination = data.getDestination();
            String filters = data.getNewFilter();
            List<RecordFilter> filterList = Optional.ofNullable(filters)
                    .filter(StringUtils::isNotBlank)
                    .map(_filters -> _filters.split(SEPARATOR_COMMA))
                    .map(Arrays::stream)
                    .orElse(Stream.empty())
                    .distinct()
                    .map(_filter -> _filter.replace(SEPARATOR_ESCAPE_DOT, SEPARATOR_DOT))
                    .map(_filter -> {
                        RecordFilter recordFilter = new RecordFilter();
                        if (StringUtils.equals(destination, "pamirsBinlog")) {
                            recordFilter.setFilterType(FilterType.BINLOG_EVENT);
                        } else {
                            recordFilter.setFilterType(FilterType.CHANGE_DATA);
                        }
                        recordFilter.setFilter(_filter);
                        log.info("Filter: [{}]", _filter);
                        return recordFilter;
                    })
                    .collect(Collectors.toList());

            recordFilterManager.createOrUpdateBatch(filterList);

            FilterWatcherManagerFactory.getInstance().allRefresh();
            recordFilterManager.refresh();
            return SimpleResult.success(data);
        } catch (Throwable throwable) {
            return SimpleResult.failure(throwable.getMessage());
        }
    }

    @Override
    @Transactional
    public SimpleResult<RefreshFilterEntity> removeFilter(RefreshFilterEntity data) {
        try {
            String destination = data.getDestination();
            String filter = data.getNewFilter();
            RecordFilter recordFilter = new RecordFilter();
            if (StringUtils.equals(destination, "pamirsBinlog")) {
                recordFilter.setFilterType(FilterType.BINLOG_EVENT);
            } else {
                recordFilter.setFilterType(FilterType.CHANGE_DATA);
            }
            recordFilter.setFilter(filter);
            recordFilterManager.delete(recordFilter);
            return SimpleResult.success(data);
        } catch (Throwable throwable) {
            return SimpleResult.failure(throwable.getMessage());
        }
    }

    @Override
    public SimpleResult<RefreshFilterEntity> refreshFilter(RefreshFilterEntity data) {
        throw new UnsupportedOperationException("refreshFilter");
    }

    @Override
    public SimpleResult<Void> addDestination(Destination data) {
        throw new UnsupportedOperationException("addDestination");
    }

    @Override
    public SimpleResult<Void> removeDestination(Destination data) {
        throw new UnsupportedOperationException("removeDestination");
    }
}
