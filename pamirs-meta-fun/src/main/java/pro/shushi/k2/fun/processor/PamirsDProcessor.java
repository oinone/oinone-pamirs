package pro.shushi.k2.fun.processor;

import com.sun.source.tree.Tree;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;
import pro.shushi.k2.fun.utils.FunUtils;
import pro.shushi.pamirs.meta.annotation.fun.D;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

/**
 * @author deng
 */
public class PamirsDProcessor extends JetBrainsWarpProcessor {
    private JavacTrees trees;
    private TreeMaker treeMaker;
    private Name.Table names;
    private Context context;
    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        processingEnv = jbUnwrap(ProcessingEnvironment.class, processingEnv);
        super.init(processingEnv);
        this.trees = JavacTrees.instance(processingEnv);
        context = ((JavacProcessingEnvironment)
                processingEnv).getContext();
        treeMaker = TreeMaker.instance(context);
        names = Names.instance(context).table;
        this.messager = processingEnv.getMessager();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        HashSet<String> supportTypes = new LinkedHashSet<>();
        supportTypes.add(D.class.getCanonicalName());
        return supportTypes;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_8;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations,
                           RoundEnvironment roundEnv) {
        Set<? extends Element> set = roundEnv.getElementsAnnotatedWith(D.class);
        set.forEach(element -> {
            JCTree jcTree = trees.getTree(element);
            jcTree.accept(new TreeTranslator() {
                @Override
                public void visitClassDef(JCTree.JCClassDecl jcClassDecl) {
                    PamirsAbstractProcessor.resetPoint(treeMaker, jcClassDecl);
                    List<JCTree.JCVariableDecl> jcVariableDeclList = List.nil();

                    for (JCTree tree : jcClassDecl.defs) {
                        if (tree.getKind().equals(Tree.Kind.VARIABLE)) {
                            JCTree.JCVariableDecl jcVariableDecl = (JCTree.JCVariableDecl) tree;
                            jcVariableDeclList = jcVariableDeclList.append(jcVariableDecl);
                        }
                    }

                    jcVariableDeclList.forEach(jcVariableDecl -> {
                        if (!jcVariableDecl.mods.toString().contains("static") && !jcVariableDecl.mods.toString().contains("final")) {
                            boolean isChain = Optional.ofNullable(jcClassDecl.sym).map(_notNull -> _notNull.getAnnotation(D.class)).map(D::chain).orElse(true);
                            Optional.ofNullable(FunUtils.makeGetterMethodDecl(jcClassDecl, jcVariableDecl, treeMaker, names, messager)).map(_method -> jcClassDecl.defs = jcClassDecl.defs.append(_method));
                            Optional.ofNullable(FunUtils.makeSetterMethodDecl(jcClassDecl, jcVariableDecl, treeMaker, names, messager, isChain)).map(_method -> jcClassDecl.defs = jcClassDecl.defs.append(_method));
                            Optional.ofNullable(FunUtils.makeUnSetterMethodDecl(jcClassDecl, jcVariableDecl, treeMaker, names, isChain)).map(_method -> jcClassDecl.defs = jcClassDecl.defs.append(_method));
//                            jcClassDecl.defs = jcClassDecl.defs.append(FunUtils.makeGetterMethodDecl(jcClassDecl, jcVariableDecl, treeMaker, names,messager));
//                            jcClassDecl.defs = jcClassDecl.defs.append(FunUtils.makeSetterMethodDecl(jcClassDecl, jcVariableDecl, treeMaker, names,messager));
//                            jcClassDecl.defs = jcClassDecl.defs.append(FunUtils.makeUnSetterMethodDecl(jcClassDecl, jcVariableDecl, treeMaker, names));
//                            jcClassDecl.defs = jcClassDecl.defs.append(FunUtils.makeNoArgsConstructorDecl(jcClassDecl, jcVariableDecl, treeMaker, names));

                        }
                    });
                    Optional.ofNullable(FunUtils.makeAllArgsConstructMethodDecl(jcClassDecl, treeMaker, names, messager, false)).map(_method -> jcClassDecl.defs = jcClassDecl.defs.append(_method));
                    Optional.ofNullable(FunUtils.makeNoArgsConstructMethodDecl(jcClassDecl, treeMaker, names, messager, false)).map(_method -> jcClassDecl.defs = jcClassDecl.defs.append(_method));
                    super.visitClassDef(jcClassDecl);

                }

            });
        });

        return true;
    }

}
