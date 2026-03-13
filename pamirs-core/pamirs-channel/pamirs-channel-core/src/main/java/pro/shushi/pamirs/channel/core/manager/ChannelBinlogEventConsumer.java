package pro.shushi.pamirs.channel.core.manager;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.channel.model.ChannelModel;
import pro.shushi.pamirs.framework.connectors.event.annotation.NotifyListener;
import pro.shushi.pamirs.framework.connectors.event.api.NotifyConsumer;
import pro.shushi.pamirs.framework.connectors.event.enumeration.ConsumerType;
import pro.shushi.pamirs.framework.session.tenant.component.PamirsTenantSession;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.configure.yaml.data.model.PamirsTableConfig;
import pro.shushi.pamirs.meta.api.core.configure.yaml.data.model.PamirsTableInfo;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.domain.model.ModelField;
import pro.shushi.pamirs.middleware.canal.EventType;
import pro.shushi.pamirs.middleware.canal.domain.Column;
import pro.shushi.pamirs.middleware.canal.domain.Row;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static pro.shushi.pamirs.channel.enmu.ChannelExpEnumerate.CHANNEL_BINLOG_EVENT_CONSUME_ERROR;

/**
 * ChannelBinlogEventConsumer
 *
 * @author yakir on 2020/04/26 20:35.
 */
@Slf4j
@Component
@NotifyListener(topic = "BINLOG_EVENT_TOPIC", tags = "*", consumerType = ConsumerType.ORDERLY, bodyClass = Row.class)
public class ChannelBinlogEventConsumer implements NotifyConsumer<Row> {

    @Autowired
    private EnhanceModelCacheImpl enhanceModelCacheImpl;
    @Autowired
    private ProcessorOneManager processorOneManager;

    @Override
    public void consume(Message<Row> event) {

        if (null == enhanceModelCacheImpl || null == processorOneManager) {
            log.error("Initialize BinlogEvent consumer failed !!!!!! Model cache Bean is null");
            return;
        }

        Row binlogEvent = event.getPayload();
        Boolean isPreview = Optional.ofNullable(binlogEvent.getEnv()).map(Boolean::valueOf).orElse(false);

        if (isPreview) {
            return;
        }

        Long id = Long.valueOf(String.valueOf(binlogEvent.getId()));
        EventType eventType = binlogEvent.getEventType();
        String schema = binlogEvent.getSchema();
        String table = binlogEvent.getTable();
        String tenant = binlogEvent.getTenant();
        log.debug("{} {} {} {} {}", schema, table, id, eventType, binlogEvent);

        if (null != tenant) {
            PamirsTenantSession.setTenant(tenant);
        }

        List<Column> binlogEventAfter = binlogEvent.getAfter();
        Map<String, Column> binlogEventAfterMap = Optional.ofNullable(binlogEventAfter)
                .map(List::stream)
                .orElse(Stream.empty())
                .collect(Collectors.toMap(Column::getName, Function.identity(), (a, b) -> a));

        // 缓存 ModelConfig
        Set<String> modelList = enhanceModelCacheImpl.table2Model(table);
        for (String model : modelList) {
            ChannelModel fromDb = ChannelModelCache.getByOrigin(model);
            if (null == fromDb) {
                continue;
            }

            ModelConfig modelConfig = PamirsSession.getContext().getModelConfig(model);
            List<ModelField> modelFields = modelConfig.getModelDefinition().getModelFields();
            Map<String, Object> shardingKeyMap = new HashMap<>();
            for (ModelField field : modelFields) {
                if (null == field) {
                    continue;
                }
                if (field.getImmutable()) {
                    String columnName = field.getColumn();
                    Column column = binlogEventAfterMap.get(columnName);
                    if (null != column) {
                        shardingKeyMap.put(columnName, column.getValue());
                    }
                }
            }

            boolean isDelete = false;
            PamirsTableConfig tableCfg = PamirsTableInfo.fetchPamirsTableInfo(model);
            if (Boolean.TRUE.equals(tableCfg.getLogicDelete())) {
                String deleteColumn = tableCfg.getLogicDeleteColumn();
                isDelete = Optional.ofNullable(binlogEventAfterMap.get(deleteColumn))
                        .map(Column::getValue)
                        .map(String::valueOf)
                        .filter(_delete -> !"0".equals(_delete))
                        .isPresent();
            }

            // 物理删除
            isDelete = isDelete || EventType.DELETE.equals(eventType) || CollectionUtils.isEmpty(binlogEventAfter);

            try {
                processorOneManager.isOk().readOne(fromDb, id, shardingKeyMap, isDelete);
            } catch (Exception e) {
                throw PamirsException.construct(CHANNEL_BINLOG_EVENT_CONSUME_ERROR, e).errThrow();
            }
        }
    }
}
