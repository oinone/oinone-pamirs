package pro.shushi.pamirs.fun.processor;

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
import pro.shushi.pamirs.fun.utils.FunUtils;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;

import javax.annotation.processing.AbstractProcessor;
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
 * @author deng
 */
public class PamirsFunProcessor extends AbstractProcessor {
    private JavacTrees trees;
    private TreeMaker treeMaker;
    private Name.Table names;
    private Context context;
    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init( processingEnv );
        this.trees = JavacTrees.instance(processingEnv);
        context = ((JavacProcessingEnvironment)
                processingEnv).getContext();
        treeMaker = TreeMaker.instance( context );
        names = Names.instance( context ).table;
        this.messager = processingEnv.getMessager();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        HashSet<String> supportTypes = new LinkedHashSet<>();
        supportTypes.add( Fun.class.getCanonicalName() );
        return supportTypes;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_8;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations,
                           RoundEnvironment roundEnv) {
        Set<? extends Element> set = roundEnv.getElementsAnnotatedWith(Fun.class);
        try{
            set.forEach(element -> {
                JCTree jcTree = trees.getTree(element);
                jcTree.accept(new TreeTranslator() {
                    @Override
                    public void visitClassDef(JCTree.JCClassDecl jcClassDecl) {

//                        makeFun(treeMaker, names, messager, jcClassDecl);

                        super.visitClassDef(jcClassDecl);
                        result = jcClassDecl;

                    }

                });
            });
        }catch (Exception e){
            System.err.println(e);
            throw e;
//            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    public static void makeFun(TreeMaker treeMaker, Name.Table names, Messager messager, JCTree.JCClassDecl jcClassDecl){
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

    private static boolean containsFunctionAnnotation(JCTree.JCMethodDecl jcMethodDecl){
        Function genericTypeModel = Optional.ofNullable(jcMethodDecl.sym).map(_notNull -> _notNull.getAnnotation(Function.class)).orElse(null);
        return null != genericTypeModel;
    }

}
