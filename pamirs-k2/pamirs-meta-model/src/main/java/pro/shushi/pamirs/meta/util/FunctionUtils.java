package pro.shushi.pamirs.meta.util;

import com.alibaba.fastjson.JSON;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import pro.shushi.k2.fun.utils.FunUtils;
import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.NoCode;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.fun.Arg;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.api.dto.fun.VarType;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.base.D;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.util.EnumUtils;
import pro.shushi.pamirs.meta.common.util.ListUtils;
import pro.shushi.pamirs.meta.domain.fun.Argument;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;
import pro.shushi.pamirs.meta.domain.fun.Type;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static pro.shushi.pamirs.meta.enmu.MetaExpEnumerate.*;
import static pro.shushi.pamirs.meta.enmu.TtypeEnum.M2O;
import static pro.shushi.pamirs.meta.enmu.TtypeEnum.O2M;

/**
 * 函数工具类
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/4 2:20 下午
 */
@Slf4j
public class FunctionUtils {

    private static final Cache<Object, Object> objectMethodsCache = Caffeine
            .newBuilder().initialCapacity(50).build();

    public static Object fetchDynamicParameter(Object params, int paramCount) {
        if (null == params) {
            return null;
        } else if (params.getClass().isArray()) {
            if (0 == paramCount) {
                return params;
            } else if (1 == paramCount) {
                return ((Object[]) params)[0];
            } else {
                return params;
            }
        } else {
            return params;
        }
    }

    public static Method getMapMethod(Function function, Object... args) throws ClassNotFoundException {
        String className = function.getClazz();
        String methodName = function.getMethod();
        return getMethod(className, methodName + (isMapArgTypes(function.getArguments(), args) ? "ForPamirsMap" : ""), fetchMapArgTypes(function.getArguments(), args), args);
    }

    public static Method getMethod(Function function, Object... args) throws ClassNotFoundException {
        String className = function.getClazz();
        String methodName = function.getMethod();
        return getMethod(className, methodName, fetchArgTypes(function.getArguments()), args);
    }

    public static Method getMethod(FunctionDefinition functionDefinition) throws ClassNotFoundException {
        String className = functionDefinition.getClazz();
        String methodName = functionDefinition.getMethod();
        String[] argTypes = CollectionUtils.isEmpty(functionDefinition.getArgumentList()) ? new String[]{} : fetchArgumentTypes(functionDefinition.getArgumentList());
        return getMethod(className, methodName, argTypes, null);
    }

    private static Method getMethod(String className, String methodName, String[] argTypes, Object[] args) throws ClassNotFoundException {
        Class<?> clazz = Class.forName(className);
        return getMethod(clazz, methodName, argTypes, args);
    }

    public static Method getMethod(Class<?> clazz, String methodName, String[] argTypes, Object[] args) {
        Object[] params = null == argTypes
                || (ListUtils.isNoNullArray(args) && isOnlyPrimaryParamDefine(argTypes)) ? args : argTypes;
        Class<?>[] argClasses = MethodUtils.getClasses(params);
        String methodKey = clazz.getName() + ":" + methodName + ":" + DigestUtils.md5DigestAsHex(JSON.toJSONBytes(argClasses));
        Method method = (Method) objectMethodsCache
                .getIfPresent(methodKey);

        if (null == method) {
            try {
                method = clazz.getDeclaredMethod(methodName, argClasses);
            } catch (NoSuchMethodException e) {
                String methodSign = clazz.getName() + CharacterConstants.SEPARATOR_DOT
                        + methodName + CharacterConstants.LEFT_BRACKET
                        + StringUtils.join(argClasses, CharacterConstants.SEPARATOR_COMMA) + CharacterConstants.RIGHT_BRACKET;
                log.warn(EnumUtils.error(BASE_FETCH_METHOD_ERROR) + ", method: {}", methodSign);
            }
            if (null == method) {
                for (Method tempMethod : clazz.getMethods()) {
                    if (tempMethod.getName().equals(methodName)) {
                        method = tempMethod;
                        break;
                    }
                }
            }
            if (null != method) {
                objectMethodsCache.put(methodKey, method);
            }
        }
        return method;
    }

    private static boolean isOnlyPrimaryParamDefine(String[] argTypes) {
        boolean onlyPrimaryParamDefine = true;
        for (String argType : argTypes) {
            if (!TypeUtils.isBaseTypeWithoutString(argType)) {
                onlyPrimaryParamDefine = false;
                break;
            }
        }
        return onlyPrimaryParamDefine;
    }

