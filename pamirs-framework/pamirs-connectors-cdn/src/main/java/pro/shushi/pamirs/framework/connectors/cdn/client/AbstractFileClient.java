package pro.shushi.pamirs.framework.connectors.cdn.client;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import pro.shushi.pamirs.framework.connectors.cdn.configure.CdnConfig;
import pro.shushi.pamirs.framework.connectors.cdn.constant.FileConstants;
import pro.shushi.pamirs.framework.connectors.cdn.factory.CdnConfigRouter;
import pro.shushi.pamirs.framework.connectors.cdn.pojo.*;
import pro.shushi.pamirs.framework.connectors.cdn.spi.CdnFileNameApi;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Exp;
import pro.shushi.pamirs.meta.api.prefix.KeyPrefixManager;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.common.util.TypeReferences;
import pro.shushi.pamirs.meta.util.DateUtils;
import pro.shushi.pamirs.meta.util.JsonUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLStreamHandler;
import java.util.*;

import static pro.shushi.pamirs.meta.common.constants.CharacterConstants.SEPARATOR_SLASH;

@Slf4j
public abstract class AbstractFileClient implements FileClient, FileConstants {

    @Autowired
    private CdnConfig cdnConfig;

    @Override
    public CdnFileForm getFormData(String filename) {
        CdnUploadFileRequest request = new CdnUploadFileRequest();
        request.setFilename(filename);
        CdnFileForm result = getFormData(request);
        if (StringUtils.isBlank(result.getSingleUploadJson())) {
            CdnSingleUploadData singleUploadData = new CdnSingleUploadData();
            singleUploadData.setUploadUrl(result.getUploadUrl());
            singleUploadData.setUploadFormData(JsonUtils.parseObject(result.getFormDataJson(), TypeReferences.TR_MAP_SS));
            result.setSingleUploadJson(JsonUtils.toJSONString(singleUploadData));
        } else if (StringUtils.isBlank(result.getFormDataJson())) {
            CdnSingleUploadData singleUploadData = JsonUtils.parseObject(result.getSingleUploadJson(), CdnSingleUploadData.class);
            result.setUploadUrl(singleUploadData.getUploadUrl());
            result.setFormDataJson(JsonUtils.toJSONString(singleUploadData.getUploadFormData()));
        }
        return result;
    }

    /**
     * 根据上传文件请求参数生成文件上传参数
     *
     * @param request 上传文件请求参数
     * @return 文件上传参数
     */
    @Override
    public CdnFileForm getFormData(CdnUploadFileRequest request) {
        String filename = getUploadFilename(request);
        String fileKey = generatorFrontUploadFileKey(filename);
        CdnFileForm fileForm;
        CdnSingleUploadData singleUploadData = generatorSingleFileFormData(fileKey, request);
        if (singleUploadData == null) {
            return getFormData(request.getFilename());
        }
        fileForm = new CdnFileForm();
        fileForm.setDownloadUrl(generatorDownloadUrl(fileKey));
        fileForm.setFilename(filename);
        fileForm.setFileName(filename);
        fileForm.setSingleUploadJson(JsonUtils.toJSONString(singleUploadData));
        CdnMultipartUploadData multipartUploadData = generatorMultipartFileFormData(fileKey, request);
        if (multipartUploadData != null) {
            fileForm.setMultipartUploadJson(JsonUtils.toJSONString(multipartUploadData));
        }
        return fileForm;
    }

    /**
     * 生成单文件上传参数
     *
     * @param fileKey 文件存储Key
     * @param request 上传文件请求参数
     * @return 单文件上传参数
     */
    protected CdnSingleUploadData generatorSingleFileFormData(String fileKey, CdnUploadFileRequest request) {
        Map<String, String> formData = generatorFormData(fileKey, request);
        if (formData == null) {
            return null;
        }
        CdnSingleUploadData cdnSingleUploadData = new CdnSingleUploadData();
        cdnSingleUploadData.setUploadUrl(generatorFrontUploadUrl());
        cdnSingleUploadData.setUploadFormData(formData);
        return cdnSingleUploadData;
    }

    /**
     * 生成分片文件上传参数
     *
     * @param fileKey 文件存储Key
     * @param request 上传文件请求参数
     * @return 分片文件上传参数
     */
    protected CdnMultipartUploadData generatorMultipartFileFormData(String fileKey, CdnUploadFileRequest request) {
        return null;
    }

    /**
     * 生成前端上传文件存储Key
     *
     * @param filename 文件名
     * @return 文件存储Key
     */
    protected String generatorFrontUploadFileKey(String filename) {
        CdnConfig cdnConfig = getCdnConfig();
        return getFileKey(cdnConfig.getMainDir(), filename);
    }

