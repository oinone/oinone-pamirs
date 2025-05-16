package pro.shushi.pamirs.core.common.entry;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 图节点
 *
 * @author Adamancy Zhang at 10:46 on 2021-09-28
 */
public final class GraphNode<T> implements Serializable {

    private static final long serialVersionUID = -823859165535866454L;

    /**
     * 节点唯一键，不可修改，且线程不安全
     */
    private String key;

    /**
     * 节点数据
     */
    private T value;

    /**
     * 指向当前节点的所有节点
     */
    private final List<GraphNode<T>> previous = new ArrayList<>();

    /**
     * 当前节点指向的所有节点
     */
    private final List<GraphNode<T>> next = new ArrayList<>();

    /**
     * 扩展数据
     */
    private Object extend;

    /**
     * 构造一个空节点
     */
    public GraphNode() {
    }

    /**
     * 构造一个具有完整数据的顶级节点
     *
     * @param key   键
     * @param value 值
     */
    public GraphNode(String key, T value) {
        this(key, value, null);
    }

    /**
     * 构造一个具有完整结构及数据的节点
     *
     * @param key    键
     * @param value  值
     * @param parent 父节点，为空则表示自己是顶级节点
     */
    public GraphNode(String key, T value, GraphNode<T> parent) {
        this.key = key;
        this.value = value;
        if (parent != null) {
            addPrevious(parent);
        }
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
     * 判断当前节点是否是根节点
     *
     * @return 是否是叶节点
     */
    public boolean isRoot() {
        return previous.isEmpty();
    }

    /**
     * 判断当前节点是否有上一个
     *
     * @return 是否有上一个
     */
    public boolean hasPrevious() {
        return !isRoot();
    }

    /**
     * 判断当前节点是否是叶节点
     *
     * @return 是否是叶节点
     */
    public boolean isLeaf() {
        return next.isEmpty();
    }

    /**
     * 判断当前节点是否有下一个
     *
     * @return 是否有下一个
     */
    public boolean hasNext() {
        return !isLeaf();
    }

    /**
     * <h>获取指向当前节点的所有节点列表</h>
     * <p>
     * 为保证结构不被随意篡改，返回结果使用不可修改列表进行封装
     * </p>
     *
     * @return 子节点列表
     */
    public List<GraphNode<T>> getPrevious() {
        return previous;
    }

    /**
     * <h>获取当前节点指向的所有节点列表</h>
     * <p>
     * 为保证结构不被随意篡改，返回结果使用不可修改列表进行封装
     * </p>
     *
     * @return 子节点列表
     */
    public List<GraphNode<T>> getNext() {
        return next;
    }

    /**
     * 将一个节点指向当前节点
     *
     * @param node 需要指向当前节点的节点
     * @return 是否添加成功
     */
    public boolean addPrevious(GraphNode<T> node) {
        if (this.previous.contains(node)) {
            return false;
        }
        this.previous.add(node);
        node.next.add(this);
        return true;
    }

    /**
     * 移除一个指向当前节点的节点
     *
     * @param node 需要移除的节点
     * @return 是否移除成功
     */
    public boolean removePrevious(GraphNode<T> node) {
        if (this.previous.remove(node)) {
            node.next.remove(this);
            return true;
        }
        return false;
    }

    /**
     * 将当前节点指向一个节点
     *
     * @param node 当前节点需要指向的节点，可重复操作，结果不变
     * @return 是否添加成功
     */
    public boolean addNext(GraphNode<T> node) {
        if (this.next.contains(node)) {
            return false;
        }
        this.next.add(node);
        node.previous.add(this);
        return true;
    }

    /**
     * 移除一个当前节点指向的节点
     *
     * @param node 需要移除的节点
     * @return 是否移除成功
     */
    public boolean removeNext(GraphNode<T> node) {
        if (this.next.remove(node)) {
            node.previous.remove(this);
            return true;
        }
        return false;
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
        if (!(o instanceof GraphNode)) {
            return false;
        }
        GraphNode<?> graphNode = (GraphNode<?>) o;
        return Objects.equals(key, graphNode.key) &&
                Objects.equals(value, graphNode.value) &&
                Objects.equals(extend, graphNode.extend);
    }

    /**
     * 重写hashCode，与{@link GraphNode#equals(Object)}逻辑一致
     *
     * @return 哈希值
     */
    @Override
    public int hashCode() {
        return Objects.hash(key, value, extend);
    }
}
