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
            log.error("Corresponding data source not found, please check data source configuration. dsKey:{}", dsKey);
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

    /**
     * 将多主机 JDBC URI（已去除 "jdbc:" 前缀）标准化为单主机形式，仅保留第一个主机条目。
     *
     * <p>支持以下多主机格式：</p>
     * <ul>
     *   <li>{@code scheme://host1:port1,host2:port2/db?params} → {@code scheme://host1:port1/db?params}</li>
     *   <li>{@code scheme://host1,host2:port/db?params}        → {@code scheme://host1:port/db?params}</li>
     * </ul>
     *
     * <p>若第一个主机条目不含端口，则从后续条目中借用端口号。</p>
     * <p><b>注意</b>：不支持 MySQL Connector/J {@code address=(host=...)(port=...)} 高级多主机格式。</p>
     *
     * @param uriWithoutJdbc 已去除 "jdbc:" 前缀的 URI 字符串
     * @return 标准化后的单主机 URI 字符串；若不存在多主机（无逗号），则原样返回
     */
    public static String normalizeMultiHost(String uriWithoutJdbc) {
        if (StringUtils.isBlank(uriWithoutJdbc)) {
            return uriWithoutJdbc;
        }
        int schemeEnd = uriWithoutJdbc.indexOf("://");
        if (schemeEnd < 0) {
            return uriWithoutJdbc;
        }

        // 定位 authority 段：scheme:// 之后，首个 '/' 或 '?' 或字符串末尾之前
        int authorityStart = schemeEnd + 3;
        int pathStart      = uriWithoutJdbc.indexOf('/', authorityStart);
        int queryStart     = uriWithoutJdbc.indexOf('?', authorityStart);
        int authorityEnd   = uriWithoutJdbc.length();
        if (pathStart  >= 0)                              { authorityEnd = pathStart; }
        if (queryStart >= 0 && queryStart < authorityEnd) { authorityEnd = queryStart; }

        String authority  = uriWithoutJdbc.substring(authorityStart, authorityEnd);
        int firstCommaPos = authority.indexOf(',');
        // 无逗号：单主机，直接返回
        if (firstCommaPos < 0) {
            return uriWithoutJdbc;
        }

        // 取第一个主机条目；若其不含端口（无 ':'），则从后续条目借用
        String firstHost = authority.substring(0, firstCommaPos);
        if (firstHost.indexOf(':') < 0) {
            String[] remainingHosts = authority.substring(firstCommaPos + 1).split(",", -1);
            for (String other : remainingHosts) {
                int colonIdx = other.indexOf(':');
                if (colonIdx >= 0) {
                    firstHost = firstHost + other.substring(colonIdx);
                    break;
                }
            }
        }

        return uriWithoutJdbc.substring(0, authorityStart) + firstHost + uriWithoutJdbc.substring(authorityEnd);
    }

    /**
     * 将 JDBC URL 转换为 {@link URI}。
     * <p>去除 "jdbc:" 前缀后，对多主机写法（authority 段含逗号）自动标准化为单主机。</p>
     *
     * @param url 完整 JDBC URL，必须以 "jdbc:" 开头
     * @return 解析后的 {@link URI}
     */
    public static URI getUriFromUrl(String url) {
        return URI.create(normalizeMultiHost(url.substring(5)));
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