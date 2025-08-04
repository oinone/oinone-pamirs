package pro.shushi.pamirs.framework.connectors.data.datasource.ddl;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.data.configure.datasource.DataSourceConfiguration;
import pro.shushi.pamirs.framework.connectors.data.configure.mapper.PamirsMapperConfiguration;
import pro.shushi.pamirs.framework.connectors.data.dialect.Dialects;
import pro.shushi.pamirs.framework.connectors.data.dialect.api.DsDialectComponent;
import pro.shushi.pamirs.framework.connectors.data.entity.DataSourceInfo;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.exception.PamirsException;

import jakarta.annotation.Resource;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

import static pro.shushi.pamirs.framework.connectors.data.enmu.DataExpEnumerate.BASE_DS_CONFIG_ERROR;

@Slf4j
@Component
public class DdlManager {

    @Resource
    private DataSourceConfiguration dataSourceConfiguration;

    @Resource
    private PamirsMapperConfiguration pamirsMapperConfiguration;

    public void createDatabase(String dsKey) {
        Objects.requireNonNull(Dialects.component(DsDialectComponent.class, dsKey)).createDatabase(dsKey);
    }

    public boolean existTable(String dsKey, String tableName) {
        return Objects.requireNonNull(Dialects.component(DsDialectComponent.class, dsKey)).existTable(dsKey, tableName);
    }

    public Map<String, String> getDsConfig(String dsKey) {
        if (null == dataSourceConfiguration) {
            return null;
        }
        Map<String, String> dataSource = dataSourceConfiguration.get(dsKey);
        if (MapUtils.isEmpty(dataSource)) {
            return null;
        }
        return dataSource;
    }

    public String getUrl(String dsKey) {
        Map<String, String> dataSource = getDsConfig(dsKey);
        if (MapUtils.isEmpty(dataSource)) {
            log.error("找不到对应的数据源，请检查数据源配置. dsKey:{}", dsKey);
            throw PamirsException.construct(BASE_DS_CONFIG_ERROR).appendMsg("数据源：" + dsKey).errThrow();
        }
        String url = dataSource.get("url");
        if (StringUtils.isBlank(url)) {
            url = dataSource.get("xa-properties.url");
        }
        return url;
    }

    public URI getUri(String dsKey) {
        return DdlManager.getUriFromUrl(getUrl(dsKey));
    }

    public static URI getUriFromUrl(String url) {
        String cleanURI = url.substring(5);
        return URI.create(cleanURI);
    }

    public DataSourceInfo getDataSourceInfo(String dsKey) {
        String url = getUrl(dsKey);
        URI uri = getUriFromUrl(url);
        return new DataSourceInfo()
                .setUrl(url)
                .setDatabase(getDatabase(uri))
                .setProtocol(getProtocolFromUrl(url))
                .setHost(uri.getHost())
                .setPort(uri.getPort())
                .setParameters(getQueryParameters(uri.getQuery()));
    }

    public String getDatabase(String dsKey) {
        return DdlManager.getDatabase(getUri(dsKey));
    }

    public static String getDatabase(URI uri) {
        String path = uri.getPath();
        return RegExUtils.replacePattern(path, "[^(a-zA-Z0-9_\\u4e00-\\u9fa5)]", CharacterConstants.SEPARATOR_EMPTY);
    }

    public String getProtocol(String dsKey) {
        return DdlManager.getProtocolFromUrl(getUrl(dsKey));
    }

    public static String getProtocolFromUrl(String url) {
        return url.split("://")[0];
    }

    public static Map<String, List<String>> getQueryParameters(String query) {
        return getQueryParameters(query, "&");
    }

    public static Map<String, List<String>> getQueryParameters(String query, String parametersSplit) {
        if (StringUtils.isBlank(query)) {
            return new HashMap<>(0);
        }
        return Arrays.stream(query.split(parametersSplit)).map(v -> v.split("="))
                .collect(Collectors.toMap(v -> v[0], v -> {
                    List<String> arrays = new ArrayList<>();
                    arrays.add(v[1]);
                    return arrays;
                }, (a, b) -> {
                    a.addAll(b);
                    return a;
                }));
    }

    public static String getSingleQueryParameter(Map<String, List<String>> queryParameters, String key) {
        return Optional.ofNullable(queryParameters.get(key)).filter(v -> !v.isEmpty()).map(v -> v.get(0)).orElse(null);
    }

    public static String generatorQuery(Map<String, List<String>> queryParameters) {
        return generatorQuery(queryParameters, "&");
    }

    public static String generatorQuery(Map<String, List<String>> queryParameters, String parametersSplit) {
        final String parameterValueSplit = "=";
        StringBuilder builder = new StringBuilder();
        boolean isAppendSplit = false;
        for (Map.Entry<String, List<String>> entry : queryParameters.entrySet()) {
            String key = entry.getKey();
            List<String> values = entry.getValue();
            if (CollectionUtils.isEmpty(values)) {
                if (isAppendSplit) {
                    builder.append(parametersSplit);
                }
                builder.append(key).append(parameterValueSplit);
                isAppendSplit = true;
            } else {
                for (String value : values) {
                    if (isAppendSplit) {
                        builder.append(parametersSplit);
                    }
                    builder.append(key).append(parameterValueSplit).append(value);
                    isAppendSplit = true;
                }
            }
        }
        return builder.toString();
    }
}