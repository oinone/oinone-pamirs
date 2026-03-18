package pro.shushi.pamirs.locale.utils;

import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.locale.constants.MetadataConstants;

import java.util.Locale;

/**
 * i18n utils
 *
 * @author Adamancy Zhang
 */
@Component
public class I18nUtils {

    private static final Logger log = LoggerFactory.getLogger(I18nUtils.class);

    private static MessageSource messageSource;

    private final MessageSource injectedMessageSource;

    public I18nUtils(MessageSource messageSource) {
        this.injectedMessageSource = messageSource;
    }

    @PostConstruct
    public void init() {
        I18nUtils.messageSource = this.injectedMessageSource;
    }

    public static boolean isTranslate() {
        return isTranslate(LocaleContextHolder.getLocale());
    }

    public static boolean isTranslate(Locale locale) {
        if (Locale.SIMPLIFIED_CHINESE.getLanguage().equals(locale.getLanguage())) {
            String country = locale.getCountry();
            return !country.isBlank() && !Locale.SIMPLIFIED_CHINESE.getCountry().equals(country);
        }
        return true;
    }

    public static Locale getLocale() {
        Locale locale = LocaleContextHolder.getLocale();
        if (Locale.SIMPLIFIED_CHINESE.getLanguage().equals(locale.getLanguage())) {
            String country = locale.getCountry();
            if (country.isBlank() || Locale.SIMPLIFIED_CHINESE.getCountry().equals(country)) {
                return Locale.SIMPLIFIED_CHINESE;
            }
        }
        return locale;
    }

    public static boolean isZh() {
        return Locale.CHINESE.getLanguage().equals(LocaleContextHolder.getLocale().getLanguage());
    }

    public static boolean isEn() {
        return Locale.ENGLISH.getLanguage().equals(LocaleContextHolder.getLocale().getLanguage());
    }

    public static String getMessage(String code, Object... args) {
        Locale locale = getLocale();
        return messageSource.getMessage(code, args, "", locale);
    }

    public static String getMessage(String code) {
        Locale locale = getLocale();
        return messageSource.getMessage(code, null, "", locale);
    }

    public static String translate(String code, String defaultMessage) {
        return translate(code, defaultMessage, (Object[]) null);
    }

    public static String translate(String code, String defaultMessage, Object... args) {
        Locale locale = getLocale();
        try {
            String message = messageSource.getMessage(code, args, "", locale);
            if (StringUtils.isBlank(message)) {
                return defaultMessage;
            }
            return message;
        } catch (Throwable e) {
            log.warn("Invalid i18n message. code: {}", code, e);
            return defaultMessage;
        }
    }

    public static String translateModule(String module, String field, String defaultMessage) {
        String code = MetadataConstants.MODULE + MetadataConstants.SPLIT + module + MetadataConstants.SPLIT + field;
        return translate(code, defaultMessage);
    }

    public static String translateModel(String module, String modelModel, String field, String defaultMessage) {
        String code = module + MetadataConstants.SPLIT + MetadataConstants.MODEL + MetadataConstants.SPLIT + modelModel + MetadataConstants.SPLIT + field;
        return translate(code, defaultMessage);
    }

    public static String translateField(String module, String modelModel, String fieldName, String property, String defaultMessage) {
        String code = module + MetadataConstants.SPLIT + MetadataConstants.MODEL
                + MetadataConstants.SPLIT + modelModel
                + MetadataConstants.SPLIT + MetadataConstants.FIELD
                + MetadataConstants.SPLIT + fieldName
                + MetadataConstants.SPLIT + property;
        return translate(code, defaultMessage);
    }

    public static String translateSequence(String module, String sequenceCode, String field, String defaultMessage) {
        String code = module + MetadataConstants.SPLIT + MetadataConstants.SEQUENCE
                + MetadataConstants.SPLIT + sequenceCode
                + MetadataConstants.SPLIT + field;
        return translate(code, defaultMessage);
    }

