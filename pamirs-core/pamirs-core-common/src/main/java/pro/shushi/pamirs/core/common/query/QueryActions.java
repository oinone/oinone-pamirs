package pro.shushi.pamirs.core.common.query;

import pro.shushi.pamirs.boot.base.enmu.ActionTypeEnum;
import pro.shushi.pamirs.boot.base.model.Action;
import pro.shushi.pamirs.core.common.DataShardingHelper;
import pro.shushi.pamirs.core.common.ObjectHelper;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.common.lambda.Getter;

import java.util.*;
import java.util.stream.Stream;

/**
 * 通过模型和名称元组查询动作集合
 *
 * @param <T> 任意动作模型
 * @author Adamancy Zhang at 11:01 on 2023-12-09
 */
public class QueryActions<T extends Action> {

    private final String model;

    private Getter<T, ?>[] selects;

    private final List<String> models;

    private final List<String> names;

    private final Set<String> repeatSet;

    public QueryActions(ActionTypeEnum actionType) {
        this.model = QueryActionHelper.getActionModel(actionType);
        this.models = new ArrayList<>(8);
        this.names = new ArrayList<>(8);
        this.repeatSet = new HashSet<>(8);
    }

    @SafeVarargs
    public final QueryActions<T> selects(Getter<T, ?>... selects) {
        this.selects = selects;
        return this;
    }

    @SuppressWarnings("unchecked")
    @SafeVarargs
    public final QueryActions<T> appendSelects(Getter<T, ?>... selects) {
        this.selects = Stream.concat(Arrays.stream(this.selects), Arrays.stream(selects)).toArray(Getter[]::new);
        return this;
    }

    public void add(String model, String name) {
        if (ObjectHelper.isRepeat(this.repeatSet, Action.sign(model, name))) {
            return;
        }
        this.models.add(model);
        this.names.add(name);
    }

    public List<T> query() {
        if (this.models.isEmpty()) {
            return Collections.emptyList();
        }
        List<List<String>> shardingModels = DataShardingHelper.build(100).sharding(this.models);
        List<List<String>> shardingNames = DataShardingHelper.build(100).sharding(this.names);
        List<T> actions = new ArrayList<>(this.models.size());
        for (int i = 0; i < shardingModels.size(); i++) {
            LambdaQueryWrapper<T> wrapper = Pops.<T>lambdaQuery()
                    .from(this.model)
                    .setBatchSize(-1)
                    .in(Arrays.asList(Action::getModel, Action::getName), shardingModels.get(i), shardingNames.get(i));
            if (this.selects != null) {
                wrapper.select(this.selects);
            }
            actions.addAll(Models.origin().queryListByWrapper(wrapper));
        }
        return actions;
    }
}