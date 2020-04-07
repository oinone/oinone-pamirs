package pro.shushi.pamirs.fun.utils;

import com.sun.source.tree.Tree;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.code.Types;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Name;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import pro.shushi.pamirs.fun.enmu.FunType;
import pro.shushi.pamirs.fun.model.FunctionDefine;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Model;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class FunUtils {

    public static void makeFunVariable(JCTree.JCClassDecl jcClassDecl,
                                       TreeMaker treeMaker, Name.Table names, Messager messager) {
        String fun = "fun";

        // 创建fun对象
//                    JCTree.JCIdent annoType =treeMaker.Ident(getNameFromString("Resource"));
        JCTree.JCAnnotation jcAnnotation = treeMaker.Annotation(memberAccess("javax.annotation.Resource", treeMaker, names), List.nil());
        jcClassDecl.defs = jcClassDecl.defs.prepend(makeFunField(List.of(jcAnnotation), "pro.shushi.pamirs.base.call.Fun", fun, treeMaker, names));

        // 创建属性get方法
        List<JCTree.JCVariableDecl> jcVariableDeclList = List.nil();

        for (JCTree tree : jcClassDecl.defs) {
            if (tree.getKind().equals(Tree.Kind.VARIABLE)) {
                JCTree.JCVariableDecl jcVariableDecl = (JCTree.JCVariableDecl) tree;
                jcVariableDeclList = jcVariableDeclList.append(jcVariableDecl);
            }
        }

        jcVariableDeclList.forEach(jcVariableDecl -> {
            if (fun.equals(jcVariableDecl.name.toString())) {
                messager.printMessage(Diagnostic.Kind.NOTE, jcVariableDecl.getName() + " has been processed");
                JCTree.JCMethodDecl jcMethodDecl = makeGetterMethodDecl(jcClassDecl, jcVariableDecl, treeMaker, names, messager);
                if (null != jcMethodDecl) {
                    jcClassDecl.defs = jcClassDecl.defs.prepend(jcMethodDecl);
                }

            }
        });
    }

    public static FunType getFunType(JCTree.JCAnnotation jcAnnotation) {
        if (null != jcAnnotation && null != jcAnnotation.type) {
            String clazzName = ((Symbol.ClassSymbol) jcAnnotation.type.tsym).fullname.toString();
            if (clazzName.equals("pro.shushi.pamirs.meta.annotation.Action")
                    || clazzName.equals("pro.shushi.pamirs.meta.model.Extpoint")
                    || clazzName.equals("pro.shushi.pamirs.meta..annotation.Function")) {

                String enumName = org.apache.commons.lang3.StringUtils.substringAfterLast(clazzName, ".");
                return FunType.valueOf(enumName.toUpperCase());

            }
        }
        return FunType.LOCAL;
    }

    public static FunctionDefine makeFunctionDefine(Element element, JCTree.JCAnnotation jcAnnotation, JCTree tree, JCTree.JCClassDecl jcClassDecl) {

        FunctionDefine functionDefine = new FunctionDefine();

        functionDefine.setFunctionType(getFunType(jcAnnotation));

        JCTree.JCMethodDecl jcMethodDecl = (JCTree.JCMethodDecl) tree;
        String              funName      = jcMethodDecl.name.toString();

        java.util.List<String> codes = jcMethodDecl.getBody().getStatements().stream().map(item -> item.toString()).collect(Collectors.toList());
//                                    String body = jcMethodDecl.getBody().toString();
        java.util.List<String> args     = jcMethodDecl.getParameters().stream().map(item -> item.name.toString()).collect(Collectors.toList());
        java.util.List<String> argTypes = jcMethodDecl.getParameters().stream().map(item -> item.vartype.toString()).collect(Collectors.toList());

        String returnType = null != jcMethodDecl.restype && !"void".equals(jcMethodDecl.restype.toString()) ?
                jcMethodDecl.restype.toString() : null;
        String namespace = jcClassDecl.sym.fullname.toString();

        List<JCTree.JCVariableDecl> jcVariableDeclList = List.nil();
        for (JCTree vtree : jcClassDecl.defs) {
            if (vtree.getKind().equals(Tree.Kind.VARIABLE)) {
                JCTree.JCVariableDecl jcVariableDecl = (JCTree.JCVariableDecl) vtree;
                jcVariableDeclList = jcVariableDeclList.append(jcVariableDecl);
            }
        }
        StringBuilder context = new StringBuilder();
        jcVariableDeclList.forEach(jcVariableDecl -> {
            context.append(jcVariableDecl.toString()).append(";\n");
        });

        try {
            InputStream classInputStream = ((Symbol.ClassSymbol) element).classfile.openInputStream();
            String classCode = new BufferedReader(new InputStreamReader(classInputStream))
                    .lines().filter(code -> code.startsWith("import ")).collect(Collectors.joining("\n"));
            functionDefine.setImports(classCode);
        } catch (IOException e) {
            e.printStackTrace();
        }

        functionDefine.setArgs(args);
        functionDefine.setArgTypes(argTypes);
        functionDefine.setCodes(codes);
        functionDefine.setContext(context.toString());
        functionDefine.setFunName(funName);
        functionDefine.setNamespace(namespace);
        functionDefine.setReturnType(returnType);
        functionDefine.setType("JAVA");
        return functionDefine;
    }

    private static JCTree.JCVariableDecl makeFunField(List<JCTree.JCAnnotation> list, String clazz, String name, TreeMaker treeMaker, Name.Table names) {
        return treeMaker.VarDef(
                treeMaker.Modifiers(Flags.PRIVATE, list),
                getNameFromString(name, names), //名字
                memberAccess(clazz, treeMaker, names), //类型
                null //treeMaker.Literal("init value"),初始化语句
        );

    }

    public static JCTree.JCMethodDecl makeFunMethodDecl(JCTree.JCClassDecl jcClassDecl, JCTree.JCMethodDecl jcMethodDecl, TreeMaker treeMaker, Name.Table names, Messager messager) {
        treeMaker.at(jcClassDecl.pos);
        //方法的访问级别
        JCTree.JCModifiers modifiers = treeMaker.Modifiers(Flags.PUBLIC);
        //定义方法名
        Name methodName = jcMethodDecl.getName();
        //定义返回值类型
        Type restype = jcMethodDecl.restype.type;
        boolean hasReturn = !"void".equals(restype);
        if(!isModelVar(restype) && jcMethodDecl.params.stream().filter(v->isModelVar(v.getType().type)).count() == 0){
            return null;
        }
        JCTree.JCExpression returnMethodType = isModelVar(restype)?memberAccess(fetchTrueModelType(restype), treeMaker, names):jcMethodDecl.restype;
        ListBuffer<JCTree.JCStatement> statements       = new ListBuffer<>();
        //定义入参
        //设置入参
        List<JCTree.JCVariableDecl> originParameters = jcMethodDecl.params;
        JCTree.JCVariableDecl[] params = new JCTree.JCVariableDecl[originParameters.size()];
        JCTree.JCExpression[] identParams = new JCTree.JCExpression[originParameters.size()];
        int i = 0;
        int countNotModelVar = 0;
        if(CollectionUtils.isNotEmpty(originParameters)){
            String errorMsg = "Function入参中存在元数据中模型的列表且不存在元数据中模型的对象，所以需要在Function的入参最后添加\"FunctionDefinition pamirs\"以防止java泛型擦除导致无法重载,函数:";
            int countListVar = 0;
            int countObjVar = 0;
            int countMockVar = 0;
            for(JCTree.JCVariableDecl v :originParameters) {
                if (isModelVar(v.vartype.type)) {
                    Name newParamName = fetchModelVarName(v.getName().toString(), names);
                    JCTree.JCVariableDecl assignStatement = treeMaker.VarDef(
                            treeMaker.Modifiers(0),
                            v.getName(), //名字
                            v.vartype,
                            treeMaker.Apply(
                                    !isModelList(v.vartype.type)?List.of(v.vartype):List.nil(),//参数类型
                                    memberAccess("pro.shushi.pamirs.meta.common.util.ObjectMapUtils.mapToModel", treeMaker, names),
                                    List.of(treeMaker.Ident(newParamName), treeMaker.Literal(fetchTrueModelName(v.sym.type).toString()))
                            ) //初始化语句
                    );
                    statements.append(assignStatement);
                    if(isModelList(v.vartype.type)){
                        countListVar++;
                    }else{
                        countObjVar++;
                    }
                    JCTree.JCExpression paramType = memberAccess(fetchTrueModelType(v.vartype.type), treeMaker, names);
                    params[i] = treeMaker.VarDef(treeMaker.Modifiers(Flags.PARAMETER, List.nil()), newParamName, paramType, null);
                    identParams[i] = treeMaker.Ident(v.getName());
                } else {
                    if(i == originParameters.length() - 1 && 0 != countListVar && 0 == countObjVar){
                        if(v.vartype.toString().equals("Functions") && v.getName().toString().equals("pamirs")){
                            params[i] = treeMaker.VarDef(treeMaker.Modifiers(Flags.PARAMETER, List.nil()), v.getName(), memberAccess("java.util.Map", treeMaker, names), null);;
                            countMockVar++;
                        }else{
                            throw new RuntimeException(errorMsg + jcMethodDecl.getName());
                        }
                    }else{
                        params[i] = v;
                    }
                    identParams[i] = treeMaker.Ident(v.getName());
                    countNotModelVar ++;
                }
                i++;
            }
            if(0 != countListVar && 0 == countObjVar && 0 == countMockVar){
                throw new RuntimeException(errorMsg + jcMethodDecl.getName());
            }
        }

        if(hasReturn){
            String returnName = "pamirs_pamirs_pamirs_result";
            String midName = returnName;

            JCTree.JCVariableDecl invokeStatement = treeMaker.VarDef(
                    treeMaker.Modifiers(0),
                    getNameFromString(returnName, names), //名字
                    jcMethodDecl.restype, //类型
                    treeMaker.Apply(
                            List.nil(),//参数类型
                            memberAccess(methodName.toString(), treeMaker, names),
                            List.from(identParams)
                    ) //初始化语句
            );
            statements.append(invokeStatement);
            if(isModelVar(restype)){
                returnName = "pamirs_"+returnName;
                JCTree.JCVariableDecl convertResultStatement = treeMaker.VarDef(
                        treeMaker.Modifiers(0),
                        getNameFromString(returnName, names), //名字
                        memberAccess(fetchTrueModelType(restype), treeMaker, names), //类型
                        treeMaker.Apply(
                                List.nil(),//参数类型
                                memberAccess("pro.shushi.pamirs.meta.common.util.ObjectMapUtils.modelToMap", treeMaker, names),
                                List.of(treeMaker.Ident(getNameFromString(midName, names)), treeMaker.Literal(fetchTrueModelName(jcMethodDecl.restype).toString()))
                        ) //初始化语句
                );
                statements.append(convertResultStatement);
            }else{
                if(i == countNotModelVar){
                    return null;
                }
            }
            statements.append(treeMaker.Return(treeMaker.Ident(names.fromString(returnName))));
        }else{
            if(i == countNotModelVar){
                return null;
            }
            JCTree.JCStatement invokeStatement = treeMaker.Exec(
                    treeMaker.Apply(
                            List.nil(),//参数类型
                            memberAccess(methodName.toString(), treeMaker, names),
                            List.from(identParams)
                    ));
            statements.append(invokeStatement);
        }
        List<JCTree.JCVariableDecl> parameters    = List.from(params);
        List<JCTree.JCExpression>   throwsClauses = jcMethodDecl.thrown;
        List<JCTree.JCTypeParameter> methodGenericParams = List.nil();
        //定义方法体
        JCTree.JCBlock               methodBody          = treeMaker.Block(0, statements.toList());

        JCTree.JCMethodDecl decl = treeMaker.MethodDef(modifiers, methodName, returnMethodType, methodGenericParams, parameters, throwsClauses, methodBody, null);
        return decl;
    }

    private static boolean isModelVar(Type type){
        // model
        Model model = Optional.ofNullable(type.tsym).map(_notNull -> _notNull.getAnnotation(Model.class)).orElse(null);
        if(null != model){
            return Boolean.TRUE;
        }
        // model list
        if(isModelList(type)){
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    private static boolean isModelList(Type type){
        Type genericType = Optional.of(type.getModelType()).map(v->v.allparams()).filter(s->null != s && 0 != s.length()).map(_notNull->_notNull.get(0)).orElse(null);
        Model genericTypeModel = Optional.ofNullable(genericType).map(_notNull -> _notNull.getAnnotation(Model.class)).orElse(null);
        if(null != genericTypeModel){
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    private static Name fetchModelVarName(String varName, Name.Table names){
        return names.fromString("pamirs_pamirs_" + varName);
    }

    private static Type fetchTrueModelName(JCTree.JCExpression restype){
        return CollectionUtils.isNotEmpty(restype.type.getTypeArguments())?restype.type.getTypeArguments().get(0):restype.type;
    }

    private static Type fetchTrueModelName(Type type){
        return CollectionUtils.isNotEmpty(type.getTypeArguments())?type.getTypeArguments().get(0):type;
    }

    private static String fetchTrueModelType(Type restype){
        return isModelList(restype)?"java.util.List":"java.util.Map";
//        return "java.util.Map";
    }

    private static JCTree.JCExpression memberAccess(String components, TreeMaker treeMaker, Name.Table names) {
        String[]            componentArray = components.split("\\.");
        JCTree.JCExpression expr           = treeMaker.Ident(getNameFromString(componentArray[0], names));
        for (int i = 1; i < componentArray.length; i++) {
            expr = treeMaker.Select(expr, getNameFromString(componentArray[i], names));
        }
        return expr;
    }

    private static Name getNameFromString(String s, Name.Table names) {
        return names.fromString(s);
    }


    public static JCTree.JCMethodDecl makeGetterMethodDecl(JCTree.JCClassDecl jcClassDecl, JCTree.JCVariableDecl jcVariableDecl, TreeMaker treeMaker, Name.Table names, Messager messager) {
        if (hasGetter(jcClassDecl, jcVariableDecl, names)) {
            messager.printMessage(Diagnostic.Kind.NOTE, jcClassDecl.name.toString() + ":" + jcVariableDecl.getName() + "Method already exist");
            return null;
        }
        ListBuffer<JCTree.JCStatement> statements = new ListBuffer<>();
        // 从map中取值
        JCTree.JCExpression mapGet;
        if (!is_DVar(jcVariableDecl)) {
            mapGet = treeMaker.Exec(treeMaker.Apply(
                    List.nil(),//参数类型
                    memberAccess("this._d.get", treeMaker, names),
                    List.of(treeMaker.Literal(jcVariableDecl.getName().toString()))
                    )
            ).expr;
            if (isPrimitiveW(jcVariableDecl.vartype.type)) {   // 数字类型包装类
                Name                  objname    = names.fromString("obj");
                JCTree.JCIdent        obj        = treeMaker.Ident(objname);
                JCTree.JCVariableDecl objValue   = treeMaker.VarDef(treeMaker.Modifiers(0), objname, memberAccess("java.lang.Object", treeMaker, names), mapGet);
                JCTree.JCBinary       aNullEq    = treeMaker.Binary(JCTree.Tag.EQ, obj, treeMaker.Literal(TypeTag.BOT, null));
                JCTree.JCReturn       returnNull = treeMaker.Return(treeMaker.Literal(TypeTag.BOT, null));
                JCTree.JCIf           anIf       = treeMaker.If(aNullEq, returnNull, null);
                JCTree.JCReturn lastReturn = treeMaker.Return(treeMaker.Apply(
                        List.nil(),//参数类型
                        memberAccess(jcVariableDecl.vartype.toString() + ".valueOf", treeMaker, names),
                        List.of(treeMaker.Exec(treeMaker.Apply(
                                List.nil(),
                                memberAccess("obj.toString", treeMaker, names),
                                List.nil()
                        )).expr)
                ));
                statements.append(objValue).append(anIf).append(lastReturn);
            } else if (isBigDecimal(jcVariableDecl.vartype.type)) {  // BigDecimal
                Name                      objname       = names.fromString("obj");
                JCTree.JCIdent            obj           = treeMaker.Ident(objname);
                JCTree.JCVariableDecl     objValue      = treeMaker.VarDef(treeMaker.Modifiers(0), objname, memberAccess("java.lang.Object", treeMaker, names), mapGet);
                JCTree.JCBinary           aNullEq       = treeMaker.Binary(JCTree.Tag.EQ, obj, treeMaker.Literal(TypeTag.BOT, null));
                JCTree.JCReturn           returnNull    = treeMaker.Return(treeMaker.Literal(TypeTag.BOT, null));
                JCTree.JCIf               anIf          = treeMaker.If(aNullEq, returnNull, null);
                JCTree.JCExpression       bigDecimal    = treeMaker.Ident(names.fromString("BigDecimal"));
                List<JCTree.JCExpression> objToString   = List.of(treeMaker.Exec(treeMaker.Apply(List.nil(), memberAccess("obj.toString", treeMaker, names), List.nil())).expr);
                JCTree.JCNewClass         newBigDecimal = treeMaker.NewClass(null, List.nil(), bigDecimal, objToString, null);
                JCTree.JCReturn           lastReturn    = treeMaker.Return(newBigDecimal);
                statements.append(objValue).append(anIf).append(lastReturn);
            } else {
                statements.append(treeMaker.Return(treeMaker.TypeCast(jcVariableDecl.vartype.type, mapGet)));
            }

        } else {
            mapGet = memberAccess("this._d", treeMaker, names);
            statements.append(treeMaker.Return(mapGet));
        }

        JCTree.JCBlock body = treeMaker.Block(0, statements.toList());
        treeMaker.at(jcClassDecl.pos);
        JCTree.JCMethodDecl decl = treeMaker.MethodDef(treeMaker.Modifiers(Flags.PUBLIC), getMethodName(jcVariableDecl, names), jcVariableDecl.vartype, List.nil(), List.nil(), List.nil(), body, null);
        return decl;
    }

    private static boolean isPrimitiveW(Type type) {
        String s = type.toString();
        return s.equals(Long.class.getName())
                || s.equals(Float.class.getName())
                || s.equals(Double.class.getName())
                || s.equals(Short.class.getName())
                || s.equals(Byte.class.getName())
                || s.equals(Integer.class.getName());

    }

    private static boolean isBigDecimal(Type type){
        String s = type.toString();
        return s.equals(BigDecimal.class.getName());
    }

    public static JCTree.JCVariableDecl makeSlf4jVariableDecl(JCTree.JCClassDecl jcClassDecl, TreeMaker treeMaker, Name.Table names, Messager messager, Context context) {

        try {
            JCTree.JCModifiers modifiers = treeMaker.Modifiers(26);// PRIVATE + STATIC + FINAL

            Types types = Types.instance(context);
//            Type  erasure = types.erasure(jcClassDecl.sym.type);
            Type erasure = jcClassDecl.sym.type.getTypeArguments().size() == 0
                    ? jcClassDecl.sym.type
                    : types.erasure(jcClassDecl.sym.type);// jcClassDecl.sym.erasure_field;
            JCTree.JCMethodInvocation apply          = treeMaker.Apply(List.nil(), memberAccess("org.slf4j.LoggerFactory.getLogger", treeMaker, names), List.of(treeMaker.ClassLiteral(erasure)));
            JCTree.JCVariableDecl     jcVariableDecl = treeMaker.VarDef(modifiers, names.fromString("log"), memberAccess(Logger.class.getName(), treeMaker, names), apply);
            messager.printMessage(Diagnostic.Kind.NOTE, jcVariableDecl.toString());
            return jcVariableDecl;
        } catch (Exception e) {
            messager.printMessage(Diagnostic.Kind.ERROR, e.getMessage());
            return null;
        }
    }

    public static JCTree.JCMethodDecl makeSetterMethodDecl(JCTree.JCClassDecl jcClassDecl, JCTree.JCVariableDecl jcVariableDecl, TreeMaker treeMaker, Name.Table names, Messager messager) {
        try {
            if (hasSetter(jcClassDecl, jcVariableDecl, names)) {
                messager.printMessage(Diagnostic.Kind.NOTE, jcClassDecl.name.toString() + ":" + jcVariableDecl.getName() + " Method already exist");
                return null;
            }
            boolean isChain = Boolean.TRUE;
            //方法的访问级别
            JCTree.JCModifiers modifiers = treeMaker.Modifiers(Flags.PUBLIC);
            //定义方法名
            Name methodName = setMethodName(jcVariableDecl.getName(), names);
            //定义返回值类型
            JCTree.JCExpression            returnMethodType = isChain ? treeMaker.Type(jcClassDecl.sym.type) : treeMaker.Type((Type) (Class.forName("com.sun.tools.javac.code.Type$JCVoidType").newInstance()));
            ListBuffer<JCTree.JCStatement> statements       = new ListBuffer<>();
            JCTree.JCStatement             mapPut;
            if (!is_DVar(jcVariableDecl)) {
                // 将值放入map中
                mapPut = treeMaker.Exec(treeMaker.Apply(
                        List.nil(),//参数类型
                        memberAccess("this._d.put", treeMaker, names),
                        List.of(treeMaker.Literal(jcVariableDecl.getName().toString()), treeMaker.Ident(jcVariableDecl.getName()))
                        )
                );
            } else {
                mapPut = treeMaker.Exec(treeMaker.Assign(memberAccess("this._d", treeMaker, names),
                        memberAccess(jcVariableDecl.name.toString(), treeMaker, names)));
            }
            statements.append(mapPut);
            if (isChain) {
                statements.append(treeMaker.Return(treeMaker.Ident(names.fromString("this"))));
            }
//            statements.append(treeMaker.Exec(treeMaker.Assign(treeMaker.Select(treeMaker.Ident(names.fromString("this")), jcVariableDecl.getName()), treeMaker.Ident(jcVariableDecl.getName()))));
            //定义方法体
            JCTree.JCBlock               methodBody          = treeMaker.Block(0, statements.toList());
            List<JCTree.JCTypeParameter> methodGenericParams = List.nil();
            //定义入参
            JCTree.JCVariableDecl param = treeMaker.VarDef(treeMaker.Modifiers(Flags.PARAMETER, List.nil()), jcVariableDecl.name, jcVariableDecl.vartype, null);
            //设置入参
            List<JCTree.JCVariableDecl> parameters    = List.of(param);
            List<JCTree.JCExpression>   throwsClauses = List.nil();
            //构建新方法
            treeMaker.at(jcClassDecl.pos);
            JCTree.JCMethodDecl decl = treeMaker.MethodDef(modifiers, methodName, returnMethodType, methodGenericParams, parameters, throwsClauses, methodBody, null);
            return decl;
        } catch (Exception e) {
            System.err.println(e);
        }
        return null;
    }

    public static boolean hasSetter(JCTree.JCClassDecl jcClassDecl, JCTree.JCVariableDecl jcVariableDecl, Name.Table names) {
        Name name  = setMethodName(jcVariableDecl.name, names);
        long count = jcClassDecl.defs.stream().filter(_jcDef -> Tree.Kind.METHOD.equals(_jcDef.getKind())).map(_jcDef -> (JCTree.JCMethodDecl) _jcDef).filter(_jcMetDef -> name.equals(_jcMetDef.name)).count();
        return count > 0;
    }

    public static boolean hasGetter(JCTree.JCClassDecl jcClassDecl, JCTree.JCVariableDecl jcVariableDecl, Name.Table names) {
        Name name  = getMethodName(jcVariableDecl, names);
        long count = jcClassDecl.defs.stream().filter(_jcDef -> Tree.Kind.METHOD.equals(_jcDef.getKind())).map(_jcDef -> (JCTree.JCMethodDecl) _jcDef).filter(_jcMetDef -> name.equals(_jcMetDef.name)).count();
        return count > 0;
    }

    public static JCTree.JCMethodDecl makeClassicsGetterMethodDecl(JCTree.JCClassDecl jcClassDecl, JCTree.JCVariableDecl jcVariableDecl, TreeMaker treeMaker, Name.Table names, Messager messager) {

        JCTree.JCExpression            mapGet     = treeMaker.Select(treeMaker.Ident(getNameFromString("this", names)), jcVariableDecl.name);
        ListBuffer<JCTree.JCStatement> statements = new ListBuffer<>();
        statements.append(treeMaker.Return(mapGet));
        JCTree.JCBlock body = treeMaker.Block(0, statements.toList());
        treeMaker.at(jcClassDecl.pos);
        JCTree.JCMethodDecl decl = treeMaker.MethodDef(treeMaker.Modifiers(Flags.PUBLIC), getMethodName(jcVariableDecl, names), jcVariableDecl.vartype, List.nil(), List.nil(), List.nil(), body, null);
        return decl;
    }

    public static JCTree.JCMethodDecl makeClassicsSetterMethodDecl(JCTree.JCClassDecl jcClassDecl, JCTree.JCVariableDecl jcVariableDecl, TreeMaker treeMaker, Name.Table names, Messager messager) {
        try {
            boolean isChain = Boolean.TRUE;
            //方法的访问级别
            JCTree.JCModifiers modifiers = treeMaker.Modifiers(Flags.PUBLIC);
            //定义方法名
            Name methodName = setMethodName(jcVariableDecl.getName(), names);
            //定义返回值类型
            JCTree.JCExpression returnMethodType = isChain
                    ? treeMaker.Type(jcClassDecl.sym.type)
                    : treeMaker.Type((Type) (Class.forName("com.sun.tools.javac.code.Type$JCVoidType").newInstance()));
            ListBuffer<JCTree.JCStatement> statements = new ListBuffer<>();
            JCTree.JCStatement             mapPut     = treeMaker.Exec(treeMaker.Assign(treeMaker.Select(treeMaker.Ident(getNameFromString("this", names)), jcVariableDecl.name), treeMaker.Ident(jcVariableDecl.getName())));
            statements.append(mapPut);
            if (isChain) {
                statements.append(treeMaker.Return(treeMaker.Ident(names.fromString("this"))));
            }
            JCTree.JCBlock               methodBody          = treeMaker.Block(0, statements.toList());
            List<JCTree.JCTypeParameter> methodGenericParams = List.nil();
            JCTree.JCVariableDecl        param               = treeMaker.VarDef(treeMaker.Modifiers(Flags.PARAMETER, List.nil()), jcVariableDecl.name, jcVariableDecl.vartype, null);
            List<JCTree.JCVariableDecl>  parameters          = List.of(param);
            List<JCTree.JCExpression>    throwsClauses       = List.nil();
            treeMaker.at(jcClassDecl.pos);
            JCTree.JCMethodDecl decl = treeMaker.MethodDef(modifiers, methodName, returnMethodType, methodGenericParams, parameters, throwsClauses, methodBody, null);
            return decl;
        } catch (Exception e) {
            messager.printMessage(Diagnostic.Kind.ERROR, e.getMessage());
        }
        return null;
    }

    public static JCTree.JCMethodDecl makeUnSetterMethodDecl(JCTree.JCClassDecl jcClassDecl, JCTree.JCVariableDecl jcVariableDecl, TreeMaker treeMaker, Name.Table names) {

        try {
            boolean isChain = Boolean.TRUE;
            //方法的访问级别
            JCTree.JCModifiers modifiers = treeMaker.Modifiers(Flags.PUBLIC);
            //定义方法名
            Name methodName = unsetMethodName(jcVariableDecl.getName(), names);
            //定义返回值类型
            JCTree.JCExpression            returnMethodType = isChain ? treeMaker.Type(jcClassDecl.sym.type) : treeMaker.Type((Type) (Class.forName("com.sun.tools.javac.code.Type$JCVoidType").newInstance()));
            ListBuffer<JCTree.JCStatement> statements       = new ListBuffer<>();
            JCTree.JCStatement             mapPut;
            if (!is_DVar(jcVariableDecl)) {
                // 将值放入map中
                mapPut = treeMaker.Exec(treeMaker.Apply(
                        List.nil(),//参数类型
                        memberAccess("this._d.remove", treeMaker, names),
                        List.of(treeMaker.Literal(jcVariableDecl.getName().toString()))
                        )
                );
            } else {
                mapPut = treeMaker.Exec(treeMaker.Apply(
                        List.nil(),//参数类型
                        memberAccess("this._d.clear", treeMaker, names),
                        List.nil()));
            }
            statements.append(mapPut);
            if (isChain) {
                statements.append(treeMaker.Return(treeMaker.Ident(names.fromString("this"))));
            }
            //定义方法体
            JCTree.JCBlock               methodBody          = treeMaker.Block(0, statements.toList());
            List<JCTree.JCTypeParameter> methodGenericParams = List.nil();
            //定义入参
//            JCTree.JCVariableDecl param = treeMaker.VarDef(treeMaker.Modifiers(Flags.PARAMETER, List.nil()), jcVariableDecl.name, jcVariableDecl.vartype, null);
            //设置入参
//            List<JCTree.JCVariableDecl> parameters    = is_DVar(jcVariableDecl) ? List.nil() : List.of(param);
            List<JCTree.JCExpression>   throwsClauses = List.nil();
            //构建新方法
            treeMaker.at(jcClassDecl.pos);
            JCTree.JCMethodDecl decl = treeMaker.MethodDef(modifiers, methodName, returnMethodType, methodGenericParams, List.nil(), throwsClauses, methodBody, null);
            return decl;
        } catch (Exception e) {
            System.err.println(e);
        }
        return null;
    }

    private static Boolean is_DVar(JCTree.JCVariableDecl jcVariableDecl) {
        return jcVariableDecl.sym.name.toString().equals("_d") ? true : false;
    }

    private static Name getMethodName(JCTree.JCVariableDecl jcVariableDecl, Name.Table names) {
        Name   name = jcVariableDecl.name;
        String s    = name.toString();
        if (jcVariableDecl.sym.type.toString().equalsIgnoreCase("boolean")) {
            return names.fromString("is" + s.substring(0, 1).toUpperCase() + s.substring(1, name.length()));
        } else {
            return names.fromString("get" + s.substring(0, 1).toUpperCase() + s.substring(1, name.length()));
        }
    }

    private static Name setMethodName(Name name, Name.Table names) {
        String s = name.toString();
        return names.fromString("set" + s.substring(0, 1).toUpperCase() + s.substring(1, name.length()));
    }

    private static Name unsetMethodName(Name name, Name.Table names) {
        String s = name.toString();
        return names.fromString("unset" + s.substring(0, 1).toUpperCase() + s.substring(1, name.length()));
    }

    public static JCTree.JCMethodDecl makeAllArgsConstructMethodDecl(JCTree.JCClassDecl jcClassDecl, TreeMaker treeMaker, Name.Table names, Messager messager, Boolean isClassics) {
        java.util.List<JCTree.JCVariableDecl> collect = jcClassDecl.defs.stream().filter(_def -> Tree.Kind.VARIABLE.equals(_def.getKind())).map(_var -> (JCTree.JCVariableDecl) _var).collect(Collectors.toList());
        return makeConstructMethodDecl(jcClassDecl, treeMaker, names, messager, collect, isClassics);
    }

    public static JCTree.JCMethodDecl makeNoArgsConstructMethodDecl(JCTree.JCClassDecl jcClassDecl, TreeMaker treeMaker, Name.Table names, Messager messager, Boolean isClassics) {
        return makeConstructMethodDecl(jcClassDecl, treeMaker, names, messager, new ArrayList<>(0), isClassics);
    }

    private static JCTree.JCMethodDecl makeConstructMethodDecl(JCTree.JCClassDecl jcClassDecl, TreeMaker treeMaker, Name.Table names, Messager messager, java.util.List<JCTree.JCVariableDecl> collect, Boolean isClassics) {
        if (hasConstruct(jcClassDecl, names, collect.toArray(new JCTree.JCVariableDecl[0]))) {
            messager.printMessage(Diagnostic.Kind.WARNING, jcClassDecl.name.toString() + ".<init>() Method already exist contains parameters " + collect.toArray());
            return null;
        }
        // 定义 修饰符
        JCTree.JCModifiers modifiers = treeMaker.Modifiers(Flags.PUBLIC);
        // 定义 方法名
        Name                           allConstructName = names.fromString("<init>");
        ListBuffer<JCTree.JCStatement> statements       = new ListBuffer<>();
        List<JCTree.JCVariableDecl>    jcVariableDecls  = List.nil();
        for (JCTree.JCVariableDecl variableDecl : collect) {
            if (Flags.isStatic(variableDecl.sym) || Flags.isConstant(variableDecl.sym)) continue;
            jcVariableDecls = jcVariableDecls.append(treeMaker.VarDef(treeMaker.Modifiers(Flags.PARAMETER), variableDecl.name, variableDecl.vartype, null));
            JCTree.JCStatement mapPut;

            if (!isClassics) {
                mapPut = treeMaker.Exec(treeMaker.Apply(
                        List.nil(),//参数类型
                        memberAccess("this._d.put", treeMaker, names),
                        List.of(treeMaker.Literal(variableDecl.getName().toString()), treeMaker.Ident(variableDecl.getName())))
                );
            } else {
                mapPut = treeMaker.Exec(treeMaker.Assign(treeMaker.Select(treeMaker.Ident(getNameFromString("this", names)), variableDecl.name), treeMaker.Ident(variableDecl.getName())));
            }
            statements.append(mapPut);
        }
        JCTree.JCBlock block = treeMaker.Block(0, statements.toList());
        treeMaker.at(jcClassDecl.pos);
        JCTree.JCMethodDecl decl = treeMaker.MethodDef(modifiers, allConstructName, null, List.nil(), jcVariableDecls, List.nil(), block, null);

        return decl;
    }

    private static boolean hasConstruct(JCTree.JCClassDecl jcClassDecl, Name.Table name, JCTree.JCVariableDecl... jcVariableDecls) {
        Boolean hasConstruct = false;

        Map<String, JCTree.JCVariableDecl> map = new HashMap<>();
        for (JCTree.JCVariableDecl jcVariableDecl : jcVariableDecls) {
            if ((jcVariableDecl.getModifiers().flags & Flags.FINAL) != Flags.FINAL && (jcVariableDecl.getModifiers().flags & Flags.STATIC) != Flags.STATIC) {
                map.put(jcVariableDecl.getName().toString(), jcVariableDecl);
            }
        }
        java.util.List<JCTree.JCMethodDecl> collect = jcClassDecl.defs.stream()
                .filter(_jcMemberdef -> Tree.Kind.METHOD.equals(_jcMemberdef.getKind()))
                .map(_jcMethod -> (JCTree.JCMethodDecl) _jcMethod)
                .filter(_jcMethod -> _jcMethod.name.equals(name.fromString("<init>")))
                .collect(Collectors.toList());

        for (JCTree.JCMethodDecl methodDecl : collect) {
            java.util.List<String> names = new ArrayList<>();
            for (JCTree.JCVariableDecl param : methodDecl.params) {
                if (map.containsKey(param.name.toString())) {
                    names.add(param.name.toString());
                }
            }
            hasConstruct = names.size() == map.size();
        }
        return hasConstruct;
    }

    public static boolean hasConstruct(JCTree.JCClassDecl jcClassDecl) {
        long count = jcClassDecl
                .defs
                .stream()
                .filter(_jcDef -> Tree.Kind.METHOD.equals(_jcDef.getKind()))
                .map(_jcDef -> (JCTree.JCMethodDecl) _jcDef)
                .filter(_jcMetDef -> "<init>".equals(_jcMetDef.name.toString()))
                .filter(_jcMetDef -> _jcMetDef.params.length()>0)
                .count();
        return count > 0;
    }

}
