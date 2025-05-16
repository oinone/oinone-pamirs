package pro.shushi.pamirs.framework.common.entry;

import pro.shushi.pamirs.meta.common.lambda.Getter;
import pro.shushi.pamirs.meta.common.lambda.LambdaUtil;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.*;

/**
 * 树节点
 *
 * @author Adamancy Zhang at 12:55 on 2020-11-25
 */
public final class TreeNode<T> implements Serializable {

    private static final long serialVersionUID = -4172709876273274747L;

    /**
     * 节点唯一键，不可修改，且线程不安全
     */
    private String key;

    /**
     * 节点数据
     */
    private T value;

    /**
     * 节点层级，从1开始计数
     */
    private int level = 1;

    /**
     * 父节点
     */
    private TreeNode<T> parent;

    /**
     * 子节点
     */
    private final List<TreeNode<T>> children = new ArrayList<>();

    /**
     * 扩展数据
     */
    private Object extend;

    /**
     * 构造一个空节点
     */
    public TreeNode() {
    }

    /**
     * 构造一个具有完整数据的顶级节点
     *
     * @param key   键
     * @param value 值
     */
    public TreeNode(String key, T value) {
        this(key, value, null);
    }

    /**
     * 构造一个具有完整结构及数据的节点
     *
     * @param key    键
     * @param value  值
     * @param parent 父节点，为空则表示自己是顶级节点
     */
    public TreeNode(String key, T value, TreeNode<T> parent) {
        this.key = key;
        this.value = value;
        this.parent = parent;
        addChildForParent(this.parent);
    }

    /**
     * 获取键
     *
     * @return 键
     */
    public String getKey() {
        return key;
    }

    /**
     * 设置键，当且仅当键为空时可用
     *
     * @param key 键
     */
    public void setKey(String key) {
        if (this.key == null) {
            this.key = key;
        }
    }

    /**
     * 获取值
     *
     * @return 值
     */
    public T getValue() {
        return value;
    }

    /**
     * 设置值
     *
     * @param value 值
     */
    public void setValue(T value) {
        this.value = value;
    }

    /**
     * 获取当前节点层级
     *
     * @return 当前节点层级
     */
    public int getLevel() {
        return level;
    }

    /**
     * 获取父节点
     *
     * @return 父节点
     */
    public TreeNode<T> getParent() {
        return parent;
    }

    /**
     * 修改当前节点的父节点
     *
     * @param parent 父节点
     */
    public void setParent(TreeNode<T> parent) {
        removeChildInParentForThis(this.parent);
        this.parent = parent;
        addChildForParent(this.parent);
    }

    /**
     * 获取扩展数据
     *
     * @return 扩展数据
     */
    public Object getExtend() {
        return extend;
    }

    /**
     * 设置扩展数据
     *
     * @param extend 扩展数据
     */
    public void setExtend(Object extend) {
        this.extend = extend;
    }

    /**
     * 判断当前节点是否是叶节点
     *
     * @return 是否是叶节点
     */
    public boolean isLeaf() {
        return children.isEmpty();
    }

    /**
     * 判断当前节点是否有子节点
     *
     * @return 是否有子节点
     */
    public boolean hasChildren() {
        return !children.isEmpty();
    }

    /**
     * <h>获取子节点列表</h>
     * <p>
     * 为保证结构不被随意篡改，返回结果使用不可修改列表进行封装
     * </p>
     *
     * @return 子节点列表
     */
    public List<TreeNode<T>> getChildren() {
        return Collections.unmodifiableList(children);
    }

    /**
     * 对子节点进行排序
     *
     * @param c 比较方式
     */
    public void sortChildren(Comparator<? super TreeNode<T>> c) {
        this.children.sort(c);
    }

    /**
     * 为当前节点添加子节点
     *
     * @param child 子节点
     */
    public void addChild(TreeNode<T> child) {
        child.setParent(this);
    }

