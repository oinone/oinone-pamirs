package pro.shushi.pamirs.locale.bundle;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.StringUtils;
import pro.shushi.pamirs.locale.configure.PamirsMessageSourceConfigure;
import pro.shushi.pamirs.locale.constants.MetadataConstants;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * MetadataResourceBundleHandler
 *
 * @author Adamancy Zhang at 15:35 on 2026-03-13
 */
public class MetadataResourceBundleHandler implements ResourceBundleHandler {

    private static final Logger log = LoggerFactory.getLogger(MetadataResourceBundleHandler.class);

    private static final String METADATA_FORMAT = "pamirs.metadata";

    private final Map<String, FileParser> parsers = new LinkedHashMap<>();

    private final Map<String, Map<String, List<Resource>>> resourceCache = new HashMap<>();

    private static final Map<String, String> FILE_PREFIX_MAPPING = new HashMap<>();

    private static final Map<String, String> DEFAULT_FIELD_MAPPING = new HashMap<>();

    private static final Set<String> TERMINAL_PROPERTIES = new HashSet<>(Arrays.asList(
            "displayName",
            "summary",
            "description",
            "label",
            "msg",
            "help",
            "title",
            "remark",
            "defaultValue",
            "tips",
            "error",
            "defaultFormat"
    ));

    private static final Set<String> GROUPING_KEYS;

    static {
        initFilePrefixMapping();
        initDefaultFieldMapping();

        GROUPING_KEYS = new HashSet<>(FILE_PREFIX_MAPPING.values());
        GROUPING_KEYS.add(MetadataConstants.FIELD);
        GROUPING_KEYS.add(MetadataConstants.ACTION);
    }

    private static void initFilePrefixMapping() {
        FILE_PREFIX_MAPPING.put("module", MetadataConstants.MODULE);
        FILE_PREFIX_MAPPING.put("model", MetadataConstants.MODEL);
        FILE_PREFIX_MAPPING.put("sequence", MetadataConstants.SEQUENCE);
        FILE_PREFIX_MAPPING.put("menu", MetadataConstants.MENU);

        FILE_PREFIX_MAPPING.put("mask", MetadataConstants.MASK);
        FILE_PREFIX_MAPPING.put("layout", MetadataConstants.LAYOUT);
        FILE_PREFIX_MAPPING.put("view", MetadataConstants.VIEW);
        FILE_PREFIX_MAPPING.put("view_template", MetadataConstants.VIEW_TEMPLATE);

        FILE_PREFIX_MAPPING.put("dictionary", MetadataConstants.DATA_DICTIONARY);
        FILE_PREFIX_MAPPING.put("error", MetadataConstants.ERROR_DEFINITION);

        FILE_PREFIX_MAPPING.put("interfaces", MetadataConstants.INTERFACES);
        FILE_PREFIX_MAPPING.put("function", MetadataConstants.FUNCTION);
        FILE_PREFIX_MAPPING.put("expression_definition", MetadataConstants.EXPRESSION_DEFINITION);
        FILE_PREFIX_MAPPING.put("compute_definition", MetadataConstants.COMPUTE_DEFINITION);

        FILE_PREFIX_MAPPING.put("hook", MetadataConstants.HOOK);
        FILE_PREFIX_MAPPING.put("extpoint", MetadataConstants.EXT_POINT);
        FILE_PREFIX_MAPPING.put("extpoint_impl", MetadataConstants.EXT_POINT_IMPLEMENTATION);

        FILE_PREFIX_MAPPING.put("server_action", MetadataConstants.SERVER_ACTION);
        FILE_PREFIX_MAPPING.put("view_action", MetadataConstants.VIEW_ACTION);
        FILE_PREFIX_MAPPING.put("client_action", MetadataConstants.CLIENT_ACTION);
        FILE_PREFIX_MAPPING.put("url_action", MetadataConstants.URL_ACTION);

        FILE_PREFIX_MAPPING.put("trigger", MetadataConstants.TRIGGER_TASK);
        FILE_PREFIX_MAPPING.put("schedule", MetadataConstants.SCHEDULE_TASK);
    }

