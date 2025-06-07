package pro.shushi.pamirs.user.core.base.spi;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;

/**
 * 返回true，不校验验证码
 */
@Slf4j
@Order //默认优先级最低，业务配置需要配置成为优先级高
@Component
@SPI.Service
public class DefaultLoginVerificationCodeApi implements LoginVerificationCodeApi {

    public Boolean checkVerificationCode(String verificationCode) {
        try {
            String env = BeanDefinitionUtils.getEnvironment().getProperty("spring.profiles.active");
            if (StringUtils.isNotBlank(env) && ("dev".equals(env) || "test".equals(env))) {
                if ("888888".equals(verificationCode)) {
                    log.info("测试环境，默认验证码 888888");
                    return true;
                }
            }
        } catch (Exception e) {
            log.error("获取上下文信息错误", e);
        }
        return false;
    }

}
