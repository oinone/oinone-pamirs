package pro.shushi.k2.fun.processor;

import com.sun.source.tree.Tree;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.*;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.util.Collections;
import java.util.Set;

/**
 * PamirsAbstractProcessor
 *
 * @author yakir on 2019/08/19 17:25.
 */
abstract
public class PamirsAbstractProcessor extends JetBrainsWarpProcessor {

    protected JavacTrees trees;
    protected TreeMaker treeMaker;
    protected Name.Table names;
    protected Context context;
    protected Messager messager;
    protected Filer filer;
    protected Types typeUtils;
    protected Elements elementUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        processingEnv = jbUnwrap(ProcessingEnvironment.class, processingEnv);
        super.init(processingEnv);
        this.trees = JavacTrees.instance(processingEnv);
        this.context = ((JavacProcessingEnvironment) processingEnv).getContext();
        this.treeMaker = TreeMaker.instance(context);
        this.names = Names.instance(context).table;
        this.messager = processingEnv.getMessager();
        this.filer = processingEnv.getFiler();
        this.typeUtils = processingEnv.getTypeUtils();
        this.elementUtils = processingEnv.getElementUtils();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(getSupportType());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }

    public abstract String getSupportType();

    protected void error(Element e, String msg, Object... args) {
        messager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args), e);
    }

    protected void warning(Element e, String msg, Object... args) {
        messager.printMessage(Diagnostic.Kind.WARNING, String.format(msg, args), e);
    }

    protected Name getMethodName(JCTree.JCVariableDecl jcVariableDecl) {
        Name name = jcVariableDecl.name;
        String s = name.toString();
        if (jcVariableDecl.sym.type.toString().equalsIgnoreCase("boolean")) {
            return names.fromString("is" + s.substring(0, 1).toUpperCase() + s.substring(1, name.length()));
        } else {
            return names.fromString("get" + s.substring(0, 1).toUpperCase() + s.substring(1, name.length()));
        }
    }

    protected JCTree.JCExpression memberAccess(String components) {
        String[] componentArray = components.split("\\.");
        JCTree.JCExpression expr = treeMaker.Ident(names.fromString(componentArray[0]));
        for (int i = 1; i < componentArray.length; i++) {
            expr = treeMaker.Select(expr, names.fromString(componentArray[i]));
        }
        return expr;
    }

    protected boolean hasToString(JCTree.JCClassDecl jcClassDecl) {
        Name name = names.fromString("toString");
        long count = jcClassDecl.defs.stream().filter(_jcDef -> Tree.Kind.METHOD.equals(_jcDef.getKind())).map(_jcDef -> (JCTree.JCMethodDecl) _jcDef).filter(_jcMetDef -> name.equals(_jcMetDef.name)).count();
        return count > 0;
    }

    protected JCTree.JCMethodDecl makeToStringMethod(JCTree.JCClassDecl jcClassDecl, boolean isClassical) {

        JCTree.JCModifiers modifiers = treeMaker.Modifiers(Flags.PUBLIC);
        Name methodName = names.fromString("toString");
        JCTree.JCExpression returnType = memberAccess(String.class.getCanonicalName());
        List<JCTree.JCTypeParameter> generics = List.nil();
        List<JCTree.JCVariableDecl> parameters = List.nil();
        List<JCTree.JCExpression> throwz = List.nil();
        ListBuffer<JCTree.JCStatement> statements = new ListBuffer<>();
        JCTree.JCExpression classNameExpress = treeMaker.Literal(jcClassDecl.getSimpleName().toString() + "{");

        for (JCTree defJCTree : jcClassDecl.defs) {
            if (defJCTree.getKind() == Tree.Kind.VARIABLE) {
                JCTree.JCVariableDecl varDecl = (JCTree.JCVariableDecl) defJCTree;
                if (varDecl.getModifiers().getFlags().contains(Modifier.STATIC) || varDecl.getModifiers().getFlags().contains(Modifier.FINAL)) {
                    continue;
                }
                JCTree.JCExpression thisExpression = treeMaker.Ident(names.fromString("this"));
                Name varGetMethodName = getMethodName(varDecl);
                JCTree.JCExpression varGetMethodSelect = treeMaker.Select(thisExpression, varGetMethodName);
                JCTree.JCExpression varGetMethod = treeMaker.Apply(List.nil(), varGetMethodSelect, List.nil());
                JCTree.JCExpression varNameExpress = treeMaker.Literal(varDecl.getName().toString() + "='");
                classNameExpress = treeMaker.Binary(JCTree.Tag.PLUS, classNameExpress, varNameExpress);
                classNameExpress = treeMaker.Binary(JCTree.Tag.PLUS, classNameExpress, varGetMethod);
                classNameExpress = treeMaker.Binary(JCTree.Tag.PLUS, classNameExpress, treeMaker.Literal("'\n"));
            }
        }

        classNameExpress = treeMaker.Binary(JCTree.Tag.PLUS, classNameExpress, treeMaker.Literal("} "));
        statements.append(treeMaker.Return(classNameExpress));
        JCTree.JCBlock methodBody = treeMaker.Block(0, statements.toList());
        JCTree.JCMethodDecl toStringMethodDecl = treeMaker.MethodDef(modifiers, methodName, returnType, generics, parameters, throwz, methodBody, null);
        return toStringMethodDecl;
    }

    /**
     * 重置当前解析树指针
     *
     * @param treeMaker   构造树
     * @param jcClassDecl 类
     */
    public static void resetPoint(TreeMaker treeMaker, JCTree.JCClassDecl jcClassDecl) {
        JCTree last = jcClassDecl.defs.last();
        if (last != null) {
            treeMaker.pos = last.pos;
        }
    }

    /**
     * 重置当前解析树指针到文件头
     *
     * @param treeMaker   构造树
     * @param jcClassDecl 类
     */
    public static void resetPointToFirst(TreeMaker treeMaker, JCTree.JCClassDecl jcClassDecl) {
        JCTree first = jcClassDecl.defs.get(0);
        if (first != null) {
            treeMaker.pos = first.pos;
        }
    }
}