    /**
     * 生成前端上传URL
     *
     * @return FormData上传URL
     */
    protected String generatorFrontUploadUrl() {
        return getBaseUploadUrl();
    }

    /**
     * 生成FormData提交参数
     *
     * @param fileKey 文件存储Key
     * @param request 上传文件请求参数
     * @return FormData提交参数
     */
    protected Map<String, String> generatorFormData(String fileKey, CdnUploadFileRequest request) {
        return null;
    }

    /**
     * 生成下载URL
     *
     * @param fileKey 文件存储Key
     * @return 下载URL
     */
    protected String generatorDownloadUrl(String fileKey) {
        CdnConfig cdnConfig = getCdnConfig();
        String baseDownloadUrl = getBaseDownloadUrl();
        String downloadUrl = baseDownloadUrl + CharacterConstants.SEPARATOR_SLASH + fileKey;
        String imageResizeParameter = cdnConfig.getImageResizeParameter();
        if (isPicture(fileKey, cdnConfig.getImageResizeExtensions()) && StringUtils.isNotBlank(imageResizeParameter)) {
            return appendPictureProcessParameters(downloadUrl, imageResizeParameter);
        }
        return downloadUrl;
    }

    /**
     * 追加图片处理参数
     *
     * @param downloadUrl          下载URL
     * @param imageResizeParameter 图片Resize处理参数
     * @return 追加后的下载链接
     */
    protected String appendPictureProcessParameters(String downloadUrl, String imageResizeParameter) {
        return downloadUrl;
    }

    /**
     * 获取上传文件名
     *
     * @param request 上传文件请求参数
     * @return 处理后的文件名
     */
    protected String getUploadFilename(CdnUploadFileRequest request) {
        String uploadFilename = request.getUploadFilename();
        if (StringUtils.isBlank(uploadFilename)) {
            String filename = request.getFilename();
            uploadFilename = Spider.getDefaultExtension(CdnFileNameApi.class).getNewFilename(filename);
            request.setUploadFilename(uploadFilename);
        }
        return uploadFilename;
    }

    protected String getContentType(CdnUploadFileRequest request) {
        return getContentType(request, MediaType.TEXT_PLAIN_VALUE);
    }

    protected String getContentType(CdnUploadFileRequest request, String defaultContentType) {
        return Optional.ofNullable(request.getContentType())
                .filter(StringUtils::isNotBlank)
                .orElse(defaultContentType);
    }

    protected List<CdnChunkFile> getChunkFiles(CdnUploadFileRequest request) {
        List<CdnChunkFile> chunkFiles = request.getChunkFiles();
        // FIXME: zbh 20240624 服务端分片暂不启用
//        if (chunkFiles == null) {
//            Long size = request.getSize();
//            if (size == null) {
//                return null;
//            }
//            long chunkSize = 10L * 1024L * 1024L;
//            chunkFiles = new ArrayList<>();
//            long chunkCount = size / chunkSize;
//            if (chunkCount <= 1) {
//                return null;
//            }
//            int i = 0;
//            for (; i < chunkCount; i++) {
//                CdnChunkFile chunkFile = new CdnChunkFile();
//                chunkFile.setPartNumber(i + 1);
//                chunkFile.setFileSize(chunkSize);
//                chunkFiles.add(chunkFile);
//            }
//            long remainder = size - chunkSize * chunkCount;
//            if (remainder >= 1) {
//                CdnChunkFile chunkFile = new CdnChunkFile();
//                chunkFile.setPartNumber(i + 1);
//                chunkFile.setFileSize(chunkSize);
//                chunkFiles.add(chunkFile);
//            }
//        }
        return chunkFiles;
    }

    protected CdnConfig getCdnConfig() {
        return Optional.ofNullable(CdnConfigRouter.get()).filter(StringUtils::isNotBlank).map(v -> cdnConfig.getOthers().get(v)).orElse(cdnConfig);
    }

    protected String getFileDir(String mainDir) {
        if (StringUtils.isBlank(mainDir)) {
            mainDir = CharacterConstants.SEPARATOR_EMPTY;
        } else {
            if (!mainDir.endsWith(SEPARATOR_SLASH)) {
                mainDir = mainDir + SEPARATOR_SLASH;
            }
        }
        String dateStr = DateUtils.formatDate(new Date(), FILE_DATE_FORMAT);
        return mainDir + dateStr;
    }

