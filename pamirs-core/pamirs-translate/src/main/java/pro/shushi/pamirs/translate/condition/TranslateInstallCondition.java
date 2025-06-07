package pro.shushi.pamirs.translate.condition;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import pro.shushi.pamirs.translate.constant.TranslateConstants;

/**
 * TranslateInstallCondition
 *
 * @author yakir on 2020/05/11 12:01.
 */
public class TranslateInstallCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {

        String status = context.getEnvironment().getProperty(TranslateConstants.TRANSLATE_STATE);
        return StringUtils.equalsIgnoreCase(status, TranslateConstants.TRANSLATE_OPEN);
    }
}
