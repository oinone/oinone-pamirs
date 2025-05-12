package pro.shushi.k2.fun.processor;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeTranslator;
import org.apache.commons.collections4.CollectionUtils;
import pro.shushi.pamirs.meta.annotation.Fun;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * PamirsFuncProcessor
 *
 * @author yakir on 2024/08/21 17:25.
 */
public class PamirsFuncProcessor extends PamirsAbstractProcessor {

    @Override
    public String getSupportType() {
        return Fun.class.getCanonicalName();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        Set<? extends Element> set = roundEnv.getElementsAnnotatedWith(Fun.class);
        for (Element element : set) {
            JCTree jcTree = trees.getTree(element);
            jcTree.accept(new TreeTranslator() {
                @Override
                public void visitClassDef(JCTree.JCClassDecl jcClassDecl) {

                    Set<String> funcNameSet = new HashSet<>();
                    for (JCTree entry : jcClassDecl.defs) {
                        if (!JCTree.Kind.METHOD.equals(entry.getKind())) {
                            continue;
                        }

                        JCTree.JCMethodDecl method = (JCTree.JCMethodDecl) entry;
                        List<JCTree.JCAnnotation> annoList = Optional.ofNullable(method.getModifiers())
                                .map(JCTree.JCModifiers::getAnnotations)
                                .orElse(null);

                        if (CollectionUtils.isEmpty(annoList)) {
                            continue;
                        }


                        String fun = null;
                        for (JCTree.JCAnnotation anno : annoList) {
                            if ("pro.shushi.pamirs.meta.annotation.Function".equals(anno.type.toString())) {
                                if (null == fun) {
                                    fun = method.getName().toString();
                                }
                            } else if ("pro.shushi.pamirs.meta.annotation.Function.fun".equals(anno.type.toString())) {
                                if (CollectionUtils.isNotEmpty(anno.args)) {
                                    JCTree.JCExpression jcExpr = anno.args.get(0);
                                    if (null != jcExpr && JCTree.Tag.ASSIGN.equals(jcExpr.getTag())) {
                                        fun = ((JCTree.JCAssign) jcExpr).rhs.toString();
                                    }
                                }
                            }
                        }

                        if (null == fun) {
                            continue;
                        }

                        if (funcNameSet.contains(fun)) {
                            messager.printMessage(Diagnostic.Kind.ERROR, jcClassDecl.sym + " 类中,存在同名Function " + fun);
                        } else {
                            funcNameSet.add(fun);
                        }
                    }
                    funcNameSet = null;
                }
            });

        }

        return false;
    }
}
