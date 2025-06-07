package pro.shushi.pamirs.user.api.crypto;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.dto.entity.DataMap;
import pro.shushi.pamirs.meta.base.AbstractModel;
import pro.shushi.pamirs.meta.base.BaseModel;
import pro.shushi.pamirs.meta.base.BaseRelation;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.base.common.CodeModel;
import pro.shushi.pamirs.meta.base.common.NameCodeModel;
import pro.shushi.pamirs.meta.util.FieldUtils;
import pro.shushi.pamirs.meta.util.JsonUtils;
import pro.shushi.pamirs.user.api.crypto.annotation.EncryptField;
import pro.shushi.pamirs.user.api.utils.AES256Utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 处理@NeedDecrypt注解的方法的参数解密
 *
 * @Author shier
 * @Description
 * @Date
 */
@Slf4j
@Aspect
@Component
public class DecryptAspect {

    private static final List<Class<?>> STOP_CLASS_LIST = Arrays.asList(DataMap.class, IdModel.class, CodeModel.class, NameCodeModel.class, BaseRelation.class, BaseModel.class, AbstractModel.class, Object.class);

    @Pointcut("@annotation(pro.shushi.pamirs.user.api.crypto.annotation.NeedDecrypt)")
    public void pointCut() {
    }

    @Around("pointCut()")
    public Object decrypt(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = null;
        Object[] args = joinPoint.getArgs();
        try {
            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof String) {
                    args[i] = decryptValue((String) args[i]);
                } else {
                    args[i] = decryptData(args[i]);
                }
            }
        } catch (Throwable e) {
            log.error("参数请求异常,请求的参数是{}", JsonUtils.toJSONString(joinPoint.getArgs()));
            throw new RuntimeException("参数解密异常", e);
        }
        result = joinPoint.proceed(args);
        return result;
    }

    private Object decryptData(Object obj) throws IllegalAccessException {
        if (Objects.isNull(obj)) {
            return null;
        }
        if (obj instanceof ArrayList) {
            decryptList(obj);
        } else {
            decryptObj(obj);
        }

        return obj;
    }

    /**
     * 针对单个实体类进行 解密
     *
     * @param obj
     * @throws IllegalAccessException
     */
    private void decryptObj(Object obj) throws IllegalAccessException {
        Class<?> cls = obj.getClass();
        while (cls != null) {
            Field[] fields = cls.getDeclaredFields();
            for (Field field : fields) {
                boolean hasSecureField = field.isAnnotationPresent(EncryptField.class);
                if (hasSecureField) {
                    field.setAccessible(true);
                    String encryptValue = (String) FieldUtils.getFieldValue(obj, field.getName());
                    String value = AES256Utils.decrypt(encryptValue);
                    FieldUtils.setFieldValue(obj, field.getName(), value);
                    field.set(obj, value);
                }
            }
            cls = cls.getSuperclass();
            if (cls != null && STOP_CLASS_LIST.contains(cls)) {
                cls = null;
            }
        }
    }

    /**
     * 针对list<实体> 进行反射、解密
     *
     * @param obj
     * @throws IllegalAccessException
     */
    private void decryptList(Object obj) throws IllegalAccessException {
        List<Object> result = new ArrayList<>();
        if (obj instanceof ArrayList) {
            for (Object o : (List<?>) obj) {
                result.add(o);
            }
        }
        for (Object object : result) {
            decryptObj(object);
        }
    }


    public String decryptValue(String realValue) {
        String value = AES256Utils.decrypt(realValue);
        return value;
    }
}