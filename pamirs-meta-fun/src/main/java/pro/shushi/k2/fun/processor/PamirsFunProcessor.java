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
import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.NoCode;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

/**
 * 函数处理器
 *
 * @author deng
 */
public class PamirsFunProcessor extends JetBrainsWarpProcessor {
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
        supportTypes.add(NoCode.class.getCanonicalName());
        return supportTypes;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_8;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations,
                           RoundEnvironment roundEnv) {
        Set<? extends Element> set = roundEnv.getElementsAnnotatedWith(NoCode.class);
        try {
            set.forEach(element -> {
                JCTree jcTree = trees.getTree(element);
                jcTree.accept(new TreeTranslator() {
                    @Override
                    public void visitClassDef(JCTree.JCClassDecl jcClassDecl) {
                        PamirsAbstractProcessor.resetPoint(treeMaker, jcClassDecl);
                        makeFun(treeMaker, names, messager, jcClassDecl);

                        super.visitClassDef(jcClassDecl);
                        result = jcClassDecl;

                    }

                });
            });
        } catch (Exception e) {
            System.err.println(e);
            throw e;
//            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    public static void makeFun(TreeMaker treeMaker, Name.Table names, Messager messager, JCTree.JCClassDecl jcClassDecl) {
        List<JCTree> defs = List.from(jcClassDecl.defs);
        defs.stream().filter(k -> k.getKind().equals(Tree.Kind.METHOD)).map(tree -> (JCTree.JCMethodDecl) tree).forEach(jcMethodDecl -> {
            if (!jcMethodDecl.mods.getFlags().contains(Modifier.STATIC) && !jcMethodDecl.mods.getFlags().contains(Modifier.FINAL)
                    && containsFunctionAnnotation(jcMethodDecl)
            ) {
                JCTree.JCMethodDecl funMethodDecl = FunUtils.makeFunMethodDecl(jcClassDecl, jcMethodDecl, treeMaker, names, messager);
                if (null != funMethodDecl) jcClassDecl.defs = jcClassDecl.defs.append(funMethodDecl);
            }
        });
    }

    private static boolean containsFunctionAnnotation(JCTree.JCMethodDecl jcMethodDecl) {
        return Optional.ofNullable(jcMethodDecl.sym)
                .filter(_notNull -> null != _notNull.getAnnotation(Function.class) || null != _notNull.getAnnotation(Action.class))
                .map(_exist-> Boolean.TRUE)
                .orElse(Boolean.FALSE);
    }

}
