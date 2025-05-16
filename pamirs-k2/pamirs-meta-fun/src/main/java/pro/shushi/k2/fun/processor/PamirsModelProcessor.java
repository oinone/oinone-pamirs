package pro.shushi.k2.fun.processor;

import com.sun.source.tree.Tree;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.List;
import pro.shushi.k2.fun.utils.FunUtils;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.fun.Data;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Optional;
import java.util.Set;

/**
 * @author deng
 */
public class PamirsModelProcessor extends PamirsAbstractProcessor {

    @Override
    public String getSupportType() {
        return Model.class.getCanonicalName();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        Set<? extends Element> set = roundEnv.getElementsAnnotatedWith(Model.class);
        for (Element element : set) {
            JCTree jcTree = trees.getTree(element);
            jcTree.accept(new TreeTranslator() {
                @Override
                public void visitClassDef(JCTree.JCClassDecl jcClassDecl) {
                    resetPoint(treeMaker, jcClassDecl);

                    Boolean aBoolean = Optional.ofNullable(jcClassDecl.sym).map(Symbol::isAnonymous).orElse(true);

                    Model model = Optional.ofNullable(jcClassDecl.sym).map(_notNull -> _notNull.getAnnotation(Model.class)).orElse(null);
                    if (null == model || aBoolean) {
                        result = jcClassDecl;
                        return;
                    }
                    Data data = Optional.ofNullable(jcClassDecl.sym).map(_notNull -> _notNull.getAnnotation(Data.class)).orElse(null);
                    if (null != data) {
                        messager.printMessage(Diagnostic.Kind.ERROR, "请不要在 " + jcClassDecl.getSimpleName() + " 上 同时使用 @Data 和 @Model注解");
                    }

                    boolean isChain = Optional.ofNullable(jcClassDecl.sym).map(_notNull -> _notNull.getAnnotation(Model.Advanced.class)).map(Model.Advanced::chain).orElse(true);

                    boolean hasDefaultConstruct = false;
                    boolean needAllArgConstruct = true;
                    boolean hasSerialVersionUID = false;
                    for (JCTree k : jcClassDecl.defs) {
                        if (Tree.Kind.VARIABLE.equals(k.getKind())) {
                            JCTree.JCVariableDecl jcVariableDecl = (JCTree.JCVariableDecl) k;

                            if ("serialVersionUID".equals(jcVariableDecl.name.toString())) {
                                hasSerialVersionUID = true;

                                if (!jcVariableDecl.mods.getFlags().contains(Modifier.STATIC)) {
                                    messager.printMessage(Diagnostic.Kind.WARNING, "serialVersionUID不是static修饰");
                                }
                                if (!jcVariableDecl.mods.getFlags().contains(Modifier.FINAL)) {
                                    messager.printMessage(Diagnostic.Kind.WARNING, "serialVersionUID不是final修饰");
                                }

                                continue;
                            }

                            if (jcVariableDecl.mods.getFlags().contains(Modifier.STATIC)) {
                                continue;
                            }
                            if (jcVariableDecl.mods.getFlags().contains(Modifier.FINAL)) {
                                continue;
                            }
                            boolean isField = false;
                            List<JCTree.JCAnnotation> fieldAnnoList = jcVariableDecl.getModifiers().getAnnotations();
                            for (JCTree.JCAnnotation jcAnno : fieldAnnoList) {
                                Type type = jcAnno.getAnnotationType().type;
                                String annoTypeName = type.toString();
                                if (annoTypeName.equalsIgnoreCase(Field.class.getName())
                                        || annoTypeName.equalsIgnoreCase(Field.many2one.class.getName())
                                        || annoTypeName.equalsIgnoreCase(Field.one2one.class.getName())
                                        || annoTypeName.equalsIgnoreCase(Field.one2many.class.getName())
                                        || annoTypeName.equalsIgnoreCase(Field.many2many.class.getName())) {
                                    isField = true;
                                    break;
                                }
                            }

                            JCTree.JCMethodDecl getMethodDecl = null;
                            JCTree.JCMethodDecl setMethodDecl = null;
                            JCTree.JCMethodDecl unsetMethodDecl = null;
                            if (isField) {
                                // FIXME: zbh 20240822 先上测试环境回归完成后再打开注释
                                // FunUtils.checkDateLtype(jcClassDecl, jcVariableDecl, messager);
                                getMethodDecl = FunUtils.makeGetterMethodDecl(jcClassDecl, jcVariableDecl, treeMaker, names, messager);
                                setMethodDecl = FunUtils.makeSetterMethodDecl(jcClassDecl, jcVariableDecl, treeMaker, names, messager, isChain);
                                unsetMethodDecl = FunUtils.makeUnSetterMethodDecl(jcClassDecl, jcVariableDecl, treeMaker, names, isChain);
                            } else {
                                if (!FunUtils.hasGetter(jcClassDecl, jcVariableDecl, names)) {
                                    getMethodDecl = FunUtils.makeClassicsGetterMethodDecl(jcClassDecl, jcVariableDecl, treeMaker, names, messager);
                                }

                                if (!FunUtils.hasSetter(jcClassDecl, jcVariableDecl, names)) {
                                    setMethodDecl = FunUtils.makeClassicsSetterMethodDecl(jcClassDecl, jcVariableDecl, treeMaker, names, messager, isChain);
                                }
                            }

                            if (null != getMethodDecl)
                                jcClassDecl.defs = jcClassDecl.defs.append(getMethodDecl);
                            if (null != setMethodDecl)
                                jcClassDecl.defs = jcClassDecl.defs.append(setMethodDecl);
                            if (null != unsetMethodDecl)
                                jcClassDecl.defs = jcClassDecl.defs.append(unsetMethodDecl);
                        }
                        if (Tree.Kind.METHOD.equals(k.getKind())) {
                            JCTree.JCMethodDecl _jcMetDef = (JCTree.JCMethodDecl) k;
                            if ("<init>".equals(_jcMetDef.name.toString())) {
                                if (_jcMetDef.params.isEmpty()) {
                                    hasDefaultConstruct = true;
                                } else {
                                    needAllArgConstruct = false;
                                }
                            }
                        }
                    }

                    if (!hasSerialVersionUID) {
                        messager.printMessage(Diagnostic.Kind.WARNING,
                                "==================================================>>>>>>>>>> 请定义serialVersionUID [" + jcClassDecl.getSimpleName() + "]");
                    }

                    if (!hasDefaultConstruct) {
                        JCTree.JCMethodDecl no = FunUtils.makeNoArgsConstructMethodDecl(jcClassDecl, treeMaker, names, messager, false);
                        if (null != no) jcClassDecl.defs = jcClassDecl.defs.append(no);
                    }

                    if (needAllArgConstruct) {
                        JCTree.JCMethodDecl all = FunUtils.makeAllArgsConstructMethodDecl(jcClassDecl, treeMaker, names, messager, false);
                        if (null != all) jcClassDecl.defs = jcClassDecl.defs.append(all);
                    }

                    if (!hasToString(jcClassDecl)) {
                        JCTree.JCMethodDecl toStringMethod = makeToStringMethod(jcClassDecl, false);
                        if (null != toStringMethod) jcClassDecl.defs = jcClassDecl.defs.append(toStringMethod);
                    }

                    Fun fun = Optional.ofNullable(jcClassDecl.sym).map(_notNull -> _notNull.getAnnotation(Fun.class)).orElse(null);
                    if (null != fun) {
                        messager.printMessage(Diagnostic.Kind.ERROR, "请不要在 " + jcClassDecl.getSimpleName() + " 上 同时使用 @Fun 和 @Model注解");
                    }
//                    else{
//                        PamirsFunProcessor.makeFun(treeMaker, names, messager, jcClassDecl);
//                    }

                    super.visitClassDef(jcClassDecl);
                    result = jcClassDecl;
//                    this.translate(jcClassDecl.defs);
                }
            });
        }

        return true;
    }

}
