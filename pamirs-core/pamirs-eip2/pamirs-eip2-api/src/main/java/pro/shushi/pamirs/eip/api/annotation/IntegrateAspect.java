package pro.shushi.pamirs.eip.api.annotation;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.eip.api.serializable.DefaultJSONSerializable;
import pro.shushi.pamirs.eip.api.service.EipExecuteService;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;
import pro.shushi.pamirs.meta.util.TypeUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
@Aspect
public class IntegrateAspect {

    @Autowired
    private DefaultJSONSerializable defaultJSONSerializable;

    @Pointcut("@annotation(pro.shushi.pamirs.eip.api.annotation.Integrate)")
    public void pointCut() {
    }

    @Around("pointCut()")
    public Object handle(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {


        Signature sig = proceedingJoinPoint.getSignature();
        MethodSignature msig = null;
        if (!(sig instanceof MethodSignature)) {
            throw new IllegalArgumentException("该注解只能用于方法");
        }
        msig = (MethodSignature) sig;
        Object target = proceedingJoinPoint.getTarget();
        Method currentMethod = target.getClass().getMethod(msig.getName(), msig.getParameterTypes());

        Integrate integrate = AnnotationUtils.findAnnotation(currentMethod, Integrate.class);
        if (integrate == null) {
            return null;
        }
        Class<?> configClass = integrate.config();
        IEipAnnotationSingletonConfig eipConfigModel = null;
        try {
            eipConfigModel = (IEipAnnotationSingletonConfig) ((IEipAnnotationSingletonConfig) configClass.newInstance()).singletonModel();
            if (eipConfigModel == null) {
                //没有初始化
                return null;
            }
        } catch (InstantiationException e) {
            return null;
        } catch (IllegalAccessException e) {
            return null;
        }

        Fun namespaceAnno = AnnotationUtils.findAnnotation(target.getClass(), Fun.class);
        String namespace = namespaceAnno.value();

        Function.fun funAnno = AnnotationUtils.findAnnotation(currentMethod, Function.fun.class);
        String fun = (funAnno != null && funAnno.value() != null) ? funAnno.value() : msig.getName();

        EipExecuteService eipExecuteService = BeanDefinitionUtils.getBean(EipExecuteService.class);
        Object[] args = proceedingJoinPoint.getArgs();
        Map<String, Object> eipParamsMap;
        if (args == null || args.length <= 0) {
            eipParamsMap = Collections.emptyMap();
        } else {
            eipParamsMap = new HashMap<>(args.length);
            Parameter[] parameters = currentMethod.getParameters();
            int i = 0;
            for (Parameter parameter : parameters) {
                Object arg = args[i];
                i++;
                if (arg == null) {
                    continue;
                }
                if (TypeUtils.isBaseType(arg.getClass())) {
                    eipParamsMap.put(parameter.getName(), arg);
                } else {
                    eipParamsMap.put(parameter.getName(), defaultJSONSerializable.serializable(arg));
                }
            }
        }
        return eipExecuteService.callByInterfaceNameAndBody(EipIntegrateConvert.getInterfaceName(namespace, fun), eipParamsMap);
    }
}
