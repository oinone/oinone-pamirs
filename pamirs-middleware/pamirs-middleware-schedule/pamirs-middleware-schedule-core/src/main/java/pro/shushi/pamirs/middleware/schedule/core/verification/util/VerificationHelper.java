package pro.shushi.pamirs.middleware.schedule.core.verification.util;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.middleware.schedule.core.verification.api.IParamsVerificationDefinition;
import pro.shushi.pamirs.middleware.schedule.core.verification.api.IParamsVerificationEnumeration;

import java.util.Map;

/**
 * @author Adamancy Zhang
 * @date 2020-10-20 22:12
 */
public class VerificationHelper {

    @SuppressWarnings("rawtypes")
    public static <T extends Enum<T> & IParamsVerificationEnumeration> boolean verify(Map<String, Object> data, Class<T> enumerationClass) {
        for (T item : enumerationClass.getEnumConstants()) {
            IParamsVerificationDefinition verificationDefinition = item.getVerificationDefinition();
            String key = verificationDefinition.getKey();
            if (StringUtils.isNotBlank(key)) {
                //noinspection unchecked
                if (!verificationDefinition.verify(data.get(key))) {
                    return Boolean.FALSE;
                }
            }
        }
        return Boolean.TRUE;
    }

    public static void required(Object value, String errorMessage) {
        if (value instanceof String) {
            if (StringUtils.isBlank((String) value)) {
                throw new IllegalArgumentException(errorMessage);
            }
        } else {
            if (value == null) {
                throw new IllegalArgumentException(errorMessage);
            }
        }
    }
}