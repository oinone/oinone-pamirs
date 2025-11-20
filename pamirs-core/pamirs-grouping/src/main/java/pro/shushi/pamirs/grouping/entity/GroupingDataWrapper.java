package pro.shushi.pamirs.grouping.entity;

import pro.shushi.pamirs.grouping.model.GroupingData;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 分组数据包装类
 *
 * @author Adamancy Zhang at 12:32 on 2025-11-14
 */
public class GroupingDataWrapper {

    private final TableGroupingFieldQuery query;

    private final GroupingData data;

    private final String key;

    private final Object value;

    private List<Object> results;

    private final Map<String, GroupingDataWrapper> groupings;

    private String parentKey;

    private String parentField;

    public GroupingDataWrapper(TableGroupingFieldQuery query, String key, GroupingData data, Object value) {
        this.query = query;
        this.data = data;
        this.key = key;
        this.value = value;
        this.groupings = new LinkedHashMap<>();
    }

    public String getKey() {
        return key;
    }

    public TableGroupingFieldQuery getQuery() {
        return query;
    }

    public GroupingData getData() {
        return data;
    }

    public String getField() {
        return data.getField();
    }

    public Object getValue() {
        return value;
    }

    public List<Object> getResults() {
        return results;
    }

    public Map<String, GroupingDataWrapper> getGroupings() {
        return groupings;
    }

    public String getParentKey() {
        return parentKey;
    }

    public void setParentKey(String parentKey) {
        this.parentKey = parentKey;
    }

    public String getParentField() {
        return parentField;
    }

    public void setParentField(String parentField) {
        this.parentField = parentField;
    }

    public void addData(Object data) {
        List<Object> results = this.results;
        if (results == null) {
            results = new ArrayList<>();
            this.results = results;
        }
        results.add(data);
    }
}