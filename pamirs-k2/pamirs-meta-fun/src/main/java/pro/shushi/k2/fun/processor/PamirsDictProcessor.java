package pro.shushi.k2.fun.processor;

import com.sun.source.tree.Tree;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.tree.JCTree;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.common.enmu.BaseEnum;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Adamancy Zhang on 2021-04-21 18:42
 */
public class PamirsDictProcessor extends PamirsAbstractProcessor {

    @Override
    public String getSupportType() {
        return Dict.class.getCanonicalName();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> set = roundEnv.getElementsAnnotatedWith(Dict.class);
        List<TreeNode<Symbol.ClassSymbol>> roots = PamirsDictProcessor.convert(set, Symbol.ClassSymbol::toString, classSymbol -> {
            Type type = classSymbol.getSuperclass();
            String parentClassName;
            if (type == null) {
                return null;
            }
            if (BaseEnum.class.getName().equals((parentClassName = type.tsym.toString()))) {
                return null;
            }
            return parentClassName;
        }, element -> {
            ElementKind kind = element.getKind();
            if (ElementKind.CLASS.equals(kind) && element instanceof Symbol.ClassSymbol) {
                return (Symbol.ClassSymbol) element;
            }
            return null;
        });
        for (TreeNode<Symbol.ClassSymbol> root : roots) {
            generatorDictStaticField(root);
        }
        return true;
    }

    private void generatorDictStaticField(TreeNode<Symbol.ClassSymbol> root) {
        Symbol.ClassSymbol parent = root.getValue();
        String parentKey = root.getKey();
        JCTree.JCClassDecl jcClassDecl = trees.getTree(parent);
        List<com.sun.tools.javac.util.Name> parentVariableList;
        if (jcClassDecl == null) {
            Iterable<Symbol> iterable = parent.members().getElements();
            parentVariableList = new ArrayList<>();
            symbol:
            for (Symbol item : iterable) {
                if (!item.getKind().equals(ElementKind.FIELD)) {
                    continue;
                }
                if (!parentKey.equals(item.type.toString())) {
                    continue;
                }
                Set<Modifier> modifiers = item.getModifiers();
                Modifier[] targetModifiers = new Modifier[]{Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL};
                for (Modifier targetModifier : targetModifiers) {
                    if (!modifiers.contains(targetModifier)) {
                        continue symbol;
                    }
                }
                parentVariableList.add(item.getQualifiedName());
            }
        } else {
            parentVariableList = streamOptional(jcClassDecl, parentKey)
                    .map(JCTree.JCVariableDecl::getName)
                    .collect(Collectors.toList());
        }
        if (parentVariableList.isEmpty()) {
            messager.printMessage(Diagnostic.Kind.ERROR, "不允许存在空的枚举类 " + parent.getQualifiedName().toString());
            return;
        }
        List<TreeNode<Symbol.ClassSymbol>> children = root.getChildren();
        for (TreeNode<Symbol.ClassSymbol> child : children) {
            Symbol.ClassSymbol childValue = child.getValue();
            JCTree.JCClassDecl childJcClassDecl = trees.getTree(childValue);
            if (childJcClassDecl == null) {
                continue;
            }
            String childKey = child.getKey();
            Map<String, JCTree.JCVariableDecl> childVariableMap = streamOptional(childJcClassDecl, childKey)
                    .collect(Collectors.toMap(v -> v.name.toString(), v -> v));
            if (childVariableMap.isEmpty()) {
                messager.printMessage(Diagnostic.Kind.ERROR, "不允许存在空的枚举类 " + childJcClassDecl.sym.getQualifiedName().toString());
                return;
            }
            for (com.sun.tools.javac.util.Name parentVariableName : parentVariableList) {
                JCTree.JCVariableDecl childVariable = childVariableMap.get(parentVariableName.toString());
                if (childVariable != null) {
                    continue;
                }
                resetPoint(treeMaker, childJcClassDecl);
                childJcClassDecl.defs = childJcClassDecl.defs.append(generatorRefStaticField(parentKey, parentVariableName, childKey));
            }
            if (!child.getChildren().isEmpty()) {
                generatorDictStaticField(child);
            }
        }
    }

    private JCTree.JCVariableDecl generatorRefStaticField(String parentClassName, com.sun.tools.javac.util.Name parentName, String childClassName) {
        return treeMaker.VarDef(
                treeMaker.Modifiers(Flags.PUBLIC | Flags.STATIC | Flags.FINAL),
                parentName,
                memberAccess(childClassName),
                treeMaker.Apply(com.sun.tools.javac.util.List.nil(),
                        memberAccess(BaseEnum.class.getName() + ".ref"),
                        com.sun.tools.javac.util.List.of(memberAccess(parentClassName + "." + parentName.toString()))));
    }

