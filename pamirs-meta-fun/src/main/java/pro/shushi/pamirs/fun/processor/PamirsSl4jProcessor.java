package pro.shushi.pamirs.fun.processor;

import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.Types;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.logger.PamirsLogger;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Set;

/**
 *
 */
public class PamirsSl4jProcessor extends PamirsAbstractProcessor {

    @Override
    public String getSupportType() {
        return Slf4j.class.getCanonicalName();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        for (Element element : roundEnv.getElementsAnnotatedWith(Slf4j.class)) {

            if (element.getKind() != ElementKind.CLASS) {
                error(element, "Only classes can be annotated with @%s", Slf4j.class.getCanonicalName());
            }

            JCTree.JCClassDecl jcClassDecl = (JCTree.JCClassDecl) trees.getTree(element);
            long slf4jCount = jcClassDecl.mods.annotations.stream()
                    .filter(_jcAnno -> _jcAnno.type.toString().equals(Slf4j.class.getName())) // 可能是匿名内部类，需要判断一下是否有@Slf4j注解 不然会报错
                    .count();

            if (slf4jCount < 1) {
                continue;
            }

            JCTree.JCModifiers modifiers = treeMaker.Modifiers(Flags.PRIVATE | Flags.STATIC | Flags.FINAL);
//            JCTree.JCExpression returnType  = treeMaker.TypeIdent(TypeTag.valueOf(Logger.class.getCanonicalName()));
            Types types = Types.instance(context);
            Type erasure = jcClassDecl.sym.type.getTypeArguments().size() == 0
                    ? jcClassDecl.sym.type
                    : types.erasure(jcClassDecl.sym.type);// jcClassDecl.sym.erasure_field;
            JCTree.JCMethodInvocation logNewInstance = treeMaker.Apply(List.nil(), memberAccess("pro.shushi.pamirs.meta.common.logger.Log.newInstance"), List.of(treeMaker.ClassLiteral(erasure)));
            JCTree.JCVariableDecl jcVariableDecl = treeMaker.VarDef(modifiers, names.fromString("log"), memberAccess(PamirsLogger.class.getName()), logNewInstance);
            messager.printMessage(Diagnostic.Kind.NOTE, jcVariableDecl.toString());

            jcClassDecl.defs = jcClassDecl.defs.append(jcVariableDecl);
        }

        return true;
    }

}