    public static String translateInterfaces(String module, String namespace, String fun, String field, String defaultMessage) {
        String code = module + MetadataConstants.SPLIT + MetadataConstants.INTERFACES
                + MetadataConstants.SPLIT + namespace
                + MetadataConstants.SPLIT + fun
                + MetadataConstants.SPLIT + field;
        return translate(code, defaultMessage);
    }

    public static String translateFunction(String module, String namespace, String fun, String field, String defaultMessage) {
        String code = module + MetadataConstants.SPLIT + MetadataConstants.FUNCTION
                + MetadataConstants.SPLIT + namespace
                + MetadataConstants.SPLIT + fun
                + MetadataConstants.SPLIT + field;
        return translate(code, defaultMessage);
    }

    public static String translateExpressionDefinition(String module, String sign, String field, String defaultMessage) {
        String code = module + MetadataConstants.SPLIT + MetadataConstants.EXPRESSION_DEFINITION
                + MetadataConstants.SPLIT + sign
                + MetadataConstants.SPLIT + field;
        return translate(code, defaultMessage);
    }

    public static String translateComputeDefinition(String module, String sign, String field, String defaultMessage) {
        String code = module + MetadataConstants.SPLIT + MetadataConstants.COMPUTE_DEFINITION
                + MetadataConstants.SPLIT + sign
                + MetadataConstants.SPLIT + field;
        return translate(code, defaultMessage);
    }

    public static String translateHook(String module, String namespace, String fun, String field, String defaultMessage) {
        String code = module + MetadataConstants.SPLIT + MetadataConstants.HOOK
                + MetadataConstants.SPLIT + namespace
                + MetadataConstants.SPLIT + fun
                + MetadataConstants.SPLIT + field;
        return translate(code, defaultMessage);
    }

    public static String translateExtPoint(String module, String namespace, String name, String field, String defaultMessage) {
        String code = module + MetadataConstants.SPLIT + MetadataConstants.EXT_POINT
                + MetadataConstants.SPLIT + namespace
                + MetadataConstants.SPLIT + name
                + MetadataConstants.SPLIT + field;
        return translate(code, defaultMessage);
    }

    public static String translateExtPointImplementation(String module, String namespace, String name, String field, String defaultMessage) {
        String code = module + MetadataConstants.SPLIT + MetadataConstants.EXT_POINT_IMPLEMENTATION
                + MetadataConstants.SPLIT + namespace
                + MetadataConstants.SPLIT + name
                + MetadataConstants.SPLIT + field;
        return translate(code, defaultMessage);
    }

    public static String translateMenu(String module, String menuName, String field, String defaultMessage) {
        String code = module + MetadataConstants.SPLIT + MetadataConstants.MENU + MetadataConstants.SPLIT + menuName + MetadataConstants.SPLIT + field;
        return translate(code, defaultMessage);
    }

    public static String translateMask(String name, String field, String defaultMessage) {
        String code = MetadataConstants.MASK + MetadataConstants.SPLIT + name + MetadataConstants.SPLIT + field;
        return translate(code, defaultMessage);
    }

    public static String translateLayout(String name, String field, String defaultMessage) {
        String code = MetadataConstants.LAYOUT + MetadataConstants.SPLIT + name + MetadataConstants.SPLIT + field;
        return translate(code, defaultMessage);
    }

    public static String translateView(String modelModel, String name, String field, String defaultMessage) {
        String code = MetadataConstants.VIEW + MetadataConstants.SPLIT + modelModel + MetadataConstants.SPLIT + name + MetadataConstants.SPLIT + field;
        return translate(code, defaultMessage);
    }

    public static String translateFieldInViewTemplate(String modelModel, String viewName, String fieldName, String property, String defaultMessage) {
        String code = MetadataConstants.VIEW_TEMPLATE + MetadataConstants.SPLIT + modelModel + MetadataConstants.SPLIT + viewName
                + MetadataConstants.SPLIT + MetadataConstants.FIELD
                + MetadataConstants.SPLIT + fieldName
                + MetadataConstants.SPLIT + property;
        return translate(code, defaultMessage);
    }