    private static void initDefaultFieldMapping() {
        DEFAULT_FIELD_MAPPING.put(MetadataConstants.MODULE, "displayName");
        DEFAULT_FIELD_MAPPING.put(MetadataConstants.MODEL, "displayName");
        DEFAULT_FIELD_MAPPING.put(MetadataConstants.FIELD, "displayName");
        DEFAULT_FIELD_MAPPING.put(MetadataConstants.SEQUENCE, "displayName");
        DEFAULT_FIELD_MAPPING.put(MetadataConstants.MENU, "displayName");

        DEFAULT_FIELD_MAPPING.put(MetadataConstants.MASK, "title");
        DEFAULT_FIELD_MAPPING.put(MetadataConstants.LAYOUT, "title");
        DEFAULT_FIELD_MAPPING.put(MetadataConstants.VIEW, "title");
        DEFAULT_FIELD_MAPPING.put(MetadataConstants.VIEW_TEMPLATE + MetadataConstants.SPLIT + MetadataConstants.FIELD, "label");

        DEFAULT_FIELD_MAPPING.put(MetadataConstants.DATA_DICTIONARY, "displayName");
        DEFAULT_FIELD_MAPPING.put(MetadataConstants.ERROR_DEFINITION, "msg");

        DEFAULT_FIELD_MAPPING.put(MetadataConstants.INTERFACES, "displayName");
        DEFAULT_FIELD_MAPPING.put(MetadataConstants.FUNCTION, "displayName");
        DEFAULT_FIELD_MAPPING.put(MetadataConstants.EXPRESSION_DEFINITION, "error");
        DEFAULT_FIELD_MAPPING.put(MetadataConstants.COMPUTE_DEFINITION, "tips");

        DEFAULT_FIELD_MAPPING.put(MetadataConstants.HOOK, "displayName");
        DEFAULT_FIELD_MAPPING.put(MetadataConstants.EXT_POINT, "displayName");
        DEFAULT_FIELD_MAPPING.put(MetadataConstants.EXT_POINT_IMPLEMENTATION, "displayName");

        DEFAULT_FIELD_MAPPING.put(MetadataConstants.ACTION, "label");
        DEFAULT_FIELD_MAPPING.put(MetadataConstants.SERVER_ACTION, "displayName");
        DEFAULT_FIELD_MAPPING.put(MetadataConstants.VIEW_ACTION, "displayName");
        DEFAULT_FIELD_MAPPING.put(MetadataConstants.CLIENT_ACTION, "displayName");
        DEFAULT_FIELD_MAPPING.put(MetadataConstants.URL_ACTION, "displayName");

        DEFAULT_FIELD_MAPPING.put(MetadataConstants.TRIGGER_TASK, "displayName");
        DEFAULT_FIELD_MAPPING.put(MetadataConstants.SCHEDULE_TASK, "displayName");
    }

    public MetadataResourceBundleHandler() {
        List<FileParser> fileParsers = Lists.newArrayList(
                new JsonFileParser(),
                new YamlFileParser()
        );
        for (FileParser fileParser : fileParsers) {
            for (String fileExtension : fileParser.getFileExtensions()) {
                parsers.put(fileExtension, fileParser);
            }
        }
    }

    @Override
    public void scanResources(PathMatchingResourcePatternResolver resolver) {
        long start = System.currentTimeMillis();
        for (String format : parsers.keySet()) {
            try {
                // Scan all resources for this format under baseName
                // Pattern: classpath*:pamirs/i18n/**/*.format
                String pattern = "classpath*:" + PamirsMessageSourceConfigure.I18N_METADATA_BASENAME + "/**/*." + format;
                Resource[] resources = resolver.getResources(pattern);

                Map<String, List<Resource>> localeMap = resourceCache.computeIfAbsent(format, k -> new HashMap<>());

                for (Resource resource : resources) {
                    String path = resource.getURI().toString();
                    String locale = extractLocaleFromPath(path);
                    if (locale != null) {
                        localeMap.computeIfAbsent(locale, k -> new ArrayList<>()).add(resource);
                    }
                }
            } catch (IOException e) {
                // silent
            }
        }
        log.info("load i18n metadata resources cost time: {}ms", System.currentTimeMillis() - start);
    }

