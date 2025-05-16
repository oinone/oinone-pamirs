package pro.shushi.pamirs.core.common;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.framework.common.entry.TreeNode;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 树帮助类
 */
public class TreeHelper {

    private TreeHelper() {
        //reject create object
    }

    public static <T> List<TreeNode<T>> convert(Collection<T> collection, Function<T, String> keyGenerator, Function<T, String> parentKeyGenerator) {
        return convert(collection, keyGenerator, parentKeyGenerator, v -> v);
    }

    public static <T, R> List<TreeNode<R>> convert(Collection<T> collection, Function<R, String> keyGenerator, Function<R, String> parentKeyGenerator, Function<T, R> converter) {
        Map<String, TreeNode<R>> rootMap = new LinkedHashMap<>();
        Map<String, TreeNode<R>> childrenMap = new HashMap<>();
        for (T object : collection) {
            //获取所需内容
            R value = converter.apply(object);
            if (value == null) {
                continue;
            }
            String key = keyGenerator.apply(value);
            if (key == null) {
                continue;
            }
            String parentKey = parentKeyGenerator.apply(value);

            //补充未填充值的节点并检查键值是否唯一，返回找到的当前节点
            TreeNode<R> currentNode = singleNodeVGS(rootMap, childrenMap, key, value);
            if (StringUtils.isBlank(parentKey)) {
                //当父节点的键值为空时，则该节点为根节点

                //若当前节点未创建，创建一个
                if (currentNode == null) {
                    currentNode = new TreeNode<>(key, value, null);
                }
                rootMap.putIfAbsent(key, currentNode);
            } else {
                //当父节点的键值不为空时，则该节点为子节点

                //找父节点
                TreeNode<R> parent = rootMap.get(parentKey);
                if (parent == null) {
                    parent = childrenMap.get(parentKey);
                }

                //当前父节点不存在，创建一个，但值为空，等待补充
                if (parent == null) {
                    parent = new TreeNode<>(parentKey, null, null);
                    childrenMap.put(parentKey, parent);
                }
                if (currentNode == null) {
                    childrenMap.put(key, new TreeNode<>(key, value, parent));
                } else {
                    if (currentNode.getParent() == null) {
                        currentNode.setParent(parent);
                    } else {
                        throw new RuntimeException(String.format("一个节点只能有一个父节点: [CurrentNodeKey %s] [CurrentParentNodeKey %s]", key, parentKey));
                    }
                }
            }
        }
        return new ArrayList<>(rootMap.values());
    }

    public static <T, TC extends Collection<T>> List<TreeNode<TC>> convertToMulti(TC collection, Function<T, String> keyGenerator, Function<T, String> parentKeyGenerator, Supplier<TC> rcSupplier) {
        return convertToMulti(collection, keyGenerator, parentKeyGenerator, v -> v, rcSupplier);
    }

    public static <T, R, TC extends Collection<T>, RC extends Collection<R>> List<TreeNode<RC>> convertToMulti(TC collection, Function<T, String> keyGenerator, Function<T, String> parentKeyGenerator, Function<T, R> converter, Supplier<RC> rcSupplier) {
        Map<String, TreeNode<RC>> rootMap = new LinkedHashMap<>();
        Map<String, TreeNode<RC>> childrenMap = new HashMap<>();
        for (T object : collection) {

            //获取所需内容
            String key = keyGenerator.apply(object);
            if (key == null) {
                continue;
            }
            String parentKey = parentKeyGenerator.apply(object);
            R value = converter.apply(object);

            //补充未填充值的节点并检查键值是否唯一，返回找到的当前节点
            TreeNode<RC> currentNode = multiNodeVGS(rootMap, childrenMap, key, value, rcSupplier);
            if (StringUtils.isBlank(parentKey)) {
                //当父节点的键值为空时，则该节点为根节点

                //若当前节点未创建，创建一个
                if (currentNode == null) {
                    RC rc = rcSupplier.get();
                    rc.add(value);
                    currentNode = new TreeNode<>(key, rc, null);
                }
                rootMap.putIfAbsent(key, currentNode);
            } else {
                //当父节点的键值不为空时，则该节点为子节点

                //找父节点
                TreeNode<RC> parent = rootMap.get(parentKey);
                if (parent == null) {
                    parent = childrenMap.get(parentKey);
                }

                //当前父节点不存在，创建一个，但值为空，等待补充
                if (parent == null) {
                    parent = new TreeNode<>(parentKey, null, null);
                    childrenMap.put(parentKey, parent);
                }
                if (currentNode == null) {
                    RC rc = rcSupplier.get();
                    rc.add(converter.apply(object));
                    childrenMap.put(key, new TreeNode<>(key, rc, parent));
                } else {
                    if (currentNode.getParent() == null) {
                        currentNode.setParent(parent);
                    } else {
                        throw new RuntimeException(String.format("一个节点只能有一个父节点: [CurrentNodeKey %s] [CurrentParentNodeKey %s]", key, parent));
                    }
                }
            }
        }
        return new ArrayList<>(rootMap.values());
    }

    private static <R> TreeNode<R> singleNodeVGS(Map<String, TreeNode<R>> rootMap, Map<String, TreeNode<R>> childrenMap, String key, R value) {
        TreeNode<R> currentNode = rootMap.get(key);
        if (currentNode == null) {
            currentNode = childrenMap.get(key);
        }
        if (currentNode != null) {
            if (currentNode.getValue() == null) {
                currentNode.setValue(value);
            } else {
                //throw new RuntimeException(String.format("不允许出现重复的节点键值: [CurrentNodeKey %s]", key));
            }
        }
        return currentNode;
    }

    private static <R, RC extends Collection<R>> TreeNode<RC> multiNodeVGS(Map<String, TreeNode<RC>> rootMap, Map<String, TreeNode<RC>> childrenMap, String key, R value, Supplier<RC> rcSupplier) {
        TreeNode<RC> currentNode = rootMap.get(key);
        if (currentNode == null) {
            currentNode = childrenMap.get(key);
        }
        if (currentNode != null) {
            RC collection = currentNode.getValue();
            if (collection == null) {
                collection = rcSupplier.get();
                currentNode.setValue(collection);
            }
            collection.add(value);
        }
        return currentNode;
    }
}
