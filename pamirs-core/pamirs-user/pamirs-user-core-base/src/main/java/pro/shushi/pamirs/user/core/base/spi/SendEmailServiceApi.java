package pro.shushi.pamirs.user.core.base.spi;

import pro.shushi.pamirs.message.model.EmailTemplate;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;
import pro.shushi.pamirs.user.api.enmu.UserBehaviorEventEnum;
import pro.shushi.pamirs.user.api.model.tmodel.PamirsUserTransient;

/**
 * @author Wuxin
 * @Date 2024/7/11
 * @since 1.0
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface SendEmailServiceApi {

    /**
     * 判断给定的行为事件是否匹配当前邮件发送策略。
     *
     * @param userBehaviorEventEnum 行为事件枚举对象
     * @return 如果行为事件匹配当前策略返回 true，否则返回 false
     */
    boolean match(UserBehaviorEventEnum userBehaviorEventEnum);

    /**
     * 执行邮件发送逻辑。
     *
     * @param user 包含用户信息的临时用户对象
     * @return 处理后的临时用户对象
     */
    PamirsUserTransient execute(PamirsUserTransient user);


    /**
     * 获取模版接口
     *
     * @param userTransient 包含用户信息的临时用户对象
     * @param code          code
     */
    EmailTemplate getEmailTemplate(PamirsUserTransient userTransient, String code);
}