    @Override
    public List<String> getFormats() {
        return Collections.singletonList(METADATA_FORMAT);
    }

    @SuppressWarnings("unchecked")
    @Override
    public ResourceBundle loadBundle(String baseName, Locale locale, String format) throws IOException {
        String lang = locale.getLanguage();
        if (!StringUtils.hasText(lang)) {
            return null;
        }

        Map<String, Object> combinedMap = new HashMap<>();

        String country = locale.getCountry();
        String localeStr;
        if (!StringUtils.hasText(country)) {
            localeStr = lang;
        } else {
            localeStr = lang + "_" + country;
        }

        boolean foundResources = false;

        for (Map.Entry<String, FileParser> entry : parsers.entrySet()) {
            String extension = entry.getKey();
            FileParser parser = entry.getValue();

            Map<String, List<Resource>> localeMap = resourceCache.get(extension);
            if (localeMap != null) {
                List<Resource> resources = localeMap.get(localeStr);
                if (resources != null && !resources.isEmpty()) {
                    foundResources = true;
                    for (Resource resource : resources) {
                        String path = resource.getURI().toString();
                        String module = extractModuleFromPath(path);
                        if (module == null) {
                            throw new NullPointerException();
                        }

                        try (InputStream is = resource.getInputStream()) {
                            Map<String, Object> map = parser.parse(is);

                            String filename = extractFileName(path);
                            String implicitPrefix = null;
                            if (filename != null) {
                                implicitPrefix = FILE_PREFIX_MAPPING.get(filename);
                            }

                            Map<String, Object> normalizedMap = new HashMap<>();
                            if (implicitPrefix == null) {
                                normalizedMap.putAll(map);
                            } else {
                                Map<String, Object> nestedMap = (Map<String, Object>) normalizedMap.computeIfAbsent(implicitPrefix, k -> new HashMap<>());
                                Object value = map.remove(implicitPrefix);
                                if (value instanceof Map && !MetadataConstants.FUNCTION.equals(implicitPrefix)) {
                                    Map<String, Object> valueMap = (Map<String, Object>) value;
                                    Map<String, Object> selfProps = new HashMap<>();
                                    for (Map.Entry<String, Object> e : valueMap.entrySet()) {
                                        String k = e.getKey();
                                        if (TERMINAL_PROPERTIES.contains(k) || GROUPING_KEYS.contains(k)) {
                                            selfProps.put(k, e.getValue());
                                        } else {
                                            nestedMap.put(k, e.getValue());
                                        }
                                    }
                                    if (!selfProps.isEmpty()) {
                                        nestedMap.put(implicitPrefix, selfProps);
                                    }
                                } else if (value != null) {
                                    nestedMap.put(implicitPrefix, value);
                                }
                                nestedMap.putAll(map);
                            }

                            Map<String, Object> flattenedMap = new HashMap<>();
                            flattenMap(module, implicitPrefix, null, implicitPrefix, new ArrayList<>(), normalizedMap, flattenedMap);
                            combinedMap.putAll(flattenedMap);
                        } catch (IOException ignored) {
                        }
                    }
                }
            }
        }

        if (!foundResources) {
            return null;
        }

        return new MapResourceBundle(combinedMap);
    }

    private String extractModuleFromPath(String path) {
        String prefix = PamirsMessageSourceConfigure.I18N_METADATA_BASENAME + "/";
        int startIndex = path.lastIndexOf(prefix);
        if (startIndex != -1) {
            String remaining = path.substring(startIndex + prefix.length());
            int slashIndex = remaining.indexOf('/');
            if (slashIndex != -1) {
                return remaining.substring(0, slashIndex);
            }
        }
        return null;
    }

    private String extractLocaleFromPath(String path) {
        String prefix = PamirsMessageSourceConfigure.I18N_METADATA_BASENAME + "/";
        int startIndex = path.lastIndexOf(prefix);
        if (startIndex != -1) {
            String remaining = path.substring(startIndex + prefix.length());
            int slashIndex = remaining.indexOf('/');
            if (slashIndex != -1) {
                String afterModule = remaining.substring(slashIndex + 1);
                int slashIndex2 = afterModule.indexOf('/');
                if (slashIndex2 != -1) {
                    return afterModule.substring(0, slashIndex2);
                }
            }
        }
        return null;
    }

