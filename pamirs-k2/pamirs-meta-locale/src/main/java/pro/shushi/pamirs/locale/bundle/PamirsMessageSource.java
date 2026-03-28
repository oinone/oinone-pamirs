package pro.shushi.pamirs.locale.bundle;

import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.lang.Nullable;
import pro.shushi.pamirs.locale.configure.PamirsMessageSourceConfigure;

import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Pamirs Message Source
 *
 * @author Adamancy Zhang at 15:36 on 2026-03-13
 */
public class PamirsMessageSource extends ResourceBundleMessageSource {

    private final ResourceBundle.Control control = new PamirsResourceBundleControl();

    private final Map<String, Map<Locale, ResourceBundle>> cachedResourceBundles = new ConcurrentHashMap<>();

    @Override
    protected ResourceBundle doGetBundle(String basename, Locale locale) throws MissingResourceException {
        if (PamirsMessageSourceConfigure.I18N_NORMAL_BASENAME.equals(basename) || PamirsMessageSourceConfigure.I18N_METADATA_BASENAME.equals(basename)) {
            return ResourceBundle.getBundle(basename, locale, control);
        }
        return super.doGetBundle(basename, locale);
    }

    @Nullable
    @Override
    protected ResourceBundle getResourceBundle(String basename, Locale locale) {
        if (PamirsMessageSourceConfigure.I18N_METADATA_BASENAME.equals(basename)) {
            if (!Locale.CHINESE.getLanguage().equals(locale.getLanguage())) {
                return super.getResourceBundle(basename, locale);
            }
            return null;
        }
        return super.getResourceBundle(basename, locale);
    }
}