    /**
     * 为当前节点在指定索引位置添加子节点
     *
     * @param index 指定索引
     * @param child 子节点
     */
    public void addChild(int index, TreeNode<T> child) {
        if (child.parent != null) {
            child.parent.children.remove(child);
        }
        child.parent = this;
        this.children.add(index, child);
        resetLevel(child);
    }

    /**
     * 获取首个与键相同的子节点
     *
     * @param getter 属性的Getter方法
     * @param <V>    任意类型
     * @param <R>    任意类型
     * @return 匹配的首个子节点
     */
    public <V, R> TreeNode<T> firstChild(@NotNull Getter<V, R> getter) {
        return firstChild(LambdaUtil.fetchFieldName(getter));
    }

    /**
     * 获取首个与键相同的子节点
     *
     * @param key 键
     * @return 匹配的首个子节点
     */
    public TreeNode<T> firstChild(@NotNull String key) {
        for (TreeNode<T> child : this.children) {
            if (key.equals(child.getKey())) {
                return child;
            }
        }
        return null;
    }

    /**
     * 查找所有指定键值的子节点
     *
     * @param getter       属性的Getter方法
     * @param otherGetters 其他属性的Getter方法
     * @param <V>          任意类型
     * @param <R>          任意类型
     * @return 所有匹配的子节点
     */
    @SafeVarargs
    public final <V, R> List<TreeNode<T>> findChildren(@NotNull Getter<V, R> getter, Getter<V, R>... otherGetters) {
        Set<String> keys = new HashSet<>(1 + otherGetters.length);
        keys.add(LambdaUtil.fetchFieldName(getter));
        for (Getter<V, R> otherGetter : otherGetters) {
            keys.add(LambdaUtil.fetchFieldName(otherGetter));
        }
        List<TreeNode<T>> list = new ArrayList<>();
        for (TreeNode<T> child : this.children) {
            if (keys.contains(child.getKey())) {
                list.add(child);
            }
        }
        return list;
    }

    /**
     * 查找所有键相同的子节点
     *
     * @param key 键
     * @return 所有匹配的子节点
     */
    public List<TreeNode<T>> findChildren(@NotNull String key) {
        List<TreeNode<T>> list = new ArrayList<>();
        for (TreeNode<T> child : this.children) {
            if (key.equals(child.getKey())) {
                list.add(child);
            }
        }
        return list;
    }

    /**
     * 将自己加入到指定父节点中
     *
     * @param parent 父节点
     */
    private void addChildForParent(TreeNode<T> parent) {
        if (parent != null) {
            parent.children.add(this);
        }
        resetLevel(this);
    }

    /**
     * 将自己从指定父节点中移除
     *
     * @param parent 父节点
     */
    private void removeChildInParentForThis(TreeNode<T> parent) {
        if (parent == null) {
            return;
        }
        parent.children.remove(this);
    }

    /**
     * 重置层级
     *
     * @param node 指定节点
     * @param <V>  任意类型
     */
    private static <V> void resetLevel(TreeNode<V> node) {
        TreeNode<V> parentNode = node.parent;
        if (parentNode == null) {
            node.level = 1;
        } else {
            node.level = parentNode.level + 1;
            for (TreeNode<V> child : node.children) {
                resetLevel(child);
            }
        }
    }

    /**
     * 重写对象相等，当且仅当键值一致时认为两对象是同一对象，请务必保证键值一致的对象中存储的值是完全一致的，否则可能造成不可预知的错误
     *
     * @param o 指定对象
     * @return 是否相等
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TreeNode)) {
            return false;
        }
        TreeNode<?> treeNode = (TreeNode<?>) o;
        return level == treeNode.level &&
                key.equals(treeNode.key) &&
                value.equals(treeNode.value) &&
                Objects.equals(extend, treeNode.extend);
    }


    /**
     * 重写hashCode，与{@link TreeNode#equals(Object)}逻辑一致
     *
     * @return 哈希值
     */
    @Override
    public int hashCode() {
        return Objects.hash(key, value, level, extend);
    }
}