    private String extractFileName(String path) {
        int lastSlashIndex = path.lastIndexOf('/');
        int dotIndex = path.lastIndexOf('.');
        if (lastSlashIndex != -1 && dotIndex != -1 && dotIndex > lastSlashIndex) {
            return path.substring(lastSlashIndex + 1, dotIndex);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private void flattenMap(String module, String implicitPrefix, String prefix, String lastKey, List<String> groupKeys, Map<String, Object> source, Map<String, Object> target) {
        for (Map.Entry<String, Object> entry : source.entrySet()) {
            String originKey = entry.getKey();
            String key;
            if (StringUtils.hasText(prefix)) {
                key = prefix + MetadataConstants.SPLIT + entry.getKey();
            } else {
                key = entry.getKey();
            }
            Object value = entry.getValue();
            if (value instanceof Map) {
                flattenMap(module, implicitPrefix, key, originKey, getNextGroupKeys(groupKeys, originKey), (Map<String, Object>) value, target);
            } else if (value instanceof String) {
                String finalKey = getFinalKey(module, key);
                String defaultProperty = null;
                if (lastKey != null && !MetadataConstants.MODEL.equals(lastKey)) {
                    if (!MetadataConstants.MODEL.equals(implicitPrefix) && !MetadataConstants.FUNCTION.equals(implicitPrefix) && GROUPING_KEYS.contains(lastKey)) {
                        defaultProperty = getDefaultProperty(groupKeys);
                    } else if (!TERMINAL_PROPERTIES.contains(originKey) && !groupKeys.isEmpty()) {
                        defaultProperty = getDefaultProperty(groupKeys);
                    }
                }
                if (defaultProperty == null) {
                    target.put(finalKey, value);
                } else {
                    target.put(finalKey + MetadataConstants.SPLIT + defaultProperty, value);
                }
            } else {
                throw new IllegalStateException();
            }
        }
    }

    private List<String> getNextGroupKeys(List<String> groupKeys, String key) {
        if (GROUPING_KEYS.contains(key)) {
            if (groupKeys.isEmpty() || !key.equals(groupKeys.get(groupKeys.size() - 1))) {
                groupKeys = new ArrayList<>(groupKeys);
                groupKeys.add(key);
            }
        }
        return groupKeys;
    }

    private String getDefaultProperty(List<String> groupKeys) {
        String currentGroup = groupKeys.get(0);
        List<String> groups = new ArrayList<>();
        groups.add(currentGroup);
        for (int i = 1; i < groupKeys.size(); i++) {
            String nextGroup = groupKeys.get(i);
            currentGroup = currentGroup + MetadataConstants.SPLIT + nextGroup;
            groups.add(currentGroup);
        }
        for (int i = groups.size() - 1; i >= 0; i--) {
            String defaultProperty = DEFAULT_FIELD_MAPPING.get(groups.get(i));
            if (defaultProperty != null) {
                return defaultProperty;
            }
        }
        return null;
    }

    private static String getFinalKey(String module, String key) {
        String finalKey;
        if (key.startsWith(MetadataConstants.MODULE + MetadataConstants.SPLIT) ||
                key.startsWith(MetadataConstants.ERROR_DEFINITION + MetadataConstants.SPLIT) ||
                key.startsWith(MetadataConstants.VIEW + MetadataConstants.SPLIT) ||
                key.startsWith(MetadataConstants.VIEW_TEMPLATE + MetadataConstants.SPLIT) ||
                key.startsWith(MetadataConstants.LAYOUT + MetadataConstants.SPLIT) ||
                key.startsWith(MetadataConstants.MASK + MetadataConstants.SPLIT)) {
            finalKey = key;
        } else {
            finalKey = (module != null ? module + MetadataConstants.SPLIT : "") + key;
        }
        return finalKey;
    }
}
