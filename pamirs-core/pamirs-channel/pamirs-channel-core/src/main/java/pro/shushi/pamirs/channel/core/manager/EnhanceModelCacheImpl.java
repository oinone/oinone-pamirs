package pro.shushi.pamirs.channel.core.manager;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.channel.api.EnhanceModelCache;
import pro.shushi.pamirs.framework.connectors.data.elastic.common.util.IndexNaming;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * EnhanceModelCacheImpl
 *
 * @author yakir on 2020/04/30 00:02.
 */
@Component
public class EnhanceModelCacheImpl implements EnhanceModelCache {

    private static final Map<String, Set<String>> TABLE_MODEL = new ConcurrentHashMap<>();
    private static final Map<String, String>      MODEL_ALIAS = new ConcurrentHashMap<>();

    Function<String, Set<String>> getModelByTable = (String tableName) -> {
        IWrapper<ModelDefinition> wrapper = Pops.<ModelDefinition>lambdaQuery()
                .from(ModelDefinition.class)
                .eq(ModelDefinition::getTable, tableName)
                .eq(ModelDefinition::getType, ModelTypeEnum.STORE.value());
        return Optional.ofNullable(Models.origin().queryListByWrapper(wrapper))
                .filter(_list -> _list.size() > 0)
                .map(List::stream)
                .orElse(Stream.empty())
                .map(ModelDefinition::getModel)
                .collect(Collectors.toSet());
    };

    @Override
    public Set<String> table2Model(String tableName) {
        Set<String> sets = TABLE_MODEL.computeIfAbsent(tableName, getModelByTable);
        if (CollectionUtils.isNotEmpty(sets)) {
            return sets;
        }
        int    lastIndexOf = tableName.lastIndexOf(CharacterConstants.SEPARATOR_UNDERLINE);
        String table       = tableName.substring(0, lastIndexOf);
        sets = TABLE_MODEL.computeIfAbsent(table, getModelByTable);
        return sets;
    }

    @Override
    public String model2Alias(String model) {
        return MODEL_ALIAS.computeIfAbsent(model, IndexNaming::alias);
    }
}