    public static List<Arg> fetchArgumentList(List<Argument> argumentList) {
        List<Arg> argList = new ArrayList<>();
        if (null == argumentList) {
            return argList;
        }
        Arg arg;
        for (Argument argument : argumentList) {
            arg = new Arg();
            arg.setName(argument.getName()).setTtype(argument.getTtype().value()).setLtype(argument.getLtype()).setLtypeT(argument.getLtypeT())
                    .setMulti(argument.getMulti())
                    .setModel(argument.getModel()).setDictionary(argument.getDictionary());
            argList.add(arg);
        }
        return argList;
    }

    public static List<String> fetchArgumentNameList(List<Argument> argumentList) {
        List<String> argNameList = new ArrayList<>();
        for (Argument argument : argumentList) {
            argNameList.add(argument.getName());
        }
        return argNameList;
    }

    public static String[] fetchArgumentNames(List<Argument> argumentList) {
        List<String> argNameList = fetchArgumentNameList(argumentList);
        String[] argNames = new String[argNameList.size()];
        argNameList.toArray(argNames);
        return argNames;
    }

    public static String[] fetchArgumentTypes(List<Argument> argumentList) {
        List<String> argTypeList = new ArrayList<>();
        for (Argument argument : argumentList) {
            argTypeList.add(argument.getLtype());
        }
        String[] argTypes = new String[argTypeList.size()];
        argTypeList.toArray(argTypes);
        return argTypes;
    }

    public static List<String> fetchArgNameList(List<Arg> argumentList) {
        if (null == argumentList) {
            return null;
        }
        List<String> argNameList = new ArrayList<>();
        for (Arg argument : argumentList) {
            argNameList.add(argument.getName());
        }
        return argNameList;
    }

    public static String[] fetchArgNames(List<Arg> argumentList) {
        List<String> argNameList = fetchArgNameList(argumentList);
        if (null == argNameList) {
            return new String[]{};
        }
        String[] argNames = new String[argNameList.size()];
        argNameList.toArray(argNames);
        return argNames;
    }

    public static String[] fetchArgTypes(List<Arg> argumentList) {
        if (null == argumentList) {
            return new String[]{};
        }
        List<String> argTypeList = new ArrayList<>();
        for (Arg argument : argumentList) {
            argTypeList.add(argument.getLtype());
        }
        String[] argTypes = new String[argTypeList.size()];
        argTypeList.toArray(argTypes);
        return argTypes;
    }

    public static String[] fetchMapArgTypes(List<Arg> argumentList, Object[] args) {
        List<String> argTypeList = new ArrayList<>();
        int i = 0;
        for (Arg argument : argumentList) {
            if (TypeUtils.isModelClass(argument.getLtype()) && args[i] != null && args[i] instanceof Map) {
                argTypeList.add(Map.class.getName());
            } else {
                argTypeList.add(argument.getLtype());
            }
            i++;
        }
        String[] argTypes = new String[argTypeList.size()];
        argTypeList.toArray(argTypes);
        return argTypes;
    }

    public static boolean isMapArgTypes(List<Arg> argumentList, Object[] args) {
        if (null != argumentList) {
            int i = 0;
            for (Arg argument : argumentList) {
                if (TypeUtils.isModelClass(argument.getLtype()) && args[i] != null && args[i] instanceof Map) {
                    return Boolean.TRUE;
                }
                i++;
            }
        }
        return Boolean.FALSE;
    }

    public static VarType fetchReturnType(Type type) {
        if (null == type) {
            return null;
        }
        return new Arg().setTtype(type.getTtype().value())
                .setLtype(type.getLtype()).setLtypeT(type.getLtypeT())
                .setMulti(type.getMulti())
                .setModel(type.getModel()).setDictionary(type.getDictionary());
    }

