package pro.shushi.pamirs.user.api.crypto.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注解在需要解密的方法上
 *
 * @author shier
 * date  2022/6/17 上午9:55
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NeedDecrypt {


}