    public static String translateActionInViewTemplate(String modelModel, String viewName, String actionName, String property, String defaultMessage) {
        String code = MetadataConstants.VIEW_TEMPLATE + MetadataConstants.SPLIT + modelModel + MetadataConstants.SPLIT + viewName
                + MetadataConstants.SPLIT + MetadataConstants.ACTION
                + MetadataConstants.SPLIT + actionName
                + MetadataConstants.SPLIT + property;
        return translate(code, defaultMessage);
    }

    public static String translateServerAction(String module, String modelModel, String name, String field, String defaultMessage) {
        String code = module + MetadataConstants.SPLIT + MetadataConstants.SERVER_ACTION
                + MetadataConstants.SPLIT + modelModel
                + MetadataConstants.SPLIT + name
                + MetadataConstants.SPLIT + field;
        return translate(code, defaultMessage);
    }

    public static String translateViewAction(String module, String modelModel, String name, String field, String defaultMessage) {
        String code = module + MetadataConstants.SPLIT + MetadataConstants.VIEW_ACTION
                + MetadataConstants.SPLIT + modelModel
                + MetadataConstants.SPLIT + name
                + MetadataConstants.SPLIT + field;
        return translate(code, defaultMessage);
    }

    public static String translateUrlAction(String module, String modelModel, String name, String field, String defaultMessage) {
        String code = module + MetadataConstants.SPLIT + MetadataConstants.URL_ACTION
                + MetadataConstants.SPLIT + modelModel
                + MetadataConstants.SPLIT + name
                + MetadataConstants.SPLIT + field;
        return translate(code, defaultMessage);
    }

    public static String translateClientAction(String module, String modelModel, String name, String field, String defaultMessage) {
        String code = module + MetadataConstants.SPLIT + MetadataConstants.CLIENT_ACTION
                + MetadataConstants.SPLIT + modelModel
                + MetadataConstants.SPLIT + name
                + MetadataConstants.SPLIT + field;
        return translate(code, defaultMessage);
    }

    public static String translateDataDictionary(String module, String dictionary, String field, String defaultMessage) {
        String code = module + MetadataConstants.SPLIT + MetadataConstants.DATA_DICTIONARY
                + MetadataConstants.SPLIT + dictionary
                + MetadataConstants.SPLIT + field;
        return translate(code, defaultMessage);
    }

    public static String translateDataDictionaryItem(String module, String dictionary, String name, String field, String defaultMessage) {
        String code = module + MetadataConstants.SPLIT + MetadataConstants.DATA_DICTIONARY
                + MetadataConstants.SPLIT + dictionary
                + MetadataConstants.SPLIT + name
                + MetadataConstants.SPLIT + field;
        return translate(code, defaultMessage);
    }

    public static String translateErrorDefinition(String clazz, String field, String defaultMessage) {
        String code = MetadataConstants.ERROR_DEFINITION
                + MetadataConstants.SPLIT + clazz
                + MetadataConstants.SPLIT + field;
        return translate(code, defaultMessage);
    }

    public static String translateErrorDefinitionItem(String clazz, String name, String field, String defaultMessage) {
        String code = MetadataConstants.ERROR_DEFINITION
                + MetadataConstants.SPLIT + clazz
                + MetadataConstants.SPLIT + name
                + MetadataConstants.SPLIT + field;
        return translate(code, defaultMessage);
    }

    public static String translateTrigger(String module, String namespace, String fun, String field, String defaultMessage) {
        String code = module + MetadataConstants.SPLIT + MetadataConstants.TRIGGER_TASK
                + MetadataConstants.SPLIT + namespace
                + MetadataConstants.SPLIT + fun
                + MetadataConstants.SPLIT + field;
        return translate(code, defaultMessage);
    }

    public static String translateSchedule(String module, String namespace, String fun, String field, String defaultMessage) {
        String code = module + MetadataConstants.SPLIT + MetadataConstants.SCHEDULE_TASK
                + MetadataConstants.SPLIT + namespace
                + MetadataConstants.SPLIT + fun
                + MetadataConstants.SPLIT + field;
        return translate(code, defaultMessage);
    }
}
