package pro.shushi.pamirs.framework.gateways.graph.java.session;

import pro.shushi.pamirs.meta.annotation.fun.Data;
import pro.shushi.pamirs.meta.domain.model.DataDictionaryItem;

import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * 非整体变更Graphql中的枚举类型
 * @author shier
 * date  2021/9/8 11:09 上午
 */
@Data
public class GraphqlEnumContext {

    private List<DataDictionaryItem> addItems = new ArrayList<>();
    private List<DataDictionaryItem> deleteItems = new ArrayList<>();
    private List<DataDictionaryItem> sameItems = new ArrayList<>();

    private void addItem(DataDictionaryItem item) {
        this.addItems.add(item);
    }

    private void deleteItem(DataDictionaryItem item) {
        this.deleteItems.add(item);
    }

    private void addSameItem(DataDictionaryItem item) {
        this.sameItems.add(item);
    }

    public GraphqlEnumContext compute(List<DataDictionaryItem> originDictionaryList, Collection<DataDictionaryItem> changedDictionaryList) {
        GraphqlEnumContext context = new GraphqlEnumContext();
        Map<String, DataDictionaryItem> originItemMap = originDictionaryList.stream().collect(Collectors.toMap(v -> v.getName(), v -> v, (a, b) -> a));
        int max = Math.max(originDictionaryList.size(), changedDictionaryList.size());
        //直接指定大小,防止再散列
        Map<String, Integer> map = new HashMap<String, Integer>(max);
        for (DataDictionaryItem object : originDictionaryList) {
            map.put(object.getName(), 1);
        }
        for (DataDictionaryItem laterItem : changedDictionaryList) {
            if (map.get(laterItem.getName()) == null) {
                context.addItem(laterItem);
            } else {
                map.put(laterItem.getName(), 2);
                //判断是否有变化
                DataDictionaryItem originItem = originItemMap.get(laterItem.getName());
                if (!isSame(originItem, laterItem)) {
                    context.addItem(laterItem);
                    context.deleteItem(originItem);
                } else {
                    context.addSameItem(laterItem);
                }
            }
        }
        return context;
    }

    /**
     * 判断枚举可选项是否需要修改graphql结构
     *
     * @param origin 原始枚举可选项
     * @param later  变更后的枚举可选项
     * @return 是否
     */
    private Boolean isSame(DataDictionaryItem origin, DataDictionaryItem later) {
        return origin.getName().equals(later.getName()) && origin.getDisplayName().equals(later.getDisplayName());
    }
}
