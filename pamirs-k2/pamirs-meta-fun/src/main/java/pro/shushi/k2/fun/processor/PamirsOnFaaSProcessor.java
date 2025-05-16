package pro.shushi.k2.fun.processor;

import com.alibaba.fastjson.JSON;
import com.sun.source.tree.Tree;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;
import org.apache.commons.collections.CollectionUtils;
import pro.shushi.k2.fun.model.FunctionDefine;
import pro.shushi.k2.fun.enmu.FunType;
import pro.shushi.k2.fun.utils.FunUtils;
import pro.shushi.pamirs.meta.annotation.fun.PamirsOn;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author deng
 */
public class PamirsOnFaaSProcessor extends JetBrainsWarpProcessor {

    private JavacTrees trees;
    private TreeMaker treeMaker;
    private Name.Table names;
    private Context context;
    private Messager             messager;
    // 因为是单线程，所以不用考虑并发
    private List<FunctionDefine> functions = new ArrayList<>();

    private static final List<String> excludeMethods = new ArrayList<>();

    static {
            excludeMethods.add("<init>");
            excludeMethods.add("equals");
            excludeMethods.add("canEqual");
            excludeMethods.add("hashCode");
            excludeMethods.add("toString");
//        excludeMethods.add("getFun");

    }

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
        supportTypes.add(PamirsOn.class.getCanonicalName());
        return supportTypes;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_8;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations,
                           RoundEnvironment roundEnv) {

        Set<? extends Element> set = roundEnv.getElementsAnnotatedWith(PamirsOn.class);
        if (set.isEmpty()) {
            return Boolean.TRUE;
        }
        String dir = PamirsOnFaaSProcessor.class.getResource("/").getPath();
        File classesFile = new File(dir);
        File faasFile = new File(classesFile.getPath() + "/faas");
        File file = new File(classesFile.getPath() + "/faas/function.json");
        try {
            if (!faasFile.exists()) {
                faasFile.mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        set.forEach(element -> {
            JCTree jcTree = trees.getTree(element);

            jcTree.accept(new TreeTranslator() {
                @Override
                public void visitClassDef(JCTree.JCClassDecl jcClassDecl) {
                    PamirsAbstractProcessor.resetPoint(treeMaker, jcClassDecl);

                    if (element.getAnnotation(PamirsOn.class).fun()) {
                        FunUtils.makeFunVariable(jcClassDecl, treeMaker, names, messager);
                    }

                    for (JCTree tree : jcClassDecl.defs) {
                        if (tree.getKind().equals(Tree.Kind.METHOD)) {
                            JCTree.JCMethodDecl jcMethodDecl = (JCTree.JCMethodDecl) tree;
                            String funName = jcMethodDecl.name.toString();
                            if (excludeMethods.contains(funName)) {
                                continue;
                            }
                            java.util.List<JCTree.JCAnnotation> funAnnotations = ((JCTree.JCMethodDecl) tree).getModifiers().getAnnotations().stream().filter(jcAnnotation ->
                                    !FunType.LOCAL.equals(FunUtils.getFunType(jcAnnotation))).collect(Collectors.toList());
                            if (CollectionUtils.isNotEmpty(funAnnotations)) {
                                functions.add(FunUtils.makeFunctionDefine(element, funAnnotations.get(0), tree, jcClassDecl));
                            } else {
                                functions.add(FunUtils.makeFunctionDefine(element, null, tree, jcClassDecl));
                            }
                        }
                    }

                    super.visitClassDef(jcClassDecl);
                }

            });

        });

        if (!functions.isEmpty()) {
            try {
                FileWriter fw = new FileWriter(file);
                fw.write(JSON.toJSONString(functions));
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return Boolean.TRUE;
    }

}
