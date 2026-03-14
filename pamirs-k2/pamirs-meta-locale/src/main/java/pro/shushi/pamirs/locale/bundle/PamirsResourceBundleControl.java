package pro.shushi.pamirs.locale.bundle;

import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import pro.shushi.pamirs.locale.configure.PamirsMessageSourceConfigure;

import java.io.IOException;
import java.util.*;

/**
 * ResourceBundleControl
 *
 * @author Adamancy Zhang at 15:35 on 2026-03-13
 */
public class PamirsResourceBundleControl extends ResourceBundle.Control {

    private final Map<String, ResourceBundleHandler> handlers = new HashMap<>();

    public PamirsResourceBundleControl() {
        handlers.put(PamirsMessageSourceConfigure.I18N_METADATA_BASENAME, new MetadataResourceBundleHandler());
        handlers.put(PamirsMessageSourceConfigure.I18N_NORMAL_BASENAME, new NormalResourceBundleHandler());

        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        for (ResourceBundleHandler handler : handlers.values()) {
            handler.scanResources(resolver);
        }
    }

    @Override
    public List<String> getFormats(String baseName) {
        ResourceBundleHandler handler = handlers.get(baseName);
        if (handler != null) {
            return handler.getFormats();
        }
        return super.getFormats(baseName);
    }

    @Override
    public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload) throws IOException, IllegalAccessException, InstantiationException {
        ResourceBundleHandler handler = handlers.get(baseName);
        if (handler != null) {
            ResourceBundle bundle = handler.loadBundle(baseName, locale, format);
            if (bundle != null) {
                return bundle;
            }
        }
        return super.newBundle(baseName, locale, format, loader, reload);
    }
}