    protected String getFileKey(String mainDir, String filename) {
        String keyPrefix = KeyPrefixManager.generate(SEPARATOR_SLASH, SEPARATOR_SLASH);
        if (StringUtils.isBlank(keyPrefix)) {
            keyPrefix = SEPARATOR_SLASH;
        } else {
            // keyPrefix如果不为空，已经以/结尾了
            keyPrefix = SEPARATOR_SLASH + keyPrefix;
        }
        return getFileDir(mainDir) + keyPrefix + filename;
    }

    protected String prepareDownloadFileKey(String fileKey) {
        try {
            String baseDownloadUrl = getBaseDownloadUrl();
            if (StringUtils.isBlank(baseDownloadUrl)) {
                return fileKey;
            }
            String prefix = new URL(baseDownloadUrl).getPath();
            if (StringUtils.isBlank(prefix)) {
                return fileKey;
            }
            if (prefix.startsWith(SEPARATOR_SLASH)) {
                prefix = prefix.substring(1);
            }
            if (!prefix.endsWith(SEPARATOR_SLASH)) {
                prefix = prefix + SEPARATOR_SLASH;
            }
            if (StringUtils.isNotBlank(prefix) && fileKey.startsWith(prefix)) {
                fileKey = fileKey.substring(prefix.length());
            }
        } catch (MalformedURLException ignored) {
        }
        return fileKey;
    }

    protected String getBaseDownloadUrl() {
        CdnConfig cdnConfig = getCdnConfig();
        String format = cdnConfig.getDownloadUrlFormat();
        if (StringUtils.isBlank(format)) {
            return getDefaultBaseDownloadUrl(cdnConfig);
        }
        return generatorFormatUrl(format, cdnConfig.getDownloadUrl(), cdnConfig.getBucket());
    }

    protected String getDefaultBaseDownloadUrl(CdnConfig cdnConfig) {
        return HTTPS + cdnConfig.getBucket() + POINT + cdnConfig.getDownloadUrl();
    }

    protected String getBaseUploadUrl() {
        CdnConfig cdnConfig = getCdnConfig();
        String format = cdnConfig.getUploadUrlFormat();
        if (StringUtils.isBlank(format)) {
            return getDefaultBaseUploadUrl(cdnConfig);
        }
        return generatorFormatUrl(format, cdnConfig.getUploadUrl(), cdnConfig.getBucket());
    }

    protected String getDefaultBaseUploadUrl(CdnConfig cdnConfig) {
        return HTTPS + cdnConfig.getBucket() + POINT + cdnConfig.getUploadUrl();
    }

    protected String generatorFormatUrl(String format, String url, String bucket) {
        Map<String, Object> context = new HashMap<>(2);
        context.put("url", url);
        context.put("bucket", bucket);
        return Exp.run(format, context);
    }

    /**
     * 获取文件名称后缀
     *
     * @param fileName 指定文件名称
     * @return 文件名称后缀，如“.txt”，无后缀时返回空字符串
     */
    public static String getSuffix(String fileName) {
        String[] names = fileName.split(CharacterConstants.SEPARATOR_ESCAPE_DOT);
        if (names.length >= 2) {
            return CharacterConstants.SEPARATOR_DOT + names[names.length - 1];
        } else {
            return CharacterConstants.SEPARATOR_EMPTY;
        }
    }

    public static boolean isPicture(String fileKey, String[] imageResizeExtensions) {
        if (imageResizeExtensions == null) {
            return false;
        }
        //获取文件扩展名
        String suffix = getSuffix(fileKey).toUpperCase();
        for (String item : imageResizeExtensions) {
            if (item.trim().toUpperCase().equals(suffix)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 解析URL查询参数
     *
     * @param queryParameters 查询参数字符串
     * @return 查询参数
     */
    public static Map<String, String> parseQueryParameters(String queryParameters) {
        Map<String, String> requestParameters = new LinkedHashMap<>();
        if (StringUtils.isBlank(queryParameters)) {
            return requestParameters;
        }
        String[] pairs = queryParameters.split(FileConstants.AND);
        for (String pair : pairs) {
            String[] keyValue = pair.split(FileConstants.EQUAL);
            if (keyValue.length == 1) {
                requestParameters.put(keyValue[0], null);
            } else if (keyValue.length == 2) {
                requestParameters.put(keyValue[0], keyValue[1]);
            }
        }
        return requestParameters;
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
                builder.append(FileConstants.AND);
            }
            builder.append(entry.getKey()).append(FileConstants.EQUAL);
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
        if (query == null) {
            query = CharacterConstants.SEPARATOR_EMPTY;
        }
        if (StringUtils.isBlank(key) || StringUtils.isBlank(value)) {
            return query;
        }
        Map<String, String> parameters = parseQueryParameters(query);
        parameters.put(key, value);
        return queryParametersToString(parameters);
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
}