    private Stream<JCTree.JCVariableDecl> streamOptional(JCTree.JCClassDecl jcClassDecl, String key) {
        return jcClassDecl.defs
                .stream()
                .filter(v -> v.getKind().equals(Tree.Kind.VARIABLE))
                .map(v -> (JCTree.JCVariableDecl) v)
                .filter(v -> {
                    if (v.vartype.type == null) {
                        if (!key.equals(v.vartype.toString())) {
                            return false;
                        }
                    } else {
                        if (!key.equals(v.vartype.type.toString())) {
                            return false;
                        }
                    }
                    Set<Modifier> modifiers = v.mods.getFlags();
                    Modifier[] targetModifiers = new Modifier[]{Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL};
                    for (Modifier targetModifier : targetModifiers) {
                        if (!modifiers.contains(targetModifier)) {
                            return false;
                        }
                    }
                    return true;
                });
    }

    private static class TreeNode<T> {

        private String key;

        private T value;

        private int level = 1;

        private TreeNode<T> parent;

        private final List<TreeNode<T>> children = new ArrayList<>();

        private Object extend;

        public TreeNode() {
        }

        public TreeNode(String key, T value, TreeNode<T> parent) {
            this.key = key;
            this.value = value;
            this.parent = parent;
            addChild(this.parent);
        }

        public TreeNode(String key, T value) {
            this(key, value, null);
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            if (this.key == null) {
                this.key = key;
            }
        }

        public T getValue() {
            return value;
        }

        public void setValue(T value) {
            this.value = value;
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public TreeNode<T> getParent() {
            return parent;
        }

        public void setParent(TreeNode<T> parent) {
            removeChild(this.parent);
            this.parent = parent;
            addChild(this.parent);
        }

        public Object getExtend() {
            return extend;
        }

        public void setExtend(Object extend) {
            this.extend = extend;
        }

        public Boolean isLeaf() {
            return children.isEmpty();
        }

        public Boolean hasChildren() {
            return !children.isEmpty();
        }

        public List<TreeNode<T>> getChildren() {
            return children;
        }

        public void forEachChildren(Consumer<TreeNode<T>> consumer) {
            for (TreeNode<T> child : this.children) {
                consumer.accept(child);
            }
        }

        private void addChild(TreeNode<T> parent) {
            if (parent != null) {
                parent.children.add(this);
            }
            resetLevel(this);
        }

        private void resetLevel(TreeNode<T> rootNode) {
            TreeNode<T> parentNode = rootNode.parent;
            if (parentNode == null) {
                rootNode.level = 1;
            } else {
                rootNode.level = parentNode.level + 1;
                for (TreeNode<T> child : rootNode.children) {
                    resetLevel(child);
                }
            }
        }

        private void removeChild(TreeNode<T> parent) {
            if (parent == null) {
                return;
            }
            parent.children.remove(this);
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof TreeNode) {
                return this.hashCode() == Objects.hash(((TreeNode<?>) o).key);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hash(key);
        }
    }

    private static <T extends Element, R extends Symbol.ClassSymbol> List<TreeNode<R>> convert(Collection<T> collection, Function<R, String> keyGenerator, Function<R, String> parentKeyGenerator, Function<T, R> converter) {
        Map<String, TreeNode<R>> rootMap = new LinkedHashMap<>(collection.size());
        Map<String, TreeNode<R>> childrenMap = new HashMap<>(collection.size());
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
                        throw new RuntimeException(String.format("一个节点只能有一个父节点: [CurrentNodeKey %s] [CurrentParentNodeKey %s]", key, parent));
                    }
                }
            }
        }
        for (TreeNode<R> node : childrenMap.values()) {
            TreeNode<R> parent = node.getParent();
            if (parent == null) {
                continue;
            }
            String parentKey = parent.getKey();
            if (parent.getValue() != null || rootMap.containsKey(parentKey)) {
                continue;
            }
            //noinspection unchecked
            parent.setValue((R) node.getValue().getSuperclass().tsym);
            rootMap.put(parentKey, parent);
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
                throw new RuntimeException(String.format("不允许出现重复的节点键值: [CurrentNodeKey %s]", key));
            }
        }
        return currentNode;
    }
}
