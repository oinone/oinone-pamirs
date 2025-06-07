package pro.shushi.pamirs.user.core.base.service;

import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;
import pro.shushi.pamirs.user.api.enmu.UserBehaviorEventEnum;
import pro.shushi.pamirs.user.api.login.IUserDataChecker;
import pro.shushi.pamirs.user.api.model.PamirsUser;
import pro.shushi.pamirs.user.api.model.tmodel.PamirsUserTransient;
import pro.shushi.pamirs.user.api.service.UserEmailConfirmationService;
import pro.shushi.pamirs.user.api.utils.UserServiceUtils;

import static pro.shushi.pamirs.user.api.enmu.UserExpEnumerate.*;
import static pro.shushi.pamirs.user.api.utils.PamirsUserDataChecker.checkEmailFormat;

/**
 * 发送确认邮件的抽象类，用于向用户发送确认邮件。
 * 子类必须实现 getPamirsUserTransient 方法。
 *
 * @author Wuxin
 * @since 1.0
 */
public abstract class SendConfirmEmailAbstract {

    protected IUserDataChecker dataChecker = BeanDefinitionUtils.getBean(IUserDataChecker.class);

    protected UserEmailConfirmationService userEmailConfirmationService = BeanDefinitionUtils.getBean(UserEmailConfirmationService.class);


    /**
     * 发送确认邮件给用户。
     *
     * @param user 用户对象，包含邮箱和其他详细信息。
     * @return 处理后更新的用户对象。
     */
    public PamirsUserTransient sendConfirmEmail(PamirsUserTransient user) {
        checkEmailFormat(user);

        String msgType = user.getMsgType();
        UserBehaviorEventEnum userBehaviorEvent = user.getUserBehaviorEvent();
        if (userBehaviorEvent == null) {
            UserServiceUtils.broken(user.setErrorMsg(USER_EMAIL_MISS_EVENT_IS_NULL_ERROR.msg())
                    .setErrorCode(USER_EMAIL_MISS_EVENT_IS_NULL_ERROR.code())
                    .setErrorField("email"));
            return user;
        }
        return generateConfirmation(user, userBehaviorEvent, msgType);
    }

    /**
     * 根据用户详情和行为事件生成 PamirsUserTransient 对象。
     *
     * @param user              用户对象。
     * @param userBehaviorEvent 触发邮件的行为事件。
     * @param msgType           发送消息的类型。
     * @return 处理后的 PamirsUserTransient 对象。
     */
    public abstract PamirsUserTransient generateConfirmation(PamirsUserTransient user, UserBehaviorEventEnum userBehaviorEvent, String msgType);


    protected void checkUserInfo(PamirsUserTransient user) {
        Long userId = PamirsSession.getUserId();
        if (userId == null) {
            UserServiceUtils.broken(user.setErrorMsg(USER_SESSION_ID_ISNULL.msg())
                    .setErrorCode(USER_SESSION_ID_ISNULL.code())
                    .setErrorField("login"));
            return;
        }
        PamirsUser currentUser = new PamirsUser().queryById(userId);
        if (currentUser == null) {
            UserServiceUtils.broken(user.setErrorMsg(USER_MODIFY_NOT_EXISTED_ERROR.msg())
                    .setErrorCode(USER_MODIFY_NOT_EXISTED_ERROR.code())
                    .setErrorField("login"));
        }
    }
}
