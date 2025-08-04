package pro.shushi.k2.fun.processor;

import com.sun.source.tree.Tree;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.*;
import pro.shushi.pamirs.meta.annotation.Fun;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author deng
 */
public class PamirsInvokePlaceProcessor extends AbstractProcessor {
    private JavacTrees trees;
    private TreeMaker treeMaker;
    private Name.Table names;
    private Context context;
    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
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
        supportTypes.add(Fun.class.getCanonicalName());
        return supportTypes;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations,
                           RoundEnvironment roundEnv) {
        Set<? extends Element> set = roundEnv.getElementsAnnotatedWith(Fun.class);
        set.forEach(element -> {
            JCTree jcTree = trees.getTree(element);
            jcTree.accept(new TreeTranslator() {
                @Override
                public void visitClassDef(JCTree.JCClassDecl jcClassDecl) {
                    PamirsAbstractProcessor.resetPoint(treeMaker, jcClassDecl);

                    // 暂存class
                    try {
                        InputStream classInputStream = ((Symbol.ClassSymbol) element).classfile.openInputStream();
                        String classCode = new BufferedReader(new InputStreamReader(classInputStream))
                                .lines().collect(Collectors.joining("\n"));
                        jcClassDecl.defs = jcClassDecl.defs.prepend(makeClassMethodDecl(classCode));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    PamirsAbstractProcessor.resetPoint(treeMaker, jcClassDecl);

                    // place action、extpoint、function invoke code to FaaS invoke
                    for (JCTree tree : jcClassDecl.defs) {
                        if (tree.getKind().equals(Tree.Kind.METHOD)) {
                            ((JCTree.JCMethodDecl) tree).getModifiers().getAnnotations().forEach(jcAnnotation -> {
                                String clazzName = ((Symbol.ClassSymbol) jcAnnotation.type.tsym).fullname.toString();
                                if (clazzName.equals("pro.shushi.pamirs.framework.base.annotation.Action")
                                        || clazzName.equals("pro.shushi.pamirs.framework.base.annotation.Extpoint")
                                        || clazzName.equals("pro.shushi.pamirs.meta.annotation.Function")) {

                                    JCTree.JCMethodDecl jcMethodDecl = (JCTree.JCMethodDecl) tree;

                                    ListBuffer<JCTree.JCStatement> statements = new ListBuffer<>();
                                    // 创建变量 functionName
                                    statements.append(treeMaker.VarDef(
                                            treeMaker.Modifiers(0),
                                            getNameFromString("functionName"), //名字
                                            memberAccess("java.lang.String"), //类型
                                            treeMaker.Literal(jcClassDecl.sym.fullname + "." + jcMethodDecl.name) //初始化语句
                                    ));

                                    List<JCTree.JCExpression> paramTypes = List.of(memberAccess("java.lang.String"));
                                    List<JCTree.JCExpression> params = List.of(treeMaker.Ident(getNameFromString("functionName")));
                                    for (JCTree.JCVariableDecl variableDecl : jcMethodDecl.params) {
                                        paramTypes = paramTypes.append(variableDecl.vartype);
                                        params = params.append(treeMaker.Ident(variableDecl.name));
                                    }

                                    // 构造callEngine
                                    JCTree.JCExpression engineInit = treeMaker.Exec(treeMaker.Apply(
                                                    List.nil(),//参数类型
                                                    memberAccess("pro.shushi.pamirs.base.call.Fun.get"),
                                                    List.nil()
                                            )
                                    ).expr;
                                    // 创建变量 callEngine
                                    statements.append(treeMaker.VarDef(
                                            treeMaker.Modifiers(0),
                                            getNameFromString("fun"), //名字
                                            memberAccess("pro.shushi.pamirs.base.call.Fun"), //类型
                                            engineInit //初始化语句
                                    ));

                                    // 创建变量 返回值
                                    statements.append(treeMaker.VarDef(
                                            treeMaker.Modifiers(0),
                                            getNameFromString("result"), //名字
                                            memberAccess("java.lang.Object"), //类型 jcMethodDecl.restype
                                            treeMaker.Apply(
                                                    paramTypes,//参数类型
                                                    memberAccess("fun.run"),
                                                    params
                                            ) //初始化语句
                                    ));
                                    // 调用 callEngine
                                    statements.append(treeMaker.Call(treeMaker.TypeCast(jcMethodDecl.restype, treeMaker.Ident(names.fromString("result")))));
                                    JCTree.JCBlock body = treeMaker.Block(0, statements.toList());
                                    // 替换body
                                    jcMethodDecl.body = body;
                                }
                            });
                        }
                    }

                    super.visitClassDef(jcClassDecl);
                }

            });
        });

        return true;
    }

    private JCTree.JCExpression memberAccess(String components) {
        String[] componentArray = components.split("\\.");
        JCTree.JCExpression expr = treeMaker.Ident(getNameFromString(componentArray[0]));
        for (int i = 1; i < componentArray.length; i++) {
            expr = treeMaker.Select(expr, getNameFromString(componentArray[i]));
        }
        return expr;
    }

    private Name getNameFromString(String s) {
        return names.fromString(s);
    }

    private JCTree.JCMethodDecl makeClassMethodDecl(String classDefine) {
        ListBuffer<JCTree.JCStatement> statements = new ListBuffer<>();
        statements.append(treeMaker.VarDef(
                treeMaker.Modifiers(0),
                getNameFromString("classDefine"), //名字
                memberAccess("java.lang.String"), //类型
                treeMaker.Literal(classDefine) //初始化语句
        ));
        statements.append(treeMaker.Return(treeMaker.Ident(names.fromString("classDefine"))));
        JCTree.JCBlock body = treeMaker.Block(0, statements.toList());
        return treeMaker.MethodDef(treeMaker.Modifiers(Flags.PUBLIC), names.fromString("getClazzDefine"), memberAccess("java.lang.String"), List.nil(), List.nil(), List.nil(), body, null);
    }

    private class Inliner extends TreeTranslator {
        @Override
        public void visitMethodDef(JCTree.JCMethodDecl jcMethodDecl) {
            super.visitMethodDef(jcMethodDecl);
            if (jcMethodDecl.getName().toString().equals("getUserName")) {
                JCTree.JCMethodDecl methodDecl = treeMaker.MethodDef(jcMethodDecl.getModifiers(), names.fromString("testMethod"), jcMethodDecl.restype, jcMethodDecl.getTypeParameters(), jcMethodDecl.getParameters(), jcMethodDecl.getThrows(), jcMethodDecl.getBody(), jcMethodDecl.defaultValue);
                result = methodDecl;
            }
        }
    }


}
