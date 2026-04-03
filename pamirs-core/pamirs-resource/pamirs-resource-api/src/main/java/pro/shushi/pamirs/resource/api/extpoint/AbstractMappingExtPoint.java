package pro.shushi.pamirs.resource.api.extpoint;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.base.extpoint.DefaultReadWriteExtPoint;
import pro.shushi.pamirs.resource.api.model.ResourceRegionMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author drome
 * @date 2020/9/97:51 下午
 */
public abstract class AbstractMappingExtPoint<T> extends DefaultReadWriteExtPoint<T> {

    @Override
    public T createBefore(T data) {
        _mappingfilter(data);
        return data;
    }

    @Override
    public T updateBefore(T data) {
        _mappingfilter(data);
        return data;
    }

    /**
     * 过滤同名的关键字
     *
     * @param data
     */
    private void _mappingfilter(T data) {
        if (data == null) {
            return;
        }
        List<ResourceRegionMapping> mappings = getMapping(data);
        if (CollectionUtils.isEmpty(mappings)) {
            return;
        }
        Map<String, ResourceRegionMapping> keywords2MappingMap = mappings.stream()
                .filter(i -> i != null && StringUtils.isNotBlank(i.getKeywords()))
                .collect(Collectors.toMap(i -> i.getKeywords().trim(), i -> i, (a, b) -> a.getId() != null ? a : b));
        setMapping(data, new ArrayList<>(keywords2MappingMap.values()));
    }

    abstract List<ResourceRegionMapping> getMapping(T t);

    abstract void setMapping(T t, List<ResourceRegionMapping> mappings);

}