    public static String[] serialize(Object[] args) {
        if (ArrayUtils.isEmpty(args)) {
            return new String[0];
        }
        String[] argSerials = new String[args.length];
        int cursor = 0;
        for (Object o : args) {
            argSerials[cursor] = String.class.equals(o.getClass()) ? (String) o : JsonUtils.toJSONString(o);
            cursor++;
        }
        return argSerials;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Object[] unserialize(String[] args, Class[] argTypes) {
        if (ArrayUtils.isEmpty(args)) {
            return new Object[0];
        }
        Object[] argSerials = new Object[args.length];
        int cursor = 0;
        for (String o : args) {
            argSerials[cursor] = String.class.equals(argTypes[cursor]) ? o : JsonUtils.parseObject(o, argTypes[cursor]);
            cursor++;
        }
        return argSerials;
    }

    public static Object[] unserialize(String[] args, String[] argTypes) throws ClassNotFoundException {
        if (ArrayUtils.isEmpty(args)) {
            return new Object[0];
        }
        Object[] argSerials = new Object[args.length];
        int cursor = 0;
        for (String o : args) {
            argSerials[cursor] = String.class.getName().equals(argTypes[cursor]) ? o : JsonUtils.parseObject(o, Class.forName(argTypes[cursor]));
            cursor++;
        }
        return argSerials;
    }

    public static List<Argument> convertArgumentList(Method method) {
        String[] argNames = MethodUtils.getArgNames(method);
        Class<?>[] types = method.getParameterTypes();
        java.lang.reflect.Type[] genericTypes = method.getGenericParameterTypes();
        List<Argument> argumentList = new ArrayList<>();
        if (null != argNames) {
            int i = 0;
            for (String argName : argNames) {
                Argument argument = new Argument();
                argument.setName(argName);
                convertTypeFromMethod(method.getDeclaringClass(), types[i], genericTypes[i], argument);
                argumentList.add(argument);
                i++;
            }
        }
        return argumentList;
    }

    public static Type convertReturnType(Method method) {
        Type type = new Type();
        Class<?> ltype = method.getReturnType();
        java.lang.reflect.Type genericType = method.getGenericReturnType();
        convertTypeFromMethod(method.getDeclaringClass(), ltype, genericType, type);
        return type;
    }

    public static String convertMethodName(Method method) {
        Class<?> declaringClass = method.getDeclaringClass();
        if (null == AnnotationUtils.findAnnotation(declaringClass, NoCode.class)) {
            return method.getName();
        }
        java.lang.reflect.Type[] parameterTypes = method.getGenericParameterTypes();

        int countGenericVar = 0;
        for (java.lang.reflect.Type parameterType : parameterTypes) {
            switch (isModelVar(parameterType)) {
                case modelObj:
                    //如果存在单模型对象入参的方法，直接返回方法名称
                    return method.getName();
                case modelGeneric:
                    countGenericVar++;
                    break;
                case base:
                    //基础数据类型/其他的类型
                    break;
            }
        }
        if (countGenericVar != 0) {
            return method.getName() + FunUtils.LOWCODE_METHOD_SUFFIX;
        }
        return method.getName();
    }

    @SuppressWarnings("unused")
    private static String fetchActualType(java.lang.reflect.Type actualType) {
        String actualTypeString = fetchGenericType(actualType);
        if (null == actualTypeString) {
            actualTypeString = actualType.getTypeName();
        }
        return actualTypeString;
    }

    private static String fetchGenericType(java.lang.reflect.Type actualType) {
        String actualTypeString;
        if (actualType instanceof TypeVariable) {
            if (((TypeVariable<?>) actualType).getGenericDeclaration() instanceof Method) {
                actualTypeString = ((Method) ((TypeVariable<?>) actualType).getGenericDeclaration()).getDeclaringClass().getName();
            } else {
                actualTypeString = ((Class<?>) ((TypeVariable<?>) actualType).getGenericDeclaration()).getName();
            }
            if (!TypeUtils.isModelClass(actualTypeString)) {
                actualTypeString = Object.class.getName();
            }
        } else if (null != actualType) {
            return actualType.getTypeName();
        } else {
            return null;
        }
        return actualTypeString;
    }

    public static void convertTypeFromMethod(Class<?> clazz, Class<?> parameterType, java.lang.reflect.Type genericType, Type type) {
        java.lang.reflect.Type actualGenericType = TypeUtils.getGenericType(genericType);
        java.lang.reflect.Type parameterOrGenericType = Optional.ofNullable(TypeUtils.getGenericType(genericType)).orElse(genericType);
        boolean modelGeneric = TypeUtils.isModelGeneric(TypeUtils.getTypeName(parameterOrGenericType));
        type.setModelGeneric(modelGeneric);

        boolean multi = parameterType.isArray() || TypeUtils.isCollection(parameterType);

        String genericTypeName;
        if (parameterType.isArray()) {
            genericTypeName = parameterType.getComponentType().getName();
        } else {
            genericTypeName = Optional.ofNullable(actualGenericType).map(java.lang.reflect.Type::getTypeName).orElse(null);
        }
        if (multi && null == genericType) {
            genericTypeName = Object.class.getName();
        }

        //判断是否是低无一体类型的
        Boolean fromNocodeModel = isNocodeModel(parameterType);
        if (fromNocodeModel) {
            type.setLtype(Map.class.getName());
        } else {
            type.setLtype(TypeUtils.fixActualType(parameterType));
        }
        type.setLtypeT(TypeUtils.fixType(genericTypeName));

        TtypeEnum ttypeEnum;
        String ttype;
        if (modelGeneric) {
            if (multi) {
                ttype = TtypeEnum.O2M.value();
                ttypeEnum = TtypeEnum.O2M;
            } else {
                ttype = TtypeEnum.M2O.value();
                ttypeEnum = TtypeEnum.M2O;
            }
        } else {
            if (fromNocodeModel) {
                if (multi) {
                    ttype = O2M.value();
                    ttypeEnum = O2M;
                } else {
                    ttype = M2O.value();
                    ttypeEnum = M2O;
                }
            } else {
                try {
                    ttype = Models.types().defaultTtypeFromLtype(type.getLtype(), type.getLtypeT(), null);
                } catch (Exception e) {
                    //打印日志，方便排查问题
                    log.error("clazz:{},parameterType:{}, genericType:{}, type:{}",
                            clazz.getCanonicalName(),parameterType.getCanonicalName(), genericType.getTypeName(), type.getTtype());
                    throw PamirsException.construct(SYSTEM_ERROR, e).errThrow();
                }
                ttypeEnum = TtypeEnum.getEnumByValue(TtypeEnum.class, ttype);
            }
        }
        type.setTtype(ttypeEnum);
        type.setMulti(multi);

        String actualType = type.getLtype();
        if (type.getMulti() || Object.class.getName().equals(actualType) && StringUtils.isNotBlank(type.getLtypeT())) {
            actualType = type.getLtypeT();
        }
        type.setModel(null);
        type.setDictionary(null);
        if (TypeUtils.isModelClass(actualType)) {
            type.setModel(fetchModel(actualType));
        } else if (fromNocodeModel) {
            type.setModel(fetchModel(parameterType.getName()));
        } else if (modelGeneric || TypeUtils.isDMap(actualType) || TypeUtils.isDataMap(actualType)) {
            type.setModel(fetchModel(clazz.getName()));
        } else if (Object.class.getName().equals(type.getLtype()) && null == type.getLtypeT()) {
            type.setModel(null);
        } else if (TtypeEnum.ENUM.value().equals(ttype) && TypeUtils.isIEnumClass(actualType)) {
            type.setDictionary(fetchDictionary(actualType));
        }
    }

    private static String fetchModel(String modelClass) {
        try {
            Class<?> clazz = Class.forName(modelClass);
            return Optional.ofNullable(AnnotationUtils.findAnnotation(clazz, Model.model.class))
                    .map(Model.model::value).orElse(
                            Optional.ofNullable(AnnotationUtils.getAnnotation(clazz, Fun.class)).map(Fun::value).orElse(modelClass)
                    );
        } catch (ClassNotFoundException e) {
            throw PamirsException.construct(BASE_FETCH_MODEL_ERROR, e).errThrow();
        }
    }

    private static String fetchDictionary(String modelClass) {
        try {
            Class<?> clazz = Class.forName(modelClass);
            return Optional.ofNullable(AnnotationUtils.getAnnotation(clazz, Dict.class))
                    .map(Dict::dictionary).orElse(
                            Optional.ofNullable(AnnotationUtils.getAnnotation(clazz, Dict.dictionary.class))
                                    .map(Dict.dictionary::value)
                                    .orElse(modelClass)
                    );
        } catch (ClassNotFoundException e) {
            throw PamirsException.construct(BASE_FETCH_DICTIONARY_ERROR, e).errThrow();
        }
    }

    private static Boolean isNocodeModel(java.lang.reflect.Type type) {
        return type instanceof Class
                && (null == ((Class<?>) type).getDeclaredAnnotation(Model.Fuse.class) && null != ((Class<?>) type).getDeclaredAnnotation(Model.class))
                && (null != ((Class<?>) type).getDeclaredAnnotation(Model.Fuse.class) && null == ((Class<?>) type).getDeclaredAnnotation(Model.class))
                && null != ((Class<?>) type).getDeclaredAnnotation(Model.model.class)
                && D.class.isAssignableFrom((Class<?>) type);
    }

    private static paramType isModelVar(java.lang.reflect.Type type) {
        if (type instanceof Class) {
            Class<?> clazz = (Class<?>) type;
            if (isNocodeModel(clazz)) {
                return paramType.modelObj;
            }
            return paramType.base;
        } else if (ParameterizedType.class.isAssignableFrom(type.getClass())) {
            java.lang.reflect.Type rawType;
            if (TypeUtils.isCollection(type)
                    || Pagination.class.equals(rawType = ((ParameterizedType) type).getRawType())
                    || IWrapper.class.isAssignableFrom((Class<?>) rawType)
            ) {
                java.lang.reflect.Type genericType = ((ParameterizedType) type).getActualTypeArguments()[0];
                if (paramType.modelObj.equals(isModelVar(genericType))) {
                    return paramType.modelGeneric;
                }
                return paramType.base;
            }
            return isModelVar(rawType);
        }
        return paramType.base;
    }

    enum paramType {
        base, modelGeneric, modelObj;
    }

}
