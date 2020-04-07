package pro.shushi.pamirs.fun.processor;

import com.sun.source.tree.Tree;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeTranslator;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.fun.Data;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.fun.utils.FunUtils;

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
//                .filter(element -> element.getKind() == ElementKind.CLASS)
//                .forEach(element -> {
            JCTree jcTree = trees.getTree(element);
            jcTree.accept(new TreeTranslator() {
                @Override
                public void visitClassDef(JCTree.JCClassDecl jcClassDecl) {

                    Boolean aBoolean = Optional.ofNullable(jcClassDecl.sym).map(_notNull -> _notNull.isAnonymous()).orElse(true);

                    Model model = Optional.ofNullable(jcClassDecl.sym).map(_notNull -> _notNull.getAnnotation(Model.class)).orElse(null);
                    if (null == model || aBoolean) {
                        result = jcClassDecl;
                        return;
                    }
                    Data data = Optional.ofNullable(jcClassDecl.sym).map(_notNull -> _notNull.getAnnotation(Data.class)).orElse(null);
                    if (null != data){
                        messager.printMessage(Diagnostic.Kind.ERROR,"请不要在 " + jcClassDecl.getSimpleName() + " 上 同时使用 @Data 和 @Model注解");
                    }
                    jcClassDecl.defs.stream().filter(k -> k.getKind().equals(Tree.Kind.VARIABLE)).map(tree -> (JCTree.JCVariableDecl) tree).forEach(jcVariableDecl -> {
                        if (!jcVariableDecl.mods.getFlags().contains(Modifier.STATIC) && !jcVariableDecl.mods.getFlags().contains(Modifier.FINAL)) {
                            JCTree.JCMethodDecl getMethodDecl = FunUtils.makeGetterMethodDecl(jcClassDecl, jcVariableDecl, treeMaker, names, messager);
                            JCTree.JCMethodDecl setMethodDecl  = FunUtils.makeSetterMethodDecl(jcClassDecl, jcVariableDecl, treeMaker, names, messager);
                            JCTree.JCMethodDecl unsetMethodDecl  = FunUtils.makeUnSetterMethodDecl(jcClassDecl, jcVariableDecl, treeMaker, names);
                            if (null != getMethodDecl) jcClassDecl.defs = jcClassDecl.defs.append(getMethodDecl);
                            if (null != setMethodDecl) jcClassDecl.defs = jcClassDecl.defs.append(setMethodDecl);
                            if (null != unsetMethodDecl) jcClassDecl.defs = jcClassDecl.defs.append(unsetMethodDecl);
                        }
                    });

                    if (!FunUtils.hasConstruct(jcClassDecl)) {
                        JCTree.JCMethodDecl all = FunUtils.makeAllArgsConstructMethodDecl(jcClassDecl, treeMaker, names, messager, false);
                        JCTree.JCMethodDecl no = FunUtils.makeNoArgsConstructMethodDecl(jcClassDecl, treeMaker, names, messager, false);
                        if (null != all) jcClassDecl.defs = jcClassDecl.defs.append(all);
                        if (null != no) jcClassDecl.defs = jcClassDecl.defs.append(no);
                    }

                    if (!hasToString(jcClassDecl)) {
                        JCTree.JCMethodDecl toStringMethod = makeToStringMethod(jcClassDecl, false);
                        if (null != toStringMethod) jcClassDecl.defs = jcClassDecl.defs.append(toStringMethod);
                    }

                    Fun fun = Optional.ofNullable(jcClassDecl.sym).map(_notNull -> _notNull.getAnnotation(Fun.class)).orElse(null);
                    if (null != fun){
                        messager.printMessage(Diagnostic.Kind.ERROR,"请不要在 " + jcClassDecl.getSimpleName() + " 上 同时使用 @Fun 和 @Model注解");
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
