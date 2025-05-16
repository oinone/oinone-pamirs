package pro.shushi.pamirs.middleware.schedule.directive;

/**
 * @author Adamancy Zhang
 * @date 2020-10-22 14:49
 */
public class DirectiveEnumerationHelper {

    public static <T extends Enum<T> & DirectiveEnumeration<T>> boolean verificationDefinition(Class<T> enumerationClass) {
        return DirectiveHelper.verificationDefinition(enumerationClass.getEnumConstants()) != -1;
    }
}
