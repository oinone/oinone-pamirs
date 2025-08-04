package pro.shushi.k2.fun.processor;

import com.sun.source.tree.Tree;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.List;
import pro.shushi.k2.fun.utils.FunUtils;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.fun.Data;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class PamirsDataProcessor extends PamirsAbstractProcessor {

    @Override
    public String getSupportType() {
        return Data.class.getCanonicalName();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> set = Optional.ofNullable(roundEnv.getElementsAnnotatedWith(Data.class)).orElse(new HashSet<>(1));
        for (Element element : set) {
            JCTree jcTree = trees.getTree(element);

            jcTree.accept(new TreeTranslator() {
                @Override
                public void visitClassDef(JCTree.JCClassDecl jcClassDecl) {
                    PamirsAbstractProcessor.resetPoint(treeMaker, jcClassDecl);

                    Boolean aBoolean = Optional.ofNullable(jcClassDecl.sym).map(Symbol.ClassSymbol::isAnonymous).orElse(true);
                    Data data = Optional.ofNullable(jcClassDecl.sym).map(_notNull -> _notNull.getAnnotation(Data.class)).orElse(null);
                    if (null == data || aBoolean) {
                        result = jcClassDecl;
                        return;
                    }
                    Model model = Optional.ofNullable(jcClassDecl.sym).map(_notNull -> _notNull.getAnnotation(Model.class)).orElse(null);
                    if (null != model) {
                        messager.printMessage(Diagnostic.Kind.ERROR, "请不要在 " + jcClassDecl.getSimpleName() + " 上 同时使用 @Data 和 @Model注解");
                    }

                    boolean isChain = data.chain();

                    boolean hasDefaultConstruct = false;
                    for (JCTree k : jcClassDecl.defs) {
                        if (Tree.Kind.VARIABLE.equals(k.getKind())) {

                            JCTree.JCVariableDecl jcVariableDecl = (JCTree.JCVariableDecl) k;

                            if (jcVariableDecl.mods.getFlags().contains(Modifier.STATIC)) {
                                continue;
                            }

                            if (!FunUtils.hasGetter(jcClassDecl, jcVariableDecl, names)) {
                                JCTree.JCMethodDecl getMethodDecl = FunUtils.makeClassicsGetterMethodDecl(jcClassDecl, jcVariableDecl, treeMaker, names, messager);
                                if (null != getMethodDecl)
                                    jcClassDecl.defs = jcClassDecl.defs.append(getMethodDecl);
                            }

                            if (!jcVariableDecl.mods.getFlags().contains(Modifier.FINAL) && !FunUtils.hasSetter(jcClassDecl, jcVariableDecl, names)) {
                                JCTree.JCMethodDecl setMethodDecl = FunUtils.makeClassicsSetterMethodDecl(jcClassDecl, jcVariableDecl, treeMaker, names, messager, isChain);
                                if (null != setMethodDecl)
                                    jcClassDecl.defs = jcClassDecl.defs.append(setMethodDecl);
                            }
                        }

                        if (Tree.Kind.METHOD.equals(k.getKind())) {
                            JCTree.JCMethodDecl _jcMetDef = (JCTree.JCMethodDecl) k;
                            if ("<init>".equals(_jcMetDef.name.toString())) {
                                hasDefaultConstruct = true;
                            }
                        }
                    }
                    if (!hasDefaultConstruct) {
                        JCTree.JCMethodDecl no = FunUtils.makeNoArgsConstructMethodDecl(jcClassDecl, treeMaker, names, messager, true);
                        if (null != no) jcClassDecl.defs = jcClassDecl.defs.append(no);
                    }

                    if (!hasToString(jcClassDecl)) {
                        JCTree.JCMethodDecl toStringMethod = makeToStringMethod(jcClassDecl, true);
                        if (null != toStringMethod) jcClassDecl.defs = jcClassDecl.defs.append(toStringMethod);
                    }

                    super.visitClassDef(jcClassDecl);
                    result = jcClassDecl;
                }
            });
        }
        return true;
    }

}
