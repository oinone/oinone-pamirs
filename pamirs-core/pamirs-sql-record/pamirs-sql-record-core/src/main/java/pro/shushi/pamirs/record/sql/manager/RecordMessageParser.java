package pro.shushi.pamirs.record.sql.manager;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.util.JsonUtils;
import pro.shushi.pamirs.middleware.canal.EventType;
import pro.shushi.pamirs.middleware.canal.domain.Column;
import pro.shushi.pamirs.middleware.canal.domain.Row;
import pro.shushi.pamirs.record.sql.pojo.SQLRecord;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static pro.shushi.pamirs.meta.common.constants.VariableNameConstants.dataModel;
import static pro.shushi.pamirs.meta.common.constants.VariableNameConstants.entityModel;

/**
 * RecordMessageParser
 *
 * @author yakir on 2023/06/30 17:54.
 */
@Slf4j
public class RecordMessageParser {

    public static Row parser(SQLRecord record) {

        String nowData = record.getNow();
        String oldData = record.getOld();

        Row row = new Row();
        row.setSchema(record.getSchema());
        row.setTable(record.getTable());
        row.setDeloyTime(record.getcT().getTime());

        Map<String, Object> oldDataMap = Optional.ofNullable(JsonUtils.parseMap(oldData)).orElse(Collections.emptyMap());
        Map<String, Object> nowDataMap = Optional.ofNullable(JsonUtils.parseMap(nowData)).orElse(Collections.emptyMap());

        List<Column> before = new ArrayList<>();
        List<Column> after  = new ArrayList<>();

        Object id  = null;
        String env = null;

        for (Map.Entry<String, Object> entry : oldDataMap.entrySet()) {
            String key = entry.getKey();
            if (StringUtils.equalsAny(key, entityModel, dataModel)) {
                continue;
            }

            Object value = entry.getValue();
            if (StringUtils.equalsIgnoreCase("id", key)) {
                id = value;
            } else if (StringUtils.equalsIgnoreCase("e", key)) {
                env = String.valueOf(value);
            }

            Column column = new Column();
            column.setName(key);
            column.setValue(value);
            before.add(column);
        }

        // 更新后的数据
        for (Map.Entry<String, Object> entry : nowDataMap.entrySet()) {
            String key = entry.getKey();
            if (StringUtils.equalsAny(key, entityModel, dataModel)) {
                continue;
            }

            Object value = entry.getValue();
            if (StringUtils.equalsIgnoreCase("id", key)) {
                id = value;
            } else if (StringUtils.equalsIgnoreCase("e", key)) {
                env = String.valueOf(value);
            }

            Column column = new Column();
            column.setName(key);
            column.setValue(entry.getValue());
            boolean isUpdate = entry.getValue().equals(oldDataMap.get(key));
            column.setUpdate(isUpdate);
            after.add(column);
        }
        EventType eventType = EventType.valueOf(record.getEventType());

        row.setId(id);
        row.setEventType(eventType);
        row.setEnv(env);
        row.setTenant(record.getT());
        row.setBefore(before);
        row.setAfter(after);
        return row;
    }
}
