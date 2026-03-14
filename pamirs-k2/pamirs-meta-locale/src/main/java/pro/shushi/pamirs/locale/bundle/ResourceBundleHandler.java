package pro.shushi.pamirs.locale.bundle;

import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * ResourceBundleHandler
 *
 * @author Adamancy Zhang at 15:35 on 2026-03-13
 */
public interface ResourceBundleHandler {

    void scanResources(PathMatchingResourcePatternResolver resolver);

    List<String> getFormats();

    ResourceBundle loadBundle(String baseName, Locale locale, String format) throws IOException;
}
