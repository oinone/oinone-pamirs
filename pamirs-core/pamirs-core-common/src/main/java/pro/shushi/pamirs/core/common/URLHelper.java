package pro.shushi.pamirs.core.common;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.core.common.constant.CommonConstants;
import pro.shushi.pamirs.core.common.enmu.DefaultProtocolEnum;
import pro.shushi.pamirs.core.common.entry.NameValueEntity;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.URLStreamHandler;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;

/**
 * URL帮助类
 *
 * @author Adamancy Zhang on 2021-02-01 11:19
 */
public class URLHelper {

    private static final String SEPARATOR_SLASH = "/";

    private static final char URL_SEPARATOR_SLASH_CHAR = '/';

    private static final String PARAMETER_VALUE_SEPARATOR = "=";

    private static final String PARAMETER_VALUE_CONNECTOR = "&";

    private static final String SEPARATOR_COLON = ":";

    private static final char SEPARATOR_COLON_CHAR = ':';

    private URLHelper() {
        //reject create object
    }

    /**
     * <h>修复相对路径</h>
     * <p>
     * 1、当首字符是"/"时进行剪裁
     * 2、当末尾字符是"/"时进行剪裁
     * </p>
     *
     * @param relativePath 相对路径
     * @return 修复后的路径
     */
    public static String repairRelativePath(String relativePath) {
        if (relativePath == null) {
            return CharacterConstants.SEPARATOR_EMPTY;
        }
        relativePath = relativePath.trim();
        if (relativePath.charAt(0) == URL_SEPARATOR_SLASH_CHAR) {
            relativePath = relativePath.substring(1);
        }
        if (relativePath.charAt(relativePath.length() - 1) == URL_SEPARATOR_SLASH_CHAR) {
            relativePath = relativePath.substring(0, relativePath.length() - 1);
        }
        return relativePath;
    }

    /**
     * <h>修复绝对路径</h>
     * <p>
     * 1、当首字符不是"/"时进行追加
     * 2、当末尾字符是"/"时进行剪裁
     * </p>
     *
     * @param absolutePath 绝对路径
     * @return 修复后的路径
     */
    public static String repairAbsolutePath(String absolutePath) {
        if (absolutePath == null) {
            return SEPARATOR_SLASH;
        }
        absolutePath = absolutePath.trim();
        if (absolutePath.charAt(0) != URL_SEPARATOR_SLASH_CHAR) {
            absolutePath = SEPARATOR_SLASH + absolutePath;
        }
        if (absolutePath.charAt(absolutePath.length() - 1) == URL_SEPARATOR_SLASH_CHAR) {
            absolutePath = absolutePath.substring(0, absolutePath.length() - 1);
        }
        return absolutePath;
    }

    /**
     * <h>修复目录路径</h>
     * <p>
     * 1、当末尾字符是"/"时进行裁剪
     * </p>
     *
     * @param directoryPath 目录路径
     * @return 修复后的路径
     */
    public static String repairDirectoryPath(String directoryPath) {
        if (directoryPath == null) {
            return CharacterConstants.SEPARATOR_EMPTY;
        }
        directoryPath = directoryPath.trim();
        if (directoryPath.charAt(directoryPath.length() - 1) == URL_SEPARATOR_SLASH_CHAR) {
            directoryPath = directoryPath.substring(0, directoryPath.length() - 1);
        }
        return directoryPath;
    }

    /**
     * <h>修复请求头</h>
     *
     * @param schema 请求头或协议
     * @return 修复后的请求头，当修复失败时，返回null
     */
    private static String repairSchema(String schema) {
        if (schema == null) {
            return null;
        }
        schema = schema.trim();
        return schema;
    }

    /**
     * <h>通过请求头获取协议</h>
     *
     * @param schema 请求头或协议
     * @return 协议
     */
    private static String getProtocolBySchema(String schema) {
        if (schema == null) {
            return null;
        }
        schema = schema.trim();
        return schema;
    }

    /**
     * 解析URL查询参数
     *
     * @param query 查询参数字符串
     * @return 查询参数
     */
    public static Map<String, String> parseQueryParameters(String query) {
        Map<String, String> queryParameters = new LinkedHashMap<>();
        if (StringUtils.isBlank(query)) {
            return queryParameters;
        }
        String[] pairs = query.split(PARAMETER_VALUE_CONNECTOR);
        for (String pair : pairs) {
            String[] keyValue = pair.split(PARAMETER_VALUE_SEPARATOR);
            if (keyValue.length == 1) {
                queryParameters.put(keyValue[0], null);
            } else if (keyValue.length == 2) {
                queryParameters.put(keyValue[0], keyValue[1]);
            }
        }
        return queryParameters;
    }

