package pro.shushi.pamirs.framework.gateways.graph.java.session;

import pro.shushi.pamirs.meta.annotation.fun.Data;
import pro.shushi.pamirs.meta.domain.model.ModelField;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author shier
 * date  2021/9/8 11:09 上午
 */
@Data
public class GraphqlFieldContext {

    private List<ModelField> addFields = new ArrayList<>();
    private List<ModelField> deleteFields = new ArrayList<>();
    private List<ModelField> sameFields = new ArrayList<>();

    private void addField(ModelField field) {
        this.addFields.add(field);
    }

    private void deleteField(ModelField field) {
        this.deleteFields.add(field);
    }

    private void addSameField(ModelField field) {
        this.sameFields.add(field);
    }

    public GraphqlFieldContext compute(List<ModelField> originFields, Collection<ModelField> laterFields) {
        GraphqlFieldContext context = new GraphqlFieldContext();
        Map<String, ModelField> originFieldMap = originFields.stream().collect(Collectors.toMap(v -> v.getName(), v -> v, (a, b) -> a));
        int max = Math.max(originFields.size(), laterFields.size());
        //直接指定大小,防止再散列
        Map<String, Integer> map = new HashMap<String, Integer>(max);
        for (ModelField object : originFields) {
            map.put(object.getName(), 1);
        }
        for (ModelField laterField : laterFields) {
            if (map.get(laterField.getName()) == null) {
                context.addField(laterField);
            } else {
                map.put(laterField.getName(), 2);
                //判断是否有变化
                ModelField originField = originFieldMap.get(laterField.getName());
                if (!isSame(originField, laterField)) {
                    context.addField(laterField);
                    context.deleteField(originField);
                } else {
                    context.addSameField(laterField);
                }
            }
        }
        return context;
    }

    /**
     * 判断模型字段是否需要修改graphql结构
     *
     * @param origin 原始字段
     * @param later  变更后的字段
     * @return 是否
     */
    private Boolean isSame(ModelField origin, ModelField later) {
        return origin.getTtype().equals(later.getTtype()) && origin.getDisplayName().equals(later.getDisplayName());
    }
}
