package pro.shushi.pamirs.locale.bundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import pro.shushi.pamirs.locale.configure.PamirsMessageSourceConfigure;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * NormalResourceBundleHandler
 *
 * @author Adamancy Zhang at 15:35 on 2026-03-13
 */
public class NormalResourceBundleHandler implements ResourceBundleHandler {

    private static final Logger log = LoggerFactory.getLogger(MetadataResourceBundleHandler.class);

    private static final String NORMAL_FORMAT = "pamirs.normal";

    private final Map<String, List<Resource>> normalResourceCache = new HashMap<>();

    @Override
    public void scanResources(PathMatchingResourcePatternResolver resolver) {
        long start = System.currentTimeMillis();
        try {
            // Pattern: classpath*:i18n/messages*.properties
            String pattern = "classpath*:" + PamirsMessageSourceConfigure.I18N_NORMAL_BASENAME + "*.properties";
            Resource[] resources = resolver.getResources(pattern);

            for (Resource resource : resources) {
                String filename = resource.getFilename();
                if (filename != null) {
                    String locale = extractLocaleFromFilename(filename);
                    normalResourceCache.computeIfAbsent(locale, k -> new ArrayList<>()).add(resource);
                }
            }
        } catch (IOException e) {
            // silent
        }
        log.info("load i18n message resources cost time: {}ms", System.currentTimeMillis() - start);
    }

    @Override
    public List<String> getFormats() {
        return Collections.singletonList(NORMAL_FORMAT);
    }

    @Override
    public ResourceBundle loadBundle(String baseName, Locale locale, String format) throws IOException {
        String localeStr = locale.toString();
        List<Resource> resources = normalResourceCache.get(localeStr);

        if (resources == null || resources.isEmpty()) {
            return null;
        }

        Map<String, Object> combinedMap = new HashMap<>();
        for (Resource resource : resources) {
            Properties properties = new Properties();
            try (InputStream is = resource.getInputStream();
                 Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                properties.load(reader);
                for (String key : properties.stringPropertyNames()) {
                    combinedMap.put(key, properties.getProperty(key));
                }
            }
        }

        return new MapResourceBundle(combinedMap);
    }

    private String extractLocaleFromFilename(String filename) {
        // Filename format: messages[_lang[_country]].properties
        String base = "messages";
        String extension = ".properties";

        if (filename.equals(base + extension)) {
            return "";
        }

        if (filename.startsWith(base + "_") && filename.endsWith(extension)) {
            return filename.substring(base.length() + 1, filename.length() - extension.length());
        }

        return "";
    }
}
