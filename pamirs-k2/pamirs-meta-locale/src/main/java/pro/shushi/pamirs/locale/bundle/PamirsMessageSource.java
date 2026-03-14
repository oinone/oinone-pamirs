package pro.shushi.pamirs.locale.bundle;

import org.springframework.context.support.ResourceBundleMessageSource;
import pro.shushi.pamirs.locale.configure.PamirsMessageSourceConfigure;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Pamirs Message Source
 *
 * @author Adamancy Zhang at 15:36 on 2026-03-13
 */
public class PamirsMessageSource extends ResourceBundleMessageSource {

    private final ResourceBundle.Control control = new PamirsResourceBundleControl();

    @Override
    protected ResourceBundle doGetBundle(String basename, Locale locale) throws MissingResourceException {
        if (PamirsMessageSourceConfigure.I18N_NORMAL_BASENAME.equals(basename) || PamirsMessageSourceConfigure.I18N_METADATA_BASENAME.equals(basename)) {
            return ResourceBundle.getBundle(basename, locale, control);
        }
        return super.doGetBundle(basename, locale);
    }
}