    /**
     * 查询参数转字符串
     *
     * @param queryParameters 查询参数
     * @return 查询参数字符串
     */
    public static String queryParametersToString(Map<String, String> queryParameters) {
        if (MapUtils.isEmpty(queryParameters)) {
            return CharacterConstants.SEPARATOR_EMPTY;
        }
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> entry : queryParameters.entrySet()) {
            String key = entry.getKey();
            if (StringUtils.isBlank(key)) {
                continue;
            }
            if (builder.length() != 0) {
                builder.append(PARAMETER_VALUE_CONNECTOR);
            }
            builder.append(entry.getKey()).append(PARAMETER_VALUE_SEPARATOR);
            String value = entry.getValue();
            if (StringUtils.isNotBlank(value)) {
                builder.append(value);
            }
        }
        return builder.toString();
    }

    /**
     * 追加单个URL查询参数
     *
     * @param query 查询参数字符串
     * @param key   键
     * @param value 值
     * @return 追加后的查询参数字符串
     */
    public static String appendQueryParameter(String query, String key, String value) {
        query = StringHelper.valueOf(query);
        if (StringUtils.isBlank(key) || StringUtils.isBlank(value)) {
            return query;
        }
        Map<String, String> parameters = parseQueryParameters(query);
        parameters.put(key, value);
        return queryParametersToString(parameters);
    }

    /**
     * 追加多个URL查询参数
     *
     * @param query           查询参数字符串
     * @param queryParameters 多个查询参数
     * @return 追加后的查询参数字符串
     */
    public static String appendQueryParameters(String query, Map<String, String> queryParameters) {
        query = StringHelper.valueOf(query);
        if (MapUtils.isEmpty(queryParameters)) {
            return query;
        }
        Map<String, String> parameters = parseQueryParameters(query);
        parameters.putAll(queryParameters);
        return queryParametersToString(parameters);
    }

    /**
     * 追加单个URL查询参数
     *
     * @param query 查询参数字符串
     * @param key   键
     * @param value 值
     * @return 追加后的查询参数字符串
     * @deprecated please using {@link URLHelper#appendRequestParameter}
     */
    @Deprecated
    public static String appendRequestParameter(String query, String key, String value) {
        return appendQueryParameter(query, key, value);
    }

    /**
     * 追加多个URL查询参数
     *
     * @param query           查询参数字符串
     * @param queryParameters 多个查询参数
     * @return 追加后的查询参数字符串
     * @deprecated please using {@link URLHelper#appendQueryParameters}
     */
    @Deprecated
    public static String appendRequestParameters(String query, Map<String, String> queryParameters) {
        return appendQueryParameters(query, queryParameters);
    }

    /**
     * URL转字符串
     *
     * @param url   URL
     * @param query 查询参数
     * @return URL字符串
     * @see URLStreamHandler#toExternalForm(java.net.URL)
     */
    public static String urlToString(URL url, String query) {
        int len = url.getProtocol().length() + 1;
        if (url.getAuthority() != null && url.getAuthority().length() > 0) {
            len += 2 + url.getAuthority().length();
        }
        if (url.getPath() != null) {
            len += url.getPath().length();
        }
        if (StringUtils.isBlank(query)) {
            query = url.getQuery();
        }
        if (StringUtils.isNotBlank(query)) {
            len += 1 + query.length();
        }
        if (url.getRef() != null) {
            len += 1 + url.getRef().length();
        }
        StringBuilder result = new StringBuilder(len);
        result.append(url.getProtocol());
        result.append(":");
        if (url.getAuthority() != null && url.getAuthority().length() > 0) {
            result.append("//");
            result.append(url.getAuthority());
        }
        if (url.getPath() != null) {
            result.append(url.getPath());
        }
        if (StringUtils.isNotBlank(query)) {
            result.append("?");
            result.append(query);
        }
        if (url.getRef() != null) {
            result.append("#");
            result.append(url.getRef());
        }
        return result.toString();
    }

    /**
     * 验证端口号是否有效
     *
     * @param port 端口号
     * @return 是否有效
     */
    public static boolean verificationPort(int port) {
        return port >= 1 && port <= 65535;
    }

    /**
     * <h>获取连接主机地址</h>
     * <p>
     * 1、当根据schema判断出指定端口为协议默认端口时，不追加端口号
     * 2、其他情况使用":"拼接主机地址和端口号
     * </p>
     *
     * @param schema 请求头或协议
     * @param host   主机地址
     * @param port   端口号
     * @return 连接主机地址
     */
    public static String getConnectHost(String schema, String host, int port) {
        if (DefaultProtocolEnum.isDefaultProtocol(schema, port)) {
            return host;
        }
        return host + CharacterConstants.SEPARATOR_COLON + port;
    }

    /**
     * 获取请求参数字符串
     *
     * @param parameters 参数键值对
     * @return 请求参数字符串
     */
    public static String getRequestParameterString(Map<String, ?> parameters) {
        return getRequestParameterString(parameters, null);
    }

    /**
     * 获取请求参数字符串，并对键值对实例对象进行自定义处理
     *
     * @param parameters 参数键值对
     * @param consumer   键值对实例对象的自定义处理
     * @return 请求参数字符串
     */
    public static String getRequestParameterString(Map<String, ?> parameters, Consumer<NameValueEntity> consumer) {
        StringBuilder builder = new StringBuilder();
        List<NameValueEntity> list = getRequestParameterEntity(parameters);
        for (NameValueEntity item : list) {
            if (consumer != null) {
                consumer.accept(item);
            }
            if (builder.length() != 0) {
                builder.append(PARAMETER_VALUE_CONNECTOR);
            }
            builder.append(item.getKey()).append(PARAMETER_VALUE_SEPARATOR).append(item.getValue());
        }
        return builder.toString();
    }

    /**
     * 通过参数键值对获取键值对实例对象列表
     *
     * @param parameters 参数键值对
     * @return 键值对实例对象列表
     */
    public static List<NameValueEntity> getRequestParameterEntity(Map<String, ?> parameters) {
        List<NameValueEntity> list = new ArrayList<>();
        for (Map.Entry<String, ?> entry : parameters.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (StringUtils.isBlank(key) || ObjectHelper.isBlank(value)) {
                continue;
            }
            if (value instanceof Collection) {
                Collection<?> collValues = (Collection<?>) value;
                for (Object collValue : collValues) {
                    if (ObjectHelper.isBlank(collValue)) {
                        continue;
                    }
                    list.add(new NameValueEntity(key, StringHelper.valueOf(collValue)));
                }
            } else {
                String stringValue = StringHelper.valueOf(value);
                list.add(new NameValueEntity(key, stringValue));
            }
        }
        return list;
    }

    /**
     * url encode
     *
     * @param url url
     * @return encoded url
     */
    public static String encode(String url) {
        if (StringUtils.isBlank(url)) {
            return url;
        }
        try {
            return URLEncoder.encode(url, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException ignored) {
        }
        return null;
    }

    /**
     * url decode
     *
     * @param url encoded url
     * @return url
     */
    public static String decode(String url) {
        if (StringUtils.isBlank(url)) {
            return url;
        }
        try {
            return URLDecoder.decode(url, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException ignored) {
        }
        return null;
    }

    /**
     * url encode. 只对文件名(中文部分)进行编码
     *
     * @param url url
     * @return encoded url
     */
    public static String encodeFileName(String url) {
        if (StringUtils.isBlank(url) || url.startsWith(CommonConstants.CLASSPATH_PROTOCOL)) {
            return url;
        }
        try {
            String fileName = url.substring(url.lastIndexOf(CharacterConstants.SEPARATOR_SLASH) + 1);
            String res = url.substring(0, url.lastIndexOf(CharacterConstants.SEPARATOR_SLASH) + 1) + URLEncoder.encode(fileName, StandardCharsets.UTF_8.name());
            // 其中空格被编码成  +   ；这样编码后空格编码还是有问题，需在处理
            // 因为 + 符号在java是关键字符需要转义，不能直接用
            // %20 为空格的编码，这里替换掉，URL才不会报错
            return res.replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException ignored) {
        }
        return null;
    }

    public static void main(String[] args) {
        String target = "https://yxit-jjxt-tjb.obs.cn-east-2.myhuaweicloud.com/bonus/2022/05/16/奖金调拨 (1)_+1652696148649.xlsx";
        System.out.println(encodeFileName(target));
        target = "奖金调拨 (1)_+1652696148649.xlsx";
        System.out.println(encodeFileName(target));
    }

}
