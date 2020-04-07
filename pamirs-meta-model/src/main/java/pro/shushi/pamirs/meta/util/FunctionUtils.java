package pro.shushi.pamirs.meta.util;

import com.alibaba.fastjson.JSON;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.dto.fun.Arg;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.api.dto.fun.VarType;
import pro.shushi.pamirs.meta.domain.fun.Argument;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;
import pro.shushi.pamirs.meta.domain.fun.Type;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 函数工具类
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/4 2:20 下午
 */
@Slf4j
public class FunctionUtils {

    private static Cache<Object, Object> objectMethodsCache = CacheBuilder
            .newBuilder().initialCapacity(50).build();

    public static Object fetchDynamicParameter(Object params, int paramCount){
        if(null == params){
            return null;
        }else if(params.getClass().isArray()){
            if(0 == paramCount){
                return null;
            }else if(1 == paramCount){
                return ((Object[])params)[0];
            }else{
                return params;
            }
        }else{
            return params;
        }
    }

    public static Method getMapMethod(Function function, Object... args) throws ClassNotFoundException {
        String className = function.getClazz();
        String methodName = function.getMethod();
        return getMethod(className, methodName + (isMapArgTypes(function.getArguments(), args)?"ForPamirsMap":""), fetchMapArgTypes(function.getArguments(), args), args);
    }

    public static Method getMethod(Function function, Object... args) throws ClassNotFoundException {
        String className = function.getClazz();
        String methodName = function.getMethod();
        return getMethod(className, methodName, fetchArgTypes(function.getArguments()), args);
    }

    public static Method getMethod(FunctionDefinition functionDefinition) throws ClassNotFoundException {
        String className = functionDefinition.getClazz();
        String methodName = functionDefinition.getMethod();
        String[] argTypes = CollectionUtils.isEmpty(functionDefinition.getArgumentList())?new String[]{}:fetchArgumentTypes(functionDefinition.getArgumentList());
        return getMethod(className, methodName, argTypes, null);
    }

    private static Method getMethod(String className, String methodName, String[] argTypes, Object[] args) throws ClassNotFoundException {
        Class modelClass = Class.forName(className);
        Class[] argClasses = MethodUtils.getClasses(null == argTypes ? args : argTypes);
        String methodKey = className + ":" + methodName + ":" + DigestUtils.md5DigestAsHex(JSON.toJSONBytes(argClasses));
        Method method = (Method) objectMethodsCache
                .getIfPresent(methodKey);

        if (null == method) {
            try {
                method = modelClass.getMethod(methodName, argClasses);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
            if (null == method) {
                for (Method tempMethod : modelClass.getMethods()) {
                    if (tempMethod.getName().equals(methodName)) {
                        method = tempMethod;
                        break;
                    }
                }
            }
            objectMethodsCache.put(methodKey, method);
        }
        return method;
    }

    public static List<Arg> fetchArgumentList(List<Argument> argumentList){
        List<Arg> argList = new ArrayList<>();
        Arg arg;
        for(Argument argument : argumentList){
            arg = new Arg();
            arg.setName(argument.getName()).setTtype(argument.getTtype().value()).setLtype(argument.getLtype()).setLtypeT(argument.getLtypeT())
                    .setMulti(argument.getMulti())
                    .setModel(argument.getModel()).setDictionary(argument.getDictionary());
            argList.add(arg);
        }
        return argList;
    }

    public static List<String> fetchArgumentNameList(List<Argument> argumentList){
        List<String> argNameList = new ArrayList<>();
        for(Argument argument : argumentList){
            argNameList.add(argument.getName());
        }
        return argNameList;
    }

    public static String[] fetchArgumentNames(List<Argument> argumentList){
        List<String> argNameList = fetchArgumentNameList(argumentList);
        String[] argNames = new String[argNameList.size()];
        argNameList.toArray(argNames);
        return argNames;
    }

    public static String[] fetchArgumentTypes(List<Argument> argumentList){
        List<String> argTypeList = new ArrayList<>();
        for(Argument argument : argumentList){
            argTypeList.add(argument.getLtype());
        }
        String[] argTypes = new String[argTypeList.size()];
        argTypeList.toArray(argTypes);
        return argTypes;
    }

    public static List<String> fetchArgNameList(List<Arg> argumentList){
        List<String> argNameList = new ArrayList<>();
        for(Arg argument : argumentList){
            argNameList.add(argument.getName());
        }
        return argNameList;
    }

    public static String[] fetchArgNames(List<Arg> argumentList){
        List<String> argNameList = fetchArgNameList(argumentList);
        String[] argNames = new String[argNameList.size()];
        argNameList.toArray(argNames);
        return argNames;
    }

    public static String[] fetchArgTypes(List<Arg> argumentList){
        List<String> argTypeList = new ArrayList<>();
        for(Arg argument : argumentList){
            argTypeList.add(argument.getLtype());
        }
        String[] argTypes = new String[argTypeList.size()];
        argTypeList.toArray(argTypes);
        return argTypes;
    }

    public static String[] fetchMapArgTypes(List<Arg> argumentList, Object[] args){
        List<String> argTypeList = new ArrayList<>();
        int i = 0;
        for(Arg argument : argumentList){
            if(TypeUtils.isModelClass(argument.getLtype()) && args[i] != null && args[i] instanceof Map){
                argTypeList.add(Map.class.getName());
            }else{
                argTypeList.add(argument.getLtype());
            }
            i++;
        }
        String[] argTypes = new String[argTypeList.size()];
        argTypeList.toArray(argTypes);
        return argTypes;
    }

    public static boolean isMapArgTypes(List<Arg> argumentList, Object[] args){
        int i = 0;
        for(Arg argument : argumentList){
            if(TypeUtils.isModelClass(argument.getLtype()) && args[i] != null && args[i] instanceof Map){
                return Boolean.TRUE;
            }
            i++;
        }
        return Boolean.FALSE;
    }

    public static VarType fetchReturnType(Type type){
        return new Arg().setTtype(type.getTtype().value()).setLtype(type.getLtype()).setLtypeT(type.getLtypeT())
                .setMulti(type.getMulti())
                .setModel(type.getModel()).setDictionary(type.getDictionary());
    }

    public static String[] serialize(Object[] args) throws IOException {
        if(ArrayUtils.isEmpty(args)){
            return new String[0];
        }
        String[] argSerials = new String[args.length];
        int cursor = 0;
        for(Object o : args){
            argSerials[cursor] = String.class.equals(o.getClass())?(String)o:JsonUtils.toJSONString(o);
            cursor++;
        }
        return argSerials;
    }

    public static Object[] unserialize(String[] args, Class[] argTypes) {
        if(ArrayUtils.isEmpty(args)){
            return new Object[0];
        }
        Object[] argSerials = new Object[args.length];
        int cursor = 0;
        for(String o : args){
            argSerials[cursor] = String.class.equals(argTypes[cursor])?o: JsonUtils.parseObject(o, argTypes[cursor]);
            cursor++;
        }
        return argSerials;
    }

    public static Object[] unserialize(String[] args, String[] argTypes) throws ClassNotFoundException {
        if(ArrayUtils.isEmpty(args)){
            return new Object[0];
        }
        Object[] argSerials = new Object[args.length];
        int cursor = 0;
        for(String o : args){
            argSerials[cursor] = String.class.getName().equals(argTypes[cursor]) ? o : JsonUtils.parseObject(o, Class.forName(argTypes[cursor]));
            cursor++;
        }
        return argSerials;
    }

}
