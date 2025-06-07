package pro.shushi.pamirs.trigger.constant;

import pro.shushi.pamirs.framework.common.constants.ConfigureConstants;

/**
 * Notify常量
 *
 * @author Adamancy Zhang at 00:00 on 2020-04-22
 */
public interface NotifyConstant {

    String TRIGGER_CONFIGURATION_KEY = ConfigureConstants.PAMIRS_EVENT_CONFIG_PREFIX + ".trigger";

    String PAMIRS_SESSION_KEY = "PAMIRS_SESSION_KEY";

    String DEFAULT_NOTIFY_EVENT_LISTENER_TOPIC = "DefaultNotifyEventListener";

    String AUTO_TRIGGER_TOPIC = "CHANGE_DATA_EVENT_TOPIC";

    String TOPIC_KEY = "TOPIC_KEY";

    String TAGS_KEY = "TAGS_KEY";

    String FUNCTION_NAMESPACE_KEY = "FUNCTION_NAMESPACE";

    String FUNCTION_FUN_KEY = "FUNCTION_FUN";

    String TARGET_FUNCTION_NAMESPACE_KEY = "TARGET_FUNCTION_NAMESPACE";

    String TARGET_FUNCTION_FUN_KEY = "TARGET_FUNCTION_FUN";
